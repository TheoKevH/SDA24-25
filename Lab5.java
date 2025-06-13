// Name: Theodore Kevin Himawan
// NPM: 2306210973
// Class: SDA B
// References: Leetcode, Google, Geeksforgeeks

import java.io.*;
import java.util.StringTokenizer;

public class Lab5 {

    private static InputReader in;
    private static PrintWriter out;
    private static DoublyLinkedList keyboard = new DoublyLinkedList();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();

        for (int i = 0; i < N; i++) {
            String command = in.next();
            char data;
            char direction;

            switch (command) {
                case "ADD":
                    direction = in.nextChar();
                    data = in.nextChar();
                    keyboard.add(data, direction);
                    break;

                case "DEL":
                    keyboard.delete();
                    break;

                case "MOVE":
                    direction = in.nextChar();
                    keyboard.move(direction);
                    break;

                case "START":
                    keyboard.start();
                    break;

                case "END":
                    keyboard.end();
                    break;

                case "SWAP":
                    keyboard.swap();
                    break;
            }
        }

        keyboard.printList();
        out.close();
    }

    private static class InputReader {

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

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }
}

class DoublyLinkedList {

    ListNode first;
    ListNode current;
    ListNode last;
    int size = 0;

    public DoublyLinkedList() {
        this.first = null;
        this.current = null;
        this.last = null;
    }

    public void printList() {
        ListNode node = first;
        while (node != null) {
            System.out.print(node.data);
            node = node.next;
        }
        System.out.println();
    }

    /*
    * Method to add ListNode relative to the {@code current} ListNode
    */
    public void add(char data, char direction) {
        ListNode node = new ListNode(data);

        if (size == 0){
            first = node;
            last = node;
            current = node;
        }else{
            if(direction == 'R'){
                node.prev = current;
                node.next = current.next;

                if(current.next != null){
                    current.next.prev = node;
                }else{
                    last = node; 
                }

                current.next = node;
            }else if(direction == 'L'){
                node.next = current;
                node.prev = current.prev;

                if(current.prev != null){
                    current.prev.next = node;
                }else{
                    first = node; 
                }

                current.prev = node;
            }
        }
        current = node; 
        size++;
    }

    /*
    * Method to delete the {@code current} ListNode
    */
    public void delete() {
        if (size == 0 || current == null){
            return;
        }

        if (current.prev != null){
            current.prev.next = current.next;
        }else{
            first = current.next; 
        }

        if(current.next != null){
            current.next.prev = current.prev;
        }else{
            last = current.prev; 
        }

        if(current.prev != null){
            current = current.prev;
        }else{
            current = current.next;
        }

        size--;
    }

    /*
    * Method to move left (prev) or right (next) from {@code current} ListNode
    */
    public void move(char direction) {
        if(direction == 'R' && current != null && current.next != null){
            current = current.next;
        }else if(direction == 'L' && current != null && current.prev != null){
            current = current.prev;
        }
    }

    /*
    * Method to move to the first (start) ListNode of the DoublyLinkedList
    */
    public void start() {
        if(size > 0){
            current = first;
        }
    }
    
    /*
    * Method to move to the last (end) ListNode of the DoublyLinkedList
    */
    public void end() {
        if(size > 0){
            current = last;
        }
    }

    /*
    * Method to swap all nodes from the left of the {@code current} node to the right of it
    */
    public void swap() {
        if(current == null || current.prev == null){
            return;
        }

        ListNode leftStart = first;
        ListNode leftEnd = current.prev;

        current.prev = null;
        first = current; 

        if(last != null){
            last.next = leftStart;
            leftStart.prev = last;
        }

        last = leftEnd;
        last.next = null;
    }
}

class ListNode {
    char data;
    ListNode next;
    ListNode prev;

    ListNode(char data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
