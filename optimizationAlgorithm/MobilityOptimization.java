package optimizationAlgorithm;

//Engine
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;

//Classes
import io.jenetics.util.Factory;
import io.jenetics.Optimize;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.BitGene;
import io.jenetics.BitChromosome;
import io.jenetics.EnumGene;
import io.jenetics.Chromosome;

//Selectors, alterers, mutators & scalers
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Alterer;
import io.jenetics.SwapMutator;
import io.jenetics.ExponentialRankSelector;

//Utilities
import io.jenetics.util.ISeq;

//Other
import java.util.ArrayList;
import java.util.HashMap;
import java.text.DecimalFormat;
/*import java.time.Duration;*/

public class MobilityOptimization
{   
    // Populations for route and ownership genotypes
    private static ISeq<Genotype<EnumGene<Integer>>> routeGenotypes;
    private static ISeq<Genotype<BitGene>> ownGenotypes;
    
    //Best genotypes of each kind find for the algotihm
    public static Genotype<EnumGene<Integer>> bestRoute;
    public static Genotype<BitGene> bestOwn;
    public static double minCost = 999999999.0;
    
    public static Node[] nodes;         //Nodes array
    public static Overlap[] overlaps;   //Overlaps array
    public static int[] startNodes;     // StartNodesIDs
    public static int destNode;         // DestNodeID
    
    public static Routes routes;        //Variable for storign Routes
    
    
    //1st index = routeID
    //2nd index = nodesOfEachRoute
    public static int[][] routeMap;                         // 2D array for mapping natural integers - 0,1,... to ID values for each of the routes
    public static Node[][] nodesPerRoute;                   // 2D array of assignable nodes per route
    
    public static HashMap <Integer, Integer> ownMapping;     // Map from nodeID to index for bit genotype
    
    //Function for evaluating route permutation genotypes (Permutation Chromosomes)
    private static double evalRoutePerm (Genotype<EnumGene<Integer>> routeGenotype) 
    {   
        //Start evaluating after ownGenotypes population has been created
        if(ownGenotypes == null)
            return 0;
            
        double cost = 0;
        //Calculate cost of current routeGenotype by evaluating it together with each of the ownGenotypes
        for( Genotype<BitGene> ownGenotype :  ownGenotypes)
        {
            cost += GenotypeCost.calculate(routeGenotype, ownGenotype);
        }
        return cost;
    }
    
    //Function for evaluating ownership genotypes (Bit Chromosomes)
    private static double evalOwnChange (Genotype<BitGene> ownGenotype) 
    {
        //Start evaluating after routeGenotypes population has been created
        if(routeGenotypes == null)
            return 0;
        
        double cost = 0;
        //Calculate cost of current ownGenotype by evaluating it together with each of the routeGenotypes
        for( Genotype<EnumGene<Integer>> routeGenotype :  routeGenotypes){
            cost += GenotypeCost.calculate(routeGenotype, ownGenotype);
        }
        return cost;
    }
    

    ////////////////////////////////////////////////////////////////////MAIN GA FUNCTION//////////////////////////////////////////////////////////////////
    public static void run (int[] initialNodes, int finalNode, int[] transitionNodes , int generationSize, int numIterations, float crossProbability, float mutateProbability, float overlapAggressiveness) 
    {
        /*
            TODO balancing of assignable nodes to route
            TODO initialization for route genotypes
            TODO improve GenotypeCost efficiency (getCost function from Route Class)
            TODO set convergence criteria
        */
        
        // SETUP GA variables
        nodes = DatabaseConnection.nodes_matrix(initialNodes, finalNode, transitionNodes);

        
        //Set start and dest nodes info
        startNodes = initialNodes;
        destNode   = finalNode;
        
        //Get routes info
        routes = new Routes(startNodes);
        routes.nodes_route(startNodes, transitionNodes, destNode, nodes, overlapAggressiveness);
        routeMap = new int[routes.routes.length][];
        ArrayList<Chromosome<EnumGene<Integer>>> routeChromosomes = new ArrayList<Chromosome<EnumGene<Integer>>>();
        nodesPerRoute = new Node[routes.routes.length][];
        
        for(int x = 0; x < routes.routes.length; x++)
        {   
            nodesPerRoute[x] = new Node[routes.routes[x].hash_Set.size()];
            routeMap[x] = new int[routes.routes[x].hash_Set.size()];
            int i = 0;
            for (Node s : routes.routes[x].hash_Set) 
            {
                routeMap[x][i] = s.id;
                nodesPerRoute[x][i] = routes.getNode(s.id, nodes);
                i++;
            }
            routeChromosomes.add(RouteChromosome.ofInteger(0,i,x));
        }
        
        //Get overlap info
        SetOverlaps _overlaps = new SetOverlaps();
        overlaps = _overlaps.getOverlaps(routes,startNodes,nodes);
        OwnChromosome.probsArray = getProbabilityArray(overlaps);
        ownMapping = new HashMap<Integer, Integer>();
        for (int i=0;i<overlaps.length;i++)
        {
            ownMapping.put(overlaps[i].overlap_node, i);
        }
        
        
        
        // Initialize both genotype factories//
        final Factory<Genotype<EnumGene<Integer>>> routePermFactory = Genotype.of(routeChromosomes);
        final Factory<Genotype<BitGene>> ownChangeFactory = Genotype.of (OwnChromosome.of(OwnChromosome.probsArray)); 

        //Build both optimization engines//                                       
        Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory).populationSize(generationSize).optimize(Optimize.MINIMUM).build();
        Engine<BitGene, Double> engineOwn = Engine.builder(MobilityOptimization::evalOwnChange, ownChangeFactory).optimize(Optimize.MINIMUM).build();
        
        
        //Execute alternate iterations of genetic algorithms
        for(int i=0; i<numIterations; i++)
        {
              //System.out.println("i: " + i);
              routeGenotypes = engineRoute.stream().limit(1).collect(EvolutionResult.toBestEvolutionResult()).getGenotypes();
              ownGenotypes   = engineOwn.stream().limit(1).collect(EvolutionResult.toBestEvolutionResult()).getGenotypes();
        }
        
