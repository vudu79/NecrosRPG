
public enum PriceList {
    HP("1", "Зелье здоровья", 50),
    XP("2", "Зелье опыта", 1000),
    SUICIDE("3", "Зелье суицида", 1),

    STRONG_BUFF("4", "Зелье на +10 силы (на одну локацию)", 200),
    DEX_BUFF("5", "Зелье на +10 ловкости (на одну локацию)", 200),
    HP_BUFF("6", "Зелье на +30 HP (на одну локацию)", 200),
    STRONG_UP("7", "Зелье на +5 силы (навсегда)", 500),
    DEX_UP("8", "Зелье на +5 ловкости (навсегда)", 500),
    HP_UP("9", "Зелье на +15 HP (навсегда)", 500),

    BOW("10", "Старый лук", 10),
    EARS("11", "Уши гоблина", 10),
    SWORD("12", "Ржавый меч", 10),
    BONE("13", "Кость", 10),
    SPEAR("14", "Поломанное копье", 10),
    SCULL("15", "Череп", 10),
    CLUB("16", "Дубина", 10),
    MASTER_KEY("17", "Отмычка", 10),
    ARTIFACT("18", "Странный артефакт", 500),

    BOW_BOSS("20", "Лук Шамана гоблинов (Легендарный, +10 к ловкости)", 5000),
    SWORD_BOSS("21", "Меч Лича (Легендарный, +10 к силе)", 5000),
    CLUB_BOSS("22", "Дубина Вождя големов (Легендарный, +20 к здоровью)", 5000),
    SPEAR_BOSS("23", "Копье Атамана (Легендарный, +10% к шансу крит атаки)", 5000);

    private final String staffNumber;
    private final String name;
    private final int price;

    private PriceList(String staffNumber, String name,int price ){
        this.staffNumber = staffNumber;
        this.name = name;
        this.price = price;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
