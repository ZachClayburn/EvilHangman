package hangman;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

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

    public EvilHangmanGame(){

    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     * <p>
     * This method should set up everything required to play the game,
     * but should not actually play the game. (ie. There should not be
     * a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength) {

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
        return null;
    }

    //Data Members
    private Set<String> dictionary;
    private Set<Character> guessList;
}
