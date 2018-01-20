package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class EvilHangmanGame implements IEvilHangmanGame{

    public static void main(String[] args) {
        //TODO Implement input validation
        String dictionaryName = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int guessCount = Integer.parseInt(args[2]);

        EvilHangmanGame game = new EvilHangmanGame();

        Path dictionaryPath = Paths.get(dictionaryName);

        game.startGame(dictionaryPath.toFile(),wordLength);

        for(int round = 0; round < guessCount; ++round){
            //TODO Implement game play loop
        }

        //TODO Implement loss conditions

    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     * <p>
     * This method should set up everything required to play the game,
     * but should not actually play the game. (ie. There should not be
     * a loop to prompt for input from the user.)
     *  @param dictionaryFile Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionaryFile, int wordLength) {
        dictionary = new TreeSet<>();
        guessList = new TreeSet<>();

        try {//Must Validate dictionaryFile BEFORE passing into this file... unless I come up with a better way
            Scanner in = new Scanner(dictionaryFile, StandardCharsets.UTF_8.name());

            while (in.hasNext()){
                String next = in.next();
                if(next.length() == wordLength){
                    dictionary.add(next);
                }
            }
        } catch (FileNotFoundException e) {
            return;
        }
    }

    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     * @throws GuessAlreadyMadeException If the character <code>guess</code>
     *                                                    has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if(!guessList.add(guess)){
            throw new GuessAlreadyMadeException();
        }

        return null;
    }

    //Data Members
    private Set<String> dictionary;
    private Set<Character> guessList;
    private Set<String> goodGuesses;
}
