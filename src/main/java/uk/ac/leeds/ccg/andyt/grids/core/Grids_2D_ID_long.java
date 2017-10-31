/*
 * Copyright (C) 2017 geoagdt.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.grids.core;

import java.io.Serializable;

/**
 * A simple ID class for distinguishing chunks or cells within chunks.
 */
public class Grids_2D_ID_long extends Object implements Serializable, Comparable {

    /**
     * For storing the row.
     */
    protected long Row;
    /**
     * For storing the column.
     */
    protected long Col;

    protected Grids_2D_ID_long() {}

    public Grids_2D_ID_long(Grids_2D_ID_long i) {
        Col = i.Col;
        Row = i.Row;
    }

    /**
     *
     * @param row The row.
     * @param col The column.
     */
    public Grids_2D_ID_long(
            long row,
            long col) {
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
        return "Grids_2D_ID_int("
                + "Row(" + Row + "), "
                + "Col(" + Col + "))";
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
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Grids_2D_ID_long) {
            Grids_2D_ID_long i = (Grids_2D_ID_long) o;
            if (i.Row > Row) {
                return 1;
            }
            if (i.Row < Row) {
                return -1;
            }
            if (i.Col > Col) {
                return 1;
            }
            if (i.Col < Col) {
                return -1;
            }
            return 0;
        }
        return -1;
    }
}
