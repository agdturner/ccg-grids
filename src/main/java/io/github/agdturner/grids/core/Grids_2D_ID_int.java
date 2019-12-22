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
package io.github.agdturner.grids.core;

import java.io.Serializable;

/**
 * Grids_2D_ID_int class for distinguishing chunks or cells within chunks.
 * 
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_2D_ID_int extends Object implements Serializable, 
        Comparable<Grids_2D_ID_int> {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the row.
     */
    protected int row;
    /**
     * For storing the column.
     */
    protected int col;

    protected Grids_2D_ID_int() {
    }

    public Grids_2D_ID_int(Grids_2D_ID_int i) {
        col = i.col;
        row = i.row;
    }

    /**
     *
     * @param row The row.
     * @param col The column.
     */
    public Grids_2D_ID_int(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return col
     */
    public int getCol() {
        return col;
    }

    /**
     * @return a description of this
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[Row=" + row + ", Col=" + col + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + row;
        hash = 97 * hash + col;
        return hash;
    }

    /**
     * Overrides equals in Object
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if ((object == null) || (object.getClass() != getClass())) {
            return false;
        }
        Grids_2D_ID_int i = (Grids_2D_ID_int) object;
        return ((col == i.col)
                && (row == i.row));
    }

    /**
     * Method required by Comparable.
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Grids_2D_ID_int t) {
        if (t.row > row) {
            return 1;
        }
        if (t.row < row) {
            return -1;
        }
        if (t.col > col) {
            return 1;
        }
        if (t.col < col) {
            return -1;
        }
        return 0;
    }
}
