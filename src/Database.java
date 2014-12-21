/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



//import
import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author F0l2g3tm3n0t
 */
public class Database {
    
    public Database(String url, String user, String pass){
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
        this.conn = connectToDatabase();
    }
    
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = null;
    private String URL = "localhost:3306";
    private String USER = "root";
    private String PASS = "";
    
    public Connection connectToDatabase(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(URL);
            System.out.println("Connected database successfully...");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public void insert(String macaddress, String annotation, String signal, String frompi){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Inserting record into the table...");
            stmt = conn.createStatement();

            String sql = "INSERT INTO `disaster`.`askforhelp`  ( `macaddress`, `annotation`, `signal`, `frompi`) "
                         + "VALUES ( "
                         + "'" + macaddress + "', "
                         + "'" + annotation + "', " 
                         + "'" + signal     + "', "
                         + "'" + frompi		+ "' "
                         + ")";
            stmt.executeUpdate(sql);
            System.out.println("Inserted record into the table...");

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
    }//end insert
    
    public void updateLocate(String macaddress, String frompi){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Updating status in the table...");
            stmt = conn.createStatement();

            String sql = "UPDATE `askforhelp` "
                         + "SET "
                         + "`frompi`			='" + frompi		+ "'"
                         + "WHERE `macaddress` 	='" + macaddress 	+ "'";
            stmt.executeUpdate(sql);
            System.out.println("Updated record in the table...");

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
    }//end update
    
    public void update(String macaddress, String annotation, String signal, String frompi){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Updating status in the table...");
            stmt = conn.createStatement();

            String sql = "UPDATE `askforhelp` "
                         + "SET "   
                         + "`annotation` 		='" + annotation 	+ "', "
                         + "`signal` 			='" + signal		+ "', "
                         + "`frompi`			='" + frompi		+ "'"
                         + "WHERE `macaddress` 	='" + macaddress 	+ "'";
            stmt.executeUpdate(sql);
            System.out.println("Updated record in the table...");

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
    }//end update
    
    public void deleteData(String ipaddress){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Deleting record"+ipaddress+"in the table...");
            stmt = conn.createStatement();
            String sql = "DELETE FROM `mysql`.`neighborinformation` "                 
                         + "WHERE `neighborinformation`.`ipaddress` ='" + ipaddress + "'";
            stmt.executeUpdate(sql);
            System.out.println("Deleted record"+ipaddress+" in the table...");

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
    }//end update
   
    public ResultSet select(String frompi){
        System.out.println("reach select method");
         //STEP 4: Execute a query
        try{
            System.out.println("selecting record in the table...");
            stmt = conn.createStatement();
            sql = "SELECT * FROM `askforhelp` WHERE `frompi` = '" + frompi + "'";
            rs = stmt.executeQuery(sql);
            System.out.println("Select record from the table...");
            return rs;

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
        return rs;
    }//end select
    
    public ResultSet selectAll(){
        System.out.println("reach select method");
         //STEP 4: Execute a query
        try{
            System.out.println("Selecting All record in the table...");
            stmt = conn.createStatement();
            sql = "SELECT * FROM `neighborinformation`";
            System.out.println("Select record from the table...");
            return rs;

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
        return rs;
    }//end selectAll
    
    public ResultSet checkMac(String macaddress){
        System.out.println("reach select method");
         //STEP 4: Execute a query
        try{
            System.out.println("selecting record in the table...");
            stmt = conn.createStatement();
            sql = "SELECT * FROM `askforhelp` WHERE `macaddress` = '" + macaddress + "'";
            rs = stmt.executeQuery(sql);
            System.out.println("Select record from the table...");
            return rs;

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
        return rs;
    }//end select
}
