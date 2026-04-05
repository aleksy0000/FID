/*
    AccountRepo.java

    Purpose -> AccountRepo.java handles all operations modifying and accessing the accounts table in the database.

    Functionality:
    - createTable() -> Inserts an account to the database -> used for creating new accounts
    - accListToString() -> Returns an arraylist of Strings that contain records from the account table -> used for quick lookup of accounts in the database
 */
package repo;

import db.Db;

import java.sql.*;
import java.util.*;

public class AccountRepo {
    //Insert an account into database
    public void insertAccountRecord(String accID,String accName, String accType, String currency){
        String sql = "INSERT INTO accounts(accID, accName, accType, currency) VALUES (?, ?, ?, ?)";
        try(Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, accID);
            ps.setString(2, accName);
            ps.setString(3, accType);
            ps.setString(4, currency);
            ps.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    //Return an ArrayList of account metadata as string.
    public List<String> accListToString(){
        String sql = "SELECT accID, accName, accType, currency FROM accounts ORDER BY accID";
        try(Connection c = Db.connect();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            List<String> out = new ArrayList<>();
            while(rs.next()){
                out.add(rs.getString("accID") + ": " + rs.getString("accName") + ": " + rs.getString("accType") + " (" + rs.getString("currency") + ")");
            }
            return out;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
