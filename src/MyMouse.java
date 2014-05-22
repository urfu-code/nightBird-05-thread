
public class MyMouse implements Mouse {

	private Point mouse_location;
	private int mouse_lifes;
	private Direction last_direction;

	public MyMouse() {
		mouse_lifes = 3;
		last_direction = null;
	}
	
	@Override
	public Direction NextMove(Action action) throws Exception {

		Point location = mouse_location;
		Direction mouse_direction = last_direction;
		if (mouse_direction == null) {
			mouse_direction = Direction.Right;
			last_direction = mouse_direction;
			return mouse_direction;
		}

		switch (action) {
		case Ok:
			mouse_location = location;
			switch (last_direction) {
			case Up: {
				mouse_direction = Direction.Left;
			}
			break;
			case Down: {
				mouse_direction = Direction.Right;
			}
			break;
			case Left: {
				mouse_direction = Direction.Down;
			}
			break;
			case Right: {
				mouse_direction = Direction.Up;
			}
			break;
			}
			break;
		case Fail:
			switch (last_direction) {
			case Up: {
				mouse_direction = Direction.Right;
			}
			break;
			case Down: {
				mouse_direction = Direction.Left;
			}
			break;
			case Left: {
				mouse_direction = Direction.Up;
			}
			break;
			case Right: {
				mouse_direction = Direction.Down;
			}
			break;	
			}
			break;
		case Life:	
			mouse_location = location;
			mouse_lifes = mouse_lifes+1;
			switch (last_direction) {
			case Up: {
				mouse_direction = Direction.Left;
			}
			break;
			case Down: {
				mouse_direction = Direction.Right;
			}
			break;
			case Left: {
				mouse_direction = Direction.Down;
			}
			break;
			case Right: {
				mouse_direction = Direction.Up;
			}
			break;	

			}
			break;

		case Dead:
			mouse_location = location;
			Kill();
			switch (last_direction) {
			case Up: {
				mouse_direction = Direction.Left;
			}
			break;
			case Down: {
				mouse_direction = Direction.Right;
			}
			break;
			case Left: {
				mouse_direction = Direction.Down;
			}
			break;
			case Right: {
				mouse_direction = Direction.Up;
			}
			break;	

			}

		}

		last_direction = mouse_direction;
		return mouse_direction;
	}
	
	public boolean Kill() {
		mouse_lifes = mouse_lifes-1;
		if (mouse_lifes > -1)
			return true;
		else
			return false;
	}
}
