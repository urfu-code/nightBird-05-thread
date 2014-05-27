
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MyNotify implements Runnable {
	private ConcurrentHashMap<Integer, Thread> clients;


	public MyNotify(ConcurrentHashMap<Integer, Thread> clients) {
		this.clients = clients;
	}

	@Override
	public void run() {
		Iterator<Integer> keySetIterator;
		while (true) {			
			keySetIterator = clients.keySet().iterator();
			while (keySetIterator.hasNext()) {
				Integer key = keySetIterator.next();
				synchronized (clients.get(key)) {
					clients.get(key).notify();
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