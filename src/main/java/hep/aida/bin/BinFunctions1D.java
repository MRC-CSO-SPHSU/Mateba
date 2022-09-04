package hep.aida.bin;

/**
 * Function objects computing dynamic bin aggregations; to be passed to generic methods.
 */
@SuppressWarnings("unused")
public class BinFunctions1D {
    /**
     * Little trick to allow for "aliasing", that is, renaming this class. Using the aliasing you can instead write
     * <p>
     * {@code BinFunctions F = BinFunctions.functions; <br> someAlgo(F.max);}
     */
    public static final BinFunctions1D functions = new BinFunctions1D();

    /**
     * Function that returns {@code bin.max()}.
     */
    public static final BinFunction1D max = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.max();
        }

        public String name() {
            return "Max";
        }
    };

    /**
     * Function that returns {@code bin.mean()}.
     */
    public static final BinFunction1D mean = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.mean();
        }

        public String name() {
            return "Mean";
        }
    };

    /**
     * Function that returns {@code bin.median()}.
     */
    public static final BinFunction1D median = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.median();
        }

        public String name() {
            return "Median";
        }
    };

    /**
     * Function that returns {@code bin.min()}.
     */
    public static final BinFunction1D min = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.min();
        }

        public String name() {
            return "Min";
        }
    };

    /**
     * Function that returns {@code bin.rms()}.
     */
    public static final BinFunction1D rms = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.rms();
        }

        public String name() {
            return "RMS";
        }
    };

    /**
     * Function that returns {@code bin.size()}.
     */
    public static final BinFunction1D size = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.size();
        }

        public String name() {
            return "Size";
        }
    };

    /**
     * Function that returns {@code bin.standardDeviation()}.
     */
    public static final BinFunction1D stdDev = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.standardDeviation();
        }

        public String name() {
            return "StdDev";
        }
    };

    /**
     * Function that returns {@code bin.sum()}.
     */
    public static final BinFunction1D sum = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.sum();
        }

        public String name() {
            return "Sum";
        }
    };

    /**
     * Function that returns {@code bin.sumOfLogarithms()}.
     */
    public static final BinFunction1D sumLog = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.sumOfLogarithms();
        }

        public String name() {
            return "SumLog";
        }
    };

    /**
     * Function that returns {@code bin.geometricMean()}.
     */
    public static final BinFunction1D geometricMean = new BinFunction1D() {
        public double apply(DynamicBin1D bin) {
            return bin.geometricMean();
        }

        public String name() {
            return "GeomMean";
        }
    };

    /**
     * Makes this class non instantiable, but still let's others inherit from
     * it.
     */
    protected BinFunctions1D() {
    }

    /**
     * Function that returns {@code bin.quantile(percentage)}.
     *
     * @param percentage the percentage of the quantile ({@code 0 <= percentage <= 1}
     *                   ).
     */
    public static BinFunction1D quantile(final double percentage) {
        return new BinFunction1D() {
            public double apply(DynamicBin1D bin) {
                return bin.quantile(percentage);
            }

            public String name() {
                return new cern.mateba.matrix.FormerFactory().create("%1.2G").form(percentage * 100) + "% Q.";
            }
        };
    }
}
