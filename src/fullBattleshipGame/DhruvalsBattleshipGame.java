package fullBattleshipGame;

//////////////////////////////////////////////////
//	Dhruval Darji     1302J     Prof. Vandeven	//
//////////////////////////////////////////////////


public class DhruvalsBattleshipGame {
	public static Gui g;
	public static void main (String[]args) throws Throwable{
		try {
//			g = new Gui();
			ComvCom c = new ComvCom();
//			c.run("10.220.3.87", 13001);
			c.run(null, 13000);
			} catch (Exception e) {
				logFile.logMessage("Error at: "+e.getMessage());
				 System.err.println("Error: "+e.getMessage());	} 
		finally{
			System.out.println("GoodBye");
			System.exit(0);
		}
		}
	}
