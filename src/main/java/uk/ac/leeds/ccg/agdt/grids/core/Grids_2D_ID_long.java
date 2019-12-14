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
package uk.ac.leeds.ccg.agdt.grids.core;

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
    protected long Row;
    /**
     * For storing the column.
     */
    protected long Col;

    protected Grids_2D_ID_long() {
    }

    public Grids_2D_ID_long(Grids_2D_ID_long i) {
        Col = i.Col;
        Row = i.Row;
    }

    /**
     *
     * @param row The row.
     * @param col The column.
     */
    public Grids_2D_ID_long(long row, long col) {
        Row = row;
        Col = col;
    }

    /**
     * @return Row
     */
    public long getRow() {
        return Row;
    }

    /**
     * @return Col
     */
    public long getCol() {
        return Col;
    }

    /**
     * @return a description of this
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[Row=" + Row + ", Col=" + Col + "]";
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
        Grids_2D_ID_long i = (Grids_2D_ID_long) object;
        return ((Col == i.Col)
                && (Row == i.Row));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (int) (Row ^ (Row >>> 32));
        hash = 13 * hash + (int) (Col ^ (Col >>> 32));
        return hash;
    }

    /**
     * Method required by Comparable
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Grids_2D_ID_long t) {
        if (t.Row > Row) {
            return 1;
        }
        if (t.Row < Row) {
            return -1;
        }
        if (t.Col > Col) {
            return 1;
        }
        if (t.Col < Col) {
            return -1;
        }
        return 0;
    }
}
