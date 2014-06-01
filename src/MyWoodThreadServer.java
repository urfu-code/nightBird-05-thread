import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MyWoodThreadServer implements Runnable {

	private ObjectInputStream reader = null;
	private ObjectOutputStream writer = null;
	private Socket socket = null;
	private MyPrintableWood wood;
	private Point start;
	private Point finish;
	private MyWoodServerSynchronizer synchronizer;

	public MyWoodThreadServer (Socket socket, MyPrintableWood wood, Point start, Point finish,
			MyWoodServerSynchronizer synchronizer) throws IOException {
		this.socket = socket;
		this.wood = wood;
		this.synchronizer = synchronizer;
		this.start = start;
		this.finish = finish;
		try {
			reader = new ObjectInputStream(socket.getInputStream());
			writer = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Oops, reader or writer has been crashed!");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Chatbox message = null;
		Action action = Action.Ok;
		try {
			while (action != Action.WoodmanNotFound && action != Action.Finish) {
				synchronized (synchronizer) {
					synchronizer.wait();
				}
				message = (Chatbox) reader.readObject();
				if (message.getCommand().equals("create mau5")) {
					wood.createWoodman(message.getName(), start, finish);
				} else
					if (message.getCommand().equals("move mau5")) {
						action = wood.move(message.getName(), message.getDirection());
						if (action == Action.WoodmanNotFound)
							System.out.println("Mau5 has just died.");
						if (action == Action.Finish)
							System.out.println("Mau5 has just finished!");
						message = new Chatbox(action);
						writer.writeObject(message);
						writer.flush();
					}
			}
		} catch (IOException e) {e.printStackTrace();
		} catch (ClassNotFoundException e) {e.printStackTrace();
		} catch (InterruptedException e) {e.printStackTrace();
		} finally {
			Destroyter d = new Destroyter();
			if (reader != null) d.close(reader);
			if (writer != null) d.close(writer);
			if (socket != null) d.close(socket);
		}
	}
}