import java.util.HashMap;
import java.util.Iterator;

public class MyNotify implements Runnable {
	private HashMap<Integer, Thread> clients;

	public MyNotify(HashMap<Integer, Thread> clients) {
		this.clients = clients;
	}

	@Override
	public void run() {
		while (true) {			
			Iterator<Integer> keySetIterator = clients.keySet().iterator();
			while (keySetIterator.hasNext()) {
				Integer key = keySetIterator.next();
				synchronized (clients.get(key)) {
					clients.get(key).notify();
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