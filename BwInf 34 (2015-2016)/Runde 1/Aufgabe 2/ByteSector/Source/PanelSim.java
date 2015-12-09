package a2;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PanelSim extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (image != null)
			g.drawImage(image, 0, 0, null);
	}
	
	public void updateImage(BufferedImage image){
		this.image = image;
	}
	
}
