public class AI {

    /**
     * Breite / Höhe des Spielfeldes
     */
    private final static int WIDTH = 65, HEIGHT = 60;

    /**
     * lastX und lastY: Koordinaten für die vorherige Position des Balles
     * firstX und firstY: Koordinaten für die erste Position des Balles seit dem
     * letzten Ändern des Winkels. Wenn diese -1 sind, bedeutet das, dass sie
     * noch nie verändert wurden und deswegen das Spiel noch nicht gestartet
     * wurde bzw. gerade der erste Zug gemacht wird.
     * abgepralltX: X-Koordinate des Balles als er das letzte Mal abgeprallt ist
     * Alle Werte sind standardmäßig auf -1 gesetzt.
     */
    private int lastX = -1, lastY = -1, firstX = -1, firstY = -1, abgepralltX = -1;

    /**
     * Anzahl der Bewegungen des Balles seit dem letzten Abprall.
     */
    private int zuegeSeitAbprall;

    public void zug(int id, Spiel.Zustand zustand, Spiel.Zug zug) {

        Spiel.Zustand.Ball ball = zustand.listeBall().get(0);
        int x = ball.xKoordinate();
        int y = ball.yKoordinate();

        // Prüfen, ob der Ball irgendwo Schläger abgeprallt ist:
        boolean yAbgeprallt = lastX != -1 &&
                Math.abs(Math.signum(firstY - lastY) - Math.signum(lastY - y)) == 2;
        // Wenn Y-Richtung vorher (firstY - lastY) anders als jetzt (lastY - y) ist.
        boolean xAbgeprallt = lastX != -1 &&
                Math.abs(Math.signum(firstX - lastX) - Math.signum(lastX - x)) == 2;
        // Wenn X-Richtung vorher (firstX - lastX) anders als jetzt (lastX - x) ist.

        // Dabei muss die Differenz der Vorzeichen 2 sein, um sicherzustellen,
        // dass der Ball in die entgegengesetzte Richtung fliegt.

        boolean move = true; // Ob der Schläger sich bewegen soll.

        // abgeprallt() aufrufen, wenn der allererste Zug ausgeführt wird
        // (abgepralltX == -1) oder wenn der Ball irgendwo abgeprallt ist.
        if (abgepralltX == -1 || yAbgeprallt || xAbgeprallt)
            move = abgeprallt(xAbgeprallt, x, y);

        // Hier bewegt sich der Schläger:
        if (move)
            move(x, y, zug, zustand, id);
    }

    /**
     * Verarbeitet die Informationen, wenn der Ball abgeprallt ist.
     *
     * @param xAbgeprallt ob der Ball an der X-Koordinate abgeprallt ist
     * @param x           aktuelle X-Koordinate des Balles
     * @param y           aktuelle Y-Koordinate des Balles
     * @return {@code true} wenn sich der Schläger bewegen soll,
     * {@code false} wenn nicht
     */
    private boolean abgeprallt(boolean xAbgeprallt, int x, int y) {

        // Normalerweise kann sich der Schläger bewegen.
        boolean move = true;

        // Wenn der Ball NUR am oberen oder unteren Rand (dafür müssen die anderen
        // Bedingungen zur Zurücksetzung alle falsch sein) abgeprallt ist, sonst
        // könnte es z. B. mit xAbgeprallt zusammen wahr sein.
        // In diesem Fall hat der Ball seinen Winkel NICHT verändert.
        if (abgepralltX != -1 && !xAbgeprallt) {
            // Da der erste Punkt nach dem Abprall die Berechnung verfälschen würde,
            // wird der erste Punkt an der oberen bzw. unteren Seite "gespiegelt":

            // Wenn der Ball oben abgeprallt ist (y muss dafür größer sein als
            // vorher), Punkt einfach negieren:
            if (lastY < y)
                firstY = -firstY;
            // Wenn der Ball unten abgeprallt ist (y kleiner als vorher), muss der
            // Punkt unter dem Spielfield sein. Dazu wird zur Spielfeldhöhe der
            // Abstand zwischen der Y-Koordinate und der Spielfeldhöhe addiert:
            else
                firstY = HEIGHT + (HEIGHT - firstY);
        }

        // Ansonsten ist dies der erste Zug (abgepralltX == -1) oder der Ball ist an
        // einem Schläger abgeprallt. In diesem Fall ist der Winkel noch nicht
        // bekannt bzw. kann sich geändert haben. Deswegen werden die
        // Startkoordinaten firstX und firstY auf die aktuelle Position gesetzt.
        else {
            // Neue Messung muss vorgommen werden, der erste Punkt wird auf aktuelle
            // Koordinate aktualisiert.
            firstX = x;
            firstY = y;

            // Bewegen kann sich der Schläger noch nicht, da der Winkel
            // möglicherweise verändert wurde.
            move = false;
        }

        // Folgendes tritt IMMER ein, wenn der Ball irgendwo abgeprallt ist bzw. das
        // Spiel gestartet wurde:

        // Die X-Koordinate des letzten Abpralls auf die aktuelle X-Koordinate
        // aktualisieren und zuegeSeitAbprall zurücksetzen.
        abgepralltX = x;
        zuegeSeitAbprall = 0;

        // Die letzten Koordinaten zurücksetzen.
        lastX = -1;
        lastY = -1;

        return move;
    }

