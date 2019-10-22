import java.io.FileNotFoundException;

public class DijkstraTest {
    public static void main(String[] args) throws FileNotFoundException {
        String filename = "src//test1.txt";

        Dijkstra.findShortPaths(filename);
    }
}
