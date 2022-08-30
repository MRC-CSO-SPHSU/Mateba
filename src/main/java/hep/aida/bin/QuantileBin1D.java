package hep.aida.bin;

import cern.colt.list.ArrayList;
import cern.jet.random.RandomEngine;
import cern.jet.stat.quantile.QuantileFinder;
import cern.jet.stat.quantile.QuantileFinderFactory;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * 1-dimensional non-rebinnable bin holding {@code double} elements with scalable quantile operations defined upon;
 * Using little main memory, quickly computes approximate quantiles over very large data sequences with and even without
 * a-priori knowledge of the number of elements to be filled; Conceptually a strongly lossily compressed multiset (or
 * bag); Guarantees to respect the worst case approximation error specified upon instance construction.
 * <p>
 * <b>Motivation and Problem:</b> Intended to help scale applications requiring quantile computation. Quantile
 * computation on very large data sequences is problematic, for the following reasons: Computing quantiles requires
 * sorting the data sequence. To sort a data sequence the entire data sequence needs to be available. Thus, data cannot
 * be thrown away during filling (as done by static bins like {@link StaticBin1D} and {@link MightyStaticBin1D}). It
 * needs to be kept, either in main memory or on disk. There is often not enough main memory available. Thus, during
 * filling data needs to be streamed onto disk. Sorting disk resident data is prohibitively time-consuming. As a
 * consequence, traditional methods either need very large memories (like {@link DynamicBin1D}) or time-consuming disk
 * based sorting.
 * <p>
 * This class proposes to efficiently solve the problem, at the expense of producing approximate rather than exact
 * results. It can deal with infinitely many elements without resorting to disk. The main memory requirements are
 * smaller than for any other known approximate technique by an order of magnitude. They get even smaller if an upper
 * limit on the maximum number of elements ever to be added is known a-priori.
 * <p>
 * <b>Approximation error:</b> The approximation guarantees are parameterizable and explicit but probabilistic, and
 * apply for arbitrary value distributions and arrival distributions of the data sequence. In other words, this class
 * guarantees to respect the worst case approximation error specified upon instance construction to a certain
 * probability. Of course, if it is specified that the approximation error should not exceed some number <i>very
 * close</i> to zero, this class will potentially consume just as much memory as any of the traditional exact techniques
 * would do.
 *
 * @implSpec The broad picture is as follows. Two concepts are used: <i>Shrinking</i> and <i>Sampling</i>. Shrinking
 * takes a data sequence, sorts it and produces a shrank data sequence by picking every k-th element and throwing away
 * all the rest. The shrank data sequence is an approximation to the original data sequence.
 * <p>
 * Imagine a large data sequence (residing on disk or being generated in memory on the fly) and a main memory
 * <i>block</i> of {@code n = b * k} elements ({@code b} is the number of buffers, {@code k} is the number of elements
 * per buffer). Fill elements from the data sequence into the block until it is full or the data sequence is exhausted.
 * When the block (or a subset of buffers) is full and the data sequence is not exhausted, apply shrinking to lossily
 * compress a number of buffers into one single buffer. Repeat these steps until all elements of the data sequence have
 * been consumed. Now the block is a shrinked approximation of the original data sequence. Treating it as if it would be
 * the original data sequence, we can determine quantiles in main memory.
 * <p>
 * Now, the whole thing boils down to the question of: Can we choose {@code b} and {@code k} (the number of buffers and
 * the buffer size) such that {@code b * k} is minimized, yet quantiles determined upon the block are <i>guaranteed</i>
 * to be away from the true quantiles no more than some {@code epsilon}? It turns out, we can. It also turns out that
 * the required main memory block size {@code n = b * k} is usually moderate.
 * <p>
 * The theme can be combined with random sampling to further reduce main memory requirements, at the expense of
 * probabilistic guarantees. Sampling filters the data sequence and feeds only selected elements to the algorithm
 * outlined above. Sampling is turned on or off, depending on the parametrization.
 * <p>
 * This quick overview does not go into important details, such as assigning proper <i>weights</i> to buffers, how to
 * choose subsets of buffers to shrink, etc.
 * @see <a href="package-summary.html">package summary</a>
 * @see <a href="package-tree.html">tree view</a>
 * @see <a href="https://dl.acm.org/doi/10.1145/304182.304204">Gurmeet Singh Manku, Sridhar Rajagopalan, and Bruce G.
 * Lindsay. 1999. Random sampling techniques for space efficient online computation of order statistics of large
 * datasets. SIGMOD Rec. 28, 2 (June 1999), 251–262.</a>
 * @see <a href="https://dl.acm.org/doi/10.1145/276304.276342">Gurmeet Singh Manku, Sridhar Rajagopalan, and Bruce G.
 * Lindsay. 1998. Approximate medians and other quantiles in one pass and with limited memory. SIGMOD Rec. 27, 2 (June
 * 1998), 426–435.</a>
 */
