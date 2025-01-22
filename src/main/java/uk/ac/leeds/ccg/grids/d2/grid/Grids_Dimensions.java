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
package uk.ac.leeds.ccg.grids.d2.grid;

import ch.obermuhlner.math.big.BigRational;
import java.io.Serializable;
import java.util.Objects;

/**
 * For storing and testing the dimensions of a grid.
 *
 * @author Andy Turner
 * @version 2.0
 */
public class Grids_Dimensions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The minimum x.
     */
    private final BigRational xMin;

    /**
     * The maximum x.
     */
    private final BigRational xMax;

    /**
     * The minimum y.
     */
    private final BigRational yMin;

    /**
     * The maximum y.
     */
    private final BigRational yMax;

    /**
     * The cellsize (width and height of a cell).
     */
    private final BigRational cellsize;

    /**
     * Half the cellsize.
     */
    private final BigRational halfCellsize;

    /**
     * The cellsize squared.
     */
    private final BigRational cellsizeSquared;

    /**
     * The width.
     */
    private final BigRational width;

    /**
     * The height.
     */
    private final BigRational height;

    /**
     * The area.
     */
    private final BigRational area;

    /**
     * @param nRows The nrows.
     * @param nCols The ncols.
     */
    public Grids_Dimensions(int nRows, int nCols) {
        this(BigRational.ZERO, BigRational.valueOf(nCols), BigRational.ZERO,
                BigRational.valueOf(nRows), BigRational.ONE);
    }

    /**
     * @param nRows The nrows.
     * @param nCols The ncols.
     */
    public Grids_Dimensions(long nRows, long nCols) {
        this(BigRational.ZERO, BigRational.valueOf(nCols), BigRational.ZERO,
                BigRational.valueOf(nRows), BigRational.ONE);
    }

    /**
     * @param width The width.
     * @param height The height.
     */
    public Grids_Dimensions(BigRational width, BigRational height) {
        this(BigRational.ZERO, width, BigRational.ZERO, height, BigRational.ONE);
    }

    /**
     * Create a new instance.
     * 
     * @param d Grids_Dimensions
     */
    public Grids_Dimensions(Grids_Dimensions d) {
        this.area = d.area;
        this.cellsize = d.cellsize;
        this.cellsizeSquared = d.cellsizeSquared;
        this.halfCellsize = d.halfCellsize;
        this.height = d.height;
        this.width = d.width;
        this.xMax = d.xMax;
        this.xMin = d.xMin;
        this.yMax = d.yMax;
        this.yMin = d.yMin;
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
    public Grids_Dimensions(BigRational xMin, BigRational xMax, BigRational yMin,
            BigRational yMax, BigRational cellsize) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.cellsize = cellsize;
        width = xMax.subtract(xMin);
        height = yMax.subtract(yMin);
        area = width.multiply(height);
        halfCellsize = cellsize.divide(2);
        cellsizeSquared = cellsize.pow(2);
    }

    /**
     * @return A text description of this.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[xMin=" + getXMin() + ", xMax="
                + getXMax() + ", yMin=" + getYMin() + ", yMax=" + getYMax()
                + ", cellsize=" + getCellsize() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Grids_Dimensions) {
            Grids_Dimensions o2 = (Grids_Dimensions) o;
            if (this.hashCode() == o2.hashCode()) {
                if (this.cellsize.compareTo(o2.cellsize) == 0) {
                    if (this.xMin.compareTo(o2.xMin) == 0) {
                        if (this.xMax.compareTo(o2.xMax) == 0) {
                            if (this.yMin.compareTo(o2.yMin) == 0) {
                                if (this.yMax.compareTo(o2.yMax) == 0) {
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
     * @return The xMin as a BigRational.
     */
    public BigRational getXMin() {
        return xMin;
    }

    /**
     * @return The xMax as a BigRational.
     */
    public BigRational getXMax() {
        return xMax;
    }

    /**
     * @return The yMin as a BigRational.
     */
    public BigRational getYMin() {
        return yMin;
    }

    /**
     * @return The yMax as a BigRational.
     */
    public BigRational getYMax() {
        return yMax;
    }

    /**
     * @return The cellsize as a BigRational.
     */
    public BigRational getCellsize() {
        return cellsize;
    }

    /**
     * @return The halfCellsize as a BigRational.
     */
    public BigRational getHalfCellsize() {
        return halfCellsize;
    }

    /**
     * @return The cellsizeSquared as a BigRational.
     */
    public BigRational getCellsizeSquared() {
        return cellsizeSquared;
    }

    /**
     * @return The width as a BigRational.
     */
    public BigRational getWidth() {
        return width;
    }

    /**
     * @return The height as a BigRational.
     */
    public BigRational getHeight() {
        return height;
    }

    /**
     * @return The area as a BigRational.
     */
    public BigRational getArea() {
        return area;
    }

    /**
     * @param d The dimensions to test for intersection.
     * @return {@code true} if this intersects with {@code d}
     */
    public boolean intersects(Grids_Dimensions d) {
        if (xMin.compareTo(d.xMax) == -1) {
            if (xMax.compareTo(d.xMin) == 1) {
                if (yMin.compareTo(d.yMax) == -1) {
                    if (yMax.compareTo(d.yMin) == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
