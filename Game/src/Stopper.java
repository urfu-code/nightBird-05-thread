
public class Stopper extends RuntimeException {

	private static final long serialVersionUID = 1L;
 
 	public Stopper() {
 		super();
 	}
 
 	public Stopper(String message) {
 		super(message);
 	}
}