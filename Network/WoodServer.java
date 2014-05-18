package Network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import WoodEngine.Action;
import WoodEngine.Point;
import WoodEngine.PrintableWood;
import WoodEngine.Wood;
import WoodEngine.WoodLoader;

public class WoodServer {
	
	private final static int PORT = 12345;
	
	@SuppressWarnings("resource")
	public static void main(String args[]){
		Action act;
		int steps = 0;
		WoodLoader wl = new WoodLoader();
		try{
			Wood wood = (PrintableWood) wl.LoadPrntbleWood(new FileInputStream("maze"), System.out);
			ServerSocket server = new ServerSocket(PORT);
			Socket inpClient = server.accept();
			System.err.println("Client connected through " + PORT + " port");
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(inpClient.getInputStream()));
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(inpClient.getOutputStream()));
			NetDirectionInfo move;
			Object inpMessage;
			try{
				while(true){
					inpMessage = ois.readObject();
					if(inpMessage.getClass() == NetCreateInfo.class){
						NetCreateInfo create = (NetCreateInfo) inpMessage;
						wood.createWoodman(create.getName(), new Point(17, 13), new Point(1, 13));
						System.out.println("Woodman " + create.getName() + " created");
						continue;
					}
					if(inpMessage.getClass() == NetDirectionInfo.class){
						move = (NetDirectionInfo) inpMessage;
						act = wood.move(move.getName(), move.getDirection());
						steps++;
						NetActionInfo newActInfo = new NetActionInfo(move.getName(), act);
						oos.writeObject(newActInfo);
						oos.flush();
					} else{
						throw new IOException("Illegal input object " + inpMessage.getClass());
					}
					if(act == Action.Finish){
						System.out.println("YOU FINISHED in " + steps);
						break;
					}
					if(act == Action.WoodmanNotFound){
						System.out.println("LOL YOU DIED in " + steps);
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally{
				tryClose(server);
				tryClose(inpClient);
				tryClose(ois);
				tryClose(oos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void tryClose(Closeable cl) {
		try{
			cl.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
