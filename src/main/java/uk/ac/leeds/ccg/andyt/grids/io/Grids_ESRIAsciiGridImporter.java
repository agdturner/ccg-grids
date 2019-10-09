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
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 * Class for importing ESRI Asciigrid.
 */
public class Grids_ESRIAsciiGridImporter extends Grids_Object {

    public Grids_Processor Processor;

    /**
     * For storing ESRIAsciigrid File
     */
    private File file;

    /**
     * For storing ESRIAsciigrid BufferedReader
     */
    private BufferedReader BR;

    /**
     * For storing ESRIAsciigrid StreamTokenizer
     */
    private StreamTokenizer ST;

    /**
     * @param f
     * @param processor
     */
    public Grids_ESRIAsciiGridImporter(
            File f,
            Grids_Processor processor) {
        super(processor.env);
        this.Processor = processor;
        init(f);
    }

    private void init(File f) {
        file = f;
        BR = env.env.io.getBufferedReader(file);
        ST = new StreamTokenizer(BR);
    }

    /**
     * Creates a new instance of ESRIAsciiGridImporter
     *
     * @param f
     * @param ge
     */
    public Grids_ESRIAsciiGridImporter(
            File f,
            Grids_Environment ge) {
        super(ge);
        Processor = ge.getProcessor();
        init(f);
    }

    public class Grids_ESRIAsciiGridHeader {

        public long NCols;
        public long NRows;
        public BigDecimal xllcorner;
        public BigDecimal yllcorner;
        public BigDecimal cellsize;
        public BigDecimal NoDataValue;

        public Grids_ESRIAsciiGridHeader() {
        }
    }

