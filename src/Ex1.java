import java.util.LinkedList;

public class Ex1 {
    public static void main(String[] args)
    { //"src/Assignment/Another example/input2.txt");
        Object[] arr = TextFileParse.textParse("input.txt"); //text file parse
        LinkedList<NetworkNode> nodes = (LinkedList<NetworkNode>) arr[0]; //assignment of nodes from return of text parser
        LinkedList<Query> queries = (LinkedList<Query>) arr[1]; //assignment of queries from return of text parser
        String XMLFilepath = (String)arr[2]; //Saving the XML filepath for use of variable elimination
        String[] toWrite = new String[queries.size()];
        for (int i = 0; i < queries.size(); i++) //iterating over queries
        {
            String str = queries.get(i).resultForQuery(nodes, XMLFilepath);
            toWrite[i] = str;
        }
        Utilities.toFile(toWrite, "output.txt"); //writing to file
    }
}

