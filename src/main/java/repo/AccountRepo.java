package repo;

import db.Db;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

public class AccountRepo {
    public long create(String accName, String accType, String currency){
        String sql = "INSERT INTO accounts(accName, accType, currency) VALUES (?, ?, ?)";
        try(Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, accName);
            ps.setString(2, accType);
            ps.setString(3, currency);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) return rs.getLong(1);

                throw new SQLException("No generated key");
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<String> list(){
        String sql = "SELECT accID, accName, accType, currency FROM accounts ORDER BY accID";
        try(Connection c = Db.connect();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            List<String> out = new ArrayList<>();
            while(rs.next()){
                out.add(rs.getLong("accID") + ": " + rs.getString("accName") + ": " + rs.getString("accType") + " (" + rs.getString("currency") + ")");
            }
            return out;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
