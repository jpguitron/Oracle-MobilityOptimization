import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class DatabaseConnection 
{
    private static float[][] time_matrix()
    {
        float[][] times;       
        Statement stmt = null;
        Connection conn = connect();
        try 
        {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM DISTANCES;" );
            int rowsCount = rs.getRow();
            System.out.println(rowsCount);
            times = new float [rowsCount][rowsCount]; 
            
            for(int x = 0;rs.next() && x < rowsCount; x++) 
            {
               int id_s = rs.getInt("ID_S");
               int id_e = rs.getInt("ID_E");
               int distance = rs.getInt("distance");
               int duration  = rs.getInt("duration");
               
               System.out.println( "ID_S = " + id_s );
               System.out.println( "ID_E = " + id_e );
               System.out.println( "distance = " + distance );
               System.out.println( "duration = " + duration );
               System.out.println();
            }
            rs.close();
            stmt.close();
            disconnect(conn);
            return times;
         } 
         catch ( Exception e ) 
         {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
         }
         System.out.println("Operation done successfully");
         return null;
    }
    private static Connection connect() 
    {
        Connection conn = null;
        try 
        {
            String url = "jdbc:sqlite:DB/SEMANAi.db";
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } 
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
            return null;
        } 


    }
    private static void disconnect(Connection conn)
    {
        try 
        {
            if (conn != null) 
            {
                conn.close();
                System.out.println("Disconnected from SQLite.");
            }
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) 
    {

        
    }
}