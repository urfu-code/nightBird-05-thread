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
		Response m_response;
		Request m_request;
		StringBuffer name = new StringBuffer();
		Random random = new Random();
		try {
			for (int i = 0; i < 6; i++) {
				name.append((char) Math.abs(random.nextInt(128)));
			}
			
			socket = new Socket("localhost", 17376);
			out = new ObjectOutputStream(socket.getOutputStream());
			m_request = new Request(name.toString());
			MyMouse Minni = new MyMouse(name.toString());	 
			out.writeObject(m_request);
			out.flush();
			Action currentAction = Action.Ok;
			while ((currentAction != Action.Finish) && (currentAction != Action.WoodmanNotFound)) {
				Direction direction = Minni.NextMove(currentAction);
				Request message = new Request(Minni.GetName(), direction);
				out.writeObject(message);
				out.flush();
				in =  new ObjectInputStream(socket.getInputStream());
				m_response = (Response) in.readObject();
				currentAction = m_response.GetAction();	
			}
			if (currentAction == Action.WoodmanNotFound) {
				System.out.println("You didn't reach finish");
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
