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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell.CellID;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * TODO:
 * docs
 */
public class Grids_GenerateTestData extends Grid2DSquareCellProcessor implements Runnable {

        Grids_Environment _Grids_Environment;

    File testDataDirectory;
    long time0;
    
    /** Creates new GenerateTestData.
     * @throws java.io.IOException */
    public Grids_GenerateTestData() throws IOException {
        super();
    }
    
    /** Creates new GenerateTestData.
     * @param workspace
     * @throws java.io.IOException */
    public Grids_GenerateTestData( File workspace ) throws IOException {
        super( workspace );
    }
    
    
    public static void main(String[] args) throws java.io.IOException {
        File file = new File( "C:/work/src/andyt/java/grids/" );
        //File file = new File( "C:/tmp/data/" );
        Grids_GenerateTestData gtd = new Grids_GenerateTestData( file );
        gtd.time0 = System.currentTimeMillis();
        gtd.run();
    }
    
    @Override
    public void run() {
        System.out.println( "Initialising..." );
        boolean _HandleOutOfMemoryError = true;
        testDataDirectory = this.get_Directory( _HandleOutOfMemoryError );
        //Grid2DSquareCellDouble[] testData = generateCatchment( _HandleOutOfMemoryError );
        //Grid2DSquareCellDouble[] testData = generateSquareData( _HandleOutOfMemoryError );
        Grids_Grid2DSquareCellDouble[] testData = generateCircularData( _HandleOutOfMemoryError );
        File file;
        for ( int i = 0 ; i < testData.length ; i ++ ) {
            System.out.println( testData[i].toString() );
            file = new File( testDataDirectory, testData[i].get_Name( _HandleOutOfMemoryError ) + ".asc" );
            new Grids_ESRIAsciiGridExporter(env).toAsciiFile( testData[i], file, _HandleOutOfMemoryError );
            file = new File( testDataDirectory, testData[i].get_Name( _HandleOutOfMemoryError ) + ".png" );
            new Grids_ImageExporter(_Grids_Environment).toGreyScaleImage( testData[i], this, file, "png", _HandleOutOfMemoryError );
        }
        System.out.println("Processing complete in " + Grids_Utilities._ReportTime( System.currentTimeMillis() - time0 ) );
    }
    
