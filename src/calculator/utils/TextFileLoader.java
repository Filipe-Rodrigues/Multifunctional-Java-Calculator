/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import calculator.turing.TuringMachine;

/**
 *
 * @author filipe
 */
public class TextFileLoader {
    
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String TURING_MACHINES_ROOT_DIRECTORY = "/Machines/Turing/";

    private TextFileLoader() {}
    
    private static String getInputAlphabet(String line) {
        return line.substring(line.indexOf(':') + 1).trim();
    }

    private static char getBlankSymbol(String line) {
        return line.substring(line.indexOf(':') + 1).trim().charAt(0);
    }

    private static int getNumberOfStates(String line) {
        return Integer.parseInt(line.substring(line.indexOf(':') + 1).trim());
    }

    private static int getInitialState(String line) {
        String state = line.substring(line.indexOf(':') + 1).trim();
        return Integer.parseInt(state.substring(1));
    }

    private static List<Integer> getFinalStates(String line) {
        List<Integer> finalStates = new ArrayList<>();
        line = line.substring(line.indexOf(':') + 1).trim();
        String[] states = line.split(",");
        for (String state : states) {
            try {
                finalStates.add(Integer.parseInt(state.trim().substring(1)));
            }catch (StringIndexOutOfBoundsException e) {
                System.out.println("WARNING: Undefined final states - your Turing machine won't accept any strings!");
            }
        }
        return finalStates;
    }

    public static TuringMachine loadTuringMachineFromFile(String fileName) {
        String line;
        String filePath = PROJECT_PATH + TURING_MACHINES_ROOT_DIRECTORY + fileName;
        File file = new File(filePath);
        try {
            FileReader fileReader = new FileReader(file);
            String inputAlphabet;
            char blank;
            int numberOfStates;
            int initialState;
            List<Integer> finalStates;
            List<String> transitions;
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                inputAlphabet = getInputAlphabet(bufferedReader.readLine());
                blank = getBlankSymbol(bufferedReader.readLine());
                numberOfStates = getNumberOfStates(bufferedReader.readLine());
                initialState = getInitialState(bufferedReader.readLine());
                finalStates = getFinalStates(bufferedReader.readLine());
                transitions = new ArrayList<>();
                bufferedReader.readLine();
                while ((line = bufferedReader.readLine()) != null) {
                    transitions.add(line);
                }
            }
            
            return new TuringMachine(inputAlphabet, blank, numberOfStates, initialState, finalStates, transitions);
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                filePath + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + filePath + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return null;
    }
}
