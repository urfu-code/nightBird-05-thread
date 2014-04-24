package Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import wood01.Action;
import wood01.Point;
import wood01.PrintableTheWood;

public class TheWoodServerThread implements Runnable {
	
	private ObjectInputStream iStream = null;
	private ObjectOutputStream oStream = null;
	private Socket fromClient = null;
	private PrintableTheWood wood;
	private MouseRequest request;
	private WoodResponse response;
	private ArrayList<Point>points;
	private TheWoodServerThreadSyncronizer monitor;
	
	public TheWoodServerThread(Socket socket, PrintableTheWood wood,ArrayList<Point> points, TheWoodServerThreadSyncronizer monitor) throws IOException {
		fromClient = socket;
		this.wood = wood;
		this.points = points;
		this.monitor = monitor;
		try {
			iStream = new ObjectInputStream(fromClient.getInputStream());
			oStream = new ObjectOutputStream(fromClient.getOutputStream());
		} catch (IOException e) {
			System.out.println("����� �����..");
		}
	}
	
	@Override
	public void run() {
		Action currentAction = Action.Ok;
		Random random = new Random();
		try {
			while (currentAction != Action.WoodmanNotFound && currentAction != Action.ExitFound) {
				synchronized(monitor) {
					monitor.wait();
				}
				request = (MouseRequest) iStream.readObject();
				if (request.getRequestType().equals("create")) {
					wood.createWoodman(request.getName(), points.get(Math.abs(random.nextInt(points.size()))), points.get(Math.abs(random.nextInt(points.size()))));
				}
				else if (request.getRequestType().equals("move")) {
					currentAction = wood.move(request.getName(), request.getDirection());
				}
				response = new WoodResponse(currentAction);
				oStream.writeObject(response);
				oStream.flush();
			}
			if (currentAction == Action.WoodmanNotFound) {
				System.out.println("���� " + request.getName() + " �������");
			}
			else {
				System.out.println("���� " + request.getName() + " ����� �����!");
			}
			
		}
		catch (IOException e) {
			System.out.println("������ �����");
		} catch (Exception e) {
			System.out.println("������ � �����");
			e.printStackTrace();
		}
		finally {
			try {
				if (iStream != null) {
					iStream.close();
				}
				if (oStream != null) {
					oStream.close();
				}
				if (fromClient != null) {
					fromClient.close();
				}
			}
			catch (IOException e) {
				System.out.println("�� �� ���������� �������!..");
			}
		}
	}

}