@SuppressWarnings("unused")
public class QuantileBin1D extends MightyStaticBin1D {

    @Serial
    private static final long serialVersionUID = -3022550983294334211L;
    protected QuantileFinder finder = null;

    /**
     * Not public; for use by subclasses only! Constructs and returns an empty bin.
     */
    protected QuantileBin1D() {
        super(false, false, 2);
    }

    /**
     * Equivalent to {@code new QuantileBin1D(false, Long.MAX_VALUE, epsilon, 0.001, 10000,
     * new MersenneTwister(new java.util.Date())}
     */
    public QuantileBin1D(final double epsilon) {
        this(false, Long.MAX_VALUE, epsilon, 0.001, 10000,
            new MersenneTwister(new java.util.Date()));
    }

    /**
     * Equivalent to {@code new QuantileBin1D(known_N, N, epsilon, delta, quantiles, randomGenerator, false, false, 2)}.
     */
    public QuantileBin1D(final boolean known_N, final long N, final double epsilon, final double delta,
                         final int quantiles, final @NotNull RandomEngine randomGenerator) {
        this(known_N, N, epsilon, delta, quantiles, randomGenerator, false, false, 2);
    }

    /**
     * Constructs and returns an empty bin that, under the given constraints, minimizes the amount of memory needed.
     * <p>
     * Some applications exactly know in advance over how many elements quantiles are to be computed. Provided with such
     * information the main memory requirements of this class are small. Other applications don't know in advance over
     * how many elements quantiles are to be computed. However, some of them can give an upper limit, which will reduce
     * main memory requirements. For example, if elements are selected from a database and filled into histograms, it is
     * usually not known in advance how many elements are being filled, but one may know that at most {@code S}
     * elements, the number of elements in the database, are filled. The third type of applications knows nothing at all
     * about the number of elements to be filled; from zero to infinitely many elements may actually be filled. This
     * method efficiently supports all three types of applications.
     *
     * @param known_N                Specifies whether the number of elements over which quantiles are to be computed is
     *                               known or not.
     * @param N                      If {@code known_N == true}, the number of elements over which quantiles are to be
     *                               computed. if {@code known_N == false}, the upper limit on the number of elements
     *                               over which quantiles are to be computed. In other words, the maximum number of
     *                               elements ever to be added. If such an upper limit is a-priori unknown, then set
     *                               {@code N = Long.MAX_VALUE}.
     * @param epsilon                The approximation error which is guaranteed not to be exceeded (e.g. {@code 0.001})
     *                               ({@code 0 <= epsilon <= 1}). To get exact rather than approximate quantiles, set
     *                               {@code epsilon = 0.0};
     * @param delta                  The allowed probability that the actual approximation error exceeds {@code epsilon}
     *                               (e.g. {@code 0.0001}) ({@code 0 <= delta <=1}). To avoid probabilistic answers, set
     *                               {@code delta=0.0}. For example, {@code delta = 0.0001} is equivalent to a
     *                               confidence of {@code 99.99%}.
     * @param quantiles              The number of quantiles to be computed (e.g. {@code 100}) ({@code quantiles >= 1}).
     *                               If unknown in advance, set this number large, e.g. {@code quantiles >= 10000}.
     * @param randomGenerator        A uniform random number generator. Set this parameter to {@code null} to use the
     *                               default generator seeded with the current time.
     * @param hasSumOfLogarithms     Tells whether {@link #sumOfLogarithms()} can return meaningful results. Set this
     *                               parameter to {@code false} if measures of sum of logarithms, geometric mean and
     *                               product are not required.
     * @param hasSumOfInversions     Tells whether {@link #sumOfInversions()} can return meaningful results. Set this
     *                               parameter to {@code false} if measures of sum of inversions, harmonic mean and
     *                               {@code sumOfPowers(-1)} are not required.
     * @param maxOrderForSumOfPowers The maximum order {@code k} for which {@link #sumOfPowers(int)} can return
     *                               meaningful results. Set this parameter to at least 3 if the skew is required, to at
     *                               least 4 if the kurtosis is required. In general, if moments are required, set this
     *                               parameter at least as large as the largest required moment. This method always
     *                               substitutes {@code Math.max(2, maxOrderForSumOfPowers)} for the parameter passed
     *                               in. Thus, {@code sumOfPowers(0..2)} always returns meaningful results.
     */
    public QuantileBin1D(final boolean known_N, final long N, final double epsilon, final double delta,
                         final int quantiles, final @NotNull RandomEngine randomGenerator,
                         final boolean hasSumOfLogarithms, final boolean hasSumOfInversions,
                         final int maxOrderForSumOfPowers) {
        super(hasSumOfLogarithms, hasSumOfInversions, maxOrderForSumOfPowers);
        this.finder = QuantileFinderFactory.newQuantileFinder(known_N, N, epsilon, delta, quantiles, randomGenerator);
        this.clear();
    }

