package thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import server.MessageClient;
import server.MessageServer;
import mouse.My_Mouse;
import wood.Action;
import wood.Direction;

public class Client{
		
	private static Socket socket;
	private static ObjectInputStream inStream;
	private static ObjectOutputStream outStream;

	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Input the name mouse:\n");
		String name = scanner.nextLine();
		My_Mouse mouse = new My_Mouse();
		try{
			socket = new Socket("localhost", 25436);
			System.out.println("Всё создал\n");
			outStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream())); 
			MessageServer messageServer = new MessageServer("createWoodman", name);
			outStream.writeObject(messageServer);
			outStream.flush();
			inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			messageServer = (MessageServer)inStream.readObject();
			Action action = Action.Ok;
			while (!(action == Action.WoodmanNotFound) && !(action == Action.Finish)) {
				Direction direction = mouse.NextMove(action);
				MessageServer message = new MessageServer("move", name.toString(), direction);
				outStream.writeObject(message);
				outStream.flush();				
				MessageClient messageClient = (MessageClient) inStream.readObject();
				action = messageClient.getAction();
			}
		}catch (IOException e) {
			//e.printStackTrace();
		}catch(ClassNotFoundException ex){
				//ex.printStackTrace();
		}
		finally{
			System.out.println("connection is broken");
			closeObject(socket);	
			closeObject(outStream);
			closeObject(inStream);
		}
	}
	
	public static void closeObject(Closeable object){
		if (object != null){
			try{
				object.close();
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
