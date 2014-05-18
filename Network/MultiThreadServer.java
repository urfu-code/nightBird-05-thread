package Network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import WoodEngine.Action;
import WoodEngine.Point;
import WoodEngine.PrintableWood;
import WoodEngine.WoodLoader;

public class MultiThreadServer {
	
	private static WoodLoader wl = new WoodLoader();
	private volatile static PrintableWood wood;
	
	private static final int PORT = 12345;
	
	private static volatile LinkedList<Socket> bufferderClients = new LinkedList<Socket>();
	private volatile static LinkedList<Thread> clients = new LinkedList<Thread>();
	private static final Object sync = new Object();
	private static ServerSocket server;
	private static HashMap<Point, Point> locations = new HashMap<Point, Point>();
	private static volatile Iterator<Point> loc;
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		locations.put(new Point(17, 13), new Point(1, 13));
		locations.put(new Point(1, 13), new Point(17, 13));
//		locations.put(new Point(3, 13), new Point(1, 9));
//		locations.put(new Point(1, 9), new Point(3, 13));
		loc = locations.keySet().iterator();
		wood = (PrintableWood) wl.LoadPrntbleWood(new FileInputStream(new File("maze")), System.out);
		server = new ServerSocket(PORT);
		Thread clBuffThread = new Thread(new ClientBuffer());
		//пускаем новичков в коридор
		clBuffThread.start();
		try {
		    Thread.sleep(5000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		while(true){
			//пустили клиентов из коридора в игровую комнату
			synchronized (bufferderClients) {
				for (Socket sckt : bufferderClients) {
					clients.add(new Thread(new ClientHandler(sckt)));
					clients.getLast().start();
				}
				bufferderClients.clear();
			}
			synchronized (sync) {
				sync.notifyAll();
			}
			wood.printWood();
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}
	
	private static class ClientBuffer implements Runnable{

	@Override
	public void run() {
		while (true){
			try {
				Socket incomingClient = server.accept();
				synchronized (bufferderClients) {
					bufferderClients.add(incomingClient);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

	private static class ClientHandler implements Runnable{
		
		private final Socket client;
		private Action act;
		private int steps = 0;
		private Object inpMessage;
		private String name;
		
		private ClientHandler(Socket inpClient){
			client = inpClient;
		}
		
		@SuppressWarnings("resource")
		@Override
		public void run() {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
				ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
				try{
					while(true){
						inpMessage = ois.readObject();
						if(inpMessage.getClass() == NetCreateInfo.class){
							NetCreateInfo create = (NetCreateInfo) inpMessage;
							synchronized (wood) {
								synchronized (loc) {
									if(!loc.hasNext()){
										loc = locations.keySet().iterator();
									}
									Point st = loc.next();
									Point fin = locations.get(st);
									wood.createWoodman(create.getName(), st, fin);
								}
							}
							name = create.getName();
							System.out.println("Woodman " + name + " created");
							synchronized (sync) {
								sync.wait();
							}
							continue;
						}
						if(inpMessage.getClass() == NetDirectionInfo.class){
							NetDirectionInfo move = (NetDirectionInfo) inpMessage;
							synchronized (wood) {
								act = wood.move(move.getName(), move.getDirection());
							}
							steps++;
							name = move.getName();
							NetActionInfo newActInfo = new NetActionInfo(move.getName(), act);
							oos.writeObject(newActInfo);
							oos.flush();
						} else{
							throw new IOException("Illegal input object " + inpMessage.getClass());
						}
						if(act == Action.Finish){
							System.out.println(name + " FINISHED in " + steps);
							break;
						}
						if(act == Action.WoodmanNotFound){
							System.out.println("LOL " + name + " DIED in " + steps);
							break;
						}
						synchronized (sync) {
							sync.wait();
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally{
					tryClose(client);
					tryClose(ois);
					tryClose(oos);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private static void tryClose(Closeable cl) {
		try{
			cl.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
}
