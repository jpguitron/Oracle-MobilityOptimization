package optimizationAlgorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import optimizationAlgorithm.DatabaseConnection;
import optimizationAlgorithm.Node;

public class mainPruebas 
{
    public static void main(String[] args) 
    {
        int[] a = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49 };
        //int [] a = {50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99};
        int[] b = { 101, 102, 103, 104, 105 };
        int c = 100;

        DatabaseConnection data = new DatabaseConnection();
        Node[] w = data.nodes_matrix(b, c, a);

        HashMap<Integer, Node> hmap = data.nodes_matrix_hashMap(b, c, a);

        for(Map.Entry<Integer, Node> entry : hmap.entrySet()) 
        {
            int key = entry.getKey();
            Node value = entry.getValue();
            
            for(Map.Entry<Integer, edge> ent : value.hmap.entrySet()) 
            {
                int key2 = ent.getKey();
                edge value2 = ent.getValue();
                System.out.println(key + " " + key2 + " "+value2.cost + " lat: "+value.lat+" lon: "+value.lon);
            }
        }
        
        /*
        for(int x = 0; x < w.length;x++) { for(int y = 0; y < w[x].TEdgeSize ;y++) 
        {
            System.out.print(w[x].id+" ");
            System.out.print(w[x].transitionEdges[y].dest+" ");
            System.out.print(w[x].transitionEdges[y].cost+" ");
            System.out.print(w[x].transitionEdges[y].dist_cost+" ");
            System.out.print(w[x].transitionEdges[y].time_cost+" | ");
            System.out.print("Lat: "+w[x].lat+" Lon:"+w[x].lon);
            System.out.println();
        }
          
        if(w[x].destEdge!=null) 
        { 
            System.out.print(w[x].id+" ");
            System.out.print(w[x].destEdge.dest+" ");
            System.out.print(w[x].destEdge.cost+" ");
            System.out.print(w[x].destEdge.dist_cost+" ");
            System.out.print(w[x].destEdge.time_cost+" | "); 
            System.out.print("Lat: "+w[x].lat+" Lon:"+w[x].lon);
            System.out.println(); 
        }
         
        for(int y = 0; y < w[x].IEdgeSize ;y++) 
        { 
            System.out.print(w[x].id+" ");
            System.out.print(w[x].initialEdges[y].dest+" ");
            System.out.print(w[x].initialEdges[y].cost+" ");
            System.out.print(w[x].initialEdges[y].dist_cost+" ");
            System.out.print(w[x].initialEdges[y].time_cost+" | "); 
            System.out.print("Lat: "+w[x].lat+" Lon:"+w[x].lon);
            System.out.println(); 
        }
         
        System.out.println("------------------------------"); }
         



        Routes rutas = new Routes(b);
        rutas.nodes_route(b, a, c, w, 1.5f);

        SetOverlaps overlaps = new SetOverlaps();
        Overlap over[] = overlaps.getOverlaps(rutas,b,w);
        for(int x = 0;x < over.length; x++)
        {
            System.out.print("Overlap "+x+": ");
            System.out.print(over[x].overlap_node+" ");
            System.out.print(over[x].route_node_1+" ");
            System.out.print(over[x].route_node_2+" ");
            System.out.println(over[x].probability);

        }
        
        */
    }
}