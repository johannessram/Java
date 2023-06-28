package DBInteractionPackage;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// javac -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar -d . DBInteraction.java
public abstract class DBInteraction{
    public Connection connection;

    public String createRandomPrimaryKey(String table){
        final String possibleCharSet = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        int numEmpLength = 0;
        if(table == "employe"){
            numEmpLength = 3;
        }
        else if(table == "conge"){
            numEmpLength = 6;
        }
        String numEmp = new String();
        for(int i = 0; i < numEmpLength; i++){
            Random rand = new Random();
            int generated = rand.nextInt(36);
            // may produce negative output
            if(generated < 0){
                generated = generated * (-1);
            }
            numEmp = numEmp.concat(possibleCharSet.substring(generated, generated + 1));
        }
        return numEmp;
    }

    public String createValablePrimaryKey(String table){
        String keyName = new String();
        String invalidKey = new String();
        if(table == "employe"){
            keyName = "numEmp";
            invalidKey = "000";
        }
        else if(table == "conge"){
            keyName = "numConge";
            invalidKey = "0000";
        }
        ArrayList<String> column = new ArrayList<String>(List.of(keyName));
        ArrayList<ArrayList<String>> allPrimaryKey = select(column);

        boolean keyAlreadyTaken;
        String randomPrimaryKey;

        do{
            randomPrimaryKey = createRandomPrimaryKey(table);
            ArrayList<String> wrappedPrimaryKey = new ArrayList<String>(List.of(randomPrimaryKey));
            keyAlreadyTaken = allPrimaryKey.contains(wrappedPrimaryKey) || randomPrimaryKey == invalidKey;
        }
        while(keyAlreadyTaken);

        return randomPrimaryKey;
    }

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

    // not used and calls a depracated method in java.sql.Date, the constructer
    // public ArrayList<Integer> splitDateTimeIntoIntegers(String dateTimeAsString){
    //     ArrayList<Integer> fields = new ArrayList<Integer>();
    //     final int start = 0;
    //     final int yearLength = 4;
    //     String year = dateTimeAsString.substring(start, yearLength);
    //     int temporary = Integer.parseInt(year);
    //     fields.add(temporary);
    //     System.out.println(temporary);

    //     int otherLength = 2;
    //     int separatorLength = 1;
    //     for(int i = yearLength + separatorLength; i < dateTimeAsString.length(); i = i + otherLength + separatorLength){
    //         System.out.println(dateTimeAsString.substring(i, i + otherLength));
    //         temporary = Integer.parseInt(dateTimeAsString.substring(i, i + otherLength));
    //         fields.add(temporary);
    //     }
    //     return fields;
    // }

    // public Date convertToDate(String dateTimeAsString){
    //     ArrayList<Integer> fields = splitDateTimeIntoIntegers(dateTimeAsString);
    //     int year = fields.get(0);
    //     int month = fields.get(1);
    //     int day = fields.get(2);
    //     return new Date(year, month, day);
    // }

    public ArrayList<ArrayList<String>> notMarkedPresentOrNULL(){
        ArrayList<ArrayList<String>> formatedResult = new ArrayList<ArrayList<String>>();
        try{
            String query = new String("SELECT * FROM employe LEFT JOIN pointage ON employe.numEmp = pointage.numEmp WHERE pointage IS null; ");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<String> conversionParameter = new ArrayList<String>(List.of("numEmp", "nom", "prenom", "poste", "salaire", "datePointage", "pointage"));
            formatedResult = convertResultSet(conversionParameter, resultSet);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return formatedResult;
    }

    // java.sql.Date





    // METHOD INTERFACES --- Create --- Read --- Update --- Delete
    public abstract int insert(ArrayList<String> attributes);
    public abstract ArrayList<ArrayList<String>> select(ArrayList<String> columns);
    public abstract int update(String primaryKey, HashMap<String, String> updateFields);
    public abstract int delete(String numEmp);
}