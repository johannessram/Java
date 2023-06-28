package DBInteractionPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class TableCRUD extends DBInteraction{
    // this will serve as the constructor of every inherithing class
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

    // METHOD INTERFACES --- Create --- Read --- Update --- Delete
    public abstract int insert(ArrayList<String> attributes);
    public abstract ArrayList<ArrayList<String>> select(ArrayList<String> columns);
    public abstract int update(String primaryKey, HashMap<String, String> updateFields);
    public abstract int delete(String numEmp);
}