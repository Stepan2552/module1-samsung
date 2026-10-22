import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String castle = "\uD83C\uDFF0"; // Замок 🏰
        String potion = "\uD83E\uDDEA"; // Исцеляющее зелье 🧪

        int sizeBoard = 5;
        int step = 0;
        int monstersDefeated = 0; // Счётчик побеждённых противников

        Person person = new Person(sizeBoard);

        Random random = new Random();
        int castleX = 1 + random.nextInt(sizeBoard);
        int castleY = 1;

        // Зелье генерируется на строках 2–4, чтобы не пересекаться со стартом игрока и замком
        int potionX = 1 + random.nextInt(sizeBoard);
        int potionY = 2 + random.nextInt(sizeBoard - 2);

        String[][] board = new String[sizeBoard][sizeBoard];
        for (int y = 1; y <= sizeBoard; y++) {
            for (int x = 1; x <= sizeBoard; x++) {
                board[y - 1][x - 1] = "  ";
            }
        }

        int countMonster = sizeBoard * sizeBoard - sizeBoard - 1;
        Monster[] monsters = new Monster[countMonster];

        // Первые два противника — боссы (BigMonster), остальные — обычные
        for (int i = 0; i < countMonster; i++) {
            Monster monster;
            if (i < 2) {
                monster = new BigMonster(sizeBoard);
            } else {
                monster = new Monster(sizeBoard);
            }
            monsters[i] = monster;
            board[monster.getY() - 1][monster.getX() - 1] = monster.getImage();
        }

        board[potionY - 1][potionX - 1] = potion;
        board[castleY - 1][castleX - 1] = castle;

        System.out.println("Привет! Ты готов начать играть в игру? (Напиши: ДА или НЕТ)");

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        System.out.println("Ваш ответ:\t" + answer);

        switch (answer) {
            case "ДА":
                System.out.println("Выбери сложность игры (от 1 до 5):");
                int difficultGame = scanner.nextInt();
                System.out.println("Выбранная сложность:\t" + difficultGame);

                while (true) {
                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                    outputBoard(board, person.getLive());

                    System.out.println("Куда пойдёт персонаж? Введите координаты X (по горизонтали) и Y (по вертикали):"
                            + "\nТекущие координаты — (X: " + person.getX()
                            + ", Y: " + person.getY() + ")");

                    int x = scanner.nextInt();
                    int y = scanner.nextInt();
                    System.out.println("Введено: X = " + x + ", Y = " + y);

                    if (person.isMoveCorrect(x, y)) {
                        // 1. Ход на свободную клетку
                        if (board[y - 1][x - 1].equals("  ")) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            step++;

                            System.out.println("Ход выполнен! Новые координаты: ("
                                    + person.getX() + ", " + person.getY()
                                    + ") | Шаг: " + step);

                            // 2. Вход в замок — победа
                        } else if (board[y - 1][x - 1].equals(castle)) {
                            step++;
                            System.out.println("\n🎉 Поздравляем! Вы добрались до замка и победили!");
                            System.out.println("Итог игры: шагов сделано — " + step
                                    + ", монстров повержено — " + monstersDefeated);
                            break;

                            // 3. Подбор зелья здоровья
                        } else if (board[y - 1][x - 1].equals(potion)) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            person.upLive();
                            step++;

                            System.out.println("✨ Вы подобрали зелье исцеления! +1 жизнь.");
                            System.out.println("Жизней: " + person.getLive() + " | Шаг: " + step);

                            // 4. Битва с монстром
                        } else {
                            for (Monster monster : monsters) {
                                if (monster.conflictPerson(x, y)) {
                                    if (monster.taskMonster(difficultGame)) {
                                        board[person.getY() - 1][person.getX() - 1] = "  ";
                                        person.move(x, y);
                                        step++;
                                        monstersDefeated++;
                                        System.out.println("Враг побеждён! Всего повержено: " + monstersDefeated);
                                    } else {
                                        person.downLive();
                                        System.out.println("Ошибка в примере! Вы потеряли жизнь. Осталось: " + person.getLive());
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println("Некорректный ход! Шагнуть можно только на соседнюю клетку по вертикали или горизонтали.");
                    }

                    if (person.getLive() <= 0) {
                        break;
                    }
                }

                if (person.getLive() <= 0) {
                    System.out.println("\n💀 Закончились жизни. Игра окончена!");
                    System.out.println("Итог: пройдено шагов — " + step
                            + ", побеждено монстров — " + monstersDefeated);
                }
                break;

            case "НЕТ":
                System.out.println("Жаль, приходи ещё!");
                break;

            default:
                System.out.println("Данные введены некорректно");
                break;
        }
    }

    static void outputBoard(String[][] board, int live) {
        String leftBlock = "| ";
        String rightBlock = "|";
        String wall = "+ —— + —— + —— + —— + —— +";

        System.out.println("       1    2    3    4    5  <- X");
        System.out.println("     " + wall);

        for (int y = 0; y < board.length; y++) {
            System.out.print(" " + (y + 1) + "   ");

            for (int x = 0; x < board[y].length; x++) {
                System.out.print(leftBlock + board[y][x] + " ");
            }
            System.out.println(rightBlock);
            System.out.println("     " + wall);
        }

        System.out.println("Количество жизней:\t" + live + "\n");
    }
}
