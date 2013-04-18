package fullBattleshipGame;

public class oceanObject{
	
	private int xPos= 0, yPos = 0, size = 0;
	private String name = null;
	
	// Skeleton oceanObject
	public oceanObject(){
		name = "oceanObject";
		xPos = 0;
		yPos = 0;
		size = 0;
	}
	
	// Standard oceanObject
	public oceanObject(String n, int x, int y, int s){
		name = n;
		xPos = x;
		yPos = y;
		size = s;
	}
	
	// oceanObject of size 1
	public oceanObject(String n, int x, int y){
		name = n;
		xPos = x;
		yPos = y;
		size = 1;
	}
	
	// Returns the name of the oceanObject
	public int getX() {
		return xPos;
	}
	
	// Sets the x of the oceanObject
	public void setX(int x){
		xPos = x;
	}
	
	// Returns the y of the oceanObject
	public int getY() {
		return yPos;
	}
	
	// Sets the y of the oceanObject
	public void setY(int y){
		yPos = y;
	}
	
	// Sets the x and y of the oceanObject
	public void setLocation(int x, int y){
		xPos = x;
		yPos = y;
	}
	
	// Sets the size of the oceanObject
	public int getSize(){
		return size;
	}
	
	// Sets the size of the oceanObject
	public void setSize(int s){
		size = s;
	}
	
	// Returns a String representation of oceanObject
	public String toString(){
		String values;
		values ="\nName is: "+name+
				"\nX is: "+xPos+
				"\nY is: "+yPos+
				"\nSize is: "+size;
		return values;
	}
}
