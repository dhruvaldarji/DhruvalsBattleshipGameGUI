package fullBattleshipGame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	public boolean selectingShips = false;
	
	board(int x, int y) throws Throwable{
		boardX = x;
		boardY = y;
		frame = new JFrame();
		frame.setVisible(false);
		frame.setBounds(0, 0, 1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		myPanel.setBackground(Color.BLUE); 
		
		oppPanel.setLayout(panelLayout);
		oppPanel.setBackground(Color.BLUE); 
		
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
				System.exit(0);
			} catch (Throwable e1) {}
		}else if(selectingShips==true){
			JButton b = (JButton) e.getSource();
			b.setBackground(Color.YELLOW);
			String[] shipCoor = b.getName().trim().split(",");
			int x = Integer.parseInt(shipCoor[1]);
			int y = Integer.parseInt(shipCoor[2]);
			largeBattleship ship = new largeBattleship(x,y);
			
			switch (Gui.getGameType()){
			case 1:
				if (ComvCom.searchFleet(ship)==false){
					ComvCom.getFleet().add(ship);
				Gui.println(ComvCom.getFleet().size());
				}
				break;
			case 2:
				if (PvCom.searchFleet(ship)==false){
					PvCom.getFleet().add(ship);
				Gui.println(PvCom.getFleet().size());
				}
				break;
			case 3:
				if (PvP.searchFleet(ship)==false){
					PvP.getFleet().add(ship);
				Gui.println(PvP.getFleet().size());
				}
				break;
			}
		}
		else if(PvCom.isBattling() == true){
			JButton b = (JButton) e.getSource();
			String[] messageCoor = b.getName().trim().split(",");
			int x = Integer.parseInt(messageCoor[1]);
			int y = Integer.parseInt(messageCoor[2]);
			Torpedo coor = new Torpedo("O",x,y);
			PvCom.getMyShotList().add(coor);
			PvCom.checkOppFleet(coor);
			enableOppBoard(false);
			PvCom.setShooting(false);
		}
		revalidate();
		repaint();
	}

//	make and add my buttons
	void addMyButtons(){
		 setMyButton(new JButton[boardY][boardX]);
		 for (int row = 0; row < boardY; row++){
	        	for (int col = 0; col < boardX; col++){
	        		// creates a new button. 
	        		getMyButton()[col][row] = new JButton("");
	        		getMyButton()[col][row].setName("Me,"+col+","+row);
	        		getMyButton()[col][row].setBackground(Color.CYAN);
	        		if (Gui.getGameType()==1){
	        			getMyButton()[col][row].setBorderPainted(false);
	        		}
	        		else{
	        			getMyButton()[col][row].setBorderPainted(true);
	        		}
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
	        		getOppButton()[col][row] = new JButton("");
	        		getOppButton()[col][row].setName("Opp,"+col+","+row);
	        		getOppButton()[col][row].setBackground(Color.CYAN);
	        		if (Gui.getGameType()==1){
	        			getOppButton()[col][row].setBorderPainted(false);
	        		}
	        		else{
	        			getOppButton()[col][row].setBorderPainted(true);
	        		}
	                getOppButton()[col][row].setLocation(col, row);
	                getOppButton()[col][row].addActionListener(this);
	                oppPanel.add(getOppButton()[col][row]); // Add the button to the frame.
	            }
	        }
		}


	public static JFrame getFrame() {
		return frame;
	}

	public static void setFrame(JFrame frame) {
		board.frame = frame;
	}

	public JPanel getMyPanel() {
		return myPanel;
	}

	public static void setMyPanel(JPanel myPanel) {
		board.myPanel = myPanel;
	}

	public JPanel getOppPanel() {
		return oppPanel;
	}

	public static void setOppPanel(JPanel oppPanel) {
		board.oppPanel = oppPanel;
	}

	public JButton[][] getMyButton() {
		return myButton;
	}

	public static void setMyButton(JButton[][] myButton) {
		board.myButton = myButton;
	}

	public JButton[][] getOppButton() {
		return oppButton;
	}

	public static void setOppButton(JButton[][] oppButton) {
		board.oppButton = oppButton;
	}
	
	public void setVisible(boolean b){	frame.setVisible(b);	}
	
	public boolean isVisible(){	return frame.isVisible();	}
	
	public void setBound(int x, int y){	frame.setLocation(x,y);	}

	public void enableMyBoard(Boolean b) {
		for (int row = 0; row < boardX; row++){
        	for (int col = 0; col < boardY; col++){
        		// enable buttons. 
        		getMyButton()[col][row].setEnabled(b);
        	}
		}
	}
	
	public void enableOppBoard(Boolean b) {
		for (int row = 0; row < boardX; row++){
        	for (int col = 0; col < boardY; col++){
        		// enable or disable buttons.
        		if(b == true){
        			if (getOppButton()[col][row].getBackground() == (Color.CYAN)
        					||
        					getOppButton()[col][row].getBackground() == (Color.YELLOW)){
        				getOppButton()[col][row].setEnabled(true);
        			}
        		}
        		else{
        			getOppButton()[col][row].setEnabled(false);
        		}
        	}
		}
	}
}
