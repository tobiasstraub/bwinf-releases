import java.util.Random;
import java.util.Scanner;

public class Keycard {

	private Scanner sc = new Scanner(System.in);
	private Random rand = new Random();

	/**
	 * Declares the 3-dimensional array. First dimension: Number of Keycards
	 * Second dimension: x-coordinate of the Keycard Third dimension:
	 * y-coordination of the Keycard
	 * 
	 * @author JuliusFreudenberger
	 */
	private boolean[][][] keycard;
	private int numberKeycards;

	/**
	 * Is called at the start of the program. Controls the process of the
	 * program and calls the methods.
	 * 
	 * @author JuliusFreudenberger
	 * @param args
	 */

	public static void main(String[] args) {
		Keycard keycard = new Keycard();
		keycard.readNumberKeycards();
		keycard.generateKeycards();
		keycard.compareKeycards();
		keycard.printKeycard();
	}

	/**
	 * Generates the Keycards randomly.
	 * 
	 * @author JuliusFreudenberger
	 */
	public void generateKeycards() {
		for (int runNumberKeycard = 0; runNumberKeycard < numberKeycards; runNumberKeycard++) {
			for (int runXcoordinate = 0; runXcoordinate < 5; runXcoordinate++) {
				for (int runYcoordinate = 0; runYcoordinate < 5; runYcoordinate++) {
					keycard[runNumberKeycard][runXcoordinate][runYcoordinate] = rand.nextBoolean();
				}
			}
		}
	}

	/**
	 * Compares one Keycard with the others and changes them, when the compared
	 * point is the same.
	 * 
	 * @author JuliusFreudenberger
	 */
	public void compareKeycards() {
		for (int runNumberKeycard = 0; runNumberKeycard < numberKeycards; runNumberKeycard++) {
			for (int runXcoordinate = 0; runXcoordinate < 4; runXcoordinate++) {
				for (int runYcoordinate = 0; runYcoordinate < 4; runYcoordinate++) {
					for (int runNumberOtherKeycard = 0; runNumberOtherKeycard < numberKeycards
							- 1; runNumberOtherKeycard++)
						if (keycard[runNumberKeycard][runXcoordinate][runYcoordinate] == keycard[runNumberOtherKeycard][runXcoordinate][runYcoordinate]) {
							if (keycard[runNumberKeycard][runXcoordinate][runYcoordinate] == true) {
								keycard[runNumberKeycard][runXcoordinate][runYcoordinate] = false;
							} else {
								keycard[runNumberKeycard][runXcoordinate][runYcoordinate] = true;
							}
						}
				}
			}
		}
	}

	/**
	 * Prints the Keycards in for-Loops.
	 * 
	 * @author JuliusFreudenberger
	 */
	public void printKeycard() {
		for (int runNumberKeycard = 0; runNumberKeycard < numberKeycards; runNumberKeycard++) {
			for (int runXcoordinate = 0; runXcoordinate < 5; runXcoordinate++) {
				for (int runYcoordinate = 0; runYcoordinate < 5; runYcoordinate++) {
					if (keycard[runNumberKeycard][runXcoordinate][runYcoordinate]) {
						System.out.print("[1]");
					} else {
						System.out.print("[0]");
					}
				}
				System.out.print("\n");
			}
			System.out.print("\n");
		}
		System.out.println("[1]: Loch,  [0]: kein Loch");
	}

	/**
	 * Reads in the number of Keycards with a Scanner.
	 * 
	 * @author JuliusFreudenberger
	 */
	public void readNumberKeycards() {
		for (int i = 0; i < 4;) {
			System.out.print("Enter Number of Keycards: ");
			numberKeycards = sc.nextInt();
			if (Math.abs(numberKeycards) == numberKeycards) {
				i = 5;
			} else {
				System.out.println("The Number of Keycards must be positive!");
			}
		}
		/**
		 * Initializes the boolean keycard with the number of Keycard in their
		 * first dimension.
		 * 
		 * @author JuliusFreudenberger
		 */
		keycard = new boolean[numberKeycards][5][5];
	}
}
