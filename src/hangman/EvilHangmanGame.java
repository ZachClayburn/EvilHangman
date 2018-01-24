package hangman;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{

    public static void main(String[] args) {
        //TODO Implement input validation
        String dictionaryName = args[0];
        int wordLength = Integer.parseInt(args[1]);
        int guessCount = Integer.parseInt(args[2]);

        EvilHangmanGame game = new EvilHangmanGame();

        Path dictionaryPath = Paths.get(dictionaryName);

        game.startGame(dictionaryPath.toFile(),wordLength);

        Scanner in = new Scanner(System.in);

        for(int round = 0; round < guessCount; ++round){
            //TODO Implement game play loop
            char guess = in.next().charAt(0);//TODO Better input for game play
            try {
                game.makeGuess(guess);
            } catch(GuessAlreadyMadeException e){
                //TODO Handle already made guesses
            }
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
                    dictionary.add(next.toLowerCase());
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
        Map<String,Set<String>> partitions = new HashMap<>();

        for(String word : dictionary) {
            Set<String> newSet = new HashSet<>();
            newSet.add(word);
            newSet = partitions.putIfAbsent(getMatchPattern(word,guess),newSet);
            if(newSet != null){
                newSet.add(word);
            }
        }

        int maxCount = 0;
        Set<String> newDictionary = null;

        for(Set<String> partition : partitions.values()){
            if(partition.size() > maxCount){
                newDictionary = partition;
                maxCount = newDictionary.size();
            }
        }

        dictionary = newDictionary;
        return newDictionary;
    }

    /*
     * Data Members
     */
    private Set<String> dictionary;
    private Set<Character> guessList;

    /*
     * Helper Functions
     */

    static String getMatchPattern(String word, char guess){
        StringBuilder matchPattern = new StringBuilder();

        for(int i = 0; i < word.length(); i++){
            if(word.charAt(i) == guess){
                matchPattern.append('+');
            }else{
                matchPattern.append('-');
            }
        }

        return matchPattern.toString();
    }
}
