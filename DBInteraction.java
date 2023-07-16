package DBInteractionPackage;

import java.sql.*;
import java.util.ArrayList;

public abstract class DBInteraction{
    public static Connection connection;

    public static ArrayList<ArrayList<String>> convertResultSet(ArrayList<String> attributes, ResultSet input){
        ArrayList<ArrayList<String>> returnValue = new ArrayList<ArrayList<String>>();
        try{
            while(input.next()){
                ArrayList<String> temporary = new ArrayList<String>();
                for(int i = 0; i < attributes.size(); i++){
                    temporary.add(input.getString(attributes.get(i)));
                }
                returnValue.add(temporary);
            }
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return returnValue;
    }

    public static void openConnection(String url, String uid, String pwd){
        try{
            Driver monDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(monDriver);
            connection = DriverManager.getConnection(url,uid,pwd);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    public static void closeConnection(){
        try{
            connection.close();
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        System.out.println("Successfully closed the connection");
    }

    public static String joinAttributesWithComa(ArrayList<String> attributes){
        String joinedArray = attributes.get(0);
        for(int i = 1; i < attributes.size(); i++){
            joinedArray = joinedArray + ", " + attributes.get(i);
        }
        return joinedArray;
    }
}