/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.grids.io;

import ch.obermuhlner.math.big.BigRational;
import java.io.StreamTokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.io.IO_Utilities;

/**
 * Class for importing ESRI Asciigrid.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ESRIAsciiGridImporter extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * The ESRIAsciigrid File
     */
    private IO_Path file;

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
    private Header header;

    /**
     * @param f The File.
     * @param e The Grids_Environment.
     * @throws java.io.FileNotFoundException If f does not exist.
     */
    public Grids_ESRIAsciiGridImporter(Grids_Environment e, IO_Path f)
            throws FileNotFoundException, IOException {
        super(e);
        init(f);
    }

    private void init(IO_Path f) throws FileNotFoundException, IOException {
        file = f;
        br = IO_Utilities.getBufferedReader(file.getPath());
        st = new StreamTokenizer(br);
    }

    /**
     * Class for the header.
     */
    public class Header {

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
        public BigRational xll;
        
        /**
         * For storing the lower left corner y.
         */
        public BigRational yll;
        
        /**
         * For storing the cellsize.
         */
        public BigRational cellsize;
        
        /**
         * For storing the NODATA value.
         */
        public BigDecimal ndv;

        /**
         * Create a new instance.
         */
        public Header() {
        }
    }

    /**
     * If {@link #header} is null, this reads the header of the file and returns 
     * a {@link Header}. Otherwise this returns {@link #header}.
     * @return a {@link Header}.
     */
    public Header getHeader() {
        if (header == null) {
            header = new Header();
            try {
                setSyntax0();
                this.st.wordChars('0', '9');
                this.st.wordChars('.', '.');
                this.st.wordChars('-', '-');
                this.st.eolIsSignificant(false);
                // ncols
                this.st.nextToken();
                this.st.nextToken();
                header.ncols = Long.valueOf(this.st.sval);
                // nrows
                this.st.nextToken();
                this.st.nextToken();
                header.nrows = Long.valueOf(this.st.sval);
                // xll
                this.st.nextToken();
                boolean b1 = true;
                if (this.st.sval.equalsIgnoreCase("xllcenter") 
                        || this.st.sval.equalsIgnoreCase("xllcentre")) {
                    b1 = false;
                }
                this.st.nextToken();
                header.xll = BigRational.valueOf(this.st.sval);

                // yll
                this.st.nextToken();
                boolean b2 = true;
                if (this.st.sval.equalsIgnoreCase("yllcenter") 
                        || this.st.sval.equalsIgnoreCase("yllcentre")) {
                    b2 = false;
                }
                this.st.nextToken();
                header.yll = BigRational.valueOf(this.st.sval);
                // cellsize
                this.st.nextToken();
                this.st.nextToken();
                BigRational cellsize = BigRational.valueOf(this.st.sval);
                header.cellsize = cellsize;
                // adjust xll
                if (!b1 || !b2) {
                    BigRational halfCellsize = cellsize.divide(2);
                    if (!b1) {
                        header.xll = header.xll.subtract(halfCellsize);
                    }
                    // adjust yll
                    if (!b2) {
                        header.yll = header.yll.subtract(halfCellsize);
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
     * @return The next value as a BigDecimal or BigDecimal.valueOf(-Double.MAX_VALUE).
     */
    public BigDecimal readBigDecimal() {
        BigDecimal r = BigDecimal.valueOf(-Double.MAX_VALUE);
        try {
            BigDecimal.valueOf(this.st.nextToken());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return r;
    }

    /**
     * @return The next value as a double or Double.NEGATIVE_INFINITY.
     */
    public double readDouble() {
        double r = Double.NEGATIVE_INFINITY;
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
                        r = Double.valueOf(tokenSplit[0]);
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
                            r = Double.valueOf(tokenSplit[0]);
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
                                r = Double.valueOf(tokenSplit[0]);
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
                                    r = Double.valueOf(tokenSplit[0]);
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
                                r = Double.valueOf(token);
                            }
                        }
                    }
                }
                if (!positiveExponent) {
                    exponent *= -1;
                }
                r *= Math.pow(10.0, exponent);
                if (!positive) {
                    r *= -1.0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return r;
    }

    /**
     * @return The next value as a int or Integer.MIN_VALUE.
     */
    public int readInt() {
        int r = Integer.MIN_VALUE;
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
                    r = (int) (number * Math.pow(10.0, this.st.nval));
                } else {
                    r = (int) number;
                    this.br.reset();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return r;
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
     * @return All but the last 4 characters of {@link #file}.
     */
    public String getFilenamePrefix() {
        String filename = this.file.getFileName().toString();
        return filename.substring(0, filename.length() - 4);
    }

}
