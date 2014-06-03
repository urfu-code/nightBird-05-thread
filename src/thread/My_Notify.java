package thread;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class My_Notify implements Runnable {
	private ConcurrentHashMap<Integer, Thread> listOfClients;

	public My_Notify(ConcurrentHashMap<Integer, Thread> listOfClients) {
		this.listOfClients = listOfClients;
	}

	@Override
	public void run() {
		while (true) {			
			Iterator<Integer> keySetIterator = listOfClients.keySet().iterator();//перебираем поочередно
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
