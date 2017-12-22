/**
 * Copyright (C) 2008 Andy Turner, Sadhvi Selvaraj, CCG, University of Leeds, UK
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.ac.leeds.ccg.andyt.grids.examples;

import java.io.File;
import java.io.IOException;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumberFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

public class Grids_GenerateRoofData
        extends Grids_Processor {

    private long Time;
    boolean HandleOutOfMemoryError;
    String FileSeparator;
    int MessageLength;
    int FilenameLength;
    String Filename;
    Grids_ImageExporter ImageExporter;
    String[] ImageTypes;
    Grids_ESRIAsciiGridExporter ESRIAsciiGridExporter;

    protected Grids_GenerateRoofData() {
    }

    public Grids_GenerateRoofData(Grids_Environment ge) {
        super(ge);
        this.Time = System.currentTimeMillis();
        this.HandleOutOfMemoryError = true;
        this.FileSeparator = System.getProperty("file.separator");
        this.MessageLength = 1000;
        this.FilenameLength = 1000;
        this.Filename = "";
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File Directory = new File(
                    "C:/Work/People/Sadhvi Selvaraj/Roofs/data/synthetic/");
            Grids_Environment ge;
            ge = new Grids_Environment(Directory);
            Grids_GenerateRoofData p;
            p = new Grids_GenerateRoofData(ge);
            p.ImageExporter = null;
            p.ImageTypes = null;
            p.ESRIAsciiGridExporter = null;
            p.HandleOutOfMemoryError = true;
            //_GenerateRoofData.run_0();
            //_GenerateRoofData.run_1();
            //p.run_2();
        } catch (Error | Exception e) {
            e.printStackTrace(System.err);
        }
    }

//    /**
//     * Creates ouputs with ridge heights between 1 and 10 and all the same dimensions (rows and columns)
//     * @throws java.io.IOException
//     */
//    public void run_2()
//            throws IOException {
//        Grids_Dimensions _Dimensions = new Grids_Dimensions(
//                ge,
//        new BigDecimal("0"),
//                new BigDecimal("0"),
//                new BigDecimal("64"),
//                new BigDecimal("32"),
//                _Dimensions[4].intValue(),
//                new BigDecimal("1"));
//        
//        long _ResizedNCols = _Dimensions[3].intValue();
//        long nrows;
//        long ncols;
//        double _CellsizeDivideTwo = _Dimensions[0].doubleValue() / 2.0d;
//        long _RowWithRidgeProportion;
//        long _RowWithRidge;
//        long _ColWithRidgeProportion;
//        long _ColWithRidge;
//        // ---------------------------
//        // Because we are rescaling in createGableRoofs and createHippedRoofs and the roofs are non complex this is arbitrary so long as it is a positive non-zero
//        double _RowRidgeHeight = 1.0d;
//        double _ColRidgeHeight = 1.0d;
//        // ---------------------------
//        long _ColStartRidgeProportion;
//        long _ColStartRidge;
//        long _ColEndRidgeProportion;
//        long _ColEndRidge;
//        long _RowStartRidgeProportion;
//        long _RowStartRidge;
//        long _RowEndRidgeProportion;
//        long _RowEndRidge;
//        Grids_GridDouble _ResizedGrid2DSquareCellDouble;
//        _ResizedGrid2DSquareCellDouble = this.GridDoubleFactory.create(
//                Directory, _ResizedNRows, _ResizedNCols, _Dimensions, HandleOutOfMemoryError);
//        //for (nrows = 10; nrows <= 80; nrows *= 2) {
//        //for ( nrows = 5; nrows <= 80; nrows *= 2 ) {
//        for (nrows = 11; nrows <= 88; nrows *= 2) {
//            //for ( ncols = 4; ncols <= 64; ncols *= 2 ) {
//            //for (ncols = 8; ncols <= 64; ncols *= 2) {
//            for (ncols = 9; ncols <= 72; ncols *= 2) {
//                for (_RowWithRidgeProportion = 2; _RowWithRidgeProportion <= 2; _RowWithRidgeProportion++) {
//                    //for (_RowWithRidgeProportion = 2; _RowWithRidgeProportion <= 8; _RowWithRidgeProportion *= 2) {
//                    _RowWithRidge = nrows / _RowWithRidgeProportion;
//                    for (_ColWithRidgeProportion = 2; _ColWithRidgeProportion <= 2; _ColWithRidgeProportion++) {
//                        //for (_ColWithRidgeProportion = 2; _ColWithRidgeProportion <= 8; _ColWithRidgeProportion *= 2) {
//                        _ColWithRidge = ncols / _ColWithRidgeProportion;
////                        // Because we are rescaling in createGableRoofs and createHippedRoofs and the roofs are non complex we do not need these loops
////                        for ( _RowRidgeHeight = 2; _RowRidgeHeight <= 8; _RowRidgeHeight *= 2 ) {
////                            for ( _ColRidgeHeight = 2; _ColRidgeHeight <= 8; _ColRidgeHeight *= 2 ) {
//                        createGableRoofs(
//                                _ResizedGrid2DSquareCellDouble,
//                                nrows,
//                                ncols,
//                                _CellsizeDivideTwo,
//                                _RowWithRidge,
//                                _ColWithRidge,
//                                (double) _RowRidgeHeight,
//                                (double) _ColRidgeHeight);
//                        //for (_RowStartRidgeProportion = 2; _RowStartRidgeProportion <= 8; _RowStartRidgeProportion *= 2) {
//                        for (_RowStartRidgeProportion = 2; _RowStartRidgeProportion <= 4; _RowStartRidgeProportion++) {
//                            _RowStartRidge = nrows / _RowStartRidgeProportion;
//                            //for (_ColStartRidgeProportion = 2; _ColStartRidgeProportion <= 8; _ColStartRidgeProportion *= 2) {
//                            //_ColEndRidgeProportion = _ColStartRidgeProportion;
//                            for (_ColStartRidgeProportion = 2; _ColStartRidgeProportion <= 4; _ColStartRidgeProportion++) {
//                                _ColStartRidge = ncols / _ColStartRidgeProportion;
//                                //_RowEndRidgeProportion = _RowStartRidgeProportion;
//                                //for (_RowEndRidgeProportion = 2; _RowEndRidgeProportion <= 8; _RowEndRidgeProportion *= 2) {
//                                for (_RowEndRidgeProportion = 2; _RowEndRidgeProportion <= 4; _RowEndRidgeProportion++) {
//                                    _RowEndRidge = nrows * (_RowEndRidgeProportion - 1) / _RowEndRidgeProportion;
//                                    //for (_ColEndRidgeProportion = 2; _ColEndRidgeProportion <= 8; _ColEndRidgeProportion *= 2) {
//                                    //_ColEndRidgeProportion = _ColStartRidgeProportion;
//                                    for (_ColEndRidgeProportion = 2; _ColEndRidgeProportion <= 4; _ColEndRidgeProportion++) {
//                                        _ColEndRidge = ncols * (_ColEndRidgeProportion - 1) / _ColEndRidgeProportion;
//                                        createHippedRoofs(
//                                                _ResizedGrid2DSquareCellDouble,
//                                                nrows,
//                                                ncols,
//                                                _CellsizeDivideTwo,
//                                                _RowWithRidge,
//                                                (double) _RowRidgeHeight,
//                                                _ColStartRidge,
//                                                _ColEndRidge,
//                                                _ColWithRidge,
//                                                (double) _ColRidgeHeight,
//                                                _RowStartRidge,
//                                                _RowEndRidge);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    /**
     * Creates ouputs with varied ridge heights and varied dimensions (rows and
     * columns)
     *
     * @throws java.io.IOException
     */
    public void run_1()
            throws IOException {
        long nrows;
        long ncols;
        double _CellsizeDivideTwo = 0.5d;
        long _RowWithRidgeProportion;
        long _RowWithRidge;
        long _ColWithRidgeProportion;
        long _ColWithRidge;
        double _RowRidgeHeight;
        double _ColRidgeHeight;
        long _ColStartRidgeProportion;
        long _ColStartRidge;
        long _ColEndRidgeProportion;
        long _ColEndRidge;
        long _RowStartRidgeProportion;
        long _RowStartRidge;
        long _RowEndRidgeProportion;
        long _RowEndRidge;
        //for ( nrows = 5; nrows <= 80; nrows *= 2 ) {
        for (nrows = 11; nrows <= 11; nrows *= 2) {
            //for ( ncols = 4; ncols <= 64; ncols *= 2 ) {
            for (ncols = 9; ncols <= 9; ncols *= 2) {
                for (_RowWithRidgeProportion = 2; _RowWithRidgeProportion <= 8; _RowWithRidgeProportion *= 2) {
                    _RowWithRidge = nrows / _RowWithRidgeProportion;
                    for (_ColWithRidgeProportion = 2; _ColWithRidgeProportion <= 8; _ColWithRidgeProportion *= 2) {
                        _ColWithRidge = ncols / _ColWithRidgeProportion;
                        for (_RowRidgeHeight = 2; _RowRidgeHeight <= 8; _RowRidgeHeight *= 2) {
                            for (_ColRidgeHeight = 2; _ColRidgeHeight <= 8; _ColRidgeHeight *= 2) {
                                createGableRoofs(
                                        nrows,
                                        ncols,
                                        _CellsizeDivideTwo,
                                        _RowWithRidge,
                                        _ColWithRidge,
                                        (double) _RowRidgeHeight,
                                        (double) _ColRidgeHeight);
                                for (_RowStartRidgeProportion = 2; _RowStartRidgeProportion <= 8; _RowStartRidgeProportion *= 2) {
                                    _RowStartRidge = nrows / _RowStartRidgeProportion;
                                    for (_ColStartRidgeProportion = 2; _ColStartRidgeProportion <= 8; _ColStartRidgeProportion *= 2) {
                                        _ColStartRidge = ncols / _ColStartRidgeProportion;
                                        for (_RowEndRidgeProportion = 2; _RowEndRidgeProportion <= 8; _RowEndRidgeProportion *= 2) {
                                            _RowEndRidge = nrows * (_RowEndRidgeProportion - 1) / _RowEndRidgeProportion;
                                            for (_ColEndRidgeProportion = 2; _ColEndRidgeProportion <= 8; _ColEndRidgeProportion *= 2) {
                                                _ColEndRidge = ncols * (_ColEndRidgeProportion - 1) / _ColEndRidgeProportion;
                                                _CreateHippedRoofs(
                                                        nrows,
                                                        ncols,
                                                        _CellsizeDivideTwo,
                                                        _RowWithRidge,
                                                        (double) _RowRidgeHeight,
                                                        _ColStartRidge,
                                                        _ColEndRidge,
                                                        _ColWithRidge,
                                                        (double) _ColRidgeHeight,
                                                        _RowStartRidge,
                                                        _RowEndRidge);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void run_0()
            throws IOException {
        long nrows = 120;//14;//6L;//320L;

        long ncols = 80;//18;//4L;//640L;

        double _CellsizeDivideTwo = 0.5;
        long _RowWithRidge = nrows / 3;
        long _ColWithRidge = ncols / 3;
        //long _RowWithRidge = nrows / 2;
        //long _ColWithRidge = ncols / 2;
        double _RowRidgeHeight = 10.0d;
        double _ColRidgeHeight = 5.0d;
        createGableRoofs(
                nrows,
                ncols,
                _CellsizeDivideTwo,
                _RowWithRidge,
                _ColWithRidge,
                _RowRidgeHeight,
                _ColRidgeHeight);
        long _ColStartRidge = ncols / 4;
        long _ColEndRidge = ncols * 3 / 4;
        long _RowStartRidge = nrows / 4;
        long _RowEndRidge = nrows * 3 / 4;
        _CreateHippedRoofs(
                nrows,
                ncols,
                _CellsizeDivideTwo,
                _RowWithRidge,
                _RowRidgeHeight,
                _ColStartRidge,
                _ColEndRidge,
                _ColWithRidge,
                _ColRidgeHeight,
                _RowStartRidge,
                _RowEndRidge);
    }

    /**
     * Adds a ridge along a row for both hipped and gabled roofs.
     *
     * @param g .This is the grid that is to be created.
     * @param rowRidgeHeight .This is the height of the ridge along a row
     * @param ncols .This is the number of rows in the grid
     * @param nrows .This is the number of columns in the grid
     * @param rowWithRidge .This is the column that has the ridge
     * @param bottomRowRidgeTanAngle .This is the ridge height over the distance
     * between the ridge and the left edge of the roof.
     * @param topRowRidgeTanAngle .This is the ridge height over the distance
     * between the ridge and the right edge of the roof.
     *
     */
    public void addRowRidge(
            Grids_GridDouble g,
            double rowRidgeHeight,
            long ncols,
            long nrows,
            long rowWithRidge,
            double bottomRowRidgeTanAngle,
            double topRowRidgeTanAngle) {
        long row;
        long col;
        double h;
        //for (row = 1; row <= _RowWithRidge; row++) {
        for (row = 0; row < rowWithRidge; row++) {
            for (col = 0; col < ncols; col++) {
                h = bottomRowRidgeTanAngle * row;
                g.setCell(row, col, h);
            }
        }
        //for (row = nrows - 1; row >= _RowWithRidge; row--) {
        for (row = nrows - 1; row > rowWithRidge; row--) {
            for (col = 0; col < ncols; col++) {
                //h = _TopRowRidgeTanAngle * (nrows - row);
                h = topRowRidgeTanAngle * ((nrows - 1) - row);
                //System.out.println("" + _HeightToAdd );
                g.setCell(row, col, h);
            }
        }
        for (col = 0; col < ncols; col++) {
            g.setCell(rowWithRidge, col, rowRidgeHeight);
        }
    }

    /**
     * Adds a ridge along a column for both hipped and gabled roofs. Add col
     * ridge
     *
     * @param g .This is the grid that is to be created.
     * @param colRidgeHeight .This is the height of the ridge along a column
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of columns in the grid
     * @param colWithRidge .This is the column that has the ridge
     * @param leftColRidgeTanAngle .This is the ridge height over the distance
     * between the ridge and the left edge of the roof.
     * @param rightColRidgeTanAngle .This is the ridge height over the distance
     * between the ridge and the right edge of the roof.
     * @param hoome .This is to handle the out of memory error.
     */
    public void addColRidge(
            Grids_GridDouble g,
            double colRidgeHeight,
            long nrows,
            long ncols,
            long colWithRidge,
            double leftColRidgeTanAngle,
            double rightColRidgeTanAngle) {
        long col;
        long row;
        double h;
        for (row = 0; row < nrows; row++) {
            //for (col = 1; col <= _ColWithRidge; col++) {
            for (col = 0; col < colWithRidge; col++) {
                h = leftColRidgeTanAngle * col;
                g.setCell(row, col, h);
            }
        }
        for (row = 0; row < nrows; row++) {
            for (col = ncols - 1; col > colWithRidge; col--) {
                //_HeightToAdd = _RightColRidgeTanAngle * (ncols - col);
                h = rightColRidgeTanAngle * ((ncols - 1) - col);
                g.setCell(row, col, h);
            }
        }
        for (row = 0; row < nrows; row++) {
            g.setCell(row, colWithRidge, colRidgeHeight);
        }
    }

    /**
     * As
     * <code>createHippedRoofs(long,long,long,double,long,long,long,double,long,long)</code>
     * except with Resize and Rescale Output to fit _Dimensions
     *
     * @param rg .This is to output the resized grid
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of cols in the grid
     * @param halfCellsize .
     * @param rowWithRidge .This is the row that has the ridge
     * @param rowRidgeHeight .The height of ridge along a row.
     * @param colStartRidge .This is the column at which the ridge must start
     * for the ridge along a row
     * @param colEndRidge .This is the column at which the ridge must end for
     * the ridge along a row
     * @param colWithRidge .This is the column that has the ridge
     * @param colRidgeHeight .The height of ridge along a column
     * @param rowStartRidge .This is the row at which the ridge must start for
     * the ridge along a column
     * @param rowEndRidge .This is the row at which the ridge must end for the
     * ridge along a column
     * @throws java.io.IOException
     */
    public void createHippedRoofs(
            Grids_GridDouble rg,
            long nrows,
            long ncols,
            double halfCellsize,
            long rowWithRidge,
            double rowRidgeHeight,
            long colStartRidge,
            long colEndRidge,
            long colWithRidge,
            double colRidgeHeight,
            long rowStartRidge,
            long rowEndRidge)
            throws IOException {
        double heightToAdd;
        // For row ridge hips
        double bottomRowRidgeTanAngle = rowRidgeHeight
                / (double) (rowWithRidge - halfCellsize);
        double topRowRidgeTanAngle = rowRidgeHeight
                / (double) ((nrows - rowWithRidge) - halfCellsize);
        double colStartRidgeTanAngle = rowRidgeHeight
                / (double) (colStartRidge - halfCellsize);
        double colEndRidgeTanAngle = rowRidgeHeight
                / (double) ((ncols - colEndRidge) - halfCellsize);
        // For col ridge hips
        double leftColRidgeTanAngle = colRidgeHeight
                / (double) (colWithRidge - halfCellsize);
        double rightColRidgeTanAngle = colRidgeHeight
                / (double) ((ncols - colWithRidge) - halfCellsize);
        double rowStartRidgeTanAngle = colRidgeHeight
                / (double) (rowStartRidge - halfCellsize);
        double rowEndRidgeTanAngle = colRidgeHeight
                / (double) ((nrows - rowEndRidge) - halfCellsize);
        Grids_GridDouble g = (Grids_GridDouble) GridDoubleFactory.create(
                nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add row ridge
        addRowRidge(g, rowRidgeHeight, ncols, nrows, rowWithRidge,
                bottomRowRidgeTanAngle, topRowRidgeTanAngle);
        // Add one hipped end
        for (row = 0; row < nrows; row++) {
            for (col = 1; col <= colStartRidge; col++) {
                heightToAdd = Math.min(g.getCell(row, col - 1),
                        colStartRidgeTanAngle * col);
                g.setCell(row, col - 1, heightToAdd);
            }
        }
        // Add other hipped end
        for (row = 1; row < nrows; row++) {
            for (col = ncols - 1; col >= colEndRidge; col--) {
                heightToAdd = Math.min(g.getCell(row - 1, col),
                        colEndRidgeTanAngle * (ncols - col));
                g.setCell(row - 1, col, heightToAdd);
            }
        }
        g.setName("HippedRowRidgedRoof_" + nrows + "_" + ncols + "_"
                + rowRidgeHeight + "_" + rowWithRidge + "_" + colStartRidge
                + "_" + colEndRidge);
        resizeRescaleOutput(rg, g, Directory, ImageExporter, ImageTypes,
                ESRIAsciiGridExporter);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add col ridge
        addColRidge(g, colRidgeHeight, nrows, ncols, colWithRidge,
                leftColRidgeTanAngle, rightColRidgeTanAngle);
        // Add one hipped end
        for (row = 1; row <= rowStartRidge; row++) {
            for (col = 0; col < ncols; col++) {
                heightToAdd = Math.min(g.getCell(row - 1, col), rowStartRidgeTanAngle * row);
                g.setCell(row - 1, col, heightToAdd);
            }
        }
        // Add other hipped end
        for (row = nrows - 1; row >= rowEndRidge; row--) {
            for (col = 1; col < ncols; col++) {
                heightToAdd = Math.min(g.getCell(row, col - 1), rowEndRidgeTanAngle * (nrows - row));
                g.setCell(row, col - 1, heightToAdd);
            }
        }
        g.setName("HippedColRidgedRoof_" + nrows + "_" + ncols + "_"
                + colRidgeHeight + "_" + colWithRidge + "_" + rowStartRidge
                + "_" + rowEndRidge);
        resizeRescaleOutput(rg,
                g,
                Directory,
                ImageExporter,
                ImageTypes,
                ESRIAsciiGridExporter);
    }

    /**
     *
     * <a name="_CreateHippedRoofs(long,long,long,double,long,long,long,double,long,long)"></a>
     * http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#createHippedRoofs
     * file:///C:/Work/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#createHippedRoofs
     *
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of columns in the grid
     * @param cellsizeDivideTwo .This is to divide the size of the cell by 2
     * @param rowWithRidge .This is the row that has the ridge
     * @param rowRidgeHeight .This is the height of the ridge
     * @param colStartRidge .This is the column at which the ridge must start
     * for the ridge along a row
     * @param colEndRidge .This is the column at which the ridge must end for
     * the ridge along a row
     * @param colWithRidge .This is the column that has the ridge
     * @param colRidgeHeight .This is the height of the ridge
     * @param rowStartRidge .This is the row at which the ridge must start for
     * the ridge along a column
     * @param rowEndRidge .This is the row at which the ridge must end for the
     * ridge along a column
     * @throws java.io.IOException
     */
    public void _CreateHippedRoofs(
            long nrows,
            long ncols,
            double cellsizeDivideTwo,
            long rowWithRidge,
            double rowRidgeHeight,
            long colStartRidge,
            long colEndRidge,
            long colWithRidge,
            double colRidgeHeight,
            long rowStartRidge,
            long rowEndRidge)
            throws IOException {
        double heightToAdd;
        // For row ridge hips
        double bottomRowRidgeTanAngle = rowRidgeHeight / (double) (rowWithRidge - cellsizeDivideTwo);
        double topRowRidgeTanAngle = rowRidgeHeight / (double) ((nrows - rowWithRidge) - cellsizeDivideTwo);
        double colStartRidgeTanAngle = rowRidgeHeight / (double) (colStartRidge - cellsizeDivideTwo);
        double colEndRidgeTanAngle = rowRidgeHeight / (double) ((ncols - colEndRidge) - cellsizeDivideTwo);
        // For col ridge hips
        double leftColRidgeTanAngle = colRidgeHeight / (double) (colWithRidge - cellsizeDivideTwo);
        double rightColRidgeTanAngle = colRidgeHeight / (double) ((ncols - colWithRidge) - cellsizeDivideTwo);
        double rowStartRidgeTanAngle = colRidgeHeight / (double) (rowStartRidge - cellsizeDivideTwo);
        double rowEndRidgeTanAngle = colRidgeHeight / (double) ((nrows - rowEndRidge) - cellsizeDivideTwo);
        Grids_GridDouble g = (Grids_GridDouble) GridDoubleFactory.create(nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add row ridge
        addRowRidge(g, rowRidgeHeight, ncols, nrows, rowWithRidge,
                bottomRowRidgeTanAngle, topRowRidgeTanAngle);
        // Add one hipped end
        for (row = 0; row < nrows; row++) {
            for (col = 0; col <= colStartRidge; col++) {
                heightToAdd = Math.min(g.getCell(row, col),
                        colStartRidgeTanAngle * col);
                g.setCell(row, col, heightToAdd);
            }
        }
        // Add other hipped end
        for (row = 0; row < nrows; row++) {
            for (col = ncols - 1; col >= colEndRidge; col--) {
                heightToAdd = Math.min(g.getCell(row, col),
                        colEndRidgeTanAngle * (ncols - col));
                g.setCell(row, col, heightToAdd);
            }
        }
        g.setName("HippedRowRidgedRoof_" + nrows + "_" + ncols + "_"
                + rowRidgeHeight + "_" + rowWithRidge + "_" + colStartRidge
                + "_" + colEndRidge);
        output(g,
                //this,
                Directory,
                ImageExporter,
                ImageTypes,
                ESRIAsciiGridExporter);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add col ridge
        addColRidge(g, colRidgeHeight, nrows, ncols, colWithRidge,
                leftColRidgeTanAngle, rightColRidgeTanAngle);
        // Add one hipped end
        for (row = 0; row <= rowStartRidge; row++) {
            for (col = 0; col < ncols; col++) {
                heightToAdd = Math.min(g.getCell(row, col),
                        rowStartRidgeTanAngle * row);
                g.setCell(row, col, heightToAdd);
            }
        }
        // Add other hipped end
        for (row = nrows - 1; row >= rowEndRidge; row--) {
            for (col = 0; col < ncols; col++) {
                heightToAdd = Math.min(g.getCell(row, col),
                        rowEndRidgeTanAngle * (nrows - row));
                g.setCell(row, col, heightToAdd);
            }
        }
        g.setName("HippedColRidgedRoof_" + nrows + "_" + ncols + "_"
                + colRidgeHeight + "_" + colWithRidge + "_" + rowStartRidge
                + "_" + rowEndRidge);
        output(g,
                //this,
                Directory,
                ImageExporter,
                ImageTypes,
                ESRIAsciiGridExporter);
    }

    /**
     * Like the other createGableRoofs method except it does the conversion to a
     * standard dimension and rescales values from 1 to 10.0d
     *
     * @param rg. This is to output the resized grid
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of cols in the grid
     * @param cellsizeDivideTwo .This is to divide the size of the cell by 2
     * @param rowWithRidge .This is the row that contains the ridge
     * @param colWithRidge .This is the column that contains the ridge
     * @param rowRidgeHeight .This is the height of the row ridge
     * @param colRidgeHeight .This is the height of the column ridge
     * @throws java.io.IOException
     */
    public void _CreateGableRoofs(
            Grids_GridDouble rg,
            long nrows,
            long ncols,
            double cellsizeDivideTwo,
            long rowWithRidge,
            long colWithRidge,
            double rowRidgeHeight,
            double colRidgeHeight)
            throws IOException {
        double heightToAdd;
        double bottomRowRidgeTanAngle = rowRidgeHeight / (double) (rowWithRidge - cellsizeDivideTwo);
        double topRowRidgeTanAngle = rowRidgeHeight / (double) ((nrows - rowWithRidge) - cellsizeDivideTwo);
        double leftColRidgeTanAngle = colRidgeHeight / (double) (colWithRidge - cellsizeDivideTwo);
        double rightColRidgeTanAngle = colRidgeHeight / (double) ((ncols - colWithRidge) - cellsizeDivideTwo);
        Grids_GridDouble g = (Grids_GridDouble) GridDoubleFactory.create(nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add row ridge
        addRowRidge(g, rowRidgeHeight, ncols, nrows, rowWithRidge,
                bottomRowRidgeTanAngle, topRowRidgeTanAngle);
        g.setName("GableRowRidgedRoof_" + nrows + "_" + ncols + "_"
                + rowRidgeHeight + "_" + rowWithRidge);
        resizeRescaleOutput(rg, g, Directory, ImageExporter, ImageTypes,
                ESRIAsciiGridExporter);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add col ridge
        addColRidge(g, colRidgeHeight, nrows, ncols, colWithRidge,
                leftColRidgeTanAngle, rightColRidgeTanAngle);
        g.setName("GableColRidgedRoof_" + nrows + "_" + ncols + "_"
                + colRidgeHeight + "_" + colWithRidge);
        resizeRescaleOutput(rg, g, Directory, ImageExporter, ImageTypes,
                ESRIAsciiGridExporter);
    }

    /**
     * This method resizes the grid and rescales the values to between 1 and 10.
     *
     * @param rg This is the resized grid.
     * @param g This is the original grid.
     * @param outputDirectory This is the output directory of the resized and
     * rescaled grid
     * @param ie .
     * @param imageTypes This is the type of the output image.
     * @param eage .
     * @throws java.io.IOException
     */
    public void resizeRescaleOutput(
            Grids_GridDouble rg,
            Grids_GridDouble g,
            File outputDirectory,
            Grids_ImageExporter ie,
            String[] imageTypes,
            Grids_ESRIAsciiGridExporter eage)
            throws IOException {
        // Resize
        resize(rg, g);
        ge.getGrids().add(rg);
        // Rescale
        Grids_GridDouble ag = rescale(rg, null, 1.0d, 10.0d);
        ge.getGrids().add(ag);
        ag.setName(g.getName() + "ResizedRescaled");
        output(ag, Directory, ie, imageTypes, eage);
    }

    /**
     * Used to create synthetic gabled roof data.
     *
     * @param nrows This is the number of rows in the grid.
     * @param ncols This is the number of cols in the grid.
     * @param halfCellsize This is to divide the size of the cell by 2.
     * @param rowWithRidge This is the row that contains the ridge.
     * @param colWithRidge This is the column that contains the ridge.
     * @param rowRidgeHeight This is the height of the row ridge.
     * @param colRidgeHeight This is the height of the column ridge.
     * @throws java.io.IOException
     */
    public void createGableRoofs(
            long nrows,
            long ncols,
            double halfCellsize,
            long rowWithRidge,
            long colWithRidge,
            double rowRidgeHeight,
            double colRidgeHeight)
            throws IOException {
        //double heightToAdd;
        double bottomRowRidgeTanAngle = rowRidgeHeight
                / (double) (rowWithRidge - halfCellsize);
        double topRowRidgeTanAngle = rowRidgeHeight
                / (double) ((nrows - rowWithRidge) - halfCellsize);
        double leftColRidgeTanAngle = colRidgeHeight
                / (double) (colWithRidge - halfCellsize);
        double rightColRidgeTanAngle = colRidgeHeight
                / (double) ((ncols - colWithRidge) - halfCellsize);
        Grids_GridDouble g = (Grids_GridDouble) GridDoubleFactory.create(
                nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add row ridge
        addRowRidge(g, rowRidgeHeight, ncols, nrows, rowWithRidge,
                bottomRowRidgeTanAngle, topRowRidgeTanAngle);
        g.setName("GableRowRidgedRoof_" + nrows + "_" + ncols + "_"
                + rowRidgeHeight + "_" + rowWithRidge);
        output(g, Directory, ImageExporter, ImageTypes, ESRIAsciiGridExporter);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                g.setCell(row, col, 0);
            }
        }
        // Add col ridge
        addColRidge(g, rowRidgeHeight, nrows, ncols, colWithRidge,
                leftColRidgeTanAngle, rightColRidgeTanAngle);
        g.setName("GableColRidgedRoof_" + nrows + "_" + ncols + "_"
                + colRidgeHeight + "_" + colWithRidge);
        output(g, Directory, ImageExporter, ImageTypes, ESRIAsciiGridExporter);
    }

    /**
     * This method disaggregates the cells if the size of
     * _Grid2DSquareCellDouble is less than 32 x 64
     *
     * @param dimensions
     * @param g
     * @param handleOutOfMemoryError
     * @param gridFactory
     * @return
     *
     */
    public Grids_GridDouble disaggregate(
            Grids_Dimensions dimensions,
            Grids_GridDouble g,
            Grids_AbstractGridNumberFactory gridFactory) {
        long nRows = g.getNRows();
        long nCols = g.getNCols();
        Grids_GridDouble result = (Grids_GridDouble) gridFactory.create(
                nRows, nCols, dimensions);
        long row;
        long col;
        double x;
        double y;
        double value;
        for (row = 0; row < nRows; row++) {
            y = result.getCellYDouble(row);
            for (col = 0; col < nCols; col++) {
                x = result.getCellXDouble(col);
                value = g.getCell(x, y);
                result.setCell(row, col, value);
            }
        }
        return result;
    }

    /**
     * <a name="_Resize(Grid2DSquareCellDouble,Grid2DSquareCellDouble,boolean)"></a>
     * http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#resize
     * This method is to resize the grid _Grid2DSquareCellDouble
     *
     * @param rg This is the grid that will be resized to.
     * @param g This is the original grid.
     */
    public void resize(
            Grids_GridDouble rg,
            Grids_GridDouble g) {
        long nRows = g.getNRows();
        long resizeNRows = rg.getNRows();
        long nCols = g.getNCols();
        long resizeNCols = rg.getNCols();
        double nRowsD = (double) nRows;
        double outputNRowsD = (double) resizeNRows;
        double nColsD = (double) nCols;
        double outputNColsD = (double) resizeNCols;
        double rowProportion = nRowsD / outputNRowsD;
        double colProportion = nColsD / outputNColsD;
        double value;
        long row;
        long col;
        double x;
        double y;
        for (row = 0; row < resizeNRows; row++) {
            for (col = 0; col < resizeNCols; col++) {
                x = colProportion * col;
                y = rowProportion * row;
                value = g.getCell(x, y);
                rg.setCell(row, col, value);
            }
        }
    }

    public long get_Time() {
        return Time;
    }
}
