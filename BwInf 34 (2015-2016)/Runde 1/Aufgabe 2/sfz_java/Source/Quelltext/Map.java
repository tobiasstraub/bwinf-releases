import java.util.Random;

public class Map {
	private Controller controller;
	private Random rand = new Random();
	private String[][] field;
	private int[][] foodPointsAndOlfactoryPoints;
	private int amountOfAntsInNest;
	private int amountOfFoodInNest;

	public Map(Controller controller) {
		this.controller = controller;
		field = new String[controller.getMapSize()][controller.getMapSize()];
		foodPointsAndOlfactoryPoints = new int[controller.getMapSize()][controller
				.getMapSize()];
		amountOfAntsInNest = controller.getAmountAnts();
		amountOfFoodInNest = 0;
	}

	/**
	 * Fills the Map-Array with the displayed brackets [].
	 * 
	 * @author Jonah Schierding
	 */

	public void createMap() {
		for (int y = 0; y < (controller.getMapSize()); y++) {
			for (int x = 0; x < controller.getMapSize(); x++) {
				field[x][y] = "[  ]";
				foodPointsAndOlfactoryPoints[x][y] = 0;
			}
		}
	}

	/**
	 * Generates the nest on the map.
	 * 
	 * @author Jonah Schierding
	 */

	public void generateNestOnMap() {
		field[controller.getDownRightCornerOfNestXCoordinate()][controller.getDownRightCornerOfNestYCoordinate()] = "[N ]";
		field[controller.getDownRightCornerOfNestXCoordinate() - 1][controller.getDownRightCornerOfNestYCoordinate()] = "[N ]";
		field[controller.getDownRightCornerOfNestXCoordinate()][controller.getDownRightCornerOfNestYCoordinate() - 1] = "[N ]";
		field[controller.getDownRightCornerOfNestXCoordinate() - 1][controller.getDownRightCornerOfNestYCoordinate() - 1] = "[N ]";
	}

	/**
	 * Distributes the Food on the map randomly.
	 * 
	 * @author Jonah Schierding
	 */

	public void generateFoodOnMap() {
		for (int i = 0; i < controller.getAmountFoodsources(); i++) {
			int x = rand.nextInt(controller.getMapSize());
			int y = rand.nextInt(controller.getMapSize());
			if (field[x][y] == "[F ]" || field[x][y] == "[N ]") {
				i--;
			} else {
				field[x][y] = "[F ]";
				foodPointsAndOlfactoryPoints[x][y] = controller
						.getSizeOfFoodsources();
			}
		}
	}

	/**
	 * Releases the ants from the foodsources changing from [A ] to [AF]
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException 
	 */

	public void releaseAntsFromFoodsources() throws InterruptedException {
		for (int y = 0; y < (controller.getMapSize()); y++) {
			for (int x = 0; x < controller.getMapSize(); x++) {
				if (field[x][y] == "[FA]") {
					if (x < controller.getMapSize() / 2 - 1
							&& field[x + 1][y] == "[  ]") {
						field[x][y] = "[F ]";
						field[x + 1][y] = "[AF]";
					} else if (x > controller.getMapSize() / 2
							&& field[x - 1][y] == "[  ]") {
						field[x][y] = "[F ]";
						field[x - 1][y] = "[AF]";
					} else if (y < controller.getMapSize() / 2 - 1
							&& field[x][y + 1] == "[  ]") {
						field[x][y] = "[F ]";
						field[x][y + 1] = "[AF]";
					} else if (y > controller.getMapSize() / 2
							&& field[x][y - 1] == "[  ]") {
						field[x][y] = "[F ]";
						field[x][y - 1] = "[AF]";
					}
					foodPointsAndOlfactoryPoints[x][y]--;
					if (foodPointsAndOlfactoryPoints[x][y] == 0) {
						field[x][y] = "[  ]";
					}
					printMap();
					//Thread.sleep(500);
				}
			}
		}
	}

	/**
	 * Moves the ants whithout food
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException 
	 */

