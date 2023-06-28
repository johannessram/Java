import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import DBInteractionPackage.TableCRUD;
import DBInteractionPackage.DBInteraction;
import DBInteractionPackage.UsefulMethods;
import DBInteractionPackage.Employe;
import DBInteractionPackage.Pointage;
import DBInteractionPackage.Conge;

// java -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar Main.java
class Main{
    static String url = "jdbc:mysql://localhost:3306/projet4Java";
    static String uid = "projet4Java";
    static String pwd = "test";

    public static void main(String[] args){
        Pointage point = new Pointage();
        point.openConnection(url, uid, pwd);
        ArrayList<ArrayList<String>> output =  point.select(new ArrayList<String>(List.of("datePointage")));
        point.closeConnection();
        read2DArrayList(output);

        UsefulMethods.openConnection(url, uid, pwd);

        String startDate = UsefulMethods.getStartDate("2023-06-20", 7);
        System.out.println(startDate);

        String now = UsefulMethods.getNowSDateTime();
        System.out.println(now);

        UsefulMethods.closeConnection();
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
