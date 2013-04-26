package fullBattleshipGame;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Gui extends JFrame implements ActionListener, WindowListener{

	private static final long serialVersionUID = 1L;
	
	JPanel mainPanel, gamePanel, choicePanel, hostPanel, joinPanel,startPanel;
	JTextField port, server;
	String s = "localhost";
	int p = 0;
	static int gameType = 0; // if 1 then ComvCom, if 2 then PvCom, if 3 then pvp
	int hostOrClient = 0; // if 1 then host , if 2 then client
	static Container c;

	boolean inGame = false;
	boolean start = false;
	
	Gui() throws Throwable{
		super("Dhruval's Battleship Game");
		try	{
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }	catch (Exception localException) {}
		
		setGui(getContentPane());
		setVisible(true);
		mainScreen();
		gameChoice();
		gameTypeScreen();
		joinScreen();
		hostScreen();	
		startScreen();
		
//		SplashScreen splash;
		
		// set Exit solution
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(this);
		setSize(500,155);
		
		play();
	}
	
	private void play() throws Throwable{
		while (start != true){
			if(start == true){
				StartGame();
			}
		}
		play(); // -------------------------------------------------------------Recursion
	}

	void mainScreen(){
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		
		JButton battle = new JButton("Battle");
		JButton config = new JButton("Config");
		JButton exitButton = new JButton("Exit");
		
		battle.addActionListener(this);
		config.addActionListener(this);
		exitButton.addActionListener(this);	
		
		mainPanel.add(battle);
		mainPanel.add(config);
		mainPanel.add(exitButton);
		
		getContentPane().add(mainPanel);
	}
	
	void gameTypeScreen(){
		gamePanel = new JPanel();
		gamePanel.setLayout(new FlowLayout());
		
		JButton com = new JButton("Com vs. Com");
		JButton pvc = new JButton("Player vs. Com");
		JButton pvp = new JButton("Player vs Player");
		JButton menu = new JButton("Menu");
		
		com.addActionListener(this);
		pvc.addActionListener(this);
		pvp.addActionListener(this);	
		menu.addActionListener(this);
		
		gamePanel.add(com);
		gamePanel.add(pvc);
		gamePanel.add(pvp);
		gamePanel.add(menu);
	}
	
	void gameChoice(){
		choicePanel = new JPanel();
		choicePanel.setLayout(new FlowLayout());
		
		JButton host = new JButton("Host");
		JButton join  = new JButton("Join");
		JButton gameTypeMenu = new JButton("Game Type Menu");
		
		
		host.addActionListener(this);
		join.addActionListener(this);
		gameTypeMenu.addActionListener(this);
		
		choicePanel.add(host);
		choicePanel.add(join);
		choicePanel.add(gameTypeMenu);
	}
	
	void hostScreen(){
		hostPanel = new JPanel();
		hostPanel.setLayout(new FlowLayout());
		
		port = new JTextField("");
		JButton create = new JButton("Host A Game");
		JButton hostJoin = new JButton("Host/Join Menu");
		
		port.setBorder(new TitledBorder(new EtchedBorder(), "Port"));
		
		create.addActionListener(this);
		hostJoin.addActionListener(this);
		
		JPanel panel = new JPanel((new GridLayout(2,1)));
		panel.add(port);
		panel.add(create);
		hostPanel.add(panel);
		hostPanel.add(hostJoin);
		
	}
	
	void joinScreen(){
		joinPanel = new JPanel();
		joinPanel.setLayout(new FlowLayout());
		
		server = new JTextField("");
		port = new JTextField("");
		JButton join = new JButton("Join a Game");
		JButton hostJoin = new JButton("Host/Join Menu");
		
		port.setBorder(new TitledBorder(new EtchedBorder(), "Port"));
		server.setBorder(new TitledBorder(new EtchedBorder(), "Server"));
		
		join.addActionListener(this);
		hostJoin.addActionListener(this);
		
		JPanel panel = new JPanel((new GridLayout(3,1)));
		
		panel.add(server);
		panel.add(port);
		panel.add(join);
		joinPanel.add(panel);
		joinPanel.add(hostJoin);
		
	}
	
	void startScreen(){
		startPanel = new JPanel();
		startPanel.setLayout(new FlowLayout());
		
		JButton startPvCom = new JButton("Start a PvCom");
		JButton menu = new JButton("Menu");
		menu.addActionListener(this);
		startPvCom.addActionListener(this);
		
		startPanel.add(startPvCom);
		startPanel.add(menu);
	}
	
	public void actionPerformed(ActionEvent arg0) {
				
		String msg = arg0.getActionCommand();
		if(msg.equalsIgnoreCase("Battle")){
			getContentPane().removeAll();
			getContentPane().add(gamePanel);
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Config")){
			getContentPane().removeAll();
			getContentPane().add(gamePanel);
			System.out.println(msg);
		}
		else if(msg.equalsIgnoreCase("Exit")){
			getContentPane().removeAll();
			System.exit(0);
		}
		else if (msg.equalsIgnoreCase("Com vs. Com")){
			getContentPane().removeAll();
			getContentPane().add(choicePanel);
			gameType = 1;
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Player vs. Com")){
			getContentPane().removeAll();
			getContentPane().add(startPanel);
			gameType = 2;
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Player vs Player")){
			getContentPane().removeAll();
			getContentPane().add(choicePanel);
			gameType = 3;
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Menu")){
			getContentPane().removeAll();
			getContentPane().add(mainPanel);
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Host/Join Menu")){
			getContentPane().removeAll();
			getContentPane().add(choicePanel);
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Host")){
			hostOrClient = 1;
			getContentPane().removeAll();
			getContentPane().add(hostPanel);
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Join")){
			hostOrClient = 2;
			getContentPane().removeAll();
			getContentPane().add(joinPanel);
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Game Type Menu")){
			getContentPane().removeAll();
			getContentPane().add(gamePanel);
			System.out.println(msg);
		}
		else if (msg.equalsIgnoreCase("Host A Game")){
			System.out.println(msg);
			hostOrClient = 1;
			if(!port.getText().equalsIgnoreCase(""))
				p = Integer.parseInt(port.getText());
			port.setText("");
			System.out.println(msg+": "+p);
			inGame = true;
			getContentPane().removeAll();
			getContentPane().add(mainPanel);
			start = true;
		}
		else if (msg.equalsIgnoreCase("Join a Game")){
			System.out.println(msg);
			hostOrClient = 2;
			if(!server.getText().equalsIgnoreCase("")) 
				s= server.getText();
			if(!(port.getText().equalsIgnoreCase(""))) 
				p = Integer.parseInt(port.getText());
			server.setText("");
			port.setText("");
			System.out.println(msg+": "+s+", "+p);
			getContentPane().removeAll();
			inGame = true;	
			getContentPane().removeAll();
			getContentPane().add(mainPanel);
			start = true;
		}
		else if(msg.equalsIgnoreCase("Start a PvCom")){
			
		}
		getContentPane().validate();
		getContentPane().repaint();
	}
	
	private void StartGame() throws Throwable {
		if (inGame = true){
			if (hostOrClient == 1){
				inHostGame();
			}
			if (hostOrClient == 2){
				inClientGame();
			}
		}
	}

	private void inHostGame() throws Throwable {
		switch (gameType){
		case 1:
			ComvCom g1 = new ComvCom();
			g1.run(null, p);
			start = false;
			inGame = false;
			break;
		case 2:
			PvCom g2 = new PvCom();
			g2.run(null, p);
			start = false;
			inGame = false;
			break;
		case 3:
			PvP g3 = new PvP();
			g3.run(null, p);
			start = false;
			inGame = false;
			break;
		}
	}
	
	private void inClientGame() throws Throwable{
		switch (gameType){
		case 1:
			ComvCom g1 = new ComvCom();
			g1.run(s, p);
			start = false;
			inGame = false;
			break;
		case 2:
			PvCom g2 = new PvCom();
			g2.run(s, p);
			start = false;
			inGame = false;
			break;
		case 3:
			PvP g3 = new PvP();
			g3.run(s, p);
			start = false;
			inGame = false;
			break;
		}
	}

	// I can ignore the other WindowListener methods
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {
		// dispose the frame
		dispose();
		System.exit(0);
	}

	public static Container getGui() {
		return c;
	}

	public void setGui(Container c) {
		Gui.c = c;
	}
	
}
