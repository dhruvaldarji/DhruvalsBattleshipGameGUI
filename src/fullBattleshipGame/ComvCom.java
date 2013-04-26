package fullBattleshipGame;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;



public class ComvCom {
	
	// My connection
	private static Connection socket;
	private int hostClient = 0; // host == 0 or client == 1
	
	// My game board
	private static board ocean;
	@SuppressWarnings("unused")
	private static boolean settingShip = false;
	
	// My various ArrayLists that track ships and shots
	private static ArrayList<ship> fleet = new ArrayList<ship>(); // -----ArrayList
	private ArrayList<Torpedo> oppShotList = new ArrayList<Torpedo>(); // -----ArrayList
	private ArrayList<Torpedo> myShotList = new ArrayList<Torpedo>(); // -----ArrayList
	private ArrayList<Torpedo> parityList = new ArrayList<Torpedo>(); // -----ArrayList

	
	// The following deal with shooting and focused shots against the opponent.
	private Torpedo coor = new Torpedo("M",0,0), oppCoor = new Torpedo(), specialShot = new Torpedo();
	private String oppStatus = "M", myStatus = "M";
	private boolean shipFound = false, right = false, down = false, left = false, up = false;
	private int timesShotSpecially = 0, rightCount = 0, downCount = 0, leftCount = 0, upCount = 0;
	
	public ComvCom() throws Throwable{
		init();
	}
		
	// Initialize the game
	private void init() throws Throwable {
		// Make a new game board with 100x100 buttons
		ocean = new board(100,100);
		// Empty my shot list
		myShotList.clear();
		// Empty opponents shot list
		oppShotList.clear();
		// Empty Parity Shotlist
		parityList.clear();
		// Game is ready to play, show the board
		ocean.setVisible(true);
		// Print my game information ip and port
		//System.out.println("Game ready on "+socket.getMyConnectionInfo()+".");
		}
	
	// Run the game
	public void run(String h, int p) throws Throwable {
		
//		chooseShips(); // User selects ships
		createShipsRand(); // Ships randomly generated
		
		// Host a game or connect to a game
		if (h == null){ // I'm hosting
			hostClient = 0;
			socket = new Connection(null, p);
		}
		else{ // I'm a client
			hostClient = 1;
			socket = new Connection(h, p);
		}
		
		// give socket time to connect and board time to show up.
		try {	Thread.sleep(1000);	}	catch (Throwable e) {}
		
		// Print who the game connected to
		System.out.println("Game now connected to "+socket.getOppConnectionInfo());
				
		battle();
//		System.out.println("opp: "+myShotList.toString()); // view opponents shotlist
//		System.out.println("me: "+oppShotList.toString()); // view my shotlist
//		System.out.println("Shots Made: "+myShotList.size()); // check how many shots i made
		close();
		}
	
	//Close the game
	private void close() throws Throwable {
		// wait 3 seconds and then close;
		try {	socket.close();	Thread.sleep(3000);	}	catch (Throwable e) {}
	}
	
	// Choose where to put ships
	@SuppressWarnings("unused")
	private void chooseShips() {
		System.out.println("Choose 24 ships");
		do{
			ocean.enableMyBoard(true);
			ocean.selectingShips = true;
		}while(getFleet().size()<24);
		ocean.setVisible(false);
		ocean.enableMyBoard(false);
		ocean.selectingShips = false;
		System.out.println("Done choosing Ships");
		ocean.setVisible(true);
	}
	
	// Computer chooses ships randomly
	private void createShipsRand() {
		largeBattleship hShip = new largeBattleship();
		largeBattleship vShip = new largeBattleship();

		getFleet().clear(); // Empty Fleet
		// Pick 4 ships
		for (int i = 0; i<4; i++){
			int x=0, y=0;
			Random randPick = new Random();
			
			int orientation = (Math.abs(randPick.nextInt(2)));
			
			if (orientation == 0){ // Horizontal Ships
				
				x = (Math.abs(randPick.nextInt(94)));
				
				y = (Math.abs(randPick.nextInt(100)));
								
				for (int s = 0; s<hShip.getSize();s++){
					hShip = new largeBattleship(x+s,y);
					// search serverFleet for ship matching newShip
					if (searchFleet(hShip)==false){
						getFleet().add(hShip);
						ocean.getMyButton()[hShip.getX()][hShip.getY()].setBackground(Color.yellow);
						}
					}
				}
			else if (orientation == 1){ // Vertical Ships
				
				x = (Math.abs(randPick.nextInt(100)));
				
				y = (Math.abs(randPick.nextInt(94)));
				
				for (int s = 0; s<6;s++){
					vShip = new largeBattleship(x,y+s);
					// search serverFleet for ship matching newShip
					if (searchFleet(vShip)==false){
						ocean.getMyButton()[vShip.getX()][vShip.getY()].setBackground(Color.yellow);
						getFleet().add(vShip);
					}
				}
			}
		}
	}

