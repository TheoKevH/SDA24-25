// Name: Theodore Kevin Himawan
// NPM: 2306210973
// Class: SDA B

import java.io.*;
import java.util.*;

public class Lab8 {
    private static InputReader in;
    private static PrintWriter out;
    private static int numVertices, numEdges;
    private static Map<Integer, Integer> stopIndexMap;
    private static int numUniqueStops;
    private static long[][] distanceMatrix;
    private static List<Edge>[] adjacencyList;
    private static Set<Integer> uniqueStops;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read the number of vertices and edges
        numVertices = in.nextInt();
        numEdges = in.nextInt();
        adjacencyList = new ArrayList[numVertices + 1];
        for (int i = 1; i <= numVertices; i++) {
            adjacencyList[i] = new ArrayList<>();
        }

        // Build the graph by reading all edges
        for (int i = 0; i < numEdges; i++) {
            int from = in.nextInt();
            int to = in.nextInt();
            long weight = in.nextLong();
            adjacencyList[from].add(new Edge(to, weight));
            adjacencyList[to].add(new Edge(from, weight));
        }

        int totalDays = in.nextInt(); // Number of days the rule is applied
        int maxDuration = in.nextInt(); // Time until the school gate closes

        // Gather all unique stops from the students' input
        uniqueStops = new HashSet<>();
        uniqueStops.add(1); // Add the school stop (stop number 1)
        List<List<Integer>> dailyStudentStops = new ArrayList<>();
        for (int i = 0; i < totalDays; i++) {
            int numStudents = in.nextInt();
            List<Integer> studentStops = new ArrayList<>();
            for (int j = 0; j < numStudents; j++) {
                int stop = in.nextInt();
                studentStops.add(stop);
                uniqueStops.add(stop);
            }
            dailyStudentStops.add(studentStops);
        }

        // Map each unique stop to an index for the distance matrix
        stopIndexMap = new HashMap<>();
        int index = 0;
        for (int stop : uniqueStops) {
            stopIndexMap.put(stop, index++);
        }
        numUniqueStops = uniqueStops.size();

        // Initialize the distance matrix with infinite values
        distanceMatrix = new long[numUniqueStops][numUniqueStops];
        for (int i = 0; i < numUniqueStops; i++) {
            Arrays.fill(distanceMatrix[i], Long.MAX_VALUE);
        }

        // Create an array of unique stops based on their indices
        int[] stopList = new int[numUniqueStops];
        for (Map.Entry<Integer, Integer> entry : stopIndexMap.entrySet()) {
            stopList[entry.getValue()] = entry.getKey();
        }

        // Compute the shortest paths between all pairs of unique stops
        for (int i = 0; i < numUniqueStops; i++) {
            int sourceStop = stopList[i];
            Map<Integer, Long> distances = dijkstra(sourceStop);
            for (int j = 0; j < numUniqueStops; j++) {
                int targetStop = stopList[j];
                distanceMatrix[i][j] = distances.getOrDefault(targetStop, Long.MAX_VALUE);
            }
        }

        // Process each day's student pick-up schedule
        for (List<Integer> studentStops : dailyStudentStops) {
            long totalTime = 0;
            int currentStop = 1; // Start from the school
            int lastPickedStop = -1;
            boolean pickedAtLeastOne = false;

            for (int stop : studentStops) {
                int idxCurrent = stopIndexMap.get(currentStop);
                int idxNext = stopIndexMap.get(stop);
                long timeToNext = distanceMatrix[idxCurrent][idxNext];
                long timeBackToSchool = distanceMatrix[idxNext][stopIndexMap.get(1)];

                if (totalTime + timeToNext + timeBackToSchool <= maxDuration) {
                    totalTime += timeToNext;
                    currentStop = stop;
                    lastPickedStop = stop;
                    pickedAtLeastOne = true;
                } else {
                    break;
                }
            }

            if (pickedAtLeastOne) {
                int idxCurrent = stopIndexMap.get(currentStop);
                totalTime += distanceMatrix[idxCurrent][stopIndexMap.get(1)];
                out.println(totalTime + " " + lastPickedStop);
            } else {
                out.println("-1 -1");
            }
        }

        out.close();
    }

    // Dijkstra's algorithm to compute shortest paths from the source to all unique stops
    static Map<Integer, Long> dijkstra(int source) {
        Map<Integer, Long> distances = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new Node(source, 0));
        Set<Integer> visited = new HashSet<>();
        Set<Integer> remainingStops = new HashSet<>(uniqueStops);
        remainingStops.remove(source);

        while (!priorityQueue.isEmpty() && !remainingStops.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            int u = currentNode.id;
            if (visited.contains(u)) continue;
            visited.add(u);
            distances.put(u, currentNode.dist);
            if (remainingStops.contains(u)) {
                remainingStops.remove(u);
            }
            for (Edge edge : adjacencyList[u]) {
                int v = edge.to;
                if (!visited.contains(v)) {
                    priorityQueue.add(new Node(v, currentNode.dist + edge.weight));
                }
            }
        }
        return distances;
    }

    static class Edge {
        int to;
        long weight;

        public Edge(int destination, long weight) {
            this.to = destination;
            this.weight = weight;
        }
    }

    static class Node implements Comparable<Node> {
        int id;
        long dist;

        public Node(int id, long distance) {
            this.id = id;
            this.dist = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Long.compare(this.dist, other.dist);
        }
    }

    // Custom input reader for efficient I/O operations
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        return null;
                    }
                    tokenizer = new StringTokenizer(line);
                } catch (IOException e) {
                    return null;
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            String token = next();
            if (token == null) return -1;
            return Integer.parseInt(token);
        }

        public long nextLong() {
            String token = next();
            if (token == null) return -1;
            return Long.parseLong(token);
        }
    }
}
