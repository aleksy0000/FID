package accountsTest;

import accounts.Account;
import accounts.AccountType;
import accounts.Currency;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    //A random ID should be generated upon creation of class
    @Test
    public void account_has_id() {
        Account account = new Account("acc test", AccountType.ASSET, Currency.EUR);

        assertNotNull(account.getAccID(), "Account ID should not be null");
    }

    //Account IDs should be unique
    @Test
    public void account_id_is_unique_across_multiple_instances(){
        Set<String> ids = new HashSet<>();

        for(int i = 0; i < 100; i++){
            Account acc =  new Account("acc test", AccountType.ASSET, Currency.EUR);
            assertTrue(ids.add(acc.getAccID()), "Duplicate ID found");
        }
    }

    //Account IDs should be a valid UUID
    @Test
    public void account_id_valid_uuid(){
        Account account1 = new Account("acc test", AccountType.ASSET, Currency.EUR);

        assertDoesNotThrow(() -> UUID.fromString(account1.getAccID()));
    }

    //Accounts should not have any NULL fields
    @Test
    public void accounts_have_no_missing_fields(){
        Account account1 = new Account("acc test", AccountType.ASSET, Currency.EUR);

        assertNotNull(account1.getAccName(), "Account name should not be null");
        assertNotNull(account1.getAccTypeString(), "Account type string should not be null");
        assertNotNull(account1.getCurrencyString(), "Currency string should not be null");
    }
}
