package de.joetabu.marian.bundeswettbewerbinformatik33.seilschaften;

/**
 * Repräsentiert eine Position.
 */
public enum Position {

    /**
     * Oben, außerhalb des Korbes.
     */
    TOP_FREE {
        public Position free() {
            return TOP_FREE;
        }

        public Position simpleSide() {
            return TOP;
        }

        public Position simpleBasket() {
            return FREE;
        }
    },

    /**
     * Unten, außerhalb des Korbes.
     */
    BOTTOM_FREE {
        public Position free() {
            return BOTTOM_FREE;
        }

        public Position simpleSide() {
            return BOTTOM;
        }

        public Position simpleBasket() {
            return FREE;
        }
    },

    /**
     * Oben, im Korb.
     */
    TOP_BASKET {
        public Position free() {
            return TOP_FREE;
        }

        public Position simpleSide() {
            return TOP;
        }

        public Position simpleBasket() {
            return BASKET;
        }
    },

    /**
     * Unten, im Korb.
     */
    BOTTOM_BASKET {
        public Position free() {
            return BOTTOM_FREE;
        }

        public Position simpleSide() {
            return BOTTOM;
        }

        public Position simpleBasket() {
            return BASKET;
        }
    },

    /**
     * Oben. Es ist egal, ob die Position im Korb ist, oder nicht.
     */
    TOP {
        public Position free() {
            return TOP_FREE;
        }

        public Position simpleSide() {
            return TOP;
        }

        public Position simpleBasket() {
            return UNKNOWN;
        }
    },

    /**
     * Unten. Es ist egal, ob die Position im Korb ist, oder nicht.
     */
    BOTTOM {
        public Position free() {
            return BOTTOM_FREE;
        }

        public Position simpleSide() {
            return BOTTOM;
        }

        public Position simpleBasket() {
            return UNKNOWN;
        }
    },

    /**
     * Außerhalb des Korbes. Es ist egal, ob die Position oben oder unten ist.
     */
    FREE {
        public Position free() {
            return FREE;
        }

        public Position simpleSide() {
            return UNKNOWN;
        }

        public Position simpleBasket() {
            return FREE;
        }
    },

    /**
     * Im Korb. Es ist egal, ob die Position oben oder unten ist.
     */
    BASKET {
        public Position free() {
            return FREE;
        }

        public Position simpleSide() {
            return UNKNOWN;
        }

        public Position simpleBasket() {
            return BASKET;
        }
    },

    /**
     * Die Position ist unbekannt.
     */
    UNKNOWN {
        public Position free() {
            return FREE;
        }

        public Position simpleSide() {
            return UNKNOWN;
        }

        public Position simpleBasket() {
            return UNKNOWN;
        }
    };

    /**
     * @return die Position auf derselben Seite, jedoch außerhalb des Korbes
     */
    public abstract Position free();

    /**
     * @return die Seite, also TOP, BOTTOM oder UNKNOWN
     */
    public abstract Position simpleSide();

    /**
     * @return ob sich die Position im Korb befindet, oder nicht, also
     * BASKET, FREE oder UNKNOWN
     */
    public abstract Position simpleBasket();

    /**
     * @param position die Startposition als String
     * @return {@code null}, wenn die Position nicht erkannt wurde,
     * ansonsten die Startposition als ein Element des Enums Position
     */
    public static Position startPositionFromString(String position) {
        switch (position.toLowerCase()) { // .toLowerCase(), damit die
        // Groß-/Kleinschreibung nicht berücksichtigt wird
            case "^": case "o": case "oben": case "^f": case "of": case
                    "obenf": case "^frei": case "ofrei": case "obenfrei":
                return TOP_FREE;
            case "_": case "u": case "unten": case "_f": case "uf": case
                    "untenf": case "_frei": case "ufrei": case "untenfrei":
                return BOTTOM_FREE;
            case "^k": case "ok": case "obenk": case "^korb": case "okorb":
            case "obenkorb":
                return TOP_BASKET;
            case "_k": case "uk": case "untenk": case "_korb": case
                    "ukorb": case "untenkorb":
                return BOTTOM_BASKET;
            default:
                return null;
        }
    }

    /**
     * @param position die Endposition als String
     * @return {@code null}, wenn die Position nicht erkannt wurde,
     * ansonsten die Endposition als ein Element des Enums
     *         Position
     */
    public static Position endPositionFromString(String position) {
        switch (position.toLowerCase()) { // .toLowerCase(), damit die
        // Groß-/Kleinschreibung nicht berücksichtigt wird
            case "e": case "egal":
                return UNKNOWN;
            case "^": case "o": case "oben":
                return TOP;
            case "_": case "u": case "unten":
                return BOTTOM;
            case "k": case "korb":
                return BASKET;
            case "f": case "frei":
                return FREE;
            case "^k": case "ok": case "obenk": case "^korb": case "okorb":
            case "obenkorb":
                return TOP_BASKET;
            case "_k": case "uk": case "untenk": case "_korb": case
                    "ukorb": case "untenkorb":
                return BOTTOM_BASKET;
            case "^f": case "of": case "obenf": case "^frei": case "ofrei":
            case "obenfrei":
                return TOP_FREE;
            case "_f": case "uf": case "untenf": case "_frei": case
                    "ufrei": case "untenfrei":
                return BOTTOM_FREE;
            default:
                return null;
        }
    }

}
