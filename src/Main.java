import java.util.LinkedList;

public class Main {
    public static void main(String[] args)
    { //"src/Assignment/Another example/input2.txt");
        Object[] arr = TextFileParse.textParse("src/Assignment/input.txt");
        LinkedList<NetworkNode> nodes = (LinkedList<NetworkNode>) arr[0];
        LinkedList<Query> queries = (LinkedList<Query>) arr[1];
        for (int i = 0; i < queries.size(); i++)
        {
            String str = queries.get(i).resultForQuery(nodes);
            System.out.println();
        }
    }
}
