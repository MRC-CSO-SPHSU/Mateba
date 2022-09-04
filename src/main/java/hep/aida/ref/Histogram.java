package hep.aida.ref;

import hep.aida.IHistogram;

import java.io.Serial;

/**
 * Base class for Histogram1D and Histogram2D.
 */
abstract class Histogram implements IHistogram {
    @Serial
    private static final long serialVersionUID = -9034696821608441433L;
    private final String title;

    Histogram(final String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
