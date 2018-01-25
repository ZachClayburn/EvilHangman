package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{

    public static void main(String[] args) {
        boolean badInputs = false;
        String dictionaryName = null;
        int wordLength = 0;
        int guessCount = 0;
        //Check for correct number of arguments
        if(args.length != 3){
            System.out.println("Incorrect number of arguments!");
            badInputs = true;
        }else {
            dictionaryName = args[0];
            try {
                wordLength = Integer.parseInt(args[1]);
                guessCount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e){
                badInputs = true;
                System.out.println("<word length> and <number of guesses> must be integers!");
            }
            if (wordLength < 1 || guessCount < 1){
                badInputs = true;
                System.out.println("<word length> and <number of guesses> must be greater than one!");
            }
        }

        if (badInputs){
            System.out.println("Correct usages is\nJava hangman.EvilHangmanGame <dictionary name> <word length> <number of guesses>");
            return;
        }
        EvilHangmanGame game = new EvilHangmanGame();

        Path dictionaryPath = Paths.get(dictionaryName);

        game.startGame(dictionaryPath.toFile(),wordLength);

        Scanner in = new Scanner(System.in);

        for(int round = 0; round < guessCount; ++round){
            //TODO Implement game play loop
            System.out.println("Guess " + (round+1) + "/" + guessCount);
            System.out.println(game.getHintString());
            System.out.println("Make a Guess:");
            String input = in.next();
            if (input.equals("Zach")){
                game.cheat();
                round--;
                continue;
            }
            char guess = input.charAt(0);//TODO Better input for game play
            try {
                game.makeGuess(guess);
            } catch(GuessAlreadyMadeException e){
                //TODO Handle already made guesses
                System.out.println("You've already guessed that letter, try again!");
                round--;
            }
            if(game.getHasWon()){
                //TODO Implement wind condition
                System.out.println("YOU WON!");
                System.out.println("My Word was " + game.getHintString());
                return;
            }
            System.out.println("\n\n");
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
        goodWord = new StringBuilder();

        try {
            Scanner in = new Scanner(dictionaryFile, StandardCharsets.UTF_8.name());

            while (in.hasNext()){
                String next = in.next();
                if(next.length() == wordLength){
                    dictionary.add(next.toLowerCase());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);//Bad form, but it complies with the interface
            return;
        }
        for(int i = 0; i < wordLength; i++){
            goodWord.append('*');
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
        String matchWord = "";

        for(Map.Entry<String,Set<String>> partition : partitions.entrySet()){
            if(partition.getValue().size() > maxCount){
                matchWord = partition.getKey();
                newDictionary = partition.getValue();
                maxCount = newDictionary.size();
            }
        }

        if (matchWord.contains("+")){
            System.out.println("That's A Match!");
            updateGoodWord(matchWord,guess);
        }else {
            System.out.println("No Match!");
        }

        dictionary = newDictionary;
        return newDictionary;
    }


    public String getHintString(){
        return goodWord.toString();
    }

    public boolean getHasWon(){
        return !goodWord.toString().contains("*");
    }

    /*
     * Data Members
     */
    private Set<String> dictionary;
    private Set<Character> guessList;
    private StringBuilder goodWord;
    private static String validLetters = "abcdefghijklmnopqrstuvwxyz";


    /*
     * Helper Functions
     */

    private static String getMatchPattern(String word, char guess){
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

    private void updateGoodWord(String matchWord, Character guess){
        for (int i = 0; i < matchWord.length(); i++) {
            if (matchWord.charAt(i) == '+'){
                goodWord.replace(i,i+1,guess.toString());
            }
        }
    }

    private void cheat(){
        Scanner in = new Scanner(System.in);
        System.out.println("There are " + dictionary.size() + " words left in the dictionary. Print them all? [y/n]");
        if(in.next().toLowerCase().charAt(0) == 'y') {
            System.out.println(Arrays.deepToString(dictionary.toArray()));
        }
    }
}
