public class Controller {

	public int OwnPPile;
	public int OpponentsPPile;
	public int OwnPoints;

	public static void main(String[] args) {
		Controller controller = new Controller();
		controller.PointMaker(controller);
	}

	public void PointMaker(Controller controller) {
		Reader reader = new Reader();
		OwnPoints = 6;
		while (OwnPPile < 100 && OpponentsPPile < 100) {
			System.out.println("Programm setzt: " + OwnPoints);
			reader.setOpponentsPoints();
			controller.ComparePoints(reader.getOpponentsPoints(), OwnPoints,
					controller);
			OwnPoints = controller.ChangeOwnPoints(reader.getOpponentsPoints(),
					OwnPoints);

		}
	}

	public void ComparePoints(int OpponentsPoints, int OwnPoints,
			Controller controller) {
		if (Math.abs(OpponentsPoints - OwnPoints) <= 5) {
			OpponentsPPile = OpponentsPPile + OpponentsPoints;
			OwnPPile = OwnPPile + OwnPoints;
		} else if (OpponentsPoints > OwnPoints) {
			OwnPPile = OwnPPile + OwnPoints;
		} else {
			OpponentsPPile = OpponentsPPile + OpponentsPoints;
		}
		System.out.println("Gewinnhaufen des Programms: " + OwnPPile);
		System.out.println("Gegnerischer Gewinnhaufen: " + OpponentsPPile);
	}

	public int ChangeOwnPoints(int OpponentsPoints, int OwnPoints) {
		if (OpponentsPoints - OwnPoints > 10) {
			OwnPoints = OwnPoints + 5;
		} else if (OpponentsPoints - OwnPoints < 10 || OwnPoints - OpponentsPoints < 0) {
			OwnPoints = OpponentsPoints + 5;
		}
		return OwnPoints;
	}
}