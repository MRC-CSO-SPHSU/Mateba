/*
Copyright (C) 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
package cern.mateba.matrix;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract base class for flexible, well human readable matrix print
 * formatting. Value type independent. A single cell is formatted via a format
 * string. Columns can be aligned left, centered, right and by decimal point.
 * <p>
 * A column can be broader than specified by the parameter
 * <tt>minColumnWidth</tt> (because a cell may not fit into that width) but a
 * column is never smaller than <tt>minColumnWidth</tt>. Normally one does not
 * need to specify <tt>minColumnWidth</tt>. Cells in a row are separated by a
 * separator string, similar separators can be set for rows and slices. For more
 * info, see the concrete subclasses.
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public abstract class AbstractFormatter implements Serializable, Cloneable {

    /**
     * The alignment string aligning the cells of a column to the left.
     */
    public static final String LEFT = "left";

    /**
     * The alignment string aligning the cells of a column to its center.
     */
    public static final String CENTER = "center";

    /**
     * The alignment string aligning the cells of a column to the right.
     */
    public static final String RIGHT = "right";

    /**
     * The alignment string aligning the cells of a column to the decimal point.
     */
    public static final String DECIMAL = "decimal";

    /**
     * The default minimum number of characters a column may have; currently
     * <tt>1</tt>.
     */
    public static final int DEFAULT_MIN_COLUMN_WIDTH = 1;

    /**
     * The default string separating any two columns from another; currently
     * <tt>" "</tt>.
     */
    public static final String DEFAULT_COLUMN_SEPARATOR = " ";

    /**
     * The default string separating any two rows from another; currently
     * <tt>"\n"</tt>.
     */
    public static final String DEFAULT_ROW_SEPARATOR = "\n";

    /**
     * The default string separating any two slices from another; currently
     * <tt>"\n\n"</tt>.
     */
    public static final String DEFAULT_SLICE_SEPARATOR = "\n\n";
    @Serial
    private static final long serialVersionUID = 5162429376798960380L;

    /**
     * The default format string for formatting a single cell value; currently
     * <tt>"%G"</tt>.
     */
    protected String alignment = LEFT;

    /**
     * The default format string for formatting a single cell value; currently
     * <tt>"%G"</tt>.
     */
    protected String format = "%G";

    /**
     * The default minimum number of characters a column may have; currently
     * <tt>1</tt>.
     */
    protected int minColumnWidth = DEFAULT_MIN_COLUMN_WIDTH;

    /**
     * The default string separating any two columns from another; currently
     * <tt>" "</tt>.
     */
    protected String columnSeparator = DEFAULT_COLUMN_SEPARATOR;

    /**
     * The default string separating any two rows from another; currently
     * <tt>"\n"</tt>.
     */
    protected String rowSeparator = DEFAULT_ROW_SEPARATOR;

    /**
     * The default string separating any two slices from another; currently
     * <tt>"\n\n"</tt>.
     */
    protected String sliceSeparator = DEFAULT_SLICE_SEPARATOR;

    /**
     * Tells whether String representations are to be preceded with summary of
     * the shape; currently <tt>true</tt>.
     */
    protected boolean printShape = true;

    private static String[] blanksCache; // for efficient String manipulations

    protected static final FormerFactory factory = new FormerFactory();

    static {
        setupBlanksCache();
    }

    /**
     * Makes this class non instantiable, but still let's others inherit from
     * it.
     */
    protected AbstractFormatter() {
    }

    /**
     * Modifies the strings in a column of the string matrix to be aligned
     * (left,centered,right,decimal).
     */
    protected void align(String[][] strings) {
        int rows = strings.length;
        int columns = 0;
        if (rows > 0)
            columns = strings[0].length;

        int[] maxColWidth = new int[columns];
        int[] maxColLead = null;
        boolean isDecimal = alignment.equals(DECIMAL);
        if (isDecimal)
            maxColLead = new int[columns];
        // int[] maxMatebarail = new int[columns];

        // for each column, determine alignment parameters
        for (int column = 0; column < columns; column++) {
            int maxWidth = minColumnWidth;
            int maxLead = Integer.MIN_VALUE;
            // int maxTrail = Integer.MIN_VALUE;
            for (String[] string : strings) {
                String s = string[column];
                maxWidth = Math.max(maxWidth, s.length());
                if (isDecimal)
                    maxLead = Math.max(maxLead, lead(s));
                // maxTrail = Math.max(maxTrail, trail(s));
            }
            maxColWidth[column] = maxWidth;
            if (isDecimal)
                maxColLead[column] = maxLead;
            // maxMatebarail[column] = maxTrail;
        }

        // format each row according to alignment parameters
        // StringBuffer total = new StringBuffer();
        for (String[] string : strings) {
            alignRow(string, maxColWidth, maxColLead);
        }

    }

    /**
     * Converts a row into a string.
     */
    protected int alignmentCode(String alignment) {
        // {-1,0,1,2} = {left,centered,right,decimal point}
        return switch (alignment) {
            case LEFT -> -1;
            case CENTER -> 0;
            case RIGHT -> 1;
            case DECIMAL -> 2;
            default -> throw new IllegalArgumentException("unknown alignment: " + alignment);
        };
    }

    /**
     * Modifies the strings the string matrix to be aligned
     * (left,centered,right,decimal).
     */
    protected void alignRow(String[] row, int[] maxColWidth, int[] maxColLead) {
        StringBuilder s = new StringBuilder();

        int columns = row.length;
        for (int column = 0; column < columns; column++) {
            s.setLength(0);
            String c = row[column];
            switch (alignment) {
                case RIGHT -> {
                    s.append(blanks(maxColWidth[column] - s.length()));
                    s.append(c);
                }
                case DECIMAL -> {
                    s.append(blanks(maxColLead[column] - lead(c)));
                    s.append(c);
                    s.append(blanks(maxColWidth[column] - s.length()));
                }
                case CENTER -> {
                    s.append(blanks((maxColWidth[column] - c.length()) / 2));
                    s.append(c);
                    s.append(blanks(maxColWidth[column] - s.length()));
                }
                case LEFT -> {
                    s.append(c);
                    s.append(blanks(maxColWidth[column] - s.length()));
                }
                default -> throw new InternalError();
            }

            row[column] = s.toString();
        }
    }

    /**
     * Returns a String with <tt>length</tt> blanks.
     */
    protected String blanks(int length) {
        if (length < 0)
            length = 0;
        if (length < blanksCache.length)
            return blanksCache[length];

        return " ".repeat(length);
    }

    /**
     * Converts a given cell to a String; no alignment considered.
     */
    protected abstract String form(AbstractMatrix1D matrix, int index, Former formatter);

    /**
     * Returns a string representations of all cells; no alignment considered.
     */
    protected abstract String[][] format(AbstractMatrix2D matrix);

    /**
     * Returns a string representations of all cells; no alignment considered.
     */
    protected String[] formatRow(AbstractMatrix1D vector) {
        Former formatter = null;
        formatter = factory.create(format);
        int s = (int) vector.size();
        String[] strings = new String[s];
        for (int i = 0; i < s; i++) {
            strings[i] = form(vector, i, formatter);
        }
        return strings;
    }

    /**
     * Returns the number of characters or the number of characters before the
     * decimal point.
     */
    protected int lead(String s) {
        return s.length();
    }

    /**
     * Returns a String with the given character repeated <tt>length</tt> times.
     */
    protected String repeat(char character, int length) {
        if (character == ' ')
            return blanks(length);
        if (length < 0)
            length = 0;
        return String.valueOf(character).repeat(length);
    }

    /**
     * Sets the column alignment (left,center,right,decimal).
     *
     * @param alignment the new alignment to be used; must be one of
     *                  <tt>{LEFT,CENTER,RIGHT,DECIMAL}</tt>.
     */
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    /**
     * Sets the string separating any two columns from another.
     *
     * @param columnSeparator the new columnSeparator to be used.
     */
    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    /**
     * Sets the way a <i>single</i> cell value is to be formatted.
     *
     * @param format the new format to be used.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the minimum number of characters a column may have.
     *
     * @param minColumnWidth the new minColumnWidth to be used.
     */
    public void setMinColumnWidth(int minColumnWidth) {
        if (minColumnWidth < 0)
            throw new IllegalArgumentException();
        this.minColumnWidth = minColumnWidth;
    }

    /**
     * Specifies whether a string representation of a matrix is to be preceded
     * with a summary of its shape.
     *
     * @param printShape <tt>true</tt> shape summary is printed, otherwise not printed.
     */
    public void setPrintShape(boolean printShape) {
        this.printShape = printShape;
    }

    /**
     * Sets the string separating any two rows from another.
     *
     * @param rowSeparator the new rowSeparator to be used.
     */
    public void setRowSeparator(String rowSeparator) {
        this.rowSeparator = rowSeparator;
    }

    /**
     * Sets the string separating any two slices from another.
     *
     * @param sliceSeparator the new sliceSeparator to be used.
     */
    public void setSliceSeparator(String sliceSeparator) {
        this.sliceSeparator = sliceSeparator;
    }

    /**
     * Cache for faster string processing.
     */
    protected static void setupBlanksCache() {
        // Pre-fabricate 40 static strings with 0,1,2,..,39 blanks, for usage
        // within method blanks(length).
        // Now, we don't need to construct and fill them on demand, and garbage
        // collect them again.
        // All 40 strings share the identical char[] array, only with different
        // offset and length --> somewhat smaller static memory footprint
        int size = 40;
        blanksCache = new String[size];
        StringBuffer buf = new StringBuffer(size);
        for (int i = size; --i >= 0; )
            buf.append(' ');
        String str = buf.toString();
        for (int i = size; --i >= 0; ) {
            blanksCache[i] = str.substring(0, i);
            // System.out.println(i+"-"+blanksCache[i]+"-");
        }
    }

    /**
     * Returns a short string representation describing the shape of the matrix.
     */
    public static String shape(AbstractMatrix1D matrix) {
        // return "Matrix1D of size="+matrix.size();
        // return matrix.size()+" element matrix";
        // return "matrix("+matrix.size()+")";
        return matrix.size() + " matrix";
    }

    /**
     * Returns a short string representation describing the shape of the matrix.
     */
    public static String shape(AbstractMatrix2D matrix) {
        return matrix.rows() + " x " + matrix.columns() + " matrix";
    }

    /**
     * Returns a short string representation describing the shape of the matrix.
     */
    public static String shape(AbstractMatrix3D matrix) {
        return matrix.slices() + " x " + matrix.rows() + " x " + matrix.columns() + " matrix";
    }

    /**
     * Returns a single string representation of the given string matrix.
     *
     * @param strings the matrix to be converted to a single string.
     */
    protected String toString(String[][] strings) {
        int rows = strings.length;
        int columns = strings.length <= 0 ? 0 : strings[0].length;

        StringBuilder total = new StringBuilder();
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            s.setLength(0);
            for (int column = 0; column < columns; column++) {
                s.append(strings[row][column]);
                if (column < columns - 1)
                    s.append(columnSeparator);
            }
            total.append(s);
            if (row < rows - 1)
                total.append(rowSeparator);
        }

        return total.toString();
    }

    /**
     * Returns a string representation of the given matrix.
     *
     * @param matrix the matrix to convert.
     */
    protected String toString(AbstractMatrix2D matrix) {
        String[][] strings = this.format(matrix);
        align(strings);
        StringBuilder total = new StringBuilder(toString(strings));
        if (printShape)
            total.insert(0, shape(matrix) + "\n");
        return total.toString();
    }

    @Override
    public AbstractFormatter clone() {
        try {
            return (AbstractFormatter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
