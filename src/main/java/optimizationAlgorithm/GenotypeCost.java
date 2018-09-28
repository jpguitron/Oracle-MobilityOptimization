package optimizationAlgorithm;

import io.jenetics.Genotype;
import io.jenetics.BitGene;
import io.jenetics.EnumGene;

public class GenotypeCost
{
    //Main function for calculating genotype cost//
    public static double calculate(Genotype<EnumGene<Integer>> routeGenotype, Genotype<BitGene> ownGenotype)
    {
        double cost = 0;
        for (int i=0; i<MobilityOptimization.startNodes.length; i++)                         // Loop through all routes
        {
            int routeID         = MobilityOptimization.startNodes[i];
            int current_node_id = routeID; 
            for(int j=0; j<routeGenotype.getChromosome(i).length(); j++)                     // Loop through all transition nodes for the current route
            {
                boolean addCost  = true;                                                     // Boolean to determine if a cost should be added for this transition
                int next_node_index = routeGenotype.getChromosome(i).getGene(j).getAllele(); // Index of possible node to visit
                int next_node_id = MobilityOptimization.routeMap[i][next_node_index];        // Get next_node_id from map
                
                for (int k=0;k<MobilityOptimization.overlaps.length;k++)                     // Loop to check if current node has overlap
                { 
                    Overlap c_overlap = MobilityOptimization.overlaps[k];
                    if(c_overlap.overlap_node == next_node_id)
                    {
                        // If node1 and bit == 0  OR node2 and bit == 1 dont add any cost (its added in the other route)
                        if(routeID == c_overlap.route_node_1)
                        {
                            int ownGenotypeIndex = MobilityOptimization.ownMapping.get(c_overlap.overlap_node);
                            if(ownGenotype.getChromosome().getGene(ownGenotypeIndex).getBit())
                                break;

                            else
                            {
                                addCost = false;
                                break;
                            }
                        }
                        else
                        {
                            int ownGenotypeIndex = MobilityOptimization.ownMapping.get(c_overlap.overlap_node);
                            if(ownGenotype.getChromosome().getGene(ownGenotypeIndex).getBit())
                            {
                                addCost = false;
                                break;
                            }

                            else
                                break;
                        }
                    }
                }
                
                //System.out.print("Add: " + addCost + " ");
                if(addCost)
                {
                    cost += getCost(current_node_id, next_node_id);
                    //System.out.println(current_node_id + " - " + next_node_id + " C " + cost);
                    current_node_id = next_node_id;
                    
                }
                else
                {
                    //System.out.println(current_node_id + " - " + next_node_id);
                }
                //System.out.println("\n");
            }

            //Add cost to last node manually
            for(Node node : MobilityOptimization.nodes)
            {
                if(node.id == current_node_id)
                {
                    //System.out.println("CF: " + node.destEdge.cost);
                    // if current node is a starter node add a very big cost to avoid this situation
                    if (current_node_id == routeID)
                        cost += 999999999;
                    
                    else
                        cost += node.destEdge.cost;
                    break;
                }
            }
        }

        if(cost < MobilityOptimization.minCost)
        {
            MobilityOptimization.minCost   = cost;
            MobilityOptimization.bestRoute = routeGenotype;
            MobilityOptimization.bestOwn   = ownGenotype;
        }
        //System.exit(0);
        return cost;
    }
    
    /*
    TODO improve this
    */
    //Helper function for adding cost between to nodes//
    public static double getCost(int c_node, int n_node)
    {
        for(Node node : MobilityOptimization.nodes)
        {
            if(node.id == c_node)
            {
                for (int k=0;k<node.TEdgeSize; k++)
                {
                    edge _edge = node.transitionEdges[k];
                    if(_edge.dest == n_node)
                    {
                        double cost = _edge.cost;
                        return cost;
                    }
                }
            }
        }
        return 0.0;
    }
    
    //Helper function for getting time and distance between two nodes//
    public static double[] getStats(int c_node, int n_node)
    {
        double[] stats = new double[2];
        for(Node node : MobilityOptimization.nodes)
        {
            if(node.id == c_node)
            {
                for (int k=0;k<node.TEdgeSize; k++)
                {
                    edge _edge = node.transitionEdges[k];
                    if(_edge.dest == n_node)
                    {
                        stats[0] = _edge.time_cost;
                        stats[1] = _edge.dist_cost;
                        return stats;
                    }
                }
            }
        }
        return stats;
    }
}
