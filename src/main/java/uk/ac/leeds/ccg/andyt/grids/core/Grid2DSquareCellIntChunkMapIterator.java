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
import gnu.trove.TIntObjectIterator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellIntChunkMap instance. 
 * The values are not returned in any particular order.
 */
public class Grid2DSquareCellIntChunkMapIterator 
        extends AbstractIterator {
    
    private TIntObjectIterator dataIterator;
    private long iteratorIndex;
    private long numberOfCells;
    
    //    private Iterator hashMapIterator;
    //    private HashSet hashSet;
    //    private Iterator hashSetIterator;
    //    private ChunkCellID chunkCellID;
    
    private int value;
    private long size;
    private long valueCounter;
    private int noDataValue;
    
    /** Creates a new instance of Grid2DSquareIntIterator */
    public Grid2DSquareCellIntChunkMapIterator() {}
    
    /**
     * Creates a new instance of Grid2DSquareIntIterator
     * @param grid2DSquareCellIntChunkMap The Grid2DSquareCellIntChunkMap to 
     *   iterate over
     */
    public Grid2DSquareCellIntChunkMapIterator(
            Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap) {
        this.dataIterator = grid2DSquareCellIntChunkMap.getData().iterator();
        iteratorIndex = 0L;
        Grid2DSquareCellInt grid2DSquareCellInt = 
                grid2DSquareCellIntChunkMap.getGrid2DSquareCellInt();
        this.noDataValue = grid2DSquareCellInt.getNoDataValue( 
                env.HandleOutOfMemoryErrorFalse );
        long chunkNrows = ( long ) grid2DSquareCellInt.getChunkNRows( 
                grid2DSquareCellIntChunkMap._ChunkID, 
                env.HandleOutOfMemoryErrorFalse );
        long chunkNcols = ( long ) grid2DSquareCellInt.getChunkNCols( 
                grid2DSquareCellIntChunkMap._ChunkID, 
                env.HandleOutOfMemoryErrorFalse );
        this.numberOfCells = chunkNrows * chunkNcols;
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
        Integer next = new Integer( this.value );
        this.iteratorIndex ++;
        try {
            if ( this.value != noDataValue ) {
                if ( this.valueCounter == this.size ) {
                    try { // In case no more!
                        this.dataIterator.advance();
                        this.value = dataIterator.key();
                        this.valueCounter = 1;
                        try {
                            this.size = 
                                    ( ( HashSet ) dataIterator.value() ).size();
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

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
