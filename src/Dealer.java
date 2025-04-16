import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Dealer implements Trader {
    private final String name;
    private int gold;
    private final Map<Staff, Integer> inventory = new HashMap<>();
    private final Map<Staff, Integer> cart = new HashMap<>();

    public Dealer(String name, int money) {
        this.name = name;
        this.gold = money;
    }

    public void updateDeposit(int gold) {
        this.gold = this.gold + gold;
    }

    @Override
    public String getTraderName() {
        return name;
    }

    @Override
    public int getDeposit() {
        return gold;
    }

    @Override
    public void addStaff(Staff staff, int quantity) {
        inventory.merge(staff, quantity, Integer::sum);
    }

    @Override
    public void removeStaff(Staff staff, int quantity) {
        inventory.computeIfPresent(staff, (p, q) -> q > quantity ? q - quantity : null);
    }

    @Override
    public boolean hasStaff(Staff staff, int quantity) {
        return inventory.getOrDefault(staff, 0) >= quantity;
    }

    @Override
    public Map<Staff, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    @Override
    public void showInventory() {
        System.out.printf(Colors.ANSI_CYAN + "=== Инвентарь (%s) ===\n" + Colors.ANSI_RESET, name);
        System.out.printf("Деньги: %d руб.\n", gold);

        if (inventory.isEmpty()) {
            System.out.println("Хабара нет");
            return;
        }

        inventory.entrySet().stream().sorted(new Comparator<Map.Entry<Staff, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Staff, Integer> o1, Map.Entry<Staff, Integer> o2) {
                        return o1.getKey().getId().compareTo(o2.getKey().getId());
                    }
                })
                .forEach((p) ->
                        System.out.printf("%s. %s: %d шт. (%d руб.)\n", p.getKey().getId(), p.getKey().getName(), p.getValue(), p.getKey().getPrice()));
    }

    @Override
    public void addToCart(Staff staff, int quantity) {
        cart.merge(staff, quantity, Integer::sum);
    }

    @Override
    public void removeFromCart(Staff staff, int quantity) {
        cart.computeIfPresent(staff, (p, q) -> q > quantity ? q - quantity : null);
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public void viewCart() {
        System.out.printf(Colors.ANSI_CYAN + "=== Корзина %s ===\n" + Colors.ANSI_RESET, name);

        if (cart.isEmpty()) {
            System.out.println("Корзина пуста");
            return;
        }

        int total = 0;
        for (Map.Entry<Staff, Integer> entry : cart.entrySet()) {
            int sum = entry.getKey().getPrice() * entry.getValue();
            System.out.printf("- %s: %d x %d = %d руб.\n",
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

        if (total > gold) {
            System.out.printf("Недостаточно средств. Нужно: %d, есть: %d\n", total, gold);
            return false;
        }

        cart.forEach((staff, quantity) -> {
            seller.removeStaff(staff, quantity);
            this.addStaff(staff, quantity);
        });

        this.gold -= total;
        if (seller instanceof Hero) {
            ((Hero) seller).updateDeposit(total);
        }

        System.out.printf(Colors.ANSI_CYAN + "%s успешно купил товары у %s на сумму %d руб.\n\n" + Colors.ANSI_RESET,
                name, seller.getTraderName(), total);
        clearCart();
        return true;
    }
}