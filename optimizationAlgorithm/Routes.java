package optimizationAlgorithm;

import java.util.*;

import optimizationAlgorithm.*;


class Route
{
    public int routeName;
    public Set<Node> hash_Set = new HashSet<Node>(); 

    public Route(){}

    public void addNode(Node node)
    {
        hash_Set.add(node); 
    }

}
public class Routes 
{
    public Route routes[];

    public Routes(int routesId[]) 
    {

        routes = new Route[routesId.length];
        for(int x = 0; x < routesId.length; x++)
        {
            routes[x] = new Route();
            routes[x].routeName = routesId[x];
        }
    }

    public Node getNode(int id, Node nodes[])
    {
        for(int x = 0; x < nodes.length; x++)
        {
            if(nodes[x].id == id)
            {
                return nodes[x];
            } 
        }
        return null;
    }

    private float getCost(int id, Node node)
    {
        for(int x = 0; x < node.TEdgeSize; x++)
        {
            if(node.transitionEdges[x].dest == id)
            {   
                return node.transitionEdges[x].cost;
            }
        }
        return 0;
    }
    
    public void addToRoute(int id, Node node)
    {
        for(int x = 0; x < routes.length;x++)
        {
            if(routes[x].routeName == id)
            {
                routes[x].addNode(node);
                break;
            }
        }
    }

    public double getAngle(Node node_i, Node node_d,Node node_t)
    {
        double deltaX = node_i.lat - node_d.lat;
        double deltaY = node_i.lon - node_d.lon;

        double angle_1 = Math.atan(deltaY/deltaX); 
        
        if(deltaX < 0)
            angle_1 += Math.PI;

        deltaX = node_t.lat - node_d.lat;
        deltaY = node_t.lon - node_d.lon;
        
        
        double angle_2 = Math.atan(deltaY/deltaX);
        
        if(deltaX < 0)
            angle_2 += Math.PI;
        
        double dif = Math.abs(angle_1-angle_2);

        while(dif > Math.PI)
        {
            dif -= Math.PI;
        }
        
        /*boolean stop = false;
        if(node_i.id == 105)
        {
            System.out.println("i: " + node_i.id + " t: " + node_t.id + " dif " + dif);
            stop = true;
        }
        if(stop)
            System.exit(0);*/

        return dif;
    }

    public void nodes_route(int initial_n[],int transition_n[], int dest_n, Node nodes[]) //Aggression parameters for assigningnodes
    {

        Node node_d = getNode(dest_n, nodes);
        
        
        for(int x = 0; x < transition_n.length; x++)
        {
            Node node_t = getNode(transition_n[x],nodes);
            //double[] angleDiff = new angleDiff[initial_n.length];   //Angle difference from this transition nodes to all initial nodes
            //float [] costs     = new angleDiff[initial_n.length];   //Cost from this transition nodes to all initial nodess
            
            int smallCostID  = initial_n[0];
            int smallAngleID = initial_n[0];
            
            Node node_i_0 = getNode(initial_n[0], nodes);
            float smallestCost = getCost(transition_n[x], node_i_0);
            double smallAngle   = getAngle(node_i_0, node_d, node_t);
            for(int y = 0; y < initial_n.length; y++)
            {
                Node node_i         = getNode(initial_n[y],nodes);
                float currentCost   = getCost(transition_n[x], node_i);
                double currentAngle = getAngle(node_i, node_d, node_t);
                
                if(currentCost<smallestCost)
                {
                    smallestCost = currentCost;
                    smallCostID  = initial_n[y];
                    
                }
                if(currentAngle < smallAngle)
                {
                    smallAngle   = currentAngle;
                    smallAngleID = initial_n[y];
                }
            }
            //System.out.println("C: " + smallCostID + " - " + " A: " + smallAngleID);
            addToRoute(smallCostID, getNode(transition_n[x], nodes));
            if(smallCostID != smallAngleID)
            {
                addToRoute(smallAngleID, getNode(transition_n[x], nodes));
            }
        }
    }
}