	// Battle stage: Choose where to shoot, shoot there , get opponent shot, 
	// do calculations, and continue in loop until win or lose.
	private void battle() throws Throwable {	
		// if host then listen
		if (hostClient == 0){	getShotAt();	}
		
		do{
			shoot();
////////////////////////////// ~ ~ ~ ~ ~ Player : Opponent Separation ~ ~ ~ ~ ~ //////////////////////////////
			getShotAt();
			
			// While opponents status is not an L, i haven't shot 10,000 times and the opponent hasn't shot 10,000 times.
			}while(!(oppStatus.equalsIgnoreCase("L")) || (myShotList.size()!=10000) || (oppShotList.size()!=10000));
		
		// If the fleet is empty or i have shot 10,000 times, I Lose
		if (getFleet().isEmpty()==true || (myShotList.size()==10000)){	System.out.println("\nYou Lose. Shame on you!");	}
		
		// Else I Win
		else {	System.out.println("\nYou're a Winner!");	}
	}

	private void shoot() throws IOException {
		
		// Calculate the next shot by sending  the coordinates of the last shot
		// and the status returned by the opponent.
		coor = findNextShot(coor, oppStatus);
		// Set my status to the calculations from the opponents last shot.
		coor.setStatus(myStatus);
		
		// Send coordinate as long as the opponent has not sent an L
		// Which means they have probably ended the connection.
		 if (!(oppCoor.getStatus().equalsIgnoreCase("L"))){
			// If opponent status is L, then don't shoot.
			socket.sendMessage(coor.toString());
		}
	}
	
	private void getShotAt() throws ClassNotFoundException, IOException, InterruptedException {
		
		// Get the opponents coordinates
		oppCoor = socket.getMessage(); 
		
		// Add opponents shot to opponent's shot list. 
		oppShotList.add(oppCoor);
		
		// Update the opponent's board
		updateOppBoard(oppStatus, coor);
		
		// Check if the opponent has shot the same place twice
		if (checkIfShot(oppCoor)==true){
			// Return  miss if the opponent has shot the same place twice
			myStatus = "M";
		}
	
		// As long as the opponents status is not a L
		// Check if the shot was a hit, miss, or lose and make that my next status.
		if (!(oppStatus.equalsIgnoreCase("L"))){	
			myStatus = checkFleet(oppCoor);
			// check if all ships have been destroyed
			if(getFleet().isEmpty()==true) myStatus = "L";
		}
	}

	// Check if ship exist in fleet already
	private boolean searchFleet(ship newShip) {
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
	private String checkFleet(Torpedo oppCoor){
	
	// Check if coordinate is a ship
	for (int i = 0; i<getFleet().size(); i++){
		if (getFleet().get(i).getX() == oppCoor.getX()
				&& 
				getFleet().get(i).getY() == oppCoor.getY()){
			// Ships is in fleet, change to hit
			ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].setBackground(Color.RED);
			getFleet().remove(i);
			return"H";
			}
		else {	
			// Shot missed, change ocean to blue
			ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].setBackground(Color.BLUE);	
			}
		}
		return "M";
	}
		
	// Check if the coordinate has already been shot by me
	private boolean checkIfIShot(Torpedo coor){
		// Check if coordinate has already been shot
		for (int i = 0; i<myShotList.size(); i++){
			if (myShotList.get(i).getX() == coor.getX()
					&& 
					myShotList.get(i).getY() == coor.getY()){
				return true; // Coordinate already shot
				}
			}
		return false; // Coordinate not shot
	}	
	
	// Check if the coordinate has already been shot by the opponent
	private boolean checkIfShot(Torpedo coor){
		// Check if coordinate has already been shot
		for (int i = 0; i<oppShotList.size(); i++){
			if (oppShotList.get(i).getX() == coor.getX()
					&& 
					oppShotList.get(i).getY() == coor.getY()){
				return true; // Coordinate already shot
				}
			}
		return false; // Coordinate not shot
	}
	
	// Update opponent's board.
	private void updateOppBoard(String oppStatus, Torpedo coor) throws InterruptedException {
		if (oppStatus.equalsIgnoreCase("H")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.RED);	}
		if (oppStatus.equalsIgnoreCase("M")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.BLUE);	}
		if (oppStatus.equalsIgnoreCase("L")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.RED);	}
	}

	// Find the next place to shoot
	private Torpedo findNextShot(Torpedo coor, String status){
		// Get the coordinate to attack (2 DIFFERENT WAYS!!!,  magicShots and randShot)
		
		// For magicShots
		// If I get a hit and shipFound == false or if shipFound == true do magicShots
		if ((status.equalsIgnoreCase("H") && (shipFound == false))||(shipFound == true)){	coor = magicShots(coor, status);	}	
		
		// Randomly generated coordinate
		else {	coor = randShot();	}	
		
		// Make sure that the shot isn't out of bounds or isn't already shot
		while(coor.getX()<0||coor.getX()>99){	coor = tryNewX(coor);	}
		while(coor.getY()<0||coor.getY()>99){	coor = tryNewY(coor);	}
		while(checkIfIShot(coor)==true){
				coor = tryNewX(coor);
				coor = tryNewY(coor);
			}
		myShotList.add(coor);
		return coor;
	}
	
	// Find random coordinate to shoot
	private Torpedo randShot(){
		Torpedo myShot = new Torpedo();
		Random randShot = new Random();
		myShot.setX(randShot.nextInt(100));
		myShot.setY(randShot.nextInt(100));
		return myShot;
	}
	
