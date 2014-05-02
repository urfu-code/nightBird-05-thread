import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Handler implements Runnable {

	private Socket socket;
	private ArrayList<Point> starts;
	private ArrayList<Point> finishes;
	private PrintableWood pWood; 
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private volatile HashMap<Integer, Thread> listOfClients;
	private Integer threadID;
	
	public Handler(Socket socket, PrintableWood wood, ArrayList<Point> starts, ArrayList<Point> finishes, HashMap<Integer, Thread> listOfClients, Integer threadID) {
		this.socket = socket;
		this.pWood = wood;
		this.starts = starts;
		this.finishes = finishes;
		this.listOfClients = listOfClients;
		this.threadID = threadID;
	}

	@Override
	public void run() {
		Action act = Action.Ok;
		int lifeCount = 3;
		Random chooseThePoint = new Random();


		try {
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

			do {
				MessageToServer recieved = (MessageToServer) ois.readObject();
				switch (recieved.getMethodName()) {
				case "createWoodman":
					synchronized (pWood) { 
						pWood.createWoodman(recieved.getWoodmanName(), starts.get(chooseThePoint.nextInt(starts.size())), finishes.get(chooseThePoint.nextInt(finishes.size())));
					}
					break;
				case "move":				
					synchronized (pWood) {
						act = pWood.move(recieved.getWoodmanName(), recieved.getDirection());
						if (act == Action.WoodmanNotFound && lifeCount == 0)
							lifeCount = -1;
						else
							lifeCount = pWood.getWoodman(recieved.getWoodmanName()).GetLifeCount();
						
						
					}
					oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					
					MessageToClient toSend;
					synchronized (listOfClients.get(threadID)) {
						toSend = new MessageToClient(act, lifeCount);
						listOfClients.get(threadID).wait();
						
					}
					oos.writeObject(toSend);
					oos.flush();
					break;
				}
			} while (act != Action.WoodmanNotFound && act != Action.Finish);

			synchronized (listOfClients) {
				if (act == Action.WoodmanNotFound || act == Action.Finish) {
					listOfClients.remove(threadID);
				}
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}
}
