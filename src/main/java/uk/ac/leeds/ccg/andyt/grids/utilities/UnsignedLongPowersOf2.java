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
package uk.ac.leeds.ccg.andyt.grids.utilities;

/**
 * TODO:
 * docs
 */
public class UnsignedLongPowersOf2 {
    
    /**
     * For storing the powers Of 2
     */
    public long[] powersOf2;
    
    /**
     * For testing...
     * @param args
     */
    public static void main( String args[] ) {
        long time = System.currentTimeMillis();
        UnsignedLongPowersOf2 l = new UnsignedLongPowersOf2();
        for ( int i = 0; i < l.powersOf2.length; i ++ ) {
            System.out.println( l.powersOf2[ i ] );
        }
        System.out.println( "Processing complete in " + Utilities._ReportTime( System.currentTimeMillis() - time ) );
    }
    
    /**
     * Creates a new instance of PowersOf2
     * Defaults array to length 63.
     */
    public UnsignedLongPowersOf2() {
        this( true );
    }
    
    /**
     * Creates a new instance of PowersOf2
     * Defaults array to length 63.
     * @param ascending
     */
    public UnsignedLongPowersOf2( boolean ascending ) {
        if ( ascending ) {
            this.powersOf2 = new long[ 64 ];
            for ( int value = 0; value < 64; value ++ ) {
                if ( value == 63 ) {
                    this.powersOf2[ value ] = Long.MIN_VALUE;
                } else {
                    this.powersOf2[ value ] = ( long ) Math.pow( 2, value );
                }
            }
        } else {
            this.powersOf2 = new long[ 64 ];
            for ( int value = 0; value < 64; value ++ ) {
                if ( value == 0 ) {
                    this.powersOf2[ value ] = Long.MIN_VALUE;
                } else {
                    this.powersOf2[ value ] = ( long ) Math.pow( 2, 63 - value );
                }
            }
        }
    }
    
    /**
     * Returns 2 raised to the power of value as a long.
     * @param value
     * TODO: move this to the chunk master and have as a long[]. The look up
     * should be faster than the creation on the fly.
     * @return 
     */
    public long powerOf2( int value ) {
        try {
            return this.powersOf2[ value ];
        } catch ( java.lang.NegativeArraySizeException e ) {
            System.out.println("Warning: Answer not a long in " + this.getClass().getName() + ".powerOf2( " + value + " ). Returning 0L!");
            return 0L;
        } catch ( java.lang.ArrayIndexOutOfBoundsException e ) {
            System.out.println("Warning: Answer too big for long precision in " + this.getClass().getName() + ".powerOf2( " + value + " ). Returning Long.MAX_VALUE!");
            return Long.MAX_VALUE;
        }
    }
}
