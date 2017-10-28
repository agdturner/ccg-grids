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
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;

public class Grids_GenerateRoofData
        extends Grids_Processor {

    private long _Time;
    boolean _HandleOutOfMemoryError;
    String _FileSeparator;
    int _MessageLength;
    int _FilenameLength;
    String _Message0;
    String _Message;
    String _Filename;
    Grids_ImageExporter _ImageExporter;
    String[] _ImageTypes;
    Grids_ESRIAsciiGridExporter _ESRIAsciiGridExporter;

    protected Grids_GenerateRoofData() {}

    public Grids_GenerateRoofData(Grids_Environment ge) {
        super(ge);
        this._Time = System.currentTimeMillis();
        this._HandleOutOfMemoryError = true;
        this._FileSeparator = System.getProperty("file.separator");
        this._MessageLength = 1000;
        this._FilenameLength = 1000;
        this._Message0 = this.ge.initString(_MessageLength, _HandleOutOfMemoryError);
        this._Message = this.ge.initString(_MessageLength, _HandleOutOfMemoryError);
        this._Filename = this.ge.initString(_FilenameLength, _HandleOutOfMemoryError);
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
            p._ImageExporter = null;
            p._ImageTypes = null;
            p._ESRIAsciiGridExporter = null;
            p._HandleOutOfMemoryError = true;
            //_GenerateRoofData.run_0();
            //_GenerateRoofData.run_1();
            //p.run_2();
        } catch (Error _Error) {
            _Error.printStackTrace();
        } catch (Exception _Exception) {
            _Exception.printStackTrace();
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
//        // Because we are rescaling in _CreateGableRoofs and _CreateHippedRoofs and the roofs are non complex this is arbitrary so long as it is a positive non-zero
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
//        _ResizedGrid2DSquareCellDouble = this.Grid2DSquareCellDoubleFactory.create(
//                Directory, _ResizedNRows, _ResizedNCols, _Dimensions, _HandleOutOfMemoryError);
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
////                        // Because we are rescaling in _CreateGableRoofs and _CreateHippedRoofs and the roofs are non complex we do not need these loops
////                        for ( _RowRidgeHeight = 2; _RowRidgeHeight <= 8; _RowRidgeHeight *= 2 ) {
////                            for ( _ColRidgeHeight = 2; _ColRidgeHeight <= 8; _ColRidgeHeight *= 2 ) {
//                        _CreateGableRoofs(
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
//                                        _CreateHippedRoofs(
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
     * Creates ouputs with varied ridge heights and varied dimensions (rows and columns)
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
                                _CreateGableRoofs(
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
        _CreateGableRoofs(
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
     * Add Row Ridge
     * <a name="_AddRowRidge(Grid2DSquareCellDouble,double,long,long,long,double,double,boolean,long,long,double)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_AddRowRidge
 This method adds a ridge along a row for both hipped and gabled roofs
     * @param _Grid2DSquareCellDouble .This is the grid that is to be created.
     * @param _RowRidgeHeight .This is the height of the ridge along a row
     * @param ncols .This is the number of rows in the grid
     * @param nrows .This is the number of columns in the grid
     * @param _RowWithRidge .This is the column that has the ridge
     * @param _BottomRowRidgeTanAngle .This is the ridge height over the distance between the ridge 
     * and the left edge of the roof.
     * @param _TopRowRidgeTanAngle .This is the ridge height over the distance between the ridge 
     * and the right edge of the roof.
     * @param _HandleOutOfMemoryError .This is to handle the out of memory error.
     * 
     */
    public void _AddRowRidge(
            Grids_GridDouble _Grid2DSquareCellDouble,
            double _RowRidgeHeight,
            long ncols,
            long nrows,
            long _RowWithRidge,
            double _BottomRowRidgeTanAngle,
            double _TopRowRidgeTanAngle,
            boolean _HandleOutOfMemoryError) {
        long row;
        long col;
        double _HeightToAdd;
        //for (row = 1; row <= _RowWithRidge; row++) {
        for (row = 0; row < _RowWithRidge; row++) {
            for (col = 0; col < ncols; col++) {
                _HeightToAdd = _BottomRowRidgeTanAngle * row;
                //_Grid2DSquareCellDouble.setCell(row - 1, col, _HeightToAdd, _HandleOutOfMemoryError);
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        //for (row = nrows - 1; row >= _RowWithRidge; row--) {
        for (row = nrows - 1; row > _RowWithRidge; row--) {
            for (col = 0; col < ncols; col++) {
                //_HeightToAdd = _TopRowRidgeTanAngle * (nrows - row);
                _HeightToAdd = _TopRowRidgeTanAngle * ((nrows - 1) - row);
                //System.out.println("" + _HeightToAdd );
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        for (col = 0; col < ncols; col++) {
            _Grid2DSquareCellDouble.setCell(_RowWithRidge, col, _RowRidgeHeight, _HandleOutOfMemoryError);
        }
    }

    /**
     * <a name="_AddColRidge(Grid2DSquareCellDouble,double,long,long,long,double,double,boolean,long,long,double)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_AddColRidge
 This method adds a ridge along a column for both hipped and gabled roofs.
     * Add col ridge
     * @param _Grid2DSquareCellDouble .This is the grid that is to be created.
     * @param _ColRidgeHeight .This is the height of the ridge along a column
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of columns in the grid
     * @param _ColWithRidge .This is the column that has the ridge
     * @param _LeftColRidgeTanAngle .This is the ridge height over the distance between the ridge 
     * and the left edge of the roof.
     * @param _RightColRidgeTanAngle .This is the ridge height over the distance between the ridge 
     * and the right edge of the roof.
     * @param _HandleOutOfMemoryError .This is to handle the out of memory error.
     */
    public void _AddColRidge(
            Grids_GridDouble _Grid2DSquareCellDouble,
            double _ColRidgeHeight,
            long nrows,
            long ncols,
            long _ColWithRidge,
            double _LeftColRidgeTanAngle,
            double _RightColRidgeTanAngle,
            boolean _HandleOutOfMemoryError) {
        long col;
        long row;
        double _HeightToAdd;
        for (row = 0; row < nrows; row++) {
            //for (col = 1; col <= _ColWithRidge; col++) {
            for (col = 0; col < _ColWithRidge; col++) {
                _HeightToAdd = _LeftColRidgeTanAngle * col;
                //_Grid2DSquareCellDouble.setCell(row, col - 1, _HeightToAdd, _HandleOutOfMemoryError);
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        for (row = 0; row < nrows; row++) {
            for (col = ncols - 1; col > _ColWithRidge; col--) {
                //_HeightToAdd = _RightColRidgeTanAngle * (ncols - col);
                _HeightToAdd = _RightColRidgeTanAngle * ((ncols - 1) - col);
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        for (row = 0; row < nrows; row++) {
            _Grid2DSquareCellDouble.setCell(row, _ColWithRidge, _ColRidgeHeight, _HandleOutOfMemoryError);
        }
    }

    /**
     * <a name="_CreateHippedRoofs(BigDecimal[],long,long,long,double,long,long,long,double,long,long)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_CreateHippedRoofs
 file:///C:/Work/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_CreateHippedRoofs
 As <code>_CreateHippedRoofs(long,long,long,double,long,long,long,double,long,long)</code> except with Resize and Rescale Output to fit _Dimensions
     * @param _ResizedGrid2DSquareCellDouble .This is to output the resized grid
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of cols in the grid
     * @param _CellsizeDivideTwo .
     * @param _RowWithRidge .This is the row that has the ridge
     * @param _RowRidgeHeight .The height of ridge along a row.
     * @param _ColStartRidge .This is the column at which the ridge must start for the ridge along a row
     * @param _ColEndRidge .This is the column at which the ridge must end for the ridge along a row
     * @param _ColWithRidge .This is the column that has the ridge
     * @param _ColRidgeHeight .The height of ridge along a column
     * @param _RowStartRidge .This is the row at which the ridge must start for the ridge along a column
     * @param _RowEndRidge .This is the row at which the ridge must end for the ridge along a column
     * @throws java.io.IOException
     */
    public void _CreateHippedRoofs(
            Grids_GridDouble _ResizedGrid2DSquareCellDouble,
            long nrows,
            long ncols,
            double _CellsizeDivideTwo,
            long _RowWithRidge,
            double _RowRidgeHeight,
            long _ColStartRidge,
            long _ColEndRidge,
            long _ColWithRidge,
            double _ColRidgeHeight,
            long _RowStartRidge,
            long _RowEndRidge)
            throws IOException {
        double _HeightToAdd;
        // For row ridge hips
        double _BottomRowRidgeTanAngle = _RowRidgeHeight / (double) (_RowWithRidge - _CellsizeDivideTwo);
        double _TopRowRidgeTanAngle = _RowRidgeHeight / (double) ((nrows - _RowWithRidge) - _CellsizeDivideTwo);
        double _ColStartRidgeTanAngle = _RowRidgeHeight / (double) (_ColStartRidge - _CellsizeDivideTwo);
        double _ColEndRidgeTanAngle = _RowRidgeHeight / (double) ((ncols - _ColEndRidge) - _CellsizeDivideTwo);
        // For col ridge hips
        double _LeftColRidgeTanAngle = _ColRidgeHeight / (double) (_ColWithRidge - _CellsizeDivideTwo);
        double _RightColRidgeTanAngle = _ColRidgeHeight / (double) ((ncols - _ColWithRidge) - _CellsizeDivideTwo);
        double _RowStartRidgeTanAngle = _ColRidgeHeight / (double) (_RowStartRidge - _CellsizeDivideTwo);
        double _RowEndRidgeTanAngle = _ColRidgeHeight / (double) ((nrows - _RowEndRidge) - _CellsizeDivideTwo);
        Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) Grid2DSquareCellDoubleFactory.create(nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add row ridge
        _AddRowRidge(
                _Grid2DSquareCellDouble,
                _RowRidgeHeight,
                ncols,
                nrows,
                _RowWithRidge,
                _BottomRowRidgeTanAngle,
                _TopRowRidgeTanAngle,
                _HandleOutOfMemoryError);
        // Add one hipped end
        for (row = 0; row < nrows; row++) {
            for (col = 1; col <= _ColStartRidge; col++) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row, col - 1, _HandleOutOfMemoryError), _ColStartRidgeTanAngle * col);
                _Grid2DSquareCellDouble.setCell(row, col - 1, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        // Add other hipped end
        for (row = 1; row < nrows; row++) {
            for (col = ncols - 1; col >= _ColEndRidge; col--) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row - 1, col, _HandleOutOfMemoryError), _ColEndRidgeTanAngle * (ncols - col));
                _Grid2DSquareCellDouble.setCell(row - 1, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        _Grid2DSquareCellDouble.setName("HippedRowRidgedRoof_" + nrows + "_" + ncols + "_" + _RowRidgeHeight + "_" + _RowWithRidge + "_" + _ColStartRidge + "_" + _ColEndRidge, _HandleOutOfMemoryError);
        _ResizeRescaleOutput(_ResizedGrid2DSquareCellDouble,
                _Grid2DSquareCellDouble,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add col ridge
        _AddColRidge(
                _Grid2DSquareCellDouble,
                _ColRidgeHeight,
                nrows,
                ncols,
                _ColWithRidge,
                _LeftColRidgeTanAngle,
                _RightColRidgeTanAngle,
                _HandleOutOfMemoryError);
        // Add one hipped end
        for (row = 1; row <= _RowStartRidge; row++) {
            for (col = 0; col < ncols; col++) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row - 1, col, _HandleOutOfMemoryError), _RowStartRidgeTanAngle * row);
                _Grid2DSquareCellDouble.setCell(row - 1, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        // Add other hipped end
        for (row = nrows - 1; row >= _RowEndRidge; row--) {
            for (col = 1; col < ncols; col++) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row, col - 1, _HandleOutOfMemoryError), _RowEndRidgeTanAngle * (nrows - row));
                _Grid2DSquareCellDouble.setCell(row, col - 1, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        _Grid2DSquareCellDouble.setName("HippedColRidgedRoof_" + nrows + "_" + ncols + "_" + _ColRidgeHeight + "_" + _ColWithRidge + "_" + _RowStartRidge + "_" + _RowEndRidge, _HandleOutOfMemoryError);
        _ResizeRescaleOutput(_ResizedGrid2DSquareCellDouble,
                _Grid2DSquareCellDouble,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
    }

    /**
     * 
     * <a name="_CreateHippedRoofs(long,long,long,double,long,long,long,double,long,long)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_CreateHippedRoofs
 file:///C:/Work/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_CreateHippedRoofs
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of columns in the grid
     * @param _CellsizeDivideTwo .This is to divide the size of the cell by 2
     * @param _RowWithRidge .This is the row that has the ridge
     * @param _RowRidgeHeight .This is the height of the ridge
     * @param _ColStartRidge .This is the column at which the ridge must start for the ridge along a row
     * @param _ColEndRidge .This is the column at which the ridge must end for the ridge along a row
     * @param _ColWithRidge .This is the column that has the ridge
     * @param _ColRidgeHeight .This is the height of the ridge
     * @param _RowStartRidge .This is the row at which the ridge must start for the ridge along a column
     * @param _RowEndRidge .This is the row at which the ridge must end for the ridge along a column
     * @throws java.io.IOException
     */
    public void _CreateHippedRoofs(
            long nrows,
            long ncols,
            double _CellsizeDivideTwo,
            long _RowWithRidge,
            double _RowRidgeHeight,
            long _ColStartRidge,
            long _ColEndRidge,
            long _ColWithRidge,
            double _ColRidgeHeight,
            long _RowStartRidge,
            long _RowEndRidge)
            throws IOException {
        double _HeightToAdd;
        // For row ridge hips
        double _BottomRowRidgeTanAngle = _RowRidgeHeight / (double) (_RowWithRidge - _CellsizeDivideTwo);
        double _TopRowRidgeTanAngle = _RowRidgeHeight / (double) ((nrows - _RowWithRidge) - _CellsizeDivideTwo);
        double _ColStartRidgeTanAngle = _RowRidgeHeight / (double) (_ColStartRidge - _CellsizeDivideTwo);
        double _ColEndRidgeTanAngle = _RowRidgeHeight / (double) ((ncols - _ColEndRidge) - _CellsizeDivideTwo);
        // For col ridge hips
        double _LeftColRidgeTanAngle = _ColRidgeHeight / (double) (_ColWithRidge - _CellsizeDivideTwo);
        double _RightColRidgeTanAngle = _ColRidgeHeight / (double) ((ncols - _ColWithRidge) - _CellsizeDivideTwo);
        double _RowStartRidgeTanAngle = _ColRidgeHeight / (double) (_RowStartRidge - _CellsizeDivideTwo);
        double _RowEndRidgeTanAngle = _ColRidgeHeight / (double) ((nrows - _RowEndRidge) - _CellsizeDivideTwo);
        Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) Grid2DSquareCellDoubleFactory.create(nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add row ridge
        _AddRowRidge(
                _Grid2DSquareCellDouble,
                _RowRidgeHeight,
                ncols,
                nrows,
                _RowWithRidge,
                _BottomRowRidgeTanAngle,
                _TopRowRidgeTanAngle,
                _HandleOutOfMemoryError);
        // Add one hipped end
        for (row = 0; row < nrows; row++) {
            for (col = 0; col <= _ColStartRidge; col++) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row, col, _HandleOutOfMemoryError), _ColStartRidgeTanAngle * col);
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        // Add other hipped end
        for (row = 0; row < nrows; row++) {
            for (col = ncols - 1; col >= _ColEndRidge; col--) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row, col, _HandleOutOfMemoryError), _ColEndRidgeTanAngle * (ncols - col));
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        _Grid2DSquareCellDouble.setName("HippedRowRidgedRoof_" + nrows + "_" + ncols + "_" + _RowRidgeHeight + "_" + _RowWithRidge + "_" + _ColStartRidge + "_" + _ColEndRidge, _HandleOutOfMemoryError);
        output(_Grid2DSquareCellDouble,
                //this,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add col ridge
        _AddColRidge(
                _Grid2DSquareCellDouble,
                _ColRidgeHeight,
                nrows,
                ncols,
                _ColWithRidge,
                _LeftColRidgeTanAngle,
                _RightColRidgeTanAngle,
                _HandleOutOfMemoryError);
        // Add one hipped end
        for (row = 0; row <= _RowStartRidge; row++) {
            for (col = 0; col < ncols; col++) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row, col, _HandleOutOfMemoryError), _RowStartRidgeTanAngle * row);
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        // Add other hipped end
        for (row = nrows - 1; row >= _RowEndRidge; row--) {
            for (col = 0; col < ncols; col++) {
                _HeightToAdd = Math.min(_Grid2DSquareCellDouble.getCell(row, col, _HandleOutOfMemoryError), _RowEndRidgeTanAngle * (nrows - row));
                _Grid2DSquareCellDouble.setCell(row, col, _HeightToAdd, _HandleOutOfMemoryError);
            }
        }
        _Grid2DSquareCellDouble.setName("HippedColRidgedRoof_" + nrows + "_" + ncols + "_" + _ColRidgeHeight + "_" + _ColWithRidge + "_" + _RowStartRidge + "_" + _RowEndRidge, _HandleOutOfMemoryError);
        output(_Grid2DSquareCellDouble,
                //this,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
    }

    /**
     * <a name="_CreateGableRoofs(Grid2DSquareCellDouble,long,long,double,long,long,double,double)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_CreateGableRoofs
 This method is like the other _CreateGableRoofs method except it does the
 conversion to a standard dimension and rescales values from 1 to 10.0d
     * @param _ResizedGrid2DSquareCellDouble. This is to output the resized grid
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of cols in the grid
     * @param _CellsizeDivideTwo .This is to divide the size of the cell by 2
     * @param _RowWithRidge .This is the row that contains the ridge
     * @param _ColWithRidge .This is the column that contains the ridge
     * @param _RowRidgeHeight .This is the height of the row ridge
     * @param _ColRidgeHeight .This is the height of the column ridge
     * @throws java.io.IOException
     */
    public void _CreateGableRoofs(
            Grids_GridDouble _ResizedGrid2DSquareCellDouble,
            long nrows,
            long ncols,
            double _CellsizeDivideTwo,
            long _RowWithRidge,
            long _ColWithRidge,
            double _RowRidgeHeight,
            double _ColRidgeHeight)
            throws IOException {
        double _HeightToAdd;
        double _BottomRowRidgeTanAngle = _RowRidgeHeight / (double) (_RowWithRidge - _CellsizeDivideTwo);
        double _TopRowRidgeTanAngle = _RowRidgeHeight / (double) ((nrows - _RowWithRidge) - _CellsizeDivideTwo);
        double _LeftColRidgeTanAngle = _ColRidgeHeight / (double) (_ColWithRidge - _CellsizeDivideTwo);
        double _RightColRidgeTanAngle = _ColRidgeHeight / (double) ((ncols - _ColWithRidge) - _CellsizeDivideTwo);
        Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) Grid2DSquareCellDoubleFactory.create(nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add row ridge
        _AddRowRidge(
                _Grid2DSquareCellDouble,
                _RowRidgeHeight,
                ncols,
                nrows,
                _RowWithRidge,
                _BottomRowRidgeTanAngle,
                _TopRowRidgeTanAngle,
                _HandleOutOfMemoryError);
        _Grid2DSquareCellDouble.setName("GableRowRidgedRoof_" + nrows + "_" + ncols + "_" + _RowRidgeHeight + "_" + _RowWithRidge, _HandleOutOfMemoryError);
        _ResizeRescaleOutput(_ResizedGrid2DSquareCellDouble,
                _Grid2DSquareCellDouble,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add col ridge
        _AddColRidge(
                _Grid2DSquareCellDouble,
                _ColRidgeHeight,
                nrows,
                ncols,
                _ColWithRidge,
                _LeftColRidgeTanAngle,
                _RightColRidgeTanAngle,
                _HandleOutOfMemoryError);
        _Grid2DSquareCellDouble.setName("GableColRidgedRoof_" + nrows + "_" + ncols + "_" + _ColRidgeHeight + "_" + _ColWithRidge, _HandleOutOfMemoryError);
        _ResizeRescaleOutput(_ResizedGrid2DSquareCellDouble,
                _Grid2DSquareCellDouble,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
    }

    /**
     * This method resizes the grid and rescales the values to between 1 and 10.
     * @param _ResizedGrid2DSquareCellDouble This is the resized grid.
     * @param _Grid2DSquareCellDouble This is the original grid.
     * @param _Output_Directory This is the output directory of the resized and rescaled grid
     * @param _ImageExporter .
     * @param _ImageTypes This is the type of the output image.
     * @param _ESRIAsciiGridExporter .
     * @param _HandleOutOfMemoryError This is to handle out of memory error.
     * @throws java.io.IOException
     */
    public void _ResizeRescaleOutput(
            Grids_GridDouble _ResizedGrid2DSquareCellDouble,
            Grids_GridDouble _Grid2DSquareCellDouble,
            File _Output_Directory,
            Grids_ImageExporter _ImageExporter,
            String[] _ImageTypes,
            Grids_ESRIAsciiGridExporter _ESRIAsciiGridExporter,
            boolean _HandleOutOfMemoryError)
            throws IOException {
        try {
            // Resize
            _Resize(_ResizedGrid2DSquareCellDouble, _Grid2DSquareCellDouble, _HandleOutOfMemoryError);
//            // Try aggregation
//            Grid2DSquareCellDouble _ResizedGrid2DSquareCellDouble = this.aggregate(
//                    _Grid2DSquareCellDouble,
//                    "SUM",
//                    _Dimensions,
//                    this.Grid2DSquareCellDoubleFactory,
//                    _HandleOutOfMemoryError);
//            // If not aggregation, then disaggregation
//            if (_ResizedGrid2DSquareCellDouble == null) {
//                _ResizedGrid2DSquareCellDouble = disaggregate(
//                        _Dimensions,
//                        _Grid2DSquareCellDouble,
//                        this.Grid2DSquareCellDoubleFactory,
//                        _HandleOutOfMemoryError);
//            }
            ge.getGrids().add(_ResizedGrid2DSquareCellDouble);
            // Rescale
            Grids_GridDouble _AggregatedGrid2DSquareCellDouble = rescale(
                    _ResizedGrid2DSquareCellDouble,
                    null,
                    1.0d,
                    10.0d,
                    _HandleOutOfMemoryError);
            ge.getGrids().add(_AggregatedGrid2DSquareCellDouble);
            _AggregatedGrid2DSquareCellDouble.setName(_Grid2DSquareCellDouble.getName(_HandleOutOfMemoryError) + "ResizedRescaled", _HandleOutOfMemoryError);
            output(_AggregatedGrid2DSquareCellDouble,
                    Directory,
                    _ImageExporter,
                    _ImageTypes,
                    _ESRIAsciiGridExporter,
                    _HandleOutOfMemoryError);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (_HandleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapChunks_Account(_HandleOutOfMemoryError);
                ge.initMemoryReserve(_HandleOutOfMemoryError);
                _ResizeRescaleOutput(
                        _ResizedGrid2DSquareCellDouble,
                        _Grid2DSquareCellDouble,
                        _Output_Directory,
                        _ImageExporter,
                        _ImageTypes,
                        _ESRIAsciiGridExporter,
                        _HandleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * <a name="_CreateGableRoofs(long,long,double,long,long,double,double)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_CreateGableRoofs
 This method is used to create gabled roof using the following parameters
     * @param nrows .This is the number of rows in the grid
     * @param ncols .This is the number of cols in the grid
     * @param _CellsizeDivideTwo .This is to divide the size of the cell by 2
     * @param _RowWithRidge .This is the row that contains the ridge
     * @param _ColWithRidge .This is the column that contains the ridge
     * @param _RowRidgeHeight .This is the height of the row ridge
     * @param _ColRidgeHeight .This is the height of the column ridge
     * @throws java.io.IOException
     */
    public void _CreateGableRoofs(
            long nrows,
            long ncols,
            double _CellsizeDivideTwo,
            long _RowWithRidge,
            long _ColWithRidge,
            double _RowRidgeHeight,
            double _ColRidgeHeight)
            throws IOException {
        double _HeightToAdd;
        double _BottomRowRidgeTanAngle = _RowRidgeHeight / (double) (_RowWithRidge - _CellsizeDivideTwo);
        double _TopRowRidgeTanAngle = _RowRidgeHeight / (double) ((nrows - _RowWithRidge) - _CellsizeDivideTwo);
        double _LeftColRidgeTanAngle = _ColRidgeHeight / (double) (_ColWithRidge - _CellsizeDivideTwo);
        double _RightColRidgeTanAngle = _ColRidgeHeight / (double) ((ncols - _ColWithRidge) - _CellsizeDivideTwo);
        Grids_GridDouble _Grid2DSquareCellDouble = (Grids_GridDouble) Grid2DSquareCellDoubleFactory.create(nrows, ncols);
        long row;
        long col;
        // Row ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add row ridge
        _AddRowRidge(
                _Grid2DSquareCellDouble,
                _RowRidgeHeight,
                ncols,
                nrows,
                _RowWithRidge,
                _BottomRowRidgeTanAngle,
                _TopRowRidgeTanAngle,
                _HandleOutOfMemoryError);
        _Grid2DSquareCellDouble.setName("GableRowRidgedRoof_" + nrows + "_" + ncols + "_" + _RowRidgeHeight + "_" + _RowWithRidge, _HandleOutOfMemoryError);
        output(_Grid2DSquareCellDouble,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
        // Col ridged roofs
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                _Grid2DSquareCellDouble.setCell(row, col, 0, _HandleOutOfMemoryError);
            }
        }
        // Add col ridge
        _AddColRidge(
                _Grid2DSquareCellDouble,
                _RowRidgeHeight,
                nrows,
                ncols,
                _ColWithRidge,
                _LeftColRidgeTanAngle,
                _RightColRidgeTanAngle,
                _HandleOutOfMemoryError);
        _Grid2DSquareCellDouble.setName("GableColRidgedRoof_" + nrows + "_" + ncols + "_" + _ColRidgeHeight + "_" + _ColWithRidge, _HandleOutOfMemoryError);
        output(_Grid2DSquareCellDouble,
                Directory,
                _ImageExporter,
                _ImageTypes,
                _ESRIAsciiGridExporter,
                _HandleOutOfMemoryError);
    }

    /**
     * This method disaggregates the cells if the size of _Grid2DSquareCellDouble is less than 32 x 64 
     *  
     * @param _HandleOutOfMemoryError
     * @param _Grid2DSquareCellFactory
     * @return 
     **/
    public Grids_GridDouble disaggregate(
            Grids_Dimensions _Dimensions,
            Grids_GridDouble _Grid2DSquareCellDouble,
            Grids_AbstractGridFactory _Grid2DSquareCellFactory,
            boolean _HandleOutOfMemoryError) {
        long _NRows = _Grid2DSquareCellDouble.getNRows(_HandleOutOfMemoryError);
        long _NCols = _Grid2DSquareCellDouble.getNCols(_HandleOutOfMemoryError);
        Grids_GridDouble result = (Grids_GridDouble) _Grid2DSquareCellFactory.create(
                _NRows,
                _NCols,
                _Dimensions);
        long row;
        long col;
        double _X_double;
        double _Y_double;
        double value;
        for (row = 0; row < _NRows; row++) {
            _Y_double = result.getCellYDouble(row, _HandleOutOfMemoryError);
            for (col = 0; col < _NCols; col++) {
                _X_double = result.getCellXDouble(col, _HandleOutOfMemoryError);
                value = _Grid2DSquareCellDouble.getCell(_X_double, _Y_double, _HandleOutOfMemoryError);
                result.setCell(row, col, value, _HandleOutOfMemoryError);
            }
        }
        return result;
    }

    /**
     * <a name="_Resize(Grid2DSquareCellDouble,Grid2DSquareCellDouble,boolean)"></a>
 http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/dist/javadoc/uk/ac/leeds/ccg/andyt/grids/examples/Grids_GenerateRoofData.html#_Resize
 This method is to resize the grid _Grid2DSquareCellDouble
     * @param _ResizeGrid2DSquareCell This is the grid that will be resized to.
     * @param _Grid2DSquareCellDouble This is the original grid.
     * @param _HandleOutOfMemoryError This is to handle the out of memory error
     * <a name="resize(Grid2DSquareCellDouble,Grid2DSquareCellDouble"></a>
     */
    public void _Resize(
            Grids_GridDouble _ResizeGrid2DSquareCell,
            Grids_GridDouble _Grid2DSquareCellDouble,
            boolean _HandleOutOfMemoryError) {
        long _NRows = _Grid2DSquareCellDouble.getNRows(_HandleOutOfMemoryError);
        long _ResizeNRows = _ResizeGrid2DSquareCell.getNRows(_HandleOutOfMemoryError);
        long _NCols = _Grid2DSquareCellDouble.getNCols(_HandleOutOfMemoryError);
        long _ResizeNCols = _ResizeGrid2DSquareCell.getNCols(_HandleOutOfMemoryError);
        double _NRows_double = (double) _NRows;
        double _OutputNRows_double = (double) _ResizeNRows;
        double _NCols_double = (double) _NCols;
        double _OutputNCols_double = (double) _ResizeNCols;
        //double _RowProportion =  _OutputNRows_double / _NRows_double;
        //double _ColProportion =  _OutputNCols_double / _NCols_double;
        double _RowProportion = _NRows_double / _OutputNRows_double;
        double _ColProportion = _NCols_double / _OutputNCols_double;
        double value;
        long row;
        long col;
        double _X;
        double _Y;
        for (row = 0; row < _ResizeNRows; row++) {
            for (col = 0; col < _ResizeNCols; col++) {
                _X = _ColProportion * col;
                _Y = _RowProportion * row;
                value = _Grid2DSquareCellDouble.getCell(_X, _Y, _HandleOutOfMemoryError);
                _ResizeGrid2DSquareCell.setCell(row, col, value, _HandleOutOfMemoryError);
            }
        }
    }

    public long get_Time() {
        return _Time;
    }
}
