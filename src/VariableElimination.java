import java.util.LinkedList;

public class VariableElimination implements Query {

    private NetworkNode queryNode;
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
        double answerExists = answerAlreadyInFactors(nodes);
        if (answerExists != -1.0) {
            return answerExists + ",0,0";
        }
        nodes = this.siphoningNotDependantNodes(nodes);
        nodes = this.siphoningNotAncestorNodes(nodes);
        nodes = this.removeAllIrrelevantValues(nodes);
        int multiplyCount = 0;
        int additionCount = 0;


        for (int i = 0; i < this.readingOrder.length; i++) { //iterating over all hidden values
            if (!Utilities.contains(nodes, readingOrder[i].getName())) {
                continue;
            }
            Object[] arr = joinAll(nodes, readingOrder[i]);
            multiplyCount += (int) arr[0];
            nodes = (LinkedList<NetworkNode>) arr[1];
            //join and parse of returned values up to here

            Object[] arr2 = eliminate(nodes.getLast().getTableKeys(), nodes.getLast().getTableValues(),
                    readingOrder[i].getName()); //eliminating the current node from reading order from the last factor,
            // as it was the one added during the join function
            String[][] currKeys = (String[][]) arr2[0];
            double[] currValues = (double[]) arr2[1];
            additionCount += (int) arr2[2];
            if (currValues.length != 1) {
                nodes.getLast().setTableKeys(currKeys);
                nodes.getLast().setTableValues(currValues);
            } else {
                nodes.getLast().setTableValues(null);
                nodes.getLast().setTableKeys(null);
                // nodes.removeLast();
            }
            //Elimination handling up to here: parsing and assignment of the corrected
        }
        /*
        now the nodes contain no hidden factors. We need to join the rest of the factors. The factors that may still
        remain shall only contain the query and evidence values (where the evidence shall be one-valued), hence we
        must join the factors by the query to result in a single factor (to normalize and return the result).
        */


        //Joining the remaining nodes
        Object[] arr = joinAll(nodes, this.queryNode);
        multiplyCount += (int) arr[0];
        nodes = (LinkedList<NetworkNode>) arr[1];


