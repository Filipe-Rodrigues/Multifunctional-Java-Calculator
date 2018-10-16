/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator.turing;

/**
 *
 * @author filipe
 */
public class TuringMachineResult {
    private String processingResult;
    private TuringProcessingStatus haltingResult;
    private String reason;
    
    public TuringMachineResult(String processingResult, TuringProcessingStatus haltingResult, String reason) {
        this.haltingResult = haltingResult;
        this.processingResult = processingResult;
        this.reason = reason;
    }
    
    public String getProcessingStatus() {
        switch (haltingResult) {
            case ACCEPT :
                return "ACEITADA";
            case REJECT :
                return "REJEITADA";
            case LOOP :
                return "LOOP";
            default :
                return "INVÁLIDO";
        } 
    }

    public String getProcessingResult() {
        return processingResult;
    }

    public String getReason() {
        return reason;
    }
    
}
