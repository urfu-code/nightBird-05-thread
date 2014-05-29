package thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import server.MessageClient;
import server.MessageServer;
import wood.Action;
import wood.Point;
import wood.PrintWood;

public class My_Thread implements Runnable{
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private Socket socket;
	private PrintWood wood;
	private volatile ConcurrentHashMap<Integer, Thread> clients;
	private ArrayList<Point> points;
	private Integer threadID;

	public My_Thread(Socket socket, PrintWood wood, ArrayList<Point> points, ConcurrentHashMap<Integer, Thread> clients, Integer threadID) throws IOException {
		this.socket = socket;
		this.wood = wood;
		this.points = points;
		this.clients = clients;
		this.threadID = threadID;
	}

	@Override
	public void run() {
		Action action = Action.Ok;
		try {
			inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			while (action != Action.WoodmanNotFound && action != Action.Finish) {
				MessageServer messageServer = (MessageServer)inStream.readObject();
				switch (messageServer.getMethod()) {
				case "createWoodman" :
					synchronized(wood){
						wood.createWoodman(messageServer.getName(), points.get(Math.abs(new Random().nextInt(points.size()))), points.get(Math.abs(new Random().nextInt(points.size()))));
					}
					break;
				case "move" :
					MessageClient messageClient= new MessageClient(action);
					synchronized (clients.get(threadID)) {
						clients.get(threadID).wait();//ожидание
						//this.wait();
					}
					synchronized(wood){
						action = wood.move(messageServer.getName(), messageServer.getDirection());
					}
					if(action == Action.WoodmanNotFound) System.out.println("Мышь умерла!:(");
					if(action == Action.Finish) System.out.println("Мышь дошла до финиша!:)");
					outStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					outStream.writeObject(messageClient);
					outStream.flush();
					break;
				}
			}
			synchronized (clients) {
				if ((action == Action.WoodmanNotFound) || (action == Action.Finish)) {
					clients.remove(threadID);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			closeObject(inStream);
			closeObject(outStream);	
			closeObject(socket);
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
