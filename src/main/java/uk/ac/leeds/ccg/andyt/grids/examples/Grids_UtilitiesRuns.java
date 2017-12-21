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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Kernel;

/**
 * TODO: docs
 */
public class Grids_UtilitiesRuns extends Grids_Processor implements Runnable {

    public Grids_UtilitiesRuns() throws IOException {
    }

    long time;

    public static void main(String[] args) {
        try {
            Grids_UtilitiesRuns u = new Grids_UtilitiesRuns();
            u.run();
        } catch (Exception | Error e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void run() {
        System.out.println("Initialising...");
        time = System.currentTimeMillis();

        //kernelVolume();
        //grid2CSV();
        //xyFileToGrid();
        //GridtoGam();
        //densityPlot();
        //toGainsChartCSV();
        //xyFileToGAM();
        //gamOutputToGrid();
        System.out.println("Processing complete in " + Grids_Utilities.getTime(System.currentTimeMillis() - time));
    }

    public void kernelVolume() {
        double weightIntersect = 1.0d;
        double weightFactor = 1.0d;
        for (int bw = 100; bw < 1000; bw += 100) {
            double bandwidth = (double) bw * 1.0d;
            //for ( int precision = 10; precision < 100; precision ++ ) {
            int precision = 1000;
            System.out.println(Grids_Kernel.getKernelVolume(bandwidth, precision, weightIntersect, weightFactor));
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
//        Grids_GridDoubleFactory factory = new Grids_GridDoubleFactory();
//        Grids_GridDouble countGrid = factory.create(countFile);
//        Grids_GridDouble parGrid = factory.create(parFile);
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
            boolean hoome)
            throws Exception {
        Grids_Files files;
        files = ge.getFiles();
        int divisions = 100;
        String resolution = "" + divisions;
        //File dataDir = files.getDataDir();
        String inDataDirectory = "d:/andyt/projects/phd/data/arc/leeds/grids/" + resolution + "/";
        String outDataDirectory = "d:/andyt/projects/phd/data/plots/" + resolution + "/";
        String xFilename = "roadm";
        String yFilename = "casnullm";
//        Grids_GridDoubleFactory gf;
//        gf = new Grids_GridDoubleFactory(
//                ge,
//                files.getGeneratedGridDoubleFactoryDir(),
//                -Double.MAX_VALUE,
//                divisions,
//                divisions,
//                new Grids_Dimensions(divisions, divisions),
//                new Grids_GridDoubleStatsNotUpdated(ge),
//                new Grids_GridChunkDoubleArrayFactory());
//        Grids_GridDouble xGrid = (Grids_GridDouble) gf.create(
//                new File(inDataDirectory + xFilename + ".asc"));
//        Grids_GridDouble yGrid = (Grids_GridDouble) gf.create(
//                new File(inDataDirectory + yFilename + ".asc"));
        Grids_GridDouble xGrid = (Grids_GridDouble) GridDoubleFactory.create(
                new File(inDataDirectory + xFilename + ".asc"));
        Grids_GridDouble yGrid = (Grids_GridDouble) GridDoubleFactory.create(
                new File(inDataDirectory + yFilename + ".asc"));
        System.out.println(xGrid.toString());
        System.out.println(yGrid.toString());
        System.out.println("Processing...");
        Object[] result = Grids_Utilities.densityPlot(xGrid, yGrid, divisions, GridDoubleFactory);
//        Object[] result = Grids_Utilities.densityPlot(xGrid, yGrid, divisions, gf);
        double[] stdevy = (double[]) result[0];
        double[] meany = (double[]) result[1];
        double[] numy = (double[]) result[2];
        Grids_GridDouble densityPlotGrid = (Grids_GridDouble) result[3];
        System.out.println(densityPlotGrid.toString());
        Grids_GridDoubleStats stats;
        stats = (Grids_GridDoubleStats) xGrid.getStats(hoome);
        double divx;
        divx = (stats.getMax(true) - stats.getMin(true)) / divisions;
        System.out.println("Exchanging...");
        //Grid2DSquareCellDoubleExchange.toImage( densityPlotGrid, new File( outDataDirectory + yFilename + xFilename + divisions + "DensityPlot.png" ), "PNG" );
        new Grids_ESRIAsciiGridExporter(ge).toAsciiFile(densityPlotGrid, 
                new File(outDataDirectory + yFilename + xFilename + divisions + "DensityPlot.asc"));
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(new File(outDataDirectory + yFilename + xFilename + divisions + "DensityPlot.csv")));
        } catch (java.io.FileNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        }
        pw.println("meanx,meany-stdevy,meany,meany+stdevy,numy");
        for (int i = 0; i < divisions; i++) {
            if (numy[i] > 0.0d) {
                //if ( numy[ i ] != 0.0d && ( numy[ i ] - 1.0d ) != 0.0d ) {
                pw.println((((double) i + 0.5) * divx) + "," + (meany[i] - stdevy[i]) + "," + meany[i] + "," + (meany[i] + stdevy[i]) + "," + numy[i]);
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
