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
import io.jenetics.PermutationChromosome;

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

    //Static variables for storing best chromosomes oh each type found so far
    private static Genotype<EnumGene<Integer>> bestRoutes;
    private static Genotype<BitGene> bestOwns;

    //Function for evaluating route permutation genotypes (Permutation Chromosomes)
    private static double evalRoutePerm (Genotype<EnumGene<Integer>> genotype) 
    {
        /*return gt.getChromosome ()
            .as (BitChromosome.class)
            .bitCount();*/
        return 1.0;
    }
    
    //Function for evaluating ownership genotypes (Bit Chromosomes)
    private static double evalOwnChange (Genotype<BitGene> genotype) 
    {
        /*return gt.getChromosome ()
            .as (BitChromosome.class)
            .bitCount();*/
        return 1.0;
    }
    
    
    public static void run (int generationSize, int numIterations, float crossProbability, float mutateProbability, float overlapAggressiveness) 
    {
    
        ////////////////////////////////////////////////////ROUTE PERMUTATIONS////////////////////////////////////////////////////////////
        
        //final Factory<Genotype<EnumGene<Integer>>> routePermFactory = Genotype.of(WarehouseChromosome.ofInteger(0,GenotypeCost.productZones,similarity)); 
        //Genotype factory for generating permutations for the stablished routes//
        /*
        TODO create chromosomes with parameters given from previous functions
        TODO create custom chromosomes for initialization
        TODO set convergence criteria
        */
        final Factory<Genotype<EnumGene<Integer>>> routePermFactory = Genotype.of(
        PermutationChromosome.ofInteger(0,10),
        PermutationChromosome.ofInteger(0,5)); 

        final Factory<Genotype<BitGene>> ownChangeFactory = Genotype.of (BitChromosome.of(10 , 0.5)); 
        
        // Best genotypes found of each kind (permutations and bits)
        bestRoutes = routePermFactory.newInstance();
        bestOwns = ownChangeFactory.newInstance();
        
        

        //Build both optimization engines//
        Engine<EnumGene<Integer>, Double> engineRoute = Engine.builder(MobilityOptimization::evalRoutePerm, routePermFactory)
                                                  .populationSize(generationSize)
                                                   .alterers(new PartiallyMatchedCrossover<>(crossProbability), new SwapMutator<>(mutateProbability))
                                                   .survivorsSelector(new ExponentialRankSelector<>())
                                                   .optimize(Optimize.MINIMUM).build();
        
        Engine<BitGene, Double> engineOwn = Engine.builder(MobilityOptimization::evalOwnChange, ownChangeFactory).optimize(Optimize.MINIMUM).build();
        
        
        //Execute alternate iterations of genetic algorithms
        for(int i=0; i<numIterations; i++){
              System.out.println("i: " + i);
              bestRoutes = engineRoute.stream().limit(1).collect(EvolutionResult.toBestGenotype());
              bestOwns   = engineOwn.stream().limit(1).collect(EvolutionResult.toBestGenotype());
         }
        
        System.out.println("Best routes: \n \t " + bestRoutes);
        System.out.println("Result owns: \n \t " + bestOwns);        
    }
}

