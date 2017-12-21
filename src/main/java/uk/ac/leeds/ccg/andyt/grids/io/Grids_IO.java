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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import javax.imageio.ImageIO;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;

/**
 * This class holds general utility methods for io.
 */
public class Grids_IO {

    /**
     * Creates a new instance of io
     */
    public Grids_IO() {
    }

    /**
     * Returns a default data directory
     * @return 
     */
    public static String getDefaultDataDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Returns true if writerType is available
     *
     * @param writerType - usually a well known text String for an image type
     * @return 
     */
    public static boolean isImageWriterAvailable(String writerType) {
        boolean result = false;
        String[] writerTypes = ImageIO.getWriterMIMETypes();
        for (int i = 0; i < writerTypes.length; i++) {
            if (writerTypes[ i].equalsIgnoreCase("image/" + writerType)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Creates gamInputFile from the countFile and parFile. The basic format is:
     * id x y count par
     * @param gamInputFile
     * @param parGrid
     * @param countGrid
     */
    public static void generateGamInput(File gamInputFile, Grids_AbstractGridNumber countGrid, Grids_AbstractGridNumber parGrid) {
        //        //System.out.println( "Utilities.generateGamInput( gamInputFile( " + gamInputFile + " ), countGrid( " + countGrid.toString() + " ), parGrid( " + parGrid.toString() + " ) )" );
        //        int nrows = countGrid.getNrows();
        //        int ncols = countGrid.getNcols();
        //        double xllcorner = countGrid.getXllcorner();
        //        double yllcorner = countGrid.getYllcorner();
        //        double cellsize = countGrid.getCellsize();
        //        double noDataValue = countGrid.getNoDataValue();
        //        // Generate Gam input file
        //        PrintWriter pw = null;
        //        try {
        //            pw = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( gamInputFile ) ) ) );
        //        } catch (java.io.FileNotFoundException e) {
        //            System.out.println( e.toString() );
        //            System.out.println( "in uk.ac.leeds.ccg.grids.Utilities.GridToGam( gamInputFile( " + gamInputFile.toString() + " ), countGrid( " + countGrid.toString() + " ), parGrid( " + parGrid.toString() + " ) )" );
        //            e.printStackTrace();
        //        }
        //        double countNoDataValue = countGrid.getNoDataValue();
        //        double parNoDataValue = parGrid.getNoDataValue();
        //        System.out.println( "parNoDataValue " + parNoDataValue );
        //        System.out.println( "countNoDataValue " + countNoDataValue );
        //        //double parNoDataValue = 0.0d;
        //        double x = 0.0d;
        //        double y = 0.0d;
        //        double count = 0.0d;
        //        double par = 0.0d;
        //        for ( int i = 0; i < nrows * ncols; i++ ) {
        //            count = countGrid.getCell( i );
        //            par = parGrid.getCell( i );
        //            if ( count == countNoDataValue || par == parNoDataValue ) {
        //                if ( count == countNoDataValue && par == parNoDataValue ) {
        //                    System.out.println( "Warning locations of noDataValues are not the same!" );
        //                }
        //            } else {
        //                if ( ! ( count == 0.0d && par == 0.0d ) ) {
        //                    //System.out.println( i + " " + countGrid.getCellX( i ) + " " + countGrid.getCellY( i ) + " " + count + " " + par );
        //                    pw.println( i + " " + countGrid.getCellX( i ) + " " + countGrid.getCellY( i ) + " " + count + " " + par );
        //                }
        //            }
        //        }
        //        pw.flush();
        //        pw.close();
    }

    //    public Raster GamOutputToRaster( File gamOutputFile, Grid2DSquareCellDoubleAbstract grid ) {
    //        //System.out.println( "GamOutputToRaster( gamOutputFile( " + gamOutputFile.toString() + " ), grid( " + grid.toString() +  " ) )" );
    //        int nrows = grid.getNrows();
    //        int ncols = grid.getNcols();
    //        double xllcorner = grid.getXllcorner();
    //        double yllcorner = grid.getYllcorner();
    //        double cellsize = grid.getCellsize();
    //        double noDataValue = grid.getNoDataValue();
    //        GeoRectangle geoRectangle = new GeoRectangle( xllcorner, yllcorner, ( cellsize * ncols ), ( cellsize * nrows ) );
    //        Raster raster = new Raster( geoRectangle, cellsize );
    //        Vector geoCircles = new Vector();
    //        SimpleGeoData geoData = new SimpleGeoData();
    //        GeoCircle geoCircle = null;
    //        try {
    //            StreamTokenizer st = new StreamTokenizer( new BufferedReader( new InputStreamReader( new FileInputStream( gamOutputFile ) ) ) );
    //            st.eolIsSignificant( false );
    //            st.parseNumbers();
    //            st.whitespaceChars( ',', ',' );
    //            st.wordChars( '"', '"' );
    //            int tokenType = st.nextToken();
    //            int read = 0;
    //            double x = 0.0d;
    //            double y = 0.0d;
    //            double radius = 0.0d;
    //            int id = 0;
    //            while ( tokenType != StreamTokenizer.TT_EOF ) {
    //                switch ( tokenType ) {
    //                    case StreamTokenizer.TT_NUMBER :
    //                        switch ( read ) {
    //                            case 0 :
    //                                read = 1;
    //                                break;
    //                            case 1 :
    //                                x = st.nval;
    //                                read = 2;
    //                                break;
    //                            case 2 :
    //                                y = st.nval;
    //                                read = 3;
    //                                break;
    //                            case 3 :
    //                                radius = st.nval;
    //                                read = 4;
    //                                break;
    //                            case 4 :
    //                                geoData.setValue( id, st.nval );
    //                                geoCircle = new GeoCircle( id, x, y, radius );
    //                                geoCircles.add( geoCircle );
    //                                id ++;
    //                                read = 0;
    //                                break;
    //                        }
    //                        break;
    //                    default :
    //                        break;
    //                }
    //                tokenType = st.nextToken();
    //            }
    //        }
    //        catch ( java.io.IOException e ) {
    //            System.out.println( e + " in GamOutputToRaster( gamOutputFile( " + gamOutputFile.toString() + " ), grid( " + grid.toString() +  " ) )" );
    //            e.printStackTrace();
    //        }
    //        return null;
    //        //return new circleRaster( geoCircles, geoData, cellsize, geoRectangle );
    //    }
    //
    //    public CircleLayer GamOutputToCircleLayer( File gamOutputFile, Grid2DSquareCellDoubleAbstract grid ) {
    //        System.out.println( "GamOutputToCircleLayer( gamOutputFile( " + gamOutputFile.toString() + " ), grid( " + grid.toString() +  " ) )" );
    //        int nrows = grid.getNrows();
    //        int ncols = grid.getNcols();
    //        double xllcorner = grid.getXllcorner();
    //        double yllcorner = grid.getYllcorner();
    //        double cellsize = grid.getCellsize();
    //        double noDataValue = grid.getNoDataValue();
    //        GeoRectangle geoRectangle = new GeoRectangle( xllcorner, yllcorner, ( cellsize * ncols ), ( cellsize * nrows ) );
    //        //Raster raster = new Raster( geoRectangle, cellsize );
    //        CircleLayer circleLayer = new CircleLayer();
    //        //Vector geoCircles = new Vector();
    //        SimpleGeoData geoData = new SimpleGeoData();
    //        GeoCircle geoCircle = null;
    //        try {
    //            StreamTokenizer st = new StreamTokenizer( new BufferedReader( new InputStreamReader( new FileInputStream( gamOutputFile ) ) ) );
    //            //st.eolIsSignificant( false );
    //            st.parseNumbers();
    //            int tokenType = st.nextToken();
    //            int read = 0;
    //            int id = 0;
    //            double x = 0.0d;
    //            double y = 0.0d;
    //            double radius = 0.0d;
    //            double value = 0.0d;
    //            while ( tokenType != StreamTokenizer.TT_EOF ) {
    //                switch ( tokenType ) {
    //                    case StreamTokenizer.TT_NUMBER :
    //                        switch ( read ) {
    //                            case 0 :
    //                                read = 1;
    //                                break;
    //                            case 1 :
    //                                x = st.nval;
    //                                read = 2;
    //                                break;
    //                            case 2 :
    //                                y = st.nval;
    //                                read = 3;
    //                                break;
    //                            case 3 :
    //                                radius = st.nval;
    //                                read = 4;
    //                                break;
    //                            case 4 :
    //                                value = st.nval;
    //                                geoData.setValue( id, value );
    //                                geoCircle = new GeoCircle( id, x, y, radius );
    //                                System.out.println( id + ", " + x + ", " + y + ", " + radius + ", " + value );
    //                                circleLayer.addGeoCircle( geoCircle );
    //                                id ++;
    //                                read = 0;
    //                                break;
    //                        }
    //                        break;
    //                    default :
    //                        break;
    //                }
    //                tokenType = st.nextToken();
    //            }
    //        }
    //        catch ( java.io.IOException e ) {
    //            System.out.println( e + " in GamOutputToRaster( gamOutputFile( " + gamOutputFile.toString() + " ), grid( " + grid.toString() +  " ) )" );
    //            e.printStackTrace();
    //        }
    //        return circleLayer;
    //    }
    /**
     * This will modify grid
     *
     * @param xyFile
     * @param grid
     * @param linesInHeader
     * @param handleOutOfMemoryError
     */
    public static void xyFileToGrid(
            File xyFile,
            Grids_GridDouble grid,
            int linesInHeader,
            boolean handleOutOfMemoryError) {
        //System.out.println( "xyFileToGrid( xyFile( " + xyFile.toString() + " ), grid( " + grid.toString() + " ) )" );
        try {
            Reader r = new BufferedReader(new InputStreamReader(new FileInputStream(xyFile)));
            StreamTokenizer st = new StreamTokenizer(r);
            st.eolIsSignificant(false);
            st.parseNumbers();
            st.whitespaceChars(',', ',');
            st.wordChars('"', '"');
//            st.eolIsSignificant(true);
//            for (int i = 0; i < linesInHeader; i++) {
//                int tokenType = st.nextToken();
//                while (tokenType != StreamTokenizer.TT_EOL) {
//                    tokenType = st.nextToken();
//                }
//            }
            int tokenType = st.nextToken();
            int read = 0;
            double x = 0.0d;
            double y = 0.0d;
            int count = 0;
            while (tokenType != StreamTokenizer.TT_EOF) {
                switch (tokenType) {
                    case StreamTokenizer.TT_NUMBER:
                        switch (read) {
                            case 0:
                                x = st.nval;
                                read = 1;
                                break;
                            case 1:
                                y = st.nval;
                                grid.addToCell(x, y, 1.0d);
//                                double current = grid.addToCell(
//                                        x, y, 1.0d, handleOutOfMemoryError);
//                                if (current > 0) {
//                                    System.out.println(current);
//                                }
                                read = 0;
                                break;
                        }
                        break;
                    default:
                        count ++;
                        if (count % 10000 == 0) {
                            System.out.println("Read " + count + " points.");
                        }
                        break;
                }
                tokenType = st.nextToken();
            }
        } catch (java.io.IOException e) {
            System.out.println(e.toString());
            System.out.println("in uk.ac.leeds.ccg.grids.Utilities.xyFileToGrid( xyFile( " + xyFile.toString() + " ), grid( " + grid.toString() + " ) )");
            e.printStackTrace();
        }
    }

    public static void gamOutputToGrid(File gamOutputFile, Grids_AbstractGridNumber grid, double weight) {
        //        try {
        //            StreamTokenizer st = new StreamTokenizer( new BufferedReader( new InputStreamReader( new FileInputStream( gamOutputFile ) ) ) );
        //            st.eolIsSignificant( false );
        //            st.parseNumbers();
        //            st.whitespaceChars( ',', ',' );
        //            st.wordChars( '"', '"' );
        //            int tokenType = st.nextToken();
        //            int read = 0;
        //            double x = 0.0d;
        //            double y = 0.0d;
        //            double radius = 0.0d;
        //            while ( tokenType != StreamTokenizer.TT_EOF ) {
        //                switch ( tokenType ) {
        //                    case StreamTokenizer.TT_NUMBER :
        //                        switch ( read ) {
        //                            case 0 :
        //                                read = 1;
        //                                break;
        //                            case 1 :
        //                                x = st.nval;
        //                                read = 2;
        //                                break;
        //                            case 2 :
        //                                y = st.nval;
        //                                read = 3;
        //                                break;
        //                            case 3 :
        //                                radius = st.nval;
        //                                read = 4;
        //                                break;
        //                            case 4 :
        //                                addToGrid( grid, x, y, radius, weight * st.nval );
        //                                //System.out.println( "adding " + x + " " + y + " " + radius + " " + st.nval );
        //                                read = 0;
        //                                break;
        //                        }
        //                        break;
        //                    default :
        //                        break;
        //                }
        //                tokenType = st.nextToken();
        //            }
        //        }
        //        catch ( java.io.IOException e ) {
        //            System.out.println( e.toString() );
        //            System.out.println( "in uk.ac.leeds.ccg.grids.Utilities.GamOutputToRaster( gamOutputFile( " + gamOutputFile.toString() + " ), grid( " + grid.toString() +  " ) )" );
        //            e.printStackTrace();
        //        }
    }

    //    // Should have moved to process
    //    private static void addToGrid( Grids_AbstractGridNumber grid, double x, double y, double radius, double centralWeight ) {
    //        //        double weightFactor = 1.0d;
    //        //        int nrows = grid.getNrows();
    //        //        int ncols = grid.getNcols();
    //        //        double cellsize = grid.getCellsize();
    //        //        Point2D.Double point;
    //        //        int cellDistance = ( int ) Math.ceil( radius / cellsize );
    //        //        int row = grid.getRowIndex( y );
    //        //        int col = grid.getColIndex( x );
    //        //        double gridCellX;
    //        //        double gridCellY;
    //        //        double distance;
    //        //        for ( int i = row - cellDistance; i <= row + cellDistance; i ++ ) {
    //        //            for ( int j = col - cellDistance; j <= col + cellDistance; j ++ ) {
    //        //                gridCellX = grid.getCellX( i, j );
    //        //                gridCellY = grid.getCellY( i, j );
    //        //                distance = distance( x, y, gridCellX, gridCellY );
    //        //                if ( distance < radius ) {
    //        //                    Utilities.getKernelWeight( radius, centralWeight, weightFactor, distance );
    //        //                    grid.addToCell( i, j, Utilities.getKernelWeight( radius, centralWeight, weightFactor, distance ) );
    //        //                }
    //        //            }
    //        //        }
    //    }
    public static void xyFileToGAM(File parFile, File countFile, File gamFile, double radius, double xmin, double ymin, double xmax, double ymax) {
        //        int id = 0;
        //        try {
        //            PrintWriter pw = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( gamFile ) ) ) );
        //            for ( int files = 0; files < 2; files ++ ) {
        //                File file = null;
        //                if ( files == 0 ) { file = parFile; }
        //                if ( files == 1 ) { file = countFile; }
        //                StreamTokenizer st = new StreamTokenizer( new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) ) );
        //                st.eolIsSignificant( false );
        //                st.parseNumbers();
        //                st.whitespaceChars( ',', ',' );
        //                st.wordChars( '"', '"' );
        //                int tokenType = st.nextToken();
        //                int read = 0;
        //                double x = 0.0d;
        //                double y = 0.0d;
        //                while ( tokenType != StreamTokenizer.TT_EOF ) {
        //                    switch ( tokenType ) {
        //                        case StreamTokenizer.TT_NUMBER :
        //                            switch ( read ) {
        //                                case 0 :
        //                                    x = st.nval;
        //                                    read = 1;
        //                                    break;
        //                                case 1 :
        //                                    y = st.nval;
        //                                    if ( x >= xmin && x <= xmax && y >= ymin && y <= ymax ) {
        //                                        //if ( files == 0 ) { pw.println( id + " " + x + " " + y + " 0 1" ); }
        //                                        //if ( files == 1 ) { pw.println( id + " " + x + " " + y + " 1 0" ); }
        //                                        if ( files == 0 ) { pw.println( id + " " + x + " " + y + " 1 0" ); }
        //                                        if ( files == 1 ) { pw.println( id + " " + x + " " + y + " 0 1" ); }
        //                                        id ++;
        //                                    }
        //                                    read = 0;
        //                                    break;
        //                            }
        //                            break;
        //                        default :
        //                            break;
        //                    }
        //                    tokenType = st.nextToken();
        //                }
        //            }
        //            pw.flush();
        //            pw.close();
        //        } catch ( java.io.IOException e ) {
        //            System.out.println( e.toString() );
        //            System.out.println( "in uk.ac.leeds.ccg.grids.Utilities.xyFileToGAM( parFile( " + parFile.toString() + " ), countFile( " + countFile.toString() + " ), gamFile( " + gamFile.toString() + " ), radius( " + radius + " ), xmin( " + xmin + "), ymin( " + ymin + " ), xmax( " + xmax + " ), ymax( " + ymax + " ) )" );
        //            e.printStackTrace();
        //        }
    }

    /**
     * Generates CSV file from gridArray
     * @param gridArray
     * @param csvFile
     * @param header
     */
    public static void gridArray2CSV(Grids_AbstractGridNumber[] gridArray, String header, File csvFile) {
        //        PrintWriter pw = null;
        //        try {
        //            pw = new PrintWriter( new FileOutputStream( csvFile ) );
        //        } catch ( java.io.IOException e ) {
        //            System.out.println( e );
        //            System.out.println( "in uk.ac.leeds.ccg.grids.Utilities.gridArray2CSV( gridArray[ " + gridArray.length + " ], header( " + header + " ), csvFile( " + csvFile.toString() + " ) )" );
        //            e.printStackTrace();
        //        }
        //        int nrows = gridArray[ 0 ].getNrows();
        //        int ncols = gridArray[ 0 ].getNcols();
        //        String line = "";
        //        pw.println( header );
        //        for ( int cell = 0; cell < nrows * ncols; cell ++ ) {
        //            line = "";
        //            for( int i = 0; i < gridArray.length; i ++ ) {
        //                line = line + gridArray[ i ].getCell( cell );
        //                if ( i != gridArray.length - 1 ) {
        //                    line = line + ",";
        //                }
        //            }
        //            pw.println( line );
        //        }
        //        pw.flush();
        //        pw.close();
    }
}
