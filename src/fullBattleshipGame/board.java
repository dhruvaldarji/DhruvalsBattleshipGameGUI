package fullBattleshipGame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class board extends JFrame implements ActionListener{
	private static int boardX = 100, boardY = 100;
	private static JFrame frame;
	private static JPanel myPanel, oppPanel, gamePanel;
	private static JButton[][] myButton, oppButton;
	boolean selectingShips = false;
	private largeBattleship s = null;
	
	board(int x, int y) throws Throwable{
		boardX = x;
		boardY = y;
		frame = new JFrame();
		frame.setVisible(false);
		frame.setBounds(0, 0, 1000, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		try	{
		      UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
		    }	catch (Exception localException) {}
			
		
		gamePanel = new JPanel();
		myPanel = new JPanel();
		oppPanel = new JPanel();
		
		gamePanel.add(myPanel);
		gamePanel.add(oppPanel);
		frame.add(gamePanel);
		
		GridLayout frameLayout = new GridLayout(1, 1);
		GridLayout panelLayout = new GridLayout(boardX, boardY);
		gamePanel.setLayout(frameLayout);
		myPanel.setLayout(panelLayout);
		myPanel.setBackground(Color.blue); 
		
		oppPanel.setLayout(panelLayout);
		oppPanel.setBackground(Color.blue); 
		
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(this);		
		
		JPanel infoPanel = new JPanel();
		infoPanel.add(exitButton,BorderLayout.CENTER);
		frame.add(infoPanel,BorderLayout.SOUTH);
		
		// Create a button for each coordinate on the ocean. 
		addMyButtons();
		addOppButtons();
		
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String msg = e.getActionCommand();
		if(msg.equalsIgnoreCase("Exit")){
			try {
				frame.dispose();
			} catch (Throwable e1) {}
		}else if(selectingShips==true){
			if (msg.startsWith("m")){
				System.out.println("Button: "+msg);
				JButton b = (JButton) e.getSource();
				b.setBackground(Color.YELLOW);
				s = new largeBattleship(getButtonX(b),getButtonY(b));
				
				switch (Gui.gameType){
				case 1:
					ComvCom.fleet.add(s);
					System.out.println(ComvCom.fleet.size());
					break;
				case 2:
					PvCom.fleet.add(s);
					System.out.println(PvCom.fleet.size());
					break;
				case 3:
					PvP.fleet.add(s);
					System.out.println(PvP.fleet.size());
					break;
				}
			}
		}
	}

private int getButtonY(JButton b) {
		String msg = b.getActionCommand();
		ArrayList<Object> info = new ArrayList<Object>();
		String[] tokens = msg.trim().split(",");
		for (String t : tokens)
			info.add(t);
		int x = Integer.parseInt(tokens[1]);
		return x;
	}

private int getButtonX(JButton b) {
		String msg = b.getActionCommand();
		ArrayList<Object> info = new ArrayList<Object>();
		String[] tokens = msg.trim().split(",");
		for (String t : tokens)
			info.add(t);
		int y = Integer.parseInt(tokens[2]);
		return y;
	}

public static JFrame getFrame() {
		return frame;
	}

	public static void setFrame(JFrame frame) {
		board.frame = frame;
	}

	public static JPanel getMyPanel() {
		return myPanel;
	}

	public static void setMyPanel(JPanel myPanel) {
		board.myPanel = myPanel;
	}

	public static JPanel getOppPanel() {
		return oppPanel;
	}

	public static void setOppPanel(JPanel oppPanel) {
		board.oppPanel = oppPanel;
	}

	public static JButton[][] getMyButton() {
		return myButton;
	}

	public static void setMyButton(JButton[][] myButton) {
		board.myButton = myButton;
	}

	public static JButton[][] getOppButton() {
		return oppButton;
	}

	public static void setOppButton(JButton[][] oppButton) {
		board.oppButton = oppButton;
	}

//	make and add my buttons
	void addMyButtons(){
		 setMyButton(new JButton[boardY][boardX]);
		 for (int row = 0; row < boardY; row++){
	        	for (int col = 0; col < boardX; col++){
	        		// creates a new button. 
	        		getMyButton()[col][row] = new JButton("m,"+row+","+col);
	        		getMyButton()[col][row].setBackground(Color.cyan);
	        		getMyButton()[col][row].setBorderPainted(false);
	                getMyButton()[col][row].setLocation(col, row);
	                getMyButton()[col][row].addActionListener(this);
	                myPanel.add(getMyButton()[col][row]); // Add the button to the frame.
	                }
	        	}
		 }
	
//	make and add opp buttons
	void addOppButtons(){
		 setOppButton(new JButton[boardY][boardX]);
		 for (int row = 0; row < boardY; row++){
	        	for (int col = 0; col < boardX; col++){
	        		// creates a new button. 
	        		getOppButton()[col][row] = new JButton("o,"+row+","+col);
	        		getOppButton()[col][row].setBackground(Color.cyan);
	        		getOppButton()[col][row].setBorderPainted(false);
	                getOppButton()[col][row].setLocation(col, row);
	                getOppButton()[col][row].addActionListener(this);
	                oppPanel.add(getOppButton()[col][row]); // Add the button to the frame.
	                }
	        	}
		 }

	public void setVisible(boolean b){	frame.setVisible(b);	}
	
	public boolean isVisible(){	return frame.isVisible();	}
	
	public void setBound(int x, int y){	frame.setLocation(x,y);	}
	
	public int getBoundX() {	return (int)frame.getLocation().getX();	}
	
	public int getBoundY() {	return (int)frame.getLocation().getY();	}

	public void enableMyBoard(Boolean b) {
		for (int row = 0; row < boardY; row++){
        	for (int col = 0; col < boardX; col++){
        		// enable buttons. 
        		getMyButton()[col][row].setEnabled(b);
        	}
		}
	}
	
	public static void enableOppBoard(Boolean b) {
		for (int row = 0; row < boardY; row++){
        	for (int col = 0; col < boardX; col++){
        		// enable buttons. 
        		getOppButton()[col][row].setEnabled(b);
        	}
		}
	}
	
	public static void disableMyButton(JButton button) {
		getOppButton()[button.getX()][button.getY()].setEnabled(false);
		System.out.print(button.getActionCommand());
	}
	
	public static void disableOppButton(JButton button) {
		getOppButton()[button.getX()][button.getY()].setEnabled(false);
	}
	
	static JButton convert(String msg) {
		ArrayList<Object> info = new ArrayList<Object>();
		String[] tokens = msg.trim().split(",");
		for (String t : tokens)
			info.add(t);
		String s = tokens[0];
		int x = Integer.parseInt(tokens[1]);
		int y = Integer.parseInt(tokens[2]);
		if (s.equalsIgnoreCase("m")){
			JButton btn = getMyButton()[y][x];
			return btn;
		}
		else{
			JButton btn = getOppButton()[y][x];
			return btn;		
		}
	}

	public void setBoard(int x, int y) {
		boardX = x;
		boardY = y;
	}

}