    /**
     * Adds the part of the specified list between indexes {@code from} (inclusive) and {@code to} (inclusive) to the
     * receiver.
     *
     * @param list the list of which elements shall be added.
     * @param from the index of the first element to be added (inclusive).
     * @param to   the index of the last element to be added (inclusive).
     * @throws IndexOutOfBoundsException if {@code list.size() > 0 && (from < 0 || from > to || to >= list.size())}.
     */

    public synchronized void addAllOfFromTo(final @NotNull ArrayList list, final int from, final int to) {
        super.addAllOfFromTo(list, from, to);
        if (this.finder != null) this.finder.addAllOfFromTo(list, from, to);
    }

    /**
     * Removes all elements from the receiver. The receiver will be empty after this call returns.
     */

    public synchronized void clear() {
        super.clear();
        if (this.finder != null) this.finder.clear();
    }

    /**
     * @return a deep copy of the receiver.
     */

    public synchronized Object clone() {
        QuantileBin1D clone = (QuantileBin1D) super.clone();
        if (this.finder != null)
            clone.finder = (QuantileFinder) clone.finder.clone();
        return clone;
    }

    /**
     * Computes the deviations from the receiver's measures to another bin's measures.
     *
     * @param other the other bin to compare with
     * @return a summary of the deviations.
     */

    public String compareWith(final @NotNull AbstractBin1D other) {
        StringBuilder buf = new StringBuilder(super.compareWith(other));
        if (other instanceof QuantileBin1D q) {
            buf.append("25%, 50% and 75% Quantiles: ")
                .append(relError(quantile(0.25), q.quantile(0.25)))
                .append(", ")
                .append(relError(quantile(0.5), q.quantile(0.5)))
                .append(", ")
                .append(relError(quantile(0.75), q.quantile(0.75)))
                .append("\nquantileInverse(mean): ")
                .append(relError(quantileInverse(mean()), q.quantileInverse(q.mean())))
                .append(" %")
                .append("\n");
        }
        return buf.toString();
    }

    /**
     * Returns the median.
     */
    public double median() {
        return quantile(0.5);
    }

    /**
     * Computes and returns the phi-quantile.
     *
     * @param phi the percentage for which the quantile is to be computed. phi must be in the interval {@code (0.0,1.0]}.
     * @return the phi quantile element.
     */
    public synchronized double quantile(final double phi) {
        return quantiles(new ArrayList(new double[]{phi})).get(0);
    }

    /**
     * Returns how many percent of the elements contained in the receiver are {@code <= element}. Does linear
     * interpolation if the element is not contained but lies in between two contained elements.
     *
     * @param element the element to search for.
     * @return the percentage {@code phi} of elements {@code <= element} {@code (0.0 <= phi <=1.0)}.
     */
    public synchronized double quantileInverse(final double element) {
        return finder.phi(element);
    }

    /**
     * Returns the quantiles of the specified percentages. For implementation reasons considerably more efficient than
     * calling {@link #quantile(double)} various times.
     *
     * @param phis The percentages for which quantiles are to be computed. Each percentage must be in the interval
     *             {@code (0.0,1.0]}. {@code percentages} must be sorted ascending.
     * @return the quantiles.
     */
    public synchronized ArrayList quantiles(final @NotNull ArrayList phis) {
        return finder.quantileElements(phis);
    }

