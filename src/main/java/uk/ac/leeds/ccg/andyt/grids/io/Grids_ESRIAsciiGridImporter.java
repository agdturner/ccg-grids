/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.io;

import java.io.File;
import java.io.StreamTokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;

/**
 * Class for importing ESRI Asciigrid.
 */
public class Grids_ESRIAsciiGridImporter extends Grids_Object {

    /**
     * The ESRIAsciigrid File
     */
    private File file;

    /**
     * The ESRIAsciigrid BufferedReader.
     */
    private BufferedReader br;

    /**
     * The ESRIAsciigrid StreamTokenizer.
     */
    private StreamTokenizer st;

    /**
     * For storing the header.
     */
    private Grids_ESRIAsciiGridHeader header;

    /**
     * @param f The File.
     * @param e The Grids_Environment.
     * @throws java.io.FileNotFoundException If f does not exist.
     */
    public Grids_ESRIAsciiGridImporter(Grids_Environment e, File f)
            throws FileNotFoundException {
        super(e);
        init(f);
    }

    private void init(File f) throws FileNotFoundException {
        file = f;
        br = env.env.io.getBufferedReader(file);
        st = new StreamTokenizer(br);
    }

    public class Grids_ESRIAsciiGridHeader {

        /**
         * For storing the number of columns.
         */
        public long ncols;
        
        /**
         * For storing the number of rows.
         */
        public long nrows;

        /**
         * For storing the lower left corner x.
         */
        public BigDecimal xll;
        
        /**
         * For storing the lower left corner y.
         */
        public BigDecimal yll;
        
        /**
         * For storing the cellsize.
         */
        public BigDecimal cellsize;
        
        /**
         * For storing the NODATA value.
         */
        public BigDecimal ndv;

        public Grids_ESRIAsciiGridHeader() {
        }
    }

    /**
     * If {@link #header} is null, this reads the header of the file and returns 
     * a {@link Grids_ESRIAsciiGridHeader}. Otherwise this returns {@link #header}.
     * @return a {@link Grids_ESRIAsciiGridHeader}.
     */
    public Grids_ESRIAsciiGridHeader getHeader() {
        if (header == null) {
            header = new Grids_ESRIAsciiGridHeader();
            try {
                setSyntax0();
                this.st.wordChars('0', '9');
                this.st.wordChars('.', '.');
                this.st.wordChars('-', '-');
                this.st.eolIsSignificant(false);
                // ncols
                this.st.nextToken();
                this.st.nextToken();
                header.ncols = new Long(this.st.sval);
                // nrows
                this.st.nextToken();
                this.st.nextToken();
                header.nrows = new Long(this.st.sval);
                // xll
                this.st.nextToken();
                boolean b1 = true;
                if (this.st.sval.equalsIgnoreCase("xllcenter") 
                        || this.st.sval.equalsIgnoreCase("xllcentre")) {
                    b1 = false;
                }
                this.st.nextToken();
                header.xll = new BigDecimal(this.st.sval);

                // yll
                this.st.nextToken();
                boolean b2 = true;
                if (this.st.sval.equalsIgnoreCase("yllcenter") 
                        || this.st.sval.equalsIgnoreCase("yllcentre")) {
                    b2 = false;
                }
                this.st.nextToken();
                header.yll = new BigDecimal(this.st.sval);
                // cellsize
                this.st.nextToken();
                this.st.nextToken();
                BigDecimal cellsize = new BigDecimal(this.st.sval);
                header.cellsize = cellsize;
                // adjust xll
                if (!b1 || !b2) {
                    BigDecimal halfCellsize = cellsize.divide(
                            new BigDecimal("2"),
                            cellsize.scale() + 4, RoundingMode.HALF_EVEN);
                    if (!b1) {
                        header.xll = ((BigDecimal) header.xll).subtract(halfCellsize);
                    }
                    // adjust yll
                    if (!b2) {
                        header.yll = ((BigDecimal) header.yll).subtract(halfCellsize);
                    }
                }
                // noDataValue
                this.br.mark(100);
                this.st.wordChars('_', '_');
                this.st.nextToken();
                header.ndv = BigDecimal.valueOf(-Double.MAX_VALUE);
                if (this.st.ttype == StreamTokenizer.TT_NUMBER) {
                    this.br.reset();
                } else {
                    if (st.sval.startsWith("n") || st.sval.startsWith("N")) {
                        setSyntax0();
                        this.st.wordChars('0', '9');
                        this.st.wordChars('-', '-');
                        this.st.wordChars('+', '+');
                        this.st.wordChars('.', '.');
//                this.st.ordinaryChar( 'e' );
//                this.st.ordinaryChar( 'd' );
//                this.st.ordinaryChar( 'E' );
//                this.st.ordinaryChar( 'D' );
//                this.st.parseNumbers();
                        //header[ 5 ] = new Double( readHeaderDoubleValue() );
                        header.ndv = BigDecimal.valueOf(readDouble());
                    } else {
                        this.br.reset();
                    }
                }
                setSyntax0();
                this.st.wordChars('0', '9');
                this.st.wordChars('-', '-');
                this.st.wordChars('+', '+');
                this.st.wordChars('.', '.');
//            this.st.ordinaryChar( 'e' );
//            this.st.ordinaryChar( 'd' );
//            this.st.ordinaryChar( 'E' );
//            this.st.ordinaryChar( 'D' );
//            this.st.parseNumbers();
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace(System.err);
            }
        }
        return header;
    }

