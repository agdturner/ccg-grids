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
package uk.ac.leeds.ccg.andyt.grids.core;
import java.io.IOException;
import java.io.RandomAccessFile;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellIntChunkRAF instance. 
 * The values are not returned in any particular order.
 */
public class Grid2DSquareCellIntChunkRAFIterator extends AbstractIterator {
    
    private RandomAccessFile data;
    
    /** Creates a new instance of Grid2DSquareIntIterator */
    public Grid2DSquareCellIntChunkRAFIterator() {}
    
    /**
     * Creates a new instance of Grid2DSquareIntIterator
     * @param grid2DSquareCellIntChunkRAF The Grid2DSquareCellIntChunkRAF to 
     *   iterate over.
     */
    public Grid2DSquareCellIntChunkRAFIterator(
            Grid2DSquareCellIntChunkRAF grid2DSquareCellIntChunkRAF) {
        this.data = grid2DSquareCellIntChunkRAF.getData();
        try {
            this.data.seek( 0L );
        } catch ( IOException ioe0 ) {
            // Shouldn't happen!
            ioe0.printStackTrace();
        }
    }
    
    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
        try {
        if ( this.data.getFilePointer() < this.data.length() ) {
            return true;
        }
        } catch ( IOException ioe0 ) {
            // Shouldn't happen...
            ioe0.printStackTrace();
        }
        return false;
    }
    
    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    @Override
    public Object next() {
        try {
            return new Integer( this.data.readInt() );
        } catch ( IOException ioe0 ) {
            // Should be at the end of the file...
            ioe0.printStackTrace();
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
