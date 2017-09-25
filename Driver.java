import javax.swing.*;
import java.io.*;

/**
  * Driver class for DirectoryLister assignment.
  * This class simply instantiates the classes needed to run the program, and
  * executes the program.
  */
public class Driver
{
	
	public static void main(String[] args)
	{
		GUI gui = new GUI();
		DirectoryLister dl = new DirectoryLister(gui);
		gui.registerModel(dl);
	}
	
}