package thread;

import java.util.Scanner;

public class Stopping extends Thread{
	volatile boolean flag;
	public Stopping() {
		this.flag = false;
	}

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			String str = sc.nextLine();
			if (str.equals("stop")) {				
				this.flag = true;
				sc.close();
				return;				
			}

		}

	}
}
