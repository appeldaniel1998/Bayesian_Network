import java.util.LinkedList;

public class TextFileParse {

    public static Object[] textParse(String filePath) {
        LinkedList<String> ls = Utilities.fileReaderToLinkedList(filePath);
        LinkedList<NetworkNode> nodes = XmlFileParse.xmlParser("src/Assignment/" + ls.get(0));
        initChildrenOfAllNodes(nodes);
        LinkedList<Query> finalQueries = new LinkedList<Query>();


        for (int i = 1; i < ls.size(); i++) {
            String line = ls.get(i);
            String sub = line.substring(0, 2);

            if (sub.equals("P(")) {
                finalQueries.addLast(varEliminationParse(line, nodes));
            } else {
                Query q = BayesParse(line, nodes);
                finalQueries.addLast(BayesParse(line, nodes));
            }
        }
        Object[] ret = new Object[2];
        ret[0] = nodes;
        ret[1] = finalQueries;
        return ret;
    }

    private static void initChildrenOfAllNodes (LinkedList<NetworkNode> nodes) {
        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).scanForChildren(nodes);
        }
    }

    private static VariableElimination varEliminationParse(String line, LinkedList<NetworkNode> nodes) {
        int indexOpenBracket = 1; //for the sake of clarity in later uses. Unchanged throughout the code
        int indexCloseBracket = line.indexOf(')');
        String givenOutcomesAndQuery = line.substring(indexOpenBracket + 1, indexCloseBracket);
        String[] splitByDefinition = givenOutcomesAndQuery.split("\\|"); //should return an array with size 2
        String[] queryNodeAndValue = splitByDefinition[0].split("=");

        NetworkNode queryNode = Utilities.searchNode(nodes, queryNodeAndValue[0]);

        Object[] arr = givenListParse(nodes, splitByDefinition[1]);
        NetworkNode[] givenNodesArr = (NetworkNode[]) arr[0];
        String[] givenValuesArr = (String[]) arr[1];


        //Reading order Parse:
        String orderOfReadingString = line.substring(indexCloseBracket + 2);
        String readingOrderStrings[] = orderOfReadingString.split("-");
        NetworkNode[] readingOrder = new NetworkNode[readingOrderStrings.length];
        for (int j = 0; j < readingOrder.length; j++) {
            readingOrder[j] = Utilities.searchNode(nodes, readingOrderStrings[j]);
        }

        return new VariableElimination(queryNode, queryNodeAndValue[1], givenNodesArr, givenValuesArr, readingOrder);
    }

    private static BayesBallQuery BayesParse(String line, LinkedList<NetworkNode> nodes) {
        String[] splitByDefinition = line.split("\\|");

        String[] queryNodesSplit = splitByDefinition[0].split("-");

        NetworkNode queryNode1 = Utilities.searchNode(nodes, queryNodesSplit[0]);
        NetworkNode queryNode2 = Utilities.searchNode(nodes, queryNodesSplit[1]);
        Object[] arr = new Object[0];
        try {
            arr = givenListParse(nodes, splitByDefinition[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            arr = givenListParse(nodes, "");
        }
        NetworkNode[] givenNodesArr = (NetworkNode[]) arr[0];
        String[] givenValuesArr = (String[]) arr[1];

        return new BayesBallQuery(queryNode1, queryNode2, givenNodesArr, givenValuesArr);
    }

    private static Object[] givenListParse(LinkedList<NetworkNode> nodes, String line) {
        LinkedList<NetworkNode> givenNodes = new LinkedList<NetworkNode>();
        LinkedList<String> givenValues = new LinkedList<String>();

        NetworkNode[] givenNodesArr = new NetworkNode[0];
        String[] givenValuesArr = new String[0];

        //splitting given pairs (node + variable), each of them splitting by "=" and adding each to the appropriate list
        //for future use and initialization of object
        if (line.length() != 0) {
            if (line.contains(",")) { //multiple pairs of nodes and values
                String[] givenPairs = line.split(",");
                for (int j = 0; j < givenPairs.length; j++) {
                    String[] temp = givenPairs[j].split("="); //array of size 2

                    NetworkNode node = Utilities.searchNode(nodes, temp[0]);
                    givenNodes.addLast(node);

                    givenValues.addLast(temp[1]);
                }
                givenNodesArr = Utilities.linkedListToArrayNodes(givenNodes);
                givenValuesArr = Utilities.linkedListToArray(givenValues);
            } else //only one pair of node and value
            {
                String[] temp = line.split("=");
                NetworkNode node = Utilities.searchNode(nodes, temp[0]);
                givenNodesArr = new NetworkNode[1];
                givenNodesArr[0] = node;
                givenValuesArr = new String[1];
                givenValuesArr[0] = temp[1];
            }
        }
        Object[] ret = new Object[2];
        ret[0] = givenNodesArr;
        ret[1] = givenValuesArr;

        return ret;
    }
}