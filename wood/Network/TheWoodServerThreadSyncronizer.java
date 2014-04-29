package Network;


public class TheWoodServerThreadSyncronizer implements Runnable {
	
	public TheWoodServerThreadSyncronizer() {
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
