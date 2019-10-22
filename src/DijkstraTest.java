import java.io.FileNotFoundException;
import java.util.Arrays;

public class DijkstraTest {
    public static void main(String[] args) throws FileNotFoundException {
        String filename = "src//test3.txt";

        int [][] spt = Dijkstra.findShortPaths(filename);

        System.out.println(Arrays.deepToString(spt));
    }
}
