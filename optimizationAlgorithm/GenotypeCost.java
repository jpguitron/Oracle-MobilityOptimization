/*package optimizationAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.EnumGene;
import java.util.Random;
public class GenotypeCost
{   
    //Relationship between time waited for vertical movement in relationship with distance traveled horizontally//
    //(Average truck lift speed)//
    static float verticalCost;
    static int productZones;                  //Amount of "D" zones (containing a single product)//
    static int beaconZones;                   //Amount of "G" zones (beacon detectable zones)//
    static int fixedZones;                    //Amout of "Z" zones (visited zones without storage capacity)//
    static int floorAmounts;                  //Amount of floors in the warehouse//
    static int distinctProds;                 //Amount of distinct products in WH//
    
    static int[] zSectionMapping;             //Ordered fixed section ids obtained from database//
    static int[] productMapping;              //Mapping of every "D" zone ID to the product ID of the product it contains//
    static int[] reserveProductMapping;       //Fill empty spaces with products proportionally to their appearance int the warehouse//
    static int[] zoneCapacities;              //Size of every "G" area//
    static int[] zoneFloors;                  //Floor mapping for every "G" zone//
    static int[] floorMapping;                //Floor of every "D" area to the floor where they are located//
    static int[] beaconZoneMapping;           //Mapping of every "D" zone to its corresponding "G" zone//
    
    //Additional time for getting product up or down from a particular floor//
    //in relation to the time required for getting something from floor 0//
    static float[] upVerticalMovementCosts;     //Cost of getting product up to a "G" zone (seconds)//
    
    //Incoming and outgoing registers for every product into every "Z" zone
    //this assumes that incoming products will be placed in locations similar to the current product locations//
    //First index "Z" zone
    //Second index product
    static int[][] movementRegisters;
    static float[][] d_matrix;             //Distance between every "G" and "Z" zone in the warehouse//
    
   
    /////////////////////////////////////////////////////REGULAR COST (TIME OPTIMIZATION)///////////////////////////////////////////////////
    ///////////////////////////////////COST OF WAREHOUSE ARRANGEMENT//////////////////////////////////
    public static double calculate(Genotype<EnumGene<Integer>> genotype)
    {
        double cost = 0;
        for(int i=0;i<genotype.getChromosome().length();i++)
        {
            int DzoneIndex = genotype.getChromosome().getGene(i).getAllele();
            
            //Only add cost if the zone moved contained a product//
            if(productMapping[DzoneIndex]!=-1)
            {
                //Cost of moving zone "DzoneIndex" to zone "i" (in ground)
                cost += d_matrix[beaconZoneMapping[i]][beaconZoneMapping[DzoneIndex]];    
                
                //Vertical cost only considered if the product is located in a zone different than the original//
                if(beaconZoneMapping[i] != beaconZoneMapping[DzoneIndex] || floorMapping[i] != floorMapping[DzoneIndex])
                {
                    //Vertical cost of rearrangement (down from current location and up to new location)//
                    cost += verticalCost * (upVerticalMovementCosts[floorMapping[i]-InputInfo.minFloor] + upVerticalMovementCosts[floorMapping[DzoneIndex]-InputInfo.minFloor]);
                }
            }
        }
        return cost;
    }
    
    
    //////////////////////////////COST FROM INCOMING AND OUTGOING PRODUCT//////////////////////////////////
    public static double stateCost(Genotype<EnumGene<Integer>> genotype, int[]productMapping)
    {
        double cost = 0;
        int [][] productPerZone = new int[zoneCapacities.length][distinctProds];
        
        int count = 0;
        for(int i=0;i<zoneCapacities.length;i++)
        {
            //Get products each zone//
            for(int j=0;j<zoneCapacities[i];j++)
            {
                int productZoneIndex = genotype.getChromosome().getGene(count).getAllele();
                int product_id = productMapping[productZoneIndex];
                
                //Count product if cell id is != -1 (NOT EMPTY SPACE)//
                if(product_id!=-1)
                    productPerZone[i][product_id]++;
                count++;
            }
        }
        
        //G zone index
        for(int i=0;i<zoneCapacities.length;i++)
        {
            //Product index
            for(int j=0;j<distinctProds;j++)
            {
                //Z zone index
                for(int k=GenotypeCost.beaconZones;k<GenotypeCost.beaconZones+GenotypeCost.fixedZones;k++)
                {
                    //HORIZONTAL COST//
                    //Incoming//
                    //Multiply amount of product "J" moved to zone "G" from zone "Z" with the frequency with which product "J" moved through this route//
                    //int incomingProduct = productPerZone[i][j] * incomingRegisters[k-zoneCapacities.length][j];
                    
                    //Outgoing//
                    //Multiply amount of product "J" moved from zone "G" to zone "Z" with the frequency with which product "J" moved through this route//
                    //This assumes that incoming products will be place in locations where ther is already product of the same type//
                    float movedProduct = productPerZone[i][j] * movementRegisters[k-zoneCapacities.length][j];
                    cost += movedProduct * d_matrix[i][k];  
                    
                    //VERTICAL COST//
                    //Substract minimum floor to the floor of the zone to adapt to the counting in the DB (start from either 0 or 1)//
                    cost+=movedProduct * upVerticalMovementCosts[zoneFloors[i]-InputInfo.minFloor]*verticalCost;
                }
            }
        }
        return cost;
    }
    
    /////////////////////////////////////////////////////DISTANCE COST///////////////////////////////////////////////////
    ///////////////////////////////////COST OF WAREHOUSE ARRANGEMENT//////////////////////////////////
    public static double rearrangementCostD(Genotype<EnumGene<Integer>> genotype)
    {
        double cost = 0;
        for(int i=0;i<genotype.getChromosome().length();i++)
        {
            int DzoneIndex = genotype.getChromosome().getGene(i).getAllele();
            //Only add cost if the zone moved contained a product//
            if(productMapping[DzoneIndex]!=-1)
                cost += d_matrix[beaconZoneMapping[i]][beaconZoneMapping[DzoneIndex]];    
        }
        return cost;
    }
    
    
    //////////////////////////////COST FROM INCOMING AND OUTGOING PRODUCT//////////////////////////////////
    public static double stateCostD(Genotype<EnumGene<Integer>> genotype, int[]productMapping)
    {
        double cost = 0;
        int [][] productPerZone = new int[zoneCapacities.length][distinctProds];
        
        int count = 0;
        for(int i=0;i<zoneCapacities.length;i++)
        {
            //Get products each zone//
            for(int j=0;j<zoneCapacities[i];j++)
            {
                int productZoneIndex = genotype.getChromosome().getGene(count).getAllele();
                int product_id = productMapping[productZoneIndex];
                
                //Count product if cell id is != -1 (NOT EMPTY SPACE)//
                if(product_id!=-1)
                    productPerZone[i][product_id]++;
                count++;
            }
        }
        
        //G zone index
        for(int i=0;i<zoneCapacities.length;i++)
        {
            //Product index
            for(int j=0;j<distinctProds;j++)
            {
                //Z zone index
                for(int k=GenotypeCost.beaconZones;k<GenotypeCost.beaconZones+GenotypeCost.fixedZones;k++)
                {
                    //HORIZONTAL COST//
                    //Incoming//
                    //Multiply amount of product "J" moved to zone "G" from zone "Z" with the frequency with which product "J" moved through this route//
                    //int incomingProduct = productPerZone[i][j] * incomingRegisters[k-zoneCapacities.length][j];
                    
                    //Outgoing//
                    //Multiply amount of product "J" moved from zone "G" to zone "Z" with the frequency with which product "J" moved through this route//
                    //This assumes that incoming products will be place in locations where ther is already product of the same type//
                    float movedProduct = productPerZone[i][j] * movementRegisters[k-zoneCapacities.length][j];
                    cost += movedProduct * d_matrix[i][k];  
                }
            }
        }
        return cost;
    }
}*/
