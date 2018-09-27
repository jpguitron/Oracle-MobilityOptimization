package optimizationAlgorithm;

import static java.lang.String.format;
import static io.jenetics.internal.util.bit.getAndSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.jenetics.internal.math.comb;
import io.jenetics.internal.util.Equality;
import io.jenetics.internal.util.Hash;
import io.jenetics.internal.util.array;
import io.jenetics.internal.util.bit;
import io.jenetics.internal.util.reflect;
import io.jenetics.internal.util.require;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;
import io.jenetics.EnumGene;
import io.jenetics.AbstractChromosome;
import io.jenetics.Gene;

import java.util.Arrays;

public final class RouteChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements Serializable
{
	private static final long serialVersionUID = 2L;
	
	private ISeq<T> _validAlleles;
	private int routeIndex;
	
	// Private primary constructor.
	private RouteChromosome(
		final ISeq<EnumGene<T>> genes,
		final Boolean valid
	) {
		super(genes);

		assert !genes.isEmpty();
		_validAlleles = genes.get(0).getValidAlleles();
		_valid = valid;
	}

	public RouteChromosome(final ISeq<EnumGene<T>> genes) {
		this(genes, null);
	}

	public ISeq<T> getValidAlleles() {
		return _validAlleles;
	}

	@Override
	public boolean isValid() {
		if (_valid == null) {
			final byte[] check = bit.newArray(_validAlleles.length());
			_valid = _genes.forAll(g -> !getAndSet(check, g.getAlleleIndex()));
		}

		return _valid;
	}

	@Override
	public RouteChromosome<T> newInstance() {
        for(int x = 0; x < MobilityOptimization.routes.routes.length; x++)
        {
            if(length()==MobilityOptimization.routes.routes[x].hash_Set.size())
            {
                //int routeIndex = MobilityOptimization.startNodes[x];
                return of(_validAlleles, length(), x);
            }
        }
		return of(_validAlleles, length());
	}
    
	@Override
	public RouteChromosome<T> newInstance(final ISeq<EnumGene<T>> genes) {
		return new RouteChromosome<>(genes);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
				.and(super.hashCode())
				.value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return _genes.asList().stream()
			.map(g -> g.getAllele().toString())
			.collect(Collectors.joining("|"));
	}
    
    /////////////////////////////////////////////MAIN GENERATION METHOD///////////////////////////////////////7
	public static <T> RouteChromosome<T> of(
		final ISeq<? extends T> alleles,
		final int length,
		final int rIndex
	) {
		require.positive(length);
		if (length > alleles.size()) {
			throw new IllegalArgumentException(format(
				"The sub-set size must be be greater then the base-set: %d > %d",
				length, alleles.size()
			));
		}
        //System.out.println(".");
		//final int[] subset = array.shuffle(comb.subset(alleles.size(), length));
		// Fill Chromosome in subset
		int[] subset = new int[length];
		

        
        int[] nodes      = MobilityOptimization.routeMap[rIndex];
        Node currentNode = MobilityOptimization.nodeMapping.get(MobilityOptimization.startNodes[rIndex]); 

        Boolean[] visitedNodes = new Boolean[nodes.length];
        Arrays.fill(visitedNodes, Boolean.FALSE);
        
        
        //Number of times to add elements
        for (int i=0;i<nodes.length;i++)
        {
            double totalScore = 0;
            double[] scores      = new double[nodes.length];   //Scores array (for selection probability of next node)
            
            //Add element each time
            for (int j=0;j<nodes.length;j++)
            {
                if(!visitedNodes[j])
                {
                    Node evaluatedNode     = MobilityOptimization.nodeMapping.get(nodes[j]);
                    double costsToDest     = evaluatedNode.hmap.get(MobilityOptimization.destNode).cost;
                    double costFromCurrent = currentNode.hmap.get (evaluatedNode.id).cost;
                    scores[j]              = costsToDest/costFromCurrent;
                    scores[j]              = Math.pow(scores[j], 10);
                    totalScore             += scores[j];
                }
                
            }
            
            //Normalize score
            for (int j=0;j<nodes.length;j++)
            {
                scores[j] = scores[j]/totalScore;
            }

            //Calculate cumulative probabilities
            for (int j=0;j<nodes.length;j++)
            {
                if(j>0)
                    scores[j] += scores[j-1];
            }
            double randomNumber = Math.random();
            //System.out.println("Random: " + randomNumber);
            
            //Select event randomly
            int selectedIndex = nodes.length;        //Initialize with last one by default
            for (int j=0;j<nodes.length;j++)
            {
                if(randomNumber < scores[j])
                {
                    selectedIndex   = j;
                    visitedNodes[j] = true;
                    currentNode     = MobilityOptimization.nodeMapping.get(nodes[j]);
                    
                    //Found node to pick, now add its index into the subset
                    subset[i]       = j;
                    //System.out.print(subset[i] + ",");
                    break;
                }
            }
            
        }
        
        final ISeq<EnumGene<T>> genes = IntStream.of(subset)
			.mapToObj(i -> EnumGene.<T>of(i, alleles))
			.collect(ISeq.toISeq());
			
        //System.out.println(genes);
        //System.exit(0);
        return new RouteChromosome<>(genes, true);
    }
            //costsToDest[i]     = evaluatedNode.destEdge.cost;
            //System.out.println(costsToDest[i]);
            //costsToCenter[i] = nodes[]
            //scores[i] = 
            //See which element to add next
            /*for(int j=1;j<nodes.length;j++)
            {
                
            }
            //System.out.print(nodes[i].id+",");
        }
        //System.out.println();
        //System.out.println(genes);
        //System.exit(0);
			
		return new RouteChromosome<>(genes, true);
	}

    
    /*
    TODO receive routeIndex here as well
    */
	///////////////////////////////////////NEW INSTANCE WITH RANDOM PERMUTATION///////////////////////////
		public static <T> RouteChromosome<T> of(
		final ISeq<? extends T> alleles,
		final int length
	) {
		require.positive(length);
		if (length > alleles.size()) {
			throw new IllegalArgumentException(format(
				"The sub-set size must be be greater then the base-set: %d > %d",
				length, alleles.size()
			));
		}
        //System.out.println("New");
		final int[] subset = array.shuffle(comb.subset(alleles.size(), length));
		final ISeq<EnumGene<T>> genes = IntStream.of(subset)
			.mapToObj(i -> EnumGene.<T>of(i, alleles))
			.collect(ISeq.toISeq());
		return new RouteChromosome<>(genes, true);
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	public static RouteChromosome<Integer>
	ofInteger(final int start, final int end, final int rIndex) {
		if (end <= start) {
			throw new IllegalArgumentException(format(
				"end <= start: %d <= %d", end, start
			));
		}

		return ofInteger(IntRange.of(start, end), end - start, rIndex);
	}

	public static RouteChromosome<Integer>
	ofInteger(final IntRange range, final int length, final int rIndex) {
		return of(
			range.stream()
				.boxed()
				.collect(ISeq.toISeq()),
			length, rIndex
		);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeObject(_validAlleles);
		for (EnumGene<?> gene : _genes) {
			out.writeInt(gene.getAlleleIndex());
		}
	}
}
