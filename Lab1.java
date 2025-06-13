//Name: Theodore Kevin Himawan
//Kelas: SDA B
//TA Code: 
//Collaborator: Valentino Kim Fernando - 2306275771

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Lab1 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read inputs
        long N = in.nextInteger();
        long M = in.nextInteger();
        int K = in.nextInteger();
        int X = in.nextInteger();
        int Y = in.nextInteger();

        int A[] = new int[K];
        int B[] = new int[K];

        for (int i = 0; i < K; i++) {
            A[i] = in.nextInteger();
            B[i] = in.nextInteger();
        }

        long ans = 0;

        // TODO: Write your code here
        // ! Hint:  Note that to get the full score, you might need to edit other parts of the code as well

        //Total lab chairs until sofita
        long totalNotFinal = M * (X-1) + Y;
        int counter = K;

        if(K <= 0){
            out.println(totalNotFinal);
        }else{
            for(int i=0; i<K; i++){
                if(A[i] > X){
                    counter--;
                }else if(A[i] == X){
                    if(B[i] > Y){
                        counter--;
                    }
                }

                
            }
            ans = totalNotFinal - counter;
             out.println(ans);
        }


        // don't forget to close/flush the output
        out.close();
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
}