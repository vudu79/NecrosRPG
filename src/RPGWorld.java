import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class RPGWorld {
    private final Hero hero;
    private final Map<Integer, List<GameUnit>> locations;
    private final Map<Integer, List<GameUnit>> bosses;
    public Dealer dealer;
    private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private final Map<Integer, String> numOfLocations = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(0, "Темный лес"),
            new AbstractMap.SimpleEntry<>(1, "Мрачная пещера"),
            new AbstractMap.SimpleEntry<>(2, "Старый замок"),
            new AbstractMap.SimpleEntry<>(3, "Заброшенная деревня"),
            new AbstractMap.SimpleEntry<>(4, "Мертвый пляж")
    );

    public RPGWorld(String name) {
        this.hero = new Hero(name, 1, 100);
        this.bosses = createBosses();

//        GameUnit wwww = new Goblin("wwwwww", 1);
//        wwww.setPawn(true);
//        GameUnit qqqq = new Goblin("qqqqqq", 1);
//        qqqq.setPawn(true);
//        hero.addPawnToArmy(wwww);
//        hero.addPawnToArmy(qqqq);

        this.locations = createLocations();
        this.dealer = createDealer();
        printAdvertising();
        System.out.println();
        System.out.println(Colors.ANSI_YELLOW + "Мир игры создан!" + Colors.ANSI_RESET);
        System.out.println();
        System.out.println(Colors.ANSI_CYAN + "================= Незамысловатые правила ===================" + Colors.ANSI_RESET);
        System.out.println("Главный герой, это воин-некромант (ну он себя таким считает), бродит по локациям и убивает все что видит.");
        System.out.println("Он может делать это в одного, а может поднять себе умертвие из убитого врага.");
        System.out.println("Чтобы это сделать, надо подкачаться! Герой должен поднять свой уровень манны до 15 единиц (каждый левелап + 5 ед.)");
        System.out.println("На одного умертвия необходимо 15 единиц маны. После подьема нечисти - любое количество маны обнуляется.");
        System.out.println("Всего наш некрос может управлять тремя умертвиями.");
        System.out.println("Урон и другие статы поднятых умертвий урезаны на 30 % от изначальных.");
        System.out.println("Пешки также качаются, если им повезет убить моба раньше героя.");
        System.out.println("Боевка игры - это лютый рандом, все как мы не любим. Стороны атакуют друг друга через шаг.");
        System.out.println("На одном шаге герой со своими пешками может атаковать врага одновременно, также как и враг на следующем шаге");
        System.out.println("может нанести урон как игроку, так и всем его пешкам. Главное - мобы в локациях не лечатся, поэтому если вас убили,");
        System.out.println("то можно потом зайти в локацию и допинать вражину).");
        System.out.println("В игре есть торговеец. Он продает зелья и может покупать у вас барахло, которое выпадает с мобов.");
        System.out.println("Он очень хитрый, поэтому не раслабляемся. Топовое оружие с боссов он не покупает по этическим соображениям.");
        System.out.println("После зачистки каждой локации, можно убить босса локации, а это, соответственно, интересный лут и много золота.");
        System.out.println("Оружие с боссов можно экипировать, дает прибавку к статам.");
        System.out.println("Ну разберетесь короче. Удачной игры и да прибудет с вами некротическая сила!!!");
        System.out.println();

    }

    public void start() {
        try {
            mainMenu();
            System.out.println("Программа завершена.");
        } catch (IOException e) {
            System.err.println("Ошибка ввода: " + e.getMessage());
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии потока ввода: " + e.getMessage());
            }
        }
    }

    private Map<Integer, List<GameUnit>> createLocations() {
        Map<Integer, List<GameUnit>> locations = new HashMap<>();
        for (int x = 0; x < 5; x++) {
            List<GameUnit> location = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                SplittableRandom random = new SplittableRandom();
                boolean proc = random.nextInt(1, 101) < 20;
                SplittableRandom random1 = new SplittableRandom();
                int mob = random1.nextInt(1, 5);
                SplittableRandom random3 = new SplittableRandom();
                int level = random3.nextInt(x + 1, x + 3);
                switch (mob) {
                    case 1:
                        GameUnit goblin = new Goblin("Гоблин", level);
                        goblin.setInventory(new Staff(PriceList.BOW.getStaffNumber(), PriceList.BOW.getName(), PriceList.BOW.getPrice()), 1);
                        goblin.setInventory(new Staff(PriceList.EARS.getStaffNumber(), PriceList.EARS.getName(), PriceList.EARS.getPrice()), 2);
                        if (proc) {
                            goblin.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 1);
                        }
                        location.add(goblin);
                        break;
                    case 2:
                        GameUnit skeleton = new Skeleton("Скелет", level);
                        skeleton.setInventory(new Staff(PriceList.SWORD.getStaffNumber(), PriceList.SWORD.getName(), PriceList.SWORD.getPrice()), 1);
                        skeleton.setInventory(new Staff(PriceList.BONE.getStaffNumber(), PriceList.BONE.getName(), PriceList.BONE.getPrice()), 3);
                        if (proc) {
                            skeleton.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 1);
                        }
                        location.add(skeleton);
                        break;
                    case 3:
                        GameUnit zombie = new Golem("Голем", level);
                        zombie.setInventory(new Staff(PriceList.CLUB.getStaffNumber(), PriceList.CLUB.getName(), PriceList.CLUB.getPrice()), 1);
                        zombie.setInventory(new Staff(PriceList.SCULL.getStaffNumber(), PriceList.SCULL.getName(), PriceList.SCULL.getPrice()), 3);
                        if (proc) {
                            zombie.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 1);
                        }
                        location.add(zombie);
                        break;
                    case 4:
                        GameUnit robber = new Robber("Разбойник", level);
                        robber.setInventory(new Staff(PriceList.SPEAR.getStaffNumber(), PriceList.SPEAR.getName(), PriceList.SPEAR.getPrice()), 1);
                        robber.setInventory(new Staff(PriceList.MASTER_KEY.getStaffNumber(), PriceList.MASTER_KEY.getName(), PriceList.MASTER_KEY.getPrice()), 3);
                        if (proc) {
                            robber.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 1);
                        }
                        location.add(robber);
                        break;
                }
            }
            locations.put(x, location);
        }
        return locations;
    }

    private Map<Integer, List<GameUnit>> createBosses() {
        Map<Integer, List<GameUnit>> bosses = new HashMap<>();
        for (int x = 0; x < 5; x++) {
            List<GameUnit> boss = new ArrayList<>();

            SplittableRandom random = new SplittableRandom();
            boolean proc = random.nextInt(1, 101) < 20;

            SplittableRandom random3 = new SplittableRandom();
            int level = random3.nextInt(x + 14, x + 17);

            switch (x) {
                case 0:
                    GameUnit goblin = new Goblin("Шаман гоблинов (босс)", level);
                    Staff bow = new Staff(PriceList.BOW_BOSS.getStaffNumber(), PriceList.BOW_BOSS.getName(), PriceList.BOW_BOSS.getPrice());
                    bow.setLegendary(true);
                    goblin.setInventory(bow, 1);
                    goblin.setInventory(new Staff(PriceList.EARS.getStaffNumber(), PriceList.EARS.getName(), PriceList.EARS.getPrice()), 20);
                    goblin.setBoss(true);
                    goblin.setGold(1000);
                    if (proc) {
                        goblin.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 3);
                    }
                    boss.add(goblin);
                    break;
                case 2:
                    GameUnit skeleton = new Skeleton("Лич", level);
                    Staff sword = new Staff(PriceList.SWORD_BOSS.getStaffNumber(), PriceList.SWORD_BOSS.getName(), PriceList.SWORD_BOSS.getPrice());
                    sword.setLegendary(true);
                    skeleton.setInventory(sword, 1);
                    skeleton.setInventory(new Staff(PriceList.BONE.getStaffNumber(), PriceList.BONE.getName(), PriceList.BONE.getPrice()), 30);
                    skeleton.setBoss(true);
                    skeleton.setGold(1000);
                    if (proc) {
                        skeleton.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 3);
                    }
                    boss.add(skeleton);
                    break;
                case 3:
                    GameUnit golem = new Golem("Вождь големов", level);
                    Staff spear = new Staff(PriceList.SPEAR_BOSS.getStaffNumber(), PriceList.SPEAR_BOSS.getName(), PriceList.SPEAR_BOSS.getPrice());
                    spear.setLegendary(true);
                    golem.setInventory(spear, 1);
                    golem.setInventory(new Staff(PriceList.SCULL.getStaffNumber(), PriceList.SCULL.getName(), PriceList.SCULL.getPrice()), 30);
                    golem.setBoss(true);
                    golem.setGold(1000);
                    if (proc) {
                        golem.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 3);
                    }
                    boss.add(golem);
                    break;
                case 4:
                    GameUnit robber = new Robber("Атаман разбойников", level);
                    Staff club = new Staff(PriceList.CLUB_BOSS.getStaffNumber(), PriceList.CLUB_BOSS.getName(), PriceList.CLUB_BOSS.getPrice());
                    club.setLegendary(true);
                    robber.setInventory(club, 1);
                    robber.setInventory(new Staff(PriceList.MASTER_KEY.getStaffNumber(), PriceList.MASTER_KEY.getName(), PriceList.MASTER_KEY.getPrice()), 30);
                    robber.setBoss(true);
                    robber.setGold(1000);
                    if (proc) {
                        robber.setInventory(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), 3);
                    }
                    boss.add(robber);
                    break;
            }
            bosses.put(x, boss);
        }
        return bosses;
    }

    private Dealer createDealer() {
        Dealer dealer = new Dealer("Еврей Мося", 10000);
        Staff hpBottle = new Staff(PriceList.HP.getStaffNumber(), PriceList.HP.getName(), PriceList.HP.getPrice());
        Staff xpBottle = new Staff(PriceList.XP.getStaffNumber(), PriceList.XP.getName(), PriceList.XP.getPrice());
        Staff suicideBottle = new Staff(PriceList.SUICIDE.getStaffNumber(), PriceList.SUICIDE.getName(), PriceList.SUICIDE.getPrice());

        Staff hpBuffBottle = new Staff(PriceList.HP_BUFF.getStaffNumber(), PriceList.HP_BUFF.getName(), PriceList.HP_BUFF.getPrice());
        Staff strongBuffBottle = new Staff(PriceList.STRONG_BUFF.getStaffNumber(), PriceList.STRONG_BUFF.getName(), PriceList.STRONG_BUFF.getPrice());
        Staff dexBuffBottle = new Staff(PriceList.DEX_BUFF.getStaffNumber(), PriceList.DEX_BUFF.getName(), PriceList.DEX_BUFF.getPrice());
        Staff hpUpBottle = new Staff(PriceList.HP_UP.getStaffNumber(), PriceList.HP_UP.getName(), PriceList.HP_UP.getPrice());
        Staff strongUpBottle = new Staff(PriceList.STRONG_UP.getStaffNumber(), PriceList.STRONG_UP.getName(), PriceList.STRONG_UP.getPrice());
        Staff dexUpBottle = new Staff(PriceList.DEX_UP.getStaffNumber(), PriceList.DEX_UP.getName(), PriceList.DEX_UP.getPrice());

        dealer.addStaff(hpBottle, 1000);
        dealer.addStaff(xpBottle, 100);
        dealer.addStaff(suicideBottle, 1);
        dealer.addStaff(hpBuffBottle, 10);
        dealer.addStaff(strongBuffBottle, 10);
        dealer.addStaff(dexBuffBottle, 10);
        dealer.addStaff(hpUpBottle, 10);
        dealer.addStaff(strongUpBottle, 10);
        dealer.addStaff(dexUpBottle, 10);
        return dealer;
    }

    private void printAdvertising() {
        for (int i = 0; i < 100; i++) {
            if (i % 5 == 0) {
                System.out.println("РЕКЛАМА ОЧЕНЬ НУЖНОЙ ВАМ ФИГНИ!  ");
            } else {
                System.out.print("РЕКЛАМА ОЧЕНЬ НУЖНОЙ ВАМ ФИГНИ!  ");
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
    }

    private void printMenu() {
        System.out.println(Colors.ANSI_CYAN + "======= МЕНЮ =======" + Colors.ANSI_RESET);
        System.out.println("1. К торговцу");
        System.out.println("2. Выбор локации");
        System.out.println("3. Параметры героя");
        System.out.println("4. Инвентарь");
        System.out.println("5. Снаряжение");
        System.out.println("6. На выход");
    }

    private void mainMenu() throws IOException {
        printMenu();
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                tradingMenu();
                break;
            case "2":
                selectLocation();
                break;
            case "3":
                printStatistic();
                break;
            case "4":
                inventoryMenu();
                break;
            case "5":
                equipmentMenu();
                break;
            case "6":
                System.out.println("На выход");
                System.exit(1);
                break;
            default:
                System.out.println("Неверный ввод!");
                mainMenu();
        }
    }

    private void equipmentMenu() throws IOException {
        Set<Staff> set = hero.getInventory().keySet().stream().filter(Staff::isLegendary).collect(Collectors.toSet());

        if (set.isEmpty()) {
            System.out.println("Пока у тебя нет ничего, что можно надеть. Иди и убей босса!\n");
            mainMenu();

        } else {
            System.out.println("Можно надеть несколько вещей сразу. ");
            System.out.println("Введите номер оружия или несколько номеров через тире (-), например 21-22-23");
            System.out.println("Вводите внимательно, проверять не буду. Как введете так и бафнитесь))\n");
            set.forEach(it -> System.out.printf(" - %s. %s\n", it.getId(), it.getName()));
            System.out.println("Выйти из меню снаряжения нажмите - 1");
            String choice = bufferedReader.readLine();
            if (choice.contains("-")) {
                String[] ss = choice.split("-");
                ArrayList<String> ll = (ArrayList<String>) Arrays.asList(ss);
                if (ll.contains("20")) {
                    hero.setDexterity(hero.getDexterity() + 10);
                    System.out.println("Ловкость увеличена на +10\n");

                }
                if (ll.contains("21")) {
                    hero.setStrength(hero.getStrength() + 10);
                    System.out.println("Сила увеличена на +10\n");

                }
                if (ll.contains("22")) {
                    hero.setHealth(hero.getHealth() + 20);
                    System.out.println("Здоровье увеличена на +20\n");

                }
                if (ll.contains("23")) {
                    hero.setCritChance(10);
                    System.out.println("Шанс критической атаки увеличен на 10%\n");
                }
            } else if (!choice.isEmpty()) {
                switch (choice) {
                    case "20":
                        hero.setDexterity(hero.getDexterity() + 10);
                        System.out.println("Ловкость увеличена на +10");
                        equipmentMenu();
                        break;
                    case "21":
                        hero.setStrength(hero.getStrength() + 10);
                        System.out.println("Сила увеличена на +10");
                        equipmentMenu();
                        break;
                    case "22":
                        hero.setHealth(hero.getHealth() + 20);
                        System.out.println("Здоровье увеличена на +20");
                        equipmentMenu();
                        break;
                    case "23":
                        hero.setCritChance(10);
                        System.out.println("Шанс критической атаки увеличен на 10%");
                        equipmentMenu();
                        break;
                    case "1":
                        mainMenu();
                        break;
                    default:
                        System.out.println("Неверный ввод!");
                        equipmentMenu();
                }
            } else {
                System.out.println("Неверный ввод!");
                equipmentMenu();
            }
        }
    }


    private void inventoryMenu() throws IOException {
        System.out.println();
        System.out.println(Colors.ANSI_YELLOW + "Мои сокровища ....." + Colors.ANSI_RESET);
        hero.showInventory();
        System.out.println();
        System.out.println("1. Все выкинуть!");
        System.out.println("2. Выпить зелье.");
        System.out.println("3. Закрыть инвентарь.");
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                hero.clearInventory();
                hero.setGold(0);
                System.out.println("Теперь у тебя как у латыша ...");
                System.out.println("Если нажал по ошибке, значит не повезло) меню подтверждения делать лень.");
                inventoryMenu();
                break;
            case "2":
                drinkMenu();
                break;
            case "3":
                mainMenu();
                break;
            default:
                System.out.println("Неверный ввод!");
                inventoryMenu();
        }
    }

    private void drinkMenu() throws IOException {
        System.out.println("Чтобы закинуться зельем введите его номер в инвентаре и через тире (-) количество");
        System.out.println("Вернутся назад - 1");
        String choice = bufferedReader.readLine();
        if (choice.equals("1")) {
            inventoryMenu();
        } else {
            String[] s = choice.split("-");
            try {
                String staffName = s[0];
                System.out.println(staffName);
                int value = Integer.parseInt(s[1]);
                System.out.println(value);
                if (!staffName.isEmpty() && value != 0) {
                    switch (staffName) {
                        case "1":
                            if (hero.hasStaff(new Staff(PriceList.HP.getStaffNumber(), PriceList.HP.getName(), PriceList.HP.getPrice()), value)) {
                                int normalHealth = 120 + (hero.getLevel() - 1) * 5;
                                int currentHealth = hero.getHealth();
                                int afterHealth = currentHealth + (100 * value);
                                if (normalHealth == currentHealth) {
                                    System.out.println("Больше в тебя не влезет!");
                                } else {
                                    hero.setHealth(Math.min(afterHealth, normalHealth));
                                    System.out.println(Colors.ANSI_GREEN + "Ты выпил лечилку" + Colors.ANSI_RESET);
                                    System.out.printf("Теперь твое здоровье - %d\n", hero.getHealth());
                                    hero.removeStaff(new Staff(PriceList.HP.getStaffNumber(), PriceList.HP.getName(), PriceList.HP.getPrice()), value);
                                }
                            } else {
                                System.out.println("Чего-то не хватает...");
                            }
                            inventoryMenu();
                        case "2":
                            if (hero.hasStaff(new Staff(PriceList.XP.getStaffNumber(), PriceList.XP.getName(), PriceList.XP.getPrice()), value)) {
                                int currentExp = hero.getExp();
                                int afterExp = currentExp + (1000 * value);
                                hero.levelUp(afterExp);
                                System.out.printf(Colors.ANSI_GREEN + "Ты выпил %d ед. зелья повышающего опыт на 1000 ед.\n" + Colors.ANSI_RESET, value);
                                System.out.printf("Твой текцщий опыт - %d\n", hero.getExp());
                                System.out.printf("Твой текщий уровень - %d\n", hero.getLevel());
                                hero.removeStaff(new Staff(PriceList.XP.getStaffNumber(), PriceList.XP.getName(), PriceList.XP.getPrice()), value);
                            } else {
                                System.out.println("В следующий раз, иди качайся!!!");
                            }
                            inventoryMenu();
                            break;
                        case "3":
                            if (hero.hasStaff(new Staff(PriceList.SUICIDE.getStaffNumber(), PriceList.SUICIDE.getName(), PriceList.SUICIDE.getPrice()), value)) {
                                System.out.println("Мы крайне осуждаем поступок героя! хотя нет - нам пофиг...");
                                Thread.sleep(3000);
                                System.exit(1);
                            } else {
                                System.out.println("В следующий раз! Иди воюй!!!");
                            }
                            break;
                        case "4":
                            if (hero.hasStaff(new Staff(PriceList.STRONG_BUFF.getStaffNumber(), PriceList.STRONG_BUFF.getName(), PriceList.STRONG_BUFF.getPrice()), value)) {
                                hero.setStrongBuffed(true);
                                System.out.println(Colors.ANSI_GREEN + "Выпито зелье на +10 к силе. Подействует только в локации." + Colors.ANSI_RESET);
                                hero.removeStaff(new Staff(PriceList.STRONG_BUFF.getStaffNumber(), PriceList.STRONG_BUFF.getName(), PriceList.STRONG_BUFF.getPrice()), value);
                            } else {
                                System.out.println("Сначала купи, потом пей");
                            }
                            inventoryMenu();
                            break;
                        case "5":
                            if (hero.hasStaff(new Staff(PriceList.DEX_BUFF.getStaffNumber(), PriceList.DEX_BUFF.getName(), PriceList.DEX_BUFF.getPrice()), value)) {
                                hero.setDexBuffed(true);
                                System.out.println(Colors.ANSI_GREEN + "Выпито зелье на +10 к ловкости. Подействует только в локации." + Colors.ANSI_RESET);
                                hero.removeStaff(new Staff(PriceList.DEX_BUFF.getStaffNumber(), PriceList.DEX_BUFF.getName(), PriceList.DEX_BUFF.getPrice()), value);
                            } else {
                                System.out.println("Сначала купи, потом пей");
                            }
                            inventoryMenu();
                            break;
                        case "6":
                            if (hero.hasStaff(new Staff(PriceList.HP_BUFF.getStaffNumber(), PriceList.HP_BUFF.getName(), PriceList.HP_BUFF.getPrice()), value)) {
                                hero.setHPBuffed(true);
                                System.out.println(Colors.ANSI_GREEN + "Выпито зелье на +30 к HP. Подействует только в локации." + Colors.ANSI_RESET);
                                hero.removeStaff(new Staff(PriceList.HP_BUFF.getStaffNumber(), PriceList.HP_BUFF.getName(), PriceList.HP_BUFF.getPrice()), value);
                            } else {
                                System.out.println("Сначала купи, потом пей");
                            }
                            inventoryMenu();
                            break;
                        case "7":
                            if (hero.hasStaff(new Staff(PriceList.STRONG_UP.getStaffNumber(), PriceList.STRONG_UP.getName(), PriceList.STRONG_UP.getPrice()), value)) {
                                int currentStrong = hero.getStrength();
                                hero.setStrength(currentStrong + 5);
                                System.out.printf(Colors.ANSI_GREEN + "Выпито зелье на +5 к силе. Ваш параметр сила теперь - %d\n" + Colors.ANSI_RESET, hero.getStrength());
                                hero.removeStaff(new Staff(PriceList.STRONG_UP.getStaffNumber(), PriceList.STRONG_UP.getName(), PriceList.STRONG_UP.getPrice()), value);
                            } else {
                                System.out.println("Сначала купи, потом пей");
                            }
                            inventoryMenu();
                            break;
                        case "8":
                            if (hero.hasStaff(new Staff(PriceList.DEX_UP.getStaffNumber(), PriceList.DEX_UP.getName(), PriceList.DEX_UP.getPrice()), value)) {
                                int currentDex = hero.getDexterity();
                                hero.setDexterity(currentDex + 5);
                                System.out.printf(Colors.ANSI_GREEN + "Выпито зелье на +5 к ловкости. Ваш параметр ловкость теперь - %d\n" + Colors.ANSI_RESET, hero.getDexterity());
                                hero.removeStaff(new Staff(PriceList.DEX_UP.getStaffNumber(), PriceList.DEX_UP.getName(), PriceList.DEX_UP.getPrice()), value);
                            } else {
                                System.out.println("Сначала купи, потом пей");
                            }
                            inventoryMenu();
                            break;
                        case "9":
                            if (hero.hasStaff(new Staff(PriceList.HP_UP.getStaffNumber(), PriceList.HP_UP.getName(), PriceList.HP_UP.getPrice()), value)) {
                                int currentHP = hero.getHealth();
                                hero.setDexterity(currentHP + 15);
                                System.out.printf(Colors.ANSI_GREEN + "Выпито зелье на +15 к количеству хитпоинтов. Ваш параметр здоровье теперь - %d\n" + Colors.ANSI_RESET, hero.getHealth());
                            } else {
                                System.out.println("Сначала купи, потом пей");
                            }
                            inventoryMenu();
                            break;
                        default:
                            System.out.println("Неверный ввод!");
                            bayMenu();
                    }
                } else {
                    System.out.println("Не правлильный ввод");
                    drinkMenu();
                }
            } catch (Exception e) {
                System.out.println("Не правлильный ввод");
                drinkMenu();
            }
        }
    }

    private void tradingMenu() throws IOException {
        System.out.println();
        System.out.println(Colors.ANSI_CYAN + "Добро пожаловать в лавку еврея Моси!" + Colors.ANSI_RESET);
        System.out.println("Мы русские, друг друга не обманываем!");
        System.out.println("1. Купить");
        System.out.println("2. Продать");
        System.out.println("3. Уйти");
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                bayMenu();
                break;
            case "2":
                sellMenu();
                break;
            case "3":
                mainMenu();
                break;
            default:
                System.out.println("Неверный ввод!");
                tradingMenu();
        }
    }


    private void bayMenu() throws IOException {
        dealer.showInventory();
        System.out.println("Чтобы положить товар в свою корзину введите номер товара и через тире количество (например 1-2):");
        System.out.println("Вернутся в меню торговли нажмите - 1");
        String choice = bufferedReader.readLine();
        if (choice.equals("1")) {
            tradingMenu();
        } else {
            try {
                if (choice.contains("-")) {
                    String[] s = choice.split("-");
                    String staffNumber = s[0];
                    int value = Integer.parseInt(s[1]);
                    if (!staffNumber.isEmpty() && value != 0) {
                        switch (staffNumber) {
                            case "1":
                                oneBayTransaction(new Staff(PriceList.HP.getStaffNumber(), PriceList.HP.getName(), PriceList.HP.getPrice()), value);
                                break;
                            case "2":
                                oneBayTransaction(new Staff(PriceList.XP.getStaffNumber(), PriceList.XP.getName(), PriceList.XP.getPrice()), value);
                                break;
                            case "3":
                                oneBayTransaction(new Staff(PriceList.SUICIDE.getStaffNumber(), PriceList.SUICIDE.getName(), PriceList.SUICIDE.getPrice()), value);
                                break;
                            case "4":
                                oneBayTransaction(new Staff(PriceList.STRONG_BUFF.getStaffNumber(), PriceList.STRONG_BUFF.getName(), PriceList.STRONG_BUFF.getPrice()), value);
                                break;
                            case "5":
                                oneBayTransaction(new Staff(PriceList.DEX_BUFF.getStaffNumber(), PriceList.DEX_BUFF.getName(), PriceList.DEX_BUFF.getPrice()), value);
                                break;
                            case "6":
                                oneBayTransaction(new Staff(PriceList.HP_BUFF.getStaffNumber(), PriceList.HP_BUFF.getName(), PriceList.HP_BUFF.getPrice()), value);
                                break;
                            case "7":
                                oneBayTransaction(new Staff(PriceList.STRONG_UP.getStaffNumber(), PriceList.STRONG_UP.getName(), PriceList.STRONG_UP.getPrice()), value);
                                break;
                            case "8":
                                oneBayTransaction(new Staff(PriceList.DEX_UP.getStaffNumber(), PriceList.DEX_UP.getName(), PriceList.DEX_UP.getPrice()), value);
                                break;
                            case "9":
                                oneBayTransaction(new Staff(PriceList.HP_UP.getStaffNumber(), PriceList.HP_UP.getName(), PriceList.HP_UP.getPrice()), value);
                                break;
                            default:
                                System.out.println("Неверный ввод!");
                                bayMenu();
                        }
                    } else {
                        System.out.println("Не правлильный ввод");
                        bayMenu();
                    }
                } else {
                    System.out.println("Не правлильный ввод");
                    bayMenu();
                }
            } catch (Exception e) {
                System.out.println("Не правлильный ввод");
                bayMenu();
            }
        }
    }

    private void sellMenu() throws IOException {
        System.out.println("Привет работникам торговли! Смотри что у меня есть, нада?\n");
        hero.showInventory();
        System.out.println();
        System.out.println("Чтобы продать свои пожитки торговцу введите номер товара и через трие количество (например 2-1) :");
        System.out.println("Вернутся в меню торговли нажмите - 1");
        String choice = bufferedReader.readLine();
        if (choice.equals("1")) {
            tradingMenu();
        } else {
            try {
                if ((choice.chars().filter(c -> c == '-').count()) == 1) {
                    String[] s = choice.split("-");
                    String staffNumber = s[0];
                    int value = Integer.parseInt(s[1]);
                    switch (staffNumber) {
                        case "1":
                            oneSellTransaction(new Staff(PriceList.HP.getStaffNumber(), PriceList.HP.getName(), PriceList.HP.getPrice()), value);
                            break;
                        case "2":
                            oneSellTransaction(new Staff(PriceList.XP.getStaffNumber(), PriceList.XP.getName(), PriceList.XP.getPrice()), value);
                            break;
                        case "3":
                            oneSellTransaction(new Staff(PriceList.SUICIDE.getStaffNumber(), PriceList.SUICIDE.getName(), PriceList.SUICIDE.getPrice()), value);
                            break;
                        case "4":
                            oneSellTransaction(new Staff(PriceList.STRONG_BUFF.getStaffNumber(), PriceList.STRONG_BUFF.getName(), PriceList.STRONG_BUFF.getPrice()), value);
                            break;
                        case "5":
                            oneSellTransaction(new Staff(PriceList.DEX_BUFF.getStaffNumber(), PriceList.DEX_BUFF.getName(), PriceList.DEX_BUFF.getPrice()), value);
                            break;
                        case "6":
                            oneSellTransaction(new Staff(PriceList.HP_BUFF.getStaffNumber(), PriceList.HP_BUFF.getName(), PriceList.HP_BUFF.getPrice()), value);
                            break;
                        case "7":
                            oneSellTransaction(new Staff(PriceList.STRONG_UP.getStaffNumber(), PriceList.STRONG_UP.getName(), PriceList.STRONG_UP.getPrice()), value);
                            break;
                        case "8":
                            oneSellTransaction(new Staff(PriceList.DEX_UP.getStaffNumber(), PriceList.DEX_UP.getName(), PriceList.DEX_UP.getPrice()), value);
                            break;
                        case "9":
                            oneSellTransaction(new Staff(PriceList.HP_UP.getStaffNumber(), PriceList.HP_UP.getName(), PriceList.HP_UP.getPrice()), value);
                            break;
                        case "10":
                            oneSellTransaction(new Staff(PriceList.BOW.getStaffNumber(), PriceList.BOW.getName(), PriceList.BOW.getPrice()), value);
                            break;
                        case "11":
                            oneSellTransaction(new Staff(PriceList.EARS.getStaffNumber(), PriceList.EARS.getName(), PriceList.EARS.getPrice()), value);
                            break;
                        case "12":
                            oneSellTransaction(new Staff(PriceList.SWORD.getStaffNumber(), PriceList.SWORD.getName(), PriceList.SWORD.getPrice()), value);
                            break;
                        case "13":
                            oneSellTransaction(new Staff(PriceList.BONE.getStaffNumber(), PriceList.BONE.getName(), PriceList.BONE.getPrice()), value);
                            break;
                        case "14":
                            oneSellTransaction(new Staff(PriceList.SPEAR.getStaffNumber(), PriceList.SPEAR.getName(), PriceList.SPEAR.getPrice()), value);
                            break;
                        case "15":
                            oneSellTransaction(new Staff(PriceList.SCULL.getStaffNumber(), PriceList.SCULL.getName(), PriceList.SCULL.getPrice()), value);
                            break;
                        case "16":
                            oneSellTransaction(new Staff(PriceList.CLUB.getStaffNumber(), PriceList.CLUB.getName(), PriceList.CLUB.getPrice()), value);
                            break;
                        case "17":
                            oneSellTransaction(new Staff(PriceList.MASTER_KEY.getStaffNumber(), PriceList.MASTER_KEY.getName(), PriceList.MASTER_KEY.getPrice()), value);
                            break;
                        case "18":
                            oneSellTransaction(new Staff(PriceList.ARTIFACT.getStaffNumber(), PriceList.ARTIFACT.getName(), PriceList.ARTIFACT.getPrice()), value);
                            break;
                        default:
                            System.out.println("Неверный ввод!");
                            sellMenu();
                    }
                } else {
                    System.out.println("Не правлильный ввод2");
                    sellMenu();
                }
            } catch (Exception e) {
                System.out.println("Не правлильный ввод3");
                sellMenu();
            }
        }
    }

    private void confirmBayMenu() throws IOException {
        System.out.println();
        System.out.println("Еще чего будешь покупать или оформляем?");
        System.out.println("1. Показывай свое барахло, буду выбирать.");
        System.out.println("2. Сделка!");
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                bayMenu();
                break;
            case "2":
                hero.buyFrom(dealer);
                hero.showInventory();
                tradingMenu();
                break;
            default:
                System.out.println("Неверный ввод!");
                tradingMenu();
        }
    }

    private void confirmSellMenu() throws IOException {
        System.out.println();
        System.out.println("Добавить свои пожитки в корзину к торговцу?");
        System.out.println("1. Есть еще что продать");
        System.out.println("2. Сделка!\n");
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                sellMenu();
                break;
            case "2":
                dealer.buyFrom(hero);
                hero.showInventory();
                System.out.println();
                dealer.showInventory();
                tradingMenu();
                break;
            default:
                System.out.println("Неверный ввод!");
                tradingMenu();
        }
    }

    private void oneBayTransaction(Staff staff, int value) throws IOException {
        hero.addToCart(staff, value);
        System.out.println();
        hero.viewCart();
        System.out.println();
        confirmBayMenu();
    }

    private void oneSellTransaction(Staff staff, int value) throws IOException {
        dealer.addToCart(staff, value);
        System.out.println();
        dealer.viewCart();
        System.out.println();
        confirmSellMenu();
    }

    private void printStatistic() throws IOException {
        System.out.println();
        System.out.printf(Colors.ANSI_CYAN + "Параметры героя %s:\n" + Colors.ANSI_RESET, hero.getName());
        System.out.println("  Класс - Некромант");
        System.out.println("  Уровень - " + hero.getLevel());
        System.out.println("  Опыт - " + hero.getExp());
        System.out.printf("  Здоровье - %d (норма %d)", hero.getHealth(), 120 + (hero.getLevel() - 1) * 5);
        System.out.println("  Сила - " + hero.getStrength());
        System.out.println("  Ловкость - " + hero.getDexterity());
        System.out.println("  Мана - " + hero.getMana());
        hero.viewArmy();
        System.out.println();
        mainMenu();
    }

    private void printLocation(List<GameUnit> location) {
        System.out.println(Colors.ANSI_CYAN + "Количество монстров в локации - " + Colors.ANSI_RESET + location.size());
        for (GameUnit unit : location) {
            System.out.println(unit.getName() + " " + unit.getLevel() + " ур. " + unit.getHealth() + "xp");
        }
    }

    private void selectLocation() throws IOException {
        System.out.println(Colors.ANSI_CYAN + "Выберити номер локации, куда пойти бить морды." + Colors.ANSI_RESET);
        System.out.println("1. Темный лес (1 - 2 ур.)");
        System.out.println("2. Мрачная пещера (2 - 3 ур.)");
        System.out.println("3. Старый замок (3 - 4 ур.)");
        System.out.println("4. Заброшенная деревня (4 - 5 ур.)");
        System.out.println("5. Мертвый пляж (5 - 6 ур.)");
        System.out.println("6. В главное меню");
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                checkLocation(0);
                break;
            case "2":
                checkLocation(1);
                break;
            case "3":
                checkLocation(2);
                break;
            case "4":
                checkLocation(3);
                break;
            case "5":
                checkLocation(4);
                break;
            case "6":
                mainMenu();
                break;
            default:
                System.out.println("Неверный ввод!");
                selectLocation();
        }
    }


    private void checkLocation(int locationNumber) throws IOException {
        checkHealth(locationNumber, locations.get(locationNumber).isEmpty());
    }

    private void checkHealth(int locationNumber, boolean isBossFighting) throws IOException {
        if (hero.getHealth() < (120 + (hero.getLevel() - 1) * 5)) {
            System.out.println(Colors.ANSI_CYAN + "Ты собрался в бой не поправив здоровье!!!" + Colors.ANSI_RESET);
            System.out.println("1. Да, надо закупится и подлечится...");
            System.out.println("2. Посмотреть что есть в инвентаре.");
            System.out.println("3. Нет, я отмороженный!!!");
            String choice = bufferedReader.readLine();
            switch (choice) {
                case "1":
                    tradingMenu();
                    break;
                case "2":
                    inventoryMenu();
                    break;
                case "3":
                    if (isBossFighting) {
                        confirmBossMenu(locationNumber);
                    } else {
                        mochilovo(locationNumber, false);
                    }
                    break;
                default:
                    System.out.println("Неверный ввод!");
                    checkLocation(locationNumber);
            }
        } else {
            if (isBossFighting) {
                confirmBossMenu(locationNumber);
            } else {
                mochilovo(locationNumber, false);
            }
        }
    }

    private void confirmBossMenu(int locationNumber) throws IOException {
        if (bosses.get(locationNumber).isEmpty()) {
            System.out.println(Colors.ANSI_CYAN + "В этой локации ты всех убил, даже босса. Пора идти в другую." + Colors.ANSI_RESET);
            mainMenu();
        } else {
            System.out.println(Colors.ANSI_CYAN + "В этой локации ты всех убил..." + Colors.ANSI_RESET);
            System.out.println("Но это не беда, тут есть босс!!! Какой именно узнаешь, если хватит смелости зайти.");
            System.out.println("1. В бой! Где наша не пропадала!");
            System.out.println("2. Убежать в страхе. Проживу и без топового оружия...");
            String choice = bufferedReader.readLine();
            switch (choice) {
                case "1":
                    mochilovo(locationNumber, true);
                    break;
                case "2":
                    System.out.println("Ты сделал правильный выбор. Слабакам туда не надо...");
                    mainMenu();
                    break;
                default:
                    System.out.println("Неверный ввод!");
                    confirmBossMenu(locationNumber);
            }
        }
    }

    private void mochilovo(int locationNumber, boolean isBossFighting) {
        System.out.println();
        System.out.printf(Colors.ANSI_CYAN + "Ты попал в локацию %s, сражайся или умри!\n\n" + Colors.ANSI_RESET, numOfLocations.get(locationNumber));
        Battle battle = new Battle();
        List<GameUnit> mobList = isBossFighting ? bosses.get(locationNumber) : locations.get(locationNumber);
        battle.start(hero, mobList, bufferedReader, new Chanel() {
            @Override
            public void win(GameUnit winner, GameUnit looser, List<GameUnit> location) throws IOException {

                if (winner instanceof Hero) {
                    System.out.println();
                    System.out.println(Colors.ANSI_GREEN + "Поздравляем, вы попедили " + looser.getName() + "а !!!" + Colors.ANSI_RESET);
                    System.out.println();
                    System.out.println(Colors.ANSI_CYAN + "Получен опыт - " + Colors.ANSI_RESET + looser.getLevel() * 50);
                    System.out.println(Colors.ANSI_CYAN + "Получено золото - " + Colors.ANSI_RESET + looser.getGold() + " шт.");
                    System.out.println(Colors.ANSI_CYAN + "Собран лут: " + Colors.ANSI_RESET);
                    looser.getInventory().forEach((s, q) -> System.out.printf(" - %s - %d шт.\n", s.getName(), q));
                    System.out.println();
                    if (looser.isBoss()) {
                        System.out.println("Это можно надеть!");
                        System.out.print(" - ");
                        hero.getInventory().keySet().stream().filter(Staff::isLegendary).forEach(it -> System.out.println(" - " + it));
                        System.out.println();
                    }
                    printLocation(location);
                    System.out.println();
                    if (hero.getMana() >= 15) {
                        summonMenu(looser);
                    }
                    mainMenu();
                } else if (winner.isPawn()) {
                    System.out.println();
                    System.out.println(Colors.ANSI_GREEN + "Поздравляем, ваше умертвие (" + winner.getName() + ") убило " + looser.getName() + "а!!!" + Colors.ANSI_RESET);
                    System.out.println();
                    System.out.println(Colors.ANSI_CYAN + "Умертвием получен опыт - " + Colors.ANSI_RESET + looser.getLevel() * 50);
                    System.out.println(Colors.ANSI_CYAN + "Получено золото - " + Colors.ANSI_RESET + looser.getGold() + " шт.");
                    System.out.println(Colors.ANSI_CYAN + "Собран лут: " + Colors.ANSI_RESET);
                    looser.getInventory().forEach((s, q) -> System.out.printf(" - %s - %d шт.\n", s.getName(), q));
                    System.out.println();
                    if (looser.isBoss()) {
                        System.out.println("Это можно надеть!");
                        System.out.print(" - ");
                        hero.getInventory().keySet().stream().filter(Staff::isLegendary).forEach(it -> System.out.println(" - " + it));
                        System.out.println();
                    }
                    System.out.println();
                    printLocation(location);
                    System.out.println();
                    mainMenu();
                }
            }

            @Override
            public void lose(GameUnit winner, GameUnit looser, List<GameUnit> location, Staff dropedStaff) throws IOException {
                if (looser instanceof Hero) {
                    System.out.println();
                    System.out.println(Colors.ANSI_RED + "Вас убил жалкий " + winner.getName() + "!!!" + Colors.ANSI_RESET);
                    if (dropedStaff != null) {
                        System.out.println("Потеряно - " + dropedStaff);
                    }
                    System.out.println("Осталось золота - " + hero.getGold());
                    System.out.println("У торговца есть интересные зелья...\n");
                    printLocation(location);
                    System.out.println();
                    mainMenu();
                } else if (looser.isPawn()) {
                    hero.removePawnFromArmy(looser);
                }
            }

            @Override
            public void escape() {
                System.out.println("Вы позорно убежали... или совершили тактическое отступление...");
                System.out.println();
                try {
                    selectLocation();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void summonMenu(GameUnit looser) throws IOException {
        System.out.println(Colors.ANSI_CYAN + "У тебя достаточно манны чтобы поднять умертвие, некромант ты или где?" + Colors.ANSI_RESET);
        System.out.printf("1. Поднять умертвие из %s %d уровня. Стоимость 15 ед. маны\n", looser.getName(), looser.getLevel());
        System.out.println("2. Нет, мне лень.");
        String choice = bufferedReader.readLine();
        switch (choice) {
            case "1":
                System.out.println("Введите имя своего умертвия:");
                String pawnName = bufferedReader.readLine();
                if (!pawnName.isEmpty()) {
                    GameUnit zombie = new Zombie(pawnName, looser.getLevel(), true);
                    boolean t = hero.addPawnToArmy(zombie);
                    hero.setMana(0);
                    if (!t) {
                        mainMenu();
                    } else {
                        System.out.printf("Зомби %s %d уровня пополнил твою маленькую армию!\n\n", zombie.getName(), zombie.getLevel());
                        mainMenu();
                    }
                } else {
                    System.out.println("Хотя бы одну букву...");
                    summonMenu(looser);
                }
                break;
            case "2":
                mainMenu();
                break;
            default:
                System.out.println("Неверный ввод!");
                summonMenu(looser);
        }
    }
}
