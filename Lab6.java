//Name: Theodore Kevin Himawan
//NPM: 2306210973
//References: Google, GeeksforGeeks
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.ArrayList;

public class Lab6 {
    private static InputReader in;
    private static PrintWriter out;
    private static AVLTree tree = new AVLTree();
    
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        // For input
        int N = in.nextInteger(); 

        for(int i = 0; i < N; i++){
            int size = in.nextInteger();
            int date = in.nextInteger();
            tree.insert(new File(size, date, true)); 
        }

        int Q = in.nextInteger();
        for (int i = 0; i < Q; i++){
            String query = in.next();
            switch (query) {
                case "A":
                    int size = in.nextInteger();
                    int date = in.nextInteger();
                    int parentId = tree.insert(new File(size, date, true)); 
                    out.println(parentId);

                    break;
                case "D":
                    size = in.nextInteger();
                    date = in.nextInteger();
                    int deletedId = tree.delete(new File(size, date, false));
                    out.println(deletedId);

                    break;
                case "P":
                    ArrayList<Integer> files = tree.inOrderTraversal();
                    if(files.isEmpty()){
                        out.println("-1");
                    }else{
                        for (int id : files) {
                            out.print(id + " ");
                        }
                        out.println();
                    }

                    break;
            }
        }

        out.close();
    }
}

class File implements Comparable<File> {
    static int nextId = 1;
    int id, size, date;

    File(int size, int date, boolean assignId) {
        this.size = size;
        this.date = date;
        if (assignId) {
            this.id = nextId++;
        }
    }

    @Override
    public int compareTo(File other) {
        if (this.size != other.size) return Integer.compare(this.size, other.size);
        if (this.date != other.date) return Integer.compare(this.date, other.date);
        return 0; 
    }
}

class Node {
    File file;
    Node left, right;
    int height;

    Node(File file) {
        this.file = file;
        this.height = 1;
    }
}

class AVLTree {
    private Node root;

    public int insert(File file) {
        root = insert(root, file);
        Node parentNode = findParent(root, file);
        if(parentNode == null){
            return file.id;
        }else{
            return parentNode.file.id;
        }
    }

    private Node insert(Node node, File file) {
        if (node == null) return new Node(file);
        
        if (file.compareTo(node.file) < 0) node.left = insert(node.left, file);
        else node.right = insert(node.right, file);

        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node); // Rebalance after insert
    }

    private Node find(Node node, File file) {
        if(node == null){
            return null;
        }
        if(file.compareTo(node.file) == 0){
            return node;
        }
        if(file.compareTo(node.file) < 0){
            return find(node.left, file);
        }

        return find(node.right, file);
    }

    private Node balance(Node node) {
        int balance = height(node.left) - height(node.right);
        
        if(balance > 1){
            if(height(node.left.left) >= height(node.left.right)){
                node = rotateRight(node);
            }else{
                node.left = rotateLeft(node.left);
                node = rotateRight(node);
            }
        }else if(balance < -1){
            if(height(node.right.right) >= height(node.right.left)){
                node = rotateLeft(node);
            }else{
                node.right = rotateRight(node.right);
                node = rotateLeft(node);
            }
        }
        
        return node;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public ArrayList<Integer> inOrderTraversal() {
        ArrayList<Integer> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    public int delete(File file) {
        int[] deletedId = new int[1];

        root = delete(root, file, deletedId);
        if(deletedId[0] == 0 ){
            return -1;
        }else{
            return deletedId[0];
        }
    }
    
    private Node delete(Node node, File file, int[] deletedId) {
        if(node == null){
            return null;
        }

        if(file.compareTo(node.file) < 0){
            node.left = delete(node.left, file, deletedId);
        }else if(file.compareTo(node.file) > 0){
            node.right = delete(node.right, file, deletedId);
        }else{
            deletedId[0] = node.file.id;
    
            if (node.left == null){
                return node.right;
            }else if(node.right == null){
                return node.left;
            }else{
                Node successor = findMin(node.right);
                node.right = deleteMin(node.right);
                successor.left = node.left;
                successor.right = node.right;

                successor.height = 1 + Math.max(height(successor.left), height(successor.right));

                successor = balance(successor);

                node = successor;
            }
        }
    
        if (node == null){
            return null;
        }
        node.height = 1 + Math.max(height(node.left), height(node.right));
    
        return balance(node);
    }
    
    private Node deleteMin(Node node) {
        if(node.left == null){
            return node.right;
        }
        node.left = deleteMin(node.left);
    
        node.height = 1 + Math.max(height(node.left), height(node.right));
    
        return balance(node);
    }    
    
    private void inOrderTraversal(Node node, ArrayList<Integer> result) {
        if (node != null){
            inOrderTraversal(node.left, result);
            result.add(node.file.id);
            inOrderTraversal(node.right, result);
        }
    }    

    private Node findParent(Node node, File file) {
        if(node == null){
            return null;
        }
        
        if((node.left != null && file.compareTo(node.left.file) == 0) ||
            (node.right != null && file.compareTo(node.right.file) == 0)){
            return node;
        }

        if(file.compareTo(node.file) < 0){
            return findParent(node.left, file);
        }

        return findParent(node.right, file);
    }

    private Node findMin(Node node) {
        while (node.left != null){
            node = node.left;
        }

        return node;
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }
}

class InputReader {
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
                if (line == null) return null;
                tokenizer = new StringTokenizer(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tokenizer.nextToken();
    }

    public int nextInteger() {
        String next = next();
        if (next == null) return -1;
        return Integer.parseInt(next);
    }
}