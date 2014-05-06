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
			server = new ServerSocket(17826);
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
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		try {
			while (true) {
				if (reader.readLine().equals("start")) {
					writer.write("Server started\r\n");
					writer.flush();
					break;
				}
				else {
					writer.write("Type start\r\n");
					writer.flush();
				}
			}
			threadSynchronizer.start();
			while (true) {
				if (reader.ready()) {
					if (reader.readLine().equals("finish")) {
						writer.write("You finished\r\n");
						writer.flush();
						threadSynchronizer.interrupt();
						break;
					}
					else {
						writer.write("Type start\r\n");
						writer.flush();
					}
				}
				try {
					thread = new Thread(new ThreadsServer(server.accept(), wood, points, synchronizer));
					thread.start();
				}
				catch (SocketTimeoutException e) {
					continue;
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