    /**
     * Reads the header of the file and returns a Object[] where; [0] = Long(
     * ncols ); [1] = (double) nrows; [2] = xllcorner; [3] = yllcorner; [4] =
     * cellsize; [5] = noDataValue if it exists or Double.NEGATIVE_INFINITY
     * otherwise
     *
     * @return
     */
    public Grids_ESRIAsciiGridHeader readHeaderObject() {
        Grids_ESRIAsciiGridHeader result = new Grids_ESRIAsciiGridHeader();
        try {
            setSyntax0();
            this.ST.wordChars('0', '9');
            this.ST.wordChars('.', '.');
            this.ST.wordChars('-', '-');
            this.ST.eolIsSignificant(false);
            // ncols
            this.ST.nextToken();
            this.ST.nextToken();
            result.NCols = new Long(this.ST.sval);
            // nrows
            this.ST.nextToken();
            this.ST.nextToken();
            result.NRows = new Long(this.ST.sval);
            // xllcorner
            this.ST.nextToken();
            boolean b1 = true;
            if (this.ST.sval.equalsIgnoreCase("xllcenter") || this.ST.sval.equalsIgnoreCase("xllcentre")) {
                b1 = false;
            }
            this.ST.nextToken();
            result.xllcorner = new BigDecimal(this.ST.sval);

            // yllcorner
            this.ST.nextToken();
            boolean b2 = true;
            if (this.ST.sval.equalsIgnoreCase("yllcenter") || this.ST.sval.equalsIgnoreCase("yllcentre")) {
                b2 = false;
            }
            this.ST.nextToken();
            result.yllcorner = new BigDecimal(this.ST.sval);
            // cellsize
            this.ST.nextToken();
            this.ST.nextToken();
            BigDecimal cellsize = new BigDecimal(this.ST.sval);
            result.cellsize = cellsize;
            // adjust xllcorner
            if (!b1 || !b2) {
                BigDecimal halfCellsize = cellsize.divide(new BigDecimal("2"), cellsize.scale() + 4, RoundingMode.HALF_EVEN);
                if (!b1) {
                    result.xllcorner = ((BigDecimal) result.xllcorner).subtract(halfCellsize);
                }
                // adjust yllcorner
                if (!b2) {
                    result.yllcorner = ((BigDecimal) result.yllcorner).subtract(halfCellsize);
                }
            }
            // noDataValue
            this.BR.mark(100);
            this.ST.wordChars('_', '_');
            this.ST.nextToken();
            result.NoDataValue = BigDecimal.valueOf(-Double.MAX_VALUE);
            if (this.ST.ttype == StreamTokenizer.TT_NUMBER) {
                this.BR.reset();
            } else {
                if (ST.sval.startsWith("n") || ST.sval.startsWith("N")) {
                    setSyntax0();
                    this.ST.wordChars('0', '9');
                    this.ST.wordChars('-', '-');
                    this.ST.wordChars('+', '+');
                    this.ST.wordChars('.', '.');

//                this.streamTokenizer.ordinaryChar( 'e' );
//                this.streamTokenizer.ordinaryChar( 'd' );
//                this.streamTokenizer.ordinaryChar( 'E' );
//                this.streamTokenizer.ordinaryChar( 'D' );
//                this.streamTokenizer.parseNumbers();
                    //result[ 5 ] = new Double( readHeaderDoubleValue() );
                    result.NoDataValue = BigDecimal.valueOf(readDouble());
                } else {
                    this.BR.reset();
                }
            }
            setSyntax0();
            this.ST.wordChars('0', '9');
            this.ST.wordChars('-', '-');
            this.ST.wordChars('+', '+');
            this.ST.wordChars('.', '.');
//            this.streamTokenizer.ordinaryChar( 'e' );
//            this.streamTokenizer.ordinaryChar( 'd' );
//            this.streamTokenizer.ordinaryChar( 'E' );
//            this.streamTokenizer.ordinaryChar( 'D' );
//            this.streamTokenizer.parseNumbers();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace(System.err);
        }
        return result;
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
        this.ST.resetSyntax();
        this.ST.whitespaceChars('\u0000', '\u0020');
        this.ST.wordChars('A', 'Z');
        this.ST.wordChars('a', 'z');
        this.ST.wordChars('\u00A0', '\u00FF');
        this.ST.eolIsSignificant(false);
    }

//    /**
//     * Returns the next value as a double or Double.NEGATIVE_INFINITY
//     */
//    public double readDouble() {
//        double result = Double.NEGATIVE_INFINITY;
//        try {
//            this.streamTokenizer.nextToken();
//            this.bufferedReader.mark( 100 );
//            result = ( double ) this.streamTokenizer.nval;
//            this.streamTokenizer.nextToken();
//            if ( this.streamTokenizer.ttype != StreamTokenizer.TT_EOF ) {
//                //if ( this.streamTokenizer.ttype != StreamTokenizer.TT_EOL ) {
//                //if ( this.streamTokenizer.ttype == StreamTokenizer.TT_WORD ) {
//                if ( this.streamTokenizer.ttype == 'e' || this.streamTokenizer.ttype == 'd' || this.streamTokenizer.ttype == 'E' || this.streamTokenizer.ttype == 'D' ) {
//                //if ( this.streamTokenizer.ttype != StreamTokenizer.TT_NUMBER ) {
//                        // Either encountered an exponent term or something else
//                        // Treat as an exponent: grab the second part and compute
//                        this.streamTokenizer.nextToken();
//                        if ( this.streamTokenizer.ttype != StreamTokenizer.TT_NUMBER ) {
//                        //if ( this.streamTokenizer.ttype == '+' ) {
//                            // this is probably a + so ignore and get the next token
//                            this.streamTokenizer.nextToken();
//                        }
//                        result *= Math.pow( 10.0, this.streamTokenizer.nval );
//                    } else {
//                        this.bufferedReader.reset();
//                    }
//                //}
//            }
//        } catch ( IOException e ) {
//            e.printStackTrace();
//        }
//        return result;
//    }
    /**
     * Returns the next value as a double or Double.NEGATIVE_INFINITY
     *
     * @return
     */
    public double readDouble() {
        double result = Double.NEGATIVE_INFINITY;
        try {
            this.ST.nextToken();
            if (this.ST.ttype != StreamTokenizer.TT_EOF) {
                String token = this.ST.sval;
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

//    /**
//     * Returns the next value as a double or Double.NEGATIVE_INFINITY
//     * TODO: Not sure this is completely correct, but it seems to work....
//     */
//    public double readHeaderDoubleValue() {
//        double result = Double.NEGATIVE_INFINITY;
//        try {
//            this.streamTokenizer.nextToken();
//            this.bufferedReader.mark( 100 );
//            result = ( double ) this.streamTokenizer.nval;
//            this.streamTokenizer.nextToken();
//            if ( this.streamTokenizer.ttype != StreamTokenizer.TT_EOF ) {
//                if ( this.streamTokenizer.ttype != StreamTokenizer.TT_NUMBER ) {
//                    // Either encountered an exponent term or something else
//                    // Treat as an exponent: grab the second part and compute
//                    this.streamTokenizer.nextToken();
//                    result *= Math.pow( 10.0, this.streamTokenizer.nval );
//                } else {
//                    this.bufferedReader.reset();
//                }
//            }
//        } catch ( IOException e ) {
//            e.printStackTrace();
//        }
//        return result;
//    }
    /**
     * Returns the next value as a int or Integer.MIN_VALUE
     *
     * @return
     */
    public int readInt() {
        int result = Integer.MIN_VALUE;
        try {
            this.ST.nextToken();
            this.BR.mark(100);
            double number = this.ST.nval;
            this.ST.nextToken();
            if (this.ST.ttype != StreamTokenizer.TT_EOF) {
                if (this.ST.ttype != StreamTokenizer.TT_NUMBER) {
                    // Either encountered an exponent term or something else
                    // Treat as an exponent: grab the second part and compute
                    this.ST.nextToken();
                    result = (int) (number * Math.pow(10.0, this.ST.nval));
                } else {
                    result = (int) number;
                    this.BR.reset();
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
            this.BR.close();
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
