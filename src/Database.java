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
    private String URL = "jdbc:mysql://localhost/disaster?useUnicode=true&characterEncoding=UTF-8&user=root&password=";
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
    
    public void insert(String macaddress, String annotation, String signal, String frompi, double lat, double lon){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Inserting record into the table...");
            stmt = conn.createStatement();

            String sql = "INSERT INTO `disaster`.`askforhelp`  ( `macaddress`, `annotation`, `signals`, `frompi`, `lat`,`lon`) "
                         + "VALUES ( "
                         + "'" + macaddress + "', "
                         + "'" + annotation + "', " 
                         + "'" + signal     + "', "
                         + "'" + frompi		+ "', "
                         + "'" + lat	    + "', "
                         + "'" + lon	    + "' "
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
    
    public void updateLocate(String macaddress, String frompi, String user, String phone, double lat, double lon){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Updating status in the table...");
            stmt = conn.createStatement();

            String sql = "UPDATE `askforhelp` "
                         + "SET "
                         + "`frompi`			='" + frompi		+ "', "
                         + "`user`				='" + user			+ "', "
                         + "`phone`				='" + phone			+ "', "
                         + "`lat`				='" + lat			+ "', "
                         + "`lon`				='" + lon			+ "' "
                         + "WHERE `macaddress` 	='" + macaddress 	+ "' and"
                         + "`signals` != 'rescued'";
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
    
    public void update(String macaddress, String annotation, String signal, String frompi, String user, String phone, double lat, double lon){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Updating status in the table...");
            stmt = conn.createStatement();

            String sql = "UPDATE `askforhelp` "
                         + "SET "   
                         + "`annotation` 		='" + annotation 	+ "', "
                         + "`signals` 			='" + signal		+ "', "
                         + "`frompi`			='" + frompi		+ "', "
                         + "`user`				='" + user			+ "', "
                         + "`phone`				='" + phone			+ "', "
                         + "`lat`				='" + lat			+ "', "
                         + "`lon`				='" + lon			+ "' "
                         + "WHERE `macaddress` 	='" + macaddress 	+ "'";
            stmt.executeUpdate(sql);
            System.out.println(sql);
            System.out.println("Updated record in the table...");

         }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
         }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
         }
    }//end update
    
    public void deleteData(String macaddress){
        Connection conn = connectToDatabase();
        Statement stmt = null;
         //STEP 4: Execute a query
        try{
            System.out.println("Deleting record"+macaddress+"in the table...");
            stmt = conn.createStatement();
            String sql = "DELETE FROM `disaster`.`askforhelp` "                 
                         + "WHERE `askforhelp`.`macaddress` ='" + macaddress + "'";
            stmt.executeUpdate(sql);
            System.out.println("Deleted record"+macaddress+" in the table...");

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
            sql = "SELECT * FROM `askforhelp` WHERE `frompi` LIKE '%" + frompi + "%'";
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
            sql = "SELECT * FROM `askforhelp`";
            rs = stmt.executeQuery(sql);
            System.out.println("Selected record from the table...");
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

	public void updateSignal(String macaddress, String signals) {
		// TODO Auto-generated method stub
		Statement stmt = null;
        //STEP 4: Execute a query
       try{
           System.out.println("Updating status in the table...");
           stmt = conn.createStatement();

           String sql = "UPDATE `askforhelp` "
                        + "SET "   
                        + "`signals` 			='" + signals		+ "' "
                        + "WHERE `macaddress` 	='" + macaddress 	+ "'";
           stmt.executeUpdate(sql);
           System.out.println(sql);
           System.out.println("Updated record in the table...");

        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }
	}
}
