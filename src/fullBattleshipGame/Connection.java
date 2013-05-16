package fullBattleshipGame;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {
	private ServerSocket		providerSocket;
	private Socket				connection;
	private ObjectOutputStream	out;
	private ObjectInputStream	in;
	private String				message;
	private boolean 			connected = false;
	private String 				ip = "localhost";
	private int 				port = 13000;
	private String 				opponent = "";
	public history h = new history();
	private int hostClient = 0; // host == 1 or client == 2

	// Host Constructor
	Connection(int p) throws Throwable, Throwable{
		port = p;
		hostClient = 1;
	}
	// Client Constructor
	Connection(String i, int p) throws Throwable, Throwable{
		ip = i;
		port = p;
		hostClient = 2;
	}
	
	public void run() throws Exception{
		connected = false;
		if (hostClient == 1){
			// if server then
			providerSocket = new ServerSocket(port);
			Gui.println("Waiting for a connection...");
			this.connection = providerSocket.accept();
			
		}
		if (hostClient == 2){
			// if client then
			this.connection = new Socket(ip,port);
		}
		//Input/Output streams
		this.in = new ObjectInputStream(this.connection.getInputStream());
		this.out = new ObjectOutputStream(this.connection.getOutputStream());
		this.out.flush();
		
		// When Connected
		Gui.println("Connected to "+getOppConnectionInfo());
		h.addConnection(opponent);
		
		setConnected(true);
	}

	// Send a message to opponent
	void sendMessage(String msg) throws IOException{
		this.out.writeObject(msg);
		this.out.flush();
		System.out.println("Me> "+msg.toString());
	}

	// Get a message from the opponent
	public Torpedo getMessage() throws ClassNotFoundException, IOException{
		Torpedo coor = new Torpedo();
		message = (String)this.in.readObject();
		coor = convert(message);
		if (coor.getStatus().equalsIgnoreCase("H")){	System.out.println("Opp> "+coor.toString()+"----------HIT!----------");	}
		else System.out.println("Opp> "+coor.toString());
		return coor;
	}

	// Converts the message into a coordinate (Torpedo) object
	static Torpedo convert(String message) {
		Torpedo coor = new Torpedo();
		String[] tokens = message.trim().split(",");
		
		String s = tokens[0];
		int x = Integer.parseInt(tokens[1]);
		int y = Integer.parseInt(tokens[2]);
		// if more data is added to the protocol, it can go here
		
		coor = new Torpedo(s,x,y);
		return coor;		
	}
	
	public String getMyConnectionInfo(){
		return this.connection.getLocalAddress()+", "+this.connection.getLocalPort();
	}
	
	public String getOppConnectionInfo(){
		return this.connection.getInetAddress()+", "+this.connection.getPort();
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	void close() throws Throwable{
		setConnected(false);
		this.in.close();
		this.out.close();
		this.connection.close();
	}
}
