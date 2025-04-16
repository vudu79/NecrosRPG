import java.util.Map;

public interface Trader {
    String getTraderName();

    int getDeposit();

    void addStaff(Staff product, int quantity);

    void removeStaff(Staff product, int quantity);

    boolean hasStaff(Staff product, int quantity);

    Map<Staff, Integer> getInventory();

    void showInventory();

    void addToCart(Staff product, int quantity);

    void removeFromCart(Staff product, int quantity);

    void viewCart();

    boolean buyFrom(Trader seller);

//    boolean sellTo(Trader buyer, Staff product, int quantity, int price);

    void clearCart();
}