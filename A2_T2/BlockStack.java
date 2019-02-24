import java.io.*;

/**
 * Class BlockStack
 * Implements character block stack and operations upon it.
 *
 * $Revision: 1.4 $
 * $Last Revision Date: 2019/02/02 $
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by an earlier code by Prof. D. Probst

 */
class BlockStack
{
	/**
	 * The number of times the stack was accessed
	 */
	private static int accessCounter = 0;

	/**
	 * # of letters in the English alphabet + 2
	 */
	public static final int MAX_SIZE = 28;

	/**
	 * Default stack size
	 */
	public static final int DEFAULT_SIZE = 6;

	/**
	 * Current size of the stack
	 */
	public int iSize = DEFAULT_SIZE;

	/**
	 * Current top of the stack
	 */
	public int iTop  = 3;

	/**
	 * stack[0:5] with four defined values
	 */
	public char acStack[] = new char[] {'a', 'b', 'c', 'd', '$', '$'};

	/**
	 * Default constructor
	 */
	public BlockStack()
	{
	}

	/**
	 * Supplied size
	 */
	public BlockStack(final int piSize)
	{

		if(piSize != DEFAULT_SIZE)
		{
			this.acStack = new char[piSize];

			// Fill in with letters of the alphabet and keep
			// 2 free blocks
			for(int i = 0; i < piSize - 2; i++)
				this.acStack[i] = (char)('a' + i);

			this.acStack[piSize - 2] = this.acStack[piSize - 1] = '$';

			this.iTop = piSize - 3;
                        this.iSize = piSize;
		}
	}

	/**
	 * Picks a value from the top without modifying the stack
	 * @return top element of the stack, char
	 */
	public char pick()
	{
		accessCounter++; //the stack was accessed so we need to track that
		return this.acStack[this.iTop];
	}

	/**
	 * Returns arbitrary value from the stack array
	 * @return the element, char
	 */
	public char getAt(final int piPosition)
	{
		accessCounter++;
		return this.acStack[piPosition];
	}

	/**
	 * Standard push operation
	 */
	public void push(final char pcBlock)
	{
		accessCounter++;
		this.acStack[++this.iTop] = pcBlock;
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
			writer.write("Value was successfully pushed onto the stack.");
			writer.newLine();
			writer.flush();
			writer.close();
		}

		catch(IOException e)
		{
			System.err.println("IOException occurred.");
		}
		System.out.println("Value was successfully pushed onto the stack.");

	}

	/**
	 * Standard pop operation
	 * @return ex-top element of the stack, char
	 */
	public char pop()
	{
		accessCounter++;
		char cBlock = this.acStack[this.iTop];
		this.acStack[this.iTop--] = '*'; // Leave prev. value undefined

		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
			writer.write("Value was successfully popped off the stack.");
			writer.newLine();
			writer.flush();
			writer.close();
		}

		catch(IOException e)
		{
			System.err.println("IOException occurred.");
		}

		System.out.println("Value was successfully popped off the stack.");
		return cBlock;
	}

	/**
	 * Method that returns the top of the stack
	 * @return The top element of the stack
	 */
	public int getITop()
	{
		return iTop;
	}

	/**
	 * Method to get the size of the stack.
	 * @return The size of the stack
	 */
	public int getISize()
	{
		return iSize;
	}

	/**
	 * Method to get the number of times that the stack was accessed
	 * @return The number of times the stack was accessed
	 */
	public int getAccessCounter()
	{
		return accessCounter;
	}

	/**
	 * A method to determine if the stack is empty or not
	 * @return Boolean indicating if stack is empty
	 */
	public boolean isEmpty()
	{
		return(this.iTop == -1);
	}
}

// EOF
