package optimizationAlgorithm;

import java.util.Set;
import java.util.HashSet;

class SetOverlaps
{

    public Overlap overlaps[];

    private float getCost(int id, Node node) 
    {
        for (int x = 0; x < node.TEdgeSize; x++) 
        {
            if (node.transitionEdges[x].dest == id) 
            {
                return node.transitionEdges[x].cost;
            }
        }
        return 0;
    }

    public float getProbability(Node node_1, Node node_2, Node transition_node) 
    {
        float cost_1 = getCost(transition_node.id, node_1);
        float cost_2 = getCost(transition_node.id, node_2);
        float prob = 1 - (cost_1 / (cost_1 + cost_2));
        return prob;
    }

    public Overlap[] getOverlaps(Routes rutas, int initial_n[], Node nodes[]) 
    {

        Node idx, idy;
        int overlapsSize = 0;
        int actualOverlap = 0;
        float prob;
        for (int x = 0; x < rutas.routes.length; x++) 
        {

            for (int y = x + 1; y < rutas.routes.length; y++) 
            {
                Set<Node> OverlapA = new HashSet<Node>(rutas.routes[x].hash_Set);
                Set<Node> OverlapB = new HashSet<Node>(rutas.routes[y].hash_Set);

                OverlapA.retainAll(OverlapB);
                overlapsSize += OverlapA.size();
            }
        }
        overlaps = new Overlap[overlapsSize];
        for (int x = 0; x < rutas.routes.length; x++) 
        {

            for (int y = x + 1; y < rutas.routes.length; y++) {
                Set<Node> OverlapA = new HashSet<Node>(rutas.routes[x].hash_Set);
                Set<Node> OverlapB = new HashSet<Node>(rutas.routes[y].hash_Set);

                OverlapA.retainAll(OverlapB);

                if (OverlapA.size() > 0) 
                {
                    idx = getNode(initial_n[x], nodes);
                    idy = getNode(initial_n[y], nodes);
                    for (Node s : OverlapA) 
                    {
                        prob = getProbability(idx, idy, s);
                        Overlap ov = new Overlap(s.id, idx.id, idy.id, prob);
                        overlaps[actualOverlap] = ov;
                        actualOverlap++;
                    } 
                }
            }
        }
        return overlaps;

    }

    private Node getNode(int id, Node nodes[]) 
    {
        for (int x = 0; x < nodes.length; x++) 
        {
            if (nodes[x].id == id) 
            {
                return nodes[x];
            }
        }
        return null;
    }

}
