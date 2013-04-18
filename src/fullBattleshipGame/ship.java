package fullBattleshipGame;

public class ship extends oceanObject{
		oceanObject ship;
		
	public ship() {
		super("ship",0,0,1);
	}
	
	public ship(String n, int x, int y, int s){
		super(n,x,y,s);
	}
}
