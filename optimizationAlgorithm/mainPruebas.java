package optimizationAlgorithm;

public class mainPruebas 
{
    public static void main(String[] args) 
    {
        int[] a = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49 };
        int[] b = { 101, 102, 103, 104, 105 };
        int c = 100;

        DatabaseConnection data = new DatabaseConnection();
        Node[] w = data.nodes_matrix(b, c, a);


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
        
        
    }
}