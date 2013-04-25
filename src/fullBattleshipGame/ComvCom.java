package fullBattleshipGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;



public class ComvCom {
	
	private static ArrayList<ship> fleet = new ArrayList<ship>();
	private static ArrayList<Torpedo> oppShotList = new ArrayList<Torpedo>();
	private static ArrayList<Torpedo> myShotList = new ArrayList<Torpedo>();
	private static board ocean;
	private static Connection socket;
	@SuppressWarnings("unused")
	private static boolean settingShip = false;
	// The following deal with focused shots against the opponent.
	private boolean shipFound = false, right = false, down = false, left = false, up = false;
	private int dir = 0, rightCount = 0, downCount = 0, leftCount = 0, upCount = 0;
	private Torpedo specialShot = null;
	private int hostClient = 0; // host == 0 or client == 1
	
	public ComvCom() throws Throwable{
		init();
	}
		
	// Initialize the game
	private void init() throws Throwable {
		ocean = new board(100,100);
		getFleet().clear();
		oppShotList.clear();
		myShotList.clear();
		specialShot = new Torpedo();
		
		ocean.setVisible(true);
		}
	
	// Run the game
	public void run(String h, int p) throws Throwable {
		
//		chooseShips(); // human
		createShipsRand(); // computer
		if (h == null){
			hostClient  = 0;
			socket = new Connection(null, p);
		}
		else{
			hostClient = 1;
			socket = new Connection(h, p);
			
		}
		System.out.println(h+", "+p);
		// give socket time to connect and board time to show up.
		try {	Thread.sleep(1000);	}	catch (Throwable e) {}
		battle();
//		System.out.println("opp: "+myShotList.toString());
//		System.out.println("me: "+oppShotList.toString());
//		System.out.println("Shots Made: "+myShotList.size());
		close();
		}
	
	//Close the game
	private void close() throws Throwable {
		// wait 3 seconds and then close;
		try {	socket.close();	Thread.sleep(3000);	}	catch (Throwable e) {}
		ocean.setVisible(false);
	}
	
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
	
	// Computer chooses ships.
	private void createShipsRand() {
		largeBattleship hShip = new largeBattleship();
		largeBattleship vShip = new largeBattleship();

		getFleet().clear();
		for (int i = 0; i<4; i++){
			int x=0, y=0;
			Random randPick = new Random();
			x = (Math.abs(randPick.nextInt())%94);
			while(x<1 || x>100){
				x = (Math.abs(randPick.nextInt())%94);
			}
			y = (Math.abs(randPick.nextInt())%94);
			while(y<1 || y>100){
				y = (Math.abs(randPick.nextInt())%94);
			}
			largeBattleship lShip = new largeBattleship(x,y);
			int orientation = (Math.abs(randPick.nextInt())%2);
			
			if (orientation == 0){ // Horizontal Ships
				for (int s = 0; s<lShip.getSize();s++){
					hShip = new largeBattleship(lShip.getX()+s,lShip.getY());
					// search serverFleet for ship matching newShip
					if (searchFleet(hShip)==false){
						getFleet().add(hShip);
						ocean.getMyButton()[hShip.getX()][hShip.getY()].setBackground(Color.yellow);
						}
					}
				}
			else if (orientation == 1){ // Vertical Ships
				for (int s = 0; s<lShip.getSize();s++){
					vShip = new largeBattleship(lShip.getX(),lShip.getY()+s);
					// search serverFleet for ship matching newShip
					if (searchFleet(vShip)==false){
						ocean.getMyButton()[vShip.getX()][vShip.getY()].setBackground(Color.yellow);
						getFleet().add(vShip);
					}
				}
			}
		}
	}

	// Battle stage: Choose where to shoot, shoot, get opponent shot, 
	// do calculations, and continue in loop until win or lose.
	private void battle() throws Throwable {
		Torpedo coor = new Torpedo();
		Torpedo oppCoor = new Torpedo();
		String oppStatus = "M";
		String myStatus = "M";
		
		try {
			// if host then listen
			if (hostClient == 0){
				// check opponent coordinate
				oppCoor = socket.getMessage(); 
				
				updateOppBoard(oppStatus, coor);
				
				//check for hit/miss
				if ((checkIfShot(oppCoor)==false)){	
					myStatus = checkFleet(oppCoor);
					// check if all ships have been destroyed
					if(getFleet().isEmpty()==true)
						myStatus = "L";
				}
			}
				
			do{
				// finds the next Shot
				coor = findNextShot(coor, oppStatus);
				coor.setStatus(myStatus);
				// Send coordinate
				 if ((!oppCoor.getStatus().equalsIgnoreCase("L"))){
					// If this is the first shot and the game is hosted then don't shoot. // Client always shoots first.
					// If opponent status is L, then don't shoot.
					socket.sendMessage(coor.toString());
				}
				
////////////////////////////// ~ ~ ~ ~ ~ Player : Opponent Separation ~ ~ ~ ~ ~ //////////////////////////////
				
				// check opponent coordinate
				oppCoor = socket.getMessage(); 
				
				updateOppBoard(oppStatus, coor);
				
				//check for hit/miss
				if ((checkIfShot(oppCoor)==false)&&(!oppStatus.equalsIgnoreCase("L"))){	
					myStatus = checkFleet(oppCoor);
					// check if all ships have been destroyed
					if(getFleet().isEmpty()==true) myStatus = "L";
				}
								
				}while(!(oppStatus.equalsIgnoreCase("L")) || (myShotList.size()!=10000) || (oppShotList.size()!=10000));
			} catch (Throwable e) {}	
		
		if (getFleet().isEmpty()==true || (myShotList.size()==10000)){	System.out.println("\nYou Lose. Shame on you!");	}
		else {	System.out.println("\nYou're a Winner!");	}
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
	private String checkFleet(Torpedo serverCoor){
		// Check if coordinate is a ship
		// add coordinate to shots list
		oppShotList.add(serverCoor); // ArrayList
		for (int i = 0; i<getFleet().size(); i++){
			if (getFleet().get(i).getX() == serverCoor.getX()
					&& 
					getFleet().get(i).getY() == serverCoor.getY()){
				ocean.getMyButton()[serverCoor.getX()][serverCoor.getY()].setBackground(Color.RED);
				getFleet().remove(i);
				return"H";
				}
			else {	ocean.getMyButton()[serverCoor.getX()][serverCoor.getY()].setBackground(Color.BLUE);	}
			}
		return "M";
		}
	
	// Check if the coordinate has already been shot
	private boolean checkIfShot(Torpedo coor){
		// check if coordinate has already been shot
		for (int i = 0; i<oppShotList.size(); i++){
			if (oppShotList.get(i).getX() == coor.getX()
					&& 
					oppShotList.get(i).getY() == coor.getY()){
				return true; // coordinate already shot
				}
			}
		return false; // coordinate not shot
		}
	
	// Update opponent's board.
	private void updateOppBoard(String oppStatus, Torpedo coor) throws InterruptedException {
		
		if (oppStatus.equalsIgnoreCase("H")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.RED);	}
		else if (oppStatus.equalsIgnoreCase("M")) {	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.BLUE);	}
		else if (oppStatus.equalsIgnoreCase("L")){
			ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.red);
			// Flash board red 4 times upon receiving L.
			ocean.setBackground(Color.RED);
			Thread.sleep(250);
			ocean.setBackground(Color.BLUE);
			Thread.sleep(250);
			ocean.setBackground(Color.RED);
			Thread.sleep(250);
			ocean.setBackground(Color.BLUE);
			Thread.sleep(250);
			ocean.setBackground(Color.RED);
			Thread.sleep(250);
			ocean.setBackground(Color.BLUE);
		}
	}

	// Find the next place to shoot
	private Torpedo findNextShot(Torpedo coor, String status){
		//get coordinate to attack (2 DIFFERENT WAYS!!!,  magicShot2 and randShot
		
		// for magicShots
		if ((status.equalsIgnoreCase("H")&&(shipFound == false))||(shipFound == true)){	coor = magicShots(coor, status);	}	
		
		//for normal shots
		else {
			coor = randShot(); // randomly generated coordinate
		}
		// make sure that the shot isn't out of bounds
		if(coor.getX()<0||coor.getX()>99){	coor = tryNewX(coor);	}
		if(coor.getY()<0||coor.getY()>99){	coor = tryNewY(coor);	}
		
		return coor;
	}
	
	// Find random coordinate to shoot
	private Torpedo randShot(){
		Torpedo myShot = new Torpedo();
		Random randShot = new Random();
		myShot.setX(randShot.nextInt(100));
		myShot.setY(randShot.nextInt(100));
		myShotList.add(myShot); //ArrayList
		return myShot;
	}
	
	// Hone in on a hit ship. Version 2
	private Torpedo magicShots(Torpedo coor, String status){
		Torpedo next = new Torpedo(); // next shot
		next = new Torpedo(specialShot.getX(),specialShot.getY());
		if (shipFound == false && status.equalsIgnoreCase("h")){
			shipFound = true;	dir = 0;
			specialShot = new Torpedo(coor.getX(), coor.getY());
			next = new Torpedo(specialShot.getX(),specialShot.getY());
		}	
		// shoot in all directions
		if (dir<4 && right == false && down == false && left == false && up == false){
			switch(dir){
			case 0: // shoot right of the coordinate
				next.setX(specialShot.getX()+1);
				myShotList.add(next);
				dir++;
				return next;
			case 1:	// if shooting below the coordinate was a hit, then do downShots
				if (status.equalsIgnoreCase("H")){	 right = true; dir++; break;	}
				else{
					// shoot below the coordinate
					next.setY(specialShot.getY()+1);
					myShotList.add(next);
					dir++;
				}
				return next;
			case 2: // if shooting right of coordinate was a hit, then do rightShots
				if(status.equalsIgnoreCase("H")){	down = true; dir++; break;	}
				else {
					// shoot left of the coordinate
					next.setX(specialShot.getX()-1);
					myShotList.add(next);
					dir++;
				}
				return next;
			case 3: // if shooting left of coordinate was a hit, then do leftShots
				if (status.equalsIgnoreCase("H")){	left = true; dir++; break;	}
				else{
					// shoot above the coordinate
					next.setY(specialShot.getY()-1);
					myShotList.add(next);
					dir++;
				}
				return next;
			case 4: // if shooting above the coordinate was a hit, do upShots
				if(status.equalsIgnoreCase("H")){	up = true; dir++; break;	}
				}
			}
		// Determine what ship has been found and shoot accordingly.
		if (right == true){
			if(rightCount<12){
				next.setX(specialShot.getX()-rightCount+5);
				rightCount++;
				myShotList.add(next);
				return next;
			}
			right = false;
			rightCount = 0;
		}
		else if (down == true){
			if(downCount<12){
				next.setY(specialShot.getY()-downCount+5);	
				downCount++;
				myShotList.add(next);
				return next;
			}
			down = false;
			downCount = 0; 
		}
		else if (left == true){
			if(leftCount<5){
				next.setX(specialShot.getX()-5+leftCount);
				leftCount++;
				myShotList.add(next);
				return next;
			}
			left = false;
			leftCount = 0;
		}
		else if (up == true){
			if(upCount <5){
				next.setY(specialShot.getY()-5+upCount);
				upCount++;
				myShotList.add(next);
				return next;
			}
			up = false;
			upCount = 0;
		}
		else {
			next = randShot(); // randomly generated coordinate
		}
		myShotList.add(next);
		return next;
	}
		
//		Increment or Decrement the X so that it doesn't go out of bounds
	private Torpedo tryNewX(Torpedo coor){
		Torpedo newCoor = new Torpedo(coor.getX(), coor.getY());
		while(newCoor.getX()<0){	newCoor.setX(newCoor.getX()+1);	}
		while(newCoor.getX()>99){	newCoor.setX(newCoor.getX()-1);	}
		return newCoor;
	}
	
//		Increment or Decrement the Y so that it doesn't go out of bounds
	private Torpedo tryNewY(Torpedo coor){
		Torpedo newCoor = new Torpedo(coor.getStatus(), coor.getX(), coor.getY());
		while(newCoor.getY()<0){	newCoor.setY(newCoor.getY()+1);	}
		while(newCoor.getY()>99){	newCoor.setY(newCoor.getY()-1);	}
		return newCoor;
	}

	
	public static ArrayList<ship> getFleet() {
		return fleet;
	}

	public static void setFleet(ArrayList<ship> fleet) {
		ComvCom.fleet = fleet;
	}
}
