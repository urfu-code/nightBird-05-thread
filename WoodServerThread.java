import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class WoodServerThread {

	PrintableWoodLoader wl;
	volatile static HashMap<Integer, Thread> listOfClients = new HashMap<Integer, Thread>();

	public static void main(String[] args) throws URISyntaxException, InterruptedException {

		CodeSource codeSource = WoodServerThread.class.getProtectionDomain().getCodeSource();
		File file = new File(new File(codeSource.getLocation().toURI().getPath()).getParentFile().getPath(), "src/input.txt");

		ArrayList<Point> starts = new ArrayList<>();
		ArrayList<Point> finishes = new ArrayList<>();

//		starts.add(new Point(1, 1));
//		starts.add(new Point(5, 1));
//		starts.add(new Point(5, 4));
		starts.add(new Point(4, 6));
//		finishes.add(new Point(6, 6));
//		finishes.add(new Point(7, 1));
		finishes.add(new Point(1, 3));
		finishes.add(new Point(6, 9));

		try {
			ServerSocket forClients = new ServerSocket(32015); // для создания сокетов и потоков-клиентов

			try {
				FileInputStream stream = new FileInputStream(file);
				PrintableWoodLoader wl = new PrintableWoodLoader();
				PrintableWood wood = wl.Load(stream);


				Notifier notty = new Notifier(listOfClients);
				Thread nottyThread = new Thread(notty);
				nottyThread.start();
				Terminator Arny = new Terminator();
				Arny.start();
				
				forClients.setSoTimeout(1000);
				while (true) {
					if (Arny.flag) {
						System.out.println("SERVER STOPPED");
						throw new Termination();
					}

					Integer threadID;
					Thread th;
					try {
						Random random = new Random();
						threadID = Math.abs(random.nextInt());
						th = new Thread(new Handler(forClients.accept(), wood, starts, finishes, listOfClients, threadID));
					} catch (SocketTimeoutException e) {
						continue;
					}

					synchronized (listOfClients) {
						listOfClients.put(threadID, th);
					}					
					th.start();
					System.out.println(listOfClients.size() + " clients!");

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			catch (Termination r) {
				forClients.close();
				System.exit(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
