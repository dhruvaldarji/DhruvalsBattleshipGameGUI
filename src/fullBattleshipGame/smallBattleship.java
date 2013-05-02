package fullBattleshipGame;

public class smallBattleship extends ship {
	ship battleship;
	
	public smallBattleship(){
		super("smallBattleship",0,0,3);
	}
	
	public smallBattleship(int x, int y){
		super("smallBattleship",x,y,3);
	}
}