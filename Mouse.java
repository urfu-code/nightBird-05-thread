import java.util.Stack;

/**
 * 
 * @author Дарья
 * @how руководствуемся правилом правой руки
 * 
 * @how если актуатор видит, что у мыши заканчиваются жизни, он сообщает, что пора "подзарядиться", изменив название алгоритма выполнения на "Back to life"
 * @how путь от жизни, если таковая встретилась, храним в стеке wayFromLife, при необходимости обращаемся к нему: идём по сохранённым в нём Direction, "противоположно разворачиваемым"
 * @how кладём в этот стек Direction от прошлого хода, с известным результатом
 * 
 * @how возвращаясь к жизни, сохраняем по ходу извлекаемые Direction в другой стек, wayBackToPosition, по которому вернёмся на точку, с которой ушли за жизнью
 * @how если встречается новая жизнь, стек wayFromLife сбрасываем и начинаем заполнять заново. это гарантирует путь до ближайшей (по мнению мыши) жизни
 * 
 * @how тупики и направления, которые дали Fail, в стек wayFromLife не заносятся
 * @how кроме того, встав на жизнь, мышь набирает некоторое количество жизней "впрок"
 */

public class Mouse implements IMouse {

	private String mouseName;
	private Direction latestDirection;
	private int getEnegry; // встав на жизнь, будем добирать столько жизней
	Stack<Direction> wayFromLife;
	Stack<Direction> wayBackToPosition; 
	private boolean life; // будем ориентироваться на неё при сохранении пути назад
	private boolean empty;
	String nameOfAlghoritm;
	private boolean itKilledMe;
	private int lifeCount;

	public int getLifeCount() {
		return lifeCount;
	}
	public void setLifeCount(int shift) {
		this.lifeCount+=shift;
	}
	// блок геттеров-сеттеров для тестов
	public Direction getDirection() {
		return this.latestDirection;
	}
	public void setDirection(Direction direction) {
		latestDirection = direction;
	}
	public String getName() {
		return this.mouseName;
	}
	public int getEnergyPoints() {
		return this.getEnegry;
	}
	public boolean getLifeStatus() {
		return this.life;
	}
	public boolean getEmptinessStatus() {
		return this.empty;
	}

	public Mouse(String name) {
		this.mouseName = name;	
		this.latestDirection = Direction.None;
		this.getEnegry = 3;
		this.life = false;
		this.empty = true;
		this.wayFromLife = new Stack<Direction>();
		this.wayBackToPosition = new Stack<Direction>();
		this.nameOfAlghoritm = "Standard";
	}	

	@Override
	public Direction NextMove(Action action) {
		if (nameOfAlghoritm == "Back to life") {
			if (!wayFromLife.empty()) {
				wayBackToPosition.push(wayFromLife.peek());		
				return reverse(wayFromLife.pop());
			}
			else
				nameOfAlghoritm = "The return";
		}

		if (!wayBackToPosition.empty() && nameOfAlghoritm == "The return" && getEnegry == 0) {
			wayFromLife.push(wayBackToPosition.peek()); // заполняем заново, вдруг снова придётся возвращаться
			return wayBackToPosition.pop();
		}

		if (wayBackToPosition.empty() && nameOfAlghoritm == "The return")
			nameOfAlghoritm = "Standard";
		
		switch (action) { 
		case Ok :
			itKilledMe = false;
			if (empty) { // если путь пуст, надо понять, где мы стоим 
				empty = false;
				return latestDirection; 
			}		
			
			if (life && !deleteDeadEnds(latestDirection)) {
				wayFromLife.push(latestDirection);
			}
			return ifItWasOk(latestDirection);

		case Fail :
			if (itKilledMe)
				setLifeCount(-1);
			if (getLifeCount() < 2) {
				nameOfAlghoritm = "Back to life";
				getEnegry = 4;
			}
			return ifThereIsWall(latestDirection);

		case Life :
			itKilledMe = false;
			if (!life)
				life = true;

			if (!wayFromLife.empty())
				wayFromLife.clear();

			if (getEnegry != 0) {
				getEnegry--;
				setLifeCount(1);
				return Direction.None;
			}
			else { 
				getEnegry = 4;
				return ifItWasOk(latestDirection);
			}
			
		case Dead : 
			itKilledMe = true;
			if (!deleteDeadEnds(latestDirection)) {
				wayFromLife.push(latestDirection);
			}
			if (getLifeCount() < 2)
				nameOfAlghoritm = "Back to life";
			return ifItWasOk(latestDirection);

		default : break;
		// сначала финишируем, потом умираем
		}
		return Direction.None; // при финише или полной потере жизней
	}

	/**
	 * @param latestDirection направление, по которому мышь уткнулась в стену
	 * @return направление, по которому мышь идёт теперь
	 */
	private Direction ifThereIsWall(Direction latestDirection) {
		switch (latestDirection) {
		case Right :
			setDirection(Direction.Up);
			return Direction.Up;
		case Up :
			setDirection(Direction.Left);
			return Direction.Left;
		case Left :
			setDirection(Direction.Down);
			return Direction.Down;
		case Down :
			setDirection(Direction.Right);
			return Direction.Right;
		case None :
			throw new RuntimeException("Неверное направление!");
		default :
			setDirection(Direction.None);
			return Direction.None;
		}
	}

	/**
	 * @param latestDirection напраление, по которому мышь шла до этого
	 * @return направление, по которому мышь идёт теперь
	 */
	private Direction ifItWasOk(Direction latestDirection) {
		switch (latestDirection) {
		case Right :
			setDirection(Direction.Down);
			return Direction.Down;
		case Up :
			setDirection(Direction.Right);
			return Direction.Right;
		case Left :
			setDirection(Direction.Up);
			return Direction.Up;
		case Down :
			setDirection(Direction.Left);
			return Direction.Left;
		default : // None
			setDirection(Direction.Right);
			return Direction.Right;
		}
	}

	/**
	 * @return противоположное направление
	 */
	private Direction reverse(Direction direction) {
		switch(direction) {
		case Up : return Direction.Down;
		case Down : return Direction.Up;
		case Right : return Direction.Left;
		case Left : return Direction.Right;
		default : return Direction.None;
		}
	}

	/**
	 * @param direction направление, которое собираемся положить в стек
	 * @return true, если последнее добавленное в стек направление противоположно переданному. т.к. направления, давшие Fail, не идут в стек, тупики - два противолопожных направления подряд
	 */
	private boolean deleteDeadEnds(Direction direction) {	
		if (!wayFromLife.empty() && direction.equals(reverse(wayFromLife.peek()))) {
//			System.out.println("проверяем " + direction + ", сравниваем с " + wayFromLife.peek() + ", превращённым в " + reverse(wayFromLife.peek()));
//			System.out.println("удаляем " + wayFromLife.peek());
			wayFromLife.pop();			
			return true;
		}
		return false;
	}
}