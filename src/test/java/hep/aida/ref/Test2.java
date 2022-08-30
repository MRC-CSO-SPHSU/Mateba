package hep.aida.ref;

import hep.aida.tdouble.ref.DoubleConverter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * A very(!) basic test of the reference implementations of AIDA histograms
 */
public class Test2 {
    public static void main2(String[] argv) {
        Random r = new Random();
        hep.aida.tdouble.IHistogram1D h1 = new hep.aida.tdouble.ref.Histogram1D("AIDA 1D Histogram", 40, -3, 3);
        for (int i = 0; i < 10000; i++)
            h1.fill(r.nextGaussian());

        hep.aida.tdouble.IHistogram2D h2 = new hep.aida.tdouble.ref.Histogram2D("AIDA 2D Histogram", 40, -3, 3, 40, -3, 3);
        for (int i = 0; i < 10000; i++)
            h2.fill(r.nextGaussian(), r.nextGaussian());

        // Write the results as a PlotML files!
        writeAsXML(h1, "aida1.xml");
        writeAsXML(h2, "aida2.xml");

        // Try some projections

        writeAsXML(h2.projectionX(), "projectionX.xml");
        writeAsXML(h2.projectionY(), "projectionY.xml");
    }

    public static void main(String[] argv) {
        double[] bounds = { -30, 0, 30, 1000 };
        Random r = new Random();
        hep.aida.tdouble.IHistogram1D h1 = new hep.aida.tdouble.ref.Histogram1D("AIDA 1D Histogram", new hep.aida.tdouble.ref.VariableAxis(bounds));
        // IHistogram1D h1 = new Histogram1D("AIDA 1D Histogram",2,-3,3);
        for (int i = 0; i < 10000; i++)
            h1.fill(r.nextGaussian());

        hep.aida.tdouble.IHistogram2D h2 = new hep.aida.tdouble.ref.Histogram2D("AIDA 2D Histogram", new hep.aida.tdouble.ref.VariableAxis(bounds),
                new hep.aida.tdouble.ref.VariableAxis(bounds));
        // IHistogram2D h2 = new Histogram2D("AIDA 2D Histogram",2,-3,3,
        // 2,-3,3);
        for (int i = 0; i < 10000; i++)
            h2.fill(r.nextGaussian(), r.nextGaussian());

        // IHistogram3D h3 = new Histogram3D("AIDA 3D Histogram",new
        // VariableAxis(bounds),new VariableAxis(bounds),new
        // VariableAxis(bounds));
        hep.aida.tdouble.IHistogram3D h3 = new hep.aida.tdouble.ref.Histogram3D("AIDA 3D Histogram", 10, -2, +2, 5, -2, +2, 3, -2, +2);
        for (int i = 0; i < 10000; i++)
            h3.fill(r.nextGaussian(), r.nextGaussian(), r.nextGaussian());

        // Write the results as a PlotML files!
        writeAsXML(h1, "aida1.xml");
        writeAsXML(h2, "aida2.xml");
        writeAsXML(h3, "aida2.xml");

        // Try some projections

        writeAsXML(h2.projectionX(), "projectionX.xml");
        writeAsXML(h2.projectionY(), "projectionY.xml");
    }

    private static void writeAsXML(hep.aida.tdouble.IHistogram1D h, String filename) {
        // System.out.println(new Converter().toString(h));
        // System.out.println(new Converter().toXML(h));

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            out.println(new Converter().toXML(h));
            out.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private static void writeAsXML(hep.aida.tdouble.IHistogram2D h, String filename) {
        // System.out.println(new Converter().toString(h));
        // System.out.println(new Converter().toXML(h));

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            out.println(new Converter().toXML(h));
            out.close();
        } catch (IOException x) {
            x.printStackTrace();
        }

    }

    private static void writeAsXML(hep.aida.tdouble.IHistogram3D h, String filename) {
        System.out.println(new Converter().toString(h));
        // System.out.println(new Converter().toXML(h));
        /*
         * try { PrintWriter out = new PrintWriter(new FileWriter(filename));
         * out.println(new Converter().toXML(h)); out.close(); } catch
         * (IOException x) { x.printStackTrace(); }
         */
    }
}
