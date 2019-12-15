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
package io.github.agdturner.grids.util;

/**
 * A 64 bit number representing the natural numbers from 0 to 2 raised to the 
 * power of 64.
 *
 * NB. It can be better to use a look up to a <code>Powersof2</code> instance 
 *   or set of them rather than calculate them on the fly.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_UnsignedLong {
    
    /**
     * The value of the <code>Grids_UnsignedLong</code>.
     * @serial
     */
    private long value;
    
    /**
     * Constructs a newly allocated <code>UnsignedLong</code> object that
     * represents the default long 0L.
     */
    public Grids_UnsignedLong() {
        this.value = 0L;
    }

    /**
     * Constructs a newly allocated <code>UnsignedLong</code> object that
     * represents the specified <code>long</code> argument.
     * @param value The value to be represented by this.
     */
    public Grids_UnsignedLong( long value ) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated <code>UnsignedLong</code> object that
     * represents a <code>long</code> binary encoded by the specified 
     * <code>boolean[]</code> argument.
     * @param binaryEncoding The binary encoding of the <code>long</code> value 
     *   to be represented by the <code>UnsignedLong</code> object.
     */
    public Grids_UnsignedLong( boolean[] binaryEncoding ) {
        long value = 0L;
        long power2;
        for ( int i = 0; i < binaryEncoding.length; i ++ ) {
            if ( binaryEncoding[ binaryEncoding.length - 1 - i ] ) {
                power2 = 1L;
                for ( int j = 0; j < i; j ++ ) {
                    power2 *= 2L;
                }
                value += power2;
            }
        }
        setLong( value );
    }
    
    /**
     * Returns this.value. 
     * @return 
     */
    public long getLong() {
        return this.value;
    }
    
    /**
     * Sets this.value to value.
     * @param value The value this.value is set to.
     */
    public void setLong( long value ) {
        this.value = value;
    }
    
    /**
     * Returns this.value as a binary encoded boolean[].
     * @return 
     */
    public boolean[] toBooleanArray() {
        boolean[] b = new boolean[64];
        long v = getLong();
        for ( int i = 0; i < 64; i ++ ) {
            //if( ( ( getLong() >>> ( b.length -  1 - i ) ) & 1L ) == 1 ) {
            if( ( ( v >>> ( 63 - i ) ) & 1L ) == 1 ) {
                b[i] = true;
            }
            //System.out.print( b[i] + " " );
        }
        //System.out.println("");
        return b;
    }

    /**
     * Returns true if the value of long as a binary encoding has a value of 1 
     * at position.
     * @param position
     * @return 
     */
    public boolean isAtPosition( int position ) {
        if( ( ( getLong() >>> ( 63 - position ) ) & 1L ) == 1 ) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns the value of long as a binary encoded String.
     * @return 
     */
    public String toBinaryString() {
        //int length = 64;
        StringBuffer b = new StringBuffer( 64 );
        long v = getLong();
        for ( int i = 0; i < 64; i ++ ) {
            //System.out.print( ( getLong() >>> ( length -  1 - i ) ) & 1L );
            b.append( ( v >>> ( 63 - i ) ) & 1L );
        }
        //System.out.println("");
        return b.toString();
    }
    
    /**
     * Returns a count of the number of values of 1 in the binary encoding of 
     * value.
     * @return 
     */
    public byte getCount() {
        byte b = 0;
        long v = getLong();
        for ( int i = 0; i < 64; i ++ ) {
            if( ( ( v >>> ( 63 - i ) ) & 1L ) == 1 ) {
                b ++;
            }
        }
        return b;
    }
    
}
