
public class Synchronizer implements Runnable {

	public Synchronizer () {}

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
				e.printStackTrace();
				break;
			}
		}
	}
}
