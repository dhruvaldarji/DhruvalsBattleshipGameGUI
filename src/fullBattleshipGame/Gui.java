package fullBattleshipGame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Gui extends JFrame implements ActionListener, WindowListener{

	private static final long serialVersionUID = 1L;
	
	private JPanel guiPanel, mainPanel, gamePanel, choicePanel, hostPanel, joinPanel, startPanel, messagePanel;
	private JTextField hostPort, joinPort, server;
	static JTextArea console;
	private String s = "localhost";
	private int p = 13000;
	private static int gameType = 0; // if 1 then ComvCom, if 2 then PvCom, if 3 then pvp
	private int hostOrClient = 0; // if 1 then host , if 2 then client
	private boolean start = false, isPvCom = false;
	private final int guiX = 425, guiY = 250;
	private static Gui thisWindow;
	
	Gui() throws Throwable{
		super("Dhruval's Battleship Game");
		try	{
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }	catch (Exception localException) {}
		mainScreen();
		gameChoice();
		gameTypeScreen();
		joinScreen();
		hostScreen();	
		PvComStartScreen();
		messageBox();		
		setLayout(new BorderLayout());
		guiPanel = new JPanel();
		// The first menu the user sees
		guiPanel.add(mainPanel);
		add(guiPanel, BorderLayout.NORTH);
		add(messagePanel, BorderLayout.CENTER);
		setVisible(true);
		setSize(guiX,guiY);
		setThisWindow(this);
		addWindowListener(this);
		// set Exit solution
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Start running once the Gui has been created.
		play();
	}
	
	private void play() throws Throwable{
		while (start != true){
			if(start == true){
				StartGame();
				play(); // -------------------------------------------------------------Recursion
			}
		}
		start = false;
		revalidate();
		repaint();
	}

	private void mainScreen(){
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		
		JButton battle = new JButton("Battle");
		JButton config = new JButton("Config");
		JButton exitButton = new JButton("Exit");
		
		battle.addActionListener(this);
		config.addActionListener(this);
		exitButton.addActionListener(this);	
		
		config.setEnabled(false); // Under Construction
		
		mainPanel.add(battle);
		mainPanel.add(config);
		mainPanel.add(exitButton);
		
	}
	
	private void gameTypeScreen(){
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
		
		com.setEnabled(true);
		pvc.setEnabled(true); 
		pvp.setEnabled(false); // Under Construction
		
		gamePanel.add(com);
		gamePanel.add(pvc);
		gamePanel.add(pvp);
		gamePanel.add(menu);
	}
	
	private void gameChoice(){
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
	
	private void hostScreen(){
		hostPanel = new JPanel();
		hostPanel.setLayout(new FlowLayout());
		
		hostPort = new JTextField("13000");
		JButton create = new JButton("Host A Game");
		JButton hostJoin = new JButton("Host/Join Menu");
		
		hostPort.setBorder(new TitledBorder(new EtchedBorder(), "Port"));
		
		create.addActionListener(this);
		hostJoin.addActionListener(this);
		
		JPanel panel = new JPanel((new GridLayout(2,1)));
		panel.add(hostPort);
		panel.add(create);
		hostPanel.add(panel);
		hostPanel.add(hostJoin);
		
	}
	
	private void joinScreen(){
		joinPanel = new JPanel();
		joinPanel.setLayout(new FlowLayout());
		
		joinPort = new JTextField("13000");
		server = new JTextField("localhost");
		
		JButton join = new JButton("Join A Game");
		JButton hostJoin = new JButton("Host/Join Menu");
		
		joinPort.setBorder(new TitledBorder(new EtchedBorder(), "Port"));
		server.setBorder(new TitledBorder(new EtchedBorder(), "Server"));
		
		join.addActionListener(this);
		hostJoin.addActionListener(this);
		
		JPanel panel = new JPanel((new GridLayout(3,1)));
		panel.add(server);
		panel.add(joinPort);
		panel.add(join);
		joinPanel.add(panel);
		joinPanel.add(hostJoin);	
	}
	
	private void PvComStartScreen(){
		startPanel = new JPanel();
		startPanel.setLayout(new FlowLayout());
		
		JButton startPvCom = new JButton("Start a PvCom");
		JButton menu = new JButton("Menu");
		
		menu.addActionListener(this);
		startPvCom.addActionListener(this);
		
		startPanel.add(startPvCom);
		startPanel.add(menu);
	}
	
	private void messageBox(){
		messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());
		messagePanel.setBorder(new TitledBorder(new EtchedBorder(), "Headquarters"));
		console = new JTextArea("Welcome to Dhruval's Battleship Game\n");
		console.setEditable(false);
		messagePanel.add(new JScrollPane(console));
	}
	
	public static void print(Object message){
		console.append(message.toString());
		console.setCaretPosition(console.getText().length() - 1);
		getThisWindow().setAlwaysOnTop(true);
		getThisWindow().setAlwaysOnTop(false);		
	}
	
	public static void println(Object message){
		console.append(message.toString()+"\n");
		console.setCaretPosition(console.getText().length() - 1);
		getThisWindow().setAlwaysOnTop(true);
		getThisWindow().setAlwaysOnTop(false);	
	}
	
	public void actionPerformed(ActionEvent arg0) {
		String msg = arg0.getActionCommand();
		if(msg.equalsIgnoreCase("Battle")){
			guiPanel.removeAll();
			guiPanel.add(gamePanel);
		}
		else if (msg.equalsIgnoreCase("Config")){
			guiPanel.removeAll();
			guiPanel.add(gamePanel);
		}
		else if(msg.equalsIgnoreCase("Exit")){
			System.exit(0);
		}
		else if (msg.equalsIgnoreCase("Com vs. Com")){
			guiPanel.removeAll();
			guiPanel.add(choicePanel);
			setGameType(1);
		}
		else if (msg.equalsIgnoreCase("Player vs. Com")){
			guiPanel.removeAll();
			guiPanel.add(startPanel);
			setGameType(2);
		}
		else if (msg.equalsIgnoreCase("Player vs Player")){
			guiPanel.removeAll();
			guiPanel.add(choicePanel);
			setGameType(3);
		}
		else if (msg.equalsIgnoreCase("Menu")){
			guiPanel.removeAll();
			guiPanel.add(mainPanel);
		}
		else if (msg.equalsIgnoreCase("Host/Join Menu")){
			guiPanel.removeAll();
			guiPanel.add(choicePanel);
		}
		else if (msg.equalsIgnoreCase("Host")){
			hostOrClient = 1;
			guiPanel.removeAll();
			guiPanel.add(hostPanel);
		}
		else if (msg.equalsIgnoreCase("Join")){
			hostOrClient = 2;
			guiPanel.removeAll();
			guiPanel.add(joinPanel);
		}
		else if (msg.equalsIgnoreCase("Game Type Menu")){
			guiPanel.removeAll();
			guiPanel.add(gamePanel);
		}
		else if (msg.equalsIgnoreCase("Host A Game")){
			if(!hostPort.getText().equalsIgnoreCase(""))
				p = Integer.parseInt(hostPort.getText());
			hostPort.setText("");
			println(msg+": "+p);
			guiPanel.removeAll();
			guiPanel.add(mainPanel);
			start = true;
		}
		else if (msg.equalsIgnoreCase("Join a Game")){
			if(!joinPort.getText().equalsIgnoreCase(""))
				p = Integer.parseInt(joinPort.getText());
			if(!server.getText().equalsIgnoreCase("")) {
				s= server.getText();
			}
			server.setText("");
			joinPort.setText("");
			println(msg+": "+s+", "+p);
			guiPanel.removeAll();
			guiPanel.add(mainPanel);
			start = true;
		}
		else if(msg.equalsIgnoreCase("Start a PvCom")){
			guiPanel.removeAll();
			guiPanel.add(mainPanel);
			isPvCom = true;
			start = true;
		}
		revalidate();
		repaint();
	}
	
	private void StartGame() throws Throwable {
		if (start = true){
			if(isPvCom == true){
				inHostGame();
			}
			if (hostOrClient == 1){
				inHostGame();
			}
			else if (hostOrClient == 2){
				inClientGame();
			}
			
		}
	}

	private void inHostGame() throws Throwable {
		switch (getGameType()){
		case 1:
			start = false;
			ComvCom g1 = new ComvCom(p);
			g1.run();
			break;
		case 2:
			isPvCom = false;
			start = false;
			PvCom g2 = new PvCom();
			g2.run();
			break;
		case 3:
			start = false;
			PvP g3 = new PvP();
			g3.run(null, p);
			break;
		}
	}
	
	private void inClientGame() throws Throwable{
		switch (getGameType()){
		case 1:
			start = false;
			ComvCom g1 = new ComvCom(s,p);
			g1.run();
			break;
		case 2:
			isPvCom = false;
			start = false;
			PvCom g2 = new PvCom();
			g2.run();
			break;
		case 3:
			start = false;
			PvP g3 = new PvP();
			g3.run(s, p);
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

	public static int getGameType() {
		return gameType;
	}

	public static void setGameType(int gameType) {
		Gui.gameType = gameType;
	}

	public static Gui getThisWindow() {
		return thisWindow;
	}

	public void setThisWindow(Gui thisWindow) {
		Gui.thisWindow = thisWindow;
	}
}
