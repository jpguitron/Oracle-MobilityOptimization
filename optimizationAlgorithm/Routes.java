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
        float deltaX = node_i.lat - node_d.lat;
        float deltaY = node_i.lon - node_d.lon;

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

        return dif;
    }

    public void nodes_route(int initial_n[],int transition_n[], int dest_n, Node nodes[], float aggression) //Aggression parameters for assigningnodes
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
            double smallAngle   = getAngle(node_d, node_i_0, node_t);
            
            for(int y = 0; y < initial_n.length; y++)
            {
                Node node_i         = getNode(initial_n[y],nodes);
                float currentCost   = getCost(transition_n[x], node_i);
                double currentAngle = getAngle(node_d, node_i, node_t);
                
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
            addToRoute(smallCostID, getNode(transition_n[x], nodes));
            if(smallCostID != smallAngleID)
            {
                addToRoute(smallAngleID, getNode(transition_n[x], nodes));
            }
        }
        
        /*
        for(int x = 0; x < transition_n.length; x++)
        {
            int smallestId = initial_n[0];
            int secondSmallestId = -1; 
            float smallestCost = getCost(transition_n[x], getNode(initial_n[0], nodes));
            float secondSmallestCost = -1;
            float proportion = 0;
            int smallestAngleID = initial_n[0];
            
            for(int y = 0; y < initial_n.length; y++)
            {
                Node node = getNode(initial_n[y],nodes);
                float currentCost = getCost(transition_n[x], node);
                
                if(smallestCost > currentCost || secondSmallestCost ==-1)
                {
                    secondSmallestCost = smallestCost;
                    secondSmallestId = smallestId;

                    smallestCost = currentCost;
                    smallestId = initial_n[y];
                    
                    proportion = secondSmallestCost/smallestCost;
                }
            }

            addToRoute(smallestId, getNode(transition_n[x], nodes));
            
            // Add if the node 
            if(proportion <= aggression)
            {
                addToRoute(secondSmallestId, getNode(transition_n[x], nodes));
            }
            
        }*/
    }
}
