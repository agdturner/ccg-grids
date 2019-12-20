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
package io.github.agdturner.grids.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDouble;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.core.Grids_Object;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import io.github.agdturner.grids.process.Grids_Processor;

/**
 * Class for exporting to images.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ImageExporter extends Grids_Object implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * @param gp
     * @param file The File exported to.
     * @param type The name of the type of image to be written e.g. "png",
     * "jpeg"
     */
    public void toGreyScaleImage(Grids_GridNumber g, Grids_Processor gp, 
            Path file, String type)
            throws IOException, ClassNotFoundException, Exception {
        // Initialisation
        env.initNotToCache();
        env.checkAndMaybeFreeMemory();
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
        env.checkAndMaybeFreeMemory();
        // If not already in the range 0 to 255, rescale grid into this range.
        Grids_GridDouble r;
        if (g instanceof Grids_GridDouble) {
            r = gp.rescale((Grids_GridDouble) g, null, 0.0d, 255.0d, env.HOOMET);
        } else {
            r = gp.rescale((Grids_GridInt) g, null, 0.0d, 255.0d, env.HOOMET);
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
        Grids_ChunkDouble chunk;
        int chunkNCols;
        int nChunkRows = r.getNChunkRows();
        int nChunkCols = r.getNChunkCols();
        for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            int chunkNRows = r.getChunkNRows(chunkRow);
            for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                chunkNCols = r.getChunkNCols(chunkCol);
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                env.addToNotToCache(r, chunkID);
                env.checkAndMaybeFreeMemory();
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
                env.removeFromNotToCache(g, chunkID);
                env.checkAndMaybeFreeMemory();
            }
        }
        System.out.println("Number of NoDataValues " + countNoDataValues);
        if (countNoDataValues == ncols * nrows) {
            System.out.println("All values seem to be noDataValues!");
        }
        write((int) ncols, (int) nrows, gridImageArray, type, file, g, env.HOOMET);
    }

    /**
     *
     * @param nCols
     * @param nRows
     * @param gridImageArray
     * @param type
     * @param file
     * @param g Grid not to cache from if possible.
     * @param hoome
     */
    private void write(int nCols, int nRows, int[] gridImageArray,
            String type, Path file, Grids_GridNumber g, boolean hoome)
            throws IOException, ClassNotFoundException, Exception {
        try {
            env.checkAndMaybeFreeMemory();
            write(nCols, nRows, gridImageArray, type, file);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                if (env.cacheChunkExcept_Account(g, env.HOOMEF) < 1) {
                    env.cacheChunks(env.HOOMEF);
                }
                env.initMemoryReserve(g, hoome);
                env.checkAndMaybeFreeMemory();
                write(nCols, nRows, gridImageArray, type, file, g, hoome);
            } else {
                throw e;
            }
        }
    }

    private void write(int nCols, int nRows, int[] gridImageArray,
            String type, Path file) {
        MemoryImageSource mis;
        mis = new MemoryImageSource(nCols, nRows, gridImageArray, 0, nCols);
        Image image = Toolkit.getDefaultToolkit().createImage(mis);
        BufferedImage bi = new BufferedImage(nCols, nRows, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        graphics.drawImage(image, 0, 0, new java.awt.Panel());
        try {
            Files.createDirectories(file.getParent());
            javax.imageio.ImageIO.write(bi, type, file.toFile());
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
            Color noDataValueColour, Grids_GridDouble g, Path file, String type)
            throws IOException, ClassNotFoundException, Exception {
        String methodName = "toColourImage(int,TreeMap<Double,Color>,Color,"
                + "Grids_GridDouble,File,String)";
        env.initNotToCache();
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
        env.checkAndMaybeFreeMemory();
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
        Grids_ChunkDouble chunk;
        int chunkNRows;
        int chunkNCols;
        int nChunkRows = g.getNChunkRows();
        int nChunkCols = g.getNChunkCols();
        for (chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            chunkNRows = g.getChunkNRows(chunkRow);
            for (chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                chunkNCols = g.getChunkNCols(chunkCol);
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                env.addToNotToCache(g, chunkID);
                env.checkAndMaybeFreeMemory();
                chunk = (Grids_ChunkDouble) g.getChunk(chunkID);
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
