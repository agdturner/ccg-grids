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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * For storing and testing the dimensions of a grid.
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
     * The cellsize (width or height of a cell). This would be better stored not
     * as a single BigDecimal Number, but as a fraction or rational number in
     * two parts - a numerator and a denominator. As sometimes a user may want
     * to create a disaggregated grid with a cellsize of a third or other non
     * even factor of the cellsize and this may have a consequence of needing to
     * round the resulting cellsize. Really, the cellsize should be stored
     * accurately and so in a future version it will be stored as a pair of
     * BigInteger numbers.
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
