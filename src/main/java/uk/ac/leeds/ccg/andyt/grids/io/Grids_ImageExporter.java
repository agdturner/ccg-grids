/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package uk.ac.leeds.ccg.andyt.grids.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 * Class for exporting to images.
 */
//public class Grids_ImageExporter extends ErrorHandler {
public class Grids_ImageExporter extends Grids_Object implements Serializable {

    //private static final long serialVersionUID = 1L;
    protected Grids_ImageExporter() {
    }

    /**
     * Creates a new instance of ImageExporter
     *
     * @param ge
     */
    public Grids_ImageExporter(
            Grids_Environment ge) {
        super(ge);
    }

    /**
     * Writes this grid as a Grey scale image
     *
     * @param g
     * @param processor
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     */
    public void toGreyScaleImage(
            Grids_AbstractGridNumber g,
            Grids_Processor processor,
            File file,
            String type) {
        // Initialisation
        ge.initNotToSwap();
        ge.checkAndMaybeFreeMemory();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
//        System.out.println("nrows" + nrows);
//        System.out.println("ncols" + ncols);        
        // Check int precision OK here.
        if (nrows * ncols > Integer.MAX_VALUE) {
            System.err.println(
                    "Unable to export AbstractGrid2DSquareCell "
                    + g.toString() + " into a "
                    + "single image using "
                    + "toGreyScaleImage(AbstractGrid2DSquareCell,File,"
                    + "String) as _NRows * _Ncols > Integer.MAXVALUE");
            System.err.println(
                    "This method either needs development, or another does "
                    + "which should be called instead of this.");
            System.err.println(
                    "The images could be created in chunks by fixing the number"
                    + " range as parameters passed into the method.");
            return;
        }
        int size = (int) (ncols * nrows);
        long row;
        long col;
//        int iValue = 0;
//        BigDecimal iValueBigDecimal = new BigDecimal("0.0");
//        BigDecimal valueBigDecimal = new BigDecimal("0.0");
//        BigDecimal bigDecimal_255 = new BigDecimal("255.0");
//        int scale = 20;
        int p;
//        int gridImageValue = Integer.MIN_VALUE;
        // Test what writers are available as this may vary on different systems!
        boolean writerAvailable = Grids_IO.isImageWriterAvailable(type);
        if (!writerAvailable) {
            System.out.println(
                    "Unable to export  using toGreyScaleImage("
                    + "AbstractGrid2DSquareCell,File,String) "
                    + "IO.isImageWriterAvailable(" + type + ") is not "
                    + "available.");
            String[] writerTypes = ImageIO.getWriterMIMETypes();
            System.out.println("WriterTypes:");
            for (String writerType : writerTypes) {
                System.out.println(writerType);
            }
            return;
        }
        int[] gridImageArray = initGridImageArray(size);
        ge.checkAndMaybeFreeMemory();
        // If not already in the range 0 to 255, rescale grid into this range.
        Grids_GridDouble r;
        if (g instanceof Grids_GridDouble) {
            r = processor.rescale((Grids_GridDouble) g, null, 0.0d, 255.0d, ge.HOOMET);
        } else {
            r = processor.rescale((Grids_GridInt) g, null, 0.0d, 255.0d, ge.HOOMET);
        }
        double noDataValue = r.getNoDataValue();
//        System.out.println("r nrows " + r.getNRows(hoome));
//        System.out.println("r ncols " + r.getNCols(hoome));
//        System.out.println("r.getCell(0L, 0L) " + r.getCell(0L, 0L, hoome));
//        System.out.println("noDataValue " + noDataValue);
        int countNoDataValues = 0;
        double v;
        int vi;
        Grids_2D_ID_int chunkID;
        Color pixel;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        Grids_AbstractGridChunkDouble chunk;
        int chunkNRows;
        int chunkNCols;
        int nChunkRows = r.getNChunkRows();
        int nChunkCols = r.getNChunkCols();
        for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            chunkNRows = r.getChunkNRows(chunkRow);
            for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                chunkNCols = r.getChunkNCols(chunkCol);
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                ge.addToNotToSwap(r, chunkID);
                ge.checkAndMaybeFreeMemory();
                chunk = r.getChunk(chunkID);
                for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                    row = r.getRow(chunkRow, cellRow);
                    for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                        col = r.getCol(chunkCol, cellCol);
                        v = r.getCell(chunk, cellRow, cellCol);
                        p = (int) ((((nrows - 1) - row) * ncols) + col);
                        //p = (int) ((row * ncols) + col); // Upside down!
                        if (v == noDataValue) {
                            // Set noDataValue as blue
                            pixel = new Color(0, 0, 255);
                            countNoDataValues++;
                        } else {
                            if (v > 255) {
                                v = 255;
                            } else {
                                if (v < 0) {
                                    v = 0;
                                }
                            }
                            vi = (int) v;
                            pixel = new Color(vi, vi, vi);
                        }
                        gridImageArray[p] = pixel.getRGB();
                    }
                }
                ge.removeFromNotToSwap(g, chunkID);
                ge.checkAndMaybeFreeMemory();
            }
        }
        System.out.println("Number of NoDataValues " + countNoDataValues);
        if (countNoDataValues == ncols * nrows) {
            System.out.println("All values seem to be noDataValues!");
        }
        write((int) ncols, (int) nrows, gridImageArray, type, file, g, ge.HOOMET);
    }

    /**
     *
     * @param nCols
     * @param nRows
     * @param gridImageArray
     * @param type
     * @param file
     * @param g Grid not to swap from if possible.
     * @param hoome
     */
    private void write(int nCols, int nRows, int[] gridImageArray,
            String type, File file, Grids_AbstractGridNumber g, boolean hoome) {
        try {
            ge.checkAndMaybeFreeMemory();
            write(nCols, nRows, gridImageArray, type, file);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(g, ge.HOOMEF) < 1) {
                    ge.swapChunks(ge.HOOMEF);
                }
                ge.initMemoryReserve(g, hoome);
                ge.checkAndMaybeFreeMemory();
                write(nCols, nRows, gridImageArray, type, file, g, hoome);
            } else {
                throw e;
            }
        }
    }

    private void write(int nCols, int nRows, int[] gridImageArray,
            String type, File file) {
        MemoryImageSource mis;
        mis = new MemoryImageSource(nCols, nRows, gridImageArray, 0, nCols);
        Image image = Toolkit.getDefaultToolkit().createImage(mis);
        BufferedImage bi;
        bi = new BufferedImage(nCols, nRows, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        graphics.drawImage(image, 0, 0, new java.awt.Panel());
        try {
            file.getParentFile().mkdirs();
            javax.imageio.ImageIO.write(bi, type, file);
        } catch (java.io.IOException e1) {
            e1.printStackTrace(System.err);
            System.out.println("Warning!!! Failed to write grid as " + type
                    + " to File(" + file.toString() + ")");
        }
        graphics.dispose();
        bi.flush();
        image.flush();
    }

    private int[] initGridImageArray(int size) {
        int[] gridImageArray = new int[size];
        Arrays.fill(gridImageArray, 0);
        return gridImageArray;
    }

    /**
     * Writes this grid as a Grey scale image
     *
     * @param duplication This is for resampling, if duplication = 0 then pixels
     * are not duplicated. If duplication = 1 then 4 times as many pixels are
     * written out. If duplication = 2 then 9 times as many pixels are written
     * out.
     * @param colours
     * @param noDataValueColour
     * @param g
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png", *
     * "jpeg"
     */
    public void toColourImage(int duplication, TreeMap<Double, Color> colours,
            Color noDataValueColour, Grids_GridDouble g, File file, String type) {
        String methodName = "toColourImage(int,TreeMap<Double,Color>,Color,"
                + "Grids_GridDouble,File,String)";
        ge.initNotToSwap();
        long nrows = g.getNRows();
        long ncols = g.getNCols();
        // Check int precision OK here.
        if (nrows * ncols * (duplication + 1) * (duplication + 1) > Integer.MAX_VALUE) {
            System.err.println(
                    "Unable to export Grids_GridDouble " + g.toString()
                    + " into a single image using " + methodName
                    + " as (nrows * ncols * (duplication + 1) * (duplication + 1) "
                    + "> Integer.MAX_VALUE)");
            System.err.println(
                    "This method either needs development, or another does "
                    + "which should be called instead of this."
                    + "The images could be created in chunks?");
            return;
        }
        long row;
        long col;
        int duplicationNRows = ((int) nrows * (duplication + 1));
        int duplicationNCols = ((int) ncols * (duplication + 1));
        int duplicationRow;
        int duplicationCol;
        int pixelRGB;
        int size = (int) (duplicationNRows * duplicationNCols);
//        int iValue = 0;
//        BigDecimal iValueBigDecimal = new BigDecimal("0.0");
//        BigDecimal valueBigDecimal = new BigDecimal("0.0");
//        BigDecimal bigDecimal_255 = new BigDecimal("255.0");
//        int scale = 20;
        int p;
//        int gridImageValue = Integer.MIN_VALUE;
        // Test what writers are available as this may vary on different systems!
        boolean writerAvailable = Grids_IO.isImageWriterAvailable(type);
        if (!writerAvailable) {
            System.out.println(
                    "Unable to export  using " + methodName
                    + "Grids_IO.isImageWriterAvailable(" + type + ") is not "
                    + "available.");
            String[] writerTypes = ImageIO.getWriterMIMETypes();
            System.out.println("WriterTypes:");
            for (String writerType : writerTypes) {
                System.out.println(writerType);
            }
            return;
        }
        int[] gridImageArray = initGridImageArray(size);
        ge.checkAndMaybeFreeMemory();
        // If not already in the range 0 to 255, rescale grid into this range.
        double noDataValue = g.getNoDataValue();
        int countNoDataValues = 0;
        Grids_2D_ID_int chunkID;
        Color pixel;
        double v;
        int chunkRow;
        int chunkCol;
        int cellRow;
        int cellCol;
        Grids_AbstractGridChunkDouble chunk;
        int chunkNRows;
        int chunkNCols;
        int nChunkRows = g.getNChunkRows();
        int nChunkCols = g.getNChunkCols();
        for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            chunkNRows = g.getChunkNRows(chunkRow);
            for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                chunkNCols = g.getChunkNCols(chunkCol);
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                ge.addToNotToSwap(g, chunkID);
                ge.checkAndMaybeFreeMemory();
                chunk = (Grids_AbstractGridChunkDouble) g.getChunk(chunkID);
                for (cellRow = 0; cellRow < chunkNRows; cellRow++) {
                    row = g.getRow(chunkRow, cellRow);
                    for (cellCol = 0; cellCol < chunkNCols; cellCol++) {
                        col = g.getCol(chunkCol, cellCol);
                        v = g.getCell(chunk, cellRow, cellCol);
                        p = (int) ((((nrows - 1) - row) * ncols) + col);
                        //p = (int) ((row * ncols) + col); // Upside down!
                        if (v == noDataValue) {
                            pixel = noDataValueColour;
                        } else {
                            if (Double.isFinite(v)) {
                                pixel = getColor(v, colours);
                            } else {
                                pixel = noDataValueColour;
                            }
                        }
                        pixelRGB = pixel.getRGB();
                        if (duplication == 0) {
                            gridImageArray[p] = pixelRGB;
                        } else {
                            for (int i = 0; i <= duplication; i++) {
                                duplicationRow = (duplicationNRows - 1) - (((int) row * (duplication + 1)) + i);
                                for (int j = 0; j <= duplication; j++) {
                                    duplicationCol = (int) (col * (duplication + 1)) + j;
                                    p = (int) ((duplicationRow * duplicationNCols) + duplicationCol);
                                    //pos = (int) ((((nrows - 1) - row + i) * ncols) + col + j);
                                    gridImageArray[p] = pixel.getRGB();
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Number of NoDataValues " + countNoDataValues);
        if (countNoDataValues == ncols * nrows) {
            System.out.println("All values seem to be noDataValues!");
        }
        write(duplicationNCols, duplicationNRows, gridImageArray, type, file);
    }

    Color getColor(double value, TreeMap<Double, Color> colors) {
        Iterator<Double> ite;
        ite = colors.keySet().iterator();
        Double v2 = null;
        while (ite.hasNext()) {
            v2 = ite.next();
            if (value <= v2) {
                return colors.get(v2);
            }
        }
        return colors.get(v2);
    }
}
