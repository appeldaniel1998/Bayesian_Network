import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileRead {
    public static void main(String[] args) {
        LinkedList<NetworkNode> ls = xmlParser(fileReaderToLinkedList("src/Assignment/alarm_net.xml"));
        System.out.println();
    }


    private static LinkedList<NetworkNode> VariableHandling(LinkedList<String> allLines, int firstIndex, int lastIndex) {
        //first line passed is the line with the first <VARIABLE> tag

        String name = "";
        LinkedList<String> outcomes = new LinkedList<String>();

        LinkedList<NetworkNode> nodes = new LinkedList<NetworkNode>();

        for (int i = firstIndex + 1; i < lastIndex-1; i++) {
            while (!allLines.get(i).equals("</VARIABLE>")) {
                String line = allLines.get(i);
                int tempLength = line.length();
                if (tempLength != 0) {
                    if (line.substring(0, 7).equals("\t<NAME>")) {
                        name = line.substring(7, tempLength - 7);
                    }
                    if (line.substring(0, 10).equals("\t<OUTCOME>")) {
                        outcomes.add(line.substring(10, tempLength - 10));
                    }

                }
                i++;
            }

            String[] temp = new String[0];
            temp = Utilities.linkedListToArray(outcomes);
            nodes.addLast(new NetworkNode(name, temp));
            outcomes = new LinkedList<String>();
            System.out.println(i);
        }
        return nodes;
    }







    private static LinkedList<NetworkNode> DefinitionHandling(LinkedList<String> allLines, int firstIndex,
                                                              LinkedList<NetworkNode> nodes) {

        String queryStr = "";
        LinkedList<NetworkNode> givenStr = new LinkedList<NetworkNode>();

        for (int i = firstIndex; i < allLines.size(); i++)
        {
            if (allLines.get(i).equals("<DEFINITION>"))
            {
                i++;
                while(!allLines.get(i).equals("</DEFINITION>"))
                {
                    String line = allLines.get(i);
                    if (line.substring(0, 6).equals("\t<FOR>"))
                    {
                        queryStr = line.substring(6, line.length() - 6);
                        NetworkNode queryNode = Utilities.searchNode(nodes, queryStr);
                    }
                    if (line.substring(0, 8).equals("\t<GIVEN>"))
                    {
                        queryStr = line.substring(8, line.length() - 8);
                        NetworkNode temp = Utilities.searchNode(nodes, queryStr);
                        givenStr.addLast(temp);
                    }









                    i++;
                }
            }
        }



















        return nodes;
    }









    private static LinkedList<NetworkNode> xmlParser(LinkedList<String> ls) {
        LinkedList<NetworkNode> nodes = new LinkedList<NetworkNode>();
        int i;
        int startIndexVars = 0;
        for (i = 0; i < ls.size(); i++) {
            if (ls.get(i).equals("<VARIABLE>"))
            {
                startIndexVars = i;
                break;
            }
        }
        for (; i < ls.size(); i++) {
            if (ls.get(i).equals("<DEFINITION>"))
            {
                nodes = VariableHandling(ls, startIndexVars, i);
                nodes = DefinitionHandling(ls, i, nodes);
                break;
            }
        }
        return nodes;
    }











//    private static LinkedList<NetworkNode> xmlParser(LinkedList<String> ls) {
//        LinkedList<NetworkNode> ret = new LinkedList<NetworkNode>();
//        int i = 0;
//        String name = ""; // name of the node
//        LinkedList<String> outcomes = new LinkedList<String>(); // possible outcomes of the node (True or false, for example)
//        String queryName = ""; //stores the name of the node in <DEFINITION>
//        LinkedList<String> parents = new LinkedList<String>(); //parents of the node (deducted from the <GIVEN> tags
//        String[] values = new String[0]; // stores the values of the table (1D, as given in the <TABLE> tag)
//        String line = ls.get(i);
//
//        //finding sections describing VARIABLES
//        for (i = 0; i < ls.size(); i++) {
//            if (line.equals("<VARIABLE>")) {
//                i++; //read the line of the opening <VARIABLE> tag
//                line = ls.get(i);
//                while (!line.equals("</VARIABLE>")) {
//                    if (line.substring(0, 7).equals("\t<NAME>")) {
//                        name = line.substring(7, line.length() - 7);
//                    }
//
//                    if (line.substring(0, 10).equals("\t<OUTCOME>")) {
//                        outcomes.addLast(line.substring(10, line.length() - 10));
//                    }
//
//                    i++;
//                    line = ls.get(i);
//                }
//                ret.addLast(new NetworkNode(name, Transformations.linkedListToArray(outcomes)));
//            }
//            i += 2;
//            line = ls.get(i);
//
//            if (line.equals("<DEFINITION>")) {
//                i++; //read the line of the opening <VARIABLE> tag
//                line = ls.get(i);
//                while (!line.equals("</DEFINITION>")) {
//                    if (line.substring(0, 6).equals("\t<FOR>")) {
//                        queryName = line.substring(6, line.length() - 6);
//                    }
//
//                    String lineSubStr = "";
//                    if (line.substring(0, 8).equals("\t<GIVEN>")) {
//                        lineSubStr = line.substring(8, line.length() - 8);
//                        parents.addLast(lineSubStr);
//                    }
//
//                    if (line.substring(0, 8).equals("\t<TABLE>")) {
//                        lineSubStr = line.substring(8, line.length() - 8);
//                        values = lineSubStr.split(" ");
//                    }
//
//                    i++;
//                    line = ls.get(i);
//                }
//                NetworkNode temp = searchNode(ret, queryName);
//                temp.setTable(Transformations.stringArrToDoubleArr(values));
//                temp.setParents(Transformations.strToNetNode(parents, ret));
//            }
//
//        }
//
//        return ret;
//    }

    private static LinkedList<String> fileReaderToLinkedList(String str) {
        LinkedList<String> ls = new LinkedList<String>();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(str));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNext()) {
            ls.add(sc.nextLine());
        }
        return ls;
    }


}
