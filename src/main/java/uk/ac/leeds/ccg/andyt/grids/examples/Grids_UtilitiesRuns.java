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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grid2DSquareCellProcessor;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Kernel;

/**
 * TODO:
 * docs
 */
public class Grids_UtilitiesRuns extends Grid2DSquareCellProcessor implements Runnable {

    public Grids_UtilitiesRuns() throws IOException {
    }
    
    long time;
    
    public static void main(String[] args) {
        try {
            Grids_UtilitiesRuns u = new Grids_UtilitiesRuns();
        u.run();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.out.println( e.toString() );
        } catch ( Error e ) {
            e.printStackTrace();
            System.out.println( e.toString() );
        }
    }
    
    @Override
    public void run() {
        System.out.println( "Initialising..." );
        time = System.currentTimeMillis();
        
        //kernelVolume();
        //grid2CSV();
        //xyFileToGrid();
        //GridtoGam();
        //densityPlot();
        //toGainsChartCSV();
        //xyFileToGAM();
        //gamOutputToGrid();
        
        System.out.println("Processing complete in " + Grids_Utilities._ReportTime( System.currentTimeMillis() - time ) );
    }
    
    public void kernelVolume() {
        double weightIntersect = 1.0d;
        double weightFactor = 1.0d;
        for ( int bw = 100; bw < 1000; bw += 100 ) {
            double bandwidth = ( double ) bw * 1.0d;
            //for ( int precision = 10; precision < 100; precision ++ ) {
            int precision = 1000;
            System.out.println(Grids_Kernel.getKernelVolume( bandwidth, precision, weightIntersect, weightFactor ) );
        }
    }
    
    public void GridtoGam() {
//        String dataDirectory0 = new String( "d:/andyt/projects/phd/data/arc/leeds/grids/20/acc/acc9296/" );
//        String dataDirectory1 = new String( "d:/andyt/projects/phd/data/arc/leeds/grids/20/acc/acc9701/" );
//        String outDataDirectory = new String( "d:/andyt/projects/phd/data/gam/input/" );
//        File countFile = new File( new String( dataDirectory0 + "a.asc" ) );
//        File parFile = new File( new String( dataDirectory1 + "a.asc" ) );
//        File outGamFile = new File( new String( outDataDirectory + "a.dat" ) );
//        PrintWriter pw = null;
//        try {
//            pw = new PrintWriter( new BufferedWriter( new OutputStreamWriter( new FileOutputStream( outGamFile ) ) ) );
//        } catch ( java.io.IOException e ) {
//            System.out.println( e );
//            System.exit( 0 );
//        }
//        Grid2DSquareCellDoubleFactory factory = new Grid2DSquareCellDoubleFactory();
//        Grid2DSquareCellDouble countGrid = factory.createGrid2DSquareCellDouble( countFile );
//        Grid2DSquareCellDouble parGrid = factory.createGrid2DSquareCellDouble( parFile );
//        
//        long nrows = countGrid.getNrows();
//        long ncols = countGrid.getNcols();
//        double countNoDataValue = countGrid.getNoDataValue();
//        double parNoDataValue = parGrid.getNoDataValue();
//        double x = 0.0d;
//        double y = 0.0d;
//        double count = 0.0d;
//        double par = 0.0d;
//        
//        long rowIndex;
//        long colIndex;
//        
//        for ( rowIndex = 0; rowIndex < nrows; rowIndex ++ ) {
//            for ( colIndex = 0; colIndex < ncols; colIndex ++ ) {
//                count = countGrid.getCell( rowIndex, colIndex );
//                par = parGrid.getCell( rowIndex, colIndex );
//                if ( par != parNoDataValue && par != 0.0d ) {
//                   if ( count == countNoDataValue ) {
//                       count = 0.0d;
//                   }
//                   pw.println( ( ( rowIndex * ncols ) + colIndex ) + " " + countGrid.getCellX( colIndex ) + " " + countGrid.getCellY( rowIndex ) + " " + count + " " + par );
//                }
//            }
//        }
//        pw.flush();
//        pw.close();
    }
    
