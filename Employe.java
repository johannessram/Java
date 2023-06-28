package DBInteractionPackage;

import DBInteractionPackage.TableCRUD;
import DBInteractionPackage.UsefulMethods;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

public class Employe extends TableCRUD{
    public ArrayList<String> attributes = new ArrayList<String>(List.of("numEmp", "nom", "prenom", "poste", "salaire"));

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

    private int dependancyDelete(String numEmp, String tableName){
        try{
            String query = new String("DELETE FROM " + tableName + " WHERE numEmp = ?; ");
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

    public int insert(ArrayList<String> attributes){
        System.out.println("INSERTION HEREE");
        String numEmp = UsefulMethods.createValablePrimaryKey("employe");
        attributes.add(0, numEmp);
        try{
            String query = new String("INSERT INTO employe VALUES(?, ?, ?, ?, ?);");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setVariablesInsert(preparedStatement, attributes);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        System.out.println("INSERTION HEREE");
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
        int affectedInPointage = dependancyDelete(numEmp, "pointage");
        int affectedInconge = dependancyDelete(numEmp, "conge");
        int affectedInEmploye = dependancyDelete(numEmp, "employe");

        return affectedInconge + affectedInEmploye + affectedInPointage;
    }

}