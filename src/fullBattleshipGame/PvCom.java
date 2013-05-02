package fullBattleshipGame;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PvCom {
	
	// My game board
	private static board ocean;
	
	// My various ArrayLists that track ships and shots
	private static ArrayList<ship> fleet = new ArrayList<ship>(); // -----ArrayList
	private static ArrayList<ship> oppFleet = new ArrayList<ship>(); // -----ArrayList
	private ArrayList<Torpedo> oppShotList = new ArrayList<Torpedo>(); // -----ArrayList
	private static ArrayList<Torpedo> myShotList = new ArrayList<Torpedo>(); // -----ArrayList
	private int boardX=20, boardY=20, shipAmt = 20;
	// The following deal with shooting and focused shots against the opponent.
	private Torpedo coor = new Torpedo("M",0,0);
	private static boolean battling=false, shooting = false;
	
	public PvCom() throws Throwable{
		init();
	}
		
	// Initialize the game
	private void init() throws Throwable {
		// Make a new game board with 100x100 buttons
		ocean = new board(boardX,boardY);
		// Empty my shot list
		getMyShotList().clear();
		// Empty opponents shot list
		oppShotList.clear();
		// Game is ready to play, show the board
		ocean.setVisible(true);
		// Print my game information ip and port
		}
	
	// Run the game
	public void run() throws Throwable {
		// give socket time to connect and board time to show up.
		try {	Thread.sleep(1000);	}	catch (Throwable e) {}
		shipAmt = (((boardX+boardY)/2));
		chooseShips(); // User selects ships
		createShipsRand(); // Computer randomly generates ships
				
		battle();
		}
	
	//Close the game
	private void close() throws Throwable {
		// Determine winner
		setBattling(false);
		if((getFleet().size()==0)){	
			Gui.println("You lost, all of your ships have been sunk!\n"+
						"You lost in "+oppShotList.size()+" shots.\n"+
						"You shot "+getMyShotList().size()+" shots.");
			ocean.getMyPanel().setBackground(Color.RED);
			Thread.sleep(250);
			ocean.getMyPanel().setBackground(Color.BLUE);
			Thread.sleep(250);
			ocean.getMyPanel().setBackground(Color.RED);
			Thread.sleep(250);
			ocean.getMyPanel().setBackground(Color.BLUE);
			Thread.sleep(250);
			ocean.getMyPanel().setBackground(Color.PINK);
		}
		else if((getOppFleet().size()==0)){	
			Gui.println("You Win, all of the opponents ships have been sunk!\n"+
						"You took "+getMyShotList().size()+" shots.\n"+
						"Opponent shot "+oppShotList.size()+" shots.");
			ocean.getOppPanel().setBackground(Color.RED);
			Thread.sleep(250);
			ocean.getOppPanel().setBackground(Color.BLUE);
			Thread.sleep(250);
			ocean.getOppPanel().setBackground(Color.RED);
			Thread.sleep(250);
			ocean.getOppPanel().setBackground(Color.BLUE);
			Thread.sleep(250);
			ocean.getOppPanel().setBackground(Color.PINK);

		}

		else if(getMyShotList().size()==(boardX*boardY)){	Gui.println("You lost, you shot "+(boardX*boardY)+" times!");	}
		else if(oppShotList.size()==(boardX*boardY)){	Gui.println("You Win, opponent shot "+(boardX*boardY)+" times!");	}
		else Gui.println("Could not determine winner.");

		// Close the socket connection
		try {	Thread.sleep(3000);	}	catch (Throwable e) {}
	}
	
	// Choose where to put ships
	private void chooseShips() {
		fleet.clear();
		ocean.enableOppBoard(false);
		Gui.println("Click ocean and select "+shipAmt+" ships");
		do{
			ocean.enableMyBoard(true);
			ocean.selectingShips = true;
		}while(getFleet().size()<shipAmt);
		ocean.selectingShips = false;
		ocean.enableMyBoard(false);
		Gui.println("Done choosing Ships");
		ocean.setVisible(true);
	}
	
	// Computer chooses ships randomly
	// Computer chooses ships randomly
		private void createShipsRand() {
			largeBattleship large = new largeBattleship();
			mediumBattleship med = new mediumBattleship();
			smallBattleship small = new smallBattleship();
			getOppFleet().clear(); // Empty Fleet
			// Pick 1 large Ship
			for (int i=0; i<1; i++){	calculateShipPlacement(large);	}
			// Pick 2 medium Ships
			for (int i=0; i<2; i++){	calculateShipPlacement(med);	}
			// Pick 2 small Ships
			for (int i=0; i<2; i++){	calculateShipPlacement(small);	}
		}
		
		private void calculateShipPlacement(ship theShip){
			int x=0, y=0;
			Random randPick = new Random();
			int orientation = (Math.abs(randPick.nextInt(2)));
			
			if (orientation == 0){ // Horizontal Ships
				x = (Math.abs(randPick.nextInt(boardX-theShip.getSize())));
				y = (Math.abs(randPick.nextInt(boardY)));
				for (int i=0; i<theShip.getSize(); i++){
					theShip = new ship(x+i,y, theShip.getSize());
					// search oppFleet for ship matching theShip
					if (searchOppFleet(theShip)==false && getOppFleet().size()<shipAmt){
						ocean.getOppButton()[x+i][y].setBackground(Color.YELLOW);
						getOppFleet().add(theShip);
						}
					}
				}
			else{ // Vertical Ships
				x = (Math.abs(randPick.nextInt(boardX)));
				y = (Math.abs(randPick.nextInt(boardY-theShip.getSize())));
				for (int i=0; i<theShip.getSize(); i++){
					theShip = new ship(x,y+i, theShip.getSize());
					// search oppFleet for ship matching theShip
					if (searchOppFleet(theShip)==false&&getOppFleet().size()<shipAmt){
						getOppFleet().add(theShip);
						ocean.getOppButton()[x][y+i].setBackground(Color.YELLOW);
					}
				}
			}
		}

	// Battle stage: Choose where to shoot, shoot there , get opponent shot, 
	// do calculations, and continue in loop until win or lose.
	private void battle() throws Throwable {	
		Gui.println("Its time to battle!\nClick on opponents ocean to sink ships.");
		setBattling(true);
		// if host then listen
		do{
			MeShoot();
			if((oppFleet.size()==0)||getMyShotList().size()==(boardX*boardY)){	close();	}
////////////////////////////// ~ ~ ~ ~ ~ Player : Computer Separation ~ ~ ~ ~ ~ //////////////////////////////
			ComShoot();
			if((fleet.size()==0)||oppShotList.size()==(boardX*boardY)){	close();	}
			// While opponents status is not an L, i haven't shot 10,000 times and the opponent hasn't shot 10,000 times.
		}while(isBattling());
	}

	private void MeShoot() throws IOException, InterruptedException {
		// Activate OppBoard for a shot
		ocean.enableOppBoard(true); // Enable buttons so that we can click on them
		setShooting(true); // let the game know that we are shooting
		
		while(isShooting() == true){
			Thread.sleep(10);
		}
	}
	
	private void ComShoot() throws ClassNotFoundException, IOException, InterruptedException {
		coor = randShot();
		while (checkComShot(coor)==true){
			coor=randShot();
		}
		oppShotList.add(coor);
		checkFleet(coor);
	}
	
	private boolean checkComShot(Torpedo comCoor) {
		// Has com shot this are yet?
		if((ocean.getMyButton()[comCoor.getX()][comCoor.getY()].getBackground()==Color.CYAN) 
				|| (ocean.getMyButton()[comCoor.getX()][comCoor.getY()].getBackground()==Color.YELLOW)){
			return false;
		}
		return true;		
	}

	// Check if ship exist in fleet already, if true then ship should not be placed.
	static boolean searchOppFleet(ship newShip) {
		for (int i = 0; i<getOppFleet().size(); i++){
			if (getOppFleet().get(i).getX() == newShip.getX()
					&& 
					getOppFleet().get(i).getY() == newShip.getY()){
				return true;
				}
			}
		return false;
	}
	// Check if ship exist in fleet already, if true then ship should not be placed.
		static boolean searchFleet(ship newShip) {
			for (int i = 0; i<getFleet().size(); i++){
				if (getFleet().get(i).getX() == newShip.getX()
						&& 
						getFleet().get(i).getY() == newShip.getY()){
					return true;
					}
				}
			return false;
		}
	
	// Check if coordinate is a hit or miss
	private void checkFleet(Torpedo oppCoor){
	
	// Check if coordinate is a ship
		if (ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].getBackground()==Color.YELLOW){
			// Ships is in fleet, change color to red
			ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].setBackground(Color.RED);
			removeFromFleet(oppCoor);
		}
		else {	
			// Shot missed, change color to blue
			ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].setBackground(Color.BLUE);	
		}
	}
	
	// Check if coordinate is a hit or miss
	public static void checkOppFleet(Torpedo coor){
	// Check if coordinate is a ship
		for (int i = 0; i<getOppFleet().size(); i++){
			if (getOppFleet().get(i).equals(coor)){
				getOppFleet().remove(i);
				// Ships is in fleet, change color to red
				ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.RED);
				Gui.println(coor.toString()+" is a HIT");
				return;
			}	
		}
		// Shot missed, change color to blue
		ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.BLUE);
		Gui.println(coor.toString()+" is a MISS");
	}

	// Remove the ship with matching coordinates from the fleet.
	private void removeFromFleet(Torpedo coor) {
		for (int i = 0; i<fleet.size(); i++){
			if (fleet.get(i).equals(coor)){
				fleet.remove(i);
			}
		}
	}

	// Find random coordinate to shoot
	private Torpedo randShot(){
		Torpedo myShot = new Torpedo();
		Random randShot = new Random();
		myShot.setX(randShot.nextInt(boardX));
		myShot.setY(randShot.nextInt(boardY));
		return myShot;
	}

	public static ArrayList<ship> getFleet() {
		return fleet;
	}

	public void setFleet(ArrayList<ship> fleet) {
		PvCom.fleet = fleet;
	}

	public static ArrayList<ship> getOppFleet() {
		return oppFleet;
	}

	public static void setOppFleet(ArrayList<ship> oppFleet) {
		PvCom.oppFleet = oppFleet;
	}

	public static boolean isBattling() {
		return battling;
	}

	public void setBattling(boolean battling) {
		PvCom.battling = battling;
	}

	public static ArrayList<Torpedo> getMyShotList() {
		return myShotList;
	}

	public void setMyShotList(ArrayList<Torpedo> myShotList) {
		PvCom.myShotList = myShotList;
	}

	public static boolean isShooting() {
		return shooting;
	}

	public static void setShooting(boolean shooting) {
		PvCom.shooting = shooting;
	}	
}

