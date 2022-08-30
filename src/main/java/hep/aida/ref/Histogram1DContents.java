package hep.aida.ref;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class Histogram1DContents {
    @Getter
    private final double[] errors;
    @Getter
    private final double[] heights;
    @Getter
    private final int[] entries;
    @Getter
    private final int nEntry;
    @Getter
    private final double sumWeight;
    @Getter
    private final double sumWeightSquared;
    @Getter
    private final double mean;
    @Getter
    private final double rms;

    public Histogram1DContents(final int @NotNull [] entries, final double @NotNull [] heights,
                               final double @NotNull [] errors, final int nEntry, final double sumWeight,
                               final double sumWeightSquared, final double mean, final double rms) {
        this.entries = entries;
        this.heights = heights;
        this.errors = errors;
        this.nEntry = nEntry;
        this.sumWeight = sumWeight;
        this.sumWeightSquared = sumWeightSquared;
        this.mean = mean;
        this.rms = rms;
    }
}
