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
    
    //2D array for mapping natural integers - 0,1,... to ID values for each of the routes
    //1st index = routeID
    //2nd index = nodesOfEachRoute
    public static int[][] routeMap;
    
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
            TODO create custom chromosomes for route initialization
            TODO set convergence criteria
            TODO get overlap information from DB
            TODO store best genotype information
            TODO better initialization for initial cost
            TODO show results in terms of time and distance
        */
        
        // SETUP GA variables
        nodes = DatabaseConnection.nodes_matrix(initialNodes, finalNode, transitionNodes);
    
        overlaps = new Overlap[3];
        overlaps[0] = new Overlap(7, 104, 105, 0.8);
        overlaps[1] = new Overlap(4, 103, 104, 0.4);
        overlaps[2] = new Overlap(5, 105, 102, 0.2);
        
        
        //Set default parameter values for custom chromosomes
        RouteChromosome.defaultAgg = overlapAggressiveness;
        OwnChromosome.probsArray = getProbabilityArray(overlaps);
        
        //Set start and dest nodes info
        startNodes = initialNodes;
        destNode   = finalNode;
        
        //Get routes info
        Routes routes = new Routes(startNodes);
        routes.nodes_route(startNodes, transitionNodes, destNode, nodes);
        routeMap = new int[routes.routes.length][];
        ArrayList<Chromosome<EnumGene<Integer>>> routeChromosomes = new ArrayList<Chromosome<EnumGene<Integer>>>();
        for(int x = 0; x < routes.routes.length; x++)
        {   
            routeMap[x] = new int[routes.routes[x].hash_Set.size()];
            int i = 0;
            for (Node s : routes.routes[x].hash_Set) 
            {
                routeMap[x][i] = s.id;
                i++;
            }
            routeChromosomes.add(RouteChromosome.ofInteger(0,i,RouteChromosome.defaultAgg));
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
        System.out.println("R1: " + " Time: " + " Distance: " + " Cost");

        System.out.println();
        System.out.println("Total Time: ");
        System.out.println("Total Distance: ");
        System.out.println("Total Cost: ");
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
    
    
    
    
    
    
    
}
/*Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory)
                                            .populationSize(generationSize)
                                            .alterers(new PartiallyMatchedCrossover<>(crossProbability), new SwapMutator<>(mutateProbability))
                                            .survivorsSelector(new ExponentialRankSelector<>())
                                            .optimize(Optimize.MINIMUM).build();*/

