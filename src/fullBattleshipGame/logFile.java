package fullBattleshipGame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class logFile {
	private static String defaultFileName = "DhruvalsBShipGameError.log";
	private static FileOutputStream fileOutPutStream;
	private static PrintWriter printWriter;
	private static String newLine = System.getProperty("line.separator");
	
	// Get the time that the error ocurred
	static String getTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date errorTime = new Date();
		return dateFormat.format(errorTime);
		}
	
	// If no file exists, create a new file.
	public logFile(){
		try {
			File errorLog = new File(defaultFileName); 
			if (!errorLog.exists())
				errorLog.createNewFile();
			}
		// If permission has been denied print: 
		catch (Exception e) {	Gui.println("Error at: "+e.getMessage());	}
		}

	// Print error message to file
	static void logMessage(String msg){
		try {
			fileOutPutStream = new FileOutputStream(defaultFileName, true);
			printWriter = new PrintWriter(fileOutPutStream);
			// Print the time that the error occurred
			printWriter.print(logFile.getTime()+": "+msg+newLine); }
		// If file, not found print: 
		catch (Exception e){	Gui.println("Error at: "+e.getStackTrace());	}
		finally {	printWriter.close();	}
		}
	
}