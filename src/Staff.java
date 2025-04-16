public class Staff {
    private final String id;
    private final String name;
    private int price;
    private boolean isLegendary = false;

    public Staff(String id, String name, int price) {
        this.name = name;
        this.price = price;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setLegendary(boolean legendary) {
        isLegendary = legendary;
    }

    public boolean isLegendary() {
        return isLegendary;
    }

    @Override
    public String toString() {
        return String.format("%s (%d руб.)", name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return name.equals(((Staff) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}