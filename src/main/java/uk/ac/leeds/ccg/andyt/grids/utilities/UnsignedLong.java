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
 * A 64 bit number representing the natural numbers from 0 to 2 raised to the 
 * power of 64.
 *
 * NB. It can be better to use a look up to a <code>Powersof2</code> instance 
 *   or set of them rather than calculate them on the fly.
 */
public class UnsignedLong {
    
    /**
     * The value of the <code>UnsignedLong</code>.
     * @serial
     */
    private long value;
    
    /**
     * Constructs a newly allocated <code>UnsignedLong</code> object that
     * represents the default long 0L.
     */
    public UnsignedLong() {
        this.value = 0L;
    }

    /**
     * Constructs a newly allocated <code>UnsignedLong</code> object that
     * represents the specified <code>long</code> argument.
     * @param value The value to be represented by this.
     */
    public UnsignedLong( long value ) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated <code>UnsignedLong</code> object that
     * represents a <code>long</code> binary encoded by the specified 
     * <code>boolean[]</code> argument.
     * @param binaryEncoding The binary encoding of the <code>long</code> value 
     *   to be represented by the <code>UnsignedLong</code> object.
     */
    public UnsignedLong( boolean[] binaryEncoding ) {
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
