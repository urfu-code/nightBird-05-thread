import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class Synchronizer implements Runnable {

	private ConcurrentHashMap<Integer, Thread> сlients;

	public Synchronizer(ConcurrentHashMap<Integer, Thread> clients) {
		this.сlients = clients;
	}

	@Override
	public void run() {
		while (true) {			
			Iterator<Integer> keySetIterator = сlients.keySet().iterator();
			while (keySetIterator.hasNext()) {
				Integer key = keySetIterator.next();
				synchronized (сlients.get(key)) {
					сlients.get(key).notify();
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
