/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator.turing;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import static calculator.turing.TuringProcessingStatus.*;

/**
 *
 * @author filipe
 */
public class TuringMachine {

    private class TuringTransition {
        private static final int L = -1;
        private static final int R = 1;
        private int sourceState;
        private char readingSymbol;
        private int targetState;
        private char writingSymbol;
        private int tapeHeadDirection;

        public TuringTransition(String transition) {
            try{
                processEncodedTransition(transition);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null, "Tem algo de errado em sua transição, confira:\n" + transition);
                sourceState = -1;
            }
        }
        
        public boolean isOK() {
            return sourceState >= 0;
        }

        private void processEncodedTransition(String transition) {
            // Isolar o estado de origem
            int cursorStart = transition.indexOf('q', 0) + 1;
            int cursorEnd = transition.indexOf(',', cursorStart);
            sourceState = Integer.parseInt(transition.substring(cursorStart, cursorEnd).trim());
            // Isolar o símbolo a ser lido da fita
            cursorStart = transition.indexOf(',', cursorEnd) + 1;
            cursorEnd = transition.indexOf(')', cursorStart);
            readingSymbol = transition.substring(cursorStart, cursorEnd).trim().charAt(0);
            // Isolar o estado de destino
            cursorStart = transition.indexOf('q', cursorEnd) + 1;
            cursorEnd = transition.indexOf(',', cursorStart);
            targetState = Integer.parseInt(transition.substring(cursorStart, cursorEnd).trim());
            // Isolar o símbolo a ser escrito na fita
            cursorStart = transition.indexOf(',', cursorEnd) + 1;
            cursorEnd = transition.indexOf(',', cursorStart);
            writingSymbol = transition.substring(cursorStart, cursorEnd).trim().charAt(0);
            // Isolar a direção do movimento da cabeça da fita
            cursorStart = transition.indexOf(',', cursorEnd) + 1;
            cursorEnd = transition.indexOf(')', cursorStart);
            char direction = transition.substring(cursorStart, cursorEnd).trim().charAt(0);
            tapeHeadDirection = (direction == 'R') ? (R) : (L);
        }
        
    }
   
    private class SimplifiedTuringTransition {
        private char readingSymbol;
        private char writingSymbol;
        private int tapeHeadDirection;

        public SimplifiedTuringTransition(char readingSymbol, char writingSymbol, int tapeHeadDirection) {
            this.readingSymbol = readingSymbol;
            this.writingSymbol = writingSymbol;
            this.tapeHeadDirection = tapeHeadDirection;
        }
        
        public SimplifiedTuringTransition(TuringTransition transition) {
            readingSymbol = transition.readingSymbol;
            writingSymbol = transition.writingSymbol;
            tapeHeadDirection = transition.tapeHeadDirection;
        }
    }

    private static final int TAPE_SIZE = 256 + 2;
    private static final int TIMEOUT_IN_SECONDS = 10;
    
    private String inputAlphabet;
    private char[] tape;
    private char blank;
    private int numberOfStates;
    private int initialState;
    private List<Integer> finalStates;
    private List<SimplifiedTuringTransition>[][] transitions;
    private boolean operable;
    private int tapeStringSize;
    private int currentState;
    private int currentTapePosition;

    public TuringMachine(String inputAlphabet, char blank,
            int numberOfStates, int initialState, List<Integer> finalStates,
            List<String> encodedTransitions) {
        this.inputAlphabet = inputAlphabet;
        this.blank = blank;
        this.numberOfStates = numberOfStates;
        this.initialState = initialState;
        this.finalStates = new ArrayList<>();
        for (int i = 0; i < finalStates.size(); i++) {
            this.finalStates.add(finalStates.get(i));
        }
        operable = initializeTransitionsMatrix(encodedTransitions);
    }
    
    private boolean initializeTransitionsMatrix(List<String> encodedTransitions) {
        try{
            transitions = new ArrayList[numberOfStates][numberOfStates];
            for (int i = 0; i < numberOfStates; i++) {
                for (int j = 0; j < numberOfStates; j++) {
                    transitions[i][j] = new ArrayList<>();
                }
            }
            for (int i = 0; i < encodedTransitions.size(); i++) {
                TuringTransition completeTransition = new TuringTransition(encodedTransitions.get(i));
                if (completeTransition.isOK()) {
                    SimplifiedTuringTransition transition = new SimplifiedTuringTransition(completeTransition);
                    transitions[completeTransition.sourceState][completeTransition.targetState].add(transition);
                } else {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("Invalid state ID: You must use only ID values less than the number of states!");
        }
        return false;
    }

    private boolean hasSymbol(char symbol, String alphabet) {
        return alphabet.indexOf(symbol) >= 0;
    }
    
    private boolean hasInvalidSymbols(String input, String reference) {
        for (int i = 0; i < input.length(); i++) {
            if (!hasSymbol(input.charAt(i), inputAlphabet)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isInputOK(String input) {
        return (input.length() >= 255 || hasInvalidSymbols(input, inputAlphabet));
    }

    private void initializeTape(String input) {
        this.tape = new char[TAPE_SIZE];
        for (int i = 0; i < TAPE_SIZE; i++) {
            tape[i] = blank;
        }
        for (int i = 0; i < input.length(); i++) {
            tape[i+1] = input.charAt(i);
        }
        tapeStringSize = input.length() + 2;
    }
    
    private boolean endedAtFinalState() {
        for (int i = 0; i < finalStates.size(); i++) {
            if (currentState == finalStates.get(i)) {
                return true;
            }
        }
        return false;
    }
    
    private SimplifiedTuringTransition searchMatchingTransition(char readingSymbol) {
        int targetState = 0;
        while (targetState < numberOfStates) {
            List<SimplifiedTuringTransition> selectedCell = transitions[currentState][targetState];
            for (int i = 0; i < selectedCell.size(); i++) {
                if (selectedCell.get(i).readingSymbol == readingSymbol) {
                    currentState = targetState;
                    return selectedCell.get(i);
                }
            }
            targetState++;
        }
        return null;
    }
    
    private void updateTapeStringSize() {
        tapeStringSize = TAPE_SIZE - 1;
        while (tapeStringSize > 2 && tape[tapeStringSize - 2] == blank && tapeStringSize > currentTapePosition) {
            tapeStringSize--;
        }
    }
    
    private TuringProcessingStatus processCurrentSymbol() {
        char tapeSymbol = tape[currentTapePosition];
        SimplifiedTuringTransition transition = searchMatchingTransition(tapeSymbol);
        if (transition != null) {
            tape[currentTapePosition] = transition.writingSymbol;
            
            if (transition.writingSymbol != blank) {
                if (currentTapePosition < 1) {
                    return LEFT_BOUND_BROKEN;
                }
                if (currentTapePosition > (TAPE_SIZE - 2)) {
                    return RIGHT_BOUND_BROKEN;
                }
            }
            currentTapePosition += transition.tapeHeadDirection;
            updateTapeStringSize();
            
            if (currentTapePosition < 0) {
                currentTapePosition = 0;
                updateTapeStringSize();
                return LEFT_BOUND_BROKEN;
            } else if (currentTapePosition >= TAPE_SIZE) {
                currentTapePosition = 0;
                updateTapeStringSize();
                return RIGHT_BOUND_BROKEN;
            }
            
            return OK;
        }
        return NO_TRANSITION_FOUND;
    }
    
    public TuringMachineResult evaluate(String input) {
        if (operable) {
            if (isInputOK(input)) {
                initializeTape(input);
                currentState = initialState;
                TuringProcessingStatus status = OK;
                long initialTime = System.currentTimeMillis();
                do {
                    String tapeSequence = getTapeRecordedString();
                    tapeSequence = new StringBuilder(tapeSequence).insert(currentTapePosition, "q" + currentState).toString();
                    System.out.println(tapeSequence);
                    status = processCurrentSymbol();
                } while (status == OK && System.currentTimeMillis() - initialTime < TIMEOUT_IN_SECONDS * 1000);
                switch (status) {
                    case NO_TRANSITION_FOUND:
                        if (endedAtFinalState()) {
                            return new TuringMachineResult(getTapeRecordedString(), ACCEPT, "A palavra foi processada e aceitada.");
                        } else {
                            return new TuringMachineResult(getTapeRecordedString(), REJECT, "A palavra foi processada e rejeitada.");
                        }
                    case LEFT_BOUND_BROKEN:
                        return new TuringMachineResult(getTapeRecordedString(), REJECT, "A MT tentou escrever em uma região inválida.");
                    case RIGHT_BOUND_BROKEN:
                        return new TuringMachineResult(getTapeRecordedString(), LOOP, "A MT (teoricamente) entrou em loop.");
                    default:
                        return new TuringMachineResult(getTapeRecordedString(), LOOP, "A MT (teoricamente) entrou em loop.");
                }
            } else {
                return new TuringMachineResult(getTapeRecordedString(), REJECT, "A entrada não está em um formato válido.");
            }
        }
        return new TuringMachineResult(getTapeRecordedString(), MACHINE_ERROR, "A máquina não pode ser criada, pois apresenta algum defeito.");
    }
    
    private String getTapeRecordedString() {
        if (tape == null) {
            initializeTape("");
        }
        return new String(tape).substring(0, tapeStringSize);
    }
}
