package DBInteractionPackage;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

// javac -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar -d . DBInteraction.java
public abstract class DBInteraction{
    public Connection connection;

    // this will serve as the constructor of every inherithing class
    public void openConnection(String url, String uid, String pwd){
        try{
            Driver monDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(monDriver);
            this.connection = DriverManager.getConnection(url,uid,pwd);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void closeConnection(){
        try{
            this.connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        System.out.println("Successfully closed the connection");
    }

    public ArrayList<ArrayList<String>> convertResultSet(ArrayList<String> attributes, ResultSet input){
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

    public String joinAttributesWithComa(ArrayList<String> attributes){
        String joinedArray = attributes.get(0);
        for(int i = 1; i < attributes.size(); i++){
            joinedArray = joinedArray + ", " + attributes.get(i);
        }
        return joinedArray;
    }

    public String joinForUpdate(HashMap<String, String> dict){
        String settings = new String();
        int i = 0;
        for(String k : dict.keySet()){
            i = i + 1;
            settings = settings.concat(k + " = ?");
            if(i != dict.size()){
                settings = settings.concat(", ");
            }
        }
        return settings;
    }


    // METHOD INTERFACES --- Create --- Read --- Update --- Delete
    public abstract int insert(ArrayList<String> attributes);
    public abstract ArrayList<ArrayList<String>> select(ArrayList<String> columns);
    public abstract int update(String primaryKey, HashMap<String, String> updateFields);
    public abstract int delete(String numEmp);
}