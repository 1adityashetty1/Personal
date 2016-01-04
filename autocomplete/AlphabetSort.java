import java.util.List;
import java.util.Scanner;
import java.util.HashSet;

/**
 * AlphabetSort. Supports linear time find() and insert().
 *
 * @author Aditya
 */
public class AlphabetSort {

    /**
     * @return trie in alphab
     */
    public Trie getTrie() {
        return trie;
    }


    private Trie trie;

    /**
     * @param args in main method
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextLine()) {
            throw new IllegalArgumentException("No Alphabet");
        }
        AlphabetSort beta = new AlphabetSort(sc.nextLine());
        if (!sc.hasNextLine()) {
            throw new IllegalArgumentException("No Words");
        }
        while (sc.hasNextLine()) {
            beta.getTrie().insert(sc.nextLine());
        }
        sc.close();
        List<String> output = beta.getTrie().getAllWords();
        for (String s : output) {
            System.out.println(s);
        } /*
        AlphabetSort beta = new AlphabetSort("agdbecfhijklmnopqrsty");
        beta.getTrie().insert("hello");
        beta.getTrie().insert("goodbye");
        beta.getTrie().insert("goodday");
        beta.getTrie().insert("death");
        beta.getTrie().insert("goop");
        List<String> Output = beta.getTrie().getAllWords();
        for(String s: Output){
            System.out.println(s);} */

    }

    /**
     * initializes AlphabetSort
     *
     * @param input creates an alphabet
     */
    public AlphabetSort(String input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null");
        }
        if (input.isEmpty()) {
            throw new IllegalArgumentException("input is empty");
        }
        if (duplicates(input.toCharArray())) {
            throw new IllegalArgumentException("Duplicate Letters");
        }

        trie = new Trie(input);
    }

    /**
     * @param list of alphabet char
     * @return boolean of duplicates
     */
    private boolean duplicates(char[] list) {
        HashSet<Character> lump = new HashSet<Character>();
        for (char i : list) {
            if (lump.contains(i)) {
                return true;
            }
            lump.add(i);
        }
        return false;
    }


}

