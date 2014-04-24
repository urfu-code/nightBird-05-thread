package Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import wood01.Point;
import wood01.PrintableTheWood;
import wood01.TheWoodLoader;

public class TheWoodServer {
	
	public static void main(String[] Args) {
		ServerSocket server = null;
		ServerSocket telnetManage = null;
		Socket fromManager = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		Thread thread;
		File file = new File("wood.txt");
		ArrayList<Point>startEndPoints = new ArrayList<Point>();
		TheWoodLoader loader = new TheWoodLoader();
		startEndPoints.add(new Point(11,6));
		startEndPoints.add(new Point(1,1));
		startEndPoints.add(new Point(11,1));
		startEndPoints.add(new Point(1,6));
		startEndPoints.add(new Point(7,4));
		startEndPoints.add(new Point(7,5));
		try {
			server = new ServerSocket(6789);
			telnetManage = new ServerSocket(6777);
			fromManager = telnetManage.accept();
			reader = new BufferedReader(new InputStreamReader(fromManager.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(fromManager.getOutputStream()));
			while (true) {
				if (reader.readLine().equals("start")) {
					writer.write("Server started!\r\n");
					writer.flush();
					break;
				}
				else {
					writer.write("Чтобы запустить сервер, наберите start и нажмите Enter\r\n");
					writer.flush();
				}
			}
			try {
				PrintableTheWood wood = (PrintableTheWood) loader.Load(new FileInputStream(file),System.out);
				System.out.println("Waiting for connection...");
				ArrayList<Thread> clients = new ArrayList<Thread>();
				Object monitor = new Object();
				TheWoodServerThreadSyncronizer sync = new TheWoodServerThreadSyncronizer(clients, monitor);
				Thread threadSync = new Thread(sync);
				threadSync.start();
				server.setSoTimeout(1000);
				while (true) {
					if (reader.ready()) {
						if (reader.readLine().equals("stop")) {
							writer.write("Server stopped!\r\n");
							writer.flush();
							threadSync.interrupt();
							break;
						}
						else {
							writer.write("Чтобы остановить сервер введите stop и нажмите Enter\r\n");
							writer.flush();
						}
					}
					try {
						thread = new Thread(new TheWoodServerThread(server.accept(), wood, startEndPoints, sync));
						clients.add(thread);
						thread.start();
						System.out.println(clients.size() + " clients connected!");
					}
					catch (SocketTimeoutException e) {
						continue;
					}
					
				}
			}
			catch (ClassNotFoundException e) {
				System.out.println("тестовая беда");
			}
			catch (IOException e) {
				System.out.println("всё, хватило с вудКлиента пакетов");
				e.printStackTrace();
			}
			catch (Exception e) {
				System.out.println("беда с лесом");
			}
			finally {
				if (server != null) {
					server.close();
				}
				if (telnetManage != null) {
					telnetManage.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			}
		}
		catch (IOException e) {
			System.out.println("port is busy");
			System.exit(-1);
		}


	}
}
