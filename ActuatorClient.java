import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class ActuatorClient {
	private static Socket sock;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;

	public static void main(String[] args) throws IOException {
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		System.out.println("Input the name");
		String name = sc.nextLine();
		Mouse norushka = new Mouse(name);

		sock = new Socket("localhost", 32015);
		oos = new ObjectOutputStream(new BufferedOutputStream(sock.getOutputStream()));

		MessageToServer toSend = new MessageToServer("createWoodman", name);
		oos.writeObject(toSend);
		oos.flush();

		Direction latestDirection = norushka.NextMove(Action.Ok);	
		Direction newDirection = Direction.None;
		Action newAction = Action.Ok;

		while (!(newAction == Action.WoodmanNotFound) && !(newAction == Action.Finish)) {
			MessageToServer moveItForMe = new MessageToServer("move", name, latestDirection);
			oos.writeObject(moveItForMe);
			oos.flush();
			
			ois = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
			try {
				MessageToClient recieved = (MessageToClient) ois.readObject();
				newAction = recieved.getAction();
				newDirection = norushka.NextMove(newAction);
				latestDirection = newDirection;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		if (newAction == Action.WoodmanNotFound)
			System.out.println(name + " is dead");
		if (newAction == Action.Finish)
			System.out.println(name + " finished!");

		try {				
			ois.close();
			oos.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
