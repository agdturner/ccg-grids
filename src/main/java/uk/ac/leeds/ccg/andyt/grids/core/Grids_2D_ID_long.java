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
     * For storing the row
     */
    protected long _Row;
    /**
     * For storing the column
     */
    protected long _Col;

    public Grids_2D_ID_long() {
        this._Col = Integer.MIN_VALUE;
        this._Row = Integer.MIN_VALUE;
    }

    public Grids_2D_ID_long(Grids_2D_ID_long i) {
        this._Col = i._Col;
        this._Row = i._Row;
    }

    /**
     *
     * @param row The row.
     * @param col The column.
     */
    public Grids_2D_ID_long(
            long row,
            long col) {
        this._Row = row;
        this._Col = col;
    }

    /**
     * @return this._Row
     */
    public long getRow() {
        return this._Row;
    }

    /**
     * @return this._Col
     */
    public long getCol() {
        return this._Col;
    }

    /**
     * @return a description of this
     */
    @Override
    public String toString() {
        return "Grids_2D_ID_int( "
                + "Row( " + getRow() + " ), "
                + "Col( " + getCol() + " ) )";
    }

    /**
     * Overrides equals in Object
     *
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
        Grids_2D_ID_long i = (Grids_2D_ID_long) object;
        return ((this._Col == i._Col)
                && (this._Row == i._Row));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (int) (this._Row ^ (this._Row >>> 32));
        hash = 13 * hash + (int) (this._Col ^ (this._Col >>> 32));
        return hash;
    }

    /**
     * Method required by Comparable
     *
     * @param o
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Grids_2D_ID_long) {
            Grids_2D_ID_long i = (Grids_2D_ID_long) o;
            if (i._Row > this._Row) {
                return 1;
            }
            if (i._Row < this._Row) {
                return -1;
            }
            if (i._Col > this._Col) {
                return 1;
            }
            if (i._Col < this._Col) {
                return -1;
            }
            return 0;
        }
        return -1;
    }
}
