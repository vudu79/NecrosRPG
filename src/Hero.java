import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Hero extends GameUnit implements Trader {
    private final String name;
    private int mana;
    private final ArrayList<GameUnit> army = new ArrayList<>();
    private boolean isHPBuffed;
    private boolean isStrongBuffed;
    private boolean isDexBuffed;
    private final Map<Staff, Integer> inventory = new HashMap<>();
    private final Map<Staff, Integer> cart = new HashMap<>();

    public Hero(String name, int level, int gold) {
        super(name, level);
        super.setGold(gold);
        super.setStrength( 20);
        super.setDexterity(20);
        super.setHealth(220);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void updateDeposit(int gold) {
        setGold(getGold() + gold);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setHPBuffed(boolean HPBuffed) {
        isHPBuffed = HPBuffed;
    }

    public void setStrongBuffed(boolean strongBuffed) {
        isStrongBuffed = strongBuffed;
    }

    public void setDexBuffed(boolean dexBuffed) {
        isDexBuffed = dexBuffed;
    }

    public boolean isHPBuffed() {
        return isHPBuffed;
    }

    public boolean isStrongBuffed() {
        return isStrongBuffed;
    }

    public boolean isDexBuffed() {
        return isDexBuffed;
    }

    public ArrayList<GameUnit> getArmy() {
        return army;
    }

    public void viewArmy() {
        System.out.println("Мой мертвый легион:");
        if (army.isEmpty()) {
            System.out.println("пока ни одного зомби не поднял...");
        } else {
            army.forEach((p) -> System.out.printf("  %s - %s %d уровня\n", p.getName(), p.getClass().getName(), p.getLevel()));
        }
    }

    public boolean addPawnToArmy(GameUnit pawn) {
        if (army.size() == 3) {
            System.out.println("Ты не сможешь управлять больше чем тремя умертвиями!");
            System.out.println("Поднятый зомбак будет чилить тут пока его не сожрут. А ты иди воюй!\n");
            return false;
        } else {
            army.add(pawn);
            return true;
        }
    }

    public void removePawnFromArmy(GameUnit pawn) {
        this.army.remove(pawn);
    }

    @Override
    public void levelUp(int exp) {
        int currentLevel = getLevel();
        if (exp >= 100) {
            setLevel(getLevel() + exp / 100) ;
            setExp(exp % 100);
            setHealth(120 + (getLevel() - 1) * 5);
            setDexterity(getDexterity() + (getLevel() - 1) + 2);
            setStrength(getStrength() + (getLevel() - 1) + 2);
            setMana(mana + (getLevel() - currentLevel) * 5);
        }
    }

    @Override
    public String getTraderName() {
        return name;
    }

    @Override
    public int getDeposit() {
        return getGold();
    }

    @Override
    public void addStaff(Staff product, int quantity) {
        inventory.merge(product, quantity, Integer::sum);
    }

    @Override
    public void removeStaff(Staff product, int quantity) {
        inventory.computeIfPresent(product, (p, q) -> q > quantity ? q - quantity : null);
    }

    @Override
    public boolean hasStaff(Staff product, int quantity) {
        return inventory.getOrDefault(product, 0) >= quantity;
    }

    @Override
    public Map<Staff, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    @Override
    public void showInventory() {
        System.out.printf(Colors.ANSI_CYAN + "=== Инвентарь (%s) ===\n" + Colors.ANSI_RESET, name);
        System.out.printf("Деньги: %d руб.\n", getGold());

        if (inventory.isEmpty()) {
            System.out.println("Хабара нет");
            return;
        }

        inventory.entrySet().stream().sorted(Comparator.comparing(o -> o.getKey().getId()))
                .forEach((p) ->
                        System.out.printf("%s. %s: %d шт. (%d руб.)\n", p.getKey().getId(), p.getKey().getName(), p.getValue(), p.getKey().getPrice()));
    }

    @Override
    public void addToCart(Staff product, int quantity) {
        cart.merge(product, quantity, Integer::sum);
    }

    @Override
    public void removeFromCart(Staff product, int quantity) {
        cart.computeIfPresent(product, (p, q) -> q > quantity ? q - quantity : null);
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public void viewCart() {
        System.out.printf(Colors.ANSI_GREEN + "=== Корзина %s ===\n" + Colors.ANSI_RESET, name);

        if (cart.isEmpty()) {
            System.out.println("Корзина пуста");
            return;
        }

        int total = 0;
        for (Map.Entry<Staff, Integer> entry : cart.entrySet()) {
            int sum = entry.getKey().getPrice() * entry.getValue();
            System.out.printf(" - %s: %d x %d = %d руб.\n",
                    entry.getKey().getName(), entry.getValue(), entry.getKey().getPrice(), sum);
            total += sum;
        }
        System.out.printf("Итого: %d руб.\n", total);
    }

    @Override
    public boolean buyFrom(Trader seller) {
        if (cart.isEmpty()) {
            System.out.println("Нечего покупать - корзина пуста");
            return false;
        }

        for (Map.Entry<Staff, Integer> entry : cart.entrySet()) {
            if (!seller.hasStaff(entry.getKey(), entry.getValue())) {
                System.out.printf("У %s недостаточно %s\n",
                        seller.getTraderName(), entry.getKey().getName());
                return false;
            }
        }

        int total = cart.entrySet().stream()
                .mapToInt(e -> e.getKey().getPrice() * e.getValue())
                .sum();

        if (total > getGold()) {
            System.out.printf("Недостаточно средств. Нужно: %d, есть: %d\n", total, getGold());
            return false;
        }

        cart.forEach((product, quantity) -> {
            seller.removeStaff(product, quantity);
            this.addStaff(product, quantity);
        });

        setGold(getGold() - total);
        if (seller instanceof Dealer) {
            ((Dealer) seller).updateDeposit(total);
        }

        System.out.printf(Colors.ANSI_GREEN + "%s успешно купил товары у %s на сумму %d руб.\n\n" + Colors.ANSI_RESET,
                name, seller.getTraderName(), total);
        clearCart();
        return true;
    }
}
