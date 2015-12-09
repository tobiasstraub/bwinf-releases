package a2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;


public class View extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static final String ACT_TURN = "TURN",
			ACT_AUTO_ON = "AUTO_ON",
			ACT_AUTO_OFF = "AUTO_OFF";
	private JButton btnNextTurn;
	private PanelSim panelSim;
	private JButton btnSchnellZiehenAn;
	private JButton btnSchnellZiehenAus;
	private JLabel lblFutterGesammeltDesc;
	private JLabel lblFutterGesammelt;
	
	public View(){
		// Eigenschaften des JFrames festlegen
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		this.setTitle("Ameisenfutter - ByteSector");
		
		// Panel zur Gruppierung der Buttons
		JPanel panelControl = new JPanel();
		getContentPane().add(panelControl, BorderLayout.SOUTH);
		
		// Button für die manuelle Steuerung
		btnNextTurn = new JButton("Nächster Zug!");
		btnNextTurn.setActionCommand(ACT_TURN);
		panelControl.add(btnNextTurn);
		
		// Button zum Starten der automatischen Zugausführung
		btnSchnellZiehenAn = new JButton("Schnell ziehen AN");
		btnSchnellZiehenAn.setActionCommand(ACT_AUTO_ON);
		panelControl.add(btnSchnellZiehenAn);
		
		// Button zum Stoppen der automatischen Zugausführung
		btnSchnellZiehenAus = new JButton("Schnell ziehen AUS");
		btnSchnellZiehenAus.setEnabled(false);
		btnSchnellZiehenAus.setActionCommand(ACT_AUTO_OFF);
		panelControl.add(btnSchnellZiehenAus);
		
		// Label zur Darstellung des bisher gesammelten Futters
		lblFutterGesammeltDesc = new JLabel("Futter gesammelt:");
		panelControl.add(lblFutterGesammeltDesc);
		lblFutterGesammelt = new JLabel("0");
		panelControl.add(lblFutterGesammelt);
		
		// Label zur Darstellung der bisher gemachten Züge
		lblZgeInsgesamtDesc = new JLabel("Züge insgesamt:");
		panelControl.add(lblZgeInsgesamtDesc);
		lblZgeInsgesamt = new JLabel("0");
		panelControl.add(lblZgeInsgesamt);
		
		// Panel zur Anzeige der Simulationsdaten
		panelSim = new PanelSim();
		panelSim.setPreferredSize(new Dimension(1000,1000));
		getContentPane().add(panelSim, BorderLayout.CENTER);
		
		pack();
		
	}
	
	// Festlegen der Farben
	private final Color CLR_NEST = new Color(114, 54, 0);
	private final Color CLR_FUTTER = new Color(255, 255, 255);
	private final Color CLR_DUFT = new Color(178, 0, 255);
	private final Color CLR_AMEISE = new Color(0, 0, 0);
	private JLabel lblZgeInsgesamtDesc;
	private JLabel lblZgeInsgesamt;
	
	public void updateView(MapTile[][] world, Ameise[] ameisen){
		BufferedImage image = new BufferedImage(panelSim.getWidth(), panelSim.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = image.createGraphics();
		
		g.setColor(new Color(0, 100, 0));
		g.fillRect(0, 0, panelSim.getWidth(), panelSim.getHeight());
		
		// Array zum Speichern der Farbe jedes einzufärbenden Feldes
		Color[][] worldClr = new Color[500][500];
		
		// Ameisen werden beim Färben zuerst betrachtet, da sie die niedrigste Priorität haben und ggf. überfärbt werden.
		for (Ameise a : ameisen){
			int x = a.getPos().getX(), y = a.getPos().getY();
				worldClr[x][y] = CLR_AMEISE;
		}
		
		for (int i = 0; i < world.length; i++)
			for (int j = 0; j < world.length; j++){
				MapTile curTile = world[i][j];
				
				// Nest braun einfärben
				if (curTile.isNest()){
					worldClr[i][j] = CLR_NEST;
					continue;
				}
				
				// Futter weiß einfärben
				if (curTile.getFutter() > 0){
					worldClr[i][j] = CLR_FUTTER;
					continue;
				}				
				
				// Duft violett einfärben
				if (curTile.getDuft() > 0){
					worldClr[i][j] = CLR_DUFT;
					continue;
				}
			}
		
		// Image herstellen
		for (int i = 0; i < worldClr.length; i++)
			for (int j = 0; j < worldClr.length; j++){
				if (worldClr[i][j] != null){
					g.setColor(worldClr[i][j]);
					g.drawRect(i*2, j*2, 1, 1);					// Jedes Feld wird mit 2x2 Pixeln dargestellt
				}
			}
				
		panelSim.updateImage(image);
		panelSim.repaint();
	}
	
	public JButton getBtnNextTurn() {
		return btnNextTurn;
	}
	
	public JButton getBtnSchnellZiehenAn() {
		return btnSchnellZiehenAn;
	}
	public JButton getBtnSchnellZiehenAus() {
		return btnSchnellZiehenAus;
	}
	public JLabel getLblFutterGesammelt() {
		return lblFutterGesammelt;
	}
	public JLabel getLblZgeInsgesamt() {
		return lblZgeInsgesamt;
	}
}
