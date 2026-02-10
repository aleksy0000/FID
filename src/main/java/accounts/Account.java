/*
    Accounts are for metadata only, they define identity, rules and lifecycle.
*/
package accounts;

import java.util.UUID;

public class Account {
    private final String accID = UUID.randomUUID().toString();
    private final String accName;
    private final AccountType accType;
    private final Currency currency;

    public Account(String accName, AccountType type, Currency currency){
        if(accName != null && !accName.trim().isBlank()){
            this.accName = accName.trim();
        }else{
            throw new IllegalArgumentException("Account name cannot be blank");
        }

        if(type != null && currency != null){
            this.accType = type;
            this.currency = currency;
        }
        else{
            throw new IllegalArgumentException("Invalid Account Type");
        }
    }

    public String getAccID(){
        return accID;
    }

    public String getAccName(){
        return accName;
    }

    public String getAccTypeString(){
        return accType.name();
    }

    public String getCurrencyString(){
        return currency.name();
    }


}
