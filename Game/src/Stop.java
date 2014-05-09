import java.util.Scanner;


public class Stop extends Thread {

	volatile boolean flag;
	
	public Stop() {
		this.flag = false;
	}

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			String str = sc.nextLine();
			if (str.equals("finish")) {				
				this.flag = true;
				sc.close();
				return;				
			}
		}
	}

}