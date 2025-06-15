// Name: Theodore Kevin Himawan
// NPM: 2306210973
// References: Stackoverflow, Geeksforgeeks, freeCodeCamp, ChatGPT

import java.io.*;
import java.util.*;

public class TP3 {
    private static InputReader in;
    private static PrintWriter out;
    // Graph related variables
    static int V, E;
    static ArrayList<Edge>[] adjList;
    static HashSet<String> PNumbersSet;
    static ArrayList<String> PNumbersList;
    static int currentCity = 1;
    static String currentPassword = "0000";
    static int[][] componentId; // [V+1][K]
    static int[][] componentSize; // [K][V+1]
    static int[] thresholds; // [K]
    static int maxEdgeWeight = 0;
    static int Q; // number of queries
    // For M queries optimization. This method will use Breadth-First Search
    static int[] distPassword;
    static boolean[] visitedPassword;
    static int[] queuePassword;
    static int[] pNumbersInt;
    static int P;
    static final int PASSWORD_SPACE = 10000;

    // Cache for F queries: city -> dist array
    // Since we have at most V=5000, storing all dist arrays might be large but we'll do it only on-demand.
    // Use a HashMap (or maybe a simple array since city ≤ V):
    static HashMap<Integer,int[]> distCache = new HashMap<>();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Process the initial inputs
        processInitialInputs();

        // Convert all PNumbers to int for M queries
        convertPNumbersToInt();

        // Prepare arrays for M BFS
        distPassword = new int[PASSWORD_SPACE];
        visitedPassword = new boolean[PASSWORD_SPACE];
        queuePassword = new int[PASSWORD_SPACE]; 

        // Handle All Queries
        for(int i=0; i<Q; i++){
            String query = in.next();
            switch (query) {
                case "R":
                    int ENERGI = in.nextInteger();
                    int resultR = R(ENERGI);
                    out.println(resultR);
                    break;
                case "F":
                    int TUJUAN = in.nextInteger();
                    int resultF = getShortestPath(currentCity, TUJUAN);
                    out.println(resultF);
                    break;
                case "M":
                    int ID = in.nextInteger();
                    String PASSWORD = in.next();
                    currentCity = ID;
                    int resultM = M(PASSWORD);
                    out.println(resultM);
                    break;
                case "J":
                    int IDj = in.nextInteger();
                    int resultJ = J(IDj);
                    out.println(resultJ);
                    break;
                default:
                    // Handle wrong input
                    break;
            }
        }

