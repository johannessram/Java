package DBInteractionPackage;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class Pointage extends TableCRUD{
    public final ArrayList<String> attributes = new ArrayList<String>(List.of("datePointage", "numEmp", "pointage"));

    private void setVariablesInsert(PreparedStatement preparedStatement, ArrayList<String> providedAttributes){
        try{
            preparedStatement.setString(1, providedAttributes.get(0));
            preparedStatement.setString(2, providedAttributes.get(1));
            preparedStatement.setString(3, providedAttributes.get(2));
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    public boolean isAlreadyMarkedPresent(String numEmp){
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
            return new ArrayList<ArrayList<String>>();
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
            System.out.println(preparedStatement);
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
            ArrayList<String> queryParameter = new ArrayList<String>(List.of(date, absentPeopleId.get(i).get(0), "non"));
            todayAbsent = insert(queryParameter);
        }
        return todayAbsent;
    }

    public int callItADay(){
        return callItADay(UsefulMethods.getNowSDate());
    }

    // peut-etre qu'on devrait un petit code hoe rehefa tonga ny heure firavana de izay rehetra tsy nanao pointage dia atao non ny an'i zareo?
    // ou peut-etre ataoko ny code hoe callItADay de ataony non izay employe rehetra tsy nanao pointage androany. Bref apetrako eto ity de ngamba ny mety asina bouton eh?
    public int insert(ArrayList<String> providedAttributes){
        try{
            String query = new String("INSERT INTO pointage VALUES(?, ?, ?); ");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            setVariablesInsert(preparedStatement, providedAttributes);
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