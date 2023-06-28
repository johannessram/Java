package DBInteractionPackage;

import java.sql.*;

public abstract class DBInteraction{
    public static Connection connection;

    public static void openConnection(String url, String uid, String pwd){
        try{
            Driver monDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(monDriver);
            connection = DriverManager.getConnection(url,uid,pwd);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void closeConnection(){
        try{
            connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        System.out.println("Successfully closed the connection");
    }
}