package DBInteractionPackage;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class Pointage extends TableCRUD{
    public String tableName = new String("pointage");
    public final ArrayList<String> attributes = new ArrayList<String>(List.of("datePointage", "numEmp", "pointage"));

    private void setVariablesInsert(PreparedStatement preparedStatement, ArrayList<String> attributes){
        try{
            preparedStatement.setString(1, attributes.get(0));
            preparedStatement.setString(2, attributes.get(1));
            preparedStatement.setString(3, attributes.get(2));
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    private boolean isAlreadyMarkedPresent(String numEmp){
        String query = new String("SELECT COUNT(datePointage) AS yes FROM pointage WHERE DATE(datePointage) = DATE(NOW()) AND pointage = \"oui\" AND numEmp = ?; ");
        int yes = 1;
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, numEmp);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                yes = resultSet.getInt("yes");
            }
            if(yes == 1){
                return true;
            }
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return false;
    }

    private void setVariablesUpdate(PreparedStatement preparedStatement, String primaryKey, HashMap<String, String> updateFields){
        int i = 0;
        // modifications
        for(String k : updateFields.keySet()){
            i = i + 1;
            String value = updateFields.get(k);
            chooseSetter(preparedStatement, k, i, value);
        }
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
            preparedStatement.setString(placeholderIndex, value);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    private ArrayList<ArrayList<String>> getDayOffPeople(ArrayList<String> columns, String date){
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        String query = new String("SELECT * FROM employe JOIN pointage ON employe.numEmp = pointage.numEmp WHERE pointage = ?; ");
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "con");
            ResultSet resultSet = preparedStatement.executeQuery();
            result = convertResultSet(columns, resultSet);
            return result;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return result;
    }

    public ArrayList<ArrayList<String>> getAbsentPeopleGivenADate(ArrayList<String> columns, String date, boolean nonKeyWord, boolean conKeyWord){
        if(!nonKeyWord && !conKeyWord){
            Employe employe = new Employe();
            return employe.select(columns);
        }
        else if(conKeyWord && !nonKeyWord){
            // use a special command if only dayOff people are needed
            return getDayOffPeople(columns, date);
        }

        ArrayList<ArrayList<String>> formatedResult = new ArrayList<ArrayList<String>>();
        String joinedColumns = joinAttributesWithComa(columns);

        String query = new String("SELECT * FROM employe WHERE numEmp NOT IN ( SELECT numEmp FROM pointage WHERE DATE(datePointage) = ? AND (pointage = ?");
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try{
            if(nonKeyWord  && !conKeyWord){
                // exclude CON keyword if only nonKeyword is needed
                query = query.concat(" OR pointage = ?)); ");
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(3, "con");
            }
            else{
                // only exclude those who has a OUI as pointage
                query = query.concat(")); ");
                preparedStatement = connection.prepareStatement(query);
            }
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, "oui");
            resultSet = preparedStatement.executeQuery();
            formatedResult = convertResultSet(columns, resultSet);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return formatedResult;
    }

    public int callItADay(String date){
        ArrayList<ArrayList<String>> absentPeopleId = getAbsentPeopleGivenADate(new ArrayList<String>(List.of("numEmp")), date, true, false);
        int todayAbsent = 0;
        for(int i = 0; i < absentPeopleId.size(); i++){
            String newKey = createDateTimePrimaryKey(i);
            ArrayList<String> queryParameter = new ArrayList<String>(List.of(absentPeopleId.get(i).get(0), "non"));
            queryParameter.add(0, newKey);
            todayAbsent += insert(queryParameter);
        }
        return todayAbsent;
    }

    public int callItADay(){
        return callItADay(UsefulMethods.getNowSDate());
    }

    // faire le pointage
    public int markAsPresent(String id){
        if(isAlreadyMarkedPresent(id)){
            return 0;
        }
        String todaySDate = UsefulMethods.getNowSDate();
        todaySDate = todaySDate.concat(" 00:00:00");
        String now = UsefulMethods.getNowSDateTime();
        String nouveauPointage = "oui";
        HashMap <String, String> updateFields = new HashMap<String, String>();
        updateFields.put("datePointage", now);
        updateFields.put("pointage", "oui");

        return update(todaySDate, updateFields);
    }

    public int insert(ArrayList<String> attributes){
        if(attributes.size() != 3){
            return -1;
        }
        try{
            String query = new String("INSERT INTO pointage VALUES(?, ?, ?); ");
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
            String query = new String("SELECT " + joinedColumns + " FROM pointage");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            formatedResult = convertResultSet(columns, resultSet);
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return formatedResult;
    }

    public int update(String dateTimeAsString, HashMap<String, String> updateFields){
        String settings = joinForUpdate(updateFields);
        try{
            String query = new String("UPDATE pointage SET " + settings + " WHERE datePointage = ?; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setVariablesUpdate(preparedStatement, dateTimeAsString, updateFields);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return -1;
    }

    public int delete(String dateTimeAsString){
        try{
            String query = new String("DELETE FROM pointage WHERE datePointage = \"" + dateTimeAsString + "\"; ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows;
        }
        catch(Exception exc){
            System.err.println(exc);
        }
        return -1;
    }
}