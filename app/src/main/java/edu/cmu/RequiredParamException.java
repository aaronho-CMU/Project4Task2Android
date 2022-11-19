/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: Nov 18, 2022
 *
 * This class extends Exception class to raise custom exception
 */


package edu.cmu;

//https://stackoverflow.com/questions/1754315/how-to-create-custom-exceptions-in-java
public class RequiredParamException extends Exception {
    public RequiredParamException(String message)
    {
        super(message);
    }
}
