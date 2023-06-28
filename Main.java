import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import DBInteractionPackage.DBInteraction;
import DBInteractionPackage.Employe;
import DBInteractionPackage.Pointage;

// java -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar  Main.java
class Main{
    public static void main(String [] args){
        String url = "jdbc:mysql://localhost:3306/projet4Java";
        String uid = "projet4Java";
        String pwd = "test";
        // create an instance of a class named like the table to which we want to CRUD
        Pointage point = new Pointage();
        point.openConnection(url, uid, pwd);

        // test Create
        // ArrayList<String> arg = new ArrayList<String>(List.of("11E", "oui"));
        // int affectedRows = point.markAsPresent("QQU");
        // System.out.println("AFFECTED ROWS = " + affectedRows);

        // ArrayList<ArrayList<String>> absentPeopleId = point.notMarkedPresentOrNULL();
        // read2DArrayList(absentPeopleId);
        // int absentPeopleNum = point.callItADay();
        // System.out.println("ABSENT PEOPLE = " + absentPeopleNum);
        // test Read

        // ArrayList<ArrayList<String>> result = point.select(new ArrayList<String>(List.of("datePointage", "numEmp", "pointage")));
        // read2DArrayList(result);

        // test Update
        // HashMap<String, String> dict = new HashMap<String, String>();
        // dict.put("pointage", "wuw");
        // int affectedRows = point.update("2023-06-20 12:34:09", dict);

        // test Delete
        // String code = new String("2023-06-27 23:12:36");
        // int affectedRows = point.delete(code);
        // System.out.println("AFFECTED ROWS = " + affectedRows);

        point.closeConnection();
    }

    public static void read2DArrayList(ArrayList<ArrayList<String>> tableau){
        for(int i = 0; i < tableau.size(); i++){
            for(int j = 0; j < tableau.get(i).size(); j++){
                System.out.print(tableau.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
}
