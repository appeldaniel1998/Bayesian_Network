import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileRead {
    public static void main(String[] args) {
        LinkedList<NetworkNode> ls = xmlParser(fileReaderToLinkedList("src/Assignment/alarm_net.xml"));
        System.out.println();
    }

    private static LinkedList<NetworkNode> xmlParser(LinkedList<String> ls) {
        LinkedList<NetworkNode> ret = new LinkedList<NetworkNode>();
        int i = 0;
        String name = ""; // name of the node
        LinkedList<String> outcomes = new LinkedList<String>(); // possible outcomes of the node (True or false, for example)
        String queryName = ""; //stores the name of the node in <DEFINITION>
        LinkedList<String> parents = new LinkedList<String>(); //parents of the node (deducted from the <GIVEN> tags
        String[] values = new String[0]; // stores the values of the table (1D, as given in the <TABLE> tag)
        String line = ls.get(i);

        //finding sections describing VARIABLES
        for (i = 0; i < ls.size(); i++) {
            if (line.equals("<VARIABLE>")) {
                i++; //read the line of the opening <VARIABLE> tag
                line = ls.get(i);
                while (!line.equals("</VARIABLE>")) {
                    if (line.substring(0, 7).equals("\t<NAME>")) {
                        name = line.substring(7, line.length() - 7);
                    }

                    if (line.substring(0, 10).equals("\t<OUTCOME>")) {
                        outcomes.addLast(line.substring(10, line.length() - 10));
                    }

                    i++;
                    line = ls.get(i);
                }
                ret.addLast(new NetworkNode(name, Transformations.linkedListToArray(outcomes)));
            }
            i += 2;
            line = ls.get(i);

            if (line.equals("<DEFINITION>")) {
                i++; //read the line of the opening <VARIABLE> tag
                line = ls.get(i);
                while (!line.equals("</DEFINITION>")) {
                    if (line.substring(0, 6).equals("\t<FOR>")) {
                        queryName = line.substring(6, line.length() - 6);
                    }

                    String lineSubStr = "";
                    if (line.substring(0, 8).equals("\t<GIVEN>")) {
                        lineSubStr = line.substring(8, line.length() - 8);
                        parents.addLast(lineSubStr);
                    }

                    if (line.substring(0, 8).equals("\t<TABLE>")) {
                        lineSubStr = line.substring(8, line.length() - 8);
                        values = lineSubStr.split(" ");
                    }

                    i++;
                    line = ls.get(i);
                }
                NetworkNode temp = searchNode(ret, queryName);
                temp.setTable(Transformations.stringArrtoDoubleArr(values));
                temp.setParents(Transformations.strToNetNode(parents, ret));
            }

        }

        return ret;
    }

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

    private static NetworkNode searchNode (LinkedList<NetworkNode> ls, String query) {
        for (int i = 0; i < ls.size(); i++) {
            if (query.equals(ls.get(i).getName())) {
                return ls.get(i);
            }
        }
        return  null;
    }
}
