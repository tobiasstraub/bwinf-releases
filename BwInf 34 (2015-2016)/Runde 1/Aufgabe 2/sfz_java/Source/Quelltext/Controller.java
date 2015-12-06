
public class Controller {

	private final int mapSize = 500;
	private final int amountFoodsources = 5;
	private final int amountAnts = 100;
	private final int sizeOfFoodsources = 50;
	private final int losingOlfactoryPointsTime = 12;
	private final int downRightCornerOfNestXCoordinate = mapSize/2;
	private final int downRightCornerOfNestYCoordinate = mapSize/2;
	
	
	public static void main(String[] args) throws InterruptedException {

		Controller controller = new Controller();
		Map map = new Map(controller);

		map.createMap();
		map.generateNestOnMap();
		map.generateFoodOnMap();
		map.printMap();

		for (int i = 1; i > 0; i++) {
			map.moveAntsWithoutFoodAtCorners();
			map.moveAntsWithoutFoodAtBorder();
			map.moveAntsWithoutFoodInMiddle();
			map.moveAntsWithFoodInMiddle();
			map.moveAntsWithFoodAtBorder();
			map.releaseAntsFromFoodsources();
			map.releaseAntsFromNest();
			if (i % controller.losingOlfactoryPointsTime == 0) {
				map.loseOlfactoryPoints();
				i = 1;
			}
			if (map.getAmountOfFoodInNest() == controller.getAmountFoodsources() * controller.getSizeOfFoodsources()) {
				System.exit(0);
			}
		}
	}

	public int getMapSize() {
		return mapSize;
	}

	public int getAmountFoodsources() {
		return amountFoodsources;
	}

	public int getAmountAnts() {
		return amountAnts;
	}

	public int getSizeOfFoodsources() {
		return sizeOfFoodsources;
	}
	
	public int getDownRightCornerOfNestXCoordinate() {
		return downRightCornerOfNestXCoordinate;
	}

	public int getDownRightCornerOfNestYCoordinate() {
		return downRightCornerOfNestYCoordinate;
	}
}
