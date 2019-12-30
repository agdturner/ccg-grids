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
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A POJO for storing the dimensions of a grid.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Dimensions implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * The minimum x.
     */
    private final BigDecimal xMin;

    /**
     * The maximum x.
     */
    private final BigDecimal xMax;

    /**
     * The minimum y.
     */
    private final BigDecimal yMin;

    /**
     * The maximum y.
     */
    private final BigDecimal yMax;

    /**
     * The cellsize (width or height.
     */
    private final BigDecimal cellsize;

    /**
     * Half the cellsize.
     */
    private final BigDecimal halfCellsize;

    /**
     * The cellsize squared.
     */
    private final BigDecimal cellsizeSquared;

    /**
     * The width.
     */
    private final BigDecimal width;

    /**
     * The height.
     */
    private final BigDecimal height;

    /**
     * The area.
     */
    private final BigDecimal area;

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
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.cellsize = cellsize;
        width = this.xMax.subtract(this.xMin);
        height = this.yMax.subtract(this.yMin);
        area = width.multiply(height);
        halfCellsize = this.cellsize.divide(BigDecimal.valueOf(2L));
        cellsizeSquared = this.cellsize.multiply(this.cellsize);
    }

    /**
     * @return A text description of this.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[XMin=" + getXMin() + ", XMax="
                + getXMax() + ", YMin=" + getYMin() + ", YMax=" + getYMax()
                + ", Cellsize=" + getCellsize() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Grids_Dimensions) {
            Grids_Dimensions o2 = (Grids_Dimensions) o;
            if (this.hashCode() == o2.hashCode()) {
                if (this.cellsize == o2.cellsize) {
                    if (this.xMin == o2.xMin) {
                        if (this.xMax == o2.xMax) {
                            if (this.yMin == o2.yMin) {
                                if (this.yMax == o2.yMax) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.xMin);
        hash = 37 * hash + Objects.hashCode(this.xMax);
        hash = 37 * hash + Objects.hashCode(this.yMin);
        hash = 37 * hash + Objects.hashCode(this.yMax);
        hash = 37 * hash + Objects.hashCode(this.cellsize);
        return hash;
    }

    /**
     * @return the xMin
     */
    public BigDecimal getXMin() {
        return xMin;
    }

    /**
     * @return the xMax
     */
    public BigDecimal getXMax() {
        return xMax;
    }

    /**
     * @return the yMin
     */
    public BigDecimal getYMin() {
        return yMin;
    }

    /**
     * @return the yMax
     */
    public BigDecimal getYMax() {
        return yMax;
    }

    /**
     * @return the cellsize
     */
    public BigDecimal getCellsize() {
        return cellsize;
    }

    /**
     * @return the halfCellsize
     */
    public BigDecimal getHalfCellsize() {
        return halfCellsize;
    }

    /**
     * @return the cellsizeSquared
     */
    public BigDecimal getCellsizeSquared() {
        return cellsizeSquared;
    }

    /**
     * @return the width
     */
    public BigDecimal getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public BigDecimal getHeight() {
        return height;
    }

    /**
     * @return the area
     */
    public BigDecimal getArea() {
        return area;
    }

}
