package DBInteractionPackage;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

// javac -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar -d . DBInteraction.java Employe.java Conge.java
public class Pointage extends DBInteraction{
    private void setVariablesInsert(PreparedStatement preparedStatement, ArrayList<String> providedAttributes){
        try{
            preparedStatement.setString(1, providedAttributes.get(0));
            preparedStatement.setString(2, providedAttributes.get(1));
        }
        catch(Exception exc){
            System.err.println(exc);
        }
    }

    public ArrayList<String> getAbsentPeopleIdOfTheDay(){
        ArrayList<ArrayList<String>> absentPeopleInfo = notMarkedPresentOrNULL();
        ArrayList<String> onlyId = new ArrayList<String>();
        for(int i = 0; i < absentPeopleInfo.size(); i++){
            String id = absentPeopleInfo.get(i).get(0);
            onlyId.add(id);
        }
        return onlyId;
    }

    // faire le pointage
    public int markAsPresent(String id){
        return insert(new ArrayList<String>(List.of(id, "oui")));
    }

    public int callItADay(){
        ArrayList<String> absentPeopleId = getAbsentPeopleIdOfTheDay();
        int todayAbsent = 0;
        for(int i = 0; i < absentPeopleId.size(); i++){
            ArrayList<String> queryParameter = new ArrayList<String>(List.of(absentPeopleId.get(i), "non"));
            todayAbsent = insert(queryParameter);
        }
        return todayAbsent;
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

    // peut-etre qu'on devrait un petit code hoe rehefa tonga ny heure firavana de izay rehetra tsy nanao pointage dia atao non ny an'i zareo?
    // ou peut-etre ataoko ny code hoe callItADay de ataony non izay employe rehetra tsy nanao pointage androany. Bref apetrako eto ity de ngamba ny mety asina bouton eh?
    public int insert(ArrayList<String> providedAttributes){
        // only provide 2 args to it: the id and the state "oui" or "non"
        try{
            String query = new String("INSERT INTO pointage VALUES(now(), ?, ?);");
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

    // tiens toi bien fa tsy immunise contre les injections le misy date fa lany haiky ah amin'ilay formattagen-le izy
    public int update(String dateTimeAsString, HashMap<String, String> updateFields){
        // problem here is about formatting a date
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