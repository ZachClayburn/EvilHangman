package hangman;

import javax.xml.stream.events.Characters;
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
            if (wordLength < 2){
                badInputs = true;
                System.out.println("<word length> must be greater than two!");
            }
            if (guessCount < 1) {
                System.out.println("<number of guesses> must be greater than one!");
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
            System.out.println("Guess " + (round+1) + "/" + guessCount);
            System.out.println(game.getHintString());
            System.out.println("Make a Guess:");
            String input = in.next().toLowerCase();
            if (input.equals("zach")){
                game.cheat();
                round--;
                continue;
            }
            if(input.length() != 1 || !validLetters.contains(input)){
                System.out.println("Please enter a single letter!");
                round--;
                continue;
            }
            char guess = input.charAt(0);
            try {
                game.makeGuess(guess);
            } catch(GuessAlreadyMadeException e){
                System.out.println("You've already guessed that letter, try again!");
                round--;
            }
            if(game.getHasWon()){
                System.out.println("YOU WON!");
                System.out.println("My Word was " + game.getHintString());
                return;
            }
            System.out.println("\n\n");
        }

        System.out.println("YOU LOSE!");
        System.out.println("My Word was " + game.getWord());
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
        if (dictionary.isEmpty()){
            System.out.println("No words of that length exist! Starting game with words of length 4");
            this.startGame(dictionaryFile,4);
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
            if (partition.getValue().size() == maxCount){
                if (!matchWord.contains("+")){
                    continue;
                } else if (!partition.getKey().contains("+")){
                    matchWord = partition.getKey();
                    newDictionary = partition.getValue();
                    maxCount = newDictionary.size();
                } else if (countHits(matchWord) != countHits(partition.getKey())){
                    if (countHits(matchWord) < countHits(partition.getKey())){
                        matchWord = partition.getKey();
                        newDictionary = partition.getValue();
                        maxCount = newDictionary.size();
                    }
                } else {
                    if (hasRightmostHit(matchWord,partition.getKey())){
                        matchWord = partition.getKey();
                        newDictionary = partition.getValue();
                        maxCount = newDictionary.size();
                    }
                }

            }else if(partition.getValue().size() > maxCount){
                matchWord = partition.getKey();
                newDictionary = partition.getValue();
                maxCount = newDictionary.size();
            }
        }

        if (matchWord.contains("+")){
            System.out.println("That's A Match!");
            updateGoodWord(matchWord,guess);
        }else {
            System.out.println("Sorry, there are no " + guess + "'s");
        }

        dictionary = newDictionary;
        return newDictionary;
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
    public boolean getHasWon(){
        return !goodWord.toString().contains("*");
    }

    public String getHintString(){
        return goodWord.toString();
    }

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

    private static int countHits(String matchString){
        int sum = 0;

        for (int i = 0; i < matchString.length(); i++) {
            if (matchString.charAt(i) == '+'){
                sum++;
            }
        }

        return sum;
    }

    private static boolean hasRightmostHit(String champ, String contender){
        int champRightmost = champ.lastIndexOf("+");
        int contenderRightmost = contender.lastIndexOf("+");

        if (champRightmost == contenderRightmost){
            return hasRightmostHit(champ.substring(0,champRightmost),contender.substring(0,contenderRightmost));
        }
        return contenderRightmost > champRightmost;


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

    private String getWord(){
        return dictionary.iterator().next();
    }
}
