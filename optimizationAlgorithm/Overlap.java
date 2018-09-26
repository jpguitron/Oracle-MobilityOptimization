package optimizationAlgorithm;

import java.util.Set;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Node;

import optimizationAlgorithm.*;

class Overlap {
    public int overlap_node;
    public int route_node_1;
    public int route_node_2;
    public double probability;

    public Overlap(int o, int r1, int r2, double p) {
        overlap_node = o;
        route_node_1 = r1;
        route_node_1 = r2;
        probability = p;
    }
}

class setOverlap {

    public Overlap overlaps[];

    private float getCost(int id, Node node) {
        for (int x = 0; x < node.TEdgeSize; x++) {
            if (node.transitionEdges[x].dest == id) {
                return node.transitionEdges[x].cost;
            }
        }
        return 0;
    }

    public float getProbability(Node node_1, Node node_2, Node transition_node) {
        cost_1 = getCost(transition_node.id, node_1);
        cost_2 = getCost(transition_node.id, node_2);
        prob = 1 - (cost_1 / (cost_1 + cost_2));
        return prob;
    }

    public Overlap[] getOverlaps(Routes rutas, int initial_n[], Node nodes[]) {

        Node idx, idy;
        int overlapsSize = 0;
        int actualOverlap = 0;
        float prob;
        for (int x = 0; x < rutas.routes.length; x++) {

            for (int y = x + 1; y < rutas.routes.length; y++) {
                Set<Node> OverlapA = new HashSet<Node>(rutas.routes[x].hash_Set);
                Set<Node> OverlapB = new HashSet<Node>(rutas.routes[y].hash_Set);

                OverlapA.retainAll(OverlapB);
                overlapsSize += OverlapA.size();
            }
        }
        overlaps = new Overlap[overlapsSize];
        for (int x = 0; x < rutas.routes.length; x++) {
            // System.out.print("Route id: " + rutas.routes[x].routeName + " : ");

            for (int y = x + 1; y < rutas.routes.length; y++) {
                Set<Node> OverlapA = new HashSet<Node>(rutas.routes[x].hash_Set);
                Set<Node> OverlapB = new HashSet<Node>(rutas.routes[y].hash_Set);

                OverlapA.retainAll(OverlapB);

                if (OverlapA.size() > 0) {
                    idx = getNode(initial_n[x], nodes);
                    idy = getNode(initial_n[y], nodes);
                    for (Node s : OverlapA) {
                        prob = getProbability(idx, idy, s);
                        Overlap ov = new Overlap(s.id, idx.id, idy.id, prob);
                        overlaps[actualOverlap] = ov;
                    } // System.out.println();
                }
            }
        }

    }

    private Node getNode(int id, Node nodes[]) {
        for (int x = 0; x < nodes.length; x++) {
            if (nodes[x].id == id) {
                return nodes[x];
            }
        }
        return null;
    }

}
