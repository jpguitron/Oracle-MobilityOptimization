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
    //public static int[][] nodesPerRoute;                   // 2D array of assignable nodes per route
    
    public static HashMap <Integer, Integer> ownMapping;     // Map from nodeID to index for bit genotype
    public static HashMap <Integer, Overlap> overlapMapping; // Map from nodeID to Overlap object
    public static HashMap <Integer, Node>    nodeMapping;    // Map from nodeID to Node object
    
    
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
    public static void run (int[] initialNodes, int finalNode, int[] transitionNodes , int generationSize, int numIterations, float crossProbability, float mutateProbability) 
    {
        /*
            TODO balancing of assignable nodes to route
            TODO RouteChromosome newInstance method
            TODO improve GenotypeCost efficiency (getCost function from Route Class)
            TODO set convergence criteria
        */
        
        // Take execution time
        double startTime =  System.currentTimeMillis();
        
        // SETUP GA variables
        nodes = DatabaseConnection.nodes_matrix(initialNodes, finalNode, transitionNodes);

        //Node info
        nodeMapping = DatabaseConnection.nodes_matrix_hashMap(initialNodes, finalNode, transitionNodes);
        
        //Set start and dest nodes info
        startNodes = initialNodes;
        destNode   = finalNode;
        
        //Get routes info
        routes = new Routes(startNodes);
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
            routeChromosomes.add(RouteChromosome.ofInteger(0,i,x));
        }
        
        //Get overlap info
        SetOverlaps _overlaps    = new SetOverlaps();
        overlaps                 = _overlaps.getOverlaps(routes,startNodes,nodes);
        OwnChromosome.probsArray = getProbabilityArray(overlaps);
        ownMapping               = new HashMap<Integer, Integer>();
        overlapMapping           = new HashMap<Integer, Overlap>();
        for (int i=0;i<overlaps.length;i++)
        {
            ownMapping.put(overlaps[i].overlap_node, i);
            overlapMapping.put(overlaps[i].overlap_node, overlaps[i]);
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
        System.out.println(getStringResult(bestRoute, bestOwn));

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
        System.out.println("Execution Time: " + (System.currentTimeMillis()-startTime)/1000 + " seconds");
        
        //Print JSON to show results//
        /*System.out.println("Results");
        System.out.println(getJsonResult(bestRoute, bestOwn));
        
        
        
        //Print JSON for initial routes// 
        System.out.println("Samples");
        Genotype<EnumGene<Integer>> sampleRoute = routePermFactory.newInstance();
        Genotype<BitGene> sampleOwn = ownChangeFactory.newInstance();
        System.out.println(getJsonResult(sampleRoute, sampleOwn));*/
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
    private static String getStringResult(Genotype<EnumGene<Integer>> routeGenotype, Genotype<BitGene> ownGenotype)
    {
        String result = "";
        
        for (int i=0;i<routeGenotype.length();i++)
        {
            result+=Integer.toString(startNodes[i]) + "|";
            Chromosome<EnumGene<Integer>> chromosome = routeGenotype.getChromosome(i);
            for (int j=0;j<chromosome.length();j++)
            {
                int index  = chromosome.getGene(j).getAllele();
                int nodeID = routeMap[i][index]; 
                    
                if(ownMapping.containsKey(nodeID))
                {
                    int ownGenotypeIndex = ownMapping.get(nodeID);
                    
                    // If node1 and bit == 1  OR node2 and bit == 0 add node to this route
                    if(startNodes[i] == overlapMapping.get(nodeID).route_node_1)
                    {
                        if(ownGenotype.getChromosome().getGene(ownGenotypeIndex).getAllele())
                        {
                            result += Integer.toString(nodeID);
                            result+="|";
                        }
                    }
                    else if(!ownGenotype.getChromosome().getGene(ownGenotypeIndex).getAllele())
                    {
                            result += Integer.toString(nodeID);
                            result+="|";
                    }
                }
                else
                {
                    result += Integer.toString(nodeID);
                    result+="|";
                }
            }
            result+=Integer.toString(destNode);
            result+="\n";
        }
        return result;
    }
    
    
    public static String getJsonResult(Genotype<EnumGene<Integer>> routeGenotype, Genotype<BitGene> ownGenotype)
    {
        String result ="";
        
        for (int i=0;i<routeGenotype.length();i++)
        {
            result+= "[\n[" + Integer.toString(startNodes[i]);
            result+=",";
            result+= nodeMapping.get(startNodes[i]).lat;
            result+=",";
            result+= nodeMapping.get(startNodes[i]).lon;
            result += "],\n";
            
            
            Chromosome<EnumGene<Integer>> chromosome = routeGenotype.getChromosome(i);
            for (int j=0;j<chromosome.length();j++)
            {
                int index  = chromosome.getGene(j).getAllele();
                int nodeID = routeMap[i][index]; 
                    
                if(ownMapping.containsKey(nodeID))
                {
                    int ownGenotypeIndex = ownMapping.get(nodeID);
                    
                    // If node1 and bit == 1  OR node2 and bit == 0 add node to this route
                    if(startNodes[i] == overlapMapping.get(nodeID).route_node_1)
                    {
                        if(ownGenotype.getChromosome().getGene(ownGenotypeIndex).getAllele())
                        {
                            result += "[";
                            result += Integer.toString(nodeID);
                            result+=",";
                            result+= nodeMapping.get(nodeID).lat;
                            result+=",";
                            result+= nodeMapping.get(nodeID).lon;
                            result += "],\n";
                        }
                    }
                    else if(!ownGenotype.getChromosome().getGene(ownGenotypeIndex).getAllele())
                    {
                            result += "[";
                            result += Integer.toString(nodeID);
                            result+=",";
                            result+= nodeMapping.get(nodeID).lat;
                            result+=",";
                            result+= nodeMapping.get(nodeID).lon;
                            result += "],\n";
                    }
                }
                else
                {
                    result += "[";
                    result += Integer.toString(nodeID);
                    result+=",";
                    result+= nodeMapping.get(nodeID).lat;
                    result+=",";
                    result+= nodeMapping.get(nodeID).lon;
                    result += "],\n";
                }
            }
            result+="[" + Integer.toString(destNode);
            result+=",";
            result+= nodeMapping.get(destNode).lat;
            result+=",";
            result+= nodeMapping.get(destNode).lon;
            result += "]\n";
            result+="]\n";
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

