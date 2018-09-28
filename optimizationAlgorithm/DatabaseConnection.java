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
    private static boolean contains(int arr[], int numToFind)
    {
        for(int x = 0; x < arr.length; x++)
        {
            if(arr[x] == numToFind)
                return true;
        }
        return false;
    }

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
            node.id = allNodes[x];
            hmap.put(allNodes[x],node);
        }

        Statement stmt = null;
        Connection conn = connect();
        
        try 
        {

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM DISTANCES WHERE ID_S != ID_E ORDER BY ID_S;" );

            int actual;
            int id_e;
            float distance;
            float duration;

            do
            {
                actual = rs.getInt("ID_S");
                if (contains(allNodes,actual)) 
                {
                 
                    id_e = rs.getInt("ID_E");
                    distance = rs.getFloat("distance");
                    duration  = rs.getFloat("duration");

                    edge edg = new edge(id_e,duration,distance);

                    hmap.get(actual).addEdge(id_e, edg);
                }
            }
            while(rs.next());


            
            rs.close();

            rs = stmt.executeQuery( "SELECT * FROM LOCATIONS ORDER BY ID;" );
            while(rs.next()) 
            {
                Node node = hmap.get(rs.getInt("ID"));
                if( node != null)
                {
                    node.lat = rs.getFloat("LAT");
                    node.lon = rs.getFloat("LON");
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
         
         return null;
        }
        
    
        public static Node[] nodes_matrix(int initialNodes[],int finalNode, int transitionNodes[])
        {
            Node nodes[];       
            Statement stmt = null;
            Connection conn = connect();
            
            try 
            {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * FROM DISTANCES WHERE ID_S != ID_E ORDER BY ID_S;" );
                int allNodes = initialNodes.length + transitionNodes.length + 1;
                nodes = new Node [allNodes]; 
                

                for(int x =0; x < allNodes; x++)
                {
                    nodes[x] = new Node();
                    nodes[x].initalizeEdges(initialNodes.length,transitionNodes.length);
                }

                int actual = rs.getInt("ID_S");
                while(!contains(transitionNodes,actual))
                {
                    actual = rs.getInt("ID_S");
                    rs.next();
                }
                


                nodes[0].id = actual;

                int contNodes = 0; 
    
                int flag = 0;
                for(int x = 0; x < allNodes;x++)
                {
                    if(x < initialNodes.length)
                    {
                        
                        if(actual == initialNodes[x])
                        {
                            flag = 0;
                            break;
                        }
                    }
                    else if(x >= initialNodes.length && x < allNodes-1)
                    {
                        if(actual == transitionNodes[x-initialNodes.length])
                        {
                            flag = 1;
                            break;
                        }
                    }
                    else
                    {
                        flag = 2;
                    }
                }
                while(rs.next()) 
                {

                   int id_s = rs.getInt("ID_S");
                   int id_e = rs.getInt("ID_E");
                   float distance = rs.getInt("distance");
                   float duration  = rs.getInt("duration");
    
                   if(actual != id_s)
                   {
                        actual = id_s;
                        
                        for(int x = 0; x < allNodes;x++)
                        {
                            
                            if(x < initialNodes.length) 
                            {
                                if(actual == initialNodes[x])
                                {
                                    flag = 0;
                                    contNodes++;
                                    nodes[contNodes].id = actual;
                                    break;
                                }
                            }
                            else if(x >= initialNodes.length && x < allNodes - 1)
                            {
                                
                                if(actual == transitionNodes[x-initialNodes.length])
                                {
                                    flag = 1;
                                    contNodes++;
                                    nodes[contNodes].id = actual;
                                    break;
                                }
                            }
                            else if(actual == finalNode)
                            {
                                contNodes++;
                                nodes[contNodes].id = actual;

                                flag = 2;
                            }
                            else
                            {
                                flag = 3;
                            }
                        }
                   }
                   
                   for(int x = 0; x < allNodes;x++)
                   {
                       
                       if(x < initialNodes.length) 
                       {
                           if(id_e == initialNodes[x])
                           {
                               flag = 0;
                               break;
                           }
                       }
                       else if(x >= initialNodes.length && x < allNodes - 1)
                       {
                           
                           if(id_e == transitionNodes[x-initialNodes.length])
                           {
                               flag = 1;
                               break;
                           }
                       }
                       else if(id_e == finalNode)
                       {
                           flag = 2;
                       }
                       else
                       {
                           flag = 3;
                       }
                   }
                   if(flag == 0)
                    nodes[contNodes].addIEdge(id_e, duration, distance);
                   else if(flag == 1)
                    nodes[contNodes].addTEdge(id_e, duration, distance);
                   else if(flag == 2)
                    nodes[contNodes].addDEdge(id_e, duration, distance);
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
        }

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
