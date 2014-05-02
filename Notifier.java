import java.util.HashMap;
import java.util.Iterator;


public class Notifier implements Runnable {
	private HashMap<Integer, Thread> listOfClients;

	public Notifier(HashMap<Integer, Thread> listOfClients) {
		this.listOfClients = listOfClients;
	}

	@Override
	public void run() {

		while (true) {			

			Iterator<Integer> keySetIterator = listOfClients.keySet().iterator();
			while (keySetIterator.hasNext()) {
				Integer key = keySetIterator.next();
				synchronized (listOfClients.get(key)) {
					listOfClients.get(key).notify();
				}		  


				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