    /**
     * Bewegt den Schläger.
     *
     * @param x       X-Koordinate des Balles
     * @param y       Y-Koordinate des Balles
     * @param zug     Spielzug
     * @param zustand Spielzustand
     * @param id      Identifikation des eigenen Schlägers
     */
    private void move(int x, int y, Spiel.Zug zug, Spiel.Zustand zustand, int id) {

        int schlaeger = getOwnSchlaeger(zustand, id).yKoordinate();

        // Wenn der Ball sich zu der anderen Seite hinbewegt, geht der Schläger
        // zurück in die Mitte (Höhe / 2 - 3, da die Variable schlaeger der
        // oberste Punkt des Schlägers ist, er soll sich aber mit der Mitte des
        // Schlägers in die Mitte stellen):
        if (firstX < x) {
            if (schlaeger < HEIGHT / 2 - 3) zug.nachUnten();
            else if (schlaeger > HEIGHT / 2 - 3) zug.nachOben();
        }

        // Der Ball bewegt sich auf den eigenen Schläger zu; Aufprall berechnen und
        // dorthin bewegen:
        else {
            // Steigung des Balles berechnen (y / x). Wenn er sich auf der
            // X-Koordinate noch nicht bewegt hat, wird der Tangenz von (-)70 Grad
            // berechnet, da der größtmögliche Winkel des Balles (-)70 Grad ist.
            // Positiv, wenn der Ball nach oben fliegt, negativ, wenn die Richtung
            // nach unten zeigt (wegen firstY - y).
            double m = (firstX - x) == 0 ?
                    (Math.tan(Math.toRadians(firstY - y > 0 ? 70 : -70))) :
                    (firstY - y) / (double) (firstX - x);

            // Aufprall des Balles berechnen:
            int aufprall = aufprall(m, x, y);

            // Ball kommt über dem mittleren Drittel auf; nach oben bewegen:
            if (aufprall < schlaeger + 2)
                zug.nachOben();
            // Ball kommt unter dem mittlere Drittel auf; nach unten bewegen:
            else if (aufprall > schlaeger + 3)
                zug.nachUnten();
            // Schläger steht an der richtigen Stelle, weitere Aktion berechnen:
            else {
                // Durchschnittliche Bewegung des Balles auf X pro Zug berechnen:
                double durchschnittX = (abgepralltX - x) / (double) zuegeSeitAbprall;

                // Wenn der Ball im nächsten Zug aufprallen sollte:
                if (x / durchschnittX <= 1) {
                    boolean gegnerFaengtAb = gegnerBekommtBall(-m, aufprall,
                            getOtherSchlaeger(zustand, id).yKoordinate(),
                            durchschnittX);

                    // Nach unten gehen, wenn der Schläger den Ball mit dem
                    // mittleren Drittel oben bekommen und wenn der Gegner den Ball
                    // bei normaler Position nicht bekommen würde.
                    if (gegnerFaengtAb && aufprall == schlaeger + 2)
                        zug.nachUnten();
                    // Nach oben gehen, wenn der Schläger den Ball mit dem
                    // mittleren
                    // Drittel unten bekommen und wenn der Gegner den Ball bei
                    // normaler Position nicht bekommen würde.
                    else if (gegnerFaengtAb && aufprall == schlaeger + 3)
                        zug.nachOben();
                }
            }
        }

        // Die letzten Koordinaten aktualisieren und Anzahl der Züge erhöhen.
        lastX = x;
        lastY = y;
        zuegeSeitAbprall++;
    }

