package optimizationAlgorithm;

class edge
{
    int dest;
    float time_cost;
    float dist_cost;
    float cost;
    public edge()
    {
        dest = 0;
        time_cost = 0;
        dist_cost = 0;
        cost = 0;
    }
    public void setEdge(int d, float t_c, float d_c)
    {
        dest = d;
        time_cost = t_c;
        dist_cost = d_c;
        cost = time_cost + dist_cost/100;
    }
}

public class Node 
{
    public float lat;
    public float lon;
    public int id;
    public HashMap<Integer, edge> hmap = new HashMap<Integer, edge>();
    public int IEdgeSize;

    public Node() 
    {
        lat = 0;
        lon = 0; 
        IEdgeSize = 0;
        TEdgeSize = 0;
    }

    public Node(float lt, float ln, int nodes, int INodes) 
    {
        lat = lt;
        lon = ln; 
        IEdgeSize = 0;
        TEdgeSize = 0;
    }

    public void initalizeEdges(int ISize,int TSize ) 
    {

    }

    public void addEdge(int dest, float time_cost, float dist_cost)
    {   
        
        if(IEdgeSize < initialEdges.length)
        {

        }
        
    }
}
