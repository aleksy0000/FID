package budget;

import java.util.ArrayList;
import java.util.List;

public class Budget {
    public static int tierCounter;
    private final Tier incomeTier;
    private ArrayList<Tier> tiers = new ArrayList<>();

    public Budget(){
        incomeTier = new Tier(0);
        incomeTier.setTierName("Income");
        tiers.add(incomeTier);
    }

    public void createTier(int priority){
        Tier tier = new Tier(priority);
        tiers.add(tier);
        tierCounter++;
    }

    public Tier getTier(int index){
        return tiers.get(index);
    }

    public List<Tier> getTiers() {
        return tiers;
    }

    public Tier getIncomeTier(){
        return incomeTier;
    }

    public double getTotalTierTotals(){
        double totalTierTotals = 0;

        for(int i = 0;i < tiers.size();i++){
            totalTierTotals += tiers.get(i).calcTierExpenseTotal();
        }

        return totalTierTotals;
    }

    public double getTotalIncome(){
        return incomeTier.calcTierExpenseTotal();
    }

    public double getTotalRevenue(){
        double totalIncome = getTotalIncome();
        double totalExpenses = getTotalTierTotals() - totalIncome;
        return totalIncome - totalExpenses;
    }
}
