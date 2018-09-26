package optimizationAlgorithm;

public class Main
{    
    public static void main(String[] args)
    {   
        //Hyperparameters
        int generationSize          = 5;
        int numIterations           = 3;
        float crossProbability      = 0.9f;
        float mutateProbability     = 0.01f;
        float overlapAggressiveness = 2.0f;
        
        //Node info
        int goalNode                = 100;
        int[] startNodes            = {101,102,103,104,105};
        int[] transitNodes          = new int[50];
        for (int i=0; i < 50;i++)
        {
            transitNodes[i] = i;
        }
        MobilityOptimization.run(startNodes,generationSize, numIterations , crossProbability, mutateProbability, overlapAggressiveness);
    }
}
