import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {	
	public static void main (String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(9874);
			Socket socket = serverSocket.accept();
			MyWoodLoader loader = new MyWoodLoader();
			PrintableWood pw = (PrintableWood) loader.prWood(System.out);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			try {
				Synchronization syn = new Synchronization();
				Thread thread = new Thread(syn);
				thread.start();
				while (true) {
					if (reader.readLine().equals("stop")) {
						writer.write("stop");
						writer.flush();
						thread.interrupt();
						break;
					}
					thread = new Thread(new ServerThread(serverSocket.accept(), pw, syn));
					thread.start();
				}
			} finally {
				close(serverSocket);
				close(socket);
				close(writer);
				close(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void close(Closeable obj) {
		try {
			obj.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
