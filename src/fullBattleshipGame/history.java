package fullBattleshipGame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class history {
	
	static String defaultFileName = "DhruvalsBShipGameHistory.log";
	static FileOutputStream fileOutPutStream;
	static PrintWriter printWriter;
	static String newLine = System.getProperty("line.separator");
	
	// Get the time that the error occurred
	static String getTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date errorTime = new Date();
		return dateFormat.format(errorTime);
		}
	
	// If no file exists, create a new file.
	public history(){
		try {
			File historyLog = new File(defaultFileName); 
			if (!historyLog.exists())
				historyLog.createNewFile();
			}
		// If permission has been denied print: 
		catch (Exception e) {	System.out.println("Error at: "+e.getMessage());	}
		}

	// Print error message to file
	static void addConnection(String msg){
		try {
			fileOutPutStream = new FileOutputStream(defaultFileName, true);
			printWriter = new PrintWriter(fileOutPutStream);
			// Print the time that the error occurred
			printWriter.print(logFile.getTime()+", Connected to: "+msg+newLine); }
		// If file, not found print: 
		catch (Exception e){	System.out.println("Error at: "+e.getStackTrace());	}
		finally {	printWriter.close();	}
		}
	}