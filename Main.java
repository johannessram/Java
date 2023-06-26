import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import DBInteractionPackage.DBInteraction;
import DBInteractionPackage.Employe;

// java -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar  Main.java
class Main{
    public static void main(String [] args){
        String url = "jdbc:mysql://localhost:3306/projet4Java";
        String uid = "projet4Java";
        String pwd = "test";
        // create an instance of a class named like the table to which we want to CRUD
        Employe emp = new Employe();
        emp.openConnection(url, uid, pwd);

        // test Create
        // int salaire = 300000;
        // String salaireString = Integer.toString(salaire);
        // ArrayList<String> arg = new ArrayList<String>(List.of("NOMENA", "FIDERANA VALISOA", "concierge", salaireString));
        // int affectedRows = emp.insert(arg);
        // System.out.println("AFFECTED ROWS = " + affectedRows);

        // test Read
        // ArrayList<ArrayList<String>> result = emp.select(new ArrayList<String>(List.of("numEmp", "nom")));
        // read2DArrayList(result);

        // test Update
        // HashMap<String, String> dict = new HashMap<String, String>();
        // dict.put("salaire", "1");
        // int affectedRows = emp.update("I8E", dict);

        // test Delete
        // String code = new String("6KJ");
        // int affectedRows = emp.delete(code);
        // System.out.println("AFFECTED ROWS = " + affectedRows);

        emp.closeConnection();
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