    /**
     * Returns how many elements are contained in the range {@code [minElement, maxElement]}. Does linear interpolation
     * if one or both of the parameter elements are not contained. Returns exact or approximate results, depending on
     * the parametrization of this class or subclasses.
     *
     * @param minElement the minimum element to search for.
     * @param maxElement the maximum element to search for.
     * @return the number of elements in the range.
     */
    public int sizeOfRange(final double minElement, final double maxElement) {
        return (int) Math.round(size() * (quantileInverse(maxElement) - quantileInverse(minElement)));
    }

    /**
     * Divides (rebins) a copy of the receiver at the given <i>percentage boundaries</i> into bins and returns these
     * bins, such that each bin <i>approximately</i> reflects the data elements of its range.
     * <p>
     * The receiver is not physically rebinned (divided); it stays unaffected by this operation. The returned bins are
     * such that <i>if</i> one would have filled elements into multiple bins instead of one single all encompassing bin
     * only, those multiple bins would have <i>approximately</i> the same statistics measures as the one's returned by
     * this method.
     * <p>
     * The {@code split(...)} methods are particularly well suited for real-time interactive rebinning (the famous
     * "scrolling slider" effect).
     * <p>
     * Passing equi-distant percentages like {@code (0.0, 0.2, 0.4, 0.6, 0.8, 1.0)} into this method will yield bins of
     * an <i>equi-depth histogram</i>, i.e. a histogram with bin boundaries adjusted such that each bin contains the
     * same number of elements, in this case 20% each. Equi-depth histograms can be useful if, for example, not enough
     * properties of the data to be captured are known a-priori to be able to define reasonable bin boundaries
     * (partitions). For example, when guesses about minima and maxima are strongly unreliable. Or when chances are that
     * by focussing too much on one particular area other important areas and characters of a data set may be missed.
     * <p>
     * <b>Accuracy</b>:
     * <p>
     * Depending on the accuracy of quantile computation and the number of sub-intervals per interval (the resolution).
     * Objects of this class compute exact or approximate quantiles, depending on the parameters used upon instance
     * construction. Objects of subclasses may <i>always</i> compute exact quantiles, as is the case for
     * {@link DynamicBin1D}. Most importantly for this class {@link QuantileBin1D}, a reasonably small epsilon (e.g.
     * 0.01, perhaps 0.001) should be used upon instance construction. The confidence parameter {@code delta} is less
     * important, you may find {@code delta=0.00001} appropriate.
     * <br>
     * The larger the resolution, the smaller the approximation error, up to some limit. Integrating over only a few
     * sub-intervals per interval will yield very crude approximations. If the resolution is set to a reasonably large
     * number, say 10..100, more small sub-intervals are integrated, resulting in more accurate results.
     * <br>
     * Note that for good accuracy, the number of quantiles computable with the given approximation guarantees should
     * upon instance construction be specified, so as to satisfy
     * <p>
     * {@code quantiles > resolution * (percentages.size() - 1)}
     * <p>
     * <p>
     * <b>Example:</b>
     * <p>
     * {@code resolution = 2, percentList = (0.0, 0.1, 0.2, 0.5, 0.9, 1.0)} means the receiver is to be split into 5
     * bins:
     * <br>
     * <ul>
     * <li>bin 0 ranges from {@code [0%..10%)} and holds the smallest 10% of the sorted elements.
     * <li>bin 1 ranges from {@code [10%..20%)} and holds the next smallest 10% of the sorted elements.
     * <li>bin 2 ranges from {@code [20%..50%)} and holds the next smallest 30% of the sorted elements.
     * <li>bin 3 ranges from {@code [50%..90%)} and holds the next smallest 40% of the sorted elements.
     * <li>bin 4 ranges from {@code [90%..100%)} and holds the largest 10% of the sorted elements.
     * </ul>
     * <p>
     * The statistics measures for each bin are to be computed at a resolution of 2 sub-bins per bin. Thus, the
     * statistics measures of a bin are the integrated measures over 2 sub-bins, each containing the same amount of
     * elements:
     * <ul>
     * <li>bin 0 has a sub-bin ranging from {@code [0%..5%)} and a sub-bin ranging from {@code [5%..10%)}.
     * <li>bin 1 has a sub-bin ranging from {@code [10%..15%)} and a sub-bin ranging from {@code [15%..20%)}.
     * <li>bin 2 has a sub-bin ranging from {@code [20%..35%)} and a sub-bin ranging from {@code [35%..50%)}.
     * <li>bin 3 has a sub-bin ranging from {@code [50%..70%)} and a sub-bin ranging from {@code [70%..90%)}.
     * <li>bin 4 has a sub-bin ranging from {@code [90%..95%)} and a sub-bin ranging from {@code [95%..100%)}.
     * </ul>
     * <p>
     * Let's concentrate on the sub-bins of bin 0.
     * <ul>
     * <li>Assume the sub-bin {@code A=[0%..5%)} has a minimum of {@code 300} and a maximum of {@code 350} ({@code 0%}
     * of all elements are less than {@code 300}, {@code 5%} of all elements are less than {@code 350}).
     * <li>Assume the sub-bin {@code B=[5%..10%)} has a minimum of {@code 350} and a maximum of {@code 550} ({@code 5%}
     * of all elements are less than {@code 350}, {@code 10%} of all elements are less than {@code 550}).
     * </ul>
     * <p>
     * Assume the entire data set consists of {@code N = 100} elements.
     * <ul>
     * <li>Then sub-bin A has an approximate mean of {@code 300 + 350 / 2 = 325}, a size of
     * {@code N * (5% - 0%) = 100 * 5% = 5} elements, an approximate sum of {@code 325 * 100 * 5% = 1625}, an
     * approximate sum of squares of {@code 325}<sup>2</sup> {@code * 100 * 5% = 528125}, an approximate sum of
     * inversions of {@code (1.0 / 325) * 100 * 5% = 0.015}, etc.
     * <li>Analogously, sub-bin B has an approximate mean of
     * {@code 350 + 550 / 2 = 450}, a size of {@code N * (10% - 5%) = 100 * 5% = 5} elements, an approximate sum of
     * {@code 450 * 100 * 5% = 2250}, an approximate sum of squares of
     * {@code 450}<sup>2</sup>{@code * 100 * 5% = 1012500}, an approximate sum of inversions of
     * {@code (1.0 / 450) * 100 * 5% = 0.01}, etc.
     * </ul>
     * <p>
     * Finally, the statistics measures of bin {@code 0} are computed by summing up (integrating) the measures of its
     * sub-intervals: Bin 0 has a size of {@code N * (10% - 0%) = 10} elements (we knew that already), sum of
     * {@code 1625 + 2250 = 3875}, sum of squares of {@code 528125 + 1012500 = 1540625}, sum of inversions of
     * {@code 0.015 + 0.01 = 0.025}, etc. From these follow other measures such as {@code mean = 3875 / 10 = 387.5},
     * {@code rms = sqrt(1540625 / 10) = 392.5}, etc. The other bins are computes analogously.
     *
     * @param percentages The percentage boundaries at which the receiver shall be split.
     * @param k           The desired number of sub-intervals per interval.
     * @implSpec The receiver is divided into {@code s = percentages.size() - 1} intervals (bins). For each interval
     * {@code I}, its minimum and maximum elements are determined based upon quantile computation. Further, each
     * interval {@code I} is split into {@code k} equi-percent-distant sub-intervals (sub-bins). In other words, an
     * interval is split into sub-intervals such that each sub-interval contains the same number of elements.
     * <p>
     * For each sub-interval {@code S}, its minimum and maximum are determined, again, based upon quantile computation.
     * They yield an approximate arithmetic mean {@code am = (min + max) / 2} of the sub-interval. A sub-interval is
     * treated as if it would contain only elements equal to the mean {@code am}. Thus, if the sub-interval contains,
     * say, {@code n} elements, it is assumed to consist of {@code n} mean elements {@code (am,am,...,am)}. A
     * sub-interval's sum of elements, sum of squared elements, sum of inversions, etc. are then approximated using such
     * a sequence of mean elements.
     * <p>
     * Finally, the statistics measures of an interval {@code I} are computed by summing up (integrating) the measures
     * of its sub-intervals.
     */
    public synchronized MightyStaticBin1D[] splitApproximately(final @NotNull ArrayList percentages, final int k) {
        int percentSize = percentages.size();
        if (k < 1 || percentSize < 2)
            throw new IllegalArgumentException();

        double[] percent = percentages.elements();
        int noOfBins = percentSize - 1;

        double[] subbins = new double[1 + k * (percentSize - 1)];
        subbins[0] = percent[0];
        int c = 1;

        for (int i = 0; i < noOfBins; i++) {
            double step = (percent[i + 1] - percent[i]) / k;
            for (int j = 1; j <= k; j++) {
                subbins[c++] = percent[i] + j * step;
            }
        }

        double[] quantiles = quantiles(new ArrayList(subbins)).elements();

        MightyStaticBin1D[] splitBins = new MightyStaticBin1D[noOfBins];
        int maxOrderForSumOfPowers = getMaxOrderForSumOfPowers();
        maxOrderForSumOfPowers = Math.min(10, maxOrderForSumOfPowers);

        int dataSize = this.size();
        c = 0;
        for (int i = 0; i < noOfBins; i++) {
            double step = (percent[i + 1] - percent[i]) / k;
            double binSum = 0;
            double binSumOfSquares = 0;
            double binSumOfLogarithms = 0;
            double binSumOfInversions = 0;
            double[] binSumOfPowers = null;
            if (maxOrderForSumOfPowers > 2) {
                binSumOfPowers = new double[maxOrderForSumOfPowers - 2];
            }

            double binMin = quantiles[c++];
            double safe_min = binMin;
            double subintervalSize = dataSize * step;

            for (int j = 1; j <= k; j++) {
                double binMax = quantiles[c++];
                double binMean = (binMin + binMax) / 2;
                binSum += binMean * subintervalSize;
                binSumOfSquares += binMean * binMean * subintervalSize;
                if (this.hasSumOfLogarithms) {
                    binSumOfLogarithms += (Math.log(binMean)) * subintervalSize;
                }
                if (this.hasSumOfInversions) {
                    binSumOfInversions += (1 / binMean) * subintervalSize;
                }
                if (maxOrderForSumOfPowers >= 3)
                    binSumOfPowers[0] += binMean * binMean * binMean * subintervalSize;
                if (maxOrderForSumOfPowers >= 4)
                    binSumOfPowers[1] += binMean * binMean * binMean * binMean * subintervalSize;
                for (int p = 5; p <= maxOrderForSumOfPowers; p++) {
                    binSumOfPowers[p - 3] += Math.pow(binMean, p) * subintervalSize;
                }

                binMin = binMax;
            }
            c--;

            int binSize = (int) Math.round((percent[i + 1] - percent[i]) * dataSize);
            double binMax = binMin;
            binMin = safe_min;

            splitBins[i] = new MightyStaticBin1D(this.hasSumOfLogarithms, this.hasSumOfInversions,
                maxOrderForSumOfPowers);
            if (binSize > 0) {
                splitBins[i].size = binSize;
                splitBins[i].min = binMin;
                splitBins[i].max = binMax;
                splitBins[i].sum = binSum;
                splitBins[i].sum_xx = binSumOfSquares;
                splitBins[i].sumOfLogarithms = binSumOfLogarithms;
                splitBins[i].sumOfInversions = binSumOfInversions;
                splitBins[i].sumOfPowers = binSumOfPowers;
            }
        }
        return splitBins;
    }

