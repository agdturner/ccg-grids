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
package uk.ac.leeds.ccg.agdt.grids.examples;

import java.io.IOException;
import java.nio.file.Paths;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.agdt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.agdt.grids.process.Grids_Processor;

/**
 * Provides example of a clip operation on a grid.
 *
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ClipGrid extends Grids_Processor {

    private static final long serialVersionUID = 1L;

    boolean HandleOutOfMemoryError;
    String Filename;

    /**
     * @throws IOException 
     */
    protected Grids_ClipGrid() throws IOException, ClassNotFoundException {
        super();
        HandleOutOfMemoryError = true;
    }

    /**
     * Creates a new RoofGeneralisation using specified Directory. WARNING:
     * Files in the specified Directory may get overwritten.
     *
     * @param ge
     */
    public Grids_ClipGrid(Grids_Environment ge) throws IOException, 
            ClassNotFoundException {
        super(ge);
        HandleOutOfMemoryError = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Grids_ClipGrid t = new Grids_ClipGrid();
            t.run();
        } catch (Exception | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public void run() {
        try {
            env.setProcessor(this);
            Generic_Path input = new Generic_Path(Paths.get(
                    files.getInputDir().toString(),                    "p15oct.asc"));
//            input = new File(
//                    gf.getInputDataDir(),
//                    "RADAR_UK_Composite_Highres_23_6.asc");
//            input = new File(
//                    gf.getInputDataDir(),
//                    "2017-08-02RADAR_UK_Composite_Highres.asc");

            //C:\Users\geoagdt\src\grids\data\input
            Grids_ESRIAsciiGridExporter eage = new Grids_ESRIAsciiGridExporter(env);
            Grids_ImageExporter ie = new Grids_ImageExporter(env);
            //String[] imageTypes = new String[0];
            String[] imageTypes = new String[1];
            imageTypes[0] = "PNG";
            String inputName  = input.getFileName().toString();
            System.out.println("inputFilename " + input);
            String inputNameWithoutExtension = inputName.substring(0, inputName.length() - 4);
            Generic_Path outDir = new Generic_Path(Paths.get(
                    files.getOutputDir().toString(), getClass().getName()));
            Grids_GridDouble gd;
            //Grids_GridDouble g;
            // Load input
            boolean notLoadedAsGrid = true;
            if (notLoadedAsGrid) {
                Generic_Path dir = new Generic_Path(Paths.get(
                        files.getGeneratedGridDoubleDir().toString(),
                        inputNameWithoutExtension));
                gd = (Grids_GridDouble) GridDoubleFactory.create(dir, input);
                // clip gridDouble
//                dir = new File(files.getGeneratedGridDoubleDir(),
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
}
