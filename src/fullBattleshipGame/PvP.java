package fullBattleshipGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;



public class PvP {
	
	static ArrayList<ship> fleet = new ArrayList<ship>();
	static ArrayList<Torpedo> oppShotList = new ArrayList<Torpedo>();
	static ArrayList<Torpedo> myShotList = new ArrayList<Torpedo>();
	static board ocean;
	static Connection socket;
	static boolean settingShip = false;
	// The following deal with focused shots against the opponent.
	boolean shipFound = false, right = false, down = false, left = false, up = false;
	int dir = 0, rightCount = 0, downCount = 0, leftCount = 0, upCount = 0;
	Torpedo specialShot = null;
	
	public PvP() throws Throwable{
		init();
	}
		
	// Initialize the game
	void init() throws Throwable {
		ocean = new board(26,26);
		fleet.clear();
		oppShotList.clear();
		myShotList.clear();
		specialShot = new Torpedo();
		
		ocean.setVisible(true);
		}
	
	// Run the game
	void run(String h, int p) throws Throwable {
		
		chooseShips(); // human
//		createShipsRand(); // computer
		
		socket = new Connection(h, p);
		
		// give socket time to connect and board time to show up.
		try {	Thread.sleep(1000);	}	catch (Throwable e) {}
		battle();
//			System.out.println("opp: "+myShotList.toString());
//			System.out.println("me: "+oppShotList.toString());
		System.out.println("Shots Made: "+myShotList.size());
		close();
		}
	
	private void chooseShips() {
		System.out.println("Choose 16 ships");
		do{
			ocean.enableMyBoard(true);
			ocean.selectingShips = true;
		}while(fleet.size()<16);
		ocean.setVisible(false);
		ocean.enableMyBoard(false);
		ocean.selectingShips = false;
		System.out.println("Done choosing Ships");
		ocean.setVisible(true);
	}

	//Close the game
	static void close() throws Throwable {
		// wait 3 seconds and then close;
		try {	socket.close();	Thread.sleep(3000);	}	catch (Throwable e) {}
		ocean.setVisible(false);
		}
	
