import java.io.IOException;
import java.util.List;

public interface Chanel {
    void win(GameUnit winner, GameUnit looser, List<GameUnit> location) throws IOException;

    void lose(GameUnit winner, GameUnit looser, List<GameUnit> location, Staff dropedStaff) throws IOException;

    void escape();

//    void everyoneIsDead();
}
