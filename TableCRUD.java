package DBInteractionPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class TableCRUD extends DBInteraction{
    public ArrayList<String> attributes;
    // this will serve as the constructor of every inherithing class
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

    private String formatAttributesForSearch(ArrayList<String> attributes){
        // ArrayList<String> attributes = childClass.attributes;
        String formattedAttributes = new String();
        for(int i = 0; i < attributes.size(); i++){
            formattedAttributes += attributes.get(i) + " LIKE ? ";
            int nextIndex = i + 1;
            if(nextIndex < attributes.size()){
                formattedAttributes += "OR ";
            }
        }
        return formattedAttributes;
    }

    // please use all the attributes of each child class
    public ArrayList<ArrayList<String>> searchUsingKeyword(String keyWord, String tableName, ArrayList<String> attributes){
        ArrayList<ArrayList<String>> result;
        try{
            String query = new String("SELECT * FROM " + tableName + " WHERE " + formatAttributesForSearch(attributes) + "; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            final int statementStartIndex = 1;
            for(int i = 0; i < attributes.size(); i++){
                preparedStatement.setString(i + statementStartIndex, "%" + keyWord + "%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return convertResultSet(attributes, resultSet);
            // System.out.println(preparedStatement);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return new ArrayList<ArrayList<String>>();
    }

    // METHOD INTERFACES --- Create --- Read --- Update --- Delete
    public abstract int insert(ArrayList<String> attributes);
    public abstract ArrayList<ArrayList<String>> select(ArrayList<String> columns);
    public abstract int update(String primaryKey, HashMap<String, String> updateFields);
    public abstract int delete(String numEmp);
}