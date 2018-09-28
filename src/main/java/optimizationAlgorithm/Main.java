package optimizationAlgorithm;

public class Main
{    
    public static void main(String[] args)
    {   
        //Hyperparameters
        int generationSize          = 10;
        int numIterations           = 500;
        float crossProbability      = 0.9f;
        float mutateProbability     = 0.01f;
        
        //Node info
        int[] initialNodes           = {101,102,103,104,105};
        int finalNode                = 100;
        int[] transitionNodes_1      = new int[50];
        int[] transitionNodes_2      = new int[50];
        for (int i=0; i < 50;i++)
        {
            transitionNodes_1[i] = i;
            transitionNodes_2[i] = 50+i;
        }
        
        //Execution
        MobilityOptimization.run(initialNodes,finalNode,transitionNodes_1,generationSize, numIterations , crossProbability, mutateProbability);
        MobilityOptimization.run(initialNodes,finalNode,transitionNodes_2,generationSize, numIterations , crossProbability, mutateProbability);
    }
}
