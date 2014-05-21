import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class ThreadsServer extends Close implements Runnable  {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private PrintableWood m_wood;
	private Request m_request;
	private Response m_response;
	private volatile HashMap <Integer, Thread> clientsList;
	private LinkedList <Point> points= new LinkedList<Point>();
	private Integer threadsID;

	public  ThreadsServer(Socket s, PrintableWood wood, HashMap <Integer, Thread> clients, LinkedList <Point> p, Integer threadID) throws IOException {
		socket = s;
		m_wood = wood;
		clientsList = clients;
		points = p;
		threadsID = threadID;
	}
<<<<<<< HEAD
=======

>>>>>>> 897ae09cc9a1c834c56c31cfb03f735d70c76e76

	@Override
	public void run() {
		Action currentAction = Action.Ok;
		Random random = new Random();
		try {
			in = new ObjectInputStream(socket.getInputStream());

			while ((currentAction != Action.Finish) && (currentAction != Action.WoodmanNotFound)) {
				m_request = (Request) in.readObject();

				switch (m_request.GetMethod()) {

				case "CreateWoodman" :
				{
					synchronized(m_wood) {
						m_wood.createWoodman(m_request.GetName(), points.get(Math.abs(random.nextInt(points.size()))), points.get(Math.abs(random.nextInt(points.size()))));				
					}
					break;
				}

				case "MoveWoodman" :
				{
					synchronized (clientsList.get(threadsID)) {
						m_response = new Response(currentAction);
						clientsList.get(threadsID).wait();
					}
					synchronized (m_wood) {
						currentAction = m_wood.move(m_request.GetName(), m_request.GetDirection());
						if (currentAction == Action.WoodmanNotFound) {
							System.out.println("Woodman not found");
						}
						if (currentAction == Action.Finish) {
							System.out.println("You reached finish");
						}
					}
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(m_response);
					out.flush();
					break;
				}				 							
				}
				synchronized (clientsList) {
					if ((currentAction == Action.WoodmanNotFound) || (currentAction == Action.Finish)) {
						clientsList.remove(threadsID);
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			tryClose(in);
			tryClose(out);
			tryClose(socket);
		}
	}
}
