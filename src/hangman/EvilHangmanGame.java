package hangman;

import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.SortedSet;

public class EvilHangmanGame implements IEvilHangmanGame {
    private Set<String> words;
    private Map<Integer, String> guessMap;
    private int wordLength;
    private SortedSet<Character> guessedChar;
    private HashMap<String, Set<String>> wordDivider;
    private int lettersLeft;
    private int guessNum;
    public char lastGuessedLetter;
    public int numLetter;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        this.wordLength = wordLength;
        guessedChar = new TreeSet<Character>();
        words = new HashSet<String>();
            //throws empty dictionary exception if dictionary file is empty
            if (dictionary.length() == 0) {
                 throw new EmptyDictionaryException();
            }
            FileReader file = new FileReader(dictionary);
            BufferedReader buffer = new BufferedReader(file);
            Scanner scanner = new Scanner(buffer);
            while (scanner.hasNext()) {
                String line = scanner.next();
                if (line.length() == wordLength) {
                    words.add(line);
                }
            }
            //Throws emptyDictionary exception if no words of the correct length are found
            if (words.size() == 0) {
                throw new EmptyDictionaryException();
            }
            guessMap = new HashMap<Integer, String>();
            for (int i = 0; i < wordLength; ++i) {
                guessMap.put(i, "-");
            }
            lettersLeft = wordLength;
            numLetter = 0;
            guessNum = 0;


    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        Character charGuess = Character.toLowerCase(guess);
        wordDivider = new HashMap<String, Set<String>>();
        if (guessedChar.contains(charGuess)) {
            throw new GuessAlreadyMadeException();
        }
        guessedChar.add(charGuess);
        lastGuessedLetter = guess;
        Iterator<String> it = words.iterator();
        while (it.hasNext()) {
            wordDivider = separateWords(guess, wordDivider, it.next());
        }
        words = getBest(wordDivider, guess);
        update_guesses();
        return words;
    }
    private void update_guesses()
    {
        int ctr = 0;
        for (int i = 0; i < wordLength; ++i){
            if (guessMap.get(i) != "-"){
                ctr += 1;
            }
        }
        lettersLeft = wordLength - ctr;
    }
    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedChar;
    }

    private HashMap<String, Set<String>> separateWords(char g, HashMap<String, Set<String>> h, String s) {
        StringBuilder builder = new StringBuilder();
        Set<String> tempSet = new HashSet<String>();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == g) {
                builder.append(g);
            } else {
                if (guessMap.get(i) != "-") {
                    builder.append(guessMap.get(i));
                } else {
                    builder.append("-");
                }

            }
        }
        if (h.get(builder.toString()) != null) {
            h.get(builder.toString()).add(s);
        } else {
            tempSet.add(s);
            h.put(builder.toString(), tempSet);
        }
        return h;
    }
    //FIXME FIND THE BIGGEST SET
    private Set<String> getBest(HashMap<String, Set<String>> h, char g) {
        numLetter = 0;
        Set<String> tempSet = new HashSet<String>();
        String tempKey = "";
        for (Map.Entry<String, Set<String>> entry : h.entrySet()) {
            if (entry.getValue().size() > tempSet.size()) {
                tempSet = entry.getValue();
                tempKey = entry.getKey();
            } else if (entry.getValue().size() == tempSet.size()) {
                StringBuilder temp = new StringBuilder();
                temp.append(g);
                String guess = temp.toString();
                if (!entry.getKey().contains(guess) && tempKey.contains(guess)) {
                    tempKey = entry.getKey();
                    tempSet = entry.getValue();
                } else if (entry.getKey().contains(guess) && tempKey.contains(guess)) {
                    if (getNumLetters(entry.getKey(), g) < getNumLetters(tempKey, g)) {
                        tempKey = entry.getKey();
                        tempSet = entry.getValue();
                    } else if (getNumLetters(entry.getKey(), g) == getNumLetters(tempKey, g)) {
                        if (getLetterLocation(entry.getKey(), g) > getLetterLocation(tempKey, g)) {
                            tempKey = entry.getKey();
                            tempSet = entry.getValue();
                        } else if (getLetterLocation(entry.getKey(), g) == getLetterLocation(tempKey, g)) {
                            int antiCounter = 0;
                            while(getLetterLocation(entry.getKey(), g, antiCounter) == getLetterLocation(tempKey, g, antiCounter)){
                                antiCounter +=1;
                            }
                            if(getLetterLocation(entry.getKey(), g, antiCounter) >= getLetterLocation(tempKey, g, antiCounter)) {
                                tempKey = entry.getKey();
                                tempSet = entry.getValue();
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < wordLength; ++i){
            StringBuilder build = new StringBuilder();
            build.append(tempKey.charAt(i));
            guessMap.put(i, build.toString());
        }
        for (int i = 0; i < wordLength; ++i){
            if (tempKey.charAt(i) == g){
                numLetter +=1;
            }
        }
        return tempSet;
    }


    private int getNumLetters(String s, char g){
        int total= 0;
            for (int i = 0; i < s.length(); ++i){
                if (s.charAt(i) == g){
                    total += 1;
                }
            }
         return total;
    }

    private int getLetterLocation(String s, char g){
        for (int i = s.length() -1; i >= 0; i--){
            if (s.charAt(i) == g){
                return i;
            }
        }
        return 0;
    }
    private int getLetterLocation(String s, char g, int index){
        for (int i = s.length() -(index + 1); i >= 0; i--){
            if (s.charAt(i) == g){
                return i;
            }
        }
        return 0;
    }
    public int numWords()
    {
        return words.size();
    }

    public void printGuessed(){
        if (!guessedChar.isEmpty()) {
            SortedSet<Character> temp = getGuessedLetters();
            System.out.println("Used letters: " + temp.toString().substring(1,temp.toString().length()-1));
            //System.out.println("\n");

        }
        else{
            System.out.println("Used letters: ");
        }
    }

    public void printWord(){
        StringBuilder build = new StringBuilder();
        for(int i = 0; i < wordLength; ++i){
            build.append(guessMap.get(i));
        }
        System.out.println("Word: ");
        System.out.println(build.toString());
    }
    public boolean isWon()
    {
        return (numLetter == wordLength);
    }
    public String fakeWord(){
        Iterator<String> it = words.iterator();
        if (!it.hasNext()) return "";
        else return it.next();
    }


}
