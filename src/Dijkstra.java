import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Dijkstra {
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    boolean directed;
    int nvertices;
    int nedges;
    int numComp;

    public Dijkstra() {
    }

    private PriorityQueue<Vertex> initPQ() {
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        Vertex current = vertices.get(0);

        // Initialize source vertex
        pq.add(current);
        current.setCurrPath(0, current.key);

        // Initialize remaining vertices
        for (int i = 1; i < this.nvertices; i++) {
            current = vertices.get(i);
            pq.add(current);
            current.setCurrPath(-1, -1);
        }

        return pq;
    }

    private PriorityQueue<Vertex> updateDistances(Vertex currVert, PriorityQueue<Vertex> pq) {
        int newDist;
        Vertex nextVert;
        Edge edge;

        // Ensure that distances of all adjacent vertices are as short as currently known possible
        for (int i = 0; i < currVert.edges.size(); i++) {
            edge = currVert.edges.get(i);
            nextVert = edge.u;
            newDist = currVert.getDistance() + edge.weight;

            if (nextVert.getDistance() < 0 || newDist < nextVert.getDistance()) {
                nextVert.setCurrPath(newDist, currVert.key);    // Update shortest path to source
            }
        }

        return pq;
    }

    private int[][] fillPathInfo() {
        int[][] spt = new int[this.nvertices][3];
        Vertex currVert;

        for (int i = 0; i < this.nvertices; i++) {
            currVert = this.vertices.get(i);
            spt[i][0] = currVert.key;
            spt[i][1] = currVert.getDistance();
            spt[i][2] = currVert.getPrev();
        }

        return spt;
    }

    // Used in testing, does not provide functionality
    private void printPQ(PriorityQueue<Vertex> pq) {
        System.out.print("{");

        for (Vertex v : pq) {
            System.out.print(v.key + "(" + v.getDistance() + ", " + v.getPrev() + "), ");
        }

        System.out.println("}");
    }

    public static int[][] findShortPaths(String filename) throws FileNotFoundException {
        int[][] spt;
        Dijkstra dijkstra = new Dijkstra();
        PriorityQueue<Vertex> pq;
        Vertex current;

        dijkstra.readfile_graph(filename);

        pq = dijkstra.initPQ();

        // Main loop. Selects next closest vertex to tree, adds to tree, and updates distances for adjacent vertices
        while (pq.size() > 0) {
            current = pq.poll();
            pq = dijkstra.updateDistances(current, pq);
        }

        spt = dijkstra.fillPathInfo();

        return spt;
    }

    void readfile_graph(String filename) throws FileNotFoundException {
        int x,y, weight;
        //read the input
        FileInputStream in = new FileInputStream(new File(filename));
        Scanner sc = new Scanner(in);
        int temp = sc.nextInt(); // if 1 directed; 0 undirected
        directed = (temp == 1);
        nvertices = sc.nextInt();
        for (int i=0; i<=nvertices-1; i++){
            Vertex tempv = new Vertex(i+1);   // kludge to store proper key starting at 1
            vertices.add(tempv);
        }

        nedges = sc.nextInt();   // m is the number of edges in the file
        int nedgesFile = nedges;
        for (int i=1; i<=nedgesFile ;i++)	{
            // System.out.println(i + " compare " + (i<=nedges) + " nedges " + nedges);
            x=sc.nextInt();
            y=sc.nextInt();
            weight = sc.nextInt();
            //  System.out.println("x  " + x + "  y:  " + y  + " i " + i);
            insert_edge(x,y, weight, directed);
        }
        // order edges to make it easier to see what is going on
        for(int i=0;i<=nvertices-1;i++)	{
            Collections.sort(vertices.get(i).edges);
        }
    }

    static void process_vertex_early(Vertex v)	{
        timer++;
        v.entry_time = timer;
        //     System.out.printf("entered vertex %d at time %d\n",v.key, v.entry_time);
    }

    static void process_vertex_late(Vertex v)	{
        timer++;
        v.exit_time = timer;
        //     System.out.printf("exit vertex %d at time %d\n",v.key , v.exit_time);
    }

    static void process_edge(Vertex x,Vertex y) 	{
        int c = edge_classification(x,y);
        if (c == BACK) System.out.printf("back edge (%d,%d)\n",x.key,y.key);
        else if (c == TREE) System.out.printf("tree edge (%d,%d)\n",x.key,y.key);
        else if (c == FORWARD) System.out.printf("forward edge (%d,%d)\n",x.key,y.key);
        else if (c == CROSS) System.out.printf("cross edge (%d,%d)\n",x.key,y.key);
        else System.out.printf("edge (%d,%d)\n not in valid class=%d",x.key,y.key,c);
    }

    static void initialize_search(Dijkstra g)	{
        for(Vertex v : g.vertices)		{
            v.processed = v.discovered = false;
            v.parent = null;
        }
    }

    static final int TREE = 1, BACK = 2, FORWARD = 3, CROSS = 4;
    static int timer = 0;

    static int edge_classification(Vertex x, Vertex y)	{
        if (y.parent == x) return(TREE);
        if (y.discovered && !y.processed) return(BACK);
        if (y.processed && (y.entry_time > x.entry_time)) return(FORWARD);
        if (y.processed && (y.entry_time < x.entry_time)) return(CROSS);
        System.out.printf("Warning: self loop (%d,%d)\n",x,y);
        return -1;
    }

    void insert_edge(int x, int y, int weight, boolean directed) 	{
        Vertex one = vertices.get(x-1);
        Vertex two = vertices.get(y-1);
        Edge edge = new Edge(one, two, weight);
        one.edges.add(edge);

        vertices.get(x-1).degree++;
        if(!directed)
            insert_edge(y,x, weight, true);
        else
            nedges++;
    }
    void remove_edge(Vertex x, Vertex y)  {
        if(x.degree<0)
            System.out.println("Warning: no edge --" + x + ", " + y);
        x.edges.remove(y);
        x.degree--;
    }

    void print_graph()	{
        for(Vertex v : vertices)	{
            System.out.println("vertex: "  + v.key);
//            for (int i = 0; i < v.degree; i++)
//                System.out.print("  adjacency list: " + v.edges.get(i).key + " (" + v.edgeWeights.get(i) + ")");
            for(Edge e : v.edges)
                System.out.print("  adjacency list: " + e.u.key + " (" + e.weight + ")");
            System.out.println();
        }
    }

    class Vertex implements Comparable<Vertex> {
        int key;
        int degree;
        int component;
        int color = -1;    // use mod numColors, -1 means not colored
        boolean discovered = false;
        boolean processed = false;
        int entry_time = 0;
        int exit_time = 0;
        Vertex parent = null;
        ArrayList<Edge> edges = new ArrayList<Edge>();
        int[] currPath = new int[2];    // (distance to subtree, vertex back to current known SPT)

        public Vertex(int tkey){
            key = tkey;
        }
        public int compareTo(Vertex other){
            if (this.key > other.key){
                return 1;
            }         else if (this.key < other.key) {
                return -1;
            }
            else
                return 0;
        }

        private void setCurrPath(int distance, int prevVert) {
            this.currPath[0] = distance;
            this.currPath[1] = prevVert;
        }

        private int getDistance() {
            return this.currPath[0];
        }

        private int getPrev() {
            return this.currPath[1];
        }
    }

    class Edge implements Comparable<Edge> {
        Vertex v, u;
        int weight;

        public Edge(Vertex v, Vertex u, int weight) {
            this.v = v;
            this.u = u;
            this.weight = weight;
        }

        public int compareTo(Edge other) {
            if (this.weight > other.weight) {
                return 1;
            } else if (this.weight < other.weight) {
                return -1;
            } else
                return 0;
        }
    }

    Vertex unProcessedV(){
        for(Vertex v:  vertices)  {
            if (! v.processed ) return v;
        }
        return null;
    }
}