    /**
     * Divides (rebins) a copy of the receiver at the given <i>interval
     * boundaries</i> into bins and returns these bins, such that each bin
     * <i>approximately</i> reflects the data elements of its range.
     * <p>
     * For each interval boundary of the axis (including -infinity and
     * +infinity), computes the percentage (quantile inverse) of elements less
     * than the boundary. Then lets
     * {@link #splitApproximately(ArrayList, int)} do the real work.
     *
     * @param axis an axis defining interval boundaries.
     * @param k    the desired number of sub-intervals per interval.
     */
    public synchronized MightyStaticBin1D[] splitApproximately(final @NotNull IAxis axis, final int k) {
        ArrayList percentages = new ArrayList(new Converter().edges(axis));
        percentages.beforeInsert(0, Double.NEGATIVE_INFINITY);
        percentages.add(Double.POSITIVE_INFINITY);
        for (int i = percentages.size(); --i >= 0; ) {
            percentages.set(i, quantileInverse(percentages.get(i)));
        }

        return splitApproximately(percentages, k);
    }

    /**
     * Returns a String representation of the receiver.
     */
    public synchronized String toString() {
        return super.toString() + "25%, 50%, 75% Quantiles: " + quantile(0.25) + ", "
            + quantile(0.5) + ", " + quantile(0.75) +
            "\nquantileInverse(median): " + quantileInverse(median()) + "\n";
    }
}
