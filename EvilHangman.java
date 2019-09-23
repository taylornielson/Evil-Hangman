package hangman;
import java.io.*;
public class EvilHangman {

    public static void main(String[] args) throws IOException, EmptyDictionaryException{
        int wordLength ;
        int numGuesses;
        Console c = System.console();
        File dictionary = new File(args[0]);
        try{
            wordLength = Integer.parseInt(args[1]);
            numGuesses = Integer.parseInt(args[2]);
            if (wordLength < 2){
                System.out.println("Usage: java [your main class name] dictionary wordLength guesses");
                return;
            }
            if (numGuesses < 1){
                System.out.println("Usage: java [your main class name] dictionary wordLength guesses");
                return;
            }
        } catch (Exception e) {
            System.out.println("Usage: java [your main class name] dictionary wordLength guesses");
            return;
        }

        EvilHangmanGame myGame = new EvilHangmanGame();
        myGame.startGame(dictionary, wordLength);
       // if (myGame.numWords() == 0){
         //   throw new EmptyDictionaryException();
        //}

        while (numGuesses > 0){
            if (numGuesses > 1){
                System.out.println("You have " + numGuesses + " guesses left");
            }
            else{
                System.out.println("You have 1 guess left");
            }
            System.out.print("Used letters: ");
            myGame.printGuessed();
            myGame.printWord();
            String guess = null;
            guess = c.readLine("Enter guess: ");
            while (!verify(guess))
            {
                if (guess != null ) System.out.println("Invalid Input");
                guess = c.readLine("Enter Next Guess: ");
            }

            while (true)
            {
                try {
                    myGame.makeGuess(guess.charAt(0));
                    break;
                }
                catch (Exception e)
                {
                    System.out.println("Letter " + guess + " already guessed.");
                    guess = c.readLine("Enter Next Guess: ");
                }
            }

            int letters = myGame.lastGuessedLetter;
            if (letters == 0)
            {
                System.out.println("Sorry, there are no "+guess+"'s");
                numGuesses--;
            }
            else
            {
                System.out.println("Yes, there are "+letters+ " "+guess+"'s");
            }

            if (myGame.isWon())
            {
                System.out.println("You win!");
                myGame.printWord();
                return;
            }
        }

        //if we got to this point, the game is lost
        System.out.println("You lose!\nThe word was "+myGame.fakeWord());
    }


    public static boolean verify(String s)
    {
        if (s==null) return false;
        if (s.length() != 1) return false;
        return Character.isLetter(s.charAt(0));
    }



}
