package fullBattleshipGame;

//////////////////////////////////////////////////
//	Dhruval Darji     1302J     Prof. Vandeven	//
//////////////////////////////////////////////////


public class DhruvalsBattleshipGame {
	static Gui g;
	public static void main (String[]args) throws Throwable{
		try {
			g = new Gui();
			} catch (Exception e) {
				logFile.logMessage("There was a problem, game has exited. Error at: "+e.getMessage());
				 System.out.println("Error: "+e.getMessage());	} 
		}
	}
