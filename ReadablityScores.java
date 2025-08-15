package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadablityScores {
	private static final Map<Integer, Integer> ageOfScore = new HashMap<>();
    static {
    	Integer i = 1;
		for (; i < 14; i++) {
			Integer value = i+5;
			ageOfScore.put(i, value);
		}
		Integer value = i+7;
		ageOfScore.put(i, value);
    }

	 private Scanner scanner;
	 private int allSentences;
	 private int allWords;
	 private int allSyllables;
	 private int allPolysyllables;
	 private int allChars;
	 private double L;
	 private double S;
	 private int ageOfARI;
	 private int ageOfFK;
	 private int ageOfSMOG;
	 private int ageOfCL;
	 
	 public ReadablityScores(String filePath) {
		 L = 0.0;
		 S = 0.0;
		 ageOfARI = 0;
		 ageOfFK = 0;
		 ageOfSMOG = 0;
		 ageOfCL = 0;
		 
		 File file = new File(filePath);
		 
		 List<String> sentences = new ArrayList<>(); 
		 allSentences = 0;
         allWords = 0;
         allSyllables = 0;
         allPolysyllables = 0;
         allChars = 0;
        
		 try {   
			 scanner = new Scanner(file);
             while (scanner.hasNextLine()) {
             	String[] sentencesArr = (scanner.nextLine()).split("(?<=[.?!])\\s+");
             	sentences.addAll(Arrays.asList(sentencesArr));
             	allSentences += sentencesArr.length;
             }
             
             for (int i = 0; i < allSentences; i++) {	                	
                 String[] words = sentences.get(i).split("\\s+");	                    
                 int wordCount = words.length;
                 allWords += wordCount;
                 
                 for (String word : words) {
                	 allChars += word.length();	
                	 int wordSyllables = getNumberOfSyllables(word);
                	 allSyllables += wordSyllables;
                	 if (wordSyllables > 2) {
                		 allPolysyllables++;
                	 }                	                    	
                 }                                
             }
               
             printAllSimpleData(allSentences, allWords, allSyllables, allPolysyllables, allChars);
             
             S = (double)allSentences / (double)allWords * 100.0;
             L = (double)allChars / (double)allWords * 100.0;
             
         } catch (FileNotFoundException e) {
             System.out.println("No file found: " + filePath);
         }
    }
	 
    private void printAllSimpleData(Integer allSentences, Integer allWords, Integer allSyllables, Integer allPolysyllables, Integer allChars) {
    	System.out.println("Words: " + allWords); 
    	System.out.println("Sentences: " + allSentences); 
    	System.out.println("Characters: " + allChars); 
    	System.out.println("Syllables: " + allSyllables); 
    	System.out.println("Polysyllables: " + allPolysyllables);     	
    } 
    
    private void getARI() {
    	double realScore = 4.71 * allChars / allWords + 0.5 * allWords / allSentences - 21.43; 
        int score = (int) Math.ceil(realScore);
        ageOfARI = ageOfScore.get(score);
    	System.out.printf("Automated Readability Index: %.2f", realScore); 
    	System.out.println(" (about "+ ageOfARI + "-year-olds).");
	}
	    
	private void getFK() {		
		double realScore = 0.39 * allWords / allSentences + 11.8 * allSyllables / allWords - 15.59;
		int score = (int) Math.ceil(realScore); 	
		ageOfFK = ageOfScore.get(score);
		System.out.printf("Flesch–Kincaid readability tests: %.2f", realScore);
		System.out.println(" (about "+ ageOfFK + "-year-olds).");
		 
	}
	private void getSMOG() {
		double realScore = 1.043 * Math.sqrt(allPolysyllables * 30 / allSentences) + 3.1291;
		int score = (int) Math.ceil(realScore);
		ageOfSMOG = ageOfScore.get(score);
		System.out.printf("Simple Measure of Gobbledygook: %.2f", realScore); 
		System.out.println(" (about "+ ageOfSMOG + "-year-olds).");
	}
		
	private void getCL() {
		double realScore = 0.0588 * L - 0.296 * S - 15.8;
		int score = 0;
		if (realScore < 14.0) {
			score = (int) Math.ceil(realScore);
		}
		else {
			score = 14;
		}				
		ageOfCL = ageOfScore.get(score);
		System.out.printf("Coleman–Liau index: %.2f", realScore);
		System.out.println(" (about "+ ageOfCL + "-year-olds).");		
	}
	
	private void getAVGAge() {
		double avg = (ageOfARI + ageOfFK + ageOfSMOG + ageOfCL) / 4;
		System.out.printf("This text should be understood in average by %.2f", avg);
		System.out.println("-year-olds.");
	}
		
	private int getNumberOfSyllables(String word) {		
		Pattern pattern = Pattern.compile("[.,!?:;)\"]");
		Matcher matcher = pattern.matcher(word);
		if (matcher.find()) {
			word = word.substring(0, matcher.start());		
		} 
		HashSet<Character> vowels = new HashSet<>();
		vowels.add('a');
		vowels.add('e');
		vowels.add('i');
		vowels.add('o');
		vowels.add('u');
		vowels.add('y');
		vowels.add('A');
		vowels.add('E');
		vowels.add('I');
		vowels.add('O');
		vowels.add('U');
		vowels.add('Y');
		
		int number = 0;
		int j = 0;
		for (int i = 0; i < word.length(); i++) {				
			if (vowels.contains(word.charAt(i))) {					
				if (i == 0 || (i > 0 && i > j)) {
					number++;						
					j = i+1;						
				}
				if (i == j) {
					j = i+1;
				}
				else if (i == word.length()-1 && (word.charAt(word.length()-1) == 'e' || word.charAt(word.length()-1) == 'E')) {
						number--;
				}					
			}					
		}
		if (number == 0) {
			number = 1;
		}		
		return number;
	}
		
	public static void main(String[] args) {
		if (args.length > 0) {
            String filePath = args[0];            
            ReadablityScores text = new ReadablityScores(filePath);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");         
            text.scanner = new Scanner(System.in);
            String choice = text.scanner.next();
            System.out.println();
            
            switch (choice) {
            case "ARI":
            	text.getARI();
            	break;
            case "FK":
            	text.getFK();
            	break;
            case "SMOG":
            	text.getSMOG();
            	break;
            case "CL":
            	text.getCL();
            	break;
            case "all":
            	text.getARI();
            	text.getFK();
            	text.getSMOG();
            	text.getCL();
            	text.getAVGAge();
            	break;
        	default:
        		System.out.println("Wrong choice");
        		break;
            }           
            text.scanner.close();
		}	
	}
}
