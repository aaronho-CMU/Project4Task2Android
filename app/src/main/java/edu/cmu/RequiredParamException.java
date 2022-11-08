package edu.cmu;

//https://stackoverflow.com/questions/1754315/how-to-create-custom-exceptions-in-java
public class RequiredParamException extends Exception {
    public RequiredParamException(String message)
    {
        super(message);
    }
}
