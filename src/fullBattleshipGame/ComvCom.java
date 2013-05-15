package fullBattleshipGame;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ComvCom implements Runnable{
	
	// My connection
	private static Connection socket;
	private int hostClient = 0; // host == 0 or client == 1
	
	// My game board
	private static board ocean;
	
	// My various ArrayLists that track ships and shots
	private static ArrayList<ship> fleet = new ArrayList<ship>(); // -----ArrayList
	private ArrayList<Torpedo> oppShotList = new ArrayList<Torpedo>(); // -----ArrayList
	private ArrayList<Torpedo> myShotList = new ArrayList<Torpedo>(); // -----ArrayList
	
	// The following deal with shooting and focused shots against the opponent.
	private Torpedo coor = new Torpedo("M",0,0), oppCoor = new Torpedo(), specialShot = new Torpedo(), next = new Torpedo();
	private String oppStatus = "M", myStatus = "M";
	private boolean shipFound = false, right = false, down = false, left = false, up = false, battling=false;
	private int timesShotSpecially = 0, hits = 0, direction = 1; 
	
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
		// Game is ready to play, show the board
		ocean.setVisible(true);
		// Print my game information ip and port
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
		socket.run();
		// give socket time to connect and board time to show up.
		try {	Thread.sleep(1000);	}	catch (Throwable e) {}
				
		battle();
//		Gui.println("opp: "+myShotList.toString()); // view opponents shotlist
//		Gui.println("me: "+oppShotList.toString()); // view my shotlist
		}
	
	//Close the game
	private void close() throws Throwable {
		// Determine winner
		battling = false;
		if(myStatus.equalsIgnoreCase("L")){	
			Gui.println("You lost, all of your ships have been sunk!\n"+
						"You lost in "+oppShotList.size()+" shots.\n"+
						"You shot "+myShotList.size()+" shots.");
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
		else if(oppStatus.equalsIgnoreCase("L")){	
			Gui.println("You Win, all of the opponents ships have been sunk!\n"+
						"You took "+myShotList.size()+" shots.\n"+
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
		else if(myShotList.size()==10000){	Gui.println("You lost, you shot 10,000 times!");	}
		else if(oppShotList.size()==10000){	Gui.println("You Win, opponent shot 10,000 times!");	}
		else Gui.println("Could not determine winner.");
		
		// Close the socket connection
		try {	socket.close();	Thread.sleep(3000);	}	catch (Throwable e) {}
	}
	
	// Choose where to put ships
	@SuppressWarnings("unused")
	private void chooseShips() {
		Gui.println("Choose 24 ships");
		do{
			ocean.enableMyBoard(true);
			ocean.selectingShips = true;
		}while(getFleet().size()<24);
		ocean.setVisible(false);
		ocean.enableMyBoard(false);
		ocean.selectingShips = false;
		Gui.println("Done choosing Ships");
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
						ocean.getMyButton()[hShip.getX()][hShip.getY()].setBackground(Color.YELLOW);
						}
					}
				}
			else if (orientation == 1){ // Vertical Ships
				x = (Math.abs(randPick.nextInt(100)));
				y = (Math.abs(randPick.nextInt(94)));
				for (int s = 0; s<vShip.getSize();s++){
					vShip = new largeBattleship(x,y+s);
					// search serverFleet for ship matching newShip
					if (searchFleet(vShip)==false){
						ocean.getMyButton()[vShip.getX()][vShip.getY()].setBackground(Color.YELLOW);
						getFleet().add(vShip);
					}
				}
			}
		}
	}

	// Battle stage: Choose where to shoot, shoot there , get opponent shot, 
	// do calculations, and continue in loop until win or lose.
	private void battle() throws Throwable {	
		battling = true;
		// if host then listen
		if (hostClient == 0){	getShotAt();	}
		do{
			shoot();
			if(myStatus.equalsIgnoreCase("L")||myShotList.size()==10000){	close();	}
			Thread.sleep(10);
////////////////////////////// ~ ~ ~ ~ ~ Player : Opponent Separation ~ ~ ~ ~ ~ //////////////////////////////
			getShotAt();
			if(oppStatus.equalsIgnoreCase("L")||oppShotList.size()==10000){	close();	}
			// While opponents status is not an L, i haven't shot 10,000 times and the opponent hasn't shot 10,000 times.
		}while(battling);
	}

	private void shoot() throws IOException {
		// Calculate the next shot by sending  the coordinates of the last shot
		// and the status returned by the opponent.
		coor = findNextShot(coor, oppStatus);
		// Check if I have hit this shot yet, if not then send. If i have, then findNextShot
		String currentStatus = checkIfIShot(coor); // Status of the coordinate I'm trying to shoot.
		while(!currentStatus.equalsIgnoreCase("N")){
			coor = findNextShot(coor,currentStatus);
			currentStatus = checkIfIShot(coor);
		}
		// Set my status to the calculations from the opponents last shot.
		coor.setStatus(myStatus);
		myShotList.add(coor);
		// Send coordinate 
		socket.sendMessage(coor.toString());
	}
	
	private void getShotAt() throws ClassNotFoundException, IOException, InterruptedException {
		// Get the opponents coordinates
		oppCoor = socket.getMessage(); 
		// Add opponents shot to opponent's shot list. 
		oppShotList.add(oppCoor);
		oppStatus = oppCoor.getStatus();
		// Update the opponent's board if opponent hasn't already shot there.
		if(checkIfOppShot(oppCoor)==false){
			updateOppBoard(oppStatus, coor);
		}
		// Check if the shot was a hit, miss, or lose and make that my next status.
		myStatus = checkFleet(oppCoor);
		// check if all ships have been destroyed
		if(getFleet().isEmpty()==true) myStatus = "L";
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
	private String checkFleet(Torpedo oppCoor){
	
	// Check if coordinate is a ship
		if (ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].getBackground()==Color.YELLOW){
			// Ships is in fleet, change color to red
			ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].setBackground(Color.RED);
			removeFromFleet(oppCoor.getX(),oppCoor.getY());
			return"H";
		}
		else {	
			// Shot missed, change color to blue
			ocean.getMyButton()[oppCoor.getX()][oppCoor.getY()].setBackground(Color.BLUE);	
		}
		return "M";
	}
	
	// Remove the ship with matching coordinates from the fleet.
	private void removeFromFleet(int x, int y) {
		for (int i = 0; i<fleet.size(); i++){
			if (fleet.get(i).getX() == x
					&& 
					fleet.get(i).getY() == y){
				fleet.remove(i);
			}
		}
	}
	
	// Check if the coordinate has already been shot by me and return value
		private String checkIfIShot(Torpedo coor){
			// Check if coordinate has already been shot
			if(ocean.getOppButton()[coor.getX()][coor.getY()].getBackground()==Color.BLUE){
				return "M"; // Coordinate is a miss
			}
			else if(ocean.getOppButton()[coor.getX()][coor.getY()].getBackground()==Color.RED){
				return "H"; // Coordinate is a hit
			}
			return "N"; // Coordinate not shot
		}	
	
	// Check if the coordinate has already been shot by the opponent
	private boolean checkIfOppShot(Torpedo coor){
		// Check if coordinate has already been shot
		if(ocean.getMyButton()[coor.getX()][coor.getY()].getBackground()==Color.CYAN){
			return false; // Coordinate already shot
			}
		return true; // Coordinate not shot
	}
	
	// Update opponent's board.
	private void updateOppBoard(String oppStatus, Torpedo coor) throws InterruptedException {
		if (oppStatus.equalsIgnoreCase("H")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.RED);	}
		else if (oppStatus.equalsIgnoreCase("M")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.BLUE);	}
		else if (oppStatus.equalsIgnoreCase("L")){	ocean.getOppButton()[coor.getX()][coor.getY()].setBackground(Color.RED);	}
	}

	// Find the next place to shoot
	private Torpedo findNextShot(Torpedo coor, String status){
		// Get the coordinate to attack (2 DIFFERENT WAYS!  magicShots and randShot)
	
		// (If I get a hit and shipFound == false) or if (shipFound == true) do magicShots
		if ((shipFound == true)||((status.equalsIgnoreCase("H") && (shipFound == false)))){	
			coor = magicShots(coor, status);
		}	
		// Randomly generated coordinate
		else {	
			coor = randShot();
		}
		return coor;
	}
	
	// Reset the values of the magicShot method
	private void specialReset() {
		direction = 1;
		shipFound = false;
		right = false;
		down = false;
		left = false;
		up = false;
		timesShotSpecially = 0;
		hits = 0;
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
		// If a ship has just been found
		if (shipFound == false && status.equalsIgnoreCase("H")){
			direction = 1;
			specialReset();
			hits = 1;
			shipFound = true;
			// the initial shot that was a Hit
			specialShot = new Torpedo(coor.getX(), coor.getY()); 
		}	
		// The calculations for the next shot are always set relative to the initial shot 
		next = new Torpedo(specialShot.getX(),specialShot.getY()); 
		// Shoot in all directions to decide where to shoot next
		if (timesShotSpecially<5 && right == false && left == false && down == false && up == false){
			switch(timesShotSpecially){
			case 0: // Shoot right of initial
				next.setX(specialShot.getX()+1);
				timesShotSpecially++;
				return next;
			case 1:	// shoot down from initial
				if (status.equalsIgnoreCase("H")){	 
					right = true;
					hits++;
					
				}
				next.setY(specialShot.getY()+1);
				timesShotSpecially++;
				return next;
			case 2: // Shoot left of initial
				if(status.equalsIgnoreCase("H")){	
					down = true;
					hits++;
				}
				next.setX(specialShot.getX()-1);
				timesShotSpecially++;
				return next;
			case 3: // Shoot up from initial
				if (status.equalsIgnoreCase("H")){	
					left = true;
					hits++;
				}
				next.setY(specialShot.getY()-1);
				timesShotSpecially++;
				return next;
			case 4:
				if(status.equalsIgnoreCase("H")){	
					up = true;
					hits++;
				}
				timesShotSpecially++;
			}
		}
		else{
			if (hits >=6){	
				specialReset();	// ship has definitely been sunk.
				next = randShot();
			} 
			// Determine what ship has been found and shoot accordingly.
			else { // ship has not yet been sunk
				if (right == true || down == true || left == true || up == true){
					// just right
					if (right == true){
						if(hits<6){
							if(hits>=2){
								if(status.equalsIgnoreCase("H")){	hits++;	} // if hit then increment
								else{
									direction *= (-1); // change direction
									coor.setX(specialShot.getX());
								}
							}
							next.setX(coor.getX()+direction);
							return next;
						}
					}
					// just down
					else if (down == true){
						if(hits<6){
							if(hits>=2){
								if(status.equalsIgnoreCase("H")){	hits++;	} // if hit then increment
								else{
									direction *= (-1); // change direction
									coor.setY(specialShot.getY());
								}
							}
							next.setY(coor.getY()+direction);
							return next;
						}
					}
					// just left
					else if (left == true){
						if(hits<6){
							if(hits>=2){
								if(status.equalsIgnoreCase("H")){	hits++;	} // if hit then increment
								else{
									direction *= (-1); // change direction
									coor.setX(specialShot.getX());
								}
							}
							next.setX(coor.getX()+direction);
							return next;
						}
					}
					// just up
					else if (up == true){
						if(hits<6){
							if(hits>=2){
								if(status.equalsIgnoreCase("H")){	hits++;	} // if hit then increment
								else{
									direction *= (-1); // change direction
									coor.setY(specialShot.getY());
								}
							}
							next.setY(coor.getY()+direction);
							return next;
						}
					}
					else {
						specialReset();
						next = randShot(); // randomly generated coordinate
					}
				}
			}
			
		}
		return next;
	}

	public static ArrayList<ship> getFleet() {
		return fleet;
	}
	

	public void setFleet(ArrayList<ship> fleet) {
		ComvCom.fleet = fleet;
	}

	@Override
	public void run() {
		try {
			run("localhost", 13000);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