	public void moveAntsWithoutFoodAtCorners() throws InterruptedException {
		if (field[0][0] == "[A ]") {
			field[0][0] = "[  ]";
			if (foodPointsAndOlfactoryPoints[0][1] < foodPointsAndOlfactoryPoints[1][0]) {
				field[1][0] = "[A ]";
			} else if (foodPointsAndOlfactoryPoints[0][1] > foodPointsAndOlfactoryPoints[1][0]) {
				field[0][1] = "[A ]";
			} else if (rand.nextBoolean()) {
				field[1][0] = "[A ]";
			} else {
				field[0][1] = "[A ]";
			}
			printMap();
			//Thread.sleep(500);
		}
		if (field[0][controller.getMapSize() - 1] == "[A ]") {
			field[0][controller.getMapSize() - 1] = "[  ]";
			if (foodPointsAndOlfactoryPoints[0][controller.getMapSize() - 2] < foodPointsAndOlfactoryPoints[1][controller
					.getMapSize() - 1]) {
				field[1][controller.getMapSize() - 1] = "[A ]";
			} else if (foodPointsAndOlfactoryPoints[0][controller.getMapSize() - 2] > foodPointsAndOlfactoryPoints[1][controller
					.getMapSize() - 1]) {
				field[0][controller.getMapSize() - 2] = "[A ]";
			} else if (rand.nextBoolean()) {
				field[1][controller.getMapSize() - 1] = "[A ]";
			} else {
				field[0][controller.getMapSize() - 2] = "[A ]";
			}
			printMap();
			//Thread.sleep(500);
		}
		if (field[controller.getMapSize() - 1][0] == "[A ]") {
			field[controller.getMapSize() - 1][0] = "[  ]";
			if (foodPointsAndOlfactoryPoints[controller.getMapSize() - 1][1] < foodPointsAndOlfactoryPoints[controller
					.getMapSize() - 2][0]) {
				field[controller.getMapSize() - 2][0] = "[A ]";
			} else if (foodPointsAndOlfactoryPoints[controller.getMapSize() - 1][1] > foodPointsAndOlfactoryPoints[controller
					.getMapSize() - 2][0]) {
				field[controller.getMapSize() - 1][1] = "[A ]";
			} else if (rand.nextBoolean()) {
				field[controller.getMapSize() - 2][0] = "[A ]";
			} else {
				field[controller.getMapSize() - 1][1] = "[A ]";
			}
			printMap();
			//Thread.sleep(500);
		}
		if (field[controller.getMapSize() - 1][controller.getMapSize() - 1] == "[A ]") {
			field[controller.getMapSize() - 1][controller.getMapSize() - 1] = "[  ]";
			if (foodPointsAndOlfactoryPoints[controller.getMapSize() - 1][controller
					.getMapSize() - 2] < foodPointsAndOlfactoryPoints[controller
					.getMapSize() - 2][controller.getMapSize() - 1]) {
				field[controller.getMapSize() - 2][controller.getMapSize() - 1] = "[A ]";
			} else if (foodPointsAndOlfactoryPoints[controller.getMapSize() - 1][controller
					.getMapSize() - 2] > foodPointsAndOlfactoryPoints[controller
					.getMapSize() - 2][controller.getMapSize() - 1]) {
				field[controller.getMapSize() - 1][controller.getMapSize() - 2] = "[A ]";
			} else if (rand.nextBoolean()) {
				field[controller.getMapSize() - 2][controller.getMapSize() - 1] = "[A ]";
			} else {
				field[controller.getMapSize() - 1][controller.getMapSize() - 2] = "[A ]";
			}
			printMap();
			//Thread.sleep(500);
		}
	}

	/**
	 * Moves the ants being at the border without food.
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException 
	 */

