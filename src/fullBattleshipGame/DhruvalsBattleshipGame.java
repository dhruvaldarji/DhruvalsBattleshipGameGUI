package fullBattleshipGame;

//////////////////////////////////////////////////
//	Dhruval Darji     1302J     Prof. Vandeven	//
//////////////////////////////////////////////////


public class DhruvalsBattleshipGame {
	public static Gui g;
	public static logFile lg = new logFile();
	public static void main (String[]args) throws Throwable{
		try {
			g = new Gui();
			} catch (Exception e) {
				lg.logMessage("Error at: "+e.getLocalizedMessage());
				 System.err.println("Error: "+e.fillInStackTrace());	} 
		finally{
			Gui.println("GoodBye");
		}
		}
	}
