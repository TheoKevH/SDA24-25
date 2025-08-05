//Name: Theodore Kevin Himawan
//NPM: 2306210973
//Class: SDA B

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Lab2 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read inputs
       //TODO
       String input = in.next();
       String check = "sda";
       int x = input.length();
       int y = check.length();

       System.out.println(solve(input, check, x, y)); //out kok enggak bisa ya?

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
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
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
    }


    public static long solve(String kata, String key, int x, int y) {
        //TODO

        //Create 2D table to store the results
        long table[][] = new long[x + 1][y + 1];

        for (int i = 0; i <= y; i++){
            table[0][i] = 0;
        }

        for (int i = 0; i <= x; i++){
            table[i][0] = 1;
        }

        for(int i = 1; i <= x; i++){
            for(int j = 1; j <= y; j++){
                if(kata.charAt(i - 1) == key.charAt(j - 1)){
                    table[i][j] = table[i - 1][j - 1] + table[i - 1][j];
                }else{
                    table[i][j] = table[i - 1][j];
                }
            }
        }

        return table[x][y];
    }
}