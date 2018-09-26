package optimizationAlgorithm;

public class Main
{    
    public static void main(String[] args)
    {   
        //Hyperparameters
        int generationSize          = 5;
        int numIterations           = 100;
        float crossProbability      = 0.9f;
        float mutateProbability     = 0.01f;
        float overlapAggressiveness = 2.0f;
    
        MobilityOptimization.run(generationSize, numIterations , crossProbability, mutateProbability, overlapAggressiveness);
    }
}
