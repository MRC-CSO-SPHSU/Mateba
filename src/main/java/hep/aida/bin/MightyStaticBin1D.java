package hep.aida.bin;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * Static and the same as its superclass, except that it can do more: Additionally computes moments of arbitrary integer
 * order, harmonic mean, geometric mean, etc.
 * <p>
 * Constructors need to be told what functionality is required for the given use case. Only maintains aggregate measures
 * (incrementally) - the added elements themselves are not kept.
 */
@SuppressWarnings("unused")
public class MightyStaticBin1D extends StaticBin1D {

    @Serial
    private static final long serialVersionUID = -7737747083287966813L;
    protected boolean hasSumOfLogarithms;

    protected double sumOfLogarithms = 0.0;

    protected boolean hasSumOfInversions;

    protected double sumOfInversions = 0.0;

    protected double[] sumOfPowers = null;

    /**
     * Constructs and returns an empty bin with limited functionality but good performance; equivalent to
     * {@code MightyStaticBin1D(false, false, 4)}.
     */
    public MightyStaticBin1D() {
        this(false, false, 4);
    }

    /**
     * Constructs and returns an empty bin with the given capabilities.
     *
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
     *                               substitutes {@code Math.max(2,maxOrderForSumOfPowers)} for the parameter passed in.
     *                               Thus, {@code sumOfPowers(0..2)} always returns meaningful results.
     * @see #hasSumOfPowers(int)
     * @see #moment(int, double)
     */
    public MightyStaticBin1D(final boolean hasSumOfLogarithms, final boolean hasSumOfInversions,
                             final int maxOrderForSumOfPowers) {
        setMaxOrderForSumOfPowers(maxOrderForSumOfPowers);
        this.hasSumOfLogarithms = hasSumOfLogarithms;
        this.hasSumOfInversions = hasSumOfInversions;
        this.clear();
    }

    /**
     * Adds the part of the specified list between indexes {@code from} (inclusive) and {@code to} (inclusive) to the
     * receiver.
     *
     * @param list the list of which elements shall be added.
     * @param from the index of the first element to be added (inclusive).
     * @param to   the index of the last element to be added (inclusive).
     * @throws IndexOutOfBoundsException if {@code list.size() > 0 && (from < 0 || from > to || to >= list.size())}
     *                                   .
     */
    public synchronized void addAllOfFromTo(final @NotNull ArrayList list, final int from, final int to) {
        super.addAllOfFromTo(list, from, to);

        if (this.sumOfPowers != null) {
            Descriptive.incrementalUpdateSumsOfPowers(list, from, to, 3, getMaxOrderForSumOfPowers(), this.sumOfPowers);
        }

        if (this.hasSumOfInversions) {
            this.sumOfInversions += Descriptive.sumOfInversions(list, from, to);
        }

        if (this.hasSumOfLogarithms) {
            this.sumOfLogarithms += Descriptive.sumOfLogarithms(list, from, to);
        }
    }

    /**
     * Resets the values of all measures.
     */
    protected void clearAllMeasures() {
        super.clearAllMeasures();

        this.sumOfLogarithms = 0.0;
        this.sumOfInversions = 0.0;

        if (this.sumOfPowers != null) for (int i = this.sumOfPowers.length; --i >= 0; ) this.sumOfPowers[i] = 0.0;
    }

    /**
     * Returns a deep copy of the receiver.
     *
     * @return a deep copy of the receiver.
     */
    public synchronized MightyStaticBin1D clone() {
        MightyStaticBin1D clone = (MightyStaticBin1D) super.clone();
        if (this.sumOfPowers != null)
            clone.sumOfPowers = clone.sumOfPowers.clone();
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
        if (other instanceof MightyStaticBin1D m) {
            if (hasSumOfLogarithms() && m.hasSumOfLogarithms())
                buf.append("geometric mean: ").append(relError(geometricMean(), m.geometricMean())).append(" %\n");
            if (hasSumOfInversions() && m.hasSumOfInversions())
                buf.append("harmonic mean: ").append(relError(harmonicMean(), m.harmonicMean())).append(" %\n");
            if (hasSumOfPowers(3) && m.hasSumOfPowers(3))
                buf.append("skew: ").append(relError(skew(), m.skew())).append(" %\n");
            if (hasSumOfPowers(4) && m.hasSumOfPowers(4))
                buf.append("kurtosis: ").append(relError(kurtosis(), m.kurtosis())).append(" %\n");
            buf.append("\n");
        }
        return buf.toString();
    }

    /**
     * Returns the geometric mean, which is {@code Product( x[i] )<sup>1.0/size()</sup>}.
     * <p>
     * This method tries to avoid overflows at the expense of an equivalent but somewhat inefficient definition:
     * {@code geoMean = exp(Sum(Log(x[i])) / size())}. Note that for a geometric mean to be meaningful, the minimum of
     * the data sequence must not be less or equal to zero.
     *
     * @return the geometric mean; {@code Double.NaN} if {@code !hasSumOfLogarithms()}.
     */
    public synchronized double geometricMean() {
        return Descriptive.geometricMean(size(), sumOfLogarithms());
    }

    /**
     * Returns the maximum order {@code k} for which sums of powers are retrievable, as specified upon instance
     * construction.
     *
     * @implSpec {@code order 0..2} is always recorded. {@code order 0} is {@link #size()} {@code order 1} is
     * {@link #sum()}, {@code order 2} is {@link #sumOfSquares()}.
     * @see #hasSumOfPowers(int)
     * @see #sumOfPowers(int)
     */
    public synchronized int getMaxOrderForSumOfPowers() {
        return this.sumOfPowers == null ? 2 : 2 + this.sumOfPowers.length;
    }

    /**
     * Sets the range of orders in which sums of powers are to be computed. In other words, {@code sumOfPower(k)} will
     * return {@code Sum(x[i]^k)} if {@code min_k <= k <= max_k || 0 <= k <= 2} and throw an exception otherwise.
     */
    protected void setMaxOrderForSumOfPowers(final int max_k) {
        this.sumOfPowers = max_k <= 2 ? null : new double[max_k - 2];
    }

    /**
     * Returns the minimum order {@code k} for which sums of powers are retrievable, as specified upon instance
     * construction.
     */
    public synchronized int getMinOrderForSumOfPowers() {
        return hasSumOfInversions() ? -1 : 0;
    }

    /**
     * Returns the harmonic mean, which is {@code size() / Sum(1 / x[i])}. Remember: If the receiver contains at least
     * one element of {@code 0.0}, the harmonic mean is {@code 0.0}.
     *
     * @return the harmonic mean; {@code Double.NaN} if {@code !hasSumOfInversions()}.
     */
    public synchronized double harmonicMean() {
        return Descriptive.harmonicMean(size(), sumOfInversions());
    }

    /**
     * Returns whether {@code sumOfInversions()} can return meaningful results.
     *
     * @return {@code false} if the bin was constructed with insufficient parametrization, {@code true} otherwise. See
     * the constructors for proper parametrization.
     */
    public boolean hasSumOfInversions() {
        return this.hasSumOfInversions;
    }

    /**
     * Tells whether {@code sumOfLogarithms()} can return meaningful results.
     *
     * @return {@code false} if the bin was constructed with insufficient parametrization, {@code true} otherwise. See
     * the constructors for proper parametrization.
     */
    public boolean hasSumOfLogarithms() {
        return this.hasSumOfLogarithms;
    }

    /**
     * Tells whether {@code sumOfPowers(k)} can return meaningful results.
     * Defined as {@code hasSumOfPowers(k) <==> getMinOrderForSumOfPowers() <= k && k <= getMaxOrderForSumOfPowers()}.
     * A return value of {@code true} implies that {@code hasSumOfPowers(k-1) .. hasSumOfPowers(0)} will also return
     * {@code true}. See the constructors for proper parametrization.
     * <p>
     * <b>Details</b>: {@code hasSumOfPowers(0..2)} will always yield {@code true}.
     * {@code hasSumOfPowers(-1) <==> hasSumOfInversions()}.
     *
     * @return {@code false} if the bin was constructed with insufficient parametrization, {@code true} otherwise.
     */
    public boolean hasSumOfPowers(final int k) {
        return getMinOrderForSumOfPowers() <= k && k <= getMaxOrderForSumOfPowers();
    }

    /**
     * Returns the kurtosis (aka excess), which is {@code -3 + moment(4, mean()) / standardDeviation()}<sup>4</sup>.
     *
     * @return the kurtosis; {@code Double.NaN} if {@code !hasSumOfPowers(4)}.
     */
    public synchronized double kurtosis() {
        return Descriptive.kurtosis(moment(4, mean()), standardDeviation());
    }

    /**
     * Returns the moment of {@code k}-th order with value {@code c}, which is
     * {@code Sum((x[i] - c)}<sup>k</sup>{@code ) / size()}.
     *
     * @param k the order; must be greater than or equal to zero.
     * @param c any number.
     * @return {@code Double.NaN} if {@code !hasSumOfPower(k)}.
     * @throws IllegalArgumentException if {@code k < 0}.
     */
    public synchronized double moment(final int k, final double c) {
        if (k < 0) throw new IllegalArgumentException("k must be >= 0");

        if (!hasSumOfPowers(k)) return Double.NaN;

        int maxOrder = Math.min(k, getMaxOrderForSumOfPowers());
        ArrayList sumOfPows = new ArrayList(maxOrder + 1);
        sumOfPows.add(size());
        sumOfPows.add(sum());
        sumOfPows.add(sumOfSquares());
        for (int i = 3; i <= maxOrder; i++) sumOfPows.add(sumOfPowers(i));

        return Descriptive.moment(k, c, size(), sumOfPows.elements());
    }

