package a2;

public class Main {

	public static void main(String[] args) {
		// Einstellungen für die verschiedenen Werte übernehmen
		int amAmeisen = Integer.parseInt(args[0]),
				amFutterquellen = Integer.parseInt(args[1]),
				tVerdunstung = Integer.parseInt(args[2]);
		Position posNest = new Position(Integer.parseInt(args[3]),
										Integer.parseInt(args[4]));
		
		// Model, View und Controller erstellen
		Model model = new Model(amAmeisen, posNest, amFutterquellen, tVerdunstung);
		View view = new View();
		new Controller(model, view);
	}

}
