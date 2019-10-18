/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.examples;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * TODO: docs
 */
public class Grids_GenerateTestData extends Grids_Processor implements Runnable {

    long time0;

    /**
     * @throws IOException
     */
    protected Grids_GenerateTestData() throws IOException {
        super();
    }

    public Grids_GenerateTestData(Grids_Environment ge) throws IOException {
        super(ge);
    }

    public static void main(String[] args) {
        try {
            Grids_Environment ge = new Grids_Environment(new Generic_Environment());
            Grids_GenerateTestData p = new Grids_GenerateTestData(ge);
            p.time0 = System.currentTimeMillis();
            p.run();
        } catch (IOException | Error e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Initialising...");
            boolean hoome = true;
            File outDir = files.getOutputDir();
//        Grids_GridDouble[] testData = generateCatchment(outDir, hoome);
//        Grids_GridDouble[] testData = generateSquareData(outDir, hoome);
            Grids_GridDouble[] testData = generateCircularData(outDir, hoome);
            File file;
            for (int i = 0; i < testData.length; i++) {
                System.out.println(testData[i].toString());
                file = new File(outDir, testData[i].getName() + ".asc");
                new Grids_ESRIAsciiGridExporter(env).toAsciiFile(testData[i], file);
                file = new File(outDir, testData[i].getName() + ".png");
                new Grids_ImageExporter(env).toGreyScaleImage(testData[i], this, file, "png");
            }
            System.out.println("Processing complete in " + Grids_Utilities.getTime(System.currentTimeMillis() - time0));
        } catch (IOException | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public Grids_GridDouble[] generateCircularData(File outDir, boolean hoome) throws IOException {
        File outdir2 = new File(outDir, "CircularData");
        outdir2.mkdirs();
        File f;
        f = new File(outdir2, "grids.txt");
        PrintWriter pw = env.env.io.getPrintWriter(f, false);
        //         minRadius  maxRadius  elevation             grids
        //circle1          0          5         -1  1,3,(5-4)
        //circle2          5          6          1  2,4,(6-4),7,8,(9-3)
        //circle3          0         20         -2  3,8
        //circle4         15         19          1  4,7,(9,2)
        //
        // Notes:
        // (5-4) means 4 of these features in grid5
        // For Grid 7 guarantee small in large
        // For Grid 9 guarantee overlapping and intersecting features
        // Grid 10 anything I like
        int ngrids = 10;
        int nrows = 100;
        int ncols = 100;
        Grids_GridDouble[] grids = new Grids_GridDouble[ngrids];
        File dir;
        for (int i = 0; i < ngrids; i++) {
            dir = env.env.io.createNewFile(files.getGeneratedGridDoubleDir());
            grids[i] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows, ncols);
            addToGrid(grids[i], 0.0d);
            grids[i].setName("Grid" + i);
        }

        // grid 1
        System.out.println("grid 1 (randomly positioned)");
        pw.println("grid 1 (randomly positioned)");
        double minRadius = 0.0d;
        double maxRadius = 5.0d;
        long row = getRandomRow(nrows, maxRadius);
        long col = getRandomCol(ncols, maxRadius);
        HashSet cellIDs = getCellIDs(grids[0], row, col, minRadius, maxRadius, hoome);
        double height = -1.0d;
        addToGrid(grids[0], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 2
        System.out.println("grid 2 (randomly positioned)");
        pw.println("grid 2 (randomly positioned)");
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[1], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[1], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 3
        System.out.println("grid 3 (randomly positioned)");
        pw.println("grid 3 (randomly positioned)");
        minRadius = 0.0d;
        maxRadius = 5.0d;
        height = -1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[2], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[2], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 0.0d;
        maxRadius = 20.0d;
        height = -2.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[2], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[2], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 4
        System.out.println("grid 4 (randomly positioned)");
        pw.println("grid 4 (randomly positioned)");
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[3], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[3], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 15.0d;
        maxRadius = 19.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[3], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[3], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 5
        System.out.println("grid 5 (randomly positioned)");
        pw.println("grid 5 (randomly positioned)");
        minRadius = 0.0d;
        maxRadius = 5.0d;
        height = -1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[4], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[4], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[4], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[4], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[4], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[4], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[4], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[4], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 6
        System.out.println("grid 6 (randomly positioned)");
        pw.println("grid 6 (randomly positioned)");
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[5], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[5], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[5], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[5], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[5], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[5], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[5], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[5], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 7
        System.out.println("grid 7 (small guaranteed to be in large)");
        pw.println("grid 7 (small guaranteed to be in large)");
        minRadius = 15.0d;
        maxRadius = 19.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[6], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[6], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 5.0d;
        maxRadius = 6.0d;
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[6], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[6], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 8
        System.out.println("grid 8 (small guaranteed to be in large)");
        pw.println("grid 8 (small guaranteed to be in large)");
        minRadius = 0.0d;
        maxRadius = 20.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius);
        col = getRandomCol(ncols, maxRadius);
        cellIDs = getCellIDs(grids[7], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[7], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 5.0d;
        maxRadius = 6.0d;
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[7], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[7], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 9
        System.out.println("grid 9 (randomly positioned but likely to intersect overlap)");
        pw.println("grid 9 (randomly positioned but likely to intersect overlap)");
        minRadius = 15.0d;
        maxRadius = 19.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius + 20);
        col = getRandomCol(ncols, maxRadius + 20);
        cellIDs = getCellIDs(grids[8], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[8], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[8], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[8], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[8], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[8], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[8], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[8], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[8], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[8], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        // grid 10
        System.out.println("grid 10 (randomly positioned but likely to intersect overlap)");
        pw.println("grid 10 (randomly positioned but likely to intersect overlap and on a slope)");
//        Grid2DSquareCellDoubleIterator grid2DSquareCellDoubleIterator = ( Grid2DSquareCellDoubleIterator ) grids[ 9 ].iterator();
//        for ( row = 0L; row < nrows; row ++ ) {
//            for ( col = 0L; col < ncols; col ++ ) {
//                grids[ 9 ].addToCell( row, col, col, _HandleOutOfMemoryError );
//            }
//        }
        minRadius = 15.0d;
        maxRadius = 19.0d;
        height = 1.0d;
        row = getRandomRow(nrows, maxRadius + 20);
        col = getRandomCol(ncols, maxRadius + 20);
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 3.0d;
        maxRadius = 6.0d;
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 5.0d;
        maxRadius = 10.0d;
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = 8.0d;
        maxRadius = 11.0d;
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = random(-10.0d, 10.0d);
        maxRadius = minRadius * random(1.0d,
                10.0d);
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = random(-10.0d, 10.0d);
        maxRadius = minRadius * random(1.0d, 10.0d);
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = random(-10.0d, 10.0d);
        maxRadius = minRadius * random(1.0d, 10.0d);
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        minRadius = random(-10.0d, 10.0d);
        maxRadius = minRadius * random(1.0d, 10.0d);
        height = random(-10.0d, 10.0d);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        row = (long) Math.ceil(row + random(-6.0d, 5.0d));
        col = (long) Math.ceil(col + random(-6.0d, 5.0d));
        cellIDs = getCellIDs(grids[9], row, col, minRadius, maxRadius, hoome);
        addToGrid(grids[9], cellIDs, height, hoome);
        minRadius = random(-10.0d, 10.0d);
        maxRadius = minRadius * random(1.0d, 10.0d);
        height = random(-10.0d, 10.0d);
        printCircularFeatureInfo(pw, minRadius, maxRadius, height, row, col);
        pw.println();

        pw.flush();
        pw.close();

        return grids;
    }

    public void printCircularFeatureInfo(PrintWriter pw, double minRadius, double maxRadius, double height, long row, long col) {
        System.out.println("minRadius " + minRadius + ", maxRadius " + maxRadius + ", height " + height + ", cellAtCentre ( " + row + ", " + col + " )");
        pw.println("minRadius " + minRadius + ", maxRadius " + maxRadius + ", height " + height + ", cellAtCentre ( " + row + ", " + col + " )");
    }

    public HashSet getCellIDs(Grids_GridDouble grid, long row, long col, double minRadius, double maxRadius, boolean _HandleOutOfMemoryError) {
        HashSet cellIDsHashSet = getCellIDsHashSet(grid, row, col, maxRadius, _HandleOutOfMemoryError);
        //cellIDsHashSet.removeAll( getCellIDsHashSet( grid, row, col, minRadius ) );
        if (minRadius > 0.0d) {
            HashSet cellIDHashSetToRemove = getCellIDsHashSet(grid, row, col, minRadius, _HandleOutOfMemoryError);
            //cellIDsHashSet.removeAll( cellIDHashSetToRemove );
            removeAll(cellIDsHashSet, cellIDHashSetToRemove);
            //            Iterator cellIDsHashSetRemoveIterator = cellIDsHashSetRemove.iterator();
            //            while ( cellIDsHashSetRemoveIterator.hasNext() ) {
            //                CellID cellID = ( CellID ) cellIDsHashSetRemoveIterator.next();
            //                boolean check = cellIDsHashSet.remove( cellID );
            //                int i = 0;
            //            }
            //            boolean check = cellIDsHashSet.removeAll( cellIDsHashSetRemove );
        }
        return cellIDsHashSet;
    }

    /**
     * Taken from HashSet.removeAll(Collection)
     *
     * @param cellIDHashSetToRemoveFrom
     * @param cellIDHashSetToRemove
     */
    public void removeAll(HashSet cellIDHashSetToRemoveFrom, HashSet cellIDHashSetToRemove) {
        boolean modified = false;
        Grids_2D_ID_long cellIDToRemove;
        Grids_2D_ID_long cellIDToTestForRemoval;
        for (Iterator iteratorToRemove = cellIDHashSetToRemove.iterator(); iteratorToRemove.hasNext();) {
            cellIDToRemove = (Grids_2D_ID_long) iteratorToRemove.next();
            for (Iterator iteratorRemoveFrom = cellIDHashSetToRemoveFrom.iterator(); iteratorRemoveFrom.hasNext();) {
                cellIDToTestForRemoval = (Grids_2D_ID_long) iteratorRemoveFrom.next();
                if (cellIDToRemove.equals(cellIDToTestForRemoval)) {
                    cellIDHashSetToRemoveFrom.remove(cellIDToTestForRemoval);
                    break;
                }
            }
        }
    }

    public HashSet getCellIDsHashSet(Grids_GridDouble grid, long row, long col, double radius, boolean _HandleOutOfMemoryError) {
        Grids_2D_ID_long[] cellIDs = grid.getCellIDs(row, col, radius);
        HashSet cellIDsHashSet = new HashSet();
        for (int cellIDIndex = 0; cellIDIndex < cellIDs.length; cellIDIndex++) {
            cellIDsHashSet.add(cellIDs[cellIDIndex]);
        }
        return cellIDsHashSet;
    }

    public long getRandomRow(long nrows, double maxRadius) {
        return (long) Math.floor(((Math.random() * (nrows - (2.0d * maxRadius))) + maxRadius));
    }

    public long getRandomCol(long ncols, double maxRadius) {
        return (long) Math.floor(((Math.random() * (ncols - (2.0d * maxRadius))) + maxRadius));
    }

    public double random(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    public Grids_GridDouble[] generateSquareData(boolean handleOutOfMemoryError) throws IOException {
        int ngrids = 5;
        int nrows = 100;
        int ncols = 100;
        Grids_GridDoubleFactory factory;
//        factory = new Grids_GridDoubleFactory(
//                    env,
//                Directory, GridChunkDoubleFactory, DefaultGridChunkDoubleFactory, nrows, ncols)
//                    nrows,
//                    ncols)
        Grids_GridDouble[] grids = new Grids_GridDouble[ngrids];
        File dir;
        for (int i = 0; i < ngrids; i++) {
            dir = env.env.io.createNewFile(files.getGeneratedGridDoubleDir());
            grids[i] = (Grids_GridDouble) GridDoubleFactory.create(dir,
                    nrows, ncols);
        }
        // grids[ 0 ]
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                grids[0].setCell(i, j, Math.random());
            }
        }
        // grids[ 1 ] should show some +ve correlation with grids[ 0 ] for large enough nrows and ncols
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                grids[1].setCell(i, j, grids[0].getCell(i, j) + Math.random());
            }
        }
        // grids[ 2 ] should be highly +vely correlated with grids[ 0 ]
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                grids[2].setCell(i, j, (10.0d * grids[0].getCell(i, j)) + Math.random());
            }
        }
        // grids[ 3 ] should show some -ve correlation with grids[ 0 ] for large enough nrows and ncols
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                grids[3].setCell(i, j, Math.random() - grids[0].getCell(i, j));
            }
        }
        // grids[ 4 ] should be highly -vely correlated with grids[ 0 ]
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                grids[4].setCell(i, j, Math.random() - (10.0d * grids[0].getCell(i, j)));
            }
        }
        return grids;
    }

    public Grids_GridDouble[] generateCatchment(boolean hoome) throws IOException {
        int nrows = 100;
        int ncols = 100;
        File dir = env.env.io.createNewFile(files.getGeneratedGridDoubleDir());
        Grids_GridDouble[] catchment = new Grids_GridDouble[1];
        catchment[0] = (Grids_GridDouble) GridDoubleFactory.create(dir, nrows, ncols);
        //catchment[0].setNoDataValue( -9999.0d );
        for (int iterations = 0; iterations < 100; iterations++) {
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    catchment[0].addToCell(row, col, Math.pow(Math.random() * (Math.abs(row - (nrows / 2.0d)) + 5.0d), 0.125d));
                    catchment[0].addToCell(row, col, Math.pow(Math.random() * ((col / 2.0d) + 5.0d), 0.125d));
                    //catchment[0].addToCell( row, col, ( Math.pow( Math.random() * ( Math.abs( row - ( nrows / 2.0d ) ) + 50.0d ), 0.125d ) ) * ( Math.pow( Math.random() * col, 0.125d ) ) );
                }
            }
        }
        // Mask
        double noDataValue = catchment[0].getNoDataValue();
        double centreX = catchment[0].getCellXDouble(49);
        double centreY = catchment[0].getCellYDouble(49);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (Grids_Utilities.distance(catchment[0].getCellXDouble(col), catchment[0].getCellYDouble(row), centreX, centreY) >= 50.0d) {
                    catchment[0].setCell(row, col, noDataValue);
                }
            }
        }
        catchment[0].setName("catchment1");
        return catchment;
    }
}
