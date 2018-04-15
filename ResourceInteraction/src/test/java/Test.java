
import ng.com.idempotent.resourceinteraction.processor.InteractionsProcessor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aardvocate
 */
public class Test {
    public static void main(String args[]) {
        String line = "be a super-duper-user ";
        System.err.println(InteractionsProcessor.occurences(line));
    }
}
