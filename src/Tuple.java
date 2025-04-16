public class Tuple<X, Y> {
    public final X key;
    public final Y value;

    public Tuple(X x, Y y) {
        this.key = x;
        this.value = y;
    }

    public X getKey() {
        return key;
    }

    public Y getValue() {
        return value;
    }
}