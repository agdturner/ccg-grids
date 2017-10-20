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

    private BigDecimal XMin;
    private BigDecimal XMax;
    private BigDecimal YMin;
    private BigDecimal YMax;
    private BigDecimal Cellsize;
    private BigDecimal HalfCellsize;
    private BigDecimal Width;
    private BigDecimal Height;
    private BigDecimal Area;
    /**
     * Stores the minimum number of decimal places used to store Dimensions.
     */
    private int DimensionsScale;
    
    protected Grids_Dimensions() {
    }
    
    /**
     * DimensionsScale will default to the maximum scale in any of the BigDecimal inputs. 
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
            BigDecimal cellsize){
        this.XMax = xMax;
        this.XMin = xMin;
        this.YMax = yMax;
        this.YMin = yMin;
        this.Cellsize = cellsize;
        this.DimensionsScale = getScale(xMin, xMax, yMin, yMax, cellsize);
    }
    
    private int getScale(
            BigDecimal xMin,
            BigDecimal xMax,
            BigDecimal yMin,
            BigDecimal yMax,
            BigDecimal cellsize) {
        int scale;
        scale = cellsize.scale();
        scale = Math.max(scale, xMin.scale());
        scale = Math.max(scale, yMin.scale());
        scale = Math.max(scale, xMax.scale());
        scale = Math.max(scale, yMax.scale());
        return scale;
    }
    
    /**
     * @param xMin The minimum x coordinate.
     * @param xMax The maximum x coordinate.
     * @param yMin The minimum y coordinate.
     * @param yMax The maximum y coordinate.
     * @param cellsize The cellsize. 
     * @param dimensionsScale The maximum number of decimal places used to store XMin, XMax, YMin, YMax.
     */
    
    public Grids_Dimensions(
            BigDecimal xMin,
            BigDecimal xMax,
            BigDecimal yMin,
            BigDecimal yMax,
            BigDecimal cellsize,
            int dimensionsScale) {
        this.XMax = xMax;
        this.XMin = xMin;
        this.YMax = yMax;
        this.YMin = yMin;
        this.DimensionsScale = dimensionsScale;
    }
    
    @Override
    public String toString(){
        String result;
        result = "Dimensions(XMin " + XMin + ", "
                + "XMax " + XMax + ", "
                + "YMin " + YMin +  ", "
                + "YMax " + YMax +  ", "
                + "Cellsize " + Cellsize + ", "
                + "DimensionsScale " + DimensionsScale + ")";
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

    /**
     * @return the DimensionsScale
     */
    public int getDimensionsScale() {
        return DimensionsScale;
    }
    
    public BigDecimal getWidth() {
        if (Width == null) {
            Width = XMax.subtract(XMin);
        }
        return Width;
    }
    
    public BigDecimal getHeight() {
        if (Height == null) {
            Height = YMax.subtract(YMin);
        }
        return Height;
    }
    
    public BigDecimal getArea() {
        if (Area == null) {
            Area = getWidth().multiply(getHeight());
        }
        return Area;
    }
    
    public BigDecimal getHalfCellsize() {
        if (HalfCellsize == null) {
            HalfCellsize = Cellsize.divide(BigDecimal.valueOf(2L));
        }
        return HalfCellsize;
    }

}
