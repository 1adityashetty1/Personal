import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;

/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 *
 * @author Aditya
 */
public class Autocomplete {
    private TST tst;
    private HashSet<String> duplicates;

    /**
     * Initializes required data structures from parallel arrays.
     *
     * @param terms   Array of terms.
     * @param weights Array of weights.
     */
    public Autocomplete(String[] terms, double[] weights) {
        tst = new TST();
        duplicates = new HashSet<String>();
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms != weights");
        }
        for (int i = 0; i < terms.length; i++) {
            if (duplicates.contains(terms[i])) {
                throw new IllegalArgumentException("Duplicates");
            }
            tst.put(terms[i], weights[i]);
            duplicates.add(terms[i]);
        }

    }

    /**
     * Find the weight of a given term. If it is not in the dictionary,
     *
     * @param term to search
     * @return double
     */
    public double weightOf(String term) {
        if (term == null) {
            throw new IllegalArgumentException("Prefix is Null");
        }
        if (term.isEmpty()) {
            throw new IllegalArgumentException("Prefix is Empty");
        }
        return tst.get(term);
    }

    /**
     * Return the top match for given prefix, or null if there is no matching term.
     *
     * @param prefix Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix is null");
        }

        return tst.keysWithPrefix(prefix, 1).peek();
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     *
     * @param prefix to search
     * @param k      top percent
     * @return Iterable<String> best matches
     */
    public Iterable<String> topMatches(String prefix, int k) {
        if (k < 1) {
            throw new IllegalArgumentException("Non Positive K");
        }
        return tst.keysWithPrefix(prefix, k);
    }

    /**
     * Returns the highest weighted matches within k edit distance of the word.
     * If the word is in the dictionary, then return an empty list.
     *
     * @param word The word to spell-check
     * @param dist Maximum edit distance to search
     * @param k    Number of results to return
     * @return Iterable in descending weight order of the matches
     */
    public Iterable<String> spellCheck(String word, int dist, int k) {
        LinkedList<String> results = new LinkedList<String>();  
        /* YOUR CODE HERE; LEAVE BLANK IF NOT PURSUING BONUS */
        return results;
    }

    /**
     * Test client. Reads the data from the file,
     * then repeatedly reads autocomplete queries from standard input and printsthe top k terms.
     *
     * @param args takes the name of an input file and an integer k as command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);
        // process queries from standard input
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }

    /**
     * TST Supports linear time find() and insert().
     * holds a character
     */
    private class TST {
        private int N;              // size
        private Node root;   // root of TST

        /**
         * Node for TST
         */
        private class Node implements Comparable {
            private char c;                        // character
            private Node left, mid, right;  // left, middle, and right subtries
            private Double val;                     // double associated with string
            private Double total;
            private String word;

            /**
             * initializes Node
             */
            public Node() {
                left = mid = right = null;
                val = total = null;
                word = null;
                c = '\u0000';

            }

            /**
             * initializes Node
             *
             * @param other comparable
             */
            public Node(Node other) {
                left = mid = right = null;
                val = other.val;
                total = val;
                c = other.c;
                word = other.word;
            }

            /**
             * @param object some object
             * @return int
             */
            @Override
            public int compareTo(Object object) {
                if (object == null) {
                    throw new IllegalArgumentException();
                }
                Node other = (Node) object;
                if (other.total == null) {
                    throw new IllegalArgumentException("Total is null");
                }
                if (other.total == total) {
                    return 0;
                }
                if (other.total > total) {
                    return 1;
                }
                if (other.total < total) {
                    return -1;
                }
                return 1;
            }


        }

        /**
         * Initializes an empty string symbol table.
         */
        public TST() {
        }

        /**
         * Returns the number of key-double pairs in this symbol table.
         *
         * @return the number of key-double pairs in this symbol table
         */
        public int size() {
            return N;
        }

        /**
         * Does this symbol table contain the given key?
         *
         * @param key the key
         * @return <tt>true</tt> if this symbol table contains <tt>key</tt> and
         * <tt>false</tt> otherwise
         * @ throw news NullPointerException if <tt>key</tt> is <tt>null</tt>
         */
        public boolean contains(String key) {
            return get(key) != null;
        }

        /**
         * Returns the double associated with the given key.
         *
         * @param key the key
         * @return the double associated with the given key if the key is in the symbol table
         * and <tt>null</tt> if the key is not in the symbol table
         * @ throw news NullPointerException if <tt>key</tt> is <tt>null</tt>
         */
        public Double get(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            if (key.isEmpty()) {
                throw new IllegalArgumentException("key must have length >= 1");
            }
            Node x = get(root, key, 0);
            if (x == null) {
                return null;
            }
            return x.val;
        }

        // return subtrie corresponding to given key

        /**
         * Returns the double associated with the given key.
         *
         * @param x   the node
         * @param key the key
         * @param d   int  the letter place
         * @return Node associated with the given key if the key is in the symbol table
         * and <tt>null</tt> if the key is not in the symbol table
         * @ throw news IllegalArgumentException
         */
        private Node get(Node x, String key, int d) {
            if (key == null) {
                throw new NullPointerException();
            }
            if (x == null) {
                return null;
            }
            char c = key.charAt(d);
            if (c < x.c) {
                return get(x.left, key, d);
            } else if (c > x.c) {
                return get(x.right, key, d);
            } else if (d < key.length() - 1) {
                return get(x.mid, key, d + 1);
            } else {
                return x;
            }
        }

        /**
         * Inserts the key-double pair into the symbol table, overwriting the old double
         * with the new double if the key is already in the symbol table.
         * If the double is <tt>null</tt>, this effectively deletes the key from the symbol
         *
         * @param key the key
         * @param val the double
         * @ throw news IllegalArgumentException if <tt>key</tt> is <tt>null</tt>
         */
        public void put(String key, double val) {
            N++;
            root = put(root, key, val, 0);
        }

        /**
         * Inserts the key-double pair into the symbol table, overwriting the old double
         * with the new double if the key is already in the symbol table.
         * If the double is <tt>null</tt>, this effectively deletes the key from the  table.
         *
         * @param x   Node x
         * @param d   int the position
         * @param key the key
         * @param val the double
         * @return Node
         * @ throw news IllegalArgumentException if <tt>key</tt> is <tt>null</tt>
         */
        private Node put(Node x, String key, double val, int d) {
            if (val < 0) {
                throw new IllegalArgumentException("Negative Weight");
            }
            char c = key.charAt(d);
            if (x == null) {
                x = new Node();
                x.c = c;

            }
            /*if ((x.val != null) && (x.val == val) && (d == (key.length() - 1))) {
                throw new IllegalArgumentException("Duplicate Entry");
            } */
            if (c < x.c) {
                x.left = put(x.left, key, val, d);
            } else if (c > x.c) {
                x.right = put(x.right, key, val, d);
            } else if (d < key.length() - 1) {
                x.mid = put(x.mid, key, val, d + 1);
            } else {
                x.val = val;
                x.word = key;
            }
            if (x.total == null || val >= x.total) {
                x.total = val;
            }
            return x;
        }

        /**
         * @param prefix the wordsearch
         * @param k      some int
         * @return Queue<String> of words
         */
        public Queue<String> keysWithPrefix(String prefix, int k) {
            PriorityQueue<Node> queue = new PriorityQueue<Node>();
            Queue queue1 = new LinkedList<String>();
            if (prefix.isEmpty()) {
                collect(root, queue, queue1, k);
            } else {
                Node x = get(root, prefix, 0);
                if (x == null) {
                    return queue1;
                }
                collectroot(x, queue, queue1, k);
            }
            return queue1;
        }

        /**
         * @param x           some node
         * @param queue       the pq
         * @param stringQueue the list of words
         * @param k           the int
         */
        private void collect(Node x, Queue<Node> queue, Queue<String> stringQueue, int k) {
            if (stringQueue.size() == k) {
                return;
            } //ends immediately if k has been reached
            int count = 0;
            if (x == null) {
                return;
            } //ends if x is null
            if (x.val != null & x.val == x.total) {
                stringQueue.add(x.word);
            } //if x ends word adds it to queue and is highest in priority
            if (x.mid != null) { //adds mid to queue
                queue.add(x.mid);
                count++;
            }
            if (x.left != null) {
                queue.add(x.left); //adds left to quue
                count++;
            }
            if (x.right != null) {
                queue.add(x.right); //adds right to queue
                count++;
            }
            if (x.val != null & x.val != x.total) {
                x = new Node(x); //null because when readded, don't want to
                queue.add(x);
                count++;
            } //if x ends word but is not highest in priority, readded to queue
            if (count == 0) {
                return;
            } //if no action taken than branch is a leaf
            for (int i = 0; i < count; i++) {
                collect(queue.poll(), queue, stringQueue, k);
            }
        }

        /**
         * @param x           some node
         * @param queue       the pq
         * @param stringQueue the list of words
         * @param k           the int
         */
        private void collectroot(Node x, Queue<Node> queue, Queue<String> stringQueue, int k) {
            if (stringQueue.size() == k) {
                return;
            } //ends immediately if k has been reached
            int count = 0;
            if (x == null) {
                return;
            } //ends if x is null
            if (x.val != null & x.val == x.total) {
                stringQueue.add(x.word);
            } //if x ends word adds it to queue and is highest in priority
            if (x.mid != null) { //adds mid to queue
                queue.add(x.mid);
                count++;
            }
            if (x.val != null & x.val != x.total) {
                x = new Node(x); //null because when readded, don't want to
                queue.add(x);
                count++;
            } //if x ends word but is not highest in priority, readded to queue in new spot
            if (count == 0) {
                return;
            } //if no action taken than branch is a leaf
            for (int i = 0; i < count; i++) {
                collect(queue.poll(), queue, stringQueue, k);
            }
        }


    }
}

