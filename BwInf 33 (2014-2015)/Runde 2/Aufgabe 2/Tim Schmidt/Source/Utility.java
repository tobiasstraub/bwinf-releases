package org.timschmidt.bwinf.a2;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Utility {

	static public Line2D.Double calcWurflinie (Point2D.Double ziel, double winkel, int zoom){
		int bahnlaenge = (int) (6 * Main.getRadius() * zoom); // Bahnlänge = dreifacher Durchmesser, damit die Wurfbahn selbst bei max. Verschiebung wie eine Sekante zum Kreis wirkt
		// TODO: Durch Verschiebung ersetzen
		double x = ziel.getX() * zoom, y = ziel.getY() * zoom;
		double sin = Math.sin(Math.toRadians(winkel)),
			cos = Math.cos(Math.toRadians(winkel));
		
		Point2D.Double 	p1 = new Point2D.Double(x - bahnlaenge/2 * sin,
							y - bahnlaenge/2 * cos),
						p2 = new Point2D.Double(x + bahnlaenge/2 * sin,
							y + bahnlaenge/2 * cos);

		
		Line2D.Double wurflinie = new Line2D.Double(p1, p2);
		
		return wurflinie;
	}
	
	static public Path2D.Double calcWurfbahn(Line2D.Double wurflinie, double winkel, int zoom){
		double sinB = Math.sin(Math.toRadians(winkel + 90)),
				cosB = Math.cos(Math.toRadians(winkel + 90));
		
		Point2D.Double	p1 = new Point2D.Double(wurflinie.getX1() - zoom * sinB,
							wurflinie.getY1() - zoom  * cosB),
						p2 = new Point2D.Double(wurflinie.getX1() + zoom * sinB,
								wurflinie.getY1() + zoom  * cosB),
						p3 = new Point2D.Double(wurflinie.getX2() + zoom * sinB,
								wurflinie.getY2() + zoom  * cosB),
						p4 = new Point2D.Double(wurflinie.getX2() - zoom * sinB,
								wurflinie.getY2() - zoom  * cosB);
		
		Path2D.Double wurfbahn = new Path2D.Double();
		
		wurfbahn.moveTo(p1.getX(), p1.getY());
		wurfbahn.lineTo(p2.getX(), p2.getY());
		wurfbahn.lineTo(p3.getX(), p3.getY());
		wurfbahn.lineTo(p4.getX(), p4.getY());
		wurfbahn.closePath();
		
		return wurfbahn;
	}
	
	static double orthoDistance(Kegel k, Point2D.Double ziel, double winkel){
		// Wurflinie wird zur Geraden umgewandelt
		double m = angleD2m(winkel),
				b = getB(m, ziel);
		/*System.out.println("W: " + winkel);
		System.out.println("m: " + m);
		System.out.println("x: " + ziel.getX() + " | y: " + ziel.getY());
		System.out.println("b: " + b);*/
		
		
		// x = (y-b)/m
		
		double seiteA = k.yKoordinate() - (m*k.xKoordinate()+b),
				seiteB = k.xKoordinate() - ((k.yKoordinate()-b)/m),
				seiteC = pythagoras(seiteA, seiteB);
		
		double d = seiteA * seiteB / seiteC;
		/*System.out.println("sA: " + seiteA);
		System.out.println("sB: " + seiteB);
		System.out.println("sC: " + seiteC);
		System.out.println("d: " + d);
		System.out.println("---");*/
		return d;
	}
	
	static double orthoDistance(Kegel k, double m, double b){
		/*System.out.println("m: " + m);
		System.out.println("x: " + ziel.getX() + " | y: " + ziel.getY());
		System.out.println("b: " + b);*/
		
		
		// x = (y-b)/m
		
		double seiteA = k.yKoordinate() - (m*k.xKoordinate()+b),
				seiteB = k.xKoordinate() - ((k.yKoordinate()-b)/m),
				seiteC = pythagoras(seiteA, seiteB);
		
		double d = seiteA * seiteB / seiteC;
		/*System.out.println("sA: " + seiteA);
		System.out.println("sB: " + seiteB);
		System.out.println("sC: " + seiteC);
		System.out.println("d: " + d);
		System.out.println("---");*/
		return d;
	}
	
	static double angleD2m(double angleD){
		// Steigung gleich dem Kotangens des Winkels v
		return 1 / Math.tan(Math.toRadians(angleD));
	}
	
	static double m2AngleD(double m){
		return Math.toDegrees(Math.atan(1 / m));
	}
	
	static double getB(double m, Point2D.Double p){
		return p.getY() - m * p.getX();
	}
	
	static double pythagoras(double a, double b){
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}
	
	static double[] calcMB(List<Kegel> kegellist){
		
		double m, b;
		 
		double summeX = 0,
				summeY = 0,
				summeXY = 0,
				summeXQuadrat = 0;
		
		int n = 0;
		for (Kegel k : kegellist)
			if (!k.umgeworfen()){
				summeX += k.xKoordinate();						// Summe Xi berechnen
				summeY += k.yKoordinate();						// Summe Yi berechnen
				summeXY += k.xKoordinate() * k.yKoordinate();			// Summe XiYi berechnen
				summeXQuadrat += Math.pow(k.xKoordinate(),2);		// Summe Xi² berechnen
				n++;
			}
		
		// Erklärung siehe Dokumentation
		double c = n * summeXQuadrat - Math.pow(summeX, 2);
		m = (n * summeXY - summeX * summeY) / c;
		b = (summeXQuadrat * summeY - summeX * summeXY) / c;
		
		double[] ret = {m, b};
		return ret;
	}
	
	static Point2D.Double findZiel (double m, double b){
		// x = (y-b)/m
		double schnittY = b,
				schnittX = (0-b)/m;
		Point2D.Double ziel = new Point2D.Double();
		
		if (schnittY < schnittX)
			ziel.setLocation(0, schnittY);
		else
			ziel.setLocation(schnittX, 0);
		
		return ziel;		
	}
	
	static List<Kegel> cloneKegellist(List<Kegel> src){
		List<Kegel> dest = new ArrayList<Kegel>();
		for (Kegel k : src)
			dest.add(k);
		return dest;
	}
	
	static boolean aussenkegelEntfernen(List<Kegel> list, double m, double b){
		if (list.size() < 1)
			return false;
		
		int indexHighestDistance = 0;
		double highestDistance = 0;
		for (int i = 0; i < list.size(); i++){
			Kegel k = list.get(i);
			if (!k.umgeworfen()){
				double d = Math.sqrt(Math.pow(Utility.orthoDistance(k, m, b),2));
				if (d > highestDistance){
					highestDistance = d;
					indexHighestDistance = i;
				}
			}
		}
		
		if (highestDistance > 1){
			list.remove(indexHighestDistance);
			return true;
		} else
			return false;
	}
	
	static int innenkegelzaehlen(List<Kegel> list, double m, double b){
		int c = 0;
		for (Kegel k : list)
			if (!k.umgeworfen() && Math.sqrt(Math.pow(orthoDistance(k, m, b),2)) < 1)
				c++;
		
		return c;
	}
}
