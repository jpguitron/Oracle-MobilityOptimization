package optimizationAlgorithm;

import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.IntStream;

import io.jenetics.internal.util.Equality;
import io.jenetics.internal.util.Hash;
import io.jenetics.internal.util.bit;
import io.jenetics.internal.util.require;
import io.jenetics.util.ISeq;

import io.jenetics.*;

public class OwnChromosome extends Number
	implements
		Chromosome<BitGene>,
		Comparable<OwnChromosome>,
		Serializable
{
	private static final long serialVersionUID = 2L;

	protected double _p;

	protected int _length;

	protected byte[] _genes;

	// Wraps the genes byte array into a Seq<BitGene>.
	private transient BitGeneISeq _seq;

	// Private primary constructor.
	private OwnChromosome(final byte[] bits, final int length, final double p) {
		_genes = bits;
		_length = length;
		_p = p;
		_seq = BitGeneMSeq.of(_genes, length).toISeq();
	}

	public OwnChromosome(final byte[] bits, final int start, final int end) {
		this(
			bit.copy(bits, start, end),
			min(bits.length << 3, end) - start,
			0.0
		);
		_p = (double)bit.count(_genes)/(double)_length;
	}

	public OwnChromosome(final byte[] bits) {
		this(bits, 0, bits.length << 3);
	}

	private OwnChromosome(final byte[] bits, final int length) {
		this(
			bits,
			length == -1 ? bits.length*8 : length,
			(double)bit.count(bits)/
			(double)(length == -1 ? bits.length*8 : length)
		);
	}

	private static byte[] toByteArray(final CharSequence value) {
		final byte[] bytes = bit.newArray(value.length());
		for (int i = value.length(); --i >= 0;) {
			final char c = value.charAt(i);
			if (c == '1') {
				bit.set(bytes, i);
			} else if (c != '0') {
				throw new IllegalArgumentException(format(
					"Illegal character '%s' at position %d", c, i
				));
			}
		}

		return bytes;
	}

	private void rangeCheck(final int index) {
		if (index < 0 || index >= _length) {
			throw new IndexOutOfBoundsException(
				"Index: " + index + ", Length: " + _length
			);
		}
	}

	public double getOneProbability() {
		return _p;
	}

	@Override
	public BitGene getGene() {
		assert _genes != null;
		assert _genes.length > 0;
		return BitGene.of(bit.get(_genes, 0));
	}

	@Deprecated
	public boolean get() {
		return bit.get(_genes, 0);
	}

	public boolean booleanValue() {
		return bit.get(_genes, 0);
	}

	@Override
	public BitGene getGene(final int index) {
		rangeCheck(index);
		assert _genes != null;
		return BitGene.of(bit.get(_genes, index));
	}

	@Deprecated
	public boolean get(final int index) {
		rangeCheck(index);
		return bit.get(_genes, index);
	}

	public boolean booleanValue(final int index) {
		rangeCheck(index);
		return bit.get(_genes, index);
	}

	@Override
	public ISeq<BitGene> toSeq() {
		return _seq;
	}

	@Override
	public int length() {
		return _length;
	}

	public int bitCount() {
		return bit.count(_genes);
	}

	@Override
	public Iterator<BitGene> iterator() {
		return _seq.iterator();
	}

	public ListIterator<BitGene> listIterator() {
		return _seq.listIterator();
	}

	@Override
	public int intValue() {
		return (int)longValue();
	}

	@Override
	public long longValue() {
		return toBigInteger().longValue();
	}

	@Override
	public float floatValue() {
		return (float)longValue();
	}

	@Override
	public double doubleValue() {
		return longValue();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public BigInteger toBigInteger() {
		return new BigInteger(_genes);
	}

	public int toByteArray(final byte[] bytes) {
		if (bytes.length < _genes.length) {
			throw new IndexOutOfBoundsException();
		}

		System.arraycopy(_genes, 0, bytes, 0, _genes.length);
		return _genes.length;
	}

	public byte[] toByteArray() {
		final byte[] data = new byte[_genes.length];
		toByteArray(data);
		return data;
	}

	public BitSet toBitSet() {
		final BitSet set = new BitSet(length());
		for (int i = 0, n = length(); i < n; ++i) {
			set.set(i, getGene(i).getBit());
		}
		return set;
	}

	public IntStream ones() {
		return IntStream.range(0, length())
			.filter(index -> bit.get(_genes, index));
	}

	public IntStream zeros() {
		return IntStream.range(0, length())
			.filter(index -> !bit.get(_genes, index));
	}

	@Override
	public OwnChromosome newInstance(final ISeq<BitGene> genes) {
		requireNonNull(genes, "Genes");
		if (genes.isEmpty()) {
			throw new IllegalArgumentException(
				"The genes sequence must contain at least one gene."
			);
		}

		final OwnChromosome chromosome = new OwnChromosome(
			bit.newArray(genes.length()), genes.length()
		);
		int ones = 0;

		if (genes instanceof BitGeneISeq) {
			final BitGeneISeq iseq = (BitGeneISeq)genes;
			iseq.copyTo(chromosome._genes);
			ones = bit.count(chromosome._genes);
		} else {
			for (int i = genes.length(); --i >= 0;) {
				if (genes.get(i).booleanValue()) {
					bit.set(chromosome._genes, i);
					++ones;
				}
			}
		}

		chromosome._p = (double)ones/(double)genes.length();
		return chromosome;
	}

	@Override
	public OwnChromosome newInstance() {
		return of(_length, _p);
	}

	public String toCanonicalString() {
		return toSeq().stream()
			.map(g -> g.booleanValue() ? "1" : "0")
			.collect(joining());
	}

	@Override
	public int compareTo(final OwnChromosome that) {
		return toBigInteger().compareTo(that.toBigInteger());
	}
	public OwnChromosome invert() {
		final byte[] data = _genes.clone();
		bit.invert(data);
		return new OwnChromosome(data, _length, 1.0 - _p);
	}

	public static OwnChromosome of(final int length, final double p) {
		return new OwnChromosome(bit.newArray(length, p), length, p);
	}

	public static OwnChromosome of(final int length) {
		return new OwnChromosome(bit.newArray(length, 0.5), length, 0.5);
	}

	public static OwnChromosome of(final BitSet bits, final int length) {
		final byte[] bytes = bit.newArray(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				bit.set(bytes, i);
			}
		}
		final double p = (double)bit.count(bytes)/(double)length;

		return new OwnChromosome(bytes, length, p);
	}

	public static OwnChromosome of(
		final BitSet bits,
		final int length,
		final double p
	) {
		final byte[] bytes = bit.newArray(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				bit.set(bytes, i);
			}
		}

		return new OwnChromosome(bytes, length, require.probability(p));
	}

	public static OwnChromosome of(final BitSet bits) {
		return new OwnChromosome(bits.toByteArray(), -1);
	}

	public static OwnChromosome of(final BigInteger value) {
		return new OwnChromosome(value.toByteArray(), -1);
	}

	public static OwnChromosome of(final BigInteger value, final double p) {
		final byte[] bits = value.toByteArray();
		return new OwnChromosome(bits, bits.length*8, require.probability(p));
	}

	public static OwnChromosome of(final CharSequence value) {
		return new OwnChromosome(toByteArray(requireNonNull(value, "Input")), -1);
	}

	public static OwnChromosome of(final CharSequence value, final double p) {
		final byte[] bits = toByteArray(requireNonNull(value, "Input"));
		return new OwnChromosome(bits, bits.length*8, require.probability(p));
	}

	public static OwnChromosome of(
		final CharSequence value,
		final int length,
		final double p
	) {
		final byte[] bits = toByteArray(requireNonNull(value, "Input"));
		return new OwnChromosome(bits, length, require.probability(p));
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_genes).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(c -> {
			boolean equals = length() == c.length();
			for (int i = 0, n = length(); equals && i < n; ++i) {
				equals = getGene(i) == c.getGene(i);
			}
			return equals;
		});
	}

	@Override
	public String toString() {
		return bit.toByteString(_genes);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(_length);
		out.writeDouble(_p);
		out.writeInt(_genes.length);
		out.write(_genes);
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		_length = in.readInt();
		_p = in.readDouble();

		final int bytes = in.readInt();
		_genes = new byte[bytes];
		in.readFully(_genes);

		_seq = BitGeneISeq.of(_genes, _length);
	}
}
