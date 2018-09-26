package optimizationAlgorithm;

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
        route_node_2= r2;
        probability = p;
    }
}
