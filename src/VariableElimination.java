import java.util.LinkedList;

public class VariableElimination implements Query {

    private final NetworkNode queryNode;
    private final String queryValue;
    private final NetworkNode[] givenNodes;
    private final String[] givenValues;
    private final NetworkNode[] readingOrder;

    public VariableElimination(NetworkNode queryNode, String queryValue, NetworkNode[] givenNodes, String[] givenValues,
                               NetworkNode[] readingOrder) {
        this.queryNode = queryNode;
        this.queryValue = queryValue;
        this.givenNodes = givenNodes;
        this.givenValues = givenValues;
        this.readingOrder = readingOrder;
    }

    @Override
    public String resultForQuery(LinkedList<NetworkNode> nodes, String XMLFilepath) {
        nodes = XmlFileParse.xmlParser(XMLFilepath);
        nodes = siphoningNotDependantNodes(nodes);
        nodes = removeAllIrrelevantValues(nodes);
        int multiplyCount = 0;
        int additionCount = 0;

        for (int i = 0; i < this.readingOrder.length; i++) {
            Object[] arr = this.joinAll(nodes, readingOrder[i]);
            multiplyCount += (int) arr[0];
            nodes = (LinkedList<NetworkNode>) arr[1];
            //join and parse of returned values up to here

            Object[] arr2 = eliminate(nodes.getLast().getTableKeys(), nodes.getLast().getTableValues(),
                    readingOrder[i].getName());
            String[][] currKeys = (String[][]) arr2[0];
            double[] currValues = (double[]) arr2[1];
            additionCount += (int) arr2[2];
            nodes.getLast().setTableKeys(currKeys);
            nodes.getLast().setTableValues(currValues);
        }
        return null;
    }


    /**
     * Function to implement elimination of a variable ("by") from a factor given. This is done by "summing out" the
     * different outcomes of this variable into a single record
     *
     * @param factorKeys   keys of the factor
     * @param factorValues values of the factor
     * @param by           the variable to sub out (as String)
     * @return The shrunken factor as Object[] to transfer both keys and values to the parent function, as well as the
     * number of addition operations performed
     */
    public static Object[] eliminate(String[][] factorKeys, double[] factorValues, String by) {
        int byIndex = Utilities.indexOf(factorKeys[0], by);
        LinkedList<String[]> newKeys = new LinkedList<>();
        LinkedList<Double> newValues = new LinkedList<>();
        int additionCount = 0;
        newKeys.addLast(Utilities.removeElement(factorKeys[0], byIndex)); //adding column names to the new factor Keys
        for (int i = 1; i < factorKeys.length; i++) {
            double currValue = factorValues[i - 1];
            String[] currRecordKeys = Utilities.removeElement(factorKeys[i], byIndex);
            if (!Utilities.contains(newKeys, currRecordKeys)) {
                for (int j = i + 1; j < factorKeys.length; j++) {
                    String[] tempKeys = Utilities.removeElement(factorKeys[j], byIndex);
                    if (Utilities.equals(tempKeys, currRecordKeys)) {
                        currValue += factorValues[j - 1];
                        additionCount++;
                    }
                }
                newKeys.addLast(currRecordKeys);
                newValues.addLast(currValue);
            }
        }
        Object[] arr = new Object[3];
        arr[0] = Utilities.linkedListTo2DArray(newKeys);
        arr[1] = Utilities.linkedListToDoubleArray(newValues);
        arr[2] = additionCount;
        return arr;
    }


