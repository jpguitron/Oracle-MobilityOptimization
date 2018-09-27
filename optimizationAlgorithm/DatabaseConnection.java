package optimizationAlgorithm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import java.sql.*;

public class DatabaseConnection 
{
    public static HashMap<Integer, Node> nodes_matrix_hashMap(int initialNodes[],int finalNode, int transitionNodes[])
    {
        HashMap<Integer, Node> hmap = new HashMap<Integer, Node>();

        int allNodes [] = new int[initialNodes.length + transitionNodes.length + 1]; 
        System.arraycopy(transitionNodes, 0, allNodes,  0, transitionNodes.length);
        System.arraycopy(initialNodes, 0, allNodes,  transitionNodes.length, initialNodes.length);
        allNodes[allNodes.length-1] = finalNode;

        for(int x = 0; x < allNodes.length; x++)
        {
            Node node = new Node();
            hmap.put(allNodes[x],node);
        }

        /*Statement stmt = null;
        Connection conn = connect();
        
        try 
        {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM DISTANCES WHERE ID_S != ID_E ORDER BY ID_S;" );


            if ( ArrayUtils.contains( fieldsToInclude, "id" ) ) 
            {
                // Do some stuff.
            }
            
            int actual = rs.getInt("ID_S");
            nodes[0].id = actual;

            while(rs.next()) 
            {
               
            }
            rs.close();

            rs = stmt.executeQuery( "SELECT * FROM LOCATIONS ORDER BY ID;" );
            contNodes = 0;
            while(rs.next()) 
            {
                int id = rs.getInt("ID");   
                if(nodes[contNodes].id == id)
                {
                    nodes[contNodes].lat = rs.getFloat("LAT");
                    nodes[contNodes].lon = rs.getFloat("LON");
                    contNodes++;
                }
            }
            
            stmt.close();
            disconnect(conn);



            return hmap;
         } 
         catch ( Exception e ) 
         {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
         }

         return null;*/
        }
        
    
    /*public static Node[] nodes_matrix(int initialNodes[],int finalNode, int transitionNodes[])
    {
        HashMap<Integer, Node> hmap = new HashMap<Integer, Node>();

        Node nodes[];     

        Statement stmt = null;
        Connection conn = connect();
        
        try 
        {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM DISTANCES WHERE ID_S != ID_E ORDER BY ID_S;" );
            int allNodes = initialNodes.length + transitionNodes.length + 1;

            for(int x = 0; x < transitionNodes.length; x++)
            {
                Node node = new Node();
                hmap.put(transitionNodes[x],node);
            }

            for(int x = 0; x < initialNodes.length; x++)
            {
                Node node = new Node();
                hmap.put(initialNodes[x],node);
            }

            Node node = new Node();
            hmap.put(finalNode,node);
            
            while(rs.next()) 
            {
               
               int id_s = rs.getInt("ID_S");
               int id_e = rs.getInt("ID_E");
               float distance = rs.getFloat("distance");
               float duration  = rs.getFloat("duration");



            }
            rs.close();

            rs = stmt.executeQuery( "SELECT * FROM LOCATIONS ORDER BY ID;" );
            contNodes = 0;
            while(rs.next()) 
            {
                int id = rs.getInt("ID");   
                if(nodes[contNodes].id == id)
                {
                    nodes[contNodes].lat = rs.getFloat("LAT");
                    nodes[contNodes].lon = rs.getFloat("LON");
                    contNodes++;
                }
            }
            
            stmt.close();
            disconnect(conn);



            return nodes;
         } 
         catch ( Exception e ) 
         {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
         }
         return null;
    }*/

    private static Connection connect() 
    {
        Connection conn = null;
        try 
        {
            String url = "jdbc:sqlite:optimizationAlgorithm/DB/SEMANAi.db";
            conn = DriverManager.getConnection(url);
            
            //System.out.println("Connection to SQLite has been established.");
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
            }
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }
}
