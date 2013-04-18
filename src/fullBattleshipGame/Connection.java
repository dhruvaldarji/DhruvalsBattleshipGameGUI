package fullBattleshipGame;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Connection {
	static ServerSocket			providerSocket;
	static Socket				connection;
	static ObjectOutputStream	out;
	static ObjectInputStream	in;
	static String				message;
	static Torpedo				torpedo;
	static int					x, y;
	static String				status = "m";
	static boolean 				connected = false;
	static String 				ip = "localhost";
	static int 					port = 13000;

        // Constructor
	Connection(String i, int p) throws Throwable, Throwable{
		ip = i;
		port = p;
		run();
	}
	
	void run() throws UnknownHostException, IOException{
		if (ip != null){
			// if client then
			connection = new Socket(ip,port);
//			connection = new Socket("localhost", 13000);
//			connection = new Socket("jv.endofinternet.net", 13000);
		}
		else{
			// if server then
			providerSocket = new ServerSocket(port);
			System.out.println("Waiting for connection");
			connection = providerSocket.accept();
			}
		
		// protocol: H M or L, x, y
		String opponent = connection.getInetAddress()+", "+connection.getPort();
		
		System.out.println("Connected to "+opponent);
		
		connected = true;
		history.addConnection(opponent);
		//Input and Output streams
		out = new ObjectOutputStream(connection.getOutputStream());
		out.flush();
		//Input and Output streams
		in = new ObjectInputStream(connection.getInputStream());	
	}

	void sendMessage(String msg) throws IOException{
		out.writeObject(msg);
		out.flush();
		System.out.println("Me> "+msg.toString());
	}

	public Torpedo getMessage() throws ClassNotFoundException, IOException{
		Torpedo coor = new Torpedo();
		message = (String)in.readObject();
		coor = convert(message);
		if (coor.getStatus().equalsIgnoreCase("h")){	System.out.println("Opp> "+coor.toString()+"----------HIT!----------");	}
		else System.out.println("server> "+coor.toString());
		return coor;
	}

	static Torpedo convert(String message) {
		Torpedo coor = new Torpedo();
		ArrayList<Object> info = new ArrayList<Object>();
		String[] tokens = message.trim().split(",");
		for (String t : tokens)
			info.add(t);
		String s = tokens[0];
		int x = Integer.parseInt(tokens[1]);
		int y = Integer.parseInt(tokens[2]);
		coor = new Torpedo(s,x,y);
		return coor;		
	}
	
	void close() throws Throwable{
		connected = false;
		in.close();
		out.close();
		connection.close();
	}
}
