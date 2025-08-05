// Name: Theodore Kevin Himawan
// NPM: 2306210973
// Class: SDA B
// References: Geeksforgeeks (https://www.geeksforgeeks.org/binary-heap/)

import java.util.*;
import java.io.*;

public class Lab7 {
    private static InputReader in;
    private static PrintWriter out;
    private static FilmHeap heap;
    private static ArrayList<Film> filmList;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        in = new InputReader(inputStream);
        out = new PrintWriter(outputStream);

        int N = in.nextInt();

        heap = new FilmHeap();
        filmList = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            long vote = in.nextLong();
            Film film = new Film(vote);
            filmList.add(film);
            heap.insert(film);
        }

        int Q = in.nextInt();

        long vote;
        int film1, film2, filmNum;
        Film film;
        
        for (int i = 0; i < Q; i++) {
            String query = in.next();
            switch (query) {
                case "T":
                    vote = in.nextLong();
                    T(vote);
                    break;

                case "K":
                    vote = in.nextLong();
                    K(vote);
                    break;
            
                case "R":
                    filmNum = in.nextInt();
                    R(filmNum);
                    break;

                case "B":
                    film1 = in.nextInt();
                    film2 = in.nextInt();
                    B(film1, film2);
                    break;

                default:
                    break;
            }
            // heap.traverse(); // for debugging after each query
        }
        out.close();
    }

    static class Film implements Comparable<Film>{
        int id;
        long vote;
        static int idCounter;

        Film(long vote) {
            id = idCounter++;
            this.vote = vote;
        }
       
        @Override
        public int compareTo(Film other){
            if (this.vote != other.vote){
                return Long.compare(other.vote, this.vote);
            } else {
                return Integer.compare(this.id, other.id);
            }
        }

    }

    static class FilmHeap {
        ArrayList<Film> heap;
        HashMap<Integer, Integer> idToIndexMap;
        int size;

        public FilmHeap() {
            heap = new ArrayList<>();
            idToIndexMap = new HashMap<>();
            size = 0;
        }

        public static int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        public void percolateUp(int i) {
            while (i > 0 && heap.get(i).compareTo(heap.get(getParentIndex(i))) < 0) {
                swap(i, getParentIndex(i));
                i = getParentIndex(i);
            }
        }

        public void percolateDown(int i) {
            while (true) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                int largest = i;
        
                if (left < size && heap.get(left).compareTo(heap.get(largest)) < 0) {
                    largest = left;
                }
        
                if (right < size && heap.get(right).compareTo(heap.get(largest)) < 0) {
                    largest = right;
                }
        
                if (largest != i) {
                    swap(i, largest);
                    i = largest;
                } else {
                    break;
                }
            }
        }

        public void updateFilm(int id) {
            int index = idToIndexMap.get(id);
            percolateUp(index);
            percolateDown(index);
        }

        public void swap(int i, int j) {
            Film temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);

            idToIndexMap.put(heap.get(i).id, i);
            idToIndexMap.put(heap.get(j).id, j);
        }

        public void insert(Film film) {
            heap.add(film);
            idToIndexMap.put(film.id, size);
            percolateUp(size);
            size++;
        }

        public Film peek() {
            return heap.get(0);
        }

        public Film poll() {
            if (size == 0) {
                return null;
            }

            Film top = heap.get(0);
            swap(0, size - 1);
            heap.remove(size - 1);
            idToIndexMap.remove(top.id);
            size--;
            percolateDown(0);
            return top;

        }

        
        // =============== HELPER METHOD FOR DEBUGGING HEAP ===============
        public void traverse() {
            out.println("=============================");
            traverseHelper(0, 0);
            out.println("=============================");
        }

        // =============== HELPER METHOD FOR DEBUGGING HEAP ===============
        private void traverseHelper(int index, int depth) {
            if (index >= size) {
                return;
            }

            // Print the current node with indentation based on depth
            for (int i = 0; i < depth; i++) {
                out.print("  ");
            }
            out.println(heap.get(index).id + " (" + heap.get(index).vote + ")");

            // Traverse left and right children
            traverseHelper(2 * index + 1, depth + 1);
            traverseHelper(2 * index + 2, depth + 1);
        }
        
    }

    static void T(Long vote){
        Film newFilm = new Film(vote);
        filmList.add(newFilm);
        heap.insert(newFilm);
        Film topFilmT = heap.peek();
        out.println(topFilmT.id + " " + topFilmT.vote);
    }

    static void K(Long vote){
        Film topFilmK = heap.peek();
        topFilmK.vote -= vote;
        heap.updateFilm(topFilmK.id);
        Film newTopFilmK = heap.peek();
        out.println(newTopFilmK.id + " " + newTopFilmK.vote);
    }

    static void R(int filmNum){
        if (filmNum > heap.size) {
            out.println(-1);
        } else {
            List<Film> topFilms = new ArrayList<>();
            for (int j = 0; j < filmNum; j++) {
                Film filmR = heap.poll();
                topFilms.add(filmR);
                out.print(filmR.id + " ");
            }
            out.println();
    
            // Re-insert the films back into the heap
            for (Film filmR : topFilms) {
                heap.insert(filmR);
            }
        }
    }

    static void B(int film1, int film2){
        Film f1 = filmList.get(film1);
        Film f2 = filmList.get(film2);

        Film winner;
        if (f1.vote > f2.vote || (f1.vote == f2.vote && f1.id < f2.id)) {
            winner = f1;
        } else {
            winner = f2;
        }

        out.println(winner.id);

        winner.vote /= 2;
        heap.updateFilm(winner.id);
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

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong()
        {
            return Long.parseLong(next());
        }
    }
}