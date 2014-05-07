import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

public class Server extends Close {
	private static Socket socket;
	private static ServerSocket server;
	private static BufferedReader reader;
	private static BufferedWriter writer;
	private static InputStream instream;
	Request m_request;
	Response m_response;
	public static void main(String[] args) throws CodeException, IOException {
		File file=new File("world.txt");
		instream = new FileInputStream(file);
		try {
			server = new ServerSocket(17396);
			socket = server.accept();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PrintableWoodLoader W = new PrintableWoodLoader();
		PrintableWood wood = W.PrintableWoodLoad(instream,System.out);			
		LinkedList<Point> points = new LinkedList<Point>();
		Synchronizer synchronizer = new Synchronizer();
		Thread thread;
		Thread threadSynchronizer = new Thread(synchronizer);
		points.add(new Point(1,2));
		points.add(new Point(1,3));
		points.add(new Point(1,4));
		points.add(new Point(1,5));
		points.add(new Point(2,7));
		points.add(new Point(6,7));			
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
		threadSynchronizer.start();
<<<<<<< HEAD
			System.out.println("Server started");
			while (true) {
					Stop s = new Stop();
					s.start();
				    if (s.flag) {
					 System.out.println("Server stopped");
					 		threadSynchronizer.interrupt();
					 		throw new  Stopper();
					 
					 					}
					try {
					thread = new Thread(new ThreadsServer(socket, wood, points, synchronizer));
					thread.start();	 			
					}
			
=======
			System.out.println("server started");
			while (true) {
				if (reader.ready()) {
					System.out.println("I' m waiting for finish");
					if (reader.readLine().equals("finish")) {
						System.out.println("You finished\r\n");
					
						threadSynchronizer.interrupt();
						break;
					}
			
				}
				try {
					thread = new Thread(new ThreadsServer(socket, wood, points, synchronizer));
					thread.start();
				}
>>>>>>> 9656234db585fadedfc55f05a113bb47d12ca81c
				catch (SocketTimeoutException e) {
					continue;
				}
		try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	}
			}
		catch (IOException e) {
			e.printStackTrace();
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
