package DataBaseConnect;

import org.json.simple.JSONObject;

import java.security.PublicKey;

import java.security.PrivateKey;
import java.sql.*;

/**
 * @author Malek Shero
 */

public class DBConnect {
    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_system_level2", "root", "");
            st = con.createStatement();
        } catch (Exception ex) {
            System.out.println("err :  " + ex);
        }
    }

    public void getData() throws SQLException {
        String query = "select * from users";
        rs = st.executeQuery(query);
        System.out.println("recordes fromData base");
        while (rs.next()) {
            String name = rs.getString("name");
            System.out.println("name " + name);
        }
    }

    public void insertTransactionToDB(String send_id, String reciver_id, String remittance_value, String describtion) throws SQLException {
        String query = "insert into remittances (send_id, reciver_id, remittance_value, describtion)" +
                " values (" + "'" + send_id + "'" + "," + "'" + reciver_id + "'" + "," + "'" + remittance_value + "'" + "," + "'" + describtion + "'" + ") ";
        System.out.println(query);
        st.executeUpdate(query);
    }


    public void insertClientKeysToDB(String publickey, String privateKey, String name) throws SQLException {

        String query = "UPDATE users SET private_key = '" + privateKey + "', public_key = '" + publickey + "'  WHERE name= '" + name + "' ";
        st.executeUpdate(query);
        System.out.println("keys Inserted");

    }

    public String getNamebySenderID(String sendname, String SendId) throws SQLException {
        String query = "select user_id from users where name =" + "'" + sendname + "'";
        rs = st.executeQuery(query);
        String userid = "";
        while (rs.next()) {
            userid = rs.getString("user_id");
            System.out.println("user_id " + userid);
        }
        if (userid.equals(SendId))
            return "Done";
        else return "This Account Number Not for This name";
    }

    public String Transfer_money(String send_id, String receiver_id, String amount) throws SQLException {

        int value = Integer.parseInt(amount);
        int accountValue = AccountValue(send_id);
        if (accountValue > value) {
            System.out.println(value);
            String query3 = "UPDATE users " +
                    "SET balance = balance + " + value +
                    " WHERE user_id= " + receiver_id;
            String query4 = " UPDATE users" +
                    " SET  balance = balance - " + value +
                    " WHERE user_id= " + send_id;
            st.addBatch(query3);
            st.addBatch(query4);
            st.executeBatch();
            return "Done";
        } else return "The Value you send is bigger than your account Value";
    }

    public int AccountValue(String send_id) throws SQLException {
        String query = "select balance from users WHERE user_id= " + "'" + send_id + "'";
        rs = st.executeQuery(query);
        int balance = 0;
        while (rs.next()) {
            balance = rs.getInt("balance");
        }
        System.out.println("not stuck in get private from db");
        return balance;
    }

    public String getPrivateKEYFromDB(String name) throws SQLException {
        String query = "select private_key from users WHERE name= " + "'" + name + "'";
        rs = st.executeQuery(query);
        String privateKey = "";
        while (rs.next()) {
            privateKey = rs.getString("private_key");
            ;
        }
        System.out.println("not stuck in get private from db");
        return privateKey;
    }
}