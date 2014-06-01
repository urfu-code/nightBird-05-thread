import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;


public class MyWoodMultiServer {

	private static final int port = 1000;  
	
	public static void main(String[] Args) throws IOException {

		ServerSocket server = null;
		Thread thread;
		File file = new File("test_forest.txt");
		MyWoodLoader loader = new MyWoodLoader();
		MyPrintableWood wood = (MyPrintableWood) loader.Load(new FileInputStream(file),System.out);
		ArrayList<Point> points = new ArrayList<Point>();
		File locations = new File("start&finish points");
		FileInputStream stream = new FileInputStream(locations);
		Scanner sc = new Scanner(locations);
		while(sc.hasNext()) points.add((new Point (sc.nextInt(),sc.nextInt())));
		stream.close();
		int Idx = 0;
		try {
			server = new ServerSocket(port);
			System.out.println("Server is online!");
			try {
				System.out.println("Waiting for connection...");
				Vector <Thread> clients = new Vector<Thread>();
				MyWoodServerSynchronizer synchronizer = new MyWoodServerSynchronizer(clients);
				thread = new Thread(synchronizer);
				thread.start();
				while (true) {
					thread = new Thread(new MyWoodThreadServer(server.accept(), wood, points.get(Idx++),
							points.get(Idx++), synchronizer));
					clients.add(thread);
					thread.start();
					System.out.println(clients.size() + " clients connected!");
					if (Idx == points.size()) Idx = 0;
				}
			}
			catch (IOException e) {
				System.out.println("Oops, IOException");
				e.printStackTrace();
			}
		} finally {
			Destroyter d = new Destroyter();
			if (server != null) d.close(server);
		}
	}
}