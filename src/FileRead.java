import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FileRead {
    public static void main(String[] args) {
        xmlParser(fileReaderToLinkedList("src/Assignment/alarm_net.xml"));
    }

    private static void xmlParser(LinkedList<String> ls) {
        int i = 0;
        String name = "";
        LinkedList<String> outcomes = new LinkedList<String>();
        String line = ls.get(i);

        //finding sections describing VARIABLES
        if (line.equals("<VARIABLE>")) {
            i++; //read the line of the opening <VARIABLE> tag
            while (!line.equals("</VARIABLE>")) {
                line = ls.get(i);

                if (line.substring(0, 7).equals("\t<NAME>")) {
                    name = line.substring(7, line.length() - 7);
                }

                if (line.substring(0, 10).equals("\t<OUTCOME>")) {
                    outcomes.addLast(line.substring(10, line.length() - 10));
                }

                i++;
            }
            NetworkNode n1 = new NetworkNode(name, LinkedListToArray(outcomes)); //HOW TO STORE NODES?????
        }


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

    private static String[] LinkedListToArray (LinkedList<String> ls) {
        String[] str = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            str[i] = ls.get(i);
        }
        return str;
    }
}
