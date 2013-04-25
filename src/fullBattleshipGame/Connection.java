package fullBattleshipGame;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
	private ServerSocket		providerSocket;
	private Socket				connection;
	private PrintWriter	out;
	private BufferedReader	in;
	private String				message;
	private boolean 			connected = false;
	private String 				ip = "localhost";
	private int 				port = 13000;

        // Constructor
	Connection(String i, int p) throws Throwable, Throwable{
		ip = i;
		port = p;
		run();
	}
	
	private void run() throws UnknownHostException, IOException{
		if (ip == null){
			
			// if server then
			providerSocket = new ServerSocket(port);
			System.out.println("Waiting for a connection...");
			connection = providerSocket.accept();
			
		}
		else{
			// if client then
			connection = new Socket(ip,port);
			}
		
		// When Connected
		String opponent = connection.getInetAddress()+", "+connection.getPort();
		
		System.out.println("Connected to "+opponent);
		
		setConnected(true);
		history.addConnection(opponent);
		//Input/Output streams
		
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		out = new PrintWriter(connection.getOutputStream(), true);
		out.flush();
	}

	// Send a message to opponent
	void sendMessage(String msg) throws IOException{
		out.println(msg);
		out.flush();
		System.out.println("Me> "+msg.toString());
	}

	// Get a message from the opponent
	public Torpedo getMessage() throws ClassNotFoundException, IOException{
		Torpedo coor = new Torpedo();
		message = (String)in.readLine();
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