    public Grids_Grid2DSquareCellDouble[] generateCircularData(
            boolean _HandleOutOfMemoryError ) {
        
        PrintWriter pw = null;
        try {
            pw = new PrintWriter( new FileOutputStream( new File( testDataDirectory, "grids.txt" ) ) );
        } catch ( FileNotFoundException fnfe0 ) {
            fnfe0.printStackTrace();
        }
        
        //         minRadius  maxRadius  elevation             Grids
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
       Grids_Grid2DSquareCellDouble[] grids = new Grids_Grid2DSquareCellDouble[ ngrids ];
        for ( int i = 0 ; i < ngrids ; i ++ ) {
            grids[ i ] = ( Grids_Grid2DSquareCellDouble ) new Grids_Grid2DSquareCellDoubleFactory(_Grids_Environment, _HandleOutOfMemoryError ).create( nrows, ncols );
            addToGrid( grids[ i ], 0.0d, _HandleOutOfMemoryError );
            grids[ i ].set_Name( "Grid" + i, _HandleOutOfMemoryError );
        }
        
        // grid 1
        System.out.println( "grid 1 (randomly positioned)" );
        pw.println( "grid 1 (randomly positioned)" );
        double minRadius = 0.0d;
        double maxRadius = 5.0d;
        long row = getRandomRow( nrows, maxRadius );
        long col = getRandomCol( ncols, maxRadius );
        HashSet cellIDs = getCellIDs( grids[ 0 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        double height = -1.0d;
        addToGrid( grids[ 0 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 2
        System.out.println( "grid 2 (randomly positioned)" );
        pw.println( "grid 2 (randomly positioned)" );
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 1 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 1 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 3
        System.out.println( "grid 3 (randomly positioned)" );
        pw.println( "grid 3 (randomly positioned)" );
        minRadius = 0.0d;
        maxRadius = 5.0d;
        height = -1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 2 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 2 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 0.0d;
        maxRadius = 20.0d;
        height = -2.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 2 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 2 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 4
        System.out.println( "grid 4 (randomly positioned)" );
        pw.println( "grid 4 (randomly positioned)" );
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 3 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 3 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 15.0d;
        maxRadius = 19.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 3 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 3 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 5
        System.out.println( "grid 5 (randomly positioned)" );
        pw.println( "grid 5 (randomly positioned)" );
        minRadius = 0.0d;
        maxRadius = 5.0d;
        height = -1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 4 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 4 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 4 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 4 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 4 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 4 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 4 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 4 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 6
        System.out.println( "grid 6 (randomly positioned)" );
        pw.println( "grid 6 (randomly positioned)" );
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 5 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 5 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 5 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 5 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 5 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 5 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 5 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 5 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 7
        System.out.println( "grid 7 (small guaranteed to be in large)" );
        pw.println( "grid 7 (small guaranteed to be in large)" );
        minRadius = 15.0d;
        maxRadius = 19.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 6 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 6 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 5.0d;
        maxRadius = 6.0d;
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 6 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 6 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 8
        System.out.println("grid 8 (small guaranteed to be in large)" );
        pw.println( "grid 8 (small guaranteed to be in large)" );
        minRadius = 0.0d;
        maxRadius = 20.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius );
        col = getRandomCol( ncols, maxRadius );
        cellIDs = getCellIDs( grids[ 7 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 7 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 5.0d;
        maxRadius = 6.0d;
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 7 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 7 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 9
        System.out.println("grid 9 (randomly positioned but likely to intersect overlap)" );
        pw.println( "grid 9 (randomly positioned but likely to intersect overlap)" );
        minRadius = 15.0d;
        maxRadius = 19.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius + 20 );
        col = getRandomCol( ncols, maxRadius + 20 );
        cellIDs = getCellIDs( grids[ 8 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 8 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 8 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 8 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 5.0d;
        maxRadius = 6.0d;
        height = 1.0d;
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 8 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 8 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 8 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 8 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 8 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 8 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        // grid 10
        System.out.println( "grid 10 (randomly positioned but likely to intersect overlap)" );
        pw.println( "grid 10 (randomly positioned but likely to intersect overlap and on a slope)" );
//        Grid2DSquareCellDoubleIterator grid2DSquareCellDoubleIterator = ( Grid2DSquareCellDoubleIterator ) grids[ 9 ].iterator();
//        for ( row = 0L; row < nrows; row ++ ) {
//            for ( col = 0L; col < ncols; col ++ ) {
//                grids[ 9 ].addToCell( row, col, col, _HandleOutOfMemoryError );
//            }
//        }
        minRadius = 15.0d;
        maxRadius = 19.0d;
        height = 1.0d;
        row = getRandomRow( nrows, maxRadius + 20 );
        col = getRandomCol( ncols, maxRadius + 20 );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 3.0d;
        maxRadius = 6.0d;
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d  ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 5.0d;
        maxRadius = 10.0d;
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d  ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d  ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = 8.0d;
        maxRadius = 11.0d;
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = random( -10.0d, 10.0d );
        maxRadius = minRadius * random( 1.0d,
        10.0d );
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = random( -10.0d, 10.0d );
        maxRadius = minRadius * random( 1.0d, 10.0d );
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = random( -10.0d, 10.0d );
        maxRadius = minRadius * random( 1.0d, 10.0d );
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        minRadius = random( -10.0d, 10.0d );
        maxRadius = minRadius * random( 1.0d, 10.0d );
        height = random( -10.0d, 10.0d );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        row = ( long ) Math.ceil( row + random( -6.0d, 5.0d ) );
        col = ( long ) Math.ceil( col + random( -6.0d, 5.0d ) );
        cellIDs = getCellIDs( grids[ 9 ], row, col, minRadius, maxRadius, _HandleOutOfMemoryError );
        addToGrid( grids[ 9 ], cellIDs, height, _HandleOutOfMemoryError );
        minRadius = random( -10.0d, 10.0d );
        maxRadius = minRadius * random( 1.0d, 10.0d );
        height = random( -10.0d, 10.0d );
        printCircularFeatureInfo( pw, minRadius, maxRadius, height, row, col );
        pw.println();
        
        pw.flush();
        pw.close();
        
        return grids;
    }
    
    public void printCircularFeatureInfo( PrintWriter pw, double minRadius, double maxRadius, double height, long row, long col ) {
        System.out.println( "minRadius " + minRadius + ", maxRadius " + maxRadius + ", height " + height + ", cellAtCentre ( " + row + ", " + col + " )" );
        pw.println( "minRadius " + minRadius + ", maxRadius " + maxRadius + ", height " + height + ", cellAtCentre ( " + row + ", " + col + " )" );
    }
    
    public HashSet getCellIDs( Grids_Grid2DSquareCellDouble grid, long row, long col, double minRadius, double maxRadius, boolean _HandleOutOfMemoryError ) {
        HashSet cellIDsHashSet = getCellIDsHashSet( grid, row, col, maxRadius, _HandleOutOfMemoryError );
        //cellIDsHashSet.removeAll( getCellIDsHashSet( grid, row, col, minRadius ) );
        if ( minRadius > 0.0d ) {
            HashSet cellIDHashSetToRemove = getCellIDsHashSet( grid, row, col, minRadius, _HandleOutOfMemoryError );
            //cellIDsHashSet.removeAll( cellIDHashSetToRemove );
            removeAll( cellIDsHashSet, cellIDHashSetToRemove );
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
     * Taken from  HashSet.removeAll(Collection)
     * @param cellIDHashSetToRemoveFrom
     * @param cellIDHashSetToRemove
     */
    public void removeAll( HashSet cellIDHashSetToRemoveFrom, HashSet cellIDHashSetToRemove ) {
        boolean modified = false;
        CellID cellIDToRemove;
        CellID cellIDToTestForRemoval;
        for ( Iterator iteratorToRemove = cellIDHashSetToRemove.iterator(); iteratorToRemove.hasNext(); ) {
            cellIDToRemove = ( CellID ) iteratorToRemove.next();
            for ( Iterator iteratorRemoveFrom = cellIDHashSetToRemoveFrom.iterator(); iteratorRemoveFrom.hasNext(); ) {
                cellIDToTestForRemoval = ( CellID ) iteratorRemoveFrom.next();
                if ( cellIDToRemove.equals( cellIDToTestForRemoval ) ) {
                    cellIDHashSetToRemoveFrom.remove( cellIDToTestForRemoval );
                    break;
                }
            }
        }
    }
    
    
    public HashSet getCellIDsHashSet( Grids_Grid2DSquareCellDouble grid, long row, long col, double radius, boolean _HandleOutOfMemoryError ) {
        CellID[] cellIDs = grid.getCellIDs( row, col, radius, _HandleOutOfMemoryError );
        HashSet cellIDsHashSet = new HashSet();
        for ( int cellIDIndex = 0; cellIDIndex < cellIDs.length; cellIDIndex ++ ) {
            cellIDsHashSet.add( cellIDs[ cellIDIndex ] );
        }
        return cellIDsHashSet;
    }
    
    public long getRandomRow( long nrows, double maxRadius ) {
        return  ( long ) Math.floor( ( ( Math.random() * ( nrows - ( 2.0d * maxRadius ) ) ) + maxRadius ) );
    }
    
    public long getRandomCol( long ncols, double maxRadius ) {
        return  ( long ) Math.floor( ( ( Math.random() * ( ncols - ( 2.0d * maxRadius ) ) ) + maxRadius ) );
    }
    
    public double random( double min, double max ) {
        return ( Math.random() * ( max - min ) ) + min;
    }
    
    public Grids_Grid2DSquareCellDouble[] generateSquareData( boolean _HandleOutOfMemoryError) {
        int ngrids = 5;
        int nrows = 100;
        int ncols = 100;
        Grids_Grid2DSquareCellDouble[] grids = new Grids_Grid2DSquareCellDouble[ ngrids ];
        for ( int i = 0 ; i < ngrids ; i ++ ) {
            grids[ i ] = ( Grids_Grid2DSquareCellDouble ) new Grids_Grid2DSquareCellDoubleFactory( _Grids_Environment, _HandleOutOfMemoryError ).create( nrows, ncols );
        }
        // grids[ 0 ]
        for ( int i = 0; i < nrows; i ++ ) {
            for ( int j = 0; j < ncols; j ++ ) {
                grids[ 0 ].setCell( i, j, Math.random(), _HandleOutOfMemoryError );
            }
        }
        // grids[ 1 ] should show some +ve correlation with grids[ 0 ] for large enough nrows and ncols
        for ( int i = 0; i < nrows; i ++ ) {
            for ( int j = 0; j < ncols; j ++ ) {
                grids[ 1 ].setCell( i, j, grids[ 0 ].getCell( i, j, _HandleOutOfMemoryError ) + Math.random(), _HandleOutOfMemoryError );
            }
        }
        // grids[ 2 ] should be highly +vely correlated with grids[ 0 ]
        for ( int i = 0; i < nrows; i ++ ) {
            for ( int j = 0; j < ncols; j ++ ) {
                grids[ 2 ].setCell( i, j, ( 10.0d * grids[ 0 ].getCell( i, j, _HandleOutOfMemoryError ) ) + Math.random(), _HandleOutOfMemoryError );
            }
        }
        // grids[ 3 ] should show some -ve correlation with grids[ 0 ] for large enough nrows and ncols
        for ( int i = 0; i < nrows; i ++ ) {
            for ( int j = 0; j < ncols; j ++ ) {
                grids[ 3 ].setCell( i, j, Math.random() - grids[ 0 ].getCell( i, j, _HandleOutOfMemoryError ), _HandleOutOfMemoryError );
            }
        }
        // grids[ 4 ] should be highly -vely correlated with grids[ 0 ]
        for ( int i = 0; i < nrows; i ++ ) {
            for ( int j = 0; j < ncols; j ++ ) {
                grids[ 4 ].setCell( i, j, Math.random() - ( 10.0d * grids[ 0 ].getCell( i, j, _HandleOutOfMemoryError ) ), _HandleOutOfMemoryError );
            }
        }
        return grids;
    }
    
    public Grids_Grid2DSquareCellDouble[] generateCatchment( boolean _HandleOutOfMemoryError ) {
        int nrows = 100;
        int ncols = 100;
        Grids_Grid2DSquareCellDouble[] catchment = new Grids_Grid2DSquareCellDouble[1];
        catchment[0] = ( Grids_Grid2DSquareCellDouble ) new Grids_Grid2DSquareCellDoubleFactory(_Grids_Environment
                , _HandleOutOfMemoryError ).create( nrows, ncols );
        //catchment[0].setNoDataValue( -9999.0d );
        for ( int iterations = 0; iterations < 100; iterations ++ ) {
            for ( int row = 0; row < nrows; row ++ ) {
                for ( int col = 0; col < ncols; col ++ ) {
                    catchment[0].addToCell( row, col, Math.pow( Math.random() * ( Math.abs( row - ( nrows / 2.0d ) ) + 5.0d ), 0.125d ), _HandleOutOfMemoryError );
                    catchment[0].addToCell( row, col, Math.pow( Math.random() * ( ( col / 2.0d ) + 5.0d ), 0.125d ), _HandleOutOfMemoryError );
                    //catchment[0].addToCell( row, col, ( Math.pow( Math.random() * ( Math.abs( row - ( nrows / 2.0d ) ) + 50.0d ), 0.125d ) ) * ( Math.pow( Math.random() * col, 0.125d ) ) );
                }
            }
        }
        // Mask
        double noDataValue = catchment[0].get_NoDataValue(_HandleOutOfMemoryError);
        double centreX = catchment[0].getCellXDouble( 49, _HandleOutOfMemoryError );
        double centreY = catchment[0].getCellYDouble( 49, _HandleOutOfMemoryError );
        for ( int row = 0; row < nrows; row ++ ) {
            for ( int col = 0; col < ncols; col ++ ) {
                if ( Grids_Utilities.distance( catchment[0].getCellXDouble( col, _HandleOutOfMemoryError ), catchment[0].getCellYDouble( row, _HandleOutOfMemoryError ), centreX, centreY ) >= 50.0d ) {
                    catchment[0].setCell( row, col, noDataValue, _HandleOutOfMemoryError );
                }
            }
        }
        catchment[0].set_Name( "catchment1", _HandleOutOfMemoryError );
        return catchment;
    }
}
