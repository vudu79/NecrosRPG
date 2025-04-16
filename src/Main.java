import java.util.Scanner;

public class Main {
    public static void main(String[] args)  {

        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println(Colors.ANSI_CYAN + "Представляем RPG игру нового поколения - консольный сингл - << НЕКРОС ХОЧЕТ ЖИТЬ >>" + Colors.ANSI_RESET);
        System.out.println("Качайте, не пожалеете! Есть платный контент! ");
        System.out.println("Если не хотите залипать в рекламу, переведите разработчику 100500 тугриков, по брацки...");
        System.out.println();
        System.out.println("© ЗАО «Личинка Джуна», 2025. Все права защищены.");
        System.out.println();
        System.out.println("Чтобы выйти из этой замечательной игры наберите команду <<рентгеноэлектрокардиографический синхрофазотрон>>");
        System.out.println("или введите имя главного нагибатора игры:");

        String name;
        while (sc.hasNext()) {
            name = sc.nextLine();
            name = name.trim();

            if (!name.isEmpty()) {
                if (name.equals("рентгеноэлектрокардиографический синхрофазотрон")) {
                    System.out.println("Правильный выбор! Всего доброго!");
                    System.exit(1);
                } else {
                    RPGWorld world = new RPGWorld(name);
                    world.start();
                }
            } else {
                System.out.println("Попробуйте еще раз");
            }
        }
        sc.close();
    }
}