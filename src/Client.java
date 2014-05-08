import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private static Socket socket;
	private static ObjectInputStream in_stream;
	private static ObjectOutputStream out_stream;

	public static void main(String[] args) throws Exception{
		
		try{ 
			Scanner scanner = new Scanner(System.in);
			System.out.println("Input the name mouse:\n");
			String name = scanner.nextLine();
			MyMouse mouse = new MyMouse();	
			socket = new Socket("localhost", 25436);
			out_stream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));;
			
			MessageServer messageServer = new MessageServer("createWoodman", name);
			out_stream.writeObject(messageServer);
			out_stream.flush();
			
			Action action = Action.Ok;
			
			while (true) {
				
				if ((action == Action.Finish) && (action == Action.WoodmanNotFound)){
					break;
				}
				
				Direction direction = mouse.NextMove(action);
				MessageServer message = new MessageServer("move", name, direction);
				out_stream.writeObject(message);
				out_stream.flush();
				
				in_stream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				try {				
					MessageClient messageClient = (MessageClient) in_stream.readObject();
					action = messageClient.getAction();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			if (action == Action.Finish) {
				System.out.println(name+" came to the Finish!");
			}
			if (action == Action.WoodmanNotFound) {
				System.out.println(name+"'s dead.");
			}
			scanner.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			System.out.println("connection is broken");
			closeObject(socket);	
			closeObject(out_stream);
			closeObject(in_stream);
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