        //Display Results
        double bestGenotypeCost = GenotypeCost.calculate(bestRoute, bestOwn);
        System.out.println("--------");
        System.out.println("Solution");
        System.out.println("--------");
        System.out.println();
        System.out.println("Route Genotype: ");
        System.out.println(getRouteResult(bestRoute) + "\n");
        System.out.println("Ownership Genotype:");
        System.out.println(bestOwn + "\n");

        System.out.println("-----------");
        System.out.println("Route Stats");
        System.out.println("-----------");
    
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double routeStats[] = new double[3];
        double totalTime = 0;
        double totalDistance = 0;
        for (int i=0;i<startNodes.length;i++)
        {
            routeStats = RouteResults.getRouteStats(i, bestRoute.getChromosome(i), bestOwn);
            totalTime += routeStats[0];
            totalDistance += routeStats[1];
            System.out.println("R" + i + " Time: " + decimalFormat.format(routeStats[0]/3600) + "hrs,   Distance: " + decimalFormat.format(routeStats[1]/1000) + "kms,   Cost: " + decimalFormat.format(routeStats[2]));
        }

        System.out.println();
        System.out.println("Total Time: " + decimalFormat.format(totalTime/3600) + " hrs");
        System.out.println("Total Distance: " + decimalFormat.format(totalDistance/1000) + " kms");
        System.out.println("Total Cost: " + decimalFormat.format(bestGenotypeCost));
        System.out.println();
    }
    
    
    ///////////////////////////////////////////HELPER FUNCTIONS////////////////////////////////////////////
    //Function for getting probability array out of all overlaps
    private static double[] getProbabilityArray(Overlap[] overlaps)
    {
        double[] probs = new double[overlaps.length];
        for (int i=0; i<overlaps.length;i++){
            probs[i] = overlaps[i].probability;
        }
        return probs;
    }
    
    
    //Function for printing Route genotype
    private static String getRouteResult(Genotype<EnumGene<Integer>> genotype)
    {
        String result = "";
        for (int i=0;i<genotype.length();i++)
        {
            Chromosome<EnumGene<Integer>> chromosome = genotype.getChromosome(i);
            for (int j=0;j<chromosome.length();j++)
            {
                int index  = chromosome.getGene(j).getAllele();
                int nodeID = routeMap[i][index]; 
                result += Integer.toString(nodeID);
                if(j<chromosome.length()-1)
                    result+="|";
            }
            result+="\n";
        }
        return result;
    }
    
    //Print result taking ownerships into account
    /*private static String getRouteResult(Genotype<EnumGene<Integer>> routeGenotype, Genotype<EnumGene<Integer>> ownGenotype)
    {
        String result = "";
        
        for (int i=0;i<genotype.length();i++)
        {
            Chromosome<EnumGene<Integer>> chromosome = genotype.getChromosome(i);
            for (int j=0;j<chromosome.length();j++)
            {
                int index  = chromosome.getGene(j).getAllele();
                int nodeID = routeMap[i][index]; 
                
                //if(ownGenotype)
                //{
                    result += Integer.toString(nodeID);
                    if(j<chromosome.length()-1)
                        result+="|";
                //}
            }
            result+="\n";
        }
        return result;
    }*/
    
}
/*Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory)
                                            .populationSize(generationSize)
                                            .alterers(new PartiallyMatchedCrossover<>(crossProbability), new SwapMutator<>(mutateProbability))
                                            .survivorsSelector(new ExponentialRankSelector<>())
                                            .optimize(Optimize.MINIMUM).build();*/