    /**
     * @return den eigenen Schläger
     */
    private Spiel.Zustand.Schlaeger getOwnSchlaeger(Spiel.Zustand zustand, int id) {
        if (zustand.listeSchlaeger().get(0).identifikation() == id)
            return zustand.listeSchlaeger().get(0);
        return zustand.listeSchlaeger().get(1);
    }

    /**
     * @return den gegnerischen Schläger
     */
    private Spiel.Zustand.Schlaeger getOtherSchlaeger(Spiel.Zustand zustand, int
            id) {
        if (zustand.listeSchlaeger().get(0).identifikation() == id)
            return zustand.listeSchlaeger().get(1);
        return zustand.listeSchlaeger().get(0);
    }

    /**
     * Berechnet den Aufprall des Balles.
     *
     * @param m Steigung, nach oben positiv, nach unten negativ
     * @param x die zurückzulegende Strecke des Balles auf der X-Koordinate
     * @param y die aktuelle Y-Koordinate des Balles
     * @return die Y-Koordinate, wo der Ball aufkommen wird
     */
    private int aufprall(double m, int x, int y) {

        // Länge der Strecke, welche der Ball noch nach OBEN oder UNTEN
        // zurückzulegen hat, um am Schläger anzukommen. Dabei wird die Strecke
        // auf X vernachlässigt.
        // Positiv, wenn der Ball nach oben fliegt, negativ, wenn die Richtung nach
        // unten zeigt (wegen m).
        int h = (int) (x * m);

        // Aufprall des Balles. Strecke nach oben oder nach unten von der aktuellen
        // Position subtrahiert. Wenn h negativ ist (Ball fliegt nach unten), ist der
        // Aufprall auch weiter unten.
        int aufprall = y - h;

        // Wenn der Ball laut der bisherigen Berechnung das Spielfeld nach oben oder
        // unten hin verlässt.
        // while statt if, da der Ball mehrmals abprallen kann.
        while (aufprall < 0 || aufprall > HEIGHT) {
            // Ball prallt oben ab; Aufpall einfach negieren um dies auszugleichen:
            if (aufprall < 0)
                aufprall = -aufprall;
                // Ball prallt unten ab; Abstand des Balles zum Rand von der
                // Spielfeldhöhe abziehen, um korrekten Aufprall zu bekommen.
            else if (aufprall > HEIGHT)
                aufprall = HEIGHT - (aufprall - HEIGHT);
        }

        return aufprall;
    }

    /**
     * @param m             Steigung, nach oben positiv, nach unten negativ
     * @param start         Y-Koordinate des Punktes, bei dem der Ball starten wird
     * @param gegner        aktuelle Y-Koordinate des Gegners
     * @param durchschnittX durchschnittliche Bewegung des Balles auf der
     *                      X-Koordinate
     */
    private boolean gegnerBekommtBall(double m, int start, int gegner,
                                      double durchschnittX) {
        int aufprall = aufprall(m, WIDTH, start);
        double zuege = WIDTH / durchschnittX;
        return (aufprall < gegner && gegner - aufprall < zuege) ||
                (aufprall > gegner + 5 && aufprall - gegner + 5 < zuege);
    }

}
