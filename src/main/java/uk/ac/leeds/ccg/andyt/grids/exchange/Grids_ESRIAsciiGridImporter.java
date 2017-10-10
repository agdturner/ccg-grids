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
package uk.ac.leeds.ccg.andyt.grids.exchange;
import java.io.File;
import java.io.StreamTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 * Class for importing ESRI Asciigrid.
 */
public class Grids_ESRIAsciiGridImporter {
    
    
    public Grids_Processor _Grid2DSquareCellProcessor;
    
    /**
     * For storing ESRIAsciigrid File
     */
    private File file;
    
    /**
     * For storing ESRIAsciigrid BufferedReader
     */
    private BufferedReader bufferedReader;
    
    /**
     * For storing ESRIAsciigrid StreamTokenizer
     */
    private StreamTokenizer streamTokenizer;
    
    /**
     * @param file
     * @param _Grid2DSquareCellProcessor
     */
    public Grids_ESRIAsciiGridImporter(
            File file,
            Grids_Processor _Grid2DSquareCellProcessor ) {
        this._Grid2DSquareCellProcessor = _Grid2DSquareCellProcessor;
        this.file = file;
        try {
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream( this.file ) ) );
            this.streamTokenizer = new StreamTokenizer( this.bufferedReader );
        } catch ( IOException ioe0 ) {
            System.out.println( ioe0.getMessage() );
            ioe0.printStackTrace();
        }
    }
    
    /** Creates a new instance of ESRIAsciiGridImporter
     * @param file
     * @param _Grids_Environment */
    public Grids_ESRIAsciiGridImporter(
            File file,
            Grids_Environment _Grids_Environment) {
        this._Grid2DSquareCellProcessor = new Grids_Processor( _Grids_Environment );
        this.file = file;
        try {
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream( this.file ) ) );
            this.streamTokenizer = new StreamTokenizer( this.bufferedReader );
        } catch ( IOException ioe0 ) {
            System.out.println( ioe0.getMessage() );
            ioe0.printStackTrace();
        }
    }
    
    /**
     * Reads the header of the file and returns a double[] where;
     * [0] = (double) ncols;
     * [1] = (double) nrows;
     * [2] = xllcorner;
     * [3] = yllcorner;
     * [4] = cellsize;
     * [5] = noDataValue if it exists or
     *       Double.NEGATIVE_INFINITY otherwise
     * @return 
     */
    public double[] readHeaderDouble() {
        Object[] headerObject = readHeaderObject();
        double[] result = new double[ 6 ];
        result[ 0 ] = ( ( Long ) headerObject[ 0 ] ).doubleValue();
        result[ 1 ] = ( ( Long ) headerObject[ 1 ] ).doubleValue();
        result[ 2 ] = ( ( BigDecimal ) headerObject[ 2 ] ).doubleValue();
        result[ 3 ] = ( ( BigDecimal ) headerObject[ 3 ] ).doubleValue();
        result[ 4 ] = ( ( BigDecimal ) headerObject[ 4 ] ).doubleValue();
        result[ 5 ] = ( ( Double ) headerObject[ 5 ] ).doubleValue();
        return result;
    }
    
    /**
     * Reads the header of the file and returns a Object[] where;
     * [0] = Long( ncols );
     * [1] = (double) nrows;
     * [2] = xllcorner;
     * [3] = yllcorner;
     * [4] = cellsize;
     * [5] = noDataValue if it exists or
     *       Double.NEGATIVE_INFINITY otherwise
     * @return 
     */
    public Object[] readHeaderObject() {
        Object[] result = new Object[ 6 ];
        try {
            setSyntax0();
            this.streamTokenizer.wordChars( '0', '9' );
            this.streamTokenizer.wordChars( '.', '.' );
            this.streamTokenizer.wordChars( '-', '-' );
            this.streamTokenizer.eolIsSignificant( false );
            // ncols
            this.streamTokenizer.nextToken();
            this.streamTokenizer.nextToken();
            result[ 0 ] = new Long( this.streamTokenizer.sval );
            // nrows
            this.streamTokenizer.nextToken();
            this.streamTokenizer.nextToken();
            result[ 1 ] = new Long( this.streamTokenizer.sval );
            // xllcorner
            this.streamTokenizer.nextToken();
            boolean b1 = true;
            if ( this.streamTokenizer.sval.equalsIgnoreCase( "xllcenter" ) || this.streamTokenizer.sval.equalsIgnoreCase( "xllcentre" ) ) {
                b1 = false;
            }
            this.streamTokenizer.nextToken();
            result[ 2 ] = new BigDecimal( this.streamTokenizer.sval );
            
            // yllcorner
            this.streamTokenizer.nextToken();
            boolean b2 = true;
            if ( this.streamTokenizer.sval.equalsIgnoreCase( "yllcenter" ) || this.streamTokenizer.sval.equalsIgnoreCase( "yllcentre" ) ) {
                b2 = false;
            }
            this.streamTokenizer.nextToken();
            result[ 3 ] = new BigDecimal( this.streamTokenizer.sval );
            // cellsize
            this.streamTokenizer.nextToken();
            this.streamTokenizer.nextToken();
            BigDecimal cellsize = new BigDecimal( this.streamTokenizer.sval );
            result[ 4 ] = cellsize;
            // adjust xllcorner
            if ( !b1 || !b2 ) {
                BigDecimal halfCellsize = cellsize.divide( new BigDecimal( "2" ), cellsize.scale() + 4, BigDecimal.ROUND_HALF_EVEN );
                if ( !b1 ) {
                    result[ 2 ] = ( ( BigDecimal ) result[ 2 ] ).subtract( halfCellsize );
                }
                // adjust yllcorner
                if ( !b2 ) {
                    result[ 3 ] = ( ( BigDecimal ) result[ 3 ] ).subtract( halfCellsize );
                }
            }
            // noDataValue
            this.bufferedReader.mark( 100 );
            this.streamTokenizer.wordChars( '_', '_' );
            this.streamTokenizer.nextToken();
            result[ 5 ] = Double.MIN_VALUE;
            if( this.streamTokenizer.ttype == StreamTokenizer.TT_NUMBER ) {
                this.bufferedReader.reset();
            } else {
                if ( streamTokenizer.sval.startsWith("n") || streamTokenizer.sval.startsWith("N") ) {
                    setSyntax0();
                    this.streamTokenizer.wordChars( '0', '9' );
                    this.streamTokenizer.wordChars( '-', '-' );
                    this.streamTokenizer.wordChars( '+', '+' );
                    this.streamTokenizer.wordChars( '.', '.' );
                    
//                this.streamTokenizer.ordinaryChar( 'e' );
//                this.streamTokenizer.ordinaryChar( 'd' );
//                this.streamTokenizer.ordinaryChar( 'E' );
//                this.streamTokenizer.ordinaryChar( 'D' );
//                this.streamTokenizer.parseNumbers();
                    //result[ 5 ] = new Double( readHeaderDoubleValue() );
                    result[ 5 ] = new Double( readDouble() );
                } else {
                    this.bufferedReader.reset();
                }
            }
            setSyntax0();
            this.streamTokenizer.wordChars( '0', '9' );
            this.streamTokenizer.wordChars( '-', '-' );
            this.streamTokenizer.wordChars( '+', '+' );
            this.streamTokenizer.wordChars( '.', '.' );
//            this.streamTokenizer.ordinaryChar( 'e' );
//            this.streamTokenizer.ordinaryChar( 'd' );
//            this.streamTokenizer.ordinaryChar( 'E' );
//            this.streamTokenizer.ordinaryChar( 'D' );
//            this.streamTokenizer.parseNumbers();
        } catch( IOException e ) {
            System.out.println( e );
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * sets this.sstreamTokenizer syntax as:
     * this.streamTokenizer.resetSyntax();
     * this.streamTokenizer.whitespaceChars( '\u0000','\u0020');
     *  this.streamTokenizer.wordChars( 'A', 'Z' );
     * this.streamTokenizer.wordChars( 'a', 'z' );
     * this.streamTokenizer.wordChars( '\u00A0', '\u00FF' );
     * this.streamTokenizer.eolIsSignificant( false );
     * this.streamTokenizer.parseNumbers();
     */
    private void setSyntax0() {
        this.streamTokenizer.resetSyntax();
        this.streamTokenizer.whitespaceChars( '\u0000','\u0020');
        this.streamTokenizer.wordChars( 'A', 'Z' );
        this.streamTokenizer.wordChars( 'a', 'z' );
        this.streamTokenizer.wordChars( '\u00A0', '\u00FF' );
        this.streamTokenizer.eolIsSignificant( false );
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
     * @return 
     */
    public double readDouble() {
        double result = Double.NEGATIVE_INFINITY;
        try {
            this.streamTokenizer.nextToken();
            if ( this.streamTokenizer.ttype != StreamTokenizer.TT_EOF ) {
                String token = this.streamTokenizer.sval;
                int exponent = 0;
                boolean positive;
                if ( token.startsWith( "-" ) ) {
                    positive = false;
                    token = token.substring( 1 );
                } else {
                    positive = true;
                }
                boolean positiveExponent = true;
                if ( token.contains( "E" ) ) {
                    String[] tokenSplit = token.split( "E" );
                    if ( tokenSplit.length != 2 ) {
                        throw new NumberFormatException();
                    } else {
                        result = Double.valueOf( tokenSplit[ 0 ] );
                        if ( tokenSplit[ 1 ].startsWith( "-" ) ) {
                            positiveExponent = false;
                            tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                        } else {
                            if ( tokenSplit[ 1 ].startsWith( "+" ) ) {
                                positiveExponent = true;
                                tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                            } else {
                                throw new NumberFormatException();
                            }
                        }
                        exponent = Integer.valueOf( tokenSplit[ 1 ] );
                    }
                } else {
                    if ( token.contains( "e" ) ) {
                        String[] tokenSplit = token.split( "e" );
                        if ( tokenSplit.length != 2 ) {
                            throw new NumberFormatException();
                        } else {
                            result = Double.valueOf( tokenSplit[ 0 ] );
                            if ( tokenSplit[ 1 ].startsWith( "-" ) ) {
                                positiveExponent = false;
                                tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                            } else {
                                if ( tokenSplit[ 1 ].startsWith( "+" ) ) {
                                    positiveExponent = true;
                                    tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                                } else {
                                    throw new NumberFormatException();
                                }
                            }
                            exponent = Integer.valueOf( tokenSplit[ 1 ] );
                        }
                    } else {
                        if ( token.contains( "D" ) ) {
                            String[] tokenSplit = token.split( "D" );
                            if ( tokenSplit.length != 2 ) {
                                throw new NumberFormatException();
                            } else {
                                result = Double.valueOf( tokenSplit[ 0 ] );
                                if ( tokenSplit[ 1 ].startsWith( "-" ) ) {
                                    positiveExponent = false;
                                    tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                                } else {
                                    if ( tokenSplit[ 1 ].startsWith( "+" ) ) {
                                        positiveExponent = true;
                                        tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                                    } else {
                                        throw new NumberFormatException();
                                    }
                                }
                                exponent = Integer.valueOf( tokenSplit[ 1 ] );
                            }
                        } else {
                            if ( token.contains( "d" ) ) {
                                String[] tokenSplit = token.split( "d" );
                                if ( tokenSplit.length != 2 ) {
                                    throw new NumberFormatException();
                                } else {
                                    result = Double.valueOf( tokenSplit[ 0 ] );
                                    if ( tokenSplit[ 1 ].startsWith( "-" ) ) {
                                        positiveExponent = false;
                                        tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                                    } else {
                                        if ( tokenSplit[ 1 ].startsWith( "+" ) ) {
                                            positiveExponent = true;
                                            tokenSplit[ 1 ] = tokenSplit[ 1 ].substring( 1 );
                                        } else {
                                            throw new NumberFormatException();
                                        }
                                    }
                                    exponent = Integer.valueOf( tokenSplit[ 1 ] );
                                }
                            } else {
                                result = Double.valueOf( token );
                            }
                        }
                    }
                }
                if ( !positiveExponent ) {
                    exponent *= -1;
                }
                result *= Math.pow( 10.0, exponent );
                if ( ! positive ) {
                    result *= -1.0;
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
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
     * @return 
     */
    public int readInt() {
        int result = Integer.MIN_VALUE;
        try {
            this.streamTokenizer.nextToken();
            this.bufferedReader.mark( 100 );
            double number = this.streamTokenizer.nval;
            this.streamTokenizer.nextToken();
            if ( this.streamTokenizer.ttype != StreamTokenizer.TT_EOF ) {
                if ( this.streamTokenizer.ttype != StreamTokenizer.TT_NUMBER ) {
                    // Either encountered an exponent term or something else
                    // Treat as an exponent: grab the second part and compute
                    this.streamTokenizer.nextToken();
                    result = ( int ) ( number * Math.pow( 10.0, this.streamTokenizer.nval ) );
                } else {
                    result = ( int ) number;
                    this.bufferedReader.reset();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * For closing this.bufferedReader.
     */
    public void close() {
        try {
            this.bufferedReader.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the prefix (before "." part of filename of this.file.
     * @return 
     */
    public String getFilenamePrefix() {
        String filename = this.file.getName();
        return filename.substring( 0, filename.length() - 4 );
    }
    
}