        Object[] arrFinal = normalize(nodes, this.queryNode, this.queryValue);
        double result = (double) arrFinal[0];
        result *= Math.pow(10, 5);
        result = Math.round(result);
        result /= Math.pow(10, 5);
        additionCount += (int) arrFinal[1];
        String ret = (result + "," + additionCount + "," + multiplyCount);
        return ret;
    }

    /**
     * Searching for table with all the given and query nodes and values, without any others to return to the user
     * without further calculations and the VE algorithm
     *
     * @param lst list of Network nodes in addition to "this"
     * @return the value requested if exists, otherwise -1 (probability cannot be -1)
     */
    public double answerAlreadyInFactors(LinkedList<NetworkNode> lst) {
        String[] givenNodeNames = new String[this.givenNodes.length];
        for (int i = 0; i < givenNodeNames.length; i++) { //creating a String array of names of given nodes
            givenNodeNames[i] = this.givenNodes[i].getName();
        }

        for (int i = 0; i < lst.size(); i++) {
            boolean flag = true;
            String[] currFactorKeysNames = lst.get(i).getTableKeys()[0];
            double[] currFactorValues = lst.get(i).getTableValues();

            for (int j = 0; j < currFactorKeysNames.length; j++) {
                if (!(Utilities.contains(givenNodeNames, currFactorKeysNames[j]) ||
                        currFactorKeysNames[j].equals(this.queryNode.getName()))) {
                    flag = false;
                    break;
                }
            }
            for (int j = 0; j < givenNodeNames.length; j++) {
                if (!(Utilities.contains(currFactorKeysNames, givenNodeNames[j]))) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                return this.getValuesFromTable(lst.get(i).getTableKeys(), currFactorValues, givenNodeNames);
            } else {
                return -1.0;
            }
        }
        return -1.0;
    }

    /**
     * Function to return the value of where all given and query nodes match to the desired in a specific table of
     * keys + values
     *
     * @param keys       keys of said table
     * @param values     values of said table
     * @param givenNames names of given nodes
     * @return double value of probability or -1 if not found (shouldn't occur)
     */
    public double getValuesFromTable(String[][] keys, double[] values, String[] givenNames) {
        for (int i = 1; i < keys.length; i++) {
            boolean flag = true;
            if (this.givenNodes.length != 0) {
                for (int j = 0; j < keys[i].length; j++) {
                    int indexOfNodeInNames = Utilities.indexOf(givenNames, keys[0][j]);
                    if (!this.givenValues[indexOfNodeInNames].equals(keys[i][j])) {
                        flag = false;
                        break;
                    }
                }
            } else {
                if (keys[0].length != 1) {
                    flag = false;
                    break;
                }
                if (keys[i][0].equals(this.queryValue)) {
                    return values[i - 1];
                }
            }
            if (flag) {
                return values[i - 1];
            }
        }
        return -1.0;
    }


    public static Object[] normalize(LinkedList<NetworkNode> nodes, NetworkNode queryNode, String queryValue) {
        //the list received consists of only one node (necessarily)
        int additionCount = 0;
        int index = Utilities.firstNonNullTable(nodes);
        String[][] keys = nodes.get(index).getTableKeys();
        double[] values = nodes.get(index).getTableValues();
        int indOfQuery = Utilities.indexOf(keys[0], queryNode.getName());
        double sum = 0;
        int wantedInd = 0;

        for (int i = 0; i < values.length; i++) {
            sum += values[i];
            additionCount++;
        }
        additionCount--;
        for (int i = 0; i < values.length; i++) //normalizing
        {
            values[i] = values[i] / sum;
        }
        for (int i = 1; i < keys.length; i++) {
            if (keys[i][indOfQuery].equals(queryValue)) {
                wantedInd = i;
                break;
            }
        }
        Object[] arr = new Object[2];
        arr[0] = values[wantedInd - 1];
        arr[1] = additionCount;
        return arr;
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
                try {
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
                            nodes.get(j).setTableKeys(null);
                            nodes.get(j).setTableValues(null);
                            // nodes.remove(j);
                        } else {
                            nodes.get(j).setTableKeys(Utilities.linkedListTo2DArray(newKeysLst));
                            nodes.get(j).setTableValues(Utilities.linkedListToDoubleArray(newValuesLst));
                        }
                    }
                } catch (NullPointerException e) {
                    ;
                }
            }
        }
        return nodes;
    }

    /**
     * A function to join all factors mentioning a node (specified by "by"). Returns the joined factor keys and values,
     * the number of multiplication operations performed, as well as the list of nodes due to some nodes (all which
     * were used in the joining) being removed
     *
     * @param nodes LinkedList of nodes
     * @param by    A node by which to join
     * @return Object[] containing all relevant info (to be parsed in parent function)
     */
    public static Object[] joinAll(LinkedList<NetworkNode> nodes, NetworkNode by) {
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
        for (int i = 0; i < nodes.size(); i++) { //removing all nodes which were used to create the new factor.
            // All their information exists in the returned factor
            if (nodeFactorsToJoin.contains(nodes.get(i))) {
                nodes.get(i).setTableValues(null);
                nodes.get(i).setTableKeys(null);
            }
        }
        NetworkNode temp = new NetworkNode(currentFactorKeys, currFactorValues);
        nodes.addLast(temp);
        Object[] arr = new Object[2];
        arr[0] = multiplyCount;
        arr[1] = nodes;
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
     * function to return all ancestors of given and query nodes (whatever node is not included shall be
     * excluded from further considerations)
     *
     * @param nodes
     * @return
     */
    public LinkedList<NetworkNode> siphoningNotAncestorNodes(LinkedList<NetworkNode> nodes) {
        LinkedList<NetworkNode> areAncestors = new LinkedList<>();
        this.queryNode = nodes.get(Utilities.indexOf(nodes, this.queryNode));
        areAncestors = ancestorTraverse(nodes, this.queryNode, areAncestors);
        for (int i = 0; i < this.givenNodes.length; i++) {
            areAncestors = ancestorTraverse(nodes, this.givenNodes[i], areAncestors);
        }
        for (int i = 0; i < nodes.size(); i++) {
            if (!Utilities.contains(areAncestors, nodes.get(i))) {
                nodes.get(i).setTableKeys(null);
                nodes.get(i).setTableValues(null);
            }
        }
        return nodes;

    }

    /**
     * Recursive function to insert all ancestors of a node into an existing list (given as input) and return it
     *
     * @param nodes        total LinkedList of nodes
     * @param currNode     current node
     * @param areAncestors list of ancestors already existing
     * @return
     */
    public static LinkedList<NetworkNode> ancestorTraverse(LinkedList<NetworkNode> nodes, NetworkNode currNode,
                                                           LinkedList<NetworkNode> areAncestors) {
        if (!Utilities.contains(areAncestors, currNode)) {
            areAncestors.addLast(currNode);
        }
        if (currNode.getParents().length == 0) {
            return areAncestors;
        }
        for (int i = 0; i < currNode.getParents().length; i++) {
            areAncestors = ancestorTraverse(nodes, currNode.getParents()[i], areAncestors);
        }
        return areAncestors;
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
            queryNode.emptyTimesVisited();
            Utilities.zeroToAllTimesVisited(givenNodes);
            BayesBallQuery temp = new BayesBallQuery(queryNode, nodes.get(i), givenNodes, givenValues);
            boolean flagBayesBall = temp.resultForQuery(nodes, null).equals("yes");
            boolean flagNodeGiven = !Utilities.contains(givenNodes, nodes.get(i));
            boolean flagNotQuery = !queryNode.equals(nodes.get(i));
            if (flagNodeGiven && flagNotQuery && flagBayesBall) {
                nodes.get(i).setTableKeys(null);
                nodes.get(i).setTableValues(null);
                // nodes.remove(i);
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
            try {
                String[] names = nodes.get(i).getTableKeys()[0];
                for (int j = 0; j < names.length; j++) {
                    if (names[j].equals(what.getName())) {
                        ret.addLast(nodes.get(i));
                        break;
                    }
                }
            } catch (NullPointerException e) {
                ;
            }
        }
        ret = sortFactors(ret);
        return ret;
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
            for (int j = i + 1; j < nodes.size(); j++) {
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
                beginningOfSameSizeInd = i;
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
            for (int j = i + 1; j <= endInd; j++) {
                int currSize = Utilities.asciiSize(nodes.get(j).getTableKeys()[0]);
                if (currSize < minSize) {
                    minNodeInd = j;
                    minSize = currSize;
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
        NetworkNode[] arr = Utilities.linkedListToArrayNodes(nodes);
        NetworkNode temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        return Utilities.arrToLinkedListNodes(arr);
    }
}