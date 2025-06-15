//Name: Theodore Kevin Himawan
//NPM: 2306210973
//References: Stackoverflow, Geeksforgeeks, freeCodeCamp, ChatGPT

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class TP1 {
    private static InputReader in;
    private static PrintWriter out;
    private static PriorityQueue<Pelanggan> customerQueue = new PriorityQueue<>();
    private static int customerCount = 0; 
    private static long[] hargaIkan;
    private static Stack<Long> diskon = new Stack<>();
    private static HashMap<Long, Pelanggan> map = new HashMap<>();
    private static int queryCount = 1;
    private static ArrayList<Solution[][]> dpTable = new ArrayList<>();
    private static long[] hargaSuvenir;
    private static long[] nilai;
    
    public static void main(String[] args){
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        long N = in.nextLong();
        long M = in.nextLong();
        long Q = in.nextLong();

        hargaIkan = new long[(int) N];
        for (int i = 0; i < N; i++) {
            hargaIkan[i] = in.nextLong();
        }

        // Membaca harga suvenir
        hargaSuvenir = new long[(int) M];
        for (int i = 0; i < M; i++) {
            hargaSuvenir[i] = in.nextLong();
        }

        // Membaca nilai kebahagiaan suvenir
        nilai = new long[(int) M];
        for (int i = 0; i < M; i++) {
            nilai[i] = in.nextLong();
        }

        // Membaca setiap query
        for (; queryCount <= Q; queryCount++) {
            String query = in.next(); 

            switch (query) {
                case "A": 
                    A();
                    break;
                case "B": 
                    B();
                    break;
                case "L": 
                    L();
                    break;
                case "S":
                    S();
                    break;
                case "D":
                    D();
                    break;
                case "O":
                    O();
                    break;
            }
        }

        out.close();
    }

    // Ini buat query A (Keluarin ID dari customer)
    public static void A(){
        long budget = in.nextLong();
        long kesabaran = in.nextLong();
        Pelanggan newCust = new Pelanggan(customerCount, budget, kesabaran);
        customerQueue.add(newCust);
        map.put((long) newCust.id, newCust);
        customerCount++;
        out.println(newCust.id);
    }

    // Ini buat query S (Minimum difference harga ikan dengan budget)
    public static void S(){
        long input = in.nextLong();
        int left = 0;
        int right = hargaIkan.length - 1;

        // Binary search
        while (left <= right){
            int mid = left + ((right - left) / 2);

            if (hargaIkan[mid] < input){
                left = mid + 1;
            }else{
                right = mid - 1;
            }
        }    
        
        long minDiff = Long.MAX_VALUE;

        if(left > 0){
            minDiff = Math.min(minDiff, Math.abs(hargaIkan[left - 1] - input));
        }
        if(left < hargaIkan.length){
            minDiff = Math.min(minDiff, Math.abs(hargaIkan[left] - input));
        }

        out.println(minDiff);
    }

    // Ini buat query L (Keluarin budget customer berdasarkan id mereka)
    public static void L(){
        long id = in.nextLong();
        Pelanggan customerOut = map.remove(id);

        if(customerOut != null && customerOut.patience > queryCount){
            out.println(customerOut.budget);
        }else{
            out.println(-1);
        }
    }

    // Ini buat query D (Keluarin jumlah voucher diskon yang ada dalam stack)
    public static void D(){
        long diskonValue = in.nextLong();
        diskon.push(diskonValue);
        out.println(diskon.size());
    }

    // Ngaco
    public static void B(){
        Pelanggan customer;

        while (true){
            customer = customerQueue.poll();

            if(customer == null){
                out.println(-1);
                return;
            }

            if(customer.patience <= queryCount){
                map.remove((long)customer.id, customer);
                continue;
            }

            if(map.getOrDefault((long) customer.id, null) == null){
                continue;
            }

            break;
        }

        int left = 0;
        int right = hargaIkan.length - 1;

        // Binary search
        while(left <= right){
            int mid = (right + left) / 2;

            if(hargaIkan[mid] <= customer.budget){
                left = mid + 1;
            }else{
                right = mid - 1;
            }
        }

        if(right < 0){
            out.println(customer.id);
            map.remove((long)customer.id);
            return;
        }

        long harga = hargaIkan[right];

        if(customer.budget == harga){
            if (!diskon.isEmpty()){
                long diskonValue = diskon.pop();
                harga -= diskonValue;
            }
        }else if(customer.budget > harga){
            diskon.push(customer.budget - harga);
        }

        customer.budget -= Math.max(harga, 1);
        customer.patience = customer.reset + queryCount;
        customerQueue.add(customer);
        out.println(customer.budget);
    }

    // Ini buat query O
    public static void O(){
        long type = in.nextLong();
        long x = in.nextLong();

        int lastX = dpTable.size() - 1;

        if (lastX < x + 1){
            for (int i = lastX + 1; i < x + 1; i++){
                Solution[][] souvenirs = new Solution[hargaSuvenir.length + 1][3];
                for (int j = 0; j < 3; j++) {
                    souvenirs[0][j] = new Solution();
                }
                dpTable.add(souvenirs);
            }

            for (int i = 1; i < hargaSuvenir.length + 1; i++){
                for (int j = lastX + 1; j < x + 1; j++){
                    Solution[][] row = dpTable.get(j);
                    Solution max;

                    max = Solution.max(Solution.max(row[i - 1][1], row[i - 1][0]), row[i - 1][2]);

                    if(j < hargaSuvenir[i - 1]){
                        row[i][0] = new Solution(max);
                        row[i][1] = new Solution(max);
                        row[i][2] = new Solution(max);
                    }else{
                        Solution souvenir1 = new Solution(dpTable.get((int) (j - hargaSuvenir[i - 1]))[i - 1][0]);
                        souvenir1.addSouvenir(nilai[i - 1], i);
                        Solution souvenir2 = new Solution(dpTable.get((int) (j - hargaSuvenir[i - 1]))[i - 1][1]);
                        souvenir2.addSouvenir(nilai[i - 1], i);
                        row[i][0] = new Solution(max);
                        row[i][1] = souvenir1;
                        row[i][2] = souvenir2;
                    }
                }
            }
        }

        Solution max = Solution.max(Solution.max(dpTable.get((int) x)[hargaSuvenir.length][0],
                dpTable.get((int) x)[hargaSuvenir.length][1]), dpTable.get((int) x)[hargaSuvenir.length][2]);

        if (type == 2){
            out.println(max.points + " " + max.toString());
        }

        else if (type == 1){
            out.println((max.points + "").trim());
        }
    }

    // Buat class Pelanggan
    static class Pelanggan implements Comparable<Pelanggan>{
        int id;
        long budget;
        long patience;
        long reset; //ini buat keep track kesabarannya

        Pelanggan(int id, long budget, long patience){
            this.id = id;
            this.budget = budget;
            this.patience = patience + queryCount;
            this.reset = patience;
        }

        @Override
        public int compareTo(Pelanggan other){
            // Prioritas 1: Budget terbesar
            if (this.budget != other.budget){
                return Long.compare(other.budget, this.budget);
            }
            // Prioritas 2: Kesabaran terkecil
            if (this.patience != other.patience){
                return Long.compare(this.patience, other.patience);
            }
            // Prioritas 3: ID terkecil
            return Integer.compare(this.id, other.id);
        }
    }

    static class Solution{
        long points;
        Deque<Long> souvenirs;

        Solution() {
            points = 0;
            souvenirs = new ArrayDeque<>();
        }

        Solution(Solution solution) {
            this.points = solution.points;
            this.souvenirs = new ArrayDeque<>(solution.souvenirs);
        }

        public void addSouvenir(long happiness, long souvenir) {
            points += happiness;
            souvenirs.add(souvenir);
        }

        public static Solution max(Solution a, Solution b) {
            if (a.points == b.points) {
                Deque<Long> c = new ArrayDeque<>(a.souvenirs);
                Deque<Long> d = new ArrayDeque<>(b.souvenirs);

                while (!c.isEmpty() && !d.isEmpty()) {
                    long first = c.pollFirst();
                    long second = d.pollFirst();

                    if (first != second) {
                        return first < second ? a : b;
                    }
                }
            } else {
                return a.points > b.points ? a : b;
            }

            return a;
        }

        @Override
        public String toString() {
            String result =  "";
            for (long souvenir : souvenirs)
                result += souvenir + " ";
            return result;
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
                    tokenizer = new StringTokenizer(reader.readLine());
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