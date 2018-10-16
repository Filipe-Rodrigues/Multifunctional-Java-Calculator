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
public enum TuringProcessingStatus {
    OK,
    LEFT_BOUND_BROKEN,
    RIGHT_BOUND_BROKEN,
    NO_TRANSITION_FOUND,
    ACCEPT,
    REJECT,
    LOOP,
    MACHINE_ERROR
}
