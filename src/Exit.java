import java.util.Scanner;

public class Exit extends Thread{
	volatile boolean flag;
	public Exit() {
		this.flag = false;
	}

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			String str = sc.nextLine();
			if (str.equals("exit")) {				
				this.flag = true;
				sc.close();
				return;				
			}

		}

	}
}