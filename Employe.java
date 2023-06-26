package DBInteractionPackage;

import DBInteractionPackage.DBInteraction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

// FACTORY DESIGN PATTERN, THE ABSTRACT IMPLEMENTATION
// javac -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar -d . DBInteraction.java Employe.java
public class Employe extends DBInteraction{
    public ArrayList<String> attributes = new ArrayList<String>(List.of("numEmp", "nom", "prenom", "poste", "salaire"));

    private String createRandomPrimaryKey(){
        final String possibleCharSet = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        final int numEmpLength = 3;
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

    private String createValablePrimaryKey(){
        ArrayList<String> column = new ArrayList<String>(List.of("numEmp"));
        ArrayList<ArrayList<String>> allPrimaryKey = select(column);

        String invalidKey = "000";
        boolean keyAlreadyTaken;
        String randomPrimaryKey;

        do{
            randomPrimaryKey = createRandomPrimaryKey();
            ArrayList<String> wrappedPrimaryKey = new ArrayList<String>(List.of(randomPrimaryKey));
            keyAlreadyTaken = allPrimaryKey.contains(wrappedPrimaryKey) || randomPrimaryKey == invalidKey;
        }
        while(keyAlreadyTaken);

        return randomPrimaryKey;
    }

    private void setVariablesInsert(PreparedStatement preparedStatement, ArrayList<String> attributes){
        try{   
            preparedStatement.setString(1, attributes.get(0));
            preparedStatement.setString(2, attributes.get(1));
            preparedStatement.setString(3, attributes.get(2));
            preparedStatement.setString(4, attributes.get(3));
            preparedStatement.setInt(5, Integer.parseInt(attributes.get(4)));
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    private void setVariablesUpdate(PreparedStatement preparedStatement, String numEmp, HashMap<String, String> updateFields){
        int i = 0;
        // modifications
        for(String k : updateFields.keySet()){
            i = i + 1;
            String value = updateFields.get(k);
            chooseSetter(preparedStatement, k, i, value);
        }

        // condition
        final int nextIndex = 1;
        int conditionIndex = updateFields.size() + nextIndex;
        try{
            preparedStatement.setString(conditionIndex, numEmp);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    private void chooseSetter(PreparedStatement preparedStatement, String k, int placeholderIndex, String value){
        try{
            if(k == "salaire"){
                int salaire = Integer.parseInt(value);
                preparedStatement.setInt(placeholderIndex, salaire);
            }
            else{
                preparedStatement.setString(placeholderIndex, value);
            }
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    public int insert(ArrayList<String> attributes){
        String numEmp = createValablePrimaryKey();
        attributes.add(0, numEmp);

        try{
            String query = new String("INSERT INTO employe VALUES(?, ?, ?, ?, ?);");
            System.out.println(query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setVariablesInsert(preparedStatement, attributes);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }

        return -1;
    }

    public ArrayList<ArrayList<String>> select(ArrayList<String> columns){
        String joinedColumns = joinAttributesWithComa(columns);
        ArrayList<ArrayList<String>> formatedResult = new ArrayList<ArrayList<String>>();
        try{
            String query = new String("SELECT " + joinedColumns + " FROM employe");
            ResultSet reader;
            Statement statement = connection.createStatement();
            reader = statement.executeQuery(query);
            // convertResultSet of the parent
            formatedResult = convertResultSet(columns, reader);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return formatedResult;
    }

    // to be supplied a HashMap with a string as key, and a string as well as the replacement value
    public int update(String numEmp, HashMap<String, String> updateFields){
        String settings = joinForUpdate(updateFields);
        try{
            String query = new String("UPDATE employe SET " + settings + " WHERE numEmp = ?; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setVariablesUpdate(preparedStatement, numEmp, updateFields);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return -1;
    }

    public int delete(String numEmp){
        try{
            String query = new String("DELETE FROM employe WHERE numEmp = ?; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, numEmp);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return -1;
    }
}