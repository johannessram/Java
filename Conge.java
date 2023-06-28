package DBInteractionPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

// javac -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar -d . DBInteraction.java Employe.java Conge.java
public class Conge extends DBInteraction{
    private void setVariablesInsert(PreparedStatement preparedStatement, ArrayList<String> attributes){
        try{   
            preparedStatement.setString(1, attributes.get(0));
            preparedStatement.setString(2, attributes.get(1));
            preparedStatement.setString(3, attributes.get(2));
            preparedStatement.setInt(4, Integer.parseInt(attributes.get(3)));
            preparedStatement.setString(5, attributes.get(4));
            preparedStatement.setString(6, attributes.get(5));
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    public int insert(ArrayList<String> attributes){
        String numEmp = createValablePrimaryKey("conge");
        attributes.add(0, numEmp);
        try{
            String query = new String("INSERT INTO conge VALUES(?, ?, ?, ?, ?, ?);");
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
            String query = new String("SELECT " + joinedColumns + " FROM conge; ");
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

    private void setVariablesUpdate(PreparedStatement preparedStatement, String primaryKey, HashMap<String, String> updateFields){
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
            preparedStatement.setString(conditionIndex, primaryKey);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    private void chooseSetter(PreparedStatement preparedStatement, String k, int placeholderIndex, String value){
        try{
            if(k == "nbrjr"){
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

    public int update(String primaryKey, HashMap<String, String> updateFields){
        String settings = joinForUpdate(updateFields);
        try{
            String query = new String("UPDATE conge SET " + settings + " WHERE numConge = ?; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setVariablesUpdate(preparedStatement, primaryKey, updateFields);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return -1;
    }

    public int delete(String primaryKey){
        try{
            String query = new String("DELETE FROM conge WHERE numConge = ?; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, primaryKey);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return -1;
    }
}