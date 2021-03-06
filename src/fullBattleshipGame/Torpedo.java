package fullBattleshipGame;

public class Torpedo {
	private int x, y;
	private String status;
	public Torpedo(){
		status = "m";
		x=-1;
		y=-1;
	}
	public Torpedo(String s, int torpedoX, int torpedoY){
		status = s;
		x = torpedoX;
		y = torpedoY;
	}
	
	public Torpedo(int torpedoX, int torpedoY){
		x = torpedoX;
		y = torpedoY;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String s) {
		status = s;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int coorX) {
		x = coorX;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int coorY) {
		y = coorY;
	}
	
	public String toString(){
		return status+","+x+","+y;
	}
	
	public boolean equals(Torpedo coor){
		if (this.getX()==coor.getX()
				&& this.getY()==coor.getY()){
			return true;
		}
		return false;
	}
	
	public boolean equals(oceanObject o){
		if (this.getX()==o.getX()
				&& this.getY()==o.getY()){
			return true;
		}
		return false;
	}
}
