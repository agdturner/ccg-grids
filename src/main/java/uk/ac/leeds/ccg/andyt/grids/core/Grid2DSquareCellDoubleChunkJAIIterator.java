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
import javax.media.jai.TiledImage;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellDoubleChunkJAI 
 * instance. The values are not returned in any particular order.
 */
public class Grid2DSquareCellDoubleChunkJAIIterator extends AbstractIterator {
    
    private TiledImage data;
    private int chunkRowIndex;
    private int chunkColIndex;
    private int chunkNrows;
    private int chunkNcols;
    
    /** Creates a new instance of Grid2DSquareDoubleIterator */
    public Grid2DSquareCellDoubleChunkJAIIterator() {}
    
    /**
     * Creates a new instance of Grid2DSquareDoubleIterator
     * @param grid2DSquareCellDoubleChunkJAI The Grid2DSquareCellDoubleChunkJAI 
     *   to iterate over.
     */
    public Grid2DSquareCellDoubleChunkJAIIterator(Grid2DSquareCellDoubleChunkJAI grid2DSquareCellDoubleChunkJAI) {
        this.data = grid2DSquareCellDoubleChunkJAI.getData();
        this.chunkRowIndex = 0;
        this.chunkColIndex = 0;
        Grid2DSquareCellDouble grid2DSquareCellDouble = grid2DSquareCellDoubleChunkJAI.getGrid2DSquareCellDouble();
        this.chunkNrows = grid2DSquareCellDouble.getChunkNRows( 
                grid2DSquareCellDoubleChunkJAI._ChunkID,
                env.HandleOutOfMemoryErrorFalse );
        this.chunkNcols = grid2DSquareCellDouble.getChunkNCols(
                grid2DSquareCellDoubleChunkJAI._ChunkID,
                env.HandleOutOfMemoryErrorFalse );
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
        return new Double( this.data.getSampleDouble( chunkColIndex, chunkRowIndex, 0 ) );
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
