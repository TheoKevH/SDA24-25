//Name: Theodore Kevin Himawan
//Class: SDA B
//NPM: 2306210973
//Referece: Geeksforgeeks, Leetcode, Google

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.StringTokenizer;

public class Lab3 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read inputs
        int N = in.nextInteger();
        int[] heightArray = new int[N];

        // Process inputs
        for (int i = 0; i < N; i++) {
            int height = in.nextInteger();
            // TODO: Add your logic here to process each height
            heightArray[i] = height;
        }

        int[] result = new int[N];
        Stack<Integer> stack = new Stack<>();

        //The last element must be 0
        result[N-1] = 0;
        stack.push(heightArray[N-1]);
        for(int i = N-2; i >= 0; i--){
            int counter = 0;
            while(!stack.isEmpty() && stack.peek() < heightArray[i]){
                stack.pop(); 
                counter++;
            }
            if(stack.isEmpty()){
                result[i]=counter;
            }else{
                result[i]= counter + 1;
            }
            stack.push(heightArray[i]);
        }

        // Output the result
        // TODO: Output the processed result
        for(int i = 0; i < result.length; i++){
            if(i > 0){
                out.print(" ");
            }
            out.print(result[i]);
        }

        // don't forget to close/flush the output
        out.close();
    }

    // TODO: Implement the logic here as required (e.g., a method to calculate result)

    // Example:
    // public static <ReturnType> yourMethodName(<Parameters>) {
    //    // Implement your logic here
    // }

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
}