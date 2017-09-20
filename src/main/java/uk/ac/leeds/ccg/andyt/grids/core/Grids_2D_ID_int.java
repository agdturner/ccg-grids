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
public class Grids_2D_ID_int extends Object implements Serializable, Comparable {

    /**
     * For storing the row
     */
    protected int _Row;
    /**
     * For storing the column
     */
    protected int _Col;

    public Grids_2D_ID_int() {
        this._Col = Integer.MIN_VALUE;
        this._Row = Integer.MIN_VALUE;
    }

    public Grids_2D_ID_int(Grids_2D_ID_int i) {
        this._Col = i._Col;
        this._Row = i._Row;
    }

    /**
     *
     * @param row The row.
     * @param col The column.
     */
    public Grids_2D_ID_int(
            int row,
            int col) {
        this._Row = row;
        this._Col = col;
    }

    /**
     * @return this._Row
     */
    public int getRow() {
        return this._Row;
    }

    /**
     * @return this._Col
     */
    public int getCol() {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this._Row;
        hash = 97 * hash + this._Col;
        return hash;
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
        Grids_2D_ID_int i = (Grids_2D_ID_int) object;
        return ((this._Col == i._Col)
                && (this._Row == i._Row));
    }

    /**
     * Method required by Comparable
     *
     * @param o
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof Grids_2D_ID_int) {
            Grids_2D_ID_int i = (Grids_2D_ID_int) o;
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
