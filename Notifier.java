import java.util.ArrayList;


public class Notifier implements Runnable {
	private ArrayList<Thread> listOfClients;

	public Notifier(ArrayList<Thread> listOfClients) {
		this.listOfClients = listOfClients;
	}

	@Override
	public void run() {

		while (true) {			

			for (int i = 0; i < listOfClients.size(); i++) {
				synchronized (listOfClients.get(i)) {
					listOfClients.get(i).notify();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