        out.flush();
    }

    // Process the initial inputs for vertex and edges
    static void processInitialInputs() {
        V = in.nextInteger();
        E = in.nextInteger();

        // Initialize adjacency list
        adjList = new ArrayList[V+1];
        for(int i = 1; i<=V ;i++){
            adjList[i] = new ArrayList<>();
        }

        ArrayList<Edge> edges = new ArrayList<>(E);

        // Read edges and build the graph
        for(int i=0;i<E;i++){
            int Vi = in.nextInteger();
            int Vj = in.nextInteger();
            int Li = in.nextInteger();
            adjList[Vi].add(new Edge(Vj, Li));
            adjList[Vj].add(new Edge(Vi, Li));
            edges.add(new Edge(Vi, Vj, Li));
            if(Li > maxEdgeWeight) maxEdgeWeight = Li;
        }

        // Precompute for Query R
        precomputeComponents(edges);

        int P = in.nextInteger(); // Number of password patterns
        PNumbersSet = new HashSet<>(P);
        PNumbersList = new ArrayList<>(P);
        for(int i=0;i<P;i++){
            String Pi = in.next();
            // Only add unique passwords
            if(!PNumbersSet.contains(Pi)){
                PNumbersSet.add(Pi);
                PNumbersList.add(Pi);
            }
        }

        Q = in.nextInteger(); 
    }


    // Convert all password numbers from strings to integer form for efficiency in M queries.
    static void convertPNumbersToInt() {
        pNumbersInt = new int[PNumbersList.size()];
        for (int i=0; i<PNumbersList.size(); i++) {
            pNumbersInt[i] = passwordToInt(PNumbersList.get(i));
        }
        P = PNumbersList.size();
    }

    // Dijkstra with caching for F queries
    static int getShortestPath(int start, int end) {
        // Check if we have computed distances from 'start' before
        if(!distCache.containsKey(start)) {
            // Run Dijkstra from 'start'
            int[] dist = new int[V+1];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[start] = 0;

            PriorityQueue<MinHeapNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.distance));
            pq.offer(new MinHeapNode(start, 0));

            while(!pq.isEmpty()) {
                MinHeapNode node = pq.poll();
                int u = node.vertex;
                int d = node.distance;
                if(d > dist[u]) {
                    continue;
                }

                for(Edge e : adjList[u]) {
                    int nd = d + e.weight;
                    if(dist[e.to] > nd) {
                        dist[e.to] = nd;
                        pq.offer(new MinHeapNode(e.to, nd));
                    }
                }
            }

            distCache.put(start, dist);
        }

        int[] distArr = distCache.get(start);

        if(distArr[end] == Integer.MAX_VALUE) {
            return -1;
        }else{
            return distArr[end];
        }
    }

    // Precompute components for R queries
    static void precomputeComponents(ArrayList<Edge> edges){
        // Collect unique edge weights
        TreeSet<Integer> edgeWeightsSet = new TreeSet<>();
        for(Edge e : edges){
            edgeWeightsSet.add(e.weight);
        }
        thresholds = new int[edgeWeightsSet.size()];
        int idx = 0;
        for(int w : edgeWeightsSet){
            thresholds[idx++] = w;
        }
        int K = thresholds.length;

        componentId = new int[V+1][K];
        componentSize = new int[K][V+1]; 

        // Sort edges by weight
        Collections.sort(edges, Comparator.comparingInt(e -> e.weight));

        int[] parent = new int[V+1];
        for(int i=1;i<=V;i++){
            parent[i] = i;
        }

        int edgeIdx = 0;
        for(int idxThreshold = 0; idxThreshold < K; idxThreshold++){
            int w = thresholds[idxThreshold];
            // Process all edges with weight == w
            while(edgeIdx < edges.size() && edges.get(edgeIdx).weight == w){
                Edge e = edges.get(edgeIdx);
                int uRoot = find(parent, e.from);
                int vRoot = find(parent, e.to);
                if(uRoot != vRoot){
                    parent[uRoot] = vRoot;
                }
                edgeIdx++;
            }
            // Now, for each node, record its component ID
            HashMap<Integer, Integer> sizeMap = new HashMap<>();
            for(int i=1;i<=V;i++){
                componentId[i][idxThreshold] = find(parent, i);
            }
            // Count size of each component
            for(int i=1;i<=V;i++){
                int compId = componentId[i][idxThreshold];
                sizeMap.put(compId, sizeMap.getOrDefault(compId, 0) + 1);
            }
            for(int i=1;i<=V;i++){
                int compId = componentId[i][idxThreshold];
                componentSize[idxThreshold][i] = sizeMap.get(compId);
            }
        }
    }

    // Find with path compression
    static int find(int[] parent, int u){
        while(parent[u] != u) {
            parent[u] = parent[parent[u]];
            u = parent[u];
        }
        return u;
    }

    // Query R: Given ENERGY, return how many cities are reachable from currentCity
    static int R(int ENERGY){
        int idx = Arrays.binarySearch(thresholds, ENERGY);
        if(idx < 0){
            idx = -idx - 2; 
        }
        if(idx < 0){
            return -1; // No edges with weight <= ENERGY
        }
        int size = componentSize[idx][currentCity];
        if(size == 1){
            return -1;
        }else{
            return size -1;
        }
    }

    // Query M to transform currentPassword to password using BFS on the password space.
    static int M(String PASSWORD){
        int start = passwordToInt(currentPassword);
        int target = passwordToInt(PASSWORD);
        if(start == target) {
            currentPassword = PASSWORD;
            return 0; // Already at the target password
        }

        Arrays.fill(visitedPassword, false);
        Arrays.fill(distPassword, Integer.MAX_VALUE);
        distPassword[start] = 0;
        visitedPassword[start] = true;

        int head=0, tail=0;
        queuePassword[tail++] = start;

        while(head < tail) {
            int cur = queuePassword[head++];
            int steps = distPassword[cur];
            if(steps >= 50) break; // limit BFS depth

            // Try all PNumbers
            for (int j=0; j<P; j++) {
                int nxt = addPasswordsInt(cur, pNumbersInt[j]);
                if(!visitedPassword[nxt]) {
                    visitedPassword[nxt] = true;
                    distPassword[nxt] = steps+1;
                    if(nxt == target) {
                        currentPassword = PASSWORD;
                        return steps+1;
                    }
                    queuePassword[tail++] = nxt;
                }
            }
        }

        return -1;
    }

    // Add two password states (both are int [0..9999]) digit-wise mod 10
    static int addPasswordsInt(int pwd1, int pwd2) {
        int result = 0;
        int factor = 1;
        for (int i=0; i<4; i++) {
            int d1 = (pwd1/factor) % 10;
            int d2 = (pwd2/factor) % 10;
            int digitSum = (d1 + d2) % 10;
            result += digitSum * factor;
            factor *= 10;
        }
        return result;
    }

    // Convert password string "abcd" to int a*1000 + b*100 + c*10 + d
    static int passwordToInt(String s) {
        int val = 0;
        for(int i=0; i<4; i++){
            val = val*10 + (s.charAt(i)-'0');
        }
        return val;
    }

    static String addPasswords(String pwd1, String pwd2){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<4;i++){
            int digitSum = (pwd1.charAt(i)-'0') + (pwd2.charAt(i)-'0');
            sb.append(digitSum%10);
        }
        return sb.toString();
    }

    // Query J to compute MST with priority to edges connected to city ID.
    static int J(int ID){
        // Collect edges once
        ArrayList<Edge> edges = new ArrayList<>();
        for(int i=1;i<=V;i++){
            for(Edge e : adjList[i]){
                if(i < e.to){
                    edges.add(new Edge(i, e.to, e.weight));
                }
            }
        }

        // Sort edges by weight
        Collections.sort(edges, Comparator.comparingInt(e -> e.weight));

        // Kruskal's algorithm including all edges connected to ID
        UnionFind uf = new UnionFind(V+1);
        int totalWeight = 0;

        // Include all edges connected to ID
        for(Edge e : adjList[ID]){
            int u = ID;
            int v = e.to;
            int uRoot = uf.find(u);
            int vRoot = uf.find(v);
            if(uRoot != vRoot){
                uf.union(u, v);
            }
            totalWeight += e.weight;
        }

        // Then, include the rest of the edges if they connect different components
        for(Edge e : edges){
            int u = e.from;
            int v = e.to;

            if(u == ID || v == ID){
                continue; // Already included
            }
            int uRoot = uf.find(u);
            int vRoot = uf.find(v);

            if(uRoot != vRoot){
                uf.union(u, v);
                totalWeight += e.weight;
            }
        }
        return totalWeight;
    }

    // Edge class for graph
    static class Edge{
        int to, weight;
        int from;

        Edge(int to, int weight){
            this.to = to;
            this.weight = weight;
        }

        Edge(int from, int to, int weight){
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    // Node for the priority queue (Dijkstra)
    static class MinHeapNode {
        int vertex;
        int distance;
        MinHeapNode(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    // Union-Find (Disjoint Set) for MST computations
    static class UnionFind{
        int[] parent;
        int[] rank;

        UnionFind(int n){
            parent = new int[n];
            rank = new int[n];
            for(int i=0;i<n;i++){
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int u){
            while(parent[u]!=u){
                parent[u] = parent[parent[u]];
                u = parent[u];
            }
            return u;
        }

        void union(int u, int v){
            int uRoot = find(u);
            int vRoot = find(v);
            if(uRoot == vRoot) return;
            if(rank[uRoot] < rank[vRoot]){
                parent[uRoot] = vRoot;
            }else if(rank[uRoot] > rank[vRoot]){
                parent[vRoot] = uRoot;
            }else{
                parent[vRoot] = uRoot;
                rank[uRoot]++;
            }
        }
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 1 << 20);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    String line = reader.readLine();
                    if(line == null){
                        return null;
                    }
                    tokenizer = new StringTokenizer(line);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInteger() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
          return Long.parseLong(next());
        }
    }
}
