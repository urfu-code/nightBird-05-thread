import java.util.HashMap;
import java.util.Iterator;


public class Synchronizer implements Runnable {

	private HashMap<Integer, Thread> сlients;

	public Synchronizer(HashMap<Integer, Thread> сlientsList) {
		this.сlients = сlientsList;
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
