import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server{
	volatile static HashMap<Integer, Thread> clients = new HashMap<Integer, Thread>();
	public static ArrayList<Point> points;
	private static ServerSocket serverSocket;
	
	public static void main(String[] args) {
		points = new ArrayList<>();
		points.add(new Point(1,1));
		points.add(new Point(6,2));
		points.add(new Point(2,3));
		points.add(new Point(4,3));
		points.add(new Point(3,6));
		points.add(new Point(4,4));
		try {
			File file = new File("wood.txt");
			MyWoodLoader loader = new MyWoodLoader();
			PrintableWood wood = (PrintableWood) loader.Load(new FileInputStream(file),System.out);
			
			serverSocket = new ServerSocket(25436);
			
			MyNotify notify = new MyNotify(clients);
			Thread thread = new Thread(notify);
			thread.start();
			
			Exit stop = new Exit();
			stop.start();
			
			serverSocket.setSoTimeout(1000);
			
			while (true) {
				Integer identifier;
				Thread mThread;
				
				if (stop.flag) {
					System.out.println("Exit");
					throw new Exception("Exit");
				}
				
				try {
					Random random = new Random();
					identifier = Math.abs(random.nextInt());
					mThread = new Thread(new ServerThread(serverSocket.accept(), wood, points, clients, identifier));
				} catch (SocketTimeoutException e) {
					continue;
				} 
				
				synchronized (clients) {
					clients.put(identifier, mThread);
				}					
				 mThread.start();
				System.out.println(clients.size() + " clients!");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception ter){
			closeObject(serverSocket);
			System.exit(0);
		}
		finally {
			closeObject(serverSocket);
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
