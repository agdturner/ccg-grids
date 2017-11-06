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
import java.math.BigDecimal;

/**
 *
 * @author geoagdt
 */
public class Grids_Dimensions implements Serializable {

    private final BigDecimal XMin;
    private final BigDecimal XMax;
    private final BigDecimal YMin;
    private final BigDecimal YMax;
    private final BigDecimal Cellsize;
    private final BigDecimal HalfCellsize;
    private final BigDecimal Width;
    private final BigDecimal Height;
    private final BigDecimal Area;

    public Grids_Dimensions(int NRows, int NCols) {
        this(BigDecimal.ZERO,
                new BigDecimal(NRows),
                BigDecimal.ZERO,
                new BigDecimal(NCols),
                BigDecimal.ONE);
    }

    /**
     * DimensionsScale will default to the maximum scale in any of the
     * BigDecimal inputs.
     *
     * @param xMin The minimum x coordinate.
     * @param xMax The maximum x coordinate.
     * @param yMin The minimum y coordinate.
     * @param yMax The maximum y coordinate.
     * @param cellsize The cellsize.
     */
    public Grids_Dimensions(
            BigDecimal xMin,
            BigDecimal xMax,
            BigDecimal yMin,
            BigDecimal yMax,
            BigDecimal cellsize) {
        this.XMax = xMax;
        this.XMin = xMin;
        this.YMax = yMax;
        this.YMin = yMin;
        Cellsize = cellsize;
        Width = XMax.subtract(XMin);
        Height = YMax.subtract(YMin);
        Area = Width.multiply(Height);
        HalfCellsize = Cellsize.divide(BigDecimal.valueOf(2L));
    }

    @Override
    public String toString() {
        String result;
        result = "Dimensions("
                + "XMin(" + XMin + "),"
                + "XMax(" + XMax + "),"
                + "YMin(" + YMin + "),"
                + "YMax(" + YMax + "),"
                + "Cellsize(" + Cellsize + "))";
        return result;
    }

    /**
     * @return the XMin
     */
    public BigDecimal getXMin() {
        return XMin;
    }

    /**
     * @return the XMax
     */
    public BigDecimal getXMax() {
        return XMax;
    }

    /**
     * @return the YMin
     */
    public BigDecimal getYMin() {
        return YMin;
    }

    /**
     * @return the YMax
     */
    public BigDecimal getYMax() {
        return YMax;
    }

    /**
     * @return the Cellsize
     */
    public BigDecimal getCellsize() {
        return Cellsize;
    }

    public BigDecimal getWidth() {
        return Width;
    }

    public BigDecimal getHeight() {
        return Height;
    }

    public BigDecimal getArea() {
        return Area;
    }

    public BigDecimal getHalfCellsize() {
        return HalfCellsize;
    }

}
