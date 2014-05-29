
public class Synchronization implements Runnable {
	
	public Synchronization() {
	}

	@Override
	public void run() {
		while (true) {
			try {
				synchronized (this) {
					notifyAll();
				}
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}
}
