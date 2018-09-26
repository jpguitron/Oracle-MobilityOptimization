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
    
    //Nodes array
    public static Node[] nodes;
    
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
    public static void run (int[] initialtNodes, int finalNode, int[] transitionNodes , int generationSize, int numIterations, float crossProbability, float mutateProbability, float overlapAggressiveness) 
    {
        nodes = DatabaseConnection.nodes_matrix(initialtNodes, finalNode, transitionNodes);
    
        Overlap[] overlaps = new Overlap[3];
        overlaps[0] = new Overlap(1, 101, 102, 0.8);
        overlaps[1] = new Overlap(1, 103, 104, 0.4);
        overlaps[2] = new Overlap(1, 105, 102, 0.2);
        //double[] probs = getProbabilityArray(overlaps);
        
        
        
        
        //Set default parameter values for custom chromosomes
        RouteChromosome.defaultAgg = overlapAggressiveness;
        //OwnChromosome.probsArray = new double[]{0.0,0.0,0.0,1.0,1.0,1.0, 1.0,1.0,1.0,1.0, 1.0,1.0};
        OwnChromosome.probsArray = getProbabilityArray(overlaps);
        /*
        TODO create chromosomes with parameters given from previous functions
        TODO create custom chromosomes for initialization
        TODO set convergence criteria
        */
        
        
        
        
        
        final Factory<Genotype<EnumGene<Integer>>> routePermFactory = Genotype.of(
        RouteChromosome.ofInteger(0,10, RouteChromosome.defaultAgg),
        RouteChromosome.ofInteger(0,5, RouteChromosome.defaultAgg)); 

        //Receive this from function//
        //double[] probs = {0.0,0.0,0.0,1.0,1.0,1.0, 1.0,1.0,1.0,1.0, 1.0,1.0};
        
        
        final Factory<Genotype<BitGene>> ownChangeFactory = Genotype.of (OwnChromosome.of(OwnChromosome.probsArray)); 

        //Build both optimization engines//
        Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory)
                                                  .populationSize(generationSize)
                                                   .alterers(new PartiallyMatchedCrossover<>(crossProbability), new SwapMutator<>(mutateProbability))
                                                   .survivorsSelector(new ExponentialRankSelector<>())
                                                   .optimize(Optimize.MINIMUM).build();
        
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

