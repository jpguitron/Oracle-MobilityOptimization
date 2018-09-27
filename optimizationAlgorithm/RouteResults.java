package optimizationAlgorithm;

import io.jenetics.Genotype;
import io.jenetics.Chromosome;
import io.jenetics.BitGene;
import io.jenetics.EnumGene;

public class RouteResults
{
    public static double[] getRouteStats(int routeIndex,Chromosome<EnumGene<Integer>> routeChromosome, Genotype<BitGene> ownGenotype)
    {
        double[] stats = new double[3];     //time, distance, cost
        double time = 0;
        double dist = 0;
        double cost = 0;
                     
        int routeID         = MobilityOptimization.startNodes[routeIndex];
        int current_node_id = routeID; 
        for(int j=0; j<routeChromosome.length(); j++)                                            // Loop through all transition nodes for the current route
        {
            boolean addCost  = true;                                                        // Boolean to determine if a cost should be added for this transition
            int next_node_index = routeChromosome.getGene(j).getAllele();                        // Index of possible node to visit
            int next_node_id = MobilityOptimization.routeMap[routeIndex][next_node_index];        // Get next_node_id from map
            for (int k=0;k<MobilityOptimization.overlaps.length;k++)                        // Loop to check if current node has overlap
            {
                Overlap c_overlap = MobilityOptimization.overlaps[k];
                if(c_overlap.overlap_node == next_node_id)
                {
                    // If node1 and bit == 0  OR node2 and bit == 1 dont add any cost (its added in the other route)
                    if(routeID == c_overlap.route_node_1)
                    {
                        if(ownGenotype.getChromosome().getGene().getBit())
                            break;

                        else
                        {
                            addCost = false;
                            break;
                        }
                    }
                    else
                    {
                        if(ownGenotype.getChromosome().getGene().getBit())
                        {
                            addCost = false;
                            break;
                        }

                        else
                            break;
                    }
                }
            }
            if(addCost)
            {
                double[] nodestats = GenotypeCost.getStats(current_node_id, next_node_id);
                cost += GenotypeCost.getCost(current_node_id, next_node_id);
                time += nodestats[0];
                dist += nodestats[1];
                current_node_id = next_node_id;
            }
        }
        
        //Add cost to last node manually
        for(Node node : MobilityOptimization.nodes)
        {
            if(node.id == current_node_id)
            {
                cost += node.destEdge.cost;
                time += node.destEdge.time_cost;
                dist += node.destEdge.dist_cost;
                break;
            }
        }
        stats[0] = time;
        stats[1] = dist;
        stats[2] = cost;
        return stats;
    }
}
