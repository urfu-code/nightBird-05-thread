import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


public class ServerThread implements Runnable {
	
	private Socket socket;
	private PrintableWood pw;
	private ArrayList<Point> start;
	private ArrayList<Point> finish;
	private Synchronization syn;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public ServerThread(Socket socket, PrintableWood wood, Synchronization syn) {
		this.socket = socket;
		this.pw = wood;
		this.syn = syn;
		start.add(new Point(1, 1)); start.add(new Point(8, 3));
		finish.add(new Point(8, 1)); finish.add(new Point(1, 5));
		try {
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Random rand = new Random();
		try {
			while (true) {
				synchronized(syn) {
					syn.wait();
				}
				MessageClient newMessageClient = (MessageClient) ois.readObject();
				if (newMessageClient.getTask().equals("create")) {
					int t = rand.nextInt(1);
					pw.createWoodman(newMessageClient.getName(), start.get(t), finish.get(t));
					MessageServer newMessageServer = new MessageServer(Action.Ok);
					oos.writeObject(newMessageServer);
					oos.flush();
				}
				if (newMessageClient.getTask().equals("move")) {
					Action action = pw.move(newMessageClient.getName(), newMessageClient.getDirection());
					MessageServer newMessageServer = new MessageServer(action);
					oos.writeObject(newMessageServer);
					oos.flush();
					if (action == Action.Finish) {
						System.out.println("finish");
						break;
					}
					if (action == Action.WoodmanNotFound) {
						System.out.println("dead");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(socket);
			close(oos);
			close(ois);
		}
	}
	
	private static void close(Closeable obj) {
		try {
			obj.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
