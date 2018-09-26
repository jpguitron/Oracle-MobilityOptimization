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

public final class RouteChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements Serializable
{
	private static final long serialVersionUID = 2L;

    //Default aggressiveness when creating new instances//
    /*
        TODO Set value with parameter from main
    */
    
    //Default aggressiveness value for new instances//
	public static float defaultAgg;
	
	private ISeq<T> _validAlleles;
	
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
		return of(_validAlleles, length(), defaultAgg);
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
		final float aggressiveness
	) {
		require.positive(length);
		if (length > alleles.size()) {
			throw new IllegalArgumentException(format(
				"The sub-set size must be be greater then the base-set: %d > %d",
				length, alleles.size()
			));
		}

		final int[] subset = array.shuffle(comb.subset(alleles.size(), length));
		final ISeq<EnumGene<T>> genes = IntStream.of(subset)
			.mapToObj(i -> EnumGene.<T>of(i, alleles))
			.collect(ISeq.toISeq());

		return new RouteChromosome<>(genes, true);
	}

	///////////////////////////////////////////////////////////////////////////////
	
	public static RouteChromosome<Integer>
	ofInteger(final int start, final int end, final float aggressiveness) {
		if (end <= start) {
			throw new IllegalArgumentException(format(
				"end <= start: %d <= %d", end, start
			));
		}

		return ofInteger(IntRange.of(start, end), end - start, aggressiveness);
	}

	public static RouteChromosome<Integer>
	ofInteger(final IntRange range, final int length, final float aggressiveness) {
		return of(
			range.stream()
				.boxed()
				.collect(ISeq.toISeq()),
			length, aggressiveness
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
