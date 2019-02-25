/**
 * Copyright (C) 2017 Andy Turner, CCG, University of Leeds, UK
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
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package uk.ac.leeds.ccg.andyt.grids.examples;

import java.io.File;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 * A class for giving an example of a clip operation on a grid.
 *
 * @author geoagdt
 */
public class Grids_ClipGrid        extends Grids_Processor {

    private long Time;
    boolean HandleOutOfMemoryError;
    String Filename;

    protected Grids_ClipGrid() {
    }

    /**
     * Creates a new RoofGeneralisation using specified Directory. WARNING:
     * Files in the specified Directory may get overwritten.
     *
     * @param ge
     */
    public Grids_ClipGrid(Grids_Environment ge) {
        super(ge);
        Time = System.currentTimeMillis();
        HandleOutOfMemoryError = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dir = new File(
                System.getProperty("user.dir"));
        System.out.print("" + dir.toString());
        if (dir.exists()) {
            System.out.println(" exists.");
            dir.mkdirs();
        } else {
            System.out.println(" does not exist.");
        }
        Grids_Environment ge = new Grids_Environment(dir);
        Grids_ClipGrid t = new Grids_ClipGrid(ge);
        t.run();
    }

    public void run() {
        try {
            ge.setProcessor(this);
            Grids_Files gf;
            gf = ge.getFiles();
            File input;
            input = new File(                    gf.getInputDataDir(),                    "p15oct.asc");
//            input = new File(
//                    gf.getInputDataDir(),
//                    "RADAR_UK_Composite_Highres_23_6.asc");
//            input = new File(
//                    gf.getInputDataDir(),
//                    "2017-08-02RADAR_UK_Composite_Highres.asc");

            //C:\Users\geoagdt\src\grids\data\input
            Grids_ESRIAsciiGridExporter eage = new Grids_ESRIAsciiGridExporter(ge);
            Grids_ImageExporter ie = new Grids_ImageExporter(ge);
            File workspaceDirectory = new File(gf.getGeneratedDataDir(),
                    "/Workspace/");

            //String[] imageTypes = new String[0];
            String[] imageTypes = new String[1];
            imageTypes[0] = "PNG";
            String inputName;
            inputName = input.getName();
            System.out.println("inputFilename " + input);
            String inputNameWithoutExtension = inputName.substring(0, inputName.length() - 4);
            File outDir;
            outDir = new File(gf.getOutputDataDir(), getClass().getName());
            Grids_GridDouble gd;
            //Grids_GridDouble g;
            // Load input
            boolean notLoadedAsGrid = true;
            if (notLoadedAsGrid) {
                File dir;
                dir = new File(ge.getFiles().getGeneratedGridDoubleDir(),
                        inputNameWithoutExtension);
                gd = (Grids_GridDouble) GridDoubleFactory.create(dir, input);
                // clip gridDouble
//                dir = new File(ge.getFiles().getGeneratedGridDoubleDir(),
//                        "Clipped" + inputNameWithoutExtension);
                long nRows = gd.getNRows();
                long nCols = gd.getNCols();
                int chunkNRows = gd.getChunkNRows();
                int chunkNCols = gd.getChunkNCols();
                long startRow;
                long startCol;
                long endRow;
                long endCol;
//                // Whole chunk
//                startRow = 0;
//                startCol = 0;
//                endRow = nRows - 1;
//                endCol = nCols - 1;
//                // Move in a chunk from the bottom left
//                startRow = chunkNRows;
//                startCol = chunkNCols;
//                endRow = nRows - 1;
//                endCol = nCols - 1;
//                // Move in a chunk from the top right
//                startRow = 0;
//                startCol = 0;
//                endRow = nRows - 1 - chunkNRows;
//                endCol = nCols - 1 - chunkNCols;
                // Move in a chunk from the bottom left and top right
                startRow = chunkNRows + 10;
                startCol = chunkNCols + 10;
                endRow = nRows - 1 - chunkNRows - 10;
                endCol = nCols - 1 - chunkNCols - 10;
//                // Move in a chunk from the bottom left and top right
//                startRow = chunkNRows + 5;
//                startCol = chunkNCols + 50;
//                endRow = nRows - 1 - chunkNRows - 5;
//                endCol = nCols - 1 - chunkNCols - 50;
//                g = (Grids_GridDouble) GridDoubleFactory.create(
//                        dir,
//                        gridDouble,
//                        startRow,//0,
//                        startCol,//0,
//                        endRow,
//                        endCol,
//                        HandleOutOfMemoryError);
//                gridDouble = g;
                gd.writeToFile();
                System.out.println("<outputImage>");
                System.out.println("outputDirectory " + outDir);
                gd.setName(inputNameWithoutExtension
                        + "_" + startRow + "_" + "_" + startCol + "_"
                        + "_" + endRow + "_" + "_" + endCol + "_");
                System.out.println("gridDouble " + gd.toString());
                outputImage(gd, outDir, ie,
                        imageTypes, HandleOutOfMemoryError);
                System.out.println("</outputImage>");
            }
        } catch (Exception | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public long getTime() {
        return Time;
    }
}
