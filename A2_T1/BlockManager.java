// Import (aka include) some stuff.
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import common.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Class BlockManager
 * Implements character block "manager" and does twists with threads.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca;
 * Inspired by previous code by Prof. D. Probst
 *
 * $Revision: 1.5 $
 * $Last Revision Date: 2019/02/02 $

 */
public class BlockManager
{
	/**
	 * The stack itself
	 */
	private static BlockStack soStack = new BlockStack();

	/**
	 * Number of threads dumping stack
	 */
	private static final int NUM_PROBERS = 4;

	/**
	 * Number of steps they take
	 */
	private static int siThreadSteps = 5;

	/**
	 * For atomicity
	 */
	//private static Semaphore mutex = new Semaphore(...);

	/*
	 * For synchronization
	 */

	/**
	 * s1 is to make sure phase I for all is done before any phase II begins
	 */
	//private static Semaphore s1 = new Semaphore(...);

	/**
	 * s2 is for use in conjunction with Thread.turnTestAndSet() for phase II proceed
	 * in the thread creation order
	 */
	//private static Semaphore s2 = new Semaphore(...);


	// The main()
	public static void main(String[] argv)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));

			// Some initial stats...
			System.out.println("Main thread starts executing.");
			writer.write("Main thread starts executing.");
			writer.newLine();
			System.out.println("Initial value of top = " + soStack.getITop() + ".");
			writer.write("Initial value of top = " + soStack.getITop() + ".");
			writer.newLine();
			System.out.println("Initial value of stack top = " + soStack.pick() + ".");
			writer.write("Initial value of stack top = " + soStack.pick() + ".");
			writer.newLine();
			System.out.println("Main thread will now fork several threads.");
			writer.write("Main thread will now fork several threads.");
			writer.newLine();

			/*
			 * The birth of threads
			 */
			AcquireBlock ab1 = new AcquireBlock();
			AcquireBlock ab2 = new AcquireBlock();
			AcquireBlock ab3 = new AcquireBlock();

			System.out.println("main(): Three AcquireBlock threads have been created.");
			writer.write("main(): Three AcquireBlock threads have been created.");
			writer.newLine();

			ReleaseBlock rb1 = new ReleaseBlock();
			ReleaseBlock rb2 = new ReleaseBlock();
			ReleaseBlock rb3 = new ReleaseBlock();

			System.out.println("main(): Three ReleaseBlock threads have been created.");
			writer.write("main(): Three ReleaseBlock threads have been created.");
			writer.newLine();

			// Create an array object first
			CharStackProber	aStackProbers[] = new CharStackProber[NUM_PROBERS];

			// Then the CharStackProber objects
			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i] = new CharStackProber();

			System.out.println("main(): CharStackProber threads have been created: " + NUM_PROBERS);
			writer.write("main(): CharStackProber threads have been created: " + NUM_PROBERS);
			writer.newLine();

			/*
			 * Twist 'em all
			 */
			ab1.start();
			aStackProbers[0].start();
			rb1.start();
			aStackProbers[1].start();
			ab2.start();
			aStackProbers[2].start();
			rb2.start();
			ab3.start();
			aStackProbers[3].start();
			rb3.start();

			System.out.println("main(): All the threads are ready.");
			writer.write("main(): All the threads are ready.");
			writer.newLine();

			/*
			 * Wait by here for all forked threads to die
			 */
			ab1.join();
			ab2.join();
			ab3.join();

			rb1.join();
			rb2.join();
			rb3.join();

			for(int i = 0; i < NUM_PROBERS; i++)
				aStackProbers[i].join();

			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + soStack.getITop() + ".");
			System.out.println("Final value of stack top = " + soStack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + soStack.getAt(soStack.getITop() - 1) + ".");
			System.out.println("Stack access count = " + soStack.getAccessCounter());

			writer.write("System terminates normally.");
			writer.newLine();
			writer.write("Final value of top = " + soStack.getITop() + ".");
			writer.newLine();
			writer.write("Final value of stack top = " + soStack.pick() + ".");
			writer.newLine();
			writer.write("Final value of stack top-1 = " + soStack.getAt(soStack.getITop() - 1) + ".");
			writer.newLine();
			writer.write("Stack access count = " + soStack.getAccessCounter());
			writer.newLine();
			writer.flush();
			writer.close();

			System.exit(0);
		}
		catch(IOException ioe)
		{
			System.err.println("IOException occurred.");
		}
		catch(InterruptedException e)
		{
			System.err.println("Caught InterruptedException (internal error): " + e.getMessage());
			e.printStackTrace(System.err);
		}
		catch(Exception e)
		{
			reportException(e);
		}
		finally
		{
			System.exit(1);
		}
	} // main()


	/**
	 * Inner AcquireBlock thread class.
	 */
	static class AcquireBlock extends BaseThread
	{
		/**
		 * A copy of a block returned by pop().
                 * @see BlockStack#pop()
		 */
		private char cCopy;

		public void run()
		{
			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
				writer.write("AcquireBlock thread [TID=" + this.iTID + "] starts executing.");
				writer.newLine();
				writer.flush();
				writer.close();
			}

			catch(IOException e)
			{
				System.err.println("IOException occurred.");
			}
			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();


			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
				writer.write("AcquireBlock thread [TID=" + this.iTID + "] requests Ms block.");
				writer.newLine();
				System.out.println("AcquireBlock thread [TID=" + this.iTID + "] requests Ms block.");

				this.cCopy = soStack.pop();

				writer.write("AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
						" from position " + (soStack.getITop() + 1) + ".");
				writer.newLine();
				System.out.println
				(
					"AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
					" from position " + (soStack.getITop() + 1) + "."
				);


				writer.write("Acq[TID=" + this.iTID + "]: Current value of top = " +
						soStack.getITop() + ".");
				writer.newLine();
				System.out.println
				(
					"Acq[TID=" + this.iTID + "]: Current value of top = " +
					soStack.getITop() + "."
				);

				writer.write("Acq[TID=" + this.iTID + "]: Current value of stack top = " +
						soStack.pick() + ".");
				writer.newLine();
				writer.flush();
				writer.close();
				System.out.println
				(
					"Acq[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + "."
				);
			}
			catch(IOException ex)
			{
				System.err.println("IOException occurred.");
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}

			phase2();

			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
				writer.write("AcquireBlock thread [TID=" + this.iTID + "] terminates.");
				writer.newLine();
				writer.flush();
				writer.close();
			}

			catch(IOException e)
			{
				System.err.println("IOException occurred.");
			}
			System.out.println("AcquireBlock thread [TID=" + this.iTID + "] terminates.");
		}
	} // class AcquireBlock


	/**
	 * Inner class ReleaseBlock.
	 */
	static class ReleaseBlock extends BaseThread
	{
		/**
		 * Block to be returned. Default is 'a' if the stack is empty.
		 */
		private char cBlock = 'a';

		public void run()
		{
			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
				writer.write("ReleaseBlock thread [TID=" + this.iTID + "] starts executing.");
				writer.newLine();
				writer.flush();
				writer.close();

			}

			catch(IOException e)
			{
				System.err.println("IOException occurred.");
			}
			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] starts executing.");


			phase1();


			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
				writer.write("ReleaseBlock thread [TID=" + this.iTID + "] returns Ms block " + this.cBlock +
						" to position " + (soStack.getITop() + 1) + ".");
				writer.newLine();
				if(soStack.isEmpty() == false)
					this.cBlock = (char)(soStack.pick() + 1);


				System.out.println
				(
					"ReleaseBlock thread [TID=" + this.iTID + "] returns Ms block " + this.cBlock +
					" to position " + (soStack.getITop() + 1) + "."
				);

				soStack.push(this.cBlock);

				writer.write("Rel[TID=" + this.iTID + "]: Current value of top = " +
						soStack.getITop() + ".");
				writer.newLine();
				writer.write("Rel[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + ".");
				writer.newLine();
				writer.flush();
				writer.close();
				System.out.println
				(
					"Rel[TID=" + this.iTID + "]: Current value of top = " +
					soStack.getITop() + "."
				);

				System.out.println
				(
					"Rel[TID=" + this.iTID + "]: Current value of stack top = " +
					soStack.pick() + "."
				);
			}
			catch(IOException ex)
			{
				System.err.println("IOException occurred.");
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}


			phase2();

			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
				writer.write("ReleaseBlock thread [TID=" + this.iTID + "] terminates.");
				writer.flush();
				writer.close();
			}

			catch(IOException e)
			{
				System.err.println("IOException occurred.");
			}
			System.out.println("ReleaseBlock thread [TID=" + this.iTID + "] terminates.");
		}
	} // class ReleaseBlock


	/**
	 * Inner class CharStackProber to dump stack contents.
	 */
	static class CharStackProber extends BaseThread
	{
		public void run()
		{
			phase1();


			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt",true));
				for(int i = 0; i < siThreadSteps; i++)
				{
					writer.write("Stack Prober [TID=" + this.iTID + "]: Stack state: ");
					writer.newLine();
					System.out.print("Stack Prober [TID=" + this.iTID + "]: Stack state: ");

					// [s] - means ordinay slot of a stack
					// (s) - current top of the stack
					for(int s = 0; s < soStack.getISize(); s++)
					{
						writer.write((s == BlockManager.soStack.getITop() ? "(" : "[") +
								BlockManager.soStack.getAt(s) +
								(s == BlockManager.soStack.getITop() ? ")" : "]"));
						writer.newLine();
						System.out.print
								(
										(s == BlockManager.soStack.getITop() ? "(" : "[") +
												BlockManager.soStack.getAt(s) +
												(s == BlockManager.soStack.getITop() ? ")" : "]")
								);
					}

					writer.write(".");
					writer.newLine();
					System.out.println(".");

				}
			}
			catch(IOException ex)
			{
				System.err.println("IOException occurred.");
			}
			catch(Exception e)
			{
				reportException(e);
				System.exit(1);
			}


			phase2();

		}
	} // class CharStackProber


	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	private static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
} // class BlockManager

// EOF
