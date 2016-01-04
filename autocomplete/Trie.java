/**
 * Prefix-Trie. Supports linear time find() and insert(). 
 * Should support determining whether a word is a full word in the 
 * Trie or a prefix.
 * @author
 */

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;


/**
 * Prefix-Trie. Supports linear time find() and insert().
 *
 * @author Aditya
 */
public class Trie {
    private TrieNode sentinel;
    private String alphabet;
    private HashSet<Character> check;

    /**
     * initializes Trie
     */
    public Trie() {
        sentinel = new TrieNode();
        alphabet = null;
        check = new HashSet<Character>();
    }

    /**
     * initializes Trie
     *
     * @param input creates an alphabet
     */
    public Trie(String input) {
        sentinel = new TrieNode();
        alphabet = input;
        check = new HashSet<Character>();
        for (char c : alphabet.toCharArray()) {
            check.add(c);
        }
    }


    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     *
     * @param s          find word
     * @param isFullWord if prefix ok
     * @return boolean
     */
    public boolean find(String s, boolean isFullWord) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return sentinel.childhas(s, isFullWord);
    }

    /**
     * @param s String of new word
     */
    public void insert(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (alphabet != null) {
            for (char c : s.toCharArray()) {
                if (!check.contains(c)) {
                    return;
                }
            }
        }
        sentinel.addWord(s, s);
    }


    /**
     * @return list of words
     */
    public List<String> getAllWords() {
        List<String> words = new ArrayList<String>();
        if (sentinel.children.size() == 0) {
            return words;
        }
        getAll(sentinel.children, words);

        return words;

    }

    /**
     * Prefix-TrieNode Supports linear time find() and insert().
     * holds a character
     */
    private class TrieNode implements Comparable {
        private TrieNode parent;
        private HashMap<Character, TrieNode> children;
        private boolean isLeaf;
        private boolean endsWord;
        private Character character;

        /**
         * @param val the entry
         */
        public void setVal(String val) {
            this.val = val;
        }

        private String val;

        /**
         * @return TrieNode
         */
        public TrieNode getParent() {
            return parent;
        }

        /**
         * @return int hashcode
         */
        public int hashCode() {
            return character.hashCode();
        }

        /**
         * @return HashMap
         */
        public HashMap<Character, TrieNode> getChildren() {
            return children;
        }

        /**
         * @return boolean isleaf?
         */
        public boolean isLeaf() {
            return isLeaf;
        }

        /**
         * @return boolean endsWord?
         */
        public boolean endsWord() {
            return endsWord;
        }

        /**
         * @return char
         */
        public char getCharacter() {
            return character;
        }

        /**
         * initilizes
         */
        public TrieNode() {
            children = new HashMap<Character, TrieNode>();
            isLeaf = true;
            endsWord = false;
            character = '\u0000';
            val = null;

        }

        /**
         * @param c initializes
         */
        public TrieNode(char c) {
            children = new HashMap<Character, TrieNode>();
            isLeaf = true;
            endsWord = false;
            this.character = c;
            val = null;
        }

        /**
         * @param word to search
         * @param substring to add
         */
        protected void addWord(String word, String substring) {
            isLeaf = false;
            char charPos = word.charAt(0);
            if (!children.keySet().contains(charPos)) {
                TrieNode n = new TrieNode(charPos);
                n.parent = this;
                children.put(charPos, n);
            }

            if (word.length() > 1) {
                children.get(charPos).addWord(word.substring(1), substring);
            } else {
                children.get(charPos).endsWord = true;
                children.get(charPos).setVal(substring);
            }

        }

        /**
         * @param input full word or not
         * @param word  word being search for
         * @return boolean
         */
        protected boolean childhas(String word, boolean input) {
            char charPos = word.charAt(0);
            if (!children.keySet().contains(charPos)) {
                return false;
            }
            if (word.length() > 1) {
                return children.get(charPos).childhas(word.substring(1), input);
            }
            if (input) {
                if (!children.get(charPos).endsWord()) {
                    return false;
                }
            }
            return true;
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
            TrieNode other = (TrieNode) object;
            if (other.getCharacter() == '\u0000') {
                throw new IllegalArgumentException();
            }
            if (getCharacter() == other.getCharacter()) {
                return 0;
            }
            if (alphabet.indexOf(other.getCharacter()) < alphabet.indexOf(getCharacter())) {
                return 1;
            }
            if (alphabet.indexOf(other.getCharacter()) > alphabet.indexOf(getCharacter())) {
                return -1;
            }
            return 1;
        }
    }

    /**
     * @param kids   is the reverse
     * @param words  we have in the trie
     */
    public void getAll(HashMap<Character, TrieNode> kids, List<String> words) {
        for (Character c : alphabet.toCharArray()) {
            if (kids.containsKey(c)) {
                TrieNode n = kids.get(c);
                if (n.children.keySet().size() > 0) {
                    if (n.endsWord()) {
                        words.add(n.val);
                    }
                    getAll(n.children, words);

                } else {
                    words.add(n.val);
                }
            }
        }
    }


}

