package accTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This class contains and manages all the accounts created by the user, directly interacting with budget.
public class AccountTracker {
    public static int accCounter = 0;
    private final List<Account> accounts = new ArrayList<Account>();
    private final Map<Integer, Account> accountsById = new HashMap<Integer, Account>();

    public AccountTracker(){

    }

    //creates new account and returns its ID
    public int createNewAccount(String accName, int accTypeChoice, double balance){
        //1. Create the new account
        Account account = new Account(accName, accTypeChoice, balance);
        accounts.add(account);

        //2. Get its ID
        int accID = account.getAccID();
        accountsById.put(accID, account);

        //3. Increment account counter
        accCounter++;

        //4. Return accID
        return accID;
    }

    public Account getAccountById(int accID) {
        return accountsById.get(accID);
    }

    public List<Account> getAccounts() {
        return accounts;
    }


}

