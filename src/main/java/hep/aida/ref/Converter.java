package hep.aida.ref;

import cern.mateba.list.tobject.ObjectArrayList;
import cern.mateba.matrix.Former;
import cern.mateba.matrix.FormerFactory;
import cern.mateba.matrix.tdouble.DoubleMatrix2D;
import cern.mateba.matrix.tdouble.DoubleMatrix3D;
import cern.mateba.matrix.tdouble.algo.DoubleFormatter;
import cern.mateba.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.mateba.matrix.tdouble.impl.DenseDoubleMatrix3D;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.bin.BinFunction1D;
import hep.aida.bin.BinFunctions1D;

/**
 * Histogram conversions, for example to String and XML format; This class requires the Mateba distribution, whereas the
 * rest of the package is entirely stand-alone.
 */
@SuppressWarnings("unused")
public class Converter {
    /**
     * Creates a new histogram converter.
     */
    public Converter() {
    }

    /**
     * Returns all edges of the given axis.
     */
    public double[] edges(IAxis axis) {
        int b = axis.bins();
        double[] bounds = new double[b + 1];
        for (int i = 0; i < b; i++)
            bounds[i] = axis.binLowerEdge(i);
        bounds[b] = axis.upperEdge();
        return bounds;
    }

    String form(Former formatter, double value) {
        return formatter.form(value);
    }

    /**
     * Returns an array[h.xAxis().bins()]; ignoring extra bins.
     */
    protected double[] toArrayErrors(IHistogram1D h) {
        int xBins = h.xAxis().bins();
        double[] array = new double[xBins];
        for (int j = xBins; --j >= 0;) {
            array[j] = h.binError(j);
        }
        return array;
    }

    /**
     * Returns an array[h.xAxis().bins()][h.yAxis().bins()]; ignoring extra bins.
     */
    protected double[][] toArrayErrors(IHistogram2D h) {
        int xBins = h.xAxis().bins();
        int yBins = h.yAxis().bins();
        double[][] array = new double[xBins][yBins];
        for (int i = yBins; --i >= 0;) {
            for (int j = xBins; --j >= 0;) {
                array[j][i] = h.binError(j, i);
            }
        }
        return array;
    }

    /**
     * Returns an array[h.xAxis().bins()]; ignoring extra bins.
     */
    protected double[] toArrayHeights(IHistogram1D h) {
        int xBins = h.xAxis().bins();
        double[] array = new double[xBins];
        for (int j = xBins; --j >= 0;) {
            array[j] = h.binHeight(j);
        }
        return array;
    }

    /**
     * Returns an array[h.xAxis().bins()][h.yAxis().bins()]; ignoring extra
     * bins.
     */
    protected double[][] toArrayHeights(IHistogram2D h) {
        int xBins = h.xAxis().bins();
        int yBins = h.yAxis().bins();
        double[][] array = new double[xBins][yBins];
        for (int i = yBins; --i >= 0;) {
            for (int j = xBins; --j >= 0;) {
                array[j][i] = h.binHeight(j, i);
            }
        }
        return array;
    }

    /**
     * Returns an array[h.xAxis().bins()][h.yAxis().bins()][h.zAxis().bins()]; ignoring extra bins.
     */
    protected double[][][] toArrayHeights(IHistogram3D h) {
        int xBins = h.xAxis().bins();
        int yBins = h.yAxis().bins();
        int zBins = h.zAxis().bins();
        double[][][] array = new double[xBins][yBins][zBins];
        for (int j = xBins; --j >= 0;) {
            for (int i = yBins; --i >= 0;) {
                for (int k = zBins; --k >= 0;) {
                    array[j][i][k] = h.binHeight(j, i, k);
                }
            }
        }
        return array;
    }

