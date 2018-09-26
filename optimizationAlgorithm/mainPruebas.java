
package optimizationAlgorithm;

import java.util.Set;
import java.util.HashSet;

import optimizationAlgorithm.*;

public class mainPruebas {
    public static void main(String[] args) {
        int[] a = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49 };
        int[] b = { 101, 102, 103, 104, 105 };
        int c = 100;

        DatabaseConnection data = new DatabaseConnection();
        Node[] w = data.nodes_matrix(b, c, a);

        /*
         * for(int x = 0; x < w.length;x++) { for(int y = 0; y < w[x].TEdgeSize ;y++) {
         * System.out.print(w[x].id+" ");
         * System.out.print(w[x].transitionEdges[y].dest+" ");
         * System.out.print(w[x].transitionEdges[y].cost+" ");
         * System.out.print(w[x].transitionEdges[y].dist_cost+" ");
         * System.out.print(w[x].transitionEdges[y].time_cost+" ");
         * System.out.println();
         * 
         * }
         * 
         * if(w[x].destEdge!=null) { System.out.print(w[x].id+" ");
         * System.out.print(w[x].destEdge.dest+" ");
         * System.out.print(w[x].destEdge.cost+" ");
         * System.out.print(w[x].destEdge.dist_cost+" ");
         * System.out.print(w[x].destEdge.time_cost+" "); System.out.println(); }
         * 
         * for(int y = 0; y < w[x].IEdgeSize ;y++) { System.out.print(w[x].id+" ");
         * System.out.print(w[x].initialEdges[y].dest+" ");
         * System.out.print(w[x].initialEdges[y].cost+" ");
         * System.out.print(w[x].initialEdges[y].dist_cost+" ");
         * System.out.print(w[x].initialEdges[y].time_cost+" "); System.out.println(); }
         * 
         * System.out.println("------------------------------"); }
         */

        Routes rutas = new Routes(b);
        rutas.nodes_route(b, a, c, w);

        //
        int y;
        for (int x = 0; x < rutas.routes.length; x++) {
            // System.out.print("Route id: " + rutas.routes[x].routeName + " : ");

            for (y = x + 1; y < rutas.routes.length; y++) {
                Set<Node> OverlapA = new HashSet<Node>(rutas.routes[x].hash_Set);
                Set<Node> OverlapB = new HashSet<Node>(rutas.routes[y].hash_Set);

                OverlapA.retainAll(OverlapB);
                if (OverlapA.size() > 0) {
                    System.out.print("Overlap x:" + x + " y:" + y + " ");
                    for (Node s : OverlapA) {
                        System.out.print(s.id + ",");
                    }
                    System.out.println();
                }
            }
            //

            /**
             * for (Node s : rutas.routes[x].hash_Set) { System.out.print(s.id + ","); }
             **/

        }
    }
}