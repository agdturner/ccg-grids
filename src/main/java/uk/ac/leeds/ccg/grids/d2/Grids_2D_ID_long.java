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
package uk.ac.leeds.ccg.grids.d2;

import java.io.Serializable;

/**
 * Grids_2D_ID_long class for distinguishing chunks or cells within chunks.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_2D_ID_long extends Object implements Serializable,
        Comparable<Grids_2D_ID_long> {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the row.
     */
    protected long row;
    /**
     * For storing the column.
     */
    protected long col;

    /**
     * Create a new instance.
     */
    protected Grids_2D_ID_long() {
    }

    /**
     * Create a new instance.
     *
     * @param i Used to initialise {@code this}.
     */
    public Grids_2D_ID_long(Grids_2D_ID_long i) {
        col = i.col;
        row = i.row;
    }

    /**
     * Create a new instance.
     *
     * @param row The row.
     * @param col The column.
     */
    public Grids_2D_ID_long(long row, long col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return row
     */
    public long getRow() {
        return row;
    }

    /**
     * @return col
     */
    public long getCol() {
        return col;
    }

    /**
     * @return a description of this
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[Row=" + row + ", Col=" + col + "]";
    }

    /**
     * Overrides equals in Object.
     *
     * @param o The Object to compare this with.
     * @return {@code true} if this is equal to {@code o} and {@code false}
     * otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (o.getClass() != getClass())) {
            return false;
        }
        Grids_2D_ID_long i = (Grids_2D_ID_long) o;
        return ((col == i.col)
                && (row == i.row));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (int) (row ^ (row >>> 32));
        hash = 13 * hash + (int) (col ^ (col >>> 32));
        return hash;
    }

    /**
     * Method required by Comparable.
     *
     * @param i The instance to compare with.
     * @return -1, 0, 1 depending on whether this is less than, the same or
     * greater than {@code i}.
     */
    @Override
    public int compareTo(Grids_2D_ID_long i) {
        if (i.row > row) {
            return 1;
        }
        if (i.row < row) {
            return -1;
        }
        if (i.col > col) {
            return 1;
        }
        if (i.col < col) {
            return -1;
        }
        return 0;
    }
}