//	Hone in on a hit ship. Version 2
	private Torpedo magicShots(Torpedo coor, String status){
		Torpedo next = new Torpedo(); // next shot
		next = new Torpedo(specialShot.getX(),specialShot.getY());
		// If a ship has just been found
		if (shipFound == false && status.equalsIgnoreCase("H")){
			shipFound = true;	timesShotSpecially = 0;
			specialShot = new Torpedo(coor.getX(), coor.getY());
			next = new Torpedo(specialShot.getX(),specialShot.getY());
		}	
		// shoot in all directions
		if (timesShotSpecially<4 && right == false && down == false && left == false && up == false){
			switch(timesShotSpecially){
			case 0: // shoot right of the coordinate
				next.setX(specialShot.getX()+1);
				timesShotSpecially++;
				return next;
			case 1:	// if shooting below the coordinate was a hit, then do downShots
				if (status.equalsIgnoreCase("H")){	 right = true; timesShotSpecially++; break;	}
				else{
					// shoot below the coordinate
					next.setY(specialShot.getY()+1);
					timesShotSpecially++;
				}
				return next;
			case 2: // if shooting right of coordinate was a hit, then do rightShots
				if(status.equalsIgnoreCase("H")){	down = true; timesShotSpecially++; break;	}
				else {
					// shoot left of the coordinate
					next.setX(specialShot.getX()-1);
					timesShotSpecially++;
				}
				return next;
			case 3: // if shooting left of coordinate was a hit, then do leftShots
				if (status.equalsIgnoreCase("H")){	left = true; timesShotSpecially++; break;	}
				else{
					// shoot above the coordinate
					next.setY(specialShot.getY()-1);
					timesShotSpecially++;
				}
				return next;
			case 4: // if shooting above the coordinate was a hit, do upShots
				if(status.equalsIgnoreCase("H")){	up = true; timesShotSpecially++; break;	}
				}
			}
		// Determine what ship has been found and shoot accordingly.
		if (right == true){
			if(rightCount<12){
				next.setX(specialShot.getX()-rightCount+5);
				rightCount++;
				return next;
			}
			right = false;
			rightCount = 0;
		}
		else if (down == true){
			if(downCount<12){
				next.setY(specialShot.getY()-downCount+5);	
				downCount++;
				return next;
			}
			down = false;
			downCount = 0; 
		}
		else if (left == true){
			if(leftCount<5){
				next.setX(specialShot.getX()-5+leftCount);
				leftCount++;
				return next;
			}
			left = false;
			leftCount = 0;
		}
		else if (up == true){
			if(upCount <5){
				next.setY(specialShot.getY()-5+upCount);
				upCount++;
				return next;
			}
			up = false;
			upCount = 0;
		}
		else {
			next = randShot(); // randomly generated coordinate
		}
		return next;
	}
		
//	Increment or Decrement the X so that it doesn't go out of bounds
	private Torpedo tryNewX(Torpedo coor){
		Torpedo newCoor = new Torpedo(coor.getX(), coor.getY());
		while(newCoor.getX()<0){	newCoor.setX(newCoor.getX()+1);	}
		while(newCoor.getX()>99){	newCoor.setX(newCoor.getX()-1);	}
		return newCoor;
	}
	
//	Increment or Decrement the Y so that it doesn't go out of bounds
	private Torpedo tryNewY(Torpedo coor){
		Torpedo newCoor = new Torpedo(coor.getStatus(), coor.getX(), coor.getY());
		while(newCoor.getY()<0){	newCoor.setY(newCoor.getY()+1);	}
		while(newCoor.getY()>99){	newCoor.setY(newCoor.getY()-1);	}
		return newCoor;
	}
	

	public static ArrayList<ship> getFleet() {
		return fleet;
	}
	

	public void setFleet(ArrayList<ship> fleet) {
		ComvCom.fleet = fleet;
	}
	
}
