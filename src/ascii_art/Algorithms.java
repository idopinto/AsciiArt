package ascii_art;

import java.util.HashSet;
import java.util.Set;

/**
 *  This class implements the algorithms required in ex4_2
 *  @author Ido Pinto
 */
public class Algorithms {

    /**
     *  Given list of words this function returns how many unique code morse is in the list.
     *  note. if two words are translated equally then their code will count once.
     *  this algorithm should run in O(S) time - complexity when S = the sum of the length of each word in the list.
     * @param words Array of strings
     * @return # of unique code morse in words.
     */
    public static int uniqueMorseRepresentations(String[] words) {
        String[] morseSet = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
                "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-",
                "..-", "...-", ".--", "-..-", "-.--", "--.."};
        return buildUniqueMorseCodeTable(words, morseSet);
    }

    /*
     * this function builds HashSet of unique code morse from given words list.
     * for every word it generates its code morse and add it to the set.
     * if code morse is generated again than it won't be added to the set.
     */
    private static int buildUniqueMorseCodeTable(String[] words, String[] morseSet) {
        Set<String> morseCodeTable = new HashSet<>(); // <morseCode,word>
        String morsedWord = "";
        for (String word : words) {
            morsedWord = generateMorseCode(word, morseSet);
            morseCodeTable.add(morsedWord);
        }
        return morseCodeTable.size();
    }

    /*
     * this function generates one code morse from given word.
     * for ever letter in the word it finds the corresponding code more in morseSet[word[i] - 'a'] and concatenate it
     * to the result morse code.
     */
    private static String generateMorseCode(String word, String[] morseSet) {
        StringBuilder morsedWord = new StringBuilder();
        for (int i = 0; i < word.length(); ++i) {
            morsedWord.append(morseSet[word.charAt(i) - 97]);
        }
        return morsedWord.toString();
    }


    /*
     * Finds and returns duplicate value in integer array of size n+1 .
     * which holds values from 1-n.
     * assuming there is one duplicate and can appear more than once.
     */
    public static int findDuplicate(int[] numList) {
        // case there is no duplicate is the number list length is 0 or 1
        if (numList.length <= 1) {
            return -1;
        }

        // note that  there is a duplicate iff there is cycle in the path created by
        // numList[0] -> numList[numList[0]] -> numList[numList[numList[0]]] -> ...
        // and if cycle does indeed exit then the duplicate is the first number that created the cycle.
        // the reason for that is that we refer to the array values as pointers to the array's indexes, so if x is a
        // duplicate then multiply nodes in the graph point to x.
        // the strategy is as follows:
        // 1. find the cycle -> 2. find the first number that created the cycle -> 3.return this number.

        // initialize the pointers
        int regularPointer = numList[0];
        int superPointer = numList[numList[0]];

        // if the superPointer and the regularPointer are equal then it means we're in cycle. that's good
        while (superPointer != regularPointer) {
            regularPointer = numList[regularPointer];
            superPointer = numList[numList[superPointer]];
        }

        // now let the regularPointer be initialized again
        regularPointer = 0;
        // the second time the pointers are equal we can relax and go watch netflix because it means we found the
        // entrance to the cycle aka the almighty duplicate is found. YAY
        while (superPointer != regularPointer) {
            regularPointer = numList[regularPointer];
            superPointer = numList[superPointer];
        }
        return regularPointer;
    }

}