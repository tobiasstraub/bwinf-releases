import java.util.ArrayList;

public class AI{

    public int runde = 0;
    
    public int verschiebung = 1;
    
    public Spiel.Zustand.Ball position1;
    public Spiel.Zustand.Ball position2;
    public Spiel.Zustand.Ball position3;
    public Spiel.Zustand.Ball position4;
    public Spiel.Zustand.Ball position5;
    public Spiel.Zustand.Ball position6;
    public Spiel.Zustand.Ball position7;
    public Spiel.Zustand.Ball position8;
    public Spiel.Zustand.Ball position9;
    public Spiel.Zustand.Ball position10;
    
    public double ptmt;
    public int punktzahl;

    public void zug(int id, Spiel.Zustand zustand, Spiel.Zug zug){
        this.runde++;
    
        /* Bei neuer Runde alles löschen */
        int punktzahl = this.getMe(zustand, id).punktzahl() + this.getEnemy(zustand, id).punktzahl();
        if (punktzahl != this.punktzahl){
            this.position10 = null;
            this.position9 = null;
            this.position8 = null;
            this.position7 = null;
            this.position6 = null;
            this.position5 = null;
            this.position4 = null;
            this.position3 = null;
            this.position2 = null;
            this.position1 = null;
            this.ptmt = 0;
            this.punktzahl = punktzahl;
            
            this.ausgabe("========== Punktzahl geändert", zug);
        }
        
        this.position10 = this.position9;
        this.position9 = this.position8;
        this.position8 = this.position7;
        this.position7 = this.position6;
        this.position6 = this.position5;
        this.position5 = this.position4;
        this.position4 = this.position3;
        this.position3 = this.position2;
        this.position2 = this.position1;
        this.position1 = this.getBall(zustand);
        
        ArrayList<Spiel.Zustand.Ball> tempListe = new ArrayList<Spiel.Zustand.Ball>();
        tempListe.add(this.position1);
        tempListe.add(this.position2);
        tempListe.add(this.position3);
        
        if (inEinerReihe(tempListe)){
            
            double t1 = 0;
            double t2 = 0;
            double t3 = 0;
            double t5 = 0;
            double t6 = 0;
            double geschwindigkeit = 0;
            
            tempListe.add(this.position4);
            tempListe.add(this.position5);
            
            if (this.inEinerReihe(tempListe)){ /* 5 Punkte? */
                tempListe.add(this.position6);
                tempListe.add(this.position7);
                
                if (this.inEinerReihe(tempListe)){ /* 7 Punkte? */
                    tempListe.add(this.position8);
                    tempListe.add(this.position9);
                    tempListe.add(this.position10);
                    
                    if (this.inEinerReihe(tempListe)){ /* 10 Punkte? */
                        /* Regression aus 10 Punkten */
                        double x1 = this.position1.xKoordinate();
                        double y1 = this.position1.yKoordinate();
                        double x2 = this.position2.xKoordinate();
                        double y2 = this.position2.yKoordinate();
                        double x3 = this.position3.xKoordinate();
                        double y3 = this.position3.yKoordinate();
                        double x4 = this.position4.xKoordinate();
                        double y4 = this.position4.yKoordinate();
                        double x5 = this.position5.xKoordinate();
                        double y5 = this.position5.yKoordinate();
                        double x6 = this.position6.xKoordinate();
                        double y6 = this.position6.yKoordinate();
                        double x7 = this.position7.xKoordinate();
                        double y7 = this.position7.yKoordinate();
                        double x8 = this.position8.xKoordinate();
                        double y8 = this.position8.yKoordinate();
                        double x9 = this.position9.xKoordinate();
                        double y9 = this.position9.yKoordinate();
                        double x10 = this.position10.xKoordinate();
                        double y10 = this.position10.yKoordinate();

                        t1 = x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10;
                        t2 = y1 + y2 + y3 + y4 + y5 + y6 + y7 + y8 + y9 + y10;
                        t3 = x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10;
                        t5 = x1 * x1 + x2 * x2 + x3 * x3 + x4 * x4 + x5 * x5 + x6 * x6 + x7 * x7 + x8 * x8 + x9 * x9 + x10 * x10;
                        t6 = 10;
                        geschwindigkeit = (double) Math.abs(x1 - x10)/10;
                        
                    }else{
                        /* Regression aus 7 Punkten */
                        double x1 = this.position1.xKoordinate();
                        double y1 = this.position1.yKoordinate();
                        double x2 = this.position2.xKoordinate();
                        double y2 = this.position2.yKoordinate();
                        double x3 = this.position3.xKoordinate();
                        double y3 = this.position3.yKoordinate();
                        double x4 = this.position4.xKoordinate();
                        double y4 = this.position4.yKoordinate();
                        double x5 = this.position5.xKoordinate();
                        double y5 = this.position5.yKoordinate();
                        double x6 = this.position6.xKoordinate();
                        double y6 = this.position6.yKoordinate();
                        double x7 = this.position7.xKoordinate();
                        double y7 = this.position7.yKoordinate();
                        
                        geschwindigkeit = (double) Math.abs(x1 - x7)/7;
                        
                        t1 = x1 + x2 + x3 + x4 + x5 + x6 + x7;
                        t2 = y1 + y2 + y3 + y4 + y5 + y6 + y7;
                        t3 = x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7;
                        t5 = x1 * x1 + x2 * x2 + x3 * x3 + x4 * x4 + x5 * x5 + x6 * x6 + x7 * x7;
                        t6 = 7;
                        
                    }
                }else{
                    /* Regression aus 5 Punkten */
                    double x1 = this.position1.xKoordinate();
                    double y1 = this.position1.yKoordinate();
                    double x2 = this.position2.xKoordinate();
                    double y2 = this.position2.yKoordinate();
                    double x3 = this.position3.xKoordinate();
                    double y3 = this.position3.yKoordinate();
                    double x4 = this.position4.xKoordinate();
                    double y4 = this.position4.yKoordinate();
                    double x5 = this.position5.xKoordinate();
                    double y5 = this.position5.yKoordinate();
                    
                    geschwindigkeit = (double) Math.abs(x1 - x5)/5;
                    
                    t1 = x1 + x2 + x3 + x4 + x5;
                    t2 = y1 + y2 + y3 + y4 + y5;
                    t3 = x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5;
                    t5 = x1 * x1 + x2 * x2 + x3 * x3 + x4 * x4 + x5 * x5;
                    t6 = 5;
                    
              }
              }else{
                /* Regression aus 3 Punkten */
                double x1 = this.position1.xKoordinate();
                double y1 = this.position1.yKoordinate();
                double x2 = this.position2.xKoordinate();
                double y2 = this.position2.yKoordinate();
                double x3 = this.position3.xKoordinate();
                double y3 = this.position3.yKoordinate();


                geschwindigkeit = (double) Math.abs(x1 - x3)/3;
                
                t1 = x1 + x2 + x3;
                t2 = y1 + y2 + y3;
                t3 = x1 * y1 + x2 * y2 + x3 * y3;
                t5 = x1 * x1 + x2 * x2 + x3 * x3;
                t6 = 3;
                
            }
            
            double b = (double) (((t6 * t3) - (t1 * t2))/((t6 * t5) - (t1 * t1)));    
            double a = (double) (((t5 * t2) - (t1 * t3))/((t6 * t5) - (t1 * t1)));
            
            
            
            //Der Ball bewegt sich auf der Regressionsgerade a + b * x mit der Geschwindigkeit 'geschwindigkeit' x pro Zug
            
            if (this.position1.xKoordinate() < this.position3.xKoordinate()){
            
            
                //Auftreffpunkt und -Winkel berechnen
                double x = a;
                int anzahl = 0;
                while(x < 0 || x > 60){
                    if (x < 0){ /* obere Bande */
                        x = -x;
                    }else if (x > 60){ /* untere Bande */
                        x = 60 - ( x - 60);
                    }
                    anzahl++;
                }
                if (anzahl % 2 != 0){ b = -b; }
                
                //b: Winkel, mit dem der Ball bei mir ankommt
                //x: Punkt, an dem er ankommt
                
                this.ptmt = x;
                
                double benoetigteZuege = (double) 65/geschwindigkeit;
                
                double b1 = 2.747477419 - (Math.abs(2.747477419 - (-b))/2); // oberes Drittel
                double b2 = (-2.747477419) + (Math.abs((-2.747477419) - (-b))/2); // unteres Drittel
                double b3 = -b; //mittleres Drittel
                
                double auftreffpunkt1 = a + b1 * 65;
                double auftreffpunkt2 = a + b2 * 65;
                double auftreffpunkt3 = a + b3 * 65;
                
                if (benoetigteZuege < (Math.abs(this.auftreffpunkt(auftreffpunkt1) - (this.getEnemy(zustand, id).yKoordinate() + 2.5))) ){
                    //Todesstoß mit dem oberen Drittel
                    this.ptmt += this.verschiebung; //Das obere Drittel zum PTMT verschieben
                    //this.ausgabe("Todesstoß mit dem oberen Drittel.", zug);
                }else if(benoetigteZuege < (Math.abs(this.auftreffpunkt(auftreffpunkt2) - (this.getEnemy(zustand, id).yKoordinate() + 2.5)))){
                    //Todesstoß mit dem unteren Drittel
                    this.ptmt -= this.verschiebung; //Das untere Drittel zum PTMT verschieben
                    //this.ausgabe("Todesstoß mit dem unteren Drittel.", zug);
                }else if(benoetigteZuege < (Math.abs(this.auftreffpunkt(auftreffpunkt3) - (this.getEnemy(zustand, id).yKoordinate() + 2.5)))){
                    //Todesstoß mit der Mitte
                    //Keine Aktion nötig, da ptmt = x
                }else{
                    //Ist es möglich, in die Ecken zu spielen?
                    if (this.auftreffpunkt(auftreffpunkt1) <= 8 || this.auftreffpunkt(auftreffpunkt1) >= 52 ){
                        //oberes Drittel benutzen
                        this.ptmt += this.verschiebung;
                    }else if(this.auftreffpunkt(auftreffpunkt2) <= 8 || this.auftreffpunkt(auftreffpunkt2) >= 52 ){
                        //unteres Drittel benutzen
                        this.ptmt -= this.verschiebung;
                    }else if(this.auftreffpunkt(auftreffpunkt3) <= 8 || this.auftreffpunkt(auftreffpunkt3) >= 52 ){
                        //Keine Aktion nötig
                    }else{
                        //Das zum Ball gerichtete äußere Drittel benutzen
                        this.ptmt += (b > 0) ? this.verschiebung : -this.verschiebung;
                    }
                 }
            }else{
                //Auftreffpunkt und -winkel beim Gegner berechnen
                double gegnerPosition = this.getEnemy(zustand, id).yKoordinate();
                double x = a + b * 65;
                int anzahl = 0;
                while(x < 0 || x > 60){
                    if (x < 0){
                        x = -x;
                    }else if (x > 60){
                        x = 60 - ( x - 60);
                    }
                    anzahl++;
                }
                if (anzahl % 2 != 0){ b = -b; }
                
                double auftreffpunktBeimGegner = x;
                double auftreffpunktBeimGegnerGerundet = Math.round(x);
                
                /* Ist der gegnerische Schläger am berechneten Punkt? */
                if (auftreffpunktBeimGegnerGerundet == gegnerPosition || auftreffpunktBeimGegnerGerundet == gegnerPosition + 1){
                    //Oberes Drittel
                    double ausfallswinkel = 2.747477419 - (Math.abs(2.747477419 - (-b))/2);
                    this.ptmt = this.auftreffpunkt(auftreffpunktBeimGegner + ausfallswinkel * 65);
                }else if (auftreffpunktBeimGegnerGerundet == gegnerPosition + 2 || auftreffpunktBeimGegnerGerundet == gegnerPosition + 3){
                    //mittleres Drittel
                    double ausfallswinkel = -b;
                    this.ptmt = this.auftreffpunkt(auftreffpunktBeimGegner + ausfallswinkel * 65);
                }else if (auftreffpunktBeimGegnerGerundet == gegnerPosition + 4 || auftreffpunktBeimGegnerGerundet == gegnerPosition + 5){
                    //unteres Drittel
                    double ausfallswinkel = (-2.747477419) + (Math.abs((-2.747477419) - (-b))/2);
                    this.ptmt = this.auftreffpunkt(auftreffpunktBeimGegner + ausfallswinkel * 65);
                }else{
                    this.ptmt = 30.0; //Anscheinend ist der gegnerische Schläger nicht am Auftreffpunkt
                }
                this.ptmt = 30.0 + ((this.ptmt - 30.0)/2);
            }
            
            this.doMove(this.ptmt, this.getMe(zustand, id), zug);
            return;
            
        }else{
            if (this.ptmt != 0){
                //PTMT befolgen
                
                this.doMove(this.ptmt, this.getMe(zustand, id), zug);
            }else{
                //Auch kein PTMT vorhanden
                //-> zum Ball bewegen
                this.doMove(this.getBall(zustand).yKoordinate(), this.getMe(zustand, id), zug);
                return;
            }
        }
    }
    
    /* Berechnet den Punkt, an dem der Ball an der Wand auftreffen wird */
    public double auftreffpunkt(double punkt){
        while (punkt < 0 || punkt > 60){
            
            if(punkt < 0){
                punkt = -punkt;
            }
            if (punkt > 60){
                punkt = 60 - (punkt - 60);
            }
        }
        return punkt;
    }
    
    /* Gibt unter Angabe der Rundennummer den Text 'text' aus  */
    public void ausgabe(String text, Spiel.Zug zug){
        zug.ausgabe("[" + this.runde + "] " + text);       
    }                
    
    /* Bewegt den Schläger in Richtung des ptmt */
    public void doMove(double ptmt, Spiel.Zustand.Schlaeger me, Spiel.Zug zug){
        double meinePosition = me.yKoordinate() + 2.5;
        
        ptmt = Math.round(ptmt);
        meinePosition = Math.round(meinePosition);
    
        if (meinePosition < ptmt) zug.nachUnten();
        if (meinePosition > ptmt) zug.nachOben();
        return; //Nötig; falls meinePosition == ptmt
    }
    
    /* Gibt zurück, ob die Punkte in 'liste' xy-linear sind */
    public boolean inEinerReihe(ArrayList<Spiel.Zustand.Ball> liste){
        
        for(int i = 0; i < liste.size(); i++){
            if (liste.get(i) == null) return false;
        }
        
        boolean OKx1 = true;
        boolean OKx2 = true;
        boolean OKy1 = true;
        boolean OKy2 = true;
        
        for(int i = 0; i < liste.size(); i++){
            if (i >= 2){
                if (liste.get(i).xKoordinate() > liste.get(i-2).xKoordinate()) OKx1 = false;
            }
        }
        
        for(int i = 0; i < liste.size(); i++){
            if (i >= 2){
                if (liste.get(i).xKoordinate() < liste.get(i-2).xKoordinate()) OKx2 = false;
            }
        }
        
        if (!OKx1 && !OKx2) return false;
        
        for(int i = 0; i < liste.size(); i++){
            if (i >= 2){
                if (liste.get(i).yKoordinate() > liste.get(i-2).yKoordinate()) OKy1 = false;
            }
        }
        
        for(int i = 0; i < liste.size(); i++){
            if (i >= 2){
                if (liste.get(i).yKoordinate() < liste.get(i-2).yKoordinate()) OKy2 = false;
            }
        }
        
        if (!OKy1 && !OKy2) return false;
        return true;
    }    
        
    //Gibt den Schläger der KI zurück
    public Spiel.Zustand.Schlaeger getMe(Spiel.Zustand zustand, int id){
        if (zustand.listeSchlaeger().get(0).identifikation() == id){
            return zustand.listeSchlaeger().get(0);
        }
        return zustand.listeSchlaeger().get(1);
    }

    //Gibt den Schläger des Gegners zurück
    public Spiel.Zustand.Schlaeger getEnemy(Spiel.Zustand zustand, int id){
        if (zustand.listeSchlaeger().get(0).identifikation() == id){
            return zustand.listeSchlaeger().get(1);
        }
        return zustand.listeSchlaeger().get(0);
    }

    //Gibt den Ball zurück
    public Spiel.Zustand.Ball getBall(Spiel.Zustand zustand){
        return zustand.listeBall().get(0);
    }
}