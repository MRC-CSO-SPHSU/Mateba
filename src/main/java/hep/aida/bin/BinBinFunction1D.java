package hep.aida.bin;

/**
 * Interface that represents a function object: a function that takes two bins as arguments and returns a single value.
 */
@SuppressWarnings("unused")
public interface BinBinFunction1D {
    /**
     * Applies a function to two bin arguments.
     *
     * @param x the first argument passed to the function.
     * @param y the second argument passed to the function.
     * @return the result of the function.
     */
    double apply(DynamicBin1D x, DynamicBin1D y);
}
