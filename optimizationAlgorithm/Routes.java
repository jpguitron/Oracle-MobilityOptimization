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

    private Node getNode(int id, Node nodes[])
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

    public void nodes_route(int initial_n[],int transition_n[], int dest_n, Node nodes[], float aggression) //Aggression parameters for assigningnodes
    {
        for(int x = 0; x < transition_n.length; x++)
        {
            int smallestId = initial_n[0];
            int secondSmallestId = -1; 
            float smallestCost = getCost(transition_n[x], getNode(initial_n[0], nodes));
            float secondSmallestCost = -1;
            float proportion = 0;
            
            for(int y = 0; y < initial_n.length; y++)
            {
                Node node = getNode(initial_n[y],nodes);

                if(smallestCost > getCost(transition_n[x], node) || secondSmallestCost ==-1)
                {
                    secondSmallestCost = smallestCost;
                    secondSmallestId = smallestId;

                    smallestCost = getCost(transition_n[x], node);
                    smallestId = initial_n[y];
                    
                    proportion = secondSmallestCost/smallestCost;
                }
            }

            addToRoute(smallestId, getNode(transition_n[x], nodes));
            if(proportion <= aggression)
            {
                addToRoute(secondSmallestId, getNode(transition_n[x], nodes));
            }
            
        }
    }
}