    /**
     * Returns a string representation of the specified array. The string
     * representation consists of a list of the arrays's elements, enclosed in
     * square brackets (<tt>"[]"</tt>). Adjacent elements are separated by the
     * characters <tt>", "</tt> (comma and space).
     *
     * @return a string representation of the specified array.
     */
    protected static String toString(double[] array) {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        int maxIndex = array.length - 1;
        for (int i = 0; i <= maxIndex; i++) {
            buf.append(array[i]);
            if (i < maxIndex)
                buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

    /**
     * Returns a string representation of the given argument.
     */
    public String toString(IAxis axis) {
        return "Range: [" + axis.lowerEdge() + "," + axis.upperEdge() + ")" +
            ", Bins: " + axis.bins() +
            ", Bin edges: " + toString(edges(axis)) + "\n";
    }

    /**
     * Returns a string representation of the given argument.
     */
    public String toString(IHistogram1D h) {
        String columnAxisName = null;
        String rowAxisName = null;
        BinFunction1D[] aggr = null;
        String format = "%G";

        Former f = new FormerFactory().create(format);
        String sep = System.getProperty("line.separator");
        int[] minMaxBins = h.minMaxBins();
        String title = h.title() + ":" + sep + "   Entries=" + form(f, h.entries()) + ", ExtraEntries="
                + form(f, h.extraEntries()) + sep + "   Mean=" + form(f, h.mean()) + ", Rms=" + form(f, h.rms()) + sep
                + "   MinBinHeight=" + form(f, h.binHeight(minMaxBins[0])) + ", MaxBinHeight="
                + form(f, h.binHeight(minMaxBins[1])) + sep + "   Axis: " + "Bins=" + form(f, h.xAxis().bins())
                + ", Min=" + form(f, h.xAxis().lowerEdge()) + ", Max=" + form(f, h.xAxis().upperEdge());

        String[] xEdges = new String[h.xAxis().bins()];
        for (int i = 0; i < h.xAxis().bins(); i++)
            xEdges[i] = form(f, h.xAxis().binLowerEdge(i));


        DoubleMatrix2D heights = new DenseDoubleMatrix2D(1, h.xAxis().bins());
        heights.viewRow(0).assign(toArrayHeights(h));

        return "%s%sHeights:%s%s".formatted(title, sep, sep, new DoubleFormatter().toTitleString(heights,
            null, xEdges, null, null, null, aggr));

    }

    /**
     * Returns a string representation of the given argument.
     */
    public String toString(IHistogram2D h) {
        String columnAxisName = "X";
        String rowAxisName = "Y";
        BinFunction1D[] aggr = { BinFunctions1D.sum };
        String format = "%G";

        Former f = new FormerFactory().create(format);
        String sep = System.getProperty("line.separator");
        int[] minMaxBins = h.minMaxBins();
        String title = h.title() + ":" + sep + "   Entries=" + form(f, h.entries()) + ", ExtraEntries="
                + form(f, h.extraEntries()) + sep + "   MeanX=" + form(f, h.meanX()) + ", RmsX=" + form(f, h.rmsX())
                + sep + "   MeanY=" + form(f, h.meanY()) + ", RmsY=" + form(f, h.rmsX()) + sep + "   MinBinHeight="
                + form(f, h.binHeight(minMaxBins[0], minMaxBins[1])) + ", MaxBinHeight="
                + form(f, h.binHeight(minMaxBins[2], minMaxBins[3])) + sep +

                "   xAxis: " + "Bins=" + form(f, h.xAxis().bins()) + ", Min=" + form(f, h.xAxis().lowerEdge())
                + ", Max=" + form(f, h.xAxis().upperEdge()) + sep +

                "   yAxis: " + "Bins=" + form(f, h.yAxis().bins()) + ", Min=" + form(f, h.yAxis().lowerEdge())
                + ", Max=" + form(f, h.yAxis().upperEdge());

        String[] xEdges = new String[h.xAxis().bins()];
        for (int i = 0; i < h.xAxis().bins(); i++)
            xEdges[i] = form(f, h.xAxis().binLowerEdge(i));

        String[] yEdges = new String[h.yAxis().bins()];
        for (int i = 0; i < h.yAxis().bins(); i++)
            yEdges[i] = form(f, h.yAxis().binLowerEdge(i));
        new ObjectArrayList(yEdges).reverse();

        DoubleMatrix2D heights = new DenseDoubleMatrix2D(toArrayHeights(h));
        heights = heights.viewDice().viewRowFlip();

        return title
                + sep
                + "Heights:"
                + sep
                + new DoubleFormatter().toTitleString(heights, yEdges, xEdges, rowAxisName, columnAxisName, null, aggr);
    }

    /**
     * Returns a string representation of the given argument.
     */
    public String toString(IHistogram3D h) {
        String columnAxisName = "X";
        String rowAxisName = "Y";
        String sliceAxisName = "Z";
        BinFunction1D[] aggr = { BinFunctions1D.sum };
        String format = "%G";

        Former f = new FormerFactory().create(format);
        String sep = System.getProperty("line.separator");
        int[] minMaxBins = h.minMaxBins();
        String title = h.title() + ":" + sep + "   Entries=" + form(f, h.entries()) + ", ExtraEntries="
                + form(f, h.extraEntries()) + sep + "   MeanX=" + form(f, h.meanX()) + ", RmsX=" + form(f, h.rmsX())
                + sep + "   MeanY=" + form(f, h.meanY()) + ", RmsY=" + form(f, h.rmsX()) + sep + "   MeanZ="
                + form(f, h.meanZ()) + ", RmsZ=" + form(f, h.rmsZ()) + sep + "   MinBinHeight="
                + form(f, h.binHeight(minMaxBins[0], minMaxBins[1], minMaxBins[2])) + ", MaxBinHeight="
                + form(f, h.binHeight(minMaxBins[3], minMaxBins[4], minMaxBins[5])) + sep +

                "   xAxis: " + "Bins=" + form(f, h.xAxis().bins()) + ", Min=" + form(f, h.xAxis().lowerEdge())
                + ", Max=" + form(f, h.xAxis().upperEdge()) + sep +

                "   yAxis: " + "Bins=" + form(f, h.yAxis().bins()) + ", Min=" + form(f, h.yAxis().lowerEdge())
                + ", Max=" + form(f, h.yAxis().upperEdge()) + sep +

                "   zAxis: " + "Bins=" + form(f, h.zAxis().bins()) + ", Min=" + form(f, h.zAxis().lowerEdge())
                + ", Max=" + form(f, h.zAxis().upperEdge());

        String[] xEdges = new String[h.xAxis().bins()];
        for (int i = 0; i < h.xAxis().bins(); i++)
            xEdges[i] = form(f, h.xAxis().binLowerEdge(i));

        String[] yEdges = new String[h.yAxis().bins()];
        for (int i = 0; i < h.yAxis().bins(); i++)
            yEdges[i] = form(f, h.yAxis().binLowerEdge(i));
        new ObjectArrayList(yEdges).reverse();

        String[] zEdges = new String[h.zAxis().bins()];
        for (int i = 0; i < h.zAxis().bins(); i++)
            zEdges[i] = form(f, h.zAxis().binLowerEdge(i));
        new ObjectArrayList(zEdges).reverse();

        DoubleMatrix3D heights = new DenseDoubleMatrix3D(toArrayHeights(h));
        heights = heights.viewDice(2, 1, 0).viewSliceFlip().viewRowFlip();
        return title
                + sep
                + "Heights:"
                + sep
                + new DoubleFormatter().toTitleString(heights, zEdges, yEdges, xEdges,
                        sliceAxisName, rowAxisName, columnAxisName, "", aggr);
    }

    /**
     * Returns a XML representation of the given argument.
     */
    public String toXML(IHistogram1D h) {
        StringBuilder buf = new StringBuilder();
        String sep = System.getProperty("line.separator");
        buf.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        buf.append(sep);
        buf.append("<!DOCTYPE plotML SYSTEM \"plotML.dtd\">");
        buf.append(sep);
        buf.append("<plotML>");
        buf.append(sep);
        buf.append("<plot>");
        buf.append(sep);
        buf.append("<dataArea>");
        buf.append(sep);
        buf.append("<data1d>");
        buf.append(sep);
        buf.append("<bins1d title=\"").append(h.title()).append("\">");
        buf.append(sep);
        for (int i = 0; i < h.xAxis().bins(); i++) {
            buf.append(h.binEntries(i)).append(",").append(h.binError(i));
            buf.append(sep);
        }
        buf.append("</bins1d>");
        buf.append(sep);
        buf.append("<binnedDataAxisAttributes type=\"double\" axis=\"x0\"");
        buf.append(" min=\"").append(h.xAxis().lowerEdge()).append("\"");
        buf.append(" max=\"").append(h.xAxis().upperEdge()).append("\"");
        buf.append(" numberOfBins=\"").append(h.xAxis().bins()).append("\"");
        buf.append("/>");
        buf.append(sep);
        buf.append("<statistics>");
        buf.append(sep);
        buf.append("<statistic name=\"Entries\" value=\"").append(h.entries()).append("\"/>");
        buf.append(sep);
        buf.append("<statistic name=\"Underflow\" value=\"").append(h.binEntries(h.UNDERFLOW)).append("\"/>");
        buf.append(sep);
        buf.append("<statistic name=\"Overflow\" value=\"").append(h.binEntries(h.OVERFLOW)).append("\"/>");
        buf.append(sep);
        if (!Double.isNaN(h.mean())) {
            buf.append("<statistic name=\"Mean\" value=\"").append(h.mean()).append("\"/>");
            buf.append(sep);
        }
        if (!Double.isNaN(h.rms())) {
            buf.append("<statistic name=\"RMS\" value=\"").append(h.rms()).append("\"/>");
            buf.append(sep);
        }
        buf.append("</statistics>");
        buf.append(sep);
        buf.append("</data1d>");
        buf.append(sep);
        buf.append("</dataArea>");
        buf.append(sep);
        buf.append("</plot>");
        buf.append(sep);
        buf.append("</plotML>");
        buf.append(sep);
        return buf.toString();
    }

    /**
     * Returns a XML representation of the given argument.
     */
    public String toXML(IHistogram2D h) {
        StringBuilder out = new StringBuilder();
        String sep = System.getProperty("line.separator");
        out.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        out.append(sep);
        out.append("<!DOCTYPE plotML SYSTEM \"plotML.dtd\">");
        out.append(sep);
        out.append("<plotML>");
        out.append(sep);
        out.append("<plot>");
        out.append(sep);
        out.append("<dataArea>");
        out.append(sep);
        out.append("<data2d type=\"xxx\">");
        out.append(sep);
        out.append("<bins2d title=\"").append(h.title()).append("\" xSize=\"").append(h.xAxis().bins()).append("\" ySize=\"").append(h.yAxis().bins()).append("\">");
        out.append(sep);
        for (int i = 0; i < h.xAxis().bins(); i++)
            for (int j = 0; j < h.yAxis().bins(); j++) {
                out.append(h.binEntries(i, j)).append(",").append(h.binError(i, j));
                out.append(sep);
            }
        out.append("</bins2d>");
        out.append(sep);
        out.append("<binnedDataAxisAttributes type=\"double\" axis=\"x0\"");
        out.append(" min=\"").append(h.xAxis().lowerEdge()).append("\"");
        out.append(" max=\"").append(h.xAxis().upperEdge()).append("\"");
        out.append(" numberOfBins=\"").append(h.xAxis().bins()).append("\"");
        out.append("/>");
        out.append(sep);
        out.append("<binnedDataAxisAttributes type=\"double\" axis=\"y0\"");
        out.append(" min=\"").append(h.yAxis().lowerEdge()).append("\"");
        out.append(" max=\"").append(h.yAxis().upperEdge()).append("\"");
        out.append(" numberOfBins=\"").append(h.yAxis().bins()).append("\"");
        out.append("/>");
        out.append(sep);
        out.append("</data2d>");
        out.append(sep);
        out.append("</dataArea>");
        out.append(sep);
        out.append("</plot>");
        out.append(sep);
        out.append("</plotML>");
        out.append(sep);
        return out.toString();
    }
}
