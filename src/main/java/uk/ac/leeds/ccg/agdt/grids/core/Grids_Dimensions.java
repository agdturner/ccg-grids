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

import java.math.BigDecimal;

/**
 * For storing the dimensions of a grid.
 * 
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Dimensions extends Grids_Object {

    private final BigDecimal XMin;
    private final BigDecimal XMax;
    private final BigDecimal YMin;
    private final BigDecimal YMax;
    private final BigDecimal Cellsize;
    private final BigDecimal HalfCellsize;
    private final BigDecimal Width;
    private final BigDecimal Height;
    private final BigDecimal Area;

    public Grids_Dimensions(int nRows, int nCols) {
        this(BigDecimal.ZERO, new BigDecimal(nCols), BigDecimal.ZERO,
                new BigDecimal(nRows), BigDecimal.ONE);
    }

    public Grids_Dimensions(long nRows, long nCols) {
        this(BigDecimal.ZERO, BigDecimal.valueOf(nCols), BigDecimal.ZERO,
                BigDecimal.valueOf(nRows), BigDecimal.ONE);
    }

    public Grids_Dimensions(BigDecimal width, BigDecimal height) {
        this(BigDecimal.ZERO, width, BigDecimal.ZERO, height, BigDecimal.ONE);
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
    public Grids_Dimensions(BigDecimal xMin, BigDecimal xMax, BigDecimal yMin, 
            BigDecimal yMax, BigDecimal cellsize) {
        this.XMin = xMin;
        this.XMax = xMax;
        this.YMin = yMin;
        this.YMax = yMax;
        Cellsize = cellsize;
        Width = XMax.subtract(XMin);
        Height = YMax.subtract(YMin);
        Area = Width.multiply(Height);
        HalfCellsize = Cellsize.divide(BigDecimal.valueOf(2L));
    }

    /**
     * @return a description of this
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[XMin=" + XMin + ", XMax=" + XMax 
                + ", YMin=" + YMin + ", YMax=" + YMax + ", Cellsize=" + Cellsize 
                + "]";
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