	public void moveAntsWithoutFoodAtBorder() throws InterruptedException {
		for (int x = 1; x < controller.getMapSize() - 1; x++) {
			if (field[x][controller.getMapSize() - 1] == "[A ]") {
				if (field[x + 1][controller.getMapSize() - 1] != "[F ]"
						&& field[x][controller.getMapSize() - 2] != "[F ]"
						&& field[x - 1][controller.getMapSize() - 1] != "[F ]") {
					boolean temp1;
					if (foodPointsAndOlfactoryPoints[x + 1][controller
							.getMapSize() - 1] < foodPointsAndOlfactoryPoints[x - 1][controller
							.getMapSize() - 1]) {
						temp1 = true;
					} else if (foodPointsAndOlfactoryPoints[x + 1][controller
							.getMapSize() - 1] > foodPointsAndOlfactoryPoints[x - 1][controller
							.getMapSize() - 1]) {
						temp1 = false;
					} else {
						temp1 = rand.nextBoolean();
					}
					if (!temp1) {
						if (foodPointsAndOlfactoryPoints[x + 1][controller
								.getMapSize() - 1] < foodPointsAndOlfactoryPoints[x][controller
								.getMapSize() - 2]
								&& field[x][controller.getMapSize() - 2] == "[  ]") {
							field[x][controller.getMapSize() - 2] = "[A ]";
							field[x][controller.getMapSize() - 1] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[x + 1][controller
								.getMapSize() - 1] > foodPointsAndOlfactoryPoints[x][controller
								.getMapSize() - 2]
								&& field[x + 1][controller.getMapSize() - 1] == "[  ]") {
							field[x + 1][controller.getMapSize() - 1] = "[A ]";
							field[x][controller.getMapSize() - 1] = "[  ]";
						} else {
							if (rand.nextBoolean()
									&& field[x][controller.getMapSize() - 2] == "[  ]") {
								field[x][controller.getMapSize() - 2] = "[A ]";
								field[x][controller.getMapSize() - 1] = "[  ]";
							} else if (field[x + 1][controller.getMapSize() - 1] == "[  ]") {
								field[x + 1][controller.getMapSize() - 1] = "[A ]";
								field[x][controller.getMapSize() - 1] = "[  ]";
							}
						}
					}
					if (temp1) {
						if (foodPointsAndOlfactoryPoints[x - 1][controller
								.getMapSize() - 1] < foodPointsAndOlfactoryPoints[x][controller
								.getMapSize() - 2]
								&& field[x][controller.getMapSize() - 2] == "[  ]") {
							field[x][controller.getMapSize() - 2] = "[A ]";
							field[x][controller.getMapSize() - 1] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[x - 1][controller
								.getMapSize() - 1] > foodPointsAndOlfactoryPoints[x][controller
								.getMapSize() - 2]
								&& field[x - 1][controller.getMapSize() - 1] == "[  ]") {
							field[x - 1][controller.getMapSize() - 1] = "[A ]";
							field[x][controller.getMapSize() - 1] = "[  ]";
						} else {
							if (rand.nextBoolean()
									&& field[x][controller.getMapSize() - 2] == "[  ]") {
								field[x][controller.getMapSize() - 2] = "[A ]";
								field[x][controller.getMapSize() - 1] = "[  ]";
							} else if (field[x - 1][controller.getMapSize() - 1] == "[  ]") {
								field[x - 1][controller.getMapSize() - 1] = "[A ]";
								field[x][controller.getMapSize() - 1] = "[  ]";
							}
						}
					}
				} else if (field[x + 1][controller.getMapSize() - 1] == "[F ]") {
					field[x + 1][controller.getMapSize() - 1] = "[FA]";
					field[x][controller.getMapSize() - 1] = "[  ]";
				} else if (field[x - 1][controller.getMapSize() - 1] == "[F ]") {
					field[x - 1][controller.getMapSize() - 1] = "[FA]";
					field[x][controller.getMapSize() - 1] = "[  ]";
				} else if (field[x][controller.getMapSize() - 2] == "[F ]") {
					field[x][controller.getMapSize() - 2] = "[FA]";
					field[x][controller.getMapSize() - 1] = "[  ]";
				}
				printMap();
				//Thread.sleep(500);
			}
		}

		for (int x = 1; x < controller.getMapSize() - 1; x++) {
			if (field[x][0] == "[A ]") {
				if (field[x + 1][0] != "[F ]" && field[x][1] != "[F ]"
						&& field[x - 1][0] != "[F ]") {
					boolean temp1;
					if (foodPointsAndOlfactoryPoints[x + 1][0] < foodPointsAndOlfactoryPoints[x - 1][0]) {
						temp1 = true;
					} else if (foodPointsAndOlfactoryPoints[x + 1][0] > foodPointsAndOlfactoryPoints[x - 1][0]) {
						temp1 = false;
					} else {
						temp1 = rand.nextBoolean();
					}
					if (!temp1) {
						if (foodPointsAndOlfactoryPoints[x + 1][0] < foodPointsAndOlfactoryPoints[x][1]
								&& field[x][1] == "[  ]") {
							field[x][1] = "[A ]";
							field[x][0] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[x + 1][0] > foodPointsAndOlfactoryPoints[x][1]
								&& field[x + 1][0] == "[  ]") {
							field[x + 1][0] = "[A ]";
							field[x][0] = "[  ]";
						} else {
							if (rand.nextBoolean() && field[x][1] == "[  ]") {
								field[x][1] = "[A ]";
								field[x][0] = "[  ]";
							} else if (field[x + 1][0] == "[  ]") {
								field[x + 1][0] = "[A ]";
								field[x][0] = "[  ]";
							}
						}
					}
					if (temp1) {
						if (foodPointsAndOlfactoryPoints[x - 1][0] < foodPointsAndOlfactoryPoints[x][1]
								&& field[x][1] == "[  ]") {
							field[x][1] = "[A ]";
							field[x][0] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[x - 1][0] > foodPointsAndOlfactoryPoints[x][1]
								&& field[x - 1][0] == "[  ]") {
							field[x - 1][0] = "[A ]";
							field[x][0] = "[  ]";
						} else {
							if (rand.nextBoolean() && field[x][1] == "[  ]") {
								field[x][1] = "[A ]";
								field[x][0] = "[  ]";
							} else if (field[x - 1][0] == "[  ]") {
								field[x - 1][0] = "[A ]";
								field[x][0] = "[  ]";
							}
						}
					}
				} else if (field[x + 1][0] == "[F ]") {
					field[x + 1][0] = "[FA]";
					field[x][0] = "[  ]";
				} else if (field[x - 1][0] == "[F ]") {
					field[x - 1][0] = "[FA]";
					field[x][0] = "[  ]";
				} else if (field[x][1] == "[F ]") {
					field[x][1] = "[FA]";
					field[x][0] = "[  ]";
				}
				printMap();
				//Thread.sleep(500);
			}
		}
		for (int y = 1; y < controller.getMapSize() - 1; y++) {
			if (field[0][y] == "[A ]") {
				if (field[0][y + 1] != "[F ]" && field[1][y] != "[F ]"
						&& field[0][y - 1] != "[F ]") {
					boolean temp1;
					if (foodPointsAndOlfactoryPoints[0][y + 1] < foodPointsAndOlfactoryPoints[0][y - 1]) {
						temp1 = true;
					} else if (foodPointsAndOlfactoryPoints[0][y + 1] > foodPointsAndOlfactoryPoints[0][y - 1]) {
						temp1 = false;
					} else {
						temp1 = rand.nextBoolean();
					}
					if (!temp1) {
						if (foodPointsAndOlfactoryPoints[0][y + 1] < foodPointsAndOlfactoryPoints[1][y]
								&& field[1][y] == "[  ]") {
							field[1][y] = "[A ]";
							field[0][y] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[0][y + 1] > foodPointsAndOlfactoryPoints[1][y]
								&& field[0][y + 1] == "[  ]") {
							field[0][y + 1] = "[A ]";
							field[0][y] = "[  ]";
						} else {
							if (rand.nextBoolean() && field[1][y] == "[  ]") {
								field[1][y] = "[A ]";
								field[0][y] = "[  ]";
							} else if (field[0][y + 1] == "[  ]") {
								field[0][y + 1] = "[A ]";
								field[0][y] = "[  ]";
							}
						}
					}
					if (temp1) {
						if (foodPointsAndOlfactoryPoints[0][y - 1] < foodPointsAndOlfactoryPoints[1][y]
								&& field[1][y] == "[  ]") {
							field[1][y] = "[A ]";
							field[0][y] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[0][y - 1] > foodPointsAndOlfactoryPoints[1][y]
								&& field[0][y - 1] == "[  ]") {
							field[0][y - 1] = "[A ]";
							field[0][y] = "[  ]";
						} else {
							if (rand.nextBoolean() && field[1][y] == "[  ]") {
								field[1][y] = "[A ]";
								field[0][y] = "[  ]";
							} else if (field[0][y - 1] == "[  ]") {
								field[0][y - 1] = "[A ]";
								field[0][y] = "[  ]";
							}
						}
					}
				} else if (field[0][y + 1] == "[F ]") {
					field[0][y + 1] = "[FA]";
					field[0][y] = "[  ]";
				} else if (field[0][y - 1] == "[F ]") {
					field[0][y - 1] = "[FA]";
					field[0][y] = "[  ]";
				} else if (field[1][y] == "[F ]") {
					field[1][y] = "[FA]";
					field[0][y] = "[  ]";
				}
				printMap();
				//Thread.sleep(500);
			}
		}
		for (int y = 1; y < controller.getMapSize() - 1; y++) {
			if (field[controller.getMapSize() - 1][y] == "[A ]") {
				if (field[controller.getMapSize() - 1][y + 1] != "[F ]"
						&& field[controller.getMapSize() - 2][y] != "[F ]"
						&& field[controller.getMapSize() - 1][y - 1] != "[F ]") {
					boolean temp1;
					if (foodPointsAndOlfactoryPoints[controller.getMapSize() - 1][y + 1] < foodPointsAndOlfactoryPoints[controller
							.getMapSize() - 1][y - 1]) {
						temp1 = true;
					} else if (foodPointsAndOlfactoryPoints[controller
							.getMapSize() - 1][y + 1] > foodPointsAndOlfactoryPoints[controller
							.getMapSize() - 1][y - 1]) {
						temp1 = false;
					} else {
						temp1 = rand.nextBoolean();
					}
					if (!temp1) {
						if (foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 1][y + 1] < foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 2][y]
								&& field[controller.getMapSize() - 2][y] == "[  ]") {
							field[controller.getMapSize() - 2][y] = "[A ]";
							field[controller.getMapSize() - 1][y] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 1][y + 1] > foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 2][y]
								&& field[controller.getMapSize() - 1][y + 1] == "[  ]") {
							field[controller.getMapSize() - 1][y + 1] = "[A ]";
							field[controller.getMapSize() - 1][y] = "[  ]";
						} else {
							if (rand.nextBoolean()
									&& field[controller.getMapSize() - 2][y] == "[  ]") {
								field[controller.getMapSize() - 2][y] = "[A ]";
								field[controller.getMapSize() - 1][y] = "[  ]";
							} else if (field[controller.getMapSize() - 1][y + 1] == "[  ]") {
								field[controller.getMapSize() - 1][y + 1] = "[A ]";
								field[controller.getMapSize() - 1][y] = "[  ]";
							}
						}
					}
					if (temp1) {
						if (foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 1][y - 1] < foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 2][y]
								&& field[controller.getMapSize() - 2][y] == "[  ]") {
							field[controller.getMapSize() - 2][y] = "[A ]";
							field[controller.getMapSize() - 1][y] = "[  ]";
						} else if (foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 1][y - 1] > foodPointsAndOlfactoryPoints[controller
								.getMapSize() - 2][y]
								&& field[controller.getMapSize() - 1][y - 1] == "[  ]") {
							field[controller.getMapSize() - 1][y - 1] = "[A ]";
							field[controller.getMapSize() - 1][y] = "[  ]";
						} else {
							if (rand.nextBoolean()
									&& field[controller.getMapSize() - 2][y] == "[  ]") {
								field[controller.getMapSize() - 2][y] = "[A ]";
								field[controller.getMapSize() - 1][y] = "[  ]";
							} else if (field[controller.getMapSize() - 1][y - 1] == "[  ]") {
								field[controller.getMapSize() - 1][y - 1] = "[A ]";
								field[controller.getMapSize() - 1][y] = "[  ]";
							}
						}
					}
				} else if (field[controller.getMapSize() - 1][y + 1] == "[F ]") {
					field[controller.getMapSize() - 1][y + 1] = "[FA]";
					field[controller.getMapSize() - 1][y] = "[  ]";
				} else if (field[controller.getMapSize() - 1][y - 1] == "[F ]") {
					field[controller.getMapSize() - 1][y - 1] = "[FA]";
					field[controller.getMapSize() - 1][y] = "[  ]";
				} else if (field[controller.getMapSize() - 2][y] == "[F ]") {
					field[controller.getMapSize() - 2][y] = "[FA]";
					field[controller.getMapSize() - 1][y] = "[  ]";
				}
				printMap();
				//Thread.sleep(500);
			}
		}
	}

	/**
	 * Moves the ants, being not at the border but in the middle, without food.
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException 
	 */

	public void moveAntsWithoutFoodInMiddle() throws InterruptedException {
		for (int y = 1; y < controller.getMapSize() - 1; y++) {
			for (int x = 1; x < controller.getMapSize() - 1; x++) {
				if (field[x][y] == "[A ]") {
					if (field[x + 1][y] != "[F ]" && field[x][y - 1] != "[F ]"
							&& field[x - 1][y] != "[F ]"
							&& field[x][y + 1] != "[F ]") {
						boolean temp1;
						boolean temp2;
						if (foodPointsAndOlfactoryPoints[x + 1][y] < foodPointsAndOlfactoryPoints[x - 1][y]) {
							temp1 = true;
						} else if (foodPointsAndOlfactoryPoints[x + 1][y] > foodPointsAndOlfactoryPoints[x - 1][y]) {
							temp1 = false;
						} else {
							temp1 = rand.nextBoolean();
						}

						if (foodPointsAndOlfactoryPoints[x][y + 1] < foodPointsAndOlfactoryPoints[x][y - 1]) {
							temp2 = true;
						} else if (foodPointsAndOlfactoryPoints[x][y + 1] > foodPointsAndOlfactoryPoints[x][y - 1]) {
							temp2 = false;
						} else {
							temp2 = rand.nextBoolean();
						}

						if (temp1 && !temp2) {
							if (foodPointsAndOlfactoryPoints[x - 1][y] < foodPointsAndOlfactoryPoints[x][y + 1]
									&& field[x][y + 1] == "[  ]") {
								field[x][y + 1] = "[A ]";
								field[x][y] = "[  ]";
							} else if (foodPointsAndOlfactoryPoints[x - 1][y] > foodPointsAndOlfactoryPoints[x][y + 1]
									&& field[x - 1][y] == "[  ]") {
								field[x - 1][y] = "[A ]";
								field[x][y] = "[  ]";
							} else {
								if (rand.nextBoolean()
										&& field[x][y + 1] == "[  ]") {
									field[x][y + 1] = "[A ]";
									field[x][y] = "[  ]";
								} else if (field[x - 1][y] == "[  ]") {
									field[x - 1][y] = "[A ]";
									field[x][y] = "[  ]";
								}
							}
						}
						if (!temp1 && temp2) {
							if (foodPointsAndOlfactoryPoints[x + 1][y] < foodPointsAndOlfactoryPoints[x][y - 1]
									&& field[x][y - 1] == "[  ]") {
								field[x][y - 1] = "[A ]";
								field[x][y] = "[  ]";
							} else if (foodPointsAndOlfactoryPoints[x + 1][y] > foodPointsAndOlfactoryPoints[x][y - 1]
									&& field[x + 1][y] == "[  ]") {
								field[x + 1][y] = "[A ]";
								field[x][y] = "[  ]";
							} else {
								if (rand.nextBoolean()
										&& field[x][y - 1] == "[  ]") {
									field[x][y - 1] = "[A ]";
									field[x][y] = "[  ]";
								} else if (field[x + 1][y] == "[  ]") {
									field[x + 1][y] = "[A ]";
									field[x][y] = "[  ]";
								}
							}
						}
						if (temp1 && temp2) {
							if (foodPointsAndOlfactoryPoints[x - 1][y] < foodPointsAndOlfactoryPoints[x][y - 1]
									&& field[x][y - 1] == "[  ]") {
								field[x][y - 1] = "[A ]";
								field[x][y] = "[  ]";
							} else if (foodPointsAndOlfactoryPoints[x - 1][y] > foodPointsAndOlfactoryPoints[x][y - 1]
									&& field[x - 1][y] == "[  ]") {
								field[x - 1][y] = "[A ]";
								field[x][y] = "[  ]";
							} else {
								if (rand.nextBoolean()
										&& field[x][y - 1] == "[  ]") {
									field[x][y - 1] = "[A ]";
									field[x][y] = "[  ]";
								} else if (field[x - 1][y] == "[  ]") {
									field[x - 1][y] = "[A ]";
									field[x][y] = "[  ]";
								}
							}
						}
						if (!temp1 && !temp2) {
							if (foodPointsAndOlfactoryPoints[x + 1][y] < foodPointsAndOlfactoryPoints[x][y + 1]
									&& field[x][y + 1] == "[  ]") {
								field[x][y + 1] = "[A ]";
								field[x][y] = "[  ]";
							} else if (foodPointsAndOlfactoryPoints[x + 1][y] > foodPointsAndOlfactoryPoints[x][y + 1]
									&& field[x + 1][y] == "[  ]") {
								field[x + 1][y] = "[A ]";
								field[x][y] = "[  ]";
							} else {
								if (rand.nextBoolean()
										&& field[x][y + 1] == "[  ]") {
									field[x][y + 1] = "[A ]";
									field[x][y] = "[  ]";
								} else if (field[x + 1][y] == "[  ]") {
									field[x + 1][y] = "[A ]";
									field[x][y] = "[  ]";
								}
							}
						}
					} else if (field[x + 1][y] == "[F ]") {
						field[x + 1][y] = "[FA]";
						field[x][y] = "[  ]";
					} else if (field[x - 1][y] == "[F ]") {
						field[x - 1][y] = "[FA]";
						field[x][y] = "[  ]";
					} else if (field[x][y + 1] == "[F ]") {
						field[x][y + 1] = "[FA]";
						field[x][y] = "[  ]";
					} else if (field[x][y - 1] == "[F ]") {
						field[x][y - 1] = "[FA]";
						field[x][y] = "[  ]";
					}
					printMap();
					//Thread.sleep(500);
				}

			}
		}
	}

	/**
	 * Moves the ants at the border with food.
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException 
	 */

	public void moveAntsWithFoodAtBorder() throws InterruptedException {
		for (int x = 1; x < controller.getMapSize() - 1; x++) {
			if (field[x][0] == "[AF]" && field[x][1] == "[  ]") {
				field[x][1] = "[AF]";
				field[x][0] = "[  ]";
				foodPointsAndOlfactoryPoints[x][0]++;
				printMap();
				//Thread.sleep(500);
			}
		}
		for (int x = 1; x < controller.getMapSize() - 1; x++) {
			if (field[x][controller.getMapSize() - 1] == "[AF]" && field[x][controller.getMapSize() - 2] == "[  ]") {
				field[x][controller.getMapSize() - 2] = "[AF]";
				field[x][controller.getMapSize() - 1] = "[  ]";
				foodPointsAndOlfactoryPoints[x][controller.getMapSize() - 1]++;
				printMap();
				//Thread.sleep(500);
			}
		}
		for (int y = 1; y < controller.getMapSize() - 1; y++) {
			if (field[0][y] == "[AF]" && field[1][y] == "[  ]") {
				field[1][y] = "[AF]";
				field[0][y] = "[  ]";
				foodPointsAndOlfactoryPoints[0][y]++;
				printMap();
				//Thread.sleep(500);
			}
		}
		for (int y = 1; y < controller.getMapSize() - 1; y++) {
			if (field[controller.getMapSize() - 1][y] == "[AF]" && field[controller.getMapSize() - 2][y] == "[  ]") {
				field[controller.getMapSize() - 2][y] = "[AF]";
				field[controller.getMapSize() - 1][y] = "[  ]";
				foodPointsAndOlfactoryPoints[controller.getMapSize() - 1][y]++;
				printMap();
				//Thread.sleep(500);
			}
		}
	}

	/**
	 * Moves the ants carrying food into to the middle, to the nest.
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException 
	 */

	public void moveAntsWithFoodInMiddle() throws InterruptedException {
		for (int y = 1; y < controller.getMapSize() - 1; y++) {
			for (int x = 1; x < controller.getMapSize() - 1; x++) {
				if (field[x][y] == "[AF]") {
					if (field[x - 1][y] == "[N ]" || field[x + 1][y] == "[N ]"
							|| field[x][y - 1] == "[N ]"
							|| field[x][y + 1] == "[N ]") {
						field[x][y] = "[  ]";
						amountOfAntsInNest++;
						amountOfFoodInNest++;
					}
					if(rand.nextBoolean()){
						if (x < controller.getDownRightCornerOfNestXCoordinate()-1
								&& field[x + 1][y] == "[  ]") {
							field[x][y] = "[  ]";
							field[x + 1][y] = "[AF]";
						}else if (x > controller.getDownRightCornerOfNestXCoordinate()
								&& field[x - 1][y] == "[  ]") {
							field[x][y] = "[  ]";
							field[x - 1][y] = "[AF]";
						}else if (y < controller.getDownRightCornerOfNestYCoordinate()-1
								&& field[x][y + 1] == "[  ]") {
							field[x][y] = "[  ]";
							field[x][y + 1] = "[AF]";
						}else if (y > controller.getDownRightCornerOfNestYCoordinate()
								&& field[x][y - 1] == "[  ]") {
							field[x][y] = "[  ]";
							field[x][y - 1] = "[AF]";
							}
					} else{ 
						if (y < controller.getDownRightCornerOfNestYCoordinate()-1
							&& field[x][y + 1] == "[  ]") {
						field[x][y] = "[  ]";
						field[x][y + 1] = "[AF]";
						}else if (y > controller.getDownRightCornerOfNestYCoordinate()
							&& field[x][y - 1] == "[  ]") {
						field[x][y] = "[  ]";
						field[x][y - 1] = "[AF]";
						}else if (x < controller.getDownRightCornerOfNestXCoordinate()-1
								&& field[x + 1][y] == "[  ]") {
							field[x][y] = "[  ]";
							field[x + 1][y] = "[AF]";
						}else if (x > controller.getDownRightCornerOfNestXCoordinate()
								&& field[x - 1][y] == "[  ]") {
							field[x][y] = "[  ]";
							field[x - 1][y] = "[AF]";
						}
					}
					foodPointsAndOlfactoryPoints[x][y]++;
					printMap();
					//Thread.sleep(500);
				}
			}
		}
	}

	/**
	 * Releases the Ants from the nest in several steps on the empty fields
	 * around the nest.
	 * 
	 * @author Jonah Schierding
	 * @throws InterruptedException
	 */

	public void releaseAntsFromNest() throws InterruptedException {
		for (int y = 0; y < (controller.getMapSize()); y++) {
			for (int x = 0; x < controller.getMapSize(); x++) {
				if (amountOfAntsInNest > 0 && field[x][y] == "[N ]" && field[x-1][y] == "[  ]") {
					field[x-1][y] = "[A ]";
					amountOfAntsInNest--;
					printMap();
					//Thread.sleep(500);
				}
				if (amountOfAntsInNest > 0 && field[x][y] == "[N ]" && field[x][y-1] == "[  ]") {
					field[x][y-1] = "[A ]";
					amountOfAntsInNest--;
					printMap();
					//Thread.sleep(500);
				}
				if (amountOfAntsInNest > 0 && field[x][y] == "[N ]" && field[x+1][y] == "[  ]") {
					field[x+1][y] = "[A ]";
					amountOfAntsInNest--;
					printMap();
					//Thread.sleep(500);
				}
				if (amountOfAntsInNest > 0 && field[x][y] == "[N ]" && field[x][y+1] == "[  ]") {
					field[x][y+1] = "[A ]";
					amountOfAntsInNest--;
					printMap();
					//Thread.sleep(500);
				}
			}
		}
	}

	/**
	 * Makes the fields with olfactory points lose one olfactory point after a certain time
	 * 
	 * @author Jonah Schierding
	 */

	public void loseOlfactoryPoints() {
		for (int y = 0; y < (controller.getMapSize()); y++) {
			for (int x = 0; x < controller.getMapSize(); x++) {
				if (foodPointsAndOlfactoryPoints[x][y] != 0
						&& field[x][y] != "[F ]" && field[x][y] != "[FA]") {
					foodPointsAndOlfactoryPoints[x][y]--;
				}
			}
		}
	}

	/**
	 * Prints the map in the console using the map-Array
	 * 
	 * @author Jonah Schierding, Julius Freudenberger
	 */

	public void printMap() {
		for (int y = 0; y < (controller.getMapSize()); y++) {
			for (int x = 0; x < controller.getMapSize(); x++) {
				System.out.print("(" + field[x][y] + foodPointsAndOlfactoryPoints[x][y] + ")");
			}
			System.out.print("\n");
		}
		System.out
				.println("AF = Ant with Food; A = Ant without Food; F = Foodsource; FA = Foodsource with Ant on it; N = Nest; Olfactory or Food Points =(Number) \n");
	}

	public int getAmountOfFoodInNest() {
		return amountOfFoodInNest;
	}
}
