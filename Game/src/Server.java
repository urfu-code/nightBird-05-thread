import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Server extends Close {
	private static Socket socket;
	private static ServerSocket server;
	private static BufferedReader reader;
	private static BufferedWriter writer;
	private static InputStream instream;
	Request m_request;
	Response m_response;
	volatile static HashMap<Integer, Thread> clients = new HashMap<Integer, Thread>();

	public static void main(String[] args) throws CodeException, IOException {
		File file=new File("world.txt");
		LinkedList<Point> points = new LinkedList<Point>();
		points.add(new Point(1,2));
			points.add(new Point(1,3));
			points.add(new Point(1,4));
			points.add(new Point(1,5));
			points.add(new Point(2,7));
			points.add(new Point(6,7));
		try {
			server = new ServerSocket(17376);
			instream = new FileInputStream(file);
			
			PrintableWoodLoader W = new PrintableWoodLoader();
			PrintableWood wood = W.PrintableWoodLoad(instream,System.out);					
			Synchronizer synchronizer = new Synchronizer(clients);
			Thread threadSynchronizer = new Thread(synchronizer);
			
					

			threadSynchronizer.start();
			Stop s = new Stop();
			s.start();

			server.setSoTimeout(1000);

			while (true) {
				Integer threadID;
				Thread thread;
				if (s.flag) {
					System.out.println("Server stopped");
					throw new Stopper("Server stopped");
				}
				try {
					Random random = new Random();
					threadID = Math.abs(random.nextInt());
					thread = new Thread(new ThreadsServer(server.accept(), wood, clients, points, threadID));
				} catch (SocketTimeoutException e) {
					continue;
				}
				synchronized (clients) {
					clients.put(threadID, thread);
				}					
				thread.start();
				if (clients.size() == 1) {
					System.out.println(clients.size() + " mouse in the wood");
				}
				else {
					System.out.println(clients.size() + " mouses in the wood");
				}
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			tryClose(server);
			tryClose(instream);
			System.exit(0);
		}
		finally {
			tryClose(server);
			tryClose(socket);
			tryClose(reader);
			tryClose(writer);
			tryClose(instream);
		}
	}
}	