    /**
     * Function to remove all data which contradicts to what was given in the query. Traverses all given nodes and all
     * nodes for each of them, constructing the "improved" factor by removing unnecessary values. If a factor becomes
     * one-valued, removes it. Returns the corrected list of nodes
     *
     * @param nodes list of nodes
     * @return corrected list of nodes
     */
    public LinkedList<NetworkNode> removeAllIrrelevantValues(LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < this.givenNodes.length; i++) { // for each given node, traverse to find irrelevant values
            for (int j = 0; j < nodes.size(); j++) { //iterating over the nodes to find irrelevant values
                // (specifically of one given node)
                String[][] currKeys = nodes.get(j).getTableKeys(); //current keys
                double[] currValues = nodes.get(j).getTableValues(); //current values
                if (Utilities.contains(currKeys[0], this.givenNodes[i].getName())) { //if such column name exists
                    // (doest necessarily have to)
                    LinkedList<String[]> newKeysLst = new LinkedList<>();
                    LinkedList<Double> newValuesLst = new LinkedList<>();
                    int indexOfNodeInKeys = Utilities.indexOf(currKeys[0], this.givenNodes[i].getName());
                    newKeysLst.addLast(currKeys[0]); //adding first row (row of names of nodes) in any case
                    for (int k = 1; k < currKeys.length; k++) {
                        if (currKeys[k][indexOfNodeInKeys].equals(this.givenValues[i])) {
                            newKeysLst.addLast(currKeys[k]);
                            newValuesLst.addLast(currValues[k - 1]);
                        }
                    }
                    if (newValuesLst.size() == 1) { //removing 1 valued factors
                        nodes.remove(j);
                    } else {
                        nodes.get(j).setTableKeys(Utilities.linkedListTo2DArray(newKeysLst));
                        nodes.get(j).setTableValues(Utilities.linkedListToDoubleArray(newValuesLst));
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * A function to join all factors mentioning a node (specified by "by"). Returns the joined factor keys and values,
     * the number of multiplication operations performed, as well as the list of nodes due to some nodes (all which
     * were used in the joining) were removed
     *
     * @param nodes LinkedList of nodes
     * @param by    A node by which to join
     * @return Object[] containing all relevant info (to be parsed in parent function)
     */
    public Object[] joinAll(LinkedList<NetworkNode> nodes, NetworkNode by) {
        LinkedList<NetworkNode> nodeFactorsToJoin = findAllFactorsMentioning(nodes, by);
        String[][] currentFactorKeys = nodeFactorsToJoin.get(0).getTableKeys();
        double[] currFactorValues = nodeFactorsToJoin.get(0).getTableValues();
        int multiplyCount = 0;
        for (int i = 1; i < nodeFactorsToJoin.size(); i++) {
            NetworkNode node = nodeFactorsToJoin.get(i);
            Object[] temp = joinTwo(currentFactorKeys, currFactorValues, node.getTableKeys(), node.getTableValues());
            currentFactorKeys = (String[][]) temp[0];
            currFactorValues = (double[]) temp[1];
            multiplyCount += (int) temp[2];
        }
        LinkedList<NetworkNode> nodesAfterRemoval = new LinkedList<>();
        for (int i = 0; i < nodes.size(); i++) { //removing all nodes which were used to create the new factor.
            // All their information exists in the returned factor
            if (!nodeFactorsToJoin.contains(nodes.get(i))) {
                nodesAfterRemoval.addLast(nodes.get(i));
            }
        }
        NetworkNode temp = new NetworkNode(currentFactorKeys, currFactorValues);
        nodesAfterRemoval.addLast(temp);
        Object[] arr = new Object[2];
        arr[0] = multiplyCount;
        arr[1] = nodesAfterRemoval;
        return arr;
    }

    /**
     * Function to join 2 specific factors into one and return it (keys and values, as well as how many multiplication
     * operations were done
     *
     * @param firstFactorKeys    keys of the first factor
     * @param firstFactorValues  values of the first factor
     * @param secondFactorKeys   keys of the second factor
     * @param secondFactorValues values of the second factor
     * @return an Object array containing the factor's keys and values and the number of multiplication operations
     * (in that order)
     */
    public static Object[] joinTwo(String[][] firstFactorKeys, double[] firstFactorValues, String[][] secondFactorKeys,
                                   double[] secondFactorValues) {
        LinkedList<String[]> newKeys = new LinkedList<>(); //keys to be created and returned
        LinkedList<Double> newValues = new LinkedList<>(); //values to be created and returned
        int multiplicationCount = 0; //to be calculated and returned
        String[] correlatingNames = correlatingNames(firstFactorKeys[0], secondFactorKeys[0]);
        String[] onlyInFirst = notCorrelating(firstFactorKeys[0], correlatingNames);
        String[] onlyInSecond = notCorrelating(secondFactorKeys[0], correlatingNames);
        String[] header = concatStringArrays(correlatingNames, onlyInFirst, onlyInSecond); // header of the final table key 2D array
        newKeys.add(header);

        for (int i = 1; i < firstFactorKeys.length; i++) { //Iterating over rows of keys of the first factor
            String[] firstCurrCorrelatingValues = currValuesOfCorrelatingOrNot(firstFactorKeys, correlatingNames, i);
            String[] onlyFirstValues = currValuesOfCorrelatingOrNot(firstFactorKeys, onlyInFirst, i);

            for (int j = 1; j < secondFactorKeys.length; j++) {
                String[] secondCurrCorrelatingValues = currValuesOfCorrelatingOrNot(secondFactorKeys, correlatingNames, j);
                String[] onlySecondValues = currValuesOfCorrelatingOrNot(secondFactorKeys, onlyInSecond, j);
                if (Utilities.equals(firstCurrCorrelatingValues, secondCurrCorrelatingValues)) {
                    String[] record = concatStringArrays(firstCurrCorrelatingValues, onlyFirstValues, onlySecondValues);
                    newKeys.addLast(record);
                    newValues.addLast(firstFactorValues[i - 1] * secondFactorValues[j - 1]);
                    multiplicationCount++;
                }
            }
        }

        Object[] ret = new Object[3];
        ret[0] = Utilities.linkedListTo2DArray(newKeys);
        ret[1] = Utilities.linkedListToDoubleArray(newValues);
        ret[2] = multiplicationCount;
        return ret;
    }


    /**
     * Returns the values of certain columns (for future comparison) as an array of Strings
     *
     * @param keys     total 2D array of keys
     * @param names    array of names of correlating nodes
     * @param rowIndex index of row we are interested in, in the keys 2D array
     * @return String[]
     */
    public static String[] currValuesOfCorrelatingOrNot(String[][] keys, String[] names, int rowIndex) {
        int[] indexOfCorrelating = new int[names.length];
        for (int i = 0; i < names.length; i++) { //finding the right indexes to look for
            indexOfCorrelating[i] = Utilities.indexOf(keys[0], names[i]);
        }

        String[] ret = new String[indexOfCorrelating.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = keys[rowIndex][indexOfCorrelating[i]];
        }

        return ret;
    }

    /**
     * Concatenating 3 String arrays into 1 longer one
     *
     * @param common
     * @param inFirst
     * @param inSecond
     * @return String[]
     */
    public static String[] concatStringArrays(String[] common, String[] inFirst, String[] inSecond) {
        String[] ret = new String[common.length + inFirst.length + inSecond.length];
        int ind = 0;
        for (int i = 0; i < common.length; i++) {
            ret[ind++] = common[i];
        }
        for (int i = 0; i < inFirst.length; i++) {
            ret[ind++] = inFirst[i];
        }
        for (int i = 0; i < inSecond.length; i++) {
            ret[ind++] = inSecond[i];
        }
        return ret;
    }

    /**
     * Function to determine which of the node names given in the string arrays are the same,
     * meaning which of the node values have to be considered at the join
     *
     * @param firstFactor  array of Strings with names of nodes
     * @param secondFactor array of Strings with names of nodes
     * @return String[] containing the correlating names
     */
    public static String[] correlatingNames(String[] firstFactor, String[] secondFactor) {
        LinkedList<String> lstRet = new LinkedList<>();
        for (int i = 0; i < firstFactor.length; i++) {
            for (int j = 0; j < secondFactor.length; j++) {
                if (firstFactor[i].equals(secondFactor[j])) {
                    lstRet.addLast(firstFactor[i]);
                    break;
                }
            }
        }
        return Utilities.linkedListToArray(lstRet);
    }

    /**
     * Returns an array of Strings which are unique to this factor.
     *
     * @param factorNames      array of Strings
     * @param correlatingNames array of non-unique strings in this array
     * @return String[]
     */
    public static String[] notCorrelating(String[] factorNames, String[] correlatingNames) {
        String[] ret = new String[factorNames.length - correlatingNames.length];
        int ind = 0;
        for (int i = 0; i < factorNames.length; i++) {
            if (!Utilities.contains(correlatingNames, factorNames[i])) {
                ret[ind] = factorNames[i];
                ind++;
            }
        }
        return ret;
    }

    /**
     * function to remove nodes conditionally independent of the query node given all evidence nodes (done using the
     * Bayes Ball algorithm)
     *
     * @param nodes: LinkedList of Network Nodes
     * @return siphoned Linked List of nodes
     */
    public LinkedList<NetworkNode> siphoningNotDependantNodes(LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            BayesBallQuery temp = new BayesBallQuery(queryNode, nodes.get(i), givenNodes, givenValues);
            boolean flagBayesBall = temp.resultForQuery(nodes, null).equals("yes");
            boolean flagNodeGiven = !Utilities.contains(givenNodes, nodes.get(i));
            boolean flagNotQuery = !queryNode.equals(nodes.get(i));
            if (flagNodeGiven && flagNotQuery && flagBayesBall) {
                nodes.remove(i);
            }
        }
        return nodes;
    }

    /**
     * Function to return a list of nodes in which the "what" node is mentioned (received as input)
     *
     * @param nodes: LinkedList of all nodes
     * @param what:  node to search
     * @return list of nodes containing the current query
     */
    public static LinkedList<NetworkNode> findAllFactorsMentioning(LinkedList<NetworkNode> nodes, NetworkNode what) {
        LinkedList<NetworkNode> ret = new LinkedList<>();
        for (int i = 0; i < nodes.size(); i++) {
            String[] names = nodes.get(i).getTableKeys()[0];
            for (int j = 0; j < names.length; j++) {
                if (names[j].equals(what.getName())) {
                    ret.addLast(nodes.get(i));
                    break;
                }
            }
        }
        nodes = sortFactors(nodes);
        return nodes;
    }

    /**
     * function to sort a list of factors according to the size of their CPT tables.
     * Wherever the CPT tables are of identical size, sort them by their ASCII values (in ascending order)
     * Returning the sorted list of Network Nodes
     *
     * @param nodes
     * @return LinkedList<NetworkNode>
     */
    public static LinkedList<NetworkNode> sortFactors(LinkedList<NetworkNode> nodes) {
        //sorting by size
        for (int i = 0; i < nodes.size(); i++) {
            int minNodeInd = i;
            int minSize = nodes.get(i).getTableKeys().length;
            for (int j = 1; j < nodes.size() - 1; j++) {
                if (nodes.get(j).getTableKeys().length < minSize) {
                    minNodeInd = j;
                    minSize = nodes.get(j).getTableKeys().length;
                }
            }
            nodes = swap(nodes, i, minNodeInd);
        }

        //sorting by ASCII values if some sizes are identical (where needed)
        int sameSizeNum = 1;
        int beginningOfSameSizeInd = 0;
        for (int i = 1; i < nodes.size(); i++) {
            if (nodes.get(i).getTableKeys().length == nodes.get(i - 1).getTableKeys().length) {
                sameSizeNum++;
            } else {
                if (sameSizeNum > 1) {
                    nodes = sortByASCII(nodes, beginningOfSameSizeInd, i - 1);
                }
                sameSizeNum = 1;
                beginningOfSameSizeInd = i + 1;
            }
        }
        if (sameSizeNum > 1) {
            nodes = sortByASCII(nodes, beginningOfSameSizeInd, nodes.size() - 1);
            sameSizeNum = 0;
        }

        return nodes;
    }

    /**
     * Sorting nodes in range of [startInd, endInd] according to the ASCII sum of first row of the keys table.
     *
     * @param nodes    list of nodes
     * @param startInd starting index (included)
     * @param endInd   end index (included)
     * @return updated list of nodes
     */
    public static LinkedList<NetworkNode> sortByASCII(LinkedList<NetworkNode> nodes, int startInd, int endInd) {
        for (int i = startInd; i <= endInd; i++) {
            int minNodeInd = i;
            int minSize = Utilities.asciiSize(nodes.get(i).getTableKeys()[0]);
            for (int j = 1; j < nodes.size() - 1; j++) {
                if (nodes.get(j).getTableKeys().length < minSize) {
                    minNodeInd = j;
                    minSize = Utilities.asciiSize(nodes.get(j).getTableKeys()[0]);
                }
            }
            nodes = swap(nodes, i, minNodeInd);
        }
        return nodes;
    }

    /**
     * Function to swap two nodes in the LinkedList with given indexes
     *
     * @param nodes list of nodes
     * @param i     first index
     * @param j     second index
     * @return the list with swapped nodes
     */
    public static LinkedList<NetworkNode> swap(LinkedList<NetworkNode> nodes, int i, int j) {
        NetworkNode temp = nodes.get(i);
        nodes.set(i, nodes.get(j));
        nodes.set(j, temp);
        return nodes;
    }
}












