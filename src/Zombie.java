public class Zombie extends GameUnit{

    public Zombie(String name , int level, boolean isPawn) {
        super(name, level);
        this.name = name;
        super.setPawn(isPawn);
        setLevel(level);
        setHealth(70 + (level - 1) * 5);
        setStrength((int) (getStrength() * 0.7) + (level - 1));
        setDexterity((int) (getDexterity() * 0.7) + (level - 1));
    }
}