	// Computer chooses ships.
	void createShipsRand() {
		largeBattleship hShip = new largeBattleship();
		largeBattleship vShip = new largeBattleship();

		fleet.clear();
		for (int i = 0; i<6; i++){
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
						fleet.add(hShip);
						board.getMyButton()[hShip.getX()][hShip.getY()].setBackground(Color.yellow);
						}
					}
				}
			else if (orientation == 1){ // Vertical Ships
				for (int s = 0; s<lShip.getSize();s++){
					vShip = new largeBattleship(lShip.getX(),lShip.getY()+s);
					// search serverFleet for ship matching newShip
					if (searchFleet(vShip)==false){
						board.getMyButton()[vShip.getX()][vShip.getY()].setBackground(Color.yellow);
						fleet.add(vShip);
					}
				}
			}
		}
	}

	// Battle stage: Choose where to shoot, shoot, get opponent shot, 
	// do calculations, and continue in loop until win or lose.
	void battle() throws Throwable {
		Torpedo coor = new Torpedo();
		Torpedo oppCoor = new Torpedo();
		String oppStatus = "m";
		String myStatus = "m";
		try {
			do{
				// finds the next Shot
				coor = findNextShot(coor, oppStatus);
				coor.setStatus(myStatus);
				// Send coordinate
				if (!oppStatus.equalsIgnoreCase("L")) socket.sendMessage(coor.toString());
				///////////////////Player : Opponent Separation///////////////////
				// check opponent coordinate
				if (!myStatus.equalsIgnoreCase("L")) oppCoor = socket.getMessage();
				oppStatus = oppCoor.getStatus();
				updateOppBoard(oppStatus, coor);
				
				if (oppStatus.equalsIgnoreCase("L"))	break;
				
				//check for hit/miss
				else if (checkShot(oppCoor)==false){	
					myStatus = checkFleet(oppCoor);
					// check if all ships have been destroyed
					if(fleet.isEmpty()==true) myStatus = "L";
				}
								
				}while(!myStatus.equalsIgnoreCase("L")|| !(oppStatus.equalsIgnoreCase("L")) || (myShotList.size()!=10000));
			} catch (Throwable e) {}
		finally{	
			if (fleet.isEmpty()==true || myShotList.size()==10000){	System.out.println("\nYou Lose. Shame on you socket!");	}
			else if (oppStatus.equalsIgnoreCase("L")){	System.out.println("\nYou're a Winner socket!");	}
			}
		}
	
	// Check if ship exist in fleet already
	boolean searchFleet(ship newShip) {
		for (int i = 0; i<fleet.size(); i++){
			if (fleet.get(i).getX() == newShip.getX()
					&& 
					fleet.get(i).getY() == newShip.getY()){
				return true;
				}
			}
		return false;
		}
	
	// Check if coordinate is a hit or miss
	String checkFleet(Torpedo serverCoor){
		// Check if coordinate is a ship
		Torpedo shot = new Torpedo(serverCoor.getStatus(),serverCoor.getX(),serverCoor.getY());
		// add coordinate to shots list
		oppShotList.add(shot);
		for (int i = 0; i<fleet.size(); i++){
			if (fleet.get(i).getX() == serverCoor.getX()
					&& 
					fleet.get(i).getY() == serverCoor.getY()){
				board.getMyButton()[serverCoor.getX()][serverCoor.getY()].setBackground(Color.red);
				fleet.remove(i);
				return"h";
				}
			else {	board.getMyButton()[serverCoor.getX()][serverCoor.getY()].setBackground(Color.blue);	}
			}
		return "m";
		}
	
	// Check if the coordinate has already been shot
	boolean checkShot(Torpedo coor){
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
	void updateOppBoard(String oppStatus, Torpedo coor) {
		if (oppStatus.equalsIgnoreCase("H")){	board.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.red);	}
		else if (oppStatus.equalsIgnoreCase("M")) {	board.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.blue);	}
		else {	/* if oppStatus  is L do Nothing*/	}
	}

	// Find the next place to shoot
	Torpedo findNextShot(Torpedo coor, String status){
		//get coordinate to attack (3 DIFFERENT WAYS!!!,  magicShot2 and randShot, or user choice
		
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
	Torpedo randShot(){
		dir = 0;
		Torpedo myShot = new Torpedo();
		Random randShot = new Random();
		myShot.setX(randShot.nextInt(100));
		myShot.setY(randShot.nextInt(100));
		myShotList.add(myShot);
		return myShot;
	}
	
	// Hone in on a hit ship. Version 2
	Torpedo magicShots(Torpedo coor, String status){
		Torpedo magicShot = new Torpedo();
		magicShot = new Torpedo(specialShot.getX(),specialShot.getY());
		if (shipFound == false && status.equalsIgnoreCase("h")){
			shipFound = true;	dir = 0;
			specialShot = new Torpedo(coor.getX(), coor.getY());
			magicShot = new Torpedo(specialShot.getX(),specialShot.getY());
		}	
		// shoot in all directions
		if (dir<4 && right == false && down == false && left == false && up == false){
			switch(dir){
			case 0: // shoot right of the coordinate
				magicShot.setX(specialShot.getX()+1);
				myShotList.add(magicShot);
				dir++;
				return magicShot;
			case 1:	// if shooting below the coordinate was a hit, then do downShots
				if (status.equalsIgnoreCase("H")){	 right = true; dir++; break;	}
				else{
					// shoot below the coordinate
					magicShot.setY(specialShot.getY()+1);
					myShotList.add(magicShot);
					dir++;
				}
				return magicShot;
			case 2: // if shooting right of coordinate was a hit, then do rightShots
				if(status.equalsIgnoreCase("H")){	down = true; dir++; break;	}
				else {
					// shoot left of the coordinate
					magicShot.setX(specialShot.getX()-1);
					myShotList.add(magicShot);
					dir++;
				}
				return magicShot;
			case 3: // if shooting left of coordinate was a hit, then do leftShots
				if (status.equalsIgnoreCase("H")){	left = true; dir++; break;	}
				else{
					// shoot above the coordinate
					magicShot.setY(specialShot.getY()-1);
					myShotList.add(magicShot);
					dir++;
				}
				return magicShot;
			case 4: // if shooting above the coordinate was a hit, do upShots
				if(status.equalsIgnoreCase("H")){	up = true; dir++; break;	}
				}
			}
		// Determine what ship has been found and shoot accordingly.
		if (right == true){
			if(rightCount<12){
				magicShot.setX(specialShot.getX()-rightCount+5);
				rightCount++;
				myShotList.add(magicShot);
				return magicShot;
			}
			right = false;
			rightCount = 0;
		}
		else if (down == true){
			if(downCount<12){
				magicShot.setY(specialShot.getY()-downCount+5);	
				downCount++;
				myShotList.add(magicShot);
				return magicShot;
			}
			down = false;
			downCount = 0; 
		}
		else if (left == true){
			if(leftCount<5){
				magicShot.setX(specialShot.getX()-5+leftCount);
				leftCount++;
				myShotList.add(magicShot);
				return magicShot;
			}
			left = false;
			leftCount = 0;
		}
		else if (up == true){
			if(upCount <5){
				magicShot.setY(specialShot.getY()-5+upCount);
				upCount++;
				myShotList.add(magicShot);
				return magicShot;
			}
			up = false;
			upCount = 0;
		}
		else {
			magicShot = randShot(); // randomly generated coordinate
		}
		myShotList.add(magicShot);
		return magicShot;
	}
		
//		Increment or Decrement the X so that it doesn't go out of bounds
	Torpedo tryNewX(Torpedo coor){
		Torpedo newCoor = new Torpedo(coor.getX(), coor.getY());
		while(newCoor.getX()<0){	newCoor.setX(newCoor.getX()+1);	}
		while(newCoor.getX()>99){	newCoor.setX(newCoor.getX()-1);	}
		return newCoor;
	}
	
//		Increment or Decrement the Y so that it doesn't go out of bounds
	Torpedo tryNewY(Torpedo coor){
		Torpedo newCoor = new Torpedo(coor.getStatus(), coor.getX(), coor.getY());
		while(newCoor.getY()<0){	newCoor.setY(newCoor.getY()+1);	}
		while(newCoor.getY()>99){	newCoor.setY(newCoor.getY()-1);	}
		return newCoor;
	}
}
