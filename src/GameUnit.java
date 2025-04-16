import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

abstract public class GameUnit {
    protected String name;
    private final AtomicInteger health = new AtomicInteger(100);
    private int gold = new SplittableRandom().nextInt(20, 70);
    private int strength = 20;
    private int dexterity = 20;
    private int level;
    private int exp = 0;
    private int critChance = 0;
    private boolean isPawn = false;
    private boolean isBoss = false;
    private final AtomicBoolean isDead = new AtomicBoolean(false);

    public GameUnit(String name, int level) {
        this.name = name;
        this.level = level;
        setHealth(100 + (level - 1) * 5);
        setStrength(strength + (level - 1));
        setDexterity(dexterity + (level - 1));
    }

//    public GameUnit(String name, int level, boolean isPawn) {
//        this.isPawn = isPawn;
//        this.name = name;
//        this.level = level;
//        setHealth(70 + (level - 1) * 5);
//        setStrength((int) (strength * 0.7) + (level - 1));
//        setDexterity((int) (dexterity * 0.7) + (level - 1));
//    }

    public void setHealth(int health) {
        this.health.set(health);
    }

    public int getHealth() {
        return health.get();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPawn(boolean pawn) {
        isPawn = pawn;
    }

    public boolean isPawn() {
        return isPawn;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public void setBoss(boolean boss) {
        isBoss = boss;
    }

    protected Map<Staff, Integer> inventory = new HashMap<>();

    public void setName(String name) {
        this.name = name;
    }

    public Map<Staff, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Staff staff, int value) {
        this.inventory.put(staff, value);
    }

    public Staff dropInventory() {
        if (this.gold > 50) this.gold = 50;
        Set<Staff> staffs = inventory.keySet();
        if (!staffs.isEmpty()) {
            Staff anyStaff = staffs.iterator().next();
            inventory.computeIfPresent(anyStaff, (p, q) -> q > 2 ? q - 1 : null);
            return anyStaff;
        }
        return null;
    }

    public void clearInventory() {
        inventory.clear();
    }

    int attack() {
        SplittableRandom random = new SplittableRandom();
        boolean hit = random.nextInt(1, 101) < dexterity * 3;
        boolean criticalHit = random.nextInt(1, 101) <= 30 + getCritChance();

        if (hit) {
            if (criticalHit) {
                return strength * 2;
            } else {
                return strength;
            }
        } else {
            return 0;
        }
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return level;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getGold() {
        return gold;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void levelUp(int exp) {
        if (exp >= 100) {
            level = level + exp / 100;
            this.exp = exp % 100;
            setHealth(100 + (level - 1) * 5);
            setDexterity(dexterity + exp / 100);
            setStrength(strength + exp / 100);
        }
    }

    public int getCritChance() {
        return critChance;
    }

    public void setCritChance(int critChance) {
        this.critChance = critChance;
    }

    public boolean isDead() {
        return isDead.get();
    }

    public void setDead(boolean dead) {
        isDead.set(dead);
    }
}
