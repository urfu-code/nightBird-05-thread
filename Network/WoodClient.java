package Network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Bot.Mouse;
import WoodEngine.Action;
import WoodEngine.Direction;

public class WoodClient {

	private final static int PORT = 12345;
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException{
		if(args.length != 1){
			throw new IOException("Wrong arguments");
		}
		String name = args[0];
		try{
			InetAddress address = InetAddress.getByName("localhost");
			Socket serverConnection = new Socket(address, PORT);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(serverConnection.getOutputStream()));
			Mouse mouse = new Mouse(name);
			NetDirectionInfo dirInfo = new NetDirectionInfo(name, mouse.NextMove(Action.Ok));
			try{
				NetCreateInfo crInf = new NetCreateInfo(name);
				oos.writeObject(crInf);
				oos.flush();
				oos.writeObject(dirInfo);
				oos.flush();
				ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(serverConnection.getInputStream()));
				try{
					while(true){
						Object inpMessage = ois.readObject();
						if(inpMessage.getClass() == NetActionInfo.class){
							NetActionInfo actInf = (NetActionInfo) inpMessage;
							if(actInf.getAction() == Action.Finish
									|| actInf.getAction() == Action.WoodmanNotFound) break;
							Direction res = mouse.NextMove(actInf.getAction());
							NetDirectionInfo newDirInfo = new NetDirectionInfo(name, res);
							System.out.println(newDirInfo.getDirection());
							oos.writeObject(newDirInfo);
							oos.flush();
						} else{
							throw new IOException("Illegal input object class " + inpMessage.getClass());
						}
					}
				} finally{
					tryClose(ois);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tryClose(serverConnection);
				tryClose(oos);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
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
