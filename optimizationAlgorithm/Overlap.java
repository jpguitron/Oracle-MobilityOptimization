package optimizationAlgorithm;

import optimizationAlgorithm.*;

class Overlap
{
    public int overlap_node;
    public int route_node_1;
    public int route_node_2;
    public double probability;
    
    public Overlap(int o, int r1, int r2, double p)
    {
        overlap_node = o;
        route_node_1 = r1;
        route_node_1= r2;
        probability = p;
    }
}

class setOverlap
{

    public Overlap overlaps[];

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
    
    public float getProbability(Node node_1, Node node_2, Node transition_node)
    {
        cost_1 = getCost(transition_node.id, node_1);
        cost_2 = getCost(transition_node.id, node_2);
        prob = 1-(cost_1/(cost_1+cost_2));
        return prob;
    }
}
