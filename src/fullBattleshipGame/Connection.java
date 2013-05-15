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
import java.net.UnknownHostException;

public class Connection {
	private ServerSocket		providerSocket;
	private Socket				connection;
	private static ObjectOutputStream	out;
	private static ObjectInputStream	in;
	private String				message;
	private boolean 			connected = false;
	private String 				ip = "localhost";
	private int 				port = 13000;
	private String 				opponent = "";
	public history h = new history();

        // Constructor
	Connection(String i, int p) throws Throwable, Throwable{
		ip = i;
		port = p;
	}
	
	public void run(){
		if (ip == null){
			// if server then
			try {
				providerSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Gui.println("Waiting for a connection...");
			try {
				connection = providerSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			// if client then
			try {
				connection = new Socket(ip,port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		
		// When Connected
		opponent = connection.getInetAddress()+", "+connection.getPort();
		
		Gui.println("Connected to "+opponent);
		
		setConnected(true);
		h.addConnection(opponent);
		//Input/Output streams
		
		try {
			in = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}	
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Send a message to opponent
	void sendMessage(String msg) throws IOException{
		out.writeObject(msg);
		out.flush();
		System.out.println("Me> "+msg.toString());
	}

	// Get a message from the opponent
	public Torpedo getMessage() throws ClassNotFoundException, IOException{
		Torpedo coor = new Torpedo();
		message = (String)in.readObject();
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
		return connection.getLocalAddress()+", "+connection.getLocalPort();
	}
	
	public String getOppConnectionInfo(){
		return connection.getInetAddress()+", "+connection.getPort();
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	void close() throws Throwable{
		setConnected(false);
		in.close();
		out.close();
		connection.close();
	}
}
