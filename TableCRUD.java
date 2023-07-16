package DBInteractionPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class TableCRUD extends DBInteraction{
    public ArrayList<String> attributes;

    public String createDateTimePrimaryKey(int numOfSeconds){
        String newDateTime = new String();
        ResultSet resultSet;
        try{
            String query = new String("SELECT DATE_ADD(NOW(), INTERVAL ? SECOND) as newPrimaryKey; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, numOfSeconds);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                return resultSet.getString("newPrimaryKey");
            }
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return new String();
    }

    // for tables employe and conge only
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
        TableCRUD concernedTable;
        String keyName = new String();
        String invalidKey = new String();
        if(table == "employe"){
            concernedTable = new Employe();
            keyName = "numEmp";
            invalidKey = "000";
        }
        else if(table == "conge"){
            concernedTable = new Conge();
            keyName = "numConge";
            invalidKey = "0000";
        }
        // else if(table == "pointage"){
        else{
            concernedTable = new Pointage();
            keyName = "datePointage";
            invalidKey = "0000-00-00 00:00:00";
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