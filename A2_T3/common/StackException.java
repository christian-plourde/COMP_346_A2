package common;

public class StackException extends Exception
{

    public StackException()
    {
        super("Stack Exception has occurred.");
    }

    public StackException(String message)
    {
        super(message);
    }
}