    public void densityPlot( 
            boolean _HandleOutOfMemoryError ) 
    throws Exception {
        String resolution = "100";
        String inDataDirectory = "d:/andyt/projects/phd/data/arc/leeds/grids/" + resolution + "/";
        String outDataDirectory = "d:/andyt/projects/phd/data/plots/" + resolution + "/";
        String xFilename = "roadm";
        String yFilename = "casnullm";
        Grids_Grid2DSquareCellDoubleFactory grid2DSquareCellDoubleFactory = new Grids_Grid2DSquareCellDoubleFactory(env, _HandleOutOfMemoryError);
        Grids_Grid2DSquareCellDouble xGrid = ( Grids_Grid2DSquareCellDouble ) grid2DSquareCellDoubleFactory.create( new File( inDataDirectory + xFilename + ".asc" ) );
        Grids_Grid2DSquareCellDouble yGrid = ( Grids_Grid2DSquareCellDouble ) grid2DSquareCellDoubleFactory.create( new File( inDataDirectory + yFilename + ".asc" ) );
        int divisions = 100;
        System.out.println( xGrid.toString() );
        System.out.println( yGrid.toString() );
        System.out.println("Processing...");
        Object[] result = Grids_Utilities.densityPlot( xGrid, yGrid, divisions, grid2DSquareCellDoubleFactory );
        double[] stdevy = ( double[] ) result[ 0 ];
        double[] meany = ( double[] ) result[ 1 ];
        double[] numy = ( double[] ) result[ 2 ];
        Grids_Grid2DSquareCellDouble densityPlotGrid = ( Grids_Grid2DSquareCellDouble ) result[ 3 ];
        System.out.println( densityPlotGrid.toString() );
        double divx = 
                ( xGrid.getGridStatistics( _HandleOutOfMemoryError ).getMaxDouble( _HandleOutOfMemoryError ) - xGrid.getGridStatistics( _HandleOutOfMemoryError ).getMinDouble( _HandleOutOfMemoryError ) ) / divisions;
        System.out.println("Exchanging...");
        //Grid2DSquareCellDoubleExchange.toImage( densityPlotGrid, new File( outDataDirectory + yFilename + xFilename + divisions + "DensityPlot.png" ), "PNG" );
        new Grids_ESRIAsciiGridExporter(env).toAsciiFile( densityPlotGrid, new File( outDataDirectory + yFilename + xFilename + divisions + "DensityPlot.asc" ), _HandleOutOfMemoryError );
        PrintWriter pw = null;
        try {
            pw = new PrintWriter( new FileOutputStream( new File( outDataDirectory + yFilename + xFilename + divisions + "DensityPlot.csv" ) ) );
        } catch ( java.io.FileNotFoundException e ) {
            System.out.println( e );
            System.exit( 0 );
        }
        pw.println( "meanx,meany-stdevy,meany,meany+stdevy,numy" );
        for ( int i = 0; i < divisions; i ++ ) {
            if ( numy[ i ] > 0.0d ) {
                //if ( numy[ i ] != 0.0d && ( numy[ i ] - 1.0d ) != 0.0d ) {
                pw.println( ( ( ( double ) i + 0.5 ) * divx ) + "," + ( meany[ i ] - stdevy[ i ] ) + "," + meany[ i ] + "," + ( meany[ i ] + stdevy[ i ] ) + "," + numy[ i ] );
            }
        }
        pw.flush();
        pw.close();
        System.out.println("Finalising...");
        //yGrid.clear();
        //xGrid.clear();
        //densityPlotGrid.clear();
    }
    
    public void toGainsChartCSV() {
//        int divisions = 100;
//        String inDataDirectory = new String( "d:/andyt/projects/phd/data/arc/leeds/grids/100/" );
//        String outDataDirectory = new String( "d:/andyt/projects/phd/data/plots/100/" );
//        String yFilename = "casnullm";
//        String xFilename = "roadm";
//        Grid2DSquareCellDoubleFactory grid2DSquareCellDoubleFactory = new Grid2DSquareCellDoubleFactory();
//        Grid2DSquareCellDouble yGrid = grid2DSquareCellDoubleFactory.createGrid2DSquareCellDouble( new File( inDataDirectory + yFilename + ".asc" ) );
//        Grid2DSquareCellDouble xGrid = grid2DSquareCellDoubleFactory.createGrid2DSquareCellDouble( new File( inDataDirectory + xFilename + ".asc" ) );
//        //System.out.println( xGrid.toString() );
//        //System.out.println( yGrid.toString() );
//        System.out.println("Processing...");
//        Grids_Utilities.toGainsChartCSV( yGrid, xGrid, divisions, new File( outDataDirectory + yFilename + xFilename + divisions + "GainsChart.csv" ) );
//        System.out.println("Finalising...");
    }
}