    /**
     * sets this.sstreamTokenizer syntax as: this.streamTokenizer.resetSyntax();
     * this.streamTokenizer.whitespaceChars( '\u0000','\u0020');
     * this.streamTokenizer.wordChars( 'A', 'Z' );
     * this.streamTokenizer.wordChars( 'a', 'z' );
     * this.streamTokenizer.wordChars( '\u00A0', '\u00FF' );
     * this.streamTokenizer.eolIsSignificant( false );
     * this.streamTokenizer.parseNumbers();
     */
    private void setSyntax0() {
        this.st.resetSyntax();
        this.st.whitespaceChars('\u0000', '\u0020');
        this.st.wordChars('A', 'Z');
        this.st.wordChars('a', 'z');
        this.st.wordChars('\u00A0', '\u00FF');
        this.st.eolIsSignificant(false);
    }

    /**
     * Returns the next value as a double or Double.NEGATIVE_INFINITY
     *
     * @return
     */
    public double readDouble() {
        double result = Double.NEGATIVE_INFINITY;
        try {
            this.st.nextToken();
            if (this.st.ttype != StreamTokenizer.TT_EOF) {
                String token = this.st.sval;
                int exponent = 0;
                boolean positive;
                if (token.startsWith("-")) {
                    positive = false;
                    token = token.substring(1);
                } else {
                    positive = true;
                }
                boolean positiveExponent = true;
                if (token.contains("E")) {
                    String[] tokenSplit = token.split("E");
                    if (tokenSplit.length != 2) {
                        throw new NumberFormatException();
                    } else {
                        result = Double.valueOf(tokenSplit[0]);
                        if (tokenSplit[1].startsWith("-")) {
                            positiveExponent = false;
                            tokenSplit[1] = tokenSplit[1].substring(1);
                        } else {
                            if (tokenSplit[1].startsWith("+")) {
                                positiveExponent = true;
                                tokenSplit[1] = tokenSplit[1].substring(1);
                            } else {
                                positiveExponent = true;
                                //throw new NumberFormatException();
                            }
                        }
                        exponent = Integer.valueOf(tokenSplit[1]);
                    }
                } else {
                    if (token.contains("e")) {
                        String[] tokenSplit = token.split("e");
                        if (tokenSplit.length != 2) {
                            throw new NumberFormatException();
                        } else {
                            result = Double.valueOf(tokenSplit[0]);
                            if (tokenSplit[1].startsWith("-")) {
                                positiveExponent = false;
                                tokenSplit[1] = tokenSplit[1].substring(1);
                            } else {
                                if (tokenSplit[1].startsWith("+")) {
                                    positiveExponent = true;
                                    tokenSplit[1] = tokenSplit[1].substring(1);
                                } else {
                                    positiveExponent = true;
                                    //throw new NumberFormatException();
                                }
                            }
                            exponent = Integer.valueOf(tokenSplit[1]);
                        }
                    } else {
                        if (token.contains("D")) {
                            String[] tokenSplit = token.split("D");
                            if (tokenSplit.length != 2) {
                                throw new NumberFormatException();
                            } else {
                                result = Double.valueOf(tokenSplit[0]);
                                if (tokenSplit[1].startsWith("-")) {
                                    positiveExponent = false;
                                    tokenSplit[1] = tokenSplit[1].substring(1);
                                } else {
                                    if (tokenSplit[1].startsWith("+")) {
                                        positiveExponent = true;
                                        tokenSplit[1] = tokenSplit[1].substring(1);
                                    } else {
                                        positiveExponent = true;
                                        //throw new NumberFormatException();
                                    }
                                }
                                exponent = Integer.valueOf(tokenSplit[1]);
                            }
                        } else {
                            if (token.contains("d")) {
                                String[] tokenSplit = token.split("d");
                                if (tokenSplit.length != 2) {
                                    throw new NumberFormatException();
                                } else {
                                    result = Double.valueOf(tokenSplit[0]);
                                    if (tokenSplit[1].startsWith("-")) {
                                        positiveExponent = false;
                                        tokenSplit[1] = tokenSplit[1].substring(1);
                                    } else {
                                        if (tokenSplit[1].startsWith("+")) {
                                            positiveExponent = true;
                                            tokenSplit[1] = tokenSplit[1].substring(1);
                                        } else {
                                            positiveExponent = true;
                                            //throw new NumberFormatException();
                                        }
                                    }
                                    exponent = Integer.valueOf(tokenSplit[1]);
                                }
                            } else {
                                result = Double.valueOf(token);
                            }
                        }
                    }
                }
                if (!positiveExponent) {
                    exponent *= -1;
                }
                result *= Math.pow(10.0, exponent);
                if (!positive) {
                    result *= -1.0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    /**
     * Returns the next value as a int or Integer.MIN_VALUE
     *
     * @return
     */
    public int readInt() {
        int result = Integer.MIN_VALUE;
        try {
            this.st.nextToken();
            this.br.mark(100);
            double number = this.st.nval;
            this.st.nextToken();
            if (this.st.ttype != StreamTokenizer.TT_EOF) {
                if (this.st.ttype != StreamTokenizer.TT_NUMBER) {
                    // Either encountered an exponent term or something else
                    // Treat as an exponent: grab the second part and compute
                    this.st.nextToken();
                    result = (int) (number * Math.pow(10.0, this.st.nval));
                } else {
                    result = (int) number;
                    this.br.reset();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    /**
     * For closing this.bufferedReader.
     */
    public void close() {
        try {
            this.br.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Returns the prefix (before "." part of filename of this.file.
     *
     * @return
     */
    public String getFilenamePrefix() {
        String filename = this.file.getName();
        return filename.substring(0, filename.length() - 4);
    }

}
