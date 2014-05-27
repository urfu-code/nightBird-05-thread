import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server{
	public static ArrayList<Point> points;
	private static ServerSocket serverSocket;
	
	public static void main(String[] args) {
		points = new ArrayList<>();
		points.add(new Point(1,1));
		points.add(new Point(6,2));
//		points.add(new Point(2,2));
//		points.add(new Point(1,6));

		try {
			File file = new File("wood.txt");
			MyWoodLoader loader = new MyWoodLoader();
			PrintableWood wood = (PrintableWood) loader.Load(new FileInputStream(file),System.out);
			serverSocket = new ServerSocket(25436);
			Synchronizer notify = new Synchronizer();
			Thread thread;
			thread = new Thread(new ServerThread(serverSocket.accept(), wood, points, notify));
			thread.start();
			thread = new Thread(notify);
			thread.start();
			while (true) {
				thread = new Thread(new ServerThread(serverSocket.accept(), wood, points, notify));
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception ter){
			closeObject(serverSocket);
			System.exit(0);
		} finally {
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
