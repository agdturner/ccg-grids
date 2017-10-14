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
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;
//import gnu.trove.TDoubleIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunkArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellDoubleChunkArray 
 * instance. The values are not returned in any particular order.
 */
public class Grids_Grid2DSquareCellDoubleChunkArrayIterator extends Grids_AbstractIterator {
    
    private double[][] data;
    private int chunkRowIndex;
    private int chunkColIndex;
    private int chunkNrows;
    private int chunkNcols;
    
    
    /** Creates a new instance of Grid2DSquareDoubleIterator */
    public Grids_Grid2DSquareCellDoubleChunkArrayIterator() {}
    
    /**
     * Creates a new instance of Grid2DSquareDoubleIterator
     * @param grid2DSquareCellDoubleChunkArray The 
     * Grid2DSquareCellDoubleChunkArray to iterate over.
     */
    public Grids_Grid2DSquareCellDoubleChunkArrayIterator( Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray ) {
        this.data = grid2DSquareCellDoubleChunkArray.getData();
        this.chunkRowIndex = 0;
        this.chunkColIndex = 0;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = grid2DSquareCellDoubleChunkArray.getGrid2DSquareCellDouble();
        this.chunkNrows = grid2DSquareCellDouble.getChunkNRows(grid2DSquareCellDoubleChunkArray.ChunkID,
                ge.HandleOutOfMemoryErrorFalse );
        this.chunkNcols = grid2DSquareCellDouble.getChunkNCols(grid2DSquareCellDoubleChunkArray.ChunkID,
                ge.HandleOutOfMemoryErrorFalse );
    }
    
    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     * TODO:
     * Try and catch ArrayOutOfboundsException should be faster
     */
    @Override
    public boolean hasNext() {
        if ( chunkColIndex + 1 == this.chunkNcols ) {
            if ( chunkRowIndex + 1 == this.chunkNrows ) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    @Override
    public Object next() {
        if ( chunkColIndex + 1 == this.chunkNcols ) {
            if ( chunkRowIndex + 1 == this.chunkNrows ) {
                //throw NoSuchElementException;
                //return null;
            } else {
                this.chunkRowIndex ++;
                this.chunkColIndex = 0;
            }
        } else {
            this.chunkColIndex ++;
        }
        return new Double( this.data[ this.chunkRowIndex ][ this.chunkColIndex ] );
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
     
     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    @Override
    public void remove() {}
    
}
