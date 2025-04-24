//Zach Mahan, 20250424, Purpose: prototype for 2D array game

import java.util.*;

public class Tester {
    public static Scanner sc1 = new Scanner (System.in);
    public static void main (String[] args) throws InterruptedException {
        startGame();
    }
    public static void startGame() throws InterruptedException{
        boolean shouldPlayAgain = false;
        Music.playMusicOnLoop("src/music/themeSong.wav");
        printTitleScreen();
        System.out.println("[ENTER] to play");
        sc1.nextLine();
        do {
            boolean shouldPlayAgainstAI = decideToPlayAgainstAIandReadRulesIfWanted();
            int rowLength = getDimensionFromUser("row");
            int columnLength = getDimensionFromUser("column");
            int[][] startingGameArr = buildGameArr(rowLength, columnLength);
            if(shouldPlayAgainstAI) playVsAI(startingGameArr);
            else playTwoPlayerGame(startingGameArr);
            shouldPlayAgain = askToPlayAgain();
        } while (shouldPlayAgain);
        bidUserFarewell();
    }
    //starting the game
    public static int getDimensionFromUser(String dimension) {
        int length = 0;
        boolean userGaveValidLength = false;
        do {
            System.out.print("Please input a " + dimension + " length (1-9): ");
            try {
                length = Integer.parseInt(sc1.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Invalid input! ");
            }
            if (length >= 1 && length <= 9) {
                System.out.println("The " + dimension + " length will be " + length + "!\n");
                userGaveValidLength = true;
            }
            else {
                System.out.println("The length must be between 1 and 9.");
            }
        } while (!userGaveValidLength);
        return length + 1; //plus 1 because the zero column is ignored!
    }
    public static int[][] buildGameArr(int rowLength, int columnLength) {
        int[][] gameArr = new int[columnLength][rowLength];
        for(int i = 0; i < columnLength; i++) {
            for(int k = 0; k < rowLength; k++) {
                gameArr[i][k] = i * 10 + k;
            }
        }
        return gameArr;
    }
    public static boolean decideToPlayAgainstAIandReadRulesIfWanted() {
        boolean playerWantsToPlayAgainstAI = false;
        String choice = "";
        do {
            System.out.println("       What would you like to do?");
            System.out.println("========================================");
            System.out.println(" [1] Play against Claude Shannon");
            System.out.println(" [2] Play against a friend");
            System.out.println(" [3] Read the rules");
            System.out.println();
            choice = sc1.nextLine().trim().toUpperCase();
            switch (choice){
                case "1":
                    System.out.println("Playing against Claude Shannon...");
                    System.out.println("You will be PLAYER 0.");
                    playerWantsToPlayAgainstAI = true;
                    pressEnterToContinue();
                    break;
                case "2":
                    System.out.println("Playing against a friend...");
                    System.out.println("Decide who will be PLAYER 0 and who will be PLAYER 1.");
                    pressEnterToContinue();
                    break;
                case "3":
                    String[] rules = {
                            "                               RULES",
                            "===================================================================",
                            "| 1) There are two players: 0 and 1. Each turn will alternate     |",
                            "|    between the two. An RNG will decide who goes first.          |",
                            "| 2) The players will have to agree on the dimensions for a game  |",
                            "|    board. Each entry will be a two digit number. The first      |",
                            "|    digit indicates the row # and the second indicates the       |",
                            "|    the column #.                                                |",
                            "| 3) During a player's turn, they may select one valid entry      |",
                            "|    from the game board.                                         |",
                            "| 4) Depending on the entry, the board will be altered uniquely.  |",
                            "| 5) The goal is to make the other player clear out the last      |",
                            "|    entry.                                                       |",
                            "| 6) A selected entry affects the board according to these rules: |",
                            "|       ->If the first digit is less than 5, only entries to the  |",
                            "|         left of the selected entry are cleared.                 |",
                            "|       ->If the first digit is 5 or greater, only entries to the |",
                            "|         right of the selected entry are cleared.                |",
                            "|    At the same time:                                            |",
                            "|       ->If the second digit is less than 5, only entries above  |",
                            "|         the selected entry are cleared.                         |",
                            "|       ->If the second digit is 5 or greater, only entries below |",
                            "|         the selected entry are cleared.                         |",
                            "===================================================================",
                    };
                    for (String line : rules) {
                        System.out.println(line);
                    }
                    pressEnterToContinue();
                    break;
                default:
                    System.out.println("Invalid input. Please enter [1], [2], or [3].");
                    pressEnterToContinue();
                    break;
            }
        } while (!choice.equals("1") && !choice.equals("2"));
        return playerWantsToPlayAgainstAI;
    }
    //playing the game
    public static void playTwoPlayerGame(int[][] startingGameArr) {
        int[][] gameArr = cloneArr(startingGameArr);
        boolean someoneHasWon = false;
        int playerTurnNum = randomizeStartingPlayerAndDeclareIt();
        do {
            clearScreen();
            displayGameState(gameArr, playerTurnNum);
            int choice = getPlayerChoiceDuringTurn(gameArr, playerTurnNum);
            adjustArrBasedOnChoice(gameArr, choice);
            someoneHasWon = checkIfSomeoneWon(gameArr);
            if (someoneHasWon) {
                displayGameState(gameArr, playerTurnNum);
                System.out.println("PLAYER " + (1 - playerTurnNum) + " has won!");
            }
            else playerTurnNum = 1 - playerTurnNum;
        } while (!someoneHasWon);
    }
    public static void displayGameState(int[][] gameArr, int playerTurnNum) {
        int rowLengthInTermsOfChars = (gameArr[1].length - 1) * 3 + 1;
        System.out.println("\n" + " ".repeat(rowLengthInTermsOfChars / 2 - 2) + "BOARD");
        String divider = "=".repeat(rowLengthInTermsOfChars);
        System.out.println(divider);
        printArr(gameArr);
        System.out.println(divider);
        System.out.println(" PLAYER " + playerTurnNum + "'S TURN");
        System.out.println("-".repeat(rowLengthInTermsOfChars));
    }
    public static int randomizeStartingPlayerAndDeclareIt() {
        Random rand = new Random();
        int startingPlayer = rand.nextInt(2);
        System.out.println("PLAYER " + startingPlayer + " will go first!");
        pressEnterToContinue();
        return startingPlayer;
    }
    public static void printArr(int[][] arrToPrint) {
        int rowsPrinted = 0;
        for (int[] row : arrToPrint) {
            for (int j : row) {
                if((j > -1 && j < 10) || j % 10 == 0) {
                    continue;
                }
                else if (j == -1) {
                    System.out.print(" ".repeat(3));
                }
                else {
                    System.out.printf("%3d", j);
                }
            }
            rowsPrinted++;
            if (rowsPrinted != 1) System.out.println();
        }
    }
    public static int getPlayerChoiceDuringTurn(int[][] gameArr, int playerTurn) {
        int choice = -1;
        boolean choiceIsValid = false;
        do {
            System.out.print("Please choose a valid number: ");
            try {
                choice = Integer.parseInt(sc1.nextLine().trim());
                if (!checkIfChoiceIsValid(gameArr, choice)) choice = -1;
            } catch (Exception e) {
                System.out.println("Invalid choice. Please enter an integer!");
                pressEnterToContinue();
                displayGameState(gameArr, playerTurn);
                continue;
            }
            if(choice == -1) {
                System.out.println("That number isn't on the board. Please try again...");
                pressEnterToContinue();
                displayGameState(gameArr, playerTurn);
            } else {
                System.out.println("Successfully chose " + choice + "!");
                pressEnterToContinue();
            }
        } while (choice == -1);
        return choice;
    }
    public static boolean checkIfChoiceIsValid(int[][] gameArr, int choice) {
        boolean gameArrContainsChoice = false;
        for (int[] row : gameArr) {
            for (int i : row) {
                if (i == choice && isNotHidden(choice)) {
                    gameArrContainsChoice = true;
                    break;
                }
            }
        }
        return gameArrContainsChoice;
    }
    public static void adjustArrBasedOnChoice(int[][] gameArr, int choice) {
        int firstDigit = choice / 10;
        int secondDigit = choice % 10;

        boolean isClearAbove = true;
        boolean isClearLeft = true;

        if (firstDigit >= 5) isClearAbove = false;
        if (secondDigit >= 5) isClearLeft = false;


        for (int[] row : gameArr) {
            for (int i = 0; i < row.length; i++) {
                int thisFirstDigit = row[i] / 10;
                int thisSecondDigit = row[i] % 10;
                if (isClearAbove && isClearLeft && isNotHidden(row[i])) {
                    if (thisFirstDigit <= firstDigit && thisSecondDigit <= secondDigit) {
                        row[i] = -1;
                    }
                }
                if (isClearAbove && !isClearLeft && isNotHidden(row[i])) {
                    if (thisFirstDigit <= firstDigit && thisSecondDigit >= secondDigit) {
                        row[i] = -1;
                    }
                }
                if (!isClearAbove && isClearLeft && isNotHidden(row[i])) {
                    if (thisFirstDigit >= firstDigit && thisSecondDigit <= secondDigit) {
                        row[i] = -1;
                    }
                }
                if (!isClearAbove && !isClearLeft && isNotHidden(row[i])) {
                    if (thisFirstDigit >= firstDigit && thisSecondDigit >= secondDigit) {
                        row[i] = -1;
                    }
                }
            }
        }

    }
    public static boolean checkIfSomeoneWon(int[][] gameArr) {
        boolean someoneWon = true;
        for(int [] row: gameArr) {
            for (int j : row) {
                if(j != -1 && isNotHidden(j)) {
                    someoneWon = false;
                    break;
                }
            }
        }
        return someoneWon;
    }
    //AI specific methods
    public static void playVsAI(int[][] startingGameArr) throws InterruptedException {
        int[][] gameArr = cloneArr(startingGameArr);
        boolean someoneHasWon = false;
        int playerTurnNum = randomizeStartingPlayerAndDeclareIt();
        do {
            clearScreen();
            displayGameState(gameArr, playerTurnNum);
            int choice = 0;
            if(playerTurnNum == 0) choice = getPlayerChoiceDuringTurn(gameArr, playerTurnNum);
            else choice = getAIChoice(gameArr);
            adjustArrBasedOnChoice(gameArr, choice);
            someoneHasWon = checkIfSomeoneWon(gameArr);
            if (someoneHasWon) {
                displayGameState(gameArr, playerTurnNum);
                System.out.println("PLAYER " + (1 - playerTurnNum) + " has won!");
                if(playerTurnNum == 0) {
                    printScaryClaudeShannon();
                    Music.playSound("src/music/evilLaugh.wav");
                    Music.playSound("src/music/bellOfDeath.wav");
                    Thread.sleep(6000);
                }
                else {
                    Music.playSound("src/music/angelicSound.wav");
                    Thread.sleep(3000);
                }
            }
            else playerTurnNum = 1 - playerTurnNum;
        } while (!someoneHasWon);
    }
    public static int getAIChoice(int[][] gameArr) throws InterruptedException {
        Random rand = new Random();
        int AIChoice = -1;
        int highestRating = 0;

        printClaudeShannonThinking();
        Thread.sleep(2000);
        ArrayList<Integer> possibleChoices = new ArrayList<>();
        for (int[] row : gameArr) {
            for (int i : row) {
                if (isNotHidden(i)) {
                    possibleChoices.add(i);
                }
            }
        }
        HashMap<Integer, Integer> ratedChoices = new HashMap<>();
        for (int possibleChoice : possibleChoices) {
            ratedChoices.put(possibleChoice, rateChoice(gameArr, possibleChoice));
        }
        for (Map.Entry<Integer, Integer> entry: ratedChoices.entrySet()) {
            if (entry.getValue() > highestRating) {
                highestRating = entry.getValue();
                AIChoice = entry.getKey();
            }
        }
        if (AIChoice == -1) {
            AIChoice = possibleChoices.get(rand.nextInt(possibleChoices.size()));
        }
        System.out.println("After much deliberation, Mr. Shannon chose " + AIChoice + "!");
        pressEnterToContinue();
        return AIChoice;
    }
    public static int rateChoice(int[][] gameArr, int choiceToTest) {
        int score = 0;

        int[][] testArr = cloneArr(gameArr);
        int startingNumSpots = 0;
        for (int[] row : gameArr) {
            for (int i : row) {
                if (isNotHidden(i)) {
                    startingNumSpots++;
                }
            }
        }
        adjustArrBasedOnChoice(testArr, choiceToTest);
        int numSpotsLeft = 0;
        for (int[] row : testArr) {
            for (int i : row) {
                if (isNotHidden(i)) {
                    numSpotsLeft++;
                }
            }
        }
        score = startingNumSpots - numSpotsLeft;
        if (numSpotsLeft >= 2 && numSpotsLeft <= 4) {
            score = 0;
        } else if (numSpotsLeft == 0) {
            score = -1;
        }
        return score;
    }
    //misc
    public static boolean isNotHidden(int n) {
        return n >= 10 && n % 10 != 0;
    }
    public static void pressEnterToContinue() {
        System.out.println("[ENTER] to continue");
        sc1.nextLine();
    }
    public static void clearScreen() {
        System.out.print("\n".repeat(80));
    }
    public static int[][] cloneArr(int[][] arrToClone) {
        int[][] newArr = new int[arrToClone.length][];
        for (int i = 0; i < arrToClone.length; i++) {
            newArr[i] = arrToClone[i].clone();
        }
        return newArr;
    }
    public static boolean askToPlayAgain() {
       boolean playerWantsToPlayAgain = false;
        String choice = "";
        do {
            System.out.println("Play again? (Y/N)");
            choice = sc1.nextLine().trim().toUpperCase();
            switch (choice){
                case "Y":
                    System.out.println("Playing again...\n");
                    playerWantsToPlayAgain = true;
                    break;
                case "N":
                    System.out.println("Oh, okay...");
                    pressEnterToContinue();
                    break;
                default:
                    System.out.println("Invalid input. Please enter [Y] or [N].");
                    break;
            }
        } while (!choice.equals("Y") && !choice.equals("N"));
        return playerWantsToPlayAgain;
    }
    //graphics
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static void printClaudeShannonThinking() {
        String[] imgToPrint = {
                "⣻⣽⣻⣻⣟⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣻⣿⣿⣿⣿⣻⢟⡟⠛⢩⠀⠈⠁⡭⡙⢯⡛⣝⣩⢃⠍⢢⠑⠄⠂⠄⠀⠀⠀⠀⠀⠀⠀",
                "⣛  CLAUDE SHANNON IS  ⣟⣾⡳⣯⢳⡻⠀⠀⠀⣦⡀⠀⠲⠂⠀⠉⢦⢡⠊⠄⡁⠈⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣟  THINKING DEEPLY... ⠽⣲⢯⡝⣳⠃⠀⢀⣼⣿⣿⣷⣼⣤⡆⠜⠢⠃⡌⠐⠀⡁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣻⣷⡿⣿⣷⣿⣷⣿⣾⣭⣿⣞⣷⣽⣾⣹⢧⣓⢯⢷⡹⣜⢧⡠⠁⢸⣏⠛⠻⢿⡿⠿⠌⡌⠡⡁⠄⡈⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣽⣞⣿⣽⣿⣻⣿⣿⣟⣿⣿⣿⣿⣿⣿⡿⣟⣯⣿⢾⣵⣞⣶⠑⠀⡌⣿⣿⡿⣾⣶⡆⠂⠌⡁⠄⠂⢀⠡⢀⠆⡡⠂⠀⠀⠀⠀⠀⠀⠀",
                "⡿⣾⣽⢾⣟⣾⡿⣟⣿⣿⣽⣿⣽⣿⣿⣿⣿⣿⢿⣽⣿⠞⠛⠁⠀⡀⠁⠻⡟⠯⠽⢏⡸⢌⡱⢀⠒⠍⡢⠱⣈⠒⡡⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣳⢯⣿⣽⣯⣿⢿⣯⣿⢾⣿⡿⣿⣷⣿⡟⢋⢉⢅⢦⢂⠆⡠⢀⠸⣄⠈⣻⠞⣱⢠⡀⠘⠀⠀⠺⡌⢄⠳⣈⠣⣑⠀⠀⠀⠀⠀⠀⠀⠀",
                "⡿⣽⣻⣾⣻⣞⣿⣿⣳⣿⢿⣽⣿⢿⣽⡞⠀⠀⠈⠞⡬⣋⠆⢇⢥⡓⢫⠨⡅⠸⣄⠳⠼⢀⠦⣘⠲⣰⠈⡕⢢⡱⢌⠀⠀⠀⠀⠀⠀⠀⠀",
                "⡿⣷⢿⣷⣏⣿⢿⣾⣿⣹⣏⣿⣾⢿⣹⠇⠀⠀⠀⢈⠱⠈⠹⢰⣆⠹⣇⠀⡆⢰⡁⡾⠁⠆⠁⡈⣁⣀⠱⣈⢁⠶⡈⠀⠀⠀⠀⠰⠈⠀⠀",
                "⡿⣽⢯⡷⣯⡿⣿⣽⢾⣳⣯⣟⣾⢿⣽⠀⠀⠀⠁⠂⠅⠄⠘⡄⢎⡻⡌⡷⠁⢰⡝⡲⠍⠀⣡⠢⡑⠈⠓⡬⢌⡒⠱⠀⠀⠀⢀⠁⠀⢀⠀",
                "⣿⣹⣯⢿⣽⣻⣽⣾⣻⢯⣿⠙⠛⠻⠚⠀⠀⠀⢀⡈⡉⡄⠀⢲⠈⢱⡹⣜⠀⢘⡰⢩⠀⠀⠀⠢⡕⣢⠙⠰⠨⠌⡱⠀⠀⠀⢂⣤⣶⣶⣿",
                "⣳⣽⣲⡟⣾⣽⣳⢯⣟⣟⣾⡀⠰⠀⠀⠀⠀⠀⠀⠤⠙⢦⠀⠠⠁⠀⠣⣸⠀⠀⢆⠃⠀⠀⠀⠀⡄⠂⠓⠄⠶⠶⠶⠿⠟⠛⠫⠻⣛⠉⠀",
                "⣗⢮⡳⣟⣳⡽⣞⣟⢾⣣⣟⡇⠀⠄⠀⠀⠀⠀⠘⠠⢉⠲⠀⠀⠀⠀⠀⠀⠃⠀⠈⡒⠴⡀⠀⣀⠀⠀⠁⠀⢀⠀⠀⠀⠀⡀⢔⡪⢶⠀⠀",
                "⠙⠊⣑⠩⣠⠙⡼⢘⣋⠶⢩⢆⠀⠀⡀⠀⠀⠀⠀⠐⢈⠁⠠⢀⠀⢀⡀⢀⣀⣀⠠⢨⣞⠿⣭⣄⠀⠠⠀⠁⠀⠀⠀⠀⠀⢀⠒⠾⣿⠘⠭",
                "⠂⠱⠀⠣⠄⡙⢄⠣⢌⠲⡑⢎⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⢎⡝⠀⠂⢰⣯⣕⣪⠭⠀⠀⠀⠀⢀⡀⢠⢀⡀⣀⠈⣀⠐⡨⠐",
                "⠀⢁⠂⡡⢘⠰⡉⢎⡙⢎⡝⣺⡀⠀⢠⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⢤⡠⣄⣛⣛⣡⢒⣌⢲⡑⢊⠦⡘⢦⡡⠚⡤⢓⢬⣞⢡⠱",
                "⠀⠂⢄⠡⢌⠰⡉⢦⡙⢮⣜⡱⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⢤⣖⣻⣼⣫⣞⣵⣫⡾⡽⣞⣳⢬⡳⣜⣣⣜⡱⠦⣱⢋⡔⢏⡞⡜⣊⠌"
        };
        for (String line : imgToPrint) {
            System.out.println(line);
        }
    }
    public static void printScaryClaudeShannon() {
        String[] imgToPrint = {
                "⢆⡴⡠⢄⢠⢀⠀⠀⠀⠀⠀⠀⠀CLAUDE SHANNON⠀⠀",
                "⣟⡶⡟⠾⣒⢧⡜⣢⢀⠄⠀⠀HAS CLAIMED YOUR⠀",
                "⣿⣿⣿⣿⣿⣷⣯⠥⠂⠀⠀⠀⠀⠀⠀⠀SOUL...⠀⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣼⠀⠀⣀⣀⣀⣀⣀⣀⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⡏⠀⣾⣿⣿⣿⣿⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣇⠀⣿⣿⣿⣿⣿⣿⠿⣿⣿⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣧⠀" + RED + "⠈" + RESET+ "⠀⠉⣿⣄⠀" + RED + "⠈" + RESET+ "⣀⣨⣿⡀⠚⢇⠀⠀⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣿⡀⢦⣷⠂⣿⣿⣶⣶⣿⣿⣟⠸⠿⠀⠀⠀⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣿⣷⠈⣽⠀⠿⢛⣾⣿⣿⣿⣶⠈⢀⡐⠈⠄⠀⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣇⠈⠐⠲⢟⣛⣉⣻⣿⠇⡆⠰⢈⠰⢀⠂⠀⠀⠀",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣄⠂⣀⣌⣹⣿⠟⢋⣴⢣⠀⠘⠠⠈⡀⠀⠀⠀",
                "⡟⣼⢯⣿⣿⣿⡿⠏⠟⠊⠀⠉⠚⠉⣡⣴⢟⣵⡟⠀⠀⠀⠀⠀⠀⠀⠀",
                "⢹⡘⠎⠞⠉⠁⠀⠀⠀⠀⠀⣧⡈⢟⣫⣶⣿⠏⠀⢀⠀⠀⠀⠀⠀⠀⠀",
                "⠂⠌⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⠁⡘⣿⣿⠇⠀⠘⠀⠈⠀⠀⠀⠀⠀⠀",
                "⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡇⠀⢹⡟⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        };
        for (String line : imgToPrint) {
            System.out.println(line);
        }
    }
    public static void bidUserFarewell() {
        String[] imgToPrint = {
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⠓⠒⠻⠭⠯⡕⡮⠓⢛⠋⠩⣗⣴⣿⣯⣻⣭⡷⢦⣆⣀⠀⠀⠈⠙⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⢟⡿⢟⣿⡇⠀⠀⠀⠀⠀⠁⡀⢀⢵⡂⣾⡝⣯⣿⣻⣿⣿⣽⣫⡿⣼⡳⡀⠀⠐⠁⠙⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠗⢩⣿⣹⠽⢁⣶⣿⣷⣤⣀⠉⠀⠘⠩⠘⠐⠾⠛⠉⠉⠁⠈⠉⠘⢽⠳⣮⠀⠀⠀⠀⡀⠁⣿  CLAUDE SHANNON   ⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠏⢠⣾⢋⡡⣢⣾⣿⣿⣿⣿⣿⣿⣶⣶⣤⣦⣶⣶⣶⣶⣶⣶⣤⡀⠀⠀⠩⡄⠀⠀⠀⠀⠀⢠⣿ BIDS YOU FAREWELL ⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⢁⢞⠠⣰⣺⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣶⣦⣦⠀⠠⡰⣄⠣⢼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇⢊⠁⢰⠞⡑⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠂⠐⢾⡜⣇⠠⣻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡝⢀⠀⡅⠀⣸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠂⠈⣵⢙⡆⣞⣪⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⣶⡄⠀⠀⠀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡓⢄⢿⢿⠁⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣷⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢻⣇⠀⠀⣾⣿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣗⠱⠸⡰⢿⡽⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢸⡻⡆⢀⣿⣿⠛⠋⠉⠉⠉⠋⠛⠻⠿⡿⣿⣿⣿⢿⣿⠿⠿⠿⠟⠿⢿⣿⣿⣿⣿⠆⠀⠡⢉⣽⣷⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⠋⠈⠸⣿⡷⡰⠀⠀⡀⠀⠀⠀⠀⠀⠀⢽⣷⠀⡀⠀⠀⠀⠀⠀⢀⣀⣉⡙⢿⣿⢤⢈⣴⣿⣿⣷⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣽",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⡀⢀⣼⣿⣿⠤⢶⣷⣤⣄⠈⠀⠀⠀⣰⣿⣿⣿⠇⠀⠀⠦⢀⡠⣖⣈⢻⣿⣿⣿⠂⣼⠟⣿⡿⣟⣿⣿⡷⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣽⣻⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣬⡛⣿⣿⣻⣧⣤⣬⣤⣤⣴⣤⣴⣿⣿⣿⣿⣤⣆⠀⢐⣶⣿⣿⣿⣯⣾⣿⣿⣽⢻⣄⠘⣿⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣏⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣯⠛⢸⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣦⣻⣿⣿⣿⣿⣿⣿⣿⠿⣼⣃⣼⣿⣿⣿⣿⡇⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣽⣿⣿⣿⣿⣿⣿⣿⠟⢻⣻⣿⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⣿⣿⣽⣿⣟⣶⣿⣿⣾⣿⣿⣿⣿⣧⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣟⣷⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣏⣛⣿⠿⢃⣔⣾⣿⣿⣿⣿⣿⣿⣆⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⠿⠽⠿⠛⠛⠛⠛⠿⠿⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣎⠉⠛⢿⣿⠿⣿⣿⣿⣿⣻⣿⣿⣿⣿⣿⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠛⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠹⣿⣿⡟⡟⠟⣿⣿⣿⣷⣦⢐⣿⣿⣿⣿⣿⣿⣏⣿⣿⣿⣿⡏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⣿⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠁⠀⠻⡼⠇⠀⠀⣈⡉⠛⠛⠯⠼⢿⣿⡿⣿⠿⢿⣇⣿⢿⣿⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟⠀⠀⠀⣾⡠⢆⡐⣿⣿⠙⠷⠖⣶⣴⣾⣶⣿⣿⣷⣾⣼⣵⣿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢹⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⡿⣯⣿⣿⣿⣿⣿⣿⠿⠋⠀⠀⠀⠀⣿⣷⢘⢿⣮⣍⡈⣐⣒⢰⠙⢿⣿⣿⣿⣿⣾⣿⣯⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢨⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⡟⣾⣿⣿⡿⠟⠋⠀⠀⠀⠀⠀⠀⠀⣻⣿⣯⡂⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣽⢏⣽⣿⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⣿⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣽⠿⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⣿⣿⣷⣥⣀⡉⠛⠻⣿⠯⢿⡿⣿⣵⣿⣿⣷⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿⡿",
                "⣿⣿⣿⡿⠟⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⣿⣿⣿⣿⡦⣝⠠⡒⣢⠐⢴⣿⣿⣿⣿⡿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⣿⣿⣿⣿⣿"
        };
        for (String line : imgToPrint) {
            System.out.println(line);
        }
    }
    public static void printTitleScreen() {
        String[] imgToPrint = {
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡀⠀⠀⡀⢀⠀⢀⠀⡀⠀⡀⢀⠀⡀⢀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠠⠀⠀⠄⠂⠀⠂⠁⠠⠁⠀⠌⠐⠀⠄⡈⠄⡐⠠⠐⠀⠄⢂⠐⢀⠂⠄⠂⡈⠄⠐⠀⡀⠀⠀⠀⠀⠀⠀⢀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠄⠂⠀⠈⠀⠈ THE FABLED 2D ARRAY GAME ⡐⡈⠄⠂⠌⡐⠀⠄⠂⠁⢀⠠⠀⠀⠀⠀⢠⣿⣿⣿⣿⣿⣿⣶⡶⠀⠀⠀⠀⠀⠀⠀⢀⢾⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠐⠀⠀⠄⠀⠀⠀⠁⠀⠠⠀⢀⠀⠐⠈⠀ FEATURING CLAUDE SHANNON⠡⢁⠔⡈⠔⠂⠄⠡⢈⠠⠁⡀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣿⣿⣿⡿⡀⠀⡄⠀⠀⠀⠀⣌⣌⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⡀⠄⠀⡀⠀⠂⠁⠀⡐⠀⠠⠀⠀⠂⠁⠄⠐⡀⠄⢁⠂⡈⠐⡈⠄⠡⠌⡘⠄⠣⢑⡈⢆⡑⢌⠢⡘⢄⠣⠌⡂⠜⠠⠉⢄⡁⢂⠐⠠⠀⡁⠀⠠⠀⢰⣿⣿⣿⣿⣿⣿⣿⣿⣿⠁⡈⡀⠀⢀⠠⢤⢉⡭⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⢀⠀⠀⠄⠀⡐⠀⠠⠁⠀⠄⠁⡐⠀⢁⠂⡈⠄⠠⠈⠄⡐⠠⢁⡐⠨⣁⠒⠌⣌⠑⣂⠲⡄⢜⡠⢃⡌⢢⠱⡘⠤⡃⠥⡉⠤⠐⡂⠌⠠⢁⠠⠀⠄⠀⠘⣉⠛⠿⣿⢿⠿⠛⠛⠛⠀⣿⣧⣷⡄⡴⠜⠓⢀⣠⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠠⢀⠈⠠⠐⠀⠌⠀⠄⠡⠐⠠⠐⠀⢂⠐⠠⠈⠄⡁⠂⠄⠃⠤⢈⠒⡀⢎⡐⢢⡉⢆⡓⡸⢄⣣⠣⠜⢤⠣⡙⢆⡍⢢⡑⢌⠡⠒⡈⡐⢀⠂⠀⡀⠂⣼⣉⣀⣠⣯⠀⠀⠐⠒⠂⠀⠈⠙⢻⣿⣼⡀⠀⠱⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠐⠀⠀⠀⠀⠀⠀⠀⠀⡀⠀⠁⡈⠀⠂⠌⠠⢁⠂⠄⣉⠰⠉⡄⢣⠘⡐⢢⢡⠣⣜⢣⡕⣣⠏⡴⣩⢛⠦⡱⣍⠶⣈⠧⡘⠤⢃⠅⠢⡐⠠⠌⢀⠐⠀⢿⣿⣿⣿⡟⠀⠀⠀⣿⣷⣶⠀⣰⣿⣿⡿⠀⠀⢠⡍⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⡁⠀⠀⠀⠀⠀⠀⠀⢀⠀⠐⠀⠠⠀⠡⣈⢁⠂⠜⡀⠆⣂⠱⡈⠆⡭⡘⢥⢊⡕⣎⠷⣚⣥⢻⡜⡥⣏⢾⡱⢎⡳⣍⢲⡡⢋⡌⡊⠅⢢⠑⠌⠀⠄⠂⠸⣿⣿⣿⡃⠀⠀⠀⠈⠻⠋⠀⣿⣿⡿⠁⣀⠆⠊⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠄⠁⠀⠀⠀⠀⠀⢀⠀⠀⡐⢀⠂⠠⠁⡄⢂ PRESS [ENTER] TO PLAY ⢭⣲⡙⣮⢳⡬⣓⡌⢧⡘⣡⢊⡐⠌⡌⠀⢂⠈⠀⢻⣿⣯⡅⠀⢀⠀⠀⠀⠀⣸⣿⣿⡷⢠⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠌⠀⠀⠄⠂⠠⢈⠠⠐⠠⠐⠠⢁⠀⡃⠰⢈⠰⣀⠣⠌⡨⢑⠂⡭⡘⡴⡹⣌⠷⣹⢎⣿⣱⣧⢻⡝⣧⢳⡹⡜⣧⢳⡱⣊⠶⡑⠆⠦⣈⠒⡰⠈⠀⠄⢀⡘⣯⠻⠿⡈⠈⠀⠐⠀⠰⢻⣿⣿⣿⡁⢿⣦⢄⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠠⠈⡐⠀⠡⢀⠂⠡⠘⡈⠐⡄⠠⢁⠢⢁⠒⡀⠆⢡⠂⡍⠰⢡⡑⢬⡱⣌⡳⣝⡾⣵⡻⣮⡟⣜⣧⣛⡶⣝⣮⣳⠵⠉⡂⠉⠂⠈⠀⠀⠀⠀⠀⠈⠄⡤⢹⣿⣯⣥⡄⠀⠁⣌⣀⡶⢿⣿⠯⠁⠸⣿⡉⠆⠈⠘⠀⠒⠄⠠⣄⣀⠀",
                "⠀⠀⠂⢁⠀⠌⡐⠠⠈⡄⠡⢀⠃⠰⢀⠡⢂⠁⣂⠡⠘⡄⢌⠰⡉⢦⠙⠦⠻⣜⡷⣯⣾⣷⣟⣷⠿⠻⢾⠽⢃⠀⠀⠀⠀⡆⠱⡆⠀⠠⠁⠀⠀⠀⢀⢢⡤⠀⠂⠻⣿⡔⠊⣀⡜⢩⣿⣧⡼⠃⠀⠀⢠⣾⠀⠀⢂⠀⠀⠀⠀⠀⠀⠙⠿",
                "⠀⡡⠈⠠⠀⠂⠄⡁⠆⢠⠁⠢⢈⢁⠂⡅⢂⠡⠂⠌⡡⠐⡌⠂⡑⠤⠈⠀⠀⠠⠝⠛⠓⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠔⠠⠈⠀⢠⠀⠀⠀⠤⡉⣰⠭⢡⠆⠀⢻⣿⣿⣿⣿⡸⠛⠉⠀⡀⠀⠀⢠⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⢃⠠⢁⠈⠤⠑⢠⠘⢠⠈⠔⡁⠢⠐⡈⠔⠂⢉⠐⠠⢁⡔⠊⠙⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠂⠀⠀⠈⠁⠀⢠⠁⠈⠀⠐⢡⢮⠀⠀⢻⣿⣿⡅⠸⡗⢁⠢⢁⠀⡄⠀⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠐⠄⠀⠀⠁⠀⠊⠀⠈⠀⠁⠁⠀⠀⠀⠀⠀⢀⡴⣟⣩⡖⢂⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠑⠂⠀⠀⠲⡦⢀⡴⠤⢓⠀⠀⠘⣿⡟⠸⠄⢐⡀⡓⠌⢸⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡾⡏⣰⣠⡈⠳⡘⣩⣕⡎⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠖⠾⠘⡁⠀⠀⠀⢿⡿⠃⠀⠈⠐⠨⠁⠆⠀⠀⠀⠀⠀⠀⠀⠀⠠⠉⠁⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠄⠀⣀⡀⠀⠀⣀⡠⣾⣭⣭⣷⣶⣶⣔⢽⣩⡼⣶⠟⣸⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢎⠁⠀⠀⠀⠀⠀⠠⠀⠀⠀⠀⠀⢀⠀⠀⠀⠅⠀⠀⠀⠀⠀",
                "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠁⠔⢋⣉⣴⣿⣿⣿⣿⣿⣿⣿⢻⡝⡺⠛⠈⠸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⠀⠀⠀⠀⠀⠀⠊⠀⠀⠀⠀⠀⠀⠠⡀⠀⠁⠈⢀⠀⠀⠀",
                "⠀⠀⠀⣀⣀⣀⣀⣀⢀⣀⡀⣠⣴⣞⣽⡟⢻⣿⣿⣿⣿⣿⣿⠏⢘⣡⣤⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣠⣤⣤⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠘⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠃⠀⠀⠀",
                "⣟⣻⢻⣙⢓⡛⠞⣹⢻⣿⣡⣿⣿⣿⣿⡄⢠⣿⣿⣿⣿⣿⣿⢰⡭⣥⢭⠬⠭⠭⣥⢤⠶⢶⣶⣶⣶⣶⣶⢶⡶⣶⣿⣿⣿⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠄⠈⠀⠀⠀⠀⠀⠀",
                "⠿⣯⣟⣿⡛⢻⣿⣿⢸⣽⢻⣿⡏⣼⡏⢠⡞⠣⠓⡟⣿⣿⣇⣶⡾⣽⢯⣿⡾⣷⢿⣾⢿⣾⣿⣿⣿⣿⣿⣿⠿⣿⣿⣿⣿⣿⣀⣀⡀⠀⡀⢠⣄⣀⡀⢀⠀⠀⠀⢠⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⣿⡿⣿⣽⡃⠘⠯⠏⠸⠞⣼⡿⢨⡟⢠⣿⡓⣀⠀⢘⡝⣿⣸⣧⣿⣥⣿⣮⡑⣮⣨⣟⣯⠟⡿⣿⣿⣿⡩⠀⣷⢨⣩⣛⢿⠿⣿⣿⠂⢰⡇⢸⠿⠶⣩⣤⢈⣠⢁⡸⠂⠌⢀⠃⡁⣿⣶⣶⣤⣤⣀⠀⣀⠀⠀⠀⠀⠀⢀⡃⠀⠀⠀⠀⠀",
                "⣻⣽⣳⠾⡅⢸⡤⠐⠆⣖⣻⠆⢹⡄⣸⠑⠛⠛⠀⣼⢡⡯⣷⣿⣿⣿⣿⣳⣘⡿⠼⠋⣾⣆⠐⡸⢯⡟⡥⠂⡟⠀⢿⣿⣿⣇⠅⣈⡡⣘⠇⠀⠈⠀⠀⣿⣽⣿⣿⣿⣯⣦⣦⣴⣓⡿⠽⣿⢟⢏⠶⢸⡟⣿⣿⣶⣶⣤⣬⣀⣀⡀⠀⠀⠀",
                "⣷⣫⣽⣫⣇⣺⣿⣾⡈⠹⠙⣏⣼⠇⡆⠀⠀⠀⠀⠟⢘⠂⠈⡋⡙⠛⠻⢿⣦⣃⢀⣁⣻⣿⣽⣷⢠⣶⢰⣶⣶⣾⣿⣶⣿⣿⣿⡿⡵⣿⢸⣴⣄⢂⠀⡿⢺⡿⣿⣿⡟⣿⣿⣿⣿⣿⣿⣶⣮⡌⢹⢸⡇⡜⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣶",
                "⣿⣿⣿⣯⣥⣽⣿⣿⣧⡟⠀⠈⠙⠂⠃⠀⠈⠀⠀⠀⡀⠄⠀⣿⣿⣯⣟⢿⣿⣿⣯⣧⣿⠻⠿⠿⡸⢿⣸⣟⣟⡛⡭⣭⡻⣿⣿⣳⢛⣿⢸⠑⠈⠄⢂⣿⣿⣿⣷⣯⣭⣻⠿⣿⣿⣿⣿⣿⣻⡇⠲⢩⣗⣺⡭⠟⠻⠿⣟⠻⣿⣿⣿⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣟⢿⣿⣿⣆⣄⠀⠀⠀⠁⠀⠀⠀⠀⡤⡥⠀⣿⣿⣿⣿⣶⣷⢶⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡏⣿⠾⣿⣷⣯⣇⣉⠉⢈⡐⢀⠂⣄⣸⣿⣿⠿⠿⠿⣿⡟⡓⣯⠛⣯⣭⣭⣧⣾⣧⣶⡴⣍⡳⢭⠇⣿⠐⢛⣸⣻⣿⣿",
                "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇⣿⣄⠀⠈⠀⠀⠀⠀⠀⣶⣧⣀⣛⣻⣿⣿⣿⣿⢯⡟⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠃⣿⡏⢻⣿⣿⣿⣿⣶⠿⡼⣭⣿⣿⣿⣿⣿⣿⣿⣿⣿⣼⣛⣿⠀⣷⣾⣿⣿⣿⣿⡵⣙⢦⡙⣎⢏⡿⠽⠿⠿⠿⠞⠛"
        };
        for (String line : imgToPrint) {
            System.out.println(line);
        }
    }
}
