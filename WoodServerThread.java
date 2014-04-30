//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.net.ServerSocket;
//import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;


public class WoodServerThread {

	PrintableWoodLoader wl;

	public static void main(String[] args) throws InterruptedException, URISyntaxException {
		
		CodeSource codeSource = WoodServerThread.class.getProtectionDomain().getCodeSource();
		File file = new File(new File(codeSource.getLocation().toURI().getPath()).getParentFile().getPath(), "src/input.txt");

		ArrayList<Point> starts = new ArrayList<>();
		ArrayList<Point> finishes = new ArrayList<>();
		ArrayList<Thread> listOfClients = new ArrayList<Thread>();
		starts.add(new Point(1, 1));
		starts.add(new Point(5, 1));
		starts.add(new Point(5, 4));
		starts.add(new Point(4, 6));
		finishes.add(new Point(6, 6));
		finishes.add(new Point(7, 1));
		finishes.add(new Point(1, 3));
		finishes.add(new Point(6, 9));

		try {
			ServerSocket forClients = new ServerSocket(32015); // для создания сокетов и потоков-клиентов
//			ServerSocket ssforUser = new ServerSocket(32115);
//			Socket forUser = ssforUser.accept();
			
//			BufferedReader reader = new BufferedReader(new InputStreamReader(forUser.getInputStream()));
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(forUser.getOutputStream()));
			
			try {
				FileInputStream stream = new FileInputStream(file);
				PrintableWoodLoader wl = new PrintableWoodLoader();
				PrintableWood wood = wl.Load(stream);
				
				
				Notifier notty = new Notifier(listOfClients);
				Thread nottyThread = new Thread(notty);
				nottyThread.start();
				
				while (true) {
//					if (reader.ready()) {
//						if (reader.readLine().equals("Stop")) {
//							writer.write("Server stopped" + System.getProperty("line.separator"));
//							writer.flush();
//							return;
//						}
//					}
					try {
						Thread th = new Thread(new Handler(forClients.accept(), wood, starts, finishes));
						listOfClients.add(th);
						th.start();
						th.join();
						System.out.println(listOfClients.size() + " clients!");
					} catch (SocketTimeoutException  e) {
						e.printStackTrace();
					}
					
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			finally {
				forClients.close();
//				ssforUser.close();
//				forUser.close();
//				reader.close();
//				writer.close();				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
