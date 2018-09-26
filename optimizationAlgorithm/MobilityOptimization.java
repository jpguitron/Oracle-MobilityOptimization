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

//Selectors, alterers, mutators & scalers
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.Alterer;
import io.jenetics.SwapMutator;
import io.jenetics.ExponentialRankSelector;

//Utilities
import io.jenetics.util.ISeq;
//Other
/*import java.util.ArrayList;
import java.time.Duration;*/

public class MobilityOptimization
{   
    // Populations for route and ownership genotypes
    private static ISeq<Genotype<EnumGene<Integer>>> routeGenotypes;
    private static ISeq<Genotype<BitGene>> ownGenotypes;
    
    
    public static Node[] nodes;         //Nodes array
    public static Overlap[] overlaps;   //Overlaps array
    public static int[] startNodes;     // StartNodesIDs
    public static int destNode;         // DestNodeID
    
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
        // Get variables required for GA//
        nodes = DatabaseConnection.nodes_matrix(initialNodes, finalNode, transitionNodes);
    
        overlaps = new Overlap[3];
        overlaps[0] = new Overlap(7, 104, 105, 0.8);
        overlaps[1] = new Overlap(4, 103, 104, 0.4);
        overlaps[2] = new Overlap(5, 105, 102, 0.2);
        
        
        //Set default parameter values for custom chromosomes
        RouteChromosome.defaultAgg = overlapAggressiveness;
        OwnChromosome.probsArray = getProbabilityArray(overlaps);
        
        startNodes = initialNodes;
        destNode   = finalNode;
        
        
        
        /*
        TODO create chromosomes with parameters given from previous functions
        TODO create custom chromosomes for initialization
        TODO set convergence criteria
        */
        
        
        
        
        // Initialize both genotype factories//
        final Factory<Genotype<EnumGene<Integer>>> routePermFactory = Genotype.of(
                                                                        RouteChromosome.ofInteger(0,5,RouteChromosome.defaultAgg),
                                                                        RouteChromosome.ofInteger(0,6, RouteChromosome.defaultAgg),
                                                                        RouteChromosome.ofInteger(0,7,RouteChromosome.defaultAgg),
                                                                        RouteChromosome.ofInteger(0,8, RouteChromosome.defaultAgg),
                                                                        RouteChromosome.ofInteger(0,9, RouteChromosome.defaultAgg)
                                                                        ); 
        
        
        final Factory<Genotype<BitGene>> ownChangeFactory = Genotype.of (OwnChromosome.of(OwnChromosome.probsArray)); 

        //Build both optimization engines//                                       
        Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory).populationSize(generationSize).optimize(Optimize.MINIMUM).build();
        Engine<BitGene, Double> engineOwn = Engine.builder(MobilityOptimization::evalOwnChange, ownChangeFactory).optimize(Optimize.MINIMUM).build();
        
        
        //Execute alternate iterations of genetic algorithms
        for(int i=0; i<numIterations; i++)
        {
              System.out.println("i: " + i);
              routeGenotypes = engineRoute.stream().limit(1).collect(EvolutionResult.toBestEvolutionResult()).getGenotypes();
              ownGenotypes   = engineOwn.stream().limit(1).collect(EvolutionResult.toBestEvolutionResult()).getGenotypes();
        }
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
    
}
/*Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory)
                                            .populationSize(generationSize)
                                            .alterers(new PartiallyMatchedCrossover<>(crossProbability), new SwapMutator<>(mutateProbability))
                                            .survivorsSelector(new ExponentialRankSelector<>())
                                            .optimize(Optimize.MINIMUM).build();*/

