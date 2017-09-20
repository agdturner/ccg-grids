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

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * Grids_AbstractGrid2DSquareCellChunk provides inner classes for ChunkCellID and
 general geometry methods for extended classes. It also controls what methods
 * extended classes must implement acting like an interface.
 * 
 * The basic geometries are ordered in set numbers of rows and columns and are
 * arranged sequentially as their base two-dimensional orthogonal coordinate
 * axes. The sequential arrangement goes along the x-axis row by row from the
 * y-axis, then up the y-axis taking each row in turn.
 * 
 * TODO:
 * Add an int TYPE attribute field which can be used to switch between different
 * types of instances?
 */
public abstract class Grids_AbstractGrid2DSquareCellChunk
        implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * A reference to the Grid2DSquareCellDoubleAbstract instance.
     */
    protected transient Grids_AbstractGrid2DSquareCell _Grid2DSquareCell;
    /**
     * For storing the Grids_2D_ID_int of this.
     * TODO:
     * Is this transient for caching?
     */
    protected transient Grids_2D_ID_int _ChunkID;
    //protected Grids_2D_ID_int _ChunkID;
    /**
     * Indicator for whether the swapped version of this chunk is upToDate.
     * TODO:
     * This adds a small amount of weight, so for 64CellMap implementations
     * it may be undesirable?
     */
    protected transient boolean isSwapUpToDate;
    //protected boolean isSwapUpToDate;

    /**
     * Returns this._Grid2DSquareCell.
     *
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_AbstractGrid2DSquareCell getGrid2DSquareCell(
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGrid2DSquareCell result = getGrid2DSquareCell();
            result.ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getGrid2DSquareCell(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns this._Grid2DSquareCell.
     * @return 
     */
    protected Grids_AbstractGrid2DSquareCell getGrid2DSquareCell() {
        return this._Grid2DSquareCell;
    }

    /**
     * Initialises _Grid2DSquareCell.
     *
     * @param _Grid2DSquareCell
     */
    protected void initGrid2DSquareCell(
            Grids_AbstractGrid2DSquareCell _Grid2DSquareCell) {
        this._Grid2DSquareCell = _Grid2DSquareCell;
    }

    /**
     * Initialises _ChunkID.
     *
     * @param _ChunkID
     */
    protected void initChunkID(
            Grids_2D_ID_int _ChunkID) {
        this._ChunkID = _ChunkID;
    }

    /**
     * Returns a copy of this._ChunkID.
     *
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_2D_ID_int getChunkID(
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getChunkID();
            getGrid2DSquareCell(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getChunkID(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns a copy of this._ChunkID.
     * @return 
     */
    protected Grids_2D_ID_int getChunkID() {
        return new Grids_2D_ID_int(this._ChunkID);
        //return this._ChunkID;
    }

    /**
     * Returns this.isSwapUpToDate
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public boolean getIsSwapUpToDate(
            boolean handleOutOfMemoryError) {
        try {
            boolean result = getIsSwapUpToDate();
            getGrid2DSquareCell(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getIsSwapUpToDate(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns this.isSwapUpToDate
     * @return 
     */
    protected boolean getIsSwapUpToDate() {
        return this.isSwapUpToDate;
    }

    /**
     * Sets this.isSwapUpToDate to isSwapUpToDate
     * @param isSwapUpToDate
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    protected void setIsSwapUpToDate(
            boolean isSwapUpToDate,
            boolean handleOutOfMemoryError) {
        try {
            setIsSwapUpToDate(isSwapUpToDate);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                setIsSwapUpToDate(
                        isSwapUpToDate,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns this.isSwapUpToDate
     * @param isSwapUpToDate
     */
    protected void setIsSwapUpToDate(
            boolean isSwapUpToDate) {
        this.isSwapUpToDate = isSwapUpToDate;
    }

    /**
     * For returning a description of this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public String toString(
            boolean handleOutOfMemoryError) {
        try {
            String result = getDescription();
            getGrid2DSquareCell(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return toString(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * For returning a description of this.
     * @return 
     */
    protected String getDescription() {
        return this.getClass().toString()
                + "( ChunkID ( " + this._ChunkID.toString() + " ) )";
    }

    /**
     * Returns the name of this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public String getName(
            boolean handleOutOfMemoryError) {
        try {
            String result = getName();
            getGrid2DSquareCell(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getName(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the name of this.
     * @return 
     */
    protected String getName() {
        return this.getClass().getName();
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed
     * to be in any particular order.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_AbstractIterator iterator(
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractIterator result = iterator();
            getGrid2DSquareCell(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return iterator(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed
     * to be in any particular order.
     * @return 
     */
    protected abstract Grids_AbstractIterator iterator();

    /**
     * Returns true if the cell given by chunk cell row index
     * chunkCellRowIndex, chunk cell col index chunkCellColIndex is in this.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public boolean inChunk(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = inChunk(
                    chunkCellRowIndex,
                    chunkCellColIndex);
            getGrid2DSquareCell(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.ge.clear_MemoryReserve();
                if (this._Grid2DSquareCell.ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.ge.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return inChunk(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns true if the cell given by chunk cell row index
     * chunkCellRowIndex, chunk cell col index chunkCellColIndex is in this.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @return 
     */
    protected boolean inChunk(
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        int chunkNrows = this._Grid2DSquareCell.getChunkNRows(this._ChunkID);
        int chunkNcols = this._Grid2DSquareCell.getChunkNCols(this._ChunkID);
        return (chunkCellRowIndex > -1
                && chunkCellRowIndex < chunkNrows
                && chunkCellColIndex > -1
                && chunkCellColIndex < chunkNcols);
    }

    /**
     * For clearing the data associated with this.
     */
    protected abstract void clearData();

    /**
     * For initialising the data associated with this.
     */
    protected abstract void initData();

    /**
     * A simple ChunkCellID class for distinguishing cells in this chunk.
     */
    public class ChunkCellID
            implements Serializable, Comparable {

        /**
         * For storing the row in which the cell is positioned
         */
        protected int chunkCellRowIndex;
        /**
         * For storing the column in which the cell is positioned
         */
        protected int chunkCellColIndex;

        /**
         * Constructs a new ChunkCellID
         */
        protected ChunkCellID() {
        }

        /**
         * Constructs a new ChunkCellID
         * @param chunkCellRowIndex the row of the cell
         * @param chunkCellColIndex the column of the cell
         */
        protected ChunkCellID(
                int chunkCellRowIndex,
                int chunkCellColIndex) {
            this.chunkCellRowIndex = chunkCellRowIndex;
            this.chunkCellColIndex = chunkCellColIndex;
        }

        /**
         * Constructs a new ChunkCellID
         * 
         * 
         * @param chunkCellRowIndex the row of the cell
         * @param chunkCellColIndex the column of the cell
         * @param _ChunkNCols the number of columns in the chunk
         */
        protected ChunkCellID(
                int _ChunkNCols,
                int chunkCellRowIndex,
                int chunkCellColIndex) {
            this.chunkCellRowIndex = chunkCellRowIndex;
            this.chunkCellColIndex = chunkCellColIndex;
        }

        /**
         * Returns chunkCellRowIndex of this CellID
         * @return 
         */
        public long getChunkCellRowIndex() {
            return this.chunkCellRowIndex;
        }

        /**
         * Returns chunkCellColIndex of this CellID
         * @return 
         */
        public long getChunkCellColIndex() {
            return this.chunkCellColIndex;
        }

        /**
         * Returns a description of this CellID
         */
        @Override
        public String toString() {
            return "chunkCellRowIndex( " + getChunkCellRowIndex() + " ), "
                    + "chunkCellColIndex( " + getChunkCellColIndex() + " )";
        }

        /**
         * Overrides equals in Object
         * @param object
         */
        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if ((object == null) || (object.getClass() != this.getClass())) {
                return false;
            }
            ChunkCellID anotherChunkCellID = (ChunkCellID) object;
            return (this.chunkCellColIndex == anotherChunkCellID.chunkCellColIndex)
                    && (this.chunkCellRowIndex == anotherChunkCellID.chunkCellRowIndex);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.chunkCellRowIndex;
            hash = 79 * hash + this.chunkCellColIndex;
            return hash;
        }

        /**
         * Method required by Comparable
         * @param object
         */
        @Override
        public int compareTo(Object object) {
            //try {
            //Cast failure should result in NullPointerException or ClassCastException
            ChunkCellID chunkCellID = (ChunkCellID) object;
            if (chunkCellID.chunkCellRowIndex > this.chunkCellRowIndex) {
                return 1;
            }
            if (chunkCellID.chunkCellRowIndex < this.chunkCellRowIndex) {
                return -1;
            }
            if (chunkCellID.chunkCellColIndex > this.chunkCellColIndex) {
                return 1;
            }
            if (chunkCellID.chunkCellColIndex < this.chunkCellColIndex) {
                return -1;
            }
            return 0;
            //} catch ( Exception e0 ) {
            //    return -1;
            //}
        }
    }
}