    /**
     * Returns the product, which is {@code Prod(x[i])}. In other words: {@code x[0] * x[1] * ... * x[size() - 1]}.
     *
     * @return the product; {@code Double.NaN} if {@code !hasSumOfLogarithms()}.
     */
    public double product() {
        return Descriptive.product(size(), sumOfLogarithms());
    }

    /**
     * Returns the skew, which is {@code moment(3, mean()) / standardDeviation()}<sup>3</sup>.
     *
     * @return the skew; {@code Double.NaN} if {@code !hasSumOfPowers(3)}.
     */
    public synchronized double skew() {
        return Descriptive.skew(moment(3, mean()), standardDeviation());
    }

    /**
     * Returns the sum of inversions, which is {@code Sum(1 / x[i])}.
     *
     * @return the sum of inversions; {@code Double.NaN} if {@code !hasSumOfInversions()}.
     */
    public double sumOfInversions() {
        return this.hasSumOfInversions ? this.sumOfInversions : Double.NaN;
    }

    /**
     * Returns the sum of logarithms, which is {@code Sum(Log(x[i]))}.
     *
     * @return the sum of logarithms; {@code Double.NaN} if {@code !hasSumOfLogarithms()}.
     */
    public synchronized double sumOfLogarithms() {
        return this.hasSumOfLogarithms ? this.sumOfLogarithms : Double.NaN;
    }

    /**
     * Returns the {@code k-th} order sum of powers, which is {@code Sum(x[i]}<sup>k</sup>{@code )}.
     *
     * @param k the order of the powers.
     * @return the sum of powers; {@code Double.NaN} if {@code !hasSumOfPowers(k)}.
     */
    public synchronized double sumOfPowers(final int k) {
        if (!hasSumOfPowers(k)) return Double.NaN;
        return switch (k) {
            case -1 -> sumOfInversions();
            case 0 -> size();
            case 1 -> sum();
            case 2 -> sumOfSquares();
            default -> this.sumOfPowers[k - 3];
        };
    }

    /**
     * Returns a String representation of the receiver.
     */

    public synchronized String toString() {
        StringBuilder buf = new StringBuilder(super.toString());

        if (hasSumOfLogarithms()) {
            buf.append("Geometric mean: ").append(geometricMean());
            buf.append("\nProduct: ").append(product()).append("\n");
        }

        if (hasSumOfInversions()) {
            buf.append("Harmonic mean: ").append(harmonicMean());
            buf.append("\nSum of inversions: ").append(sumOfInversions()).append("\n");
        }

        int maxOrder = getMaxOrderForSumOfPowers();
        int maxPrintOrder = Math.min(6, maxOrder);

        if (maxOrder > 2) {
            buf.append("Skew: ").append(skew()).append("\n");
            if (maxOrder >= 4) buf.append("Kurtosis: ").append(kurtosis()).append("\n");
            for (int i = 3; i <= maxPrintOrder; i++)
                buf.append("Sum of powers(").append(i).append("): ").append(sumOfPowers(i)).append("\n");
            for (int k = 0; k <= maxPrintOrder; k++)
                buf.append("Moment(").append(k).append(",0): ").append(moment(k, 0)).append("\n");
            for (int k = 0; k <= maxPrintOrder; k++)
                buf.append("Moment(").append(k).append(",mean()): ").append(moment(k, mean())).append("\n");
        }
        return buf.toString();
    }

    /**
     * Returns whether two bins are equal; They are equal if the other object is of the same class or a subclass of this
     * class and both have the same size, minimum, maximum, sum, sumOfSquares, sumOfInversions and
     * sumOfLogarithms.
     */
    protected boolean xequals(final Object object) {
        if (!(object instanceof MightyStaticBin1D other)) return false;
        return super.equals(other) && sumOfInversions() == other.sumOfInversions()
            && sumOfLogarithms() == other.sumOfLogarithms();
    }

    /**
     * Tells whether {@code sumOfPowers(fromK) .. sumOfPowers(toK)} can return meaningful results.
     *
     * @return {@code false} if the bin was constructed with insufficient parametrization, {@code true} otherwise. See
     * the constructors for proper parametrization.
     * @throws IllegalArgumentException if {@code fromK > toK}.
     */
    protected boolean xhasSumOfPowers(final int fromK, final int toK) {
        if (fromK > toK) throw new IllegalArgumentException("fromK must be less or equal to toK");
        return getMinOrderForSumOfPowers() <= fromK && toK <= getMaxOrderForSumOfPowers();
    }

    /**
     * Returns {@code getMinOrderForSumOfPowers() <= k && k <= getMaxOrderForSumOfPowers()}.
     */
    protected synchronized boolean xisLegalOrder(final int k) {
        return getMinOrderForSumOfPowers() <= k && k <= getMaxOrderForSumOfPowers();
    }
}
