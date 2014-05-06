import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Client extends Close {

	private static Socket socket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	public static void main(String[] args) throws Exception{
		System.out.println("Game started");	
		Response m_response;
		Request m_request;
		Random random = new Random();
		StringBuffer name = new StringBuffer();

		socket = new Socket("localhost", 17826);			
		out = new ObjectOutputStream(socket.getOutputStream());

		for (int i = 0; i < 6; i++) {
			name.append((char)Math.abs(random.nextInt(128)));
		}

		m_request = new Request(name.toString());
		MyMouse Minni = new MyMouse(name.toString());  
		out.writeObject(m_request);
		out.flush();
		Action currentAction = Action.Ok;
		try {
			while ((currentAction != Action.Finish) && (currentAction != Action.WoodmanNotFound)) {
				Direction direction = Minni.NextMove(currentAction);
				Request message = new Request(name.toString(), direction);
				out.writeObject(message);
				out.flush();
				in = new ObjectInputStream(socket.getInputStream());
				m_response = (Response) in.readObject();
				currentAction = m_response.GetAction();

				
			}
			if (currentAction == Action.WoodmanNotFound) {
					System.out.println("Woodman not found");
				} 

				if (currentAction == Action.Finish) {
					System.out.println("You reached finish");
				}
			System.out.println("Game over");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			tryClose(socket);
			tryClose(out);
			tryClose(in);
		}
	}


}
