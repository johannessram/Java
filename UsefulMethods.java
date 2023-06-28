package DBInteractionPackage;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/*
DO NOT FORGET TO OPEN A CONNECTION FROM ANY DERIVED CLASS OF DBInteraction
OR YOU CAN JUST USE THIS IN YOUR MAIN FILE: UsefulMethods.openConnection(String url, String uid, String pwd);
AND DO NOT FORGET TO CLOSE IT: UsefulMethods.closeConnection()
*/

public class UsefulMethods extends DBInteraction{
    public static String createRandomPrimaryKey(String table){
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

    public static String createValablePrimaryKey(String table){
        TableCRUD concernedTable;
        String keyName = new String();
        String invalidKey = new String();
        if(table == "employe"){
            concernedTable = new Employe();
            keyName = "numEmp";
            invalidKey = "000";
        }
        // else if(table == "conge")
        else{
            concernedTable = new Conge();
            keyName = "numConge";
            invalidKey = "0000";
        }
        ArrayList<String> column = new ArrayList<String>(List.of(keyName));
        ArrayList<ArrayList<String>> allPrimaryKey = concernedTable.select(column);
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

    public static String getNowSDateTime(){
        String dateTime = new String("0000-00-00 00:00:00");
        try{
            String query = new String("SELECT now() as now; ");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                dateTime = result.getString("now");
            }
        }
        catch(Exception exc){
            System.err.println("RETURN NULL DATE 0000-00-00 00:00:00");
            System.err.println(exc);
        }
        return dateTime;
    }

    public static String getNowSDate(){
        String date = new String("0000-00-00");
        try{
            String query = new String("SELECT date(now()) as today; ");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                date = result.getString("today");
            }
        }
        catch(Exception exc){
            System.err.println("RETURN NULL DATE 0000-00-00");
            System.err.println(exc);
        }
        return date;
    }

    public static String getEndDate(String startDate, int numOfDays){
        String date = new String("0000-00-00");
        try{
            String query = new String("SELECT DATE_ADD(?, INTERVAL ? DAY) AS endDate; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, startDate);
            preparedStatement.setInt(2, numOfDays);
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()){
                date = result.getString("endDate");
            }
        }
        catch(Exception exc){
            System.err.println("RETURN NULL DATE 0000-00-00");
            System.err.println(exc);
        }
        return date;
    }

    public static int getNumOfDays(String startDate, String endDate){
        int numOfDays = 0;
        try{
            String query = new String("SELECT DATEDIFF(?, ?) AS numOfDays; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()){
                numOfDays = result.getInt("numOfDays");
            }
        }
        catch(Exception exc){
            System.err.println("RETURNED 0 REGARDLESS OF THE START AND END DATES");
            System.err.println(exc);
        }
        return numOfDays;
    }

    public static String getStartDate(String endDate, int numOfDays){
        int rewindNumOfDays = numOfDays * (-1);
        return UsefulMethods.getEndDate(endDate, rewindNumOfDays);
    }
}