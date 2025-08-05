import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer; 

public class Lab4 {
    private static InputReader in;
    private static PrintWriter out;
    public static String a, b;
    public static int n, m;
    public static int modulo = (int) Math.pow(10, 9) + 7; 


    public static void main(String[] args) {
		InputStream inputStream = System.in;
		in = new InputReader(inputStream);
		OutputStream outputStream = System.out;
		out = new PrintWriter(outputStream);

        // Read inputs
		a = in.next();
		b = in.next();

		n = a.length();
		m = b.length();

        // Call the function to get the result
		long result = solve(a, b, n, m);

        // Output the result
        // TODO: Output the processed result
        out.println(result);
        // don't forget to close/flush the output
		out.close();
    }

    public static long solve(String a, String b, int n, int m){
        // TODO: Implement the logic here as required
        long[][] dp = new long[n + 1][m + 1];

        for(int i = 0; i <= n; i++){
            dp[i][0] = 1; 
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                if(a.charAt(i - 1) == b.charAt(j - 1)){
                    dp[i][j] = dp[i - 1][j];

                    if(i > 1){
                        dp[i][j] = (dp[i][j] + dp[i - 2][j - 1]) % modulo ;
                    }else{
                        dp[i][j] = (dp[i][j] + dp[i -1][j - 1]) % modulo;
                    }
                }else{
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        //SEMOGA BERHASIL PLSSSs
        return dp[n][m];
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

        public long nextLong() {
          return Long.parseLong(next());
      }
    }
}