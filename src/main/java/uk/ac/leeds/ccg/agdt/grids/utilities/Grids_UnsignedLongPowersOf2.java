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
package uk.ac.leeds.ccg.agdt.grids.utilities;

/**
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_UnsignedLongPowersOf2 {
    
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
        Grids_UnsignedLongPowersOf2 l = new Grids_UnsignedLongPowersOf2();
        for ( int i = 0; i < l.powersOf2.length; i ++ ) {
            System.out.println( l.powersOf2[ i ] );
        }
        System.out.println( "Processing complete in " + Grids_Utilities.getTime( System.currentTimeMillis() - time ) );
    }
    
    /**
     * Creates a new instance of PowersOf2
     * Defaults array to length 63.
     */
    public Grids_UnsignedLongPowersOf2() {
        this( true );
    }
    
    /**
     * Creates a new instance of PowersOf2
     * Defaults array to length 63.
     * @param ascending
     */
    public Grids_UnsignedLongPowersOf2( boolean ascending ) {
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
