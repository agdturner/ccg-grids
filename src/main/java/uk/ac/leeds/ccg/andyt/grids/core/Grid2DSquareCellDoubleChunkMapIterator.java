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
import gnu.trove.TDoubleObjectIterator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellDoubleChunkMap 
 * instance. The values are not returned in any particular order.
 */
public class Grid2DSquareCellDoubleChunkMapIterator extends AbstractIterator {
    
    private TDoubleObjectIterator dataIterator;
    private long iteratorIndex;
    private long numberOfCells;
    
    //    private Iterator hashMapIterator;
    //    private HashSet hashSet;
    //    private Iterator hashSetIterator;
    //    private ChunkCellID chunkCellID;
    
    private double value;
    private long size;
    private long valueCounter;
    private double noDataValue;
    
    /** Creates a new instance of Grid2DSquareDoubleIterator */
    public Grid2DSquareCellDoubleChunkMapIterator() {}
    
    /**
     * Creates a new instance of Grid2DSquareDoubleIterator
     * @param grid2DSquareCellDoubleChunkMap The Grid2DSquareCellDoubleChunkMap 
     *   to iterate over.
     */
    public Grid2DSquareCellDoubleChunkMapIterator(
            Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap ) {
        this.dataIterator = grid2DSquareCellDoubleChunkMap.getData().iterator();
        iteratorIndex = 0L;
        Grid2DSquareCellDouble grid2DSquareCellDouble = grid2DSquareCellDoubleChunkMap.getGrid2DSquareCellDouble();
        this.noDataValue = grid2DSquareCellDouble._NoDataValue;
        this.numberOfCells = ( 
                ( long ) grid2DSquareCellDouble.getChunkNRows(
                grid2DSquareCellDoubleChunkMap._ChunkID,
                env.HandleOutOfMemoryErrorFalse ) *
                ( long ) grid2DSquareCellDouble.getChunkNCols(
                grid2DSquareCellDoubleChunkMap._ChunkID,
                env.HandleOutOfMemoryErrorFalse ) );
        try { // In case empty!
            this.dataIterator.advance();
            this.value = dataIterator.key();
            this.valueCounter = 1L;
            try {
                this.size = ( ( HashSet ) dataIterator.value() ).size();
            } catch ( java.lang.ClassCastException e ) {
                this.size = 1L;
            }
        } catch ( NoSuchElementException nsee0 ) {
            this.value = this.noDataValue;
            //this.valueCounter = this.numberOfCells;
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
        if ( iteratorIndex + 2L < numberOfCells ) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    @Override
    public Object next() {
        Double next = new Double( this.value );
        this.iteratorIndex ++;
        try {
            if ( this.value != noDataValue ) {
                if ( this.valueCounter == this.size ) {
                    try { // In case no more!
                        this.dataIterator.advance();
                        this.value = dataIterator.key();
                        this.valueCounter = 1;
                        try {
                            this.size = ( ( HashSet ) dataIterator.value() ).size();
                        } catch ( java.lang.ClassCastException e ) {
                            this.size = 1;
                        }
                    } catch ( NoSuchElementException nsee0 ) {
                        this.value = noDataValue;
                    }
                } else {
                    this.valueCounter ++;
                }
            }
        } catch ( Exception e0 ) {
            // Should be last element!
            //e0.printStackTrace();
        }
        return next;
    }
    
    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
     *
     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove() {}
    
}
