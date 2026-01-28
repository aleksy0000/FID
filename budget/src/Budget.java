import java.util.ArrayList;

public class Budget {
    public static int tierCounter;
    protected ArrayList<Tier> tiers = new ArrayList<>();

    public void createTier(int priority){
        Tier tier = new Tier(priority);
        tiers.add(tier);
        tierCounter++;
    }

    public Tier getTier(int index){
        return tiers.get(index);
    }
}
