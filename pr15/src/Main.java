import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static DateTimeFormatter frmtr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static Scanner sc = new Scanner(System.in);
    public static String p1Name = "Player1";
    public static String p2Name = "Player2";
    public static String STATS_FILE = "stats.txt";
    public static final int STATS_SIZE = 100;
    public static int statsCount = 0;
    public static LocalDateTime[] gameDates = new LocalDateTime[STATS_SIZE];
    public static String[] winners = new String[STATS_SIZE];

    public static void main(String[] args) {
        String[] config = loadConfig();
        p1Name = config[1];
        p2Name = config[2];
        int size = 5;
        try {
            size = Integer.parseInt(config[0]);
        } catch (NumberFormatException e) {
            System.out.println("Помивка 😡🤬: " + e.getMessage());
        }
        loadStats();
        while (true) {
            int choice = mainMenu();
            if (choice == 1) {
                game(size);
            } else if (choice == 2) {
                int newSize = settings(size);
                if (newSize != -1) {
                    size = newSize;
                    System.out.println("Розмір поля змінено!");
                    saveConfig(size, p1Name, p2Name);
                }
            } else if (choice == 3) {
                printStats();
            } else if (choice == 4) {
                printConfig(size, p1Name, p2Name);
            } else if (choice == 5) {
                saveStats();
                saveConfig(size, p1Name, p2Name);
                System.out.println("Ви обрали вихід!");
                return;
            } else {
                System.out.println("Щось не те, спробуйте ще раз!");
            }
        }
    }

    public static int mainMenu() {
        String menu = """
                \n1. Грати(Нова гра)
                2. Налаштування
                3. Переглянути статистику
                4. Переглянути конфіг
                5. Вихід
                """;
        System.out.println(menu);
        System.out.print("Сюди : ");
        if (sc.hasNextInt()) {
            int choice = sc.nextInt();
            sc.nextLine();
            return choice;
        }
        sc.nextLine();
        return -1;
    }

    public static int settings(int currentSize) {
        System.out.println("\nВи обрали налаштування!");
        while (true) {
            System.out.println("""
                    \nОберіть розмір поля :
                    1. 3x3
                    2. 5x5
                    3. 7x7
                    4. 9x9
                    5. Змінити ім'я гравця
                    6. Головне меню""");
            System.out.print("\nСюди : ");
            if (sc.hasNextInt()) {
                int choice = sc.nextInt();
                sc.nextLine();
                int size = -1;
                if (choice == 1) {
                    size = 5;
                    currentSize = size;
                } else if (choice == 2) {
                    size = 9;
                    currentSize = size;
                } else if (choice == 3) {
                    size = 13;
                    currentSize = size;
                } else if (choice == 4) {
                    size = 17;
                    currentSize = size;
                } else if (choice == 5) {
                    System.out.print("Введіть ім'я першого гравця(X) : ");
                    p1Name = sc.nextLine();
                    System.out.print("Введіть ім'я другого гравця(O) : ");
                    p2Name = sc.nextLine();
                    System.out.println("Імена гравців змінено!");
                    saveConfig(currentSize, p1Name, p2Name);
                    return -1;
                } else if (choice == 6) {
                    return -1;
                } else {
                    System.out.println("Немає такого (( (правильно буде число від 1 до 6)");
                }
                if(size != -1){
                    return size;
                }
            } else {
                System.out.println("Ви ввели не число! Спробуйте ще раз.");
                sc.nextLine();
            }
        }
    }


    public static char[][] initializeGameField(int size) {
        char[][] array = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i % 2 == 1) {
                    array[i][j] = '-';
                } else if (j % 2 == 1) {
                    array[i][j] = '|';
                } else {
                    array[i][j] = ' ';
                }
            }
        }
        return array;
    }

    public static void displayGameField(char[][] array, int masssize, int masssize1) {
        System.out.println("\nВаше поле:");
        System.out.print(" ");
        for (int i = 0; i < masssize1; i++) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();
        for (int i = 0; i < masssize; i++) {
            if (i % 2 == 0) {
                System.out.print(i / 2 + 1);
            } else {
                System.out.print(" ");
            }
            for (int j = 0; j < masssize; j++) {
                System.out.print(array[i][j]);
            }
            System.out.println();
        }
    }

    public static boolean move(char[][] array, int masssize, char currentplayer, Scanner sc) {
        System.out.println("Введіть першу координату : ");
        if (Main.sc.hasNextInt()) {
            int cord1 = Main.sc.nextInt() - 1;
            System.out.println("Введіть другу координату : ");
            if (Main.sc.hasNextInt()) {
                int cord2 = Main.sc.nextInt() - 1;
                cord1 = cord1 * 2;
                cord2 = cord2 * 2;
                if(cord1 > masssize || cord2 > masssize || cord1 < 0 || cord2 < 0) {
                    System.out.println("Ви ввели невірні координати(((");
                    return false;
                } else if (array[cord1][cord2] == ' ') {
                    array[cord1][cord2] = currentplayer;
                    return true;
                } else {
                    System.out.println("Клітинка зайнята, спробуйте ще раз !!))");
                    return false;
                }
            }
        }
        System.out.println("Ви ввели не числове значення, спробуйте ще раз!");
        Main.sc.next();
        return false;
    }

    public static void game(int size) {
        char[][] array = initializeGameField(size);
        int masssize = array.length;
        int masssize1 = masssize - 2;
        if (size == 9) {
            masssize1 = masssize1 - 2;
        } else if (size == 13) {
            masssize1 = masssize1 - 4;
        } else if (size == 17) {
            masssize1 = masssize1 - 6;
        }
        int c = 0;
        char currentplayer = 'X';
        boolean endgame = false;
        while (!endgame) {
            displayGameField(array, masssize, masssize1);
            String currentName = (currentplayer == 'X') ? p1Name : p2Name;
            System.out.println("Хід гравця: " + currentName + " (" + currentplayer + ")");
            if (move(array, masssize, currentplayer, sc)) {
                c = c + 1;
                if ((size == 5 && c == 9) || (size == 9 && c == 25) || (size == 13 && c == 49) || (size == 17 && c == 81)) {
                    displayGameField(array, masssize, masssize1);
                    System.out.println("\nНічия!");
                    addStats("Нічия");
                    return;
                } else if (checkWin(array, currentplayer, masssize)) {
                    displayGameField(array, masssize, masssize1);
                    System.out.println("\nГравець " + currentName + " (" + currentplayer + ") переміг!");
                    addStats(currentName);
                    return;
                }
                if (currentplayer == 'X') {
                    currentplayer = 'O';
                } else {
                    currentplayer = 'X';
                }
            }
        }
    }

    public static boolean checkWin(char[][] array, char lastplayer, int masssize) {
        for (int i = 0; i < masssize; i += 2) {
            for (int j = 0; j < masssize - 4; j += 2) {
                if (array[i][j] == lastplayer && array[i][j + 2] == lastplayer && array[i][j + 4] == lastplayer) {
                    return true;
                }
            }
        }
        for (int i = 0; i < masssize - 4; i += 2) {
            for (int j = 0; j < masssize; j += 2) {
                if (array[i][j] == lastplayer && array[i + 2][j] == lastplayer && array[i + 4][j] == lastplayer) {
                    return true;
                }
            }
        }
        for (int i = 0; i < masssize - 4; i += 2) {
            for (int j = 0; j < masssize - 4; j += 2) {
                if (array[i][j] == lastplayer && array[i + 2][j + 2] == lastplayer && array[i + 4][j + 4] == lastplayer) {
                    return true;
                }
            }
        }
        for (int i = 0; i < masssize - 4; i += 2) {
            for (int j = 4; j < masssize; j += 2) {
                if (array[i][j] == lastplayer && array[i + 2][j - 2] == lastplayer && array[i + 4][j - 4] == lastplayer) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void saveConfig(int size, String player1, String player2) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("config.txt"));
            writer.write("size=" + size + "\n" + "player1=" + player1 + "\n" + "player2=" + player2 + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Помивка 😡🤬: " + e.getMessage());
        }
    }

    public static void printConfig(int size, String p1, String p2) {
        System.out.println("\nПоточний розмір поля: " + size + "\n");
        System.out.println("Ім'я першого гравця: " + p1 + "\n");
        System.out.println("Ім'я другого гравця: " + p2);
    }

    public static String[] loadConfig() {
        String[] config = {"5", "Player1", "Player2"};
        try {
            BufferedReader br = new BufferedReader(new FileReader("config.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() >= 5 && line.charAt(0) == 's' && line.charAt(1) == 'i' && line.charAt(2) == 'z' && line.charAt(3) == 'e' && line.charAt(4) == '=') {
                    String value = "";
                    for (int i = 5; i < line.length(); i++) {
                        value += line.charAt(i);
                    }
                    config[0] = value;
                } else if (line.length() >= 8 && line.charAt(0) == 'p' && line.charAt(1) == 'l' && line.charAt(2) == 'a' && line.charAt(3) == 'y' && line.charAt(4) == 'e' && line.charAt(5) == 'r' && line.charAt(6) == '1' && line.charAt(7) == '=') {
                    String value = "";
                    for (int i = 8; i < line.length(); i++) {
                        value += line.charAt(i);
                    }
                    config[1] = value;
                } else if (line.length() >= 8 && line.charAt(0) == 'p' && line.charAt(1) == 'l' && line.charAt(2) == 'a' && line.charAt(3) == 'y' && line.charAt(4) == 'e' && line.charAt(5) == 'r' && line.charAt(6) == '2' && line.charAt(7) == '=') {
                    String value = "";
                    for (int i = 8; i < line.length(); i++) value += line.charAt(i);
                    config[2] = value;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Помивка 😡🤬: " + e.getMessage());
        }
        return config;
    }

    public static void addStats(String winner) {
        if (statsCount < STATS_SIZE) {
            gameDates[statsCount] = LocalDateTime.now();
            winners[statsCount] = winner;
            statsCount = statsCount + 1;
        }
    }

    public static void printStats() {
        if (statsCount == 0) {
            System.out.println("\nСтатистика відсутня!");
            return;
        }
        for (int i = 0; i < statsCount; i++) {
            System.out.println("\nДата та час гри: " + gameDates[i].format(frmtr));
            if (winners[i].equals("Нічия")) {
                System.out.println("Результат : Нічия");
            } else {
                System.out.println("Результат : переміг " + winners[i]);
            }
        }
    }

    public static void saveStats() {
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(STATS_FILE))) {
            for (int i = 0; i < statsCount; i++) {
                wr.write(gameDates[i].format(frmtr) + "|" + winners[i] + "\n");
            }
            System.out.println("\nСтатистику збережено успішно.");
        } catch (IOException e) {
            System.out.println("Помивка 😡🤬: " + e.getMessage());
        }
    }

    public static void loadStats() {
        statsCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(STATS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && statsCount < STATS_SIZE) {
                if (line.equals("")) continue;
                int sep = -1;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '|') {
                        sep = i;
                        break;
                    }
                }
                if (sep > 0) {
                    String dateStr = "";
                    for (int i = 0; i < sep; i++) dateStr += line.charAt(i);
                    String winnerStr = "";
                    for (int i = sep + 1; i < line.length(); i++) winnerStr += line.charAt(i);
                    gameDates[statsCount] = LocalDateTime.parse(dateStr, frmtr);
                    winners[statsCount] = winnerStr;
                    statsCount = statsCount + 1;
                }
            }
        } catch (IOException | java.time.DateTimeException e) {
            System.out.println("Помивка 😡🤬: " + e.getMessage());
        }
    }
}

