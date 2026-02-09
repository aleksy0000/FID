package accTrack;

public class Account {
    public static int accNum = 0;
    private final int accID;
    private final String accName;
    private AccountType accType;
    private double balance;

    public Account(String accName, int accTypeChoice, double balance){
        this.accName = accName;
        this.balance = balance;
        if(accTypeChoice == 1){
            accType = AccountType.CURRENT;
        }
        else if(accTypeChoice == 2){
            accType = AccountType.SAVINGS;
        }
        else{
            System.out.println("Invalid Account Type");
        }

        accNum++;
        accID = accNum;
    }

    public void deposit(double amount){
        balance = balance + amount;
    }

    public void withdraw(double amount){
        balance = balance - amount;
    }

    public int getAccID(){
        return accID;
    }

    public String getAccName(){
        return accName;
    }

    public AccountType getAccType(){
        return accType;
    }

    public double getBalance() {
        return balance;
    }

}
