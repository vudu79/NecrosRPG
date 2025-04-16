import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class Battle {
    private final AtomicInteger step = new AtomicInteger(1);
    private final AtomicBoolean isFighting = new AtomicBoolean(true);

    private String hitSize(GameUnit fighter, int hit) {
        String hitSize = "";
        if (hit == 0) {
            hitSize = Colors.ANSI_WHITE + "ПРОМАХ!" + Colors.ANSI_RESET;
        } else if (hit == fighter.getStrength()) {
            hitSize = Colors.ANSI_GREEN + "НАНОСИТ УРОН " + hit + Colors.ANSI_RESET;
        } else if (hit > fighter.getStrength())
            hitSize = Colors.ANSI_YELLOW + "НАНОСИТ КРИТИЧЕСКИЙ УРОН " + hit + Colors.ANSI_RESET;
        return hitSize;
    }

    private boolean hitResult(GameUnit fighter1, GameUnit fighter2, int step) {
        if (!fighter1.isDead() && !fighter2.isDead() && fighter1.getHealth()> 0 && fighter2.getHealth()> 0 ) {
            int hit = fighter1.attack();
            String flash1 = (fighter1 instanceof Hero || fighter1.isPawn()) ? Colors.ANSI_GREEN : Colors.ANSI_RED;
            String flash2 = (fighter2 instanceof Hero || fighter2.isPawn()) ? Colors.ANSI_GREEN : Colors.ANSI_RED;
            String reset = Colors.ANSI_RESET;
            String isPawn1 = fighter1.isPawn() ? " (умертвие) " : " ";
            String isPawn2 = fighter2.isPawn() ? " (умертвие) " : " ";

            System.out.println("Ход " + step + ": " +
                    flash1 + fighter1.getName() + isPawn1 + fighter1.getLevel() + "ур. " + reset + "(xp-" + fighter1.getHealth() + ")" +
                    " атакует " +
                    flash2 + fighter2.getName() + isPawn2 + fighter2.getLevel() + "ур. " + reset + "(xp-" + fighter2.getHealth() + ") " +
                    hitSize(fighter1, hit));

            fighter2.setHealth(fighter2.getHealth() - hit);
        }
        return fighter2.getHealth() <= 0;
    }

    void fight(int whoFirst, Hero hero, GameUnit monster, List<GameUnit> location, ExecutorService executorService, Chanel chanel) throws IOException {
        Runnable runnable = () -> {
            boolean eee;
            while (isFighting.get()) {
                if (whoFirst == 1) {
                    eee = step.get() % 2 == 0;
                } else {
                    eee = step.get() % 2 > 0;
                }
                if (eee) {
                    if (hitResult(hero, monster, step.get())) {
                        hero.setExp(hero.getExp() + monster.getLevel() * 50);
                        hero.levelUp(hero.getExp());
                        hero.setGold(hero.getGold() + monster.getGold());
                        hero.getArmy().forEach(it -> it.setHealth(70 + (it.getLevel() - 1) * 5));
                        for (Map.Entry<Staff, Integer> entry : monster.getInventory().entrySet()) {
                            Staff s = entry.getKey();
                            Integer q = entry.getValue();
                            hero.addStaff(s, q);
                        }
                        monster.setDead(true);
                        location.remove(monster);
                        if (hero.isStrongBuffed()) {
                            hero.setStrength(hero.getStrength() - 10);
                            hero.setStrongBuffed(false);
                        }
                        if (hero.isDexBuffed()) {
                            hero.setDexterity(hero.getDexterity() - 10);
                            hero.setDexBuffed(false);
                        }
                        if (hero.isHPBuffed()) {
                            hero.setHealth(hero.getHealth() - 30);
                            hero.setHPBuffed(false);
                        }
                        isFighting.set(false);
                        try {
                            chanel.win(hero, monster, location);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                } else {
                    if (hitResult(monster, hero, step.get())) {
                        monster.setExp(monster.getExp() + hero.getLevel() * 50);
                        monster.levelUp(monster.getExp());
                        monster.setGold(monster.getGold() + hero.getGold());
                        hero.getArmy().forEach(it -> it.setHealth(70 + (it.getLevel() - 1) * 5));
                        hero.setHealth(10);
                        Staff dropedStaff = hero.dropInventory();
                        isFighting.set(false);
                        try {
                            chanel.lose(monster, hero, location, dropedStaff);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }
                step.incrementAndGet();
                try {
                    Thread.sleep(490 + new SplittableRandom().nextInt(0 , 10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        };
        executorService.execute(runnable);

        if (!hero.getArmy().isEmpty()) {
            hero.getArmy().forEach((pawn) -> executorService.execute(() -> {
                boolean eee;

                    while (isFighting.get()) {
                        if (whoFirst == 1) {
                            eee = step.get() % 2 == 0;
                        } else {
                            eee = step.get() % 2 > 0;
                        }

                        SplittableRandom random = new SplittableRandom();
                        boolean chance = random.nextBoolean();
                        if (chance) {
                            if (eee) {
                                if (hitResult(pawn, monster, step.get())) {
                                    pawn.setExp(pawn.getExp() + monster.getLevel() * 50);
                                    pawn.levelUp(pawn.getExp());
                                    for (Map.Entry<Staff, Integer> entry : monster.getInventory().entrySet()) {
                                        Staff s = entry.getKey();
                                        Integer q = entry.getValue();
                                        hero.addStaff(s, q);
                                    }
                                    monster.setDead(true);
                                    location.remove(monster);
                                    try {
                                        isFighting.set(false);
                                        chanel.win(pawn, monster, location);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return;
                                }
                            } else {
                                if (hitResult(monster, pawn, step.get())) {
                                    monster.setExp(monster.getExp() + pawn.getLevel() * 100);
                                    monster.levelUp(monster.getExp());
                                    pawn.setHealth(70 + (pawn.getLevel() - 1) * 5);
                                    System.out.println(Colors.ANSI_RED + "Умертвие (" + pawn.getName() + "), пало в бою!" + Colors.ANSI_RESET);
                                try {
                                    chanel.lose(monster, pawn, location, null);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                return;
                                }
                            }
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

            }));
        }

        try {
            boolean b = executorService.awaitTermination(600, TimeUnit.SECONDS);
            if (b) System.out.println("Что то где то зависло ...");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(Hero hero, List<GameUnit> mobList, BufferedReader bufferedReader, Chanel chanel) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        if (hero.isStrongBuffed()) {
            hero.setStrength(hero.getStrength() + 10);
        }
        if (hero.isDexBuffed()) {
            hero.setDexterity(hero.getDexterity() + 10);
        }
        if (hero.isHPBuffed()) {
            hero.setHealth(hero.getHealth() + 30);
        }

        int randomIndex = mobList.size() == 1 ? 0 : new SplittableRandom().nextInt(0, mobList.size());
        GameUnit monster = mobList.get(randomIndex);

        int whoFirst = new SplittableRandom().nextInt(0, 2);
        if (whoFirst == 0) {
            boolean isChecked = true;
            System.out.println(Colors.ANSI_GREEN + "Обнаружен враг - " + monster.getName() + " (уровень " + monster.getLevel() + ")" + Colors.ANSI_RESET);
            System.out.println("Вы можете атаковать первым.");
            System.out.println("1. В бой!");
            System.out.println("2. Пожалуй, потом как-нибудь....");
            String choice;
            while (isChecked) {
                try {
                    choice = bufferedReader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                switch (choice) {
                    case "1":
                        try {
                            fight(whoFirst, hero, monster, mobList, executorService, chanel);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        isChecked = false;
                        break;
                    case "2":
                        chanel.escape();
                        isChecked = false;
                        break;
                    default:
                        System.out.println("Неверный ввод! Введите 1 или 2. Что тут сложного!");
                        break;
                }
            }
        } else {
            boolean isChecked = true;
            boolean randomEscape = new SplittableRandom().nextInt(0, 101) < 50;
            System.out.println(Colors.ANSI_RED + "На вас напал " + monster.getName() + " (уровень " + monster.getLevel() + ")" + Colors.ANSI_RESET);
            System.out.println("Потеря инициативы, вас атакуют!");
            System.out.println("1. В бой!");
            System.out.println("2. Попытаться убежать.");
            String choice;
            while (isChecked) {
                try {
                    choice = bufferedReader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                switch (choice) {
                    case "1":
                        try {
                            fight(whoFirst, hero, monster, mobList, executorService, chanel);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        isChecked = false;
                        break;
                    case "2":
                        if (randomEscape) {
                            chanel.escape();
                        } else {
                            System.out.println("Убажать не получилось, тебя догнали.");
                            System.out.println();
                            try {
                                fight(whoFirst, hero, monster, mobList, executorService, chanel);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        isChecked = false;
                        break;
                    default:
                        System.out.println("Неверный ввод! Введите 1 или 2. Что тут сложного!");
                        break;
                }
            }
        }
    }
}
