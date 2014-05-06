package thread;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import wood.My_WoodLoader;
import wood.Point;
import wood.PrintWood;

public class ServerThread{
	My_WoodLoader loader;
	volatile static HashMap<Integer, Thread> clients = new HashMap<Integer, Thread>();
	
	public static void main(String[] args) {
	//содержит три строчки старта и финиша
		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(1, 1));
		points.add(new Point(1, 3));
		points.add(new Point(9, 1));
		points.add(new Point(7, 3));
		points.add(new Point(7, 1));
		File file = new File("myWood.txt");
		ServerSocket serverSocket = null;
		FileInputStream stream = null;
		try {
			serverSocket = new ServerSocket(25436);
			//System.out.println("Client connected");
			stream = new FileInputStream(file);
			My_WoodLoader wl = new My_WoodLoader();
			PrintWood wood = wl.Load(stream, System.out);
			
			My_Notify myNotify = new My_Notify(clients);
			Thread notifyThread = new Thread(myNotify);
			notifyThread.start();
			Stopping work = new Stopping();
			work.start();
			
			serverSocket.setSoTimeout(1000);
			
			while (true) {
				if (work.flag) {
					System.out.println("server is stopped");
					throw new Exception("server is stopped");
				}
				Integer threadID;
				Thread mThread;
				try {
					Random random = new Random();
					threadID = Math.abs(random.nextInt());
					mThread = new Thread(new My_Thread(serverSocket.accept(), wood, points, clients, threadID));
				} catch (SocketTimeoutException e) {
					continue;
				}

				synchronized (clients) {
					clients.put(threadID, mThread);
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
			closeObject(stream);
			System.exit(0);
		}
		finally {
			closeObject(stream);
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

