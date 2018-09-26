package optimizationAlgorithm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.lang.model.util.ElementScanner6;

import java.sql.*;

public class DatabaseConnection 
{
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
                else if(x >= initialNodes.length)
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
               int distance = rs.getInt("distance");
               int duration  = rs.getInt("duration");

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
            stmt.close();
            disconnect(conn);
            return nodes;
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
                //System.out.println("Disconnected from SQLite.");
            }
        } 
        catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }

    /*public static void main(String[] args) 
    {
        int [] a = {0,1,2,3};
        int [] b = {101,102};
        int c = 100;
        
        Node[] w = nodes_matrix(b, c, a);
        
        System.out.print("id");
        System.out.print(" id_2");
        System.out.print(w[x].transitionEdges[y].cost+" ");
        System.out.print(w[x].transitionEdges[y].dist_cost+" ");
        System.out.print(w[x].transitionEdges[y].time_cost+" ");
        System.out.println();

        for(int x = 0; x < w.length;x++)
        {
            for(int y = 0; y < w[x].TEdgeSize ;y++)
            {
                System.out.print(w[x].id+" ");
                System.out.print(w[x].transitionEdges[y].dest+" ");
                System.out.print(w[x].transitionEdges[y].cost+" ");
                System.out.print(w[x].transitionEdges[y].dist_cost+" ");
                System.out.print(w[x].transitionEdges[y].time_cost+" ");
                System.out.println();
                
            }

            if(w[x].destEdge!=null)
            {
                System.out.print(w[x].id+" ");
                System.out.print(w[x].destEdge.dest+" ");
                System.out.print(w[x].destEdge.cost+" ");
                System.out.print(w[x].destEdge.dist_cost+" ");
                System.out.print(w[x].destEdge.time_cost+" ");
                System.out.println();
            }

            for(int y = 0; y < w[x].IEdgeSize ;y++)
            {
                System.out.print(w[x].id+" ");
                System.out.print(w[x].initialEdges[y].dest+" ");
                System.out.print(w[x].initialEdges[y].cost+" ");
                System.out.print(w[x].initialEdges[y].dist_cost+" ");
                System.out.print(w[x].initialEdges[y].time_cost+" ");
                System.out.println();
            }
            


            System.out.println("------------------------------");


        }
    }*/
}
