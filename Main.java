import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import DBInteractionPackage.DBInteraction;
import DBInteractionPackage.Employe;
import DBInteractionPackage.Pointage;
import DBInteractionPackage.Conge;

// java -classpath .:DBInteractionPackage/mysql-connector-java-8.0.27.jar Main.java
class Main{
    public static void main(String [] args){
        String url = "jdbc:mysql://localhost:3306/projet4Java";
        String uid = "projet4Java";
        String pwd = "test";
        // create an instance of a class named like the table to which we want to CRUD
        Conge conge = new Conge();
        conge.openConnection(url, uid, pwd);

        // test insert
        // int aff = conge.insert(new ArrayList<String>(List.of("JXM", "communion de sa fille", Integer.toString(1), "2023-06-18", "2023-06-12")));
        // System.out.println(aff);

        // test select
        // ArrayList<ArrayList<String>> testSelect = conge.select(new ArrayList<String>(List.of("numConge", "numEmp", "motif", "nbrjr", "dateDemande", "dateRetour")));
        // read2DArrayList(testSelect);

        // test update
        // HashMap<String, String> param = new HashMap<String, String>();
        // param.put("dateRetour", "2023-06-19");
        // param.put("motif", "mariage d'un proche");
        // int affectedRows = conge.update("UQ6EC6", param);

        // test delete
        // int affectedRows = conge.delete("R3GC");


        conge.closeConnection();
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
