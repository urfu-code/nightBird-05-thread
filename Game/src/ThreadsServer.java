import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

public class ThreadsServer extends Close implements Runnable  {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private PrintableWood m_wood;
	private Request m_request;
	private Response m_response;
	private LinkedList <Point> points= new LinkedList<Point>();
	private Synchronizer synchronizer;

	public  ThreadsServer(Socket s, PrintableWood wood, LinkedList <Point> p, Synchronizer sync) throws IOException {
		socket = s;
		m_wood = wood;
		points = p;
		synchronizer = sync;
		}

	@Override
	public void run() {
		Action currentAction = Action.Ok;
		Random random = new Random();
		try {
			in = new ObjectInputStream(socket.getInputStream());



			while ((currentAction != Action.Finish) && (currentAction != Action.WoodmanNotFound)) {
				synchronized(synchronizer) {
					synchronizer.wait();
				}
				m_request = (Request) in.readObject();

				switch (m_request.GetMethod()) {

				case "CreateWoodman" :
					m_wood.createWoodman(m_request.GetName(), points.get(Math.abs(random.nextInt(points.size()))), points.get(Math.abs(random.nextInt(points.size()))));				

				case "MoveWoodman" :
					currentAction = m_wood.move(m_request.GetName(), m_request.GetDirection());
					out = new ObjectOutputStream(socket.getOutputStream());
					m_response = new Response(currentAction);
					out.writeObject(m_response);
					out.flush();					 							
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
