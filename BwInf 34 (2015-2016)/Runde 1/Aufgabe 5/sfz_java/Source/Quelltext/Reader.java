import java.util.Scanner;

public class Reader {
	private Scanner sc = new Scanner(System.in);
	private int OpponentsPoints;
	
	public void setOpponentsPoints() {

		System.out.print("Der Gegner setzt ?: ");
		OpponentsPoints = sc.nextInt();
	}
	public int getOpponentsPoints() {
		
		return OpponentsPoints;
	}
}