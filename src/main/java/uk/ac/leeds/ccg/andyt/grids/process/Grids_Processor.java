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
package uk.ac.leeds.ccg.andyt.grids.process;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDouble;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellDoubleChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleChunkRAFFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellDoubleChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleChunk64CellMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleChunkJAIFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellDoubleChunkMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellInt;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellIntChunk64CellMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellIntChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCellIntChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellIntChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellIntChunkJAIFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellIntChunkMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellIntChunkRAFFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Grid2DSquareCellIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_GridStatistics0;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_GridStatistics1;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridExporter;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class holding methods for processing an individual Grids_AbstractGrid2DSquareCell
 or multiple Grid2DSquareCellAbstracts. TODO: Implement a general replace
 * method ( deprecate/replace mask methods ).
 */
public class Grids_Processor extends Grids_Object {

    /**
     * For storing the start time of the processing.
     */
    protected long startTime;
    /**
     * The log for recording progress and information about the processing.
     */
    protected PrintWriter log;
    /**
     * The log indentation (how many spaces before a log message line is
     * output).
     */
    protected int logIndentation;
    /**
     * Workspace directory for the processing.
     */
    protected File _Directory;
//    /** Abstracted to Grids_OutOfMemoryErrorHandler
//     * A collection of the Grid2dSquareCellAbstract Objects that
//     * may have data that can be swapped to release memory for
//     * processing.
//     */
//    public HashSet _AbstractGrid2DSquareCell_HashSet;
    /**
     * Default Grids_AbstractGrid2DSquareCellIntChunkFactory
     */
    public Grids_AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory;
    /**
     * Grids_Grid2DSquareCellIntChunk64CellMapFactory
     */
    public Grids_Grid2DSquareCellIntChunk64CellMapFactory _Grid2DSquareCellIntChunk64CellMapFactory;
    /**
     * Grids_Grid2DSquareCellIntChunkArrayFactory
     */
    public Grids_Grid2DSquareCellIntChunkArrayFactory _Grid2DSquareCellIntChunkArrayFactory;
    /**
     * Grids_Grid2DSquareCellIntChunkJAIFactory
     */
    public Grids_Grid2DSquareCellIntChunkJAIFactory _Grid2DSquareCellIntChunkJAIFactory;
    /**
     * Grids_Grid2DSquareCellIntChunkMapFactory
     */
    public Grids_Grid2DSquareCellIntChunkMapFactory _Grid2DSquareCellIntChunkMapFactory;
    /**
     * Grids_Grid2DSquareCellIntChunkRAFFactory
     */
    public Grids_Grid2DSquareCellIntChunkRAFFactory _Grid2DSquareCellIntChunkRAFFactory;
    /**
     * Grids_Grid2DSquareCellIntFactory
     */
    public Grids_Grid2DSquareCellIntFactory _Grid2DSquareCellIntFactory;
    /**
     * Default Grids_AbstractGrid2DSquareCellDoubleChunkFactory
     */
    public Grids_AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory;
    /**
     * Grids_Grid2DSquareCellDoubleChunk64CellMapFactory
     */
    public Grids_Grid2DSquareCellDoubleChunk64CellMapFactory _Grid2DSquareCellDoubleChunk64CellMapFactory;
    /**
     * Grids_Grid2DSquareCellDoubleChunkArrayFactory
     */
    public Grids_Grid2DSquareCellDoubleChunkArrayFactory _Grid2DSquareCellDoubleChunkArrayFactory;
    /**
     * Grids_Grid2DSquareCellDoubleChunkJAIFactory
     */
    public Grids_Grid2DSquareCellDoubleChunkJAIFactory _Grid2DSquareCellDoubleChunkJAIFactory;
    /**
     * Grids_Grid2DSquareCellDoubleChunkMapFactory
     */
    public Grids_Grid2DSquareCellDoubleChunkMapFactory _Grid2DSquareCellDoubleChunkMapFactory;
    /**
     * Grids_Grid2DSquareCellDoubleChunkRAFFactory
     */
    public Grids_Grid2DSquareCellDoubleChunkRAFFactory _Grid2DSquareCellDoubleChunkRAFFactory;
    /**
     * Grids_Grid2DSquareCellDoubleFactory
     */
    public Grids_Grid2DSquareCellDoubleFactory _Grid2DSquareCellDoubleFactory;
    /**
     * Grids_GridStatistics0
     */
    public Grids_GridStatistics0 _GridStatistics0;
    /**
     * Grids_GridStatistics1
     */
    public Grids_GridStatistics1 _GridStatistics1;
    /**
     * Grids_GridStatistics1
     */
    public Grids_AbstractGridStatistics _GridStatistics;

    /**
     * Creates a new instance of Grid2DSquareCellDoubleProcessor
     */
    public Grids_Processor() {
        //this( FileCreator.createNewFile() );
        //this( FileCreator.createNewFile( new File( System.getProperty( "tmpdir" ) ) ) );
        //this( FileCreator.createNewFile( new File( System.getProperty( "java.io.tmpdir" ) ) ) );
        this(new File(System.getProperty("java.io.tmpdir")));
    }

    /**
     * Creates a new instance of Grid2DSquareCellDoubleProcessor. By default the
     * logs are appended to the end of the log file if it exists. To overwrite
     * the log file use: Grid2DSquareCellDoubleProcessor( _Directory, false );
     *
     * @param _Directory
     */
    public Grids_Processor(
            File _Directory) {
        this(_Directory, true);
    }

    /**
     * Creates a new instance of Grid2DSquareCellDoubleProcessor. By default the
     * logs are appended to the end of the log file if it exists. To overwrite
     * the log file use: Grid2DSquareCellDoubleProcessor( _Directory, false );
     *
     * @param _Grids_Environment
     * @param _Directory
     */
    public Grids_Processor(
            Grids_Environment _Grids_Environment,
            File _Directory) {
        this(_Grids_Environment, _Directory, true);
    }

    /*
     * Creates a new instance of Grid2DSquareCellDoubleProcessor.
     **/
    public Grids_Processor(
            Grids_Environment ge) {
        this.ge = ge;
        this.startTime = System.currentTimeMillis();
        File workspace = new File(System.getProperty("java.io.tmpdir"));
        File logFile = new File(workspace, "log.txt");
        this._Directory = workspace;
        try {
            this.log = new PrintWriter(
                    new FileOutputStream(logFile, true));
        } catch (FileNotFoundException e) {
            int _MessageLength = 1000;
            String _Message0 = ge.initString(
                    _MessageLength,
                    ge.HandleOutOfMemoryErrorTrue);
            String _Message = ge.initString(
                    _MessageLength,
                    ge.HandleOutOfMemoryErrorTrue);
            _Message = e.getMessage();
            _Message = ge.println(
                    _Message,
                    _Message0,
                    ge.HandleOutOfMemoryErrorTrue);
            System.err.println(e.getMessage());
            //e.printStackTrace();
        }
        this.logIndentation = 0;
//        log(    this.logIndentation,
//                "log file " + _Directory.toString() + File.separator + "log.txt set up " + Calendar.getInstance().getTime().toString(),
//       this.handleOutOfMemoryErrorFalse );
        initFactories();
    }

    /**
     * Creates a new instance of Grid2DSquareCellDoubleProcessor. The log file
     * in _Directory will be overwritten if appendToLogFile is false.
     *
     * @param _Directory
     * @param appendToLogFile
     */
    public Grids_Processor(
            File _Directory,
            boolean appendToLogFile) {
        this(null, _Directory, appendToLogFile);
    }

    /**
     * Creates a new instance of Grid2DSquareCellDoubleProcessor. The log file
     * in _Directory will be overwritten if appendToLogFile is false.
     *
     * @param env
     * @param _Directory
     * @param appendToLogFile
     */
    public Grids_Processor(
            Grids_Environment env,
            File _Directory,
            boolean appendToLogFile) {
        try {
            this.ge = env;
            this.startTime = System.currentTimeMillis();
            File logFile;
            if (_Directory.exists()) {
                logFile = new File(_Directory, "log.txt");
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                if (appendToLogFile) {
                }
            } else {
                _Directory.mkdir();
                logFile = new File(_Directory, "log.txt");
                logFile.createNewFile();
            }
            this._Directory = _Directory;
            this.log = new PrintWriter(
                    new FileOutputStream(logFile, true));
            this.logIndentation = 0;
            log(this.logIndentation,
                    "log file " + _Directory.toString() + File.separator + "log.txt set up " + Calendar.getInstance().getTime().toString(),
                    env.HandleOutOfMemoryErrorTrue);
            initFactories();
        } catch (IOException ioe0) {
            int _MessageLength = 1000;
            String _Message0 = env.initString(
                    _MessageLength,
                    env.HandleOutOfMemoryErrorTrue);
            String _Message = env.initString(
                    _MessageLength,
                    env.HandleOutOfMemoryErrorTrue);
            _Message = ioe0.getMessage();
            _Message = env.println(
                    _Message,
                    _Message0,
                    env.HandleOutOfMemoryErrorTrue);
            System.err.println(ioe0.getMessage());
            //ioe0.printStackTrace();
        }
    }

    /**
     * Initialises All Factories.
     */
    private void initFactories() {
        initChunkFactories();
        initFactories(this._Directory);
        initGridStatistics();
    }

    /**
     * Initialises Non Chunk Factories.
     *
     * @param _Directory The directory factories will create _Directory
     * directories in.
     */
    private void initFactories(File workspace) {
        this._Grid2DSquareCellIntFactory = new Grids_Grid2DSquareCellIntFactory(
                new File(workspace, "Grid2DSquareCellInt"),
                ge,
                ge.HandleOutOfMemoryErrorTrue);
        this._Grid2DSquareCellDoubleFactory = new Grids_Grid2DSquareCellDoubleFactory(
                new File(workspace, "Grid2DSquareCellDouble"),
                ge,
                ge.HandleOutOfMemoryErrorTrue);
    }

    /**
     * Initialises Grid2DSquareCellChunk Factories.
     */
    private void initChunkFactories() {
        this._Grid2DSquareCellIntChunk64CellMapFactory = new Grids_Grid2DSquareCellIntChunk64CellMapFactory();
        this._Grid2DSquareCellIntChunkArrayFactory = new Grids_Grid2DSquareCellIntChunkArrayFactory();
        this._Grid2DSquareCellIntChunkJAIFactory = new Grids_Grid2DSquareCellIntChunkJAIFactory();
        this._Grid2DSquareCellIntChunkMapFactory = new Grids_Grid2DSquareCellIntChunkMapFactory();
        this._Grid2DSquareCellIntChunkRAFFactory = new Grids_Grid2DSquareCellIntChunkRAFFactory();
        //this._Grid2DSquareCellIntChunkFactory = _Grid2DSquareCellIntChunkMapFactory;
        this._Grid2DSquareCellIntChunkFactory = _Grid2DSquareCellIntChunkArrayFactory;
        this._Grid2DSquareCellDoubleChunk64CellMapFactory = new Grids_Grid2DSquareCellDoubleChunk64CellMapFactory();
        this._Grid2DSquareCellDoubleChunkArrayFactory = new Grids_Grid2DSquareCellDoubleChunkArrayFactory();
        this._Grid2DSquareCellDoubleChunkJAIFactory = new Grids_Grid2DSquareCellDoubleChunkJAIFactory();
        this._Grid2DSquareCellDoubleChunkMapFactory = new Grids_Grid2DSquareCellDoubleChunkMapFactory();
        this._Grid2DSquareCellDoubleChunkRAFFactory = new Grids_Grid2DSquareCellDoubleChunkRAFFactory();
        //this._Grid2DSquareCellDoubleChunkFactory = _Grid2DSquareCellDoubleChunkMapFactory;
        this._Grid2DSquareCellDoubleChunkFactory = _Grid2DSquareCellDoubleChunkArrayFactory;
    }

    /**
     * Initialises GridStatistics.
     */
    private void initGridStatistics() {
        this._GridStatistics0 = new Grids_GridStatistics0();
        this._GridStatistics1 = new Grids_GridStatistics1();
        this._GridStatistics = _GridStatistics0;
        //this._GridStatistics = _GridStatistics1;
    }

    /**
     * Returns a copy of this.startTime.
     *
     * @return
     */
    protected long getTime0() {
        return this.startTime;
    }

    /**
     * @return A copy of this.startTime.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public long getTime0(
            boolean handleOutOfMemoryError) {
        try {
            long result = getTime0();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return getTime0(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns a copy of this._Directory
     *
     * @param handleOutOfMemoryError
     * @return
     */
    public File get_Directory(
            boolean handleOutOfMemoryError) {
        try {
            File result = new File(this._Directory.toString());
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return get_Directory(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

//    /**
//     * Returns this._AbstractGrid2DSquareCell_HashSet
//     */
//    protected HashSet get_AbstractGrid2DSquareCell_HashSet() {
//        return this._AbstractGrid2DSquareCell_HashSet;
//    }
    /**
     * Returns a Grids_AbstractGrid2DSquareCell from
 this._AbstractGrid2DSquareCell_HashSet.
     *
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_AbstractGrid getGrid2DSquareCell(
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGrid result = getGrid2DSquareCell();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return getGrid2DSquareCell(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns a Grids_AbstractGrid2DSquareCell from
 this._AbstractGrid2DSquareCell_HashSet.
     *
     * @return
     */
    protected Grids_AbstractGrid getGrid2DSquareCell() {
        return ge.get_AbstractGrid2DSquareCell_HashSet().iterator().next();
    }

    /**
     * Changes _Directory to that passed in if it can be created. By default
     * this does not copy the logfile from the existing _Directory when it sets
     * up a log in the new location. To do this use: set_Directory( _Directory,
     * true )
     *
     *
     * @param _Directory The _Directory to change to.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    protected void set_Directory(
            File _Directory,
            boolean handleOutOfMemoryError) {
        try {
            set_Directory(_Directory);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                set_Directory(
                        _Directory,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Changes this._Directory, this._Grid2DSquareCellDoubleFactory._Directory,
     * this._Grid2DSquareCellIntFactory._Directory to _Directory. Does not copy
     * the logfile from the existing _Directory. To do this use: set_Directory(
     * _Directory, true )
     *
     * @param _Directory The _Directory to change to.
     */
    protected void set_Directory(
            File _Directory) {
        this._Directory = _Directory;
        this._Grid2DSquareCellDoubleFactory.set_Directory(_Directory);
        this._Grid2DSquareCellIntFactory.set_Directory(_Directory);
    }

    /**
     * Changes this._Directory to that passed in if it can be created. If
     * copyLogFile is true, this copies the logfile from the existing _Directory
     * and sets up the log to append in the new location.
     *
     * @param _Directory The _Directory to change to.
     * @param copyLogFile
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void set_Directory(
            File _Directory,
            boolean copyLogFile,
            boolean handleOutOfMemoryError) {
        try {
            boolean mkdirSuccess = false;
            File newLog = new File(_Directory, "log.txt");
            try {
                if (!_Directory.exists()) {
                    mkdirSuccess = _Directory.mkdir();
                    if (copyLogFile) {
                        copyAndSetUpNewLog(newLog, handleOutOfMemoryError);
                    } else {
                        newLog.createNewFile();
                        this.log = new PrintWriter(new FileOutputStream(newLog, true));
                    }
                } else {
                    if (!newLog.exists()) {
                        newLog.createNewFile();
                        this.log = new PrintWriter(new FileOutputStream(newLog, true));
                    } else {
                        if (copyLogFile) {
                            this.log = new PrintWriter(new FileOutputStream(newLog, true));
                        } else {
                            this.log = new PrintWriter(new FileOutputStream(newLog, false));
                        }
                    }
                }
            } catch (IOException ioe0) {
                System.err.println(ioe0.getMessage());
                //ioe0.printStackTrace();
            }
            this._Directory = _Directory;
            this._Grid2DSquareCellDoubleFactory.set_Directory(_Directory);
            this._Grid2DSquareCellIntFactory.set_Directory(_Directory);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                set_Directory(
                        _Directory,
                        copyLogFile,
                        handleOutOfMemoryError);
                return;
            }
            throw _OutOfMemoryError;
        }
    }

    /**
     * Copies and sets up a new log.
     *
     * @param newLog
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @throws java.io.IOException
     */
    public void copyAndSetUpNewLog(
            File newLog,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            this.log.flush();
            this.log.close();
            File workspace = get_Directory(handleOutOfMemoryError);
            File oldLog = new File(workspace, "log.txt");
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(oldLog));
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(newLog));
            for (int i = 0; i < oldLog.length(); i++) {
                bos.write(bis.read());
            }
            bos.flush();
            bos.close();
            bis.close();
            this.log = new PrintWriter(new FileOutputStream(newLog, true));
            log("log file copied from " + oldLog.toString() + " " + Calendar.getInstance().toString(),
                    handleOutOfMemoryError);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                copyAndSetUpNewLog(
                        newLog,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Writes string to log file and the console (standard output)
     *
     * @param string The message to log.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void log(
            String string,
            boolean handleOutOfMemoryError) {
        try {
            log(this.logIndentation,
                    string,
                    handleOutOfMemoryError);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                log(string,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Writes string to log file and the console (standard output) indenting
     * string by logIndentation amount of white-space.
     *
     * @param logIndentation The indentation of string.
     * @param string The message to log.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public final void log(
            int logIndentation,
            String string,
            boolean handleOutOfMemoryError) {
        try {
            log(logIndentation,
                    string);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                log(logIndentation,
                        string,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Writes string to log file and the console (standard output) indenting
     * string by logIndentation amount of white-space.
     *
     * @param logIndentation The indentation of string.
     * @param string The message to log.
     */
    protected void log(
            int logIndentation,
            String string) {
        boolean handleOutOfMemoryError = true;
        int _MessageLength = 1000;
        String _Message0 = ge.initString(
                _MessageLength,
                handleOutOfMemoryError);
        String _Message = ge.initString(_MessageLength, handleOutOfMemoryError);
        if (string.endsWith("}")) {
            logIndentation--;
            this.logIndentation--;
        }
        for (int i = 0; i < logIndentation; i++) {
            System.out.print(" ");
            this.log.write(" ");
        }
        _Message = string;
        _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
        this.log.write(string);
        this.log.write(System.getProperty("line.separator"));
        this.log.flush();
        if (string.endsWith("{")) {
            this.logIndentation++;
        }
    }

    /**
     * Returns the distance between a pair of coordinates
     *
     * @param x1 The x coordinte of one point.
     * @param y1 The y coordinte of one point.
     * @param x2 The x coordinte of another point.
     * @param y2 The y coordinte of another point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final double distance(
            double x1,
            double y1,
            double x2,
            double y2,
            boolean handleOutOfMemoryError) {
        try {
            double result = distance(
                    x1,
                    y1,
                    x2,
                    y2);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return distance(
                        x1,
                        y1,
                        x2,
                        y2,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the distance between a pair of coordinates
     *
     *
     * @param x1 The x coordinte of one point.
     * @param y1 The y coordinte of one point.
     * @param x2 The x coordinte of another point.
     * @param y2 The y coordinte of another point.
     * @param nChunkCols The number of Grid2DSquareCellChunkAbstract columns in
     * theAbstractGrid2DSquareCelll that's Grid2DSquareCellChunkAbstract could
     * be swapped if an OutOfMemoryError is thrown.
     * @param _ChunkRowIndex The chunk row index of the
     * Grid2DSquareCellChunkAbstract not to be swapped if an OutOfMemoryError is
     * thrown.
     * @param _ChunkColIndex The chunk column index of the
     * Grid2DSquareCellChunkAbstract not to be swapped if an OutOfMemoryError is
     * thrown.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final double distance(
            double x1,
            double y1,
            double x2,
            double y2,
            int nChunkCols,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = distance(x1, y1, x2, y2);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(
                        handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return distance(
                        x1, y1, x2, y2,
                        nChunkCols,
                        _ChunkRowIndex,
                        _ChunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the distance between a pair of coordinates
     *
     * @param x1 The x coordinte of one point.
     * @param y1 The y coordinte of one point.
     * @param x2 The x coordinte of another point.
     * @param y2 The y coordinte of another point.
     * @return
     */
    protected final double distance(
            double x1,
            double y1,
            double x2,
            double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0d) + Math.pow(y1 - y2, 2.0d));
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from
     * (x1,y1) to (x2,y2).
     *
     * @param x1 The x coordinte of one point.
     * @param y1 The y coordinte of one point.
     * @param x2 The x coordinte of another point.
     * @param y2 The y coordinte of another point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method and swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final double angle(
            double x1,
            double y1,
            double x2,
            double y2,
            boolean handleOutOfMemoryError) {
        try {
            double result = angle(
                    x1,
                    y1,
                    x2,
                    y2);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return angle(
                        x1,
                        y1,
                        x2,
                        y2,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from
     * (x1,y1) to (x2,y2).
     *
     *
     * @param x1 The x coordinte of one point.
     * @param y1 The y coordinte of one point.
     * @param x2 The x coordinte of another point.
     * @param y2 The y coordinte of another point.
     * @param nChunkCols The number of Grid2DSquareCellChunkAbstract columns in
     * theAbstractGrid2DSquareCelll that's Grid2DSquareCellChunkAbstract could
     * be swapped if an OutOfMemoryError is thrown.
     * @param _ChunkRowIndex The chunk row index of the
     * Grid2DSquareCellChunkAbstract not to be swapped if an OutOfMemoryError is
     * thrown.
     * @param _ChunkColIndex The chunk column index of the
     * Grid2DSquareCellChunkAbstract not to be swapped if an OutOfMemoryError is
     * thrown.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method and swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final double angle(
            double x1,
            double y1,
            double x2,
            double y2,
            int nChunkCols,
            int _ChunkRowIndex,
            int _ChunkColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = angle(
                    x1,
                    y1,
                    x2,
                    y2);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return angle(
                        x1, y1, x2, y2,
                        nChunkCols,
                        _ChunkRowIndex,
                        _ChunkColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the clockwise angle in radians to the y axis of the line from
     * (x1,y1) to (x2,y2).
     *
     * @param x1 The x coordinte of one point.
     * @param y1 The y coordinte of one point.
     * @param x2 The x coordinte of another point.
     * @param y2 The y coordinte of another point.
     * @return
     */
    protected final double angle(
            double x1,
            double y1,
            double x2,
            double y2) {
        double xdiff = x1 - x2;
        double ydiff = y1 - y2;
        double angle;
        if (xdiff == 0.0d && ydiff == 0.0d) {
            angle = -1.0d;
        } else {
            if (xdiff <= 0.0d) {
                if (xdiff == 0.0d) {
                    if (ydiff <= 0.0d) {
                        angle = 0.0d;
                    } else {
                        angle = Math.PI;
                    }
                } else {
                    if (ydiff <= 0.0d) {
                        if (ydiff == 0.0d) {
                            angle = Math.PI / 2.0d;
                        } else {
                            angle = Math.atan(Math.abs(xdiff / ydiff));
                        }
                    } else {
                        angle = Math.PI - Math.atan(Math.abs(xdiff / ydiff));
                    }
                }
            } else {
                if (ydiff <= 0.0d) {
                    if (ydiff == 0.0d) {
                        angle = 3.0d * Math.PI / 2.0d;
                    } else {
                        angle = (2.0d * Math.PI) - Math.atan(Math.abs(xdiff / ydiff));
                    }
                } else {
                    angle = Math.PI + Math.atan(Math.abs(xdiff / ydiff));
                }
            }
        }
        return angle;
    }

    /**
     * Modifies grid by setting to grid.noDataValue those cells coincident with
     * mask.noDataValue cells. Warning!!! The grid and mask are assumed to be
     * coincident and have the same origin.
     *
     *
     * @param grid TheAbstractGrid2DSquareCelll that masked.
     * @param mask TheAbstractGrid2DSquareCelll to use as a mask.
     * @param handleOutOfMemoryError
     */
    public void mask(
            Grids_AbstractGrid2DSquareCell grid,
            Grids_AbstractGrid2DSquareCell mask,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            ge.get_AbstractGrid2DSquareCell_HashSet().add(mask);
            int chunkNrows = grid.get_ChunkNRows(
                    handleOutOfMemoryError);
            int chunkNcols = grid.get_ChunkNCols(
                    handleOutOfMemoryError);
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            Grids_2D_ID_int chunkID;
            int thisChunkNrows = Integer.MIN_VALUE;
            int thisChunkNcols = Integer.MIN_VALUE;
            int _ChunkRowIndex = Integer.MIN_VALUE;
            int _ChunkColIndex = Integer.MIN_VALUE;
            int chunkCellRowIndex = Integer.MIN_VALUE;
            int chunkCellColIndex = Integer.MIN_VALUE;
            if (grid.getClass() == Grids_Grid2DSquareCellInt.class) {
                int _NoDataValue = ((Grids_Grid2DSquareCellInt) grid).getNoDataValue(handleOutOfMemoryError);
                if (mask.getClass() == Grids_Grid2DSquareCellInt.class) {
                    Grids_Grid2DSquareCellInt maskInt
                            = (Grids_Grid2DSquareCellInt) mask;
                    int maskNoDataValue = maskInt.getNoDataValue(
                            handleOutOfMemoryError);
                    int value;
                    Iterator iterator = maskInt.iterator(
                            handleOutOfMemoryError);
                    Grids_AbstractGrid2DSquareCellIntChunk maskIntChunk;
                    Grids_AbstractGrid2DSquareCellIntChunk resultChunk;
                    while (iterator.hasNext()) {
                        maskIntChunk
                                = (Grids_AbstractGrid2DSquareCellIntChunk) iterator.next();
                        chunkID = maskIntChunk.getChunkID(handleOutOfMemoryError);
                        thisChunkNrows = maskInt.getChunkNRows(
                                chunkID,
                                handleOutOfMemoryError);
                        thisChunkNcols = maskInt.getChunkNCols(
                                chunkID,
                                handleOutOfMemoryError);
                        _ChunkRowIndex = chunkID.getRow();
                        _ChunkColIndex = chunkID.getCol();
                        for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                            for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                                value = maskIntChunk.getCell(
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        maskNoDataValue,
                                        handleOutOfMemoryError);
                                if (value == maskNoDataValue) {
                                    grid.setCell(
                                            ((long) _ChunkRowIndex * (long) chunkNrows) + (long) chunkCellRowIndex,
                                            ((long) _ChunkColIndex * (long) chunkNcols) + (long) chunkCellColIndex,
                                            _NoDataValue,
                                            handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                } else {
                    // ( mask.getClass() == Grids_Grid2DSquareCellDouble.class )
                    Grids_Grid2DSquareCellDouble maskDouble
                            = (Grids_Grid2DSquareCellDouble) mask;
                    double maskNoDataValue = maskDouble.get_NoDataValue(
                            handleOutOfMemoryError);
                    double value;
                    Iterator iterator = maskDouble.iterator(
                            handleOutOfMemoryError);
                    Grids_AbstractGrid2DSquareCellDoubleChunk maskDoubleChunk;
                    Grids_AbstractGrid2DSquareCellIntChunk resultChunk;
                    while (iterator.hasNext()) {
                        maskDoubleChunk
                                = (Grids_AbstractGrid2DSquareCellDoubleChunk) iterator.next();
                        chunkID = maskDoubleChunk.getChunkID(handleOutOfMemoryError);
                        thisChunkNrows = maskDouble.getChunkNRows(
                                chunkID,
                                handleOutOfMemoryError);
                        thisChunkNcols = maskDouble.getChunkNCols(
                                chunkID,
                                handleOutOfMemoryError);
                        _ChunkRowIndex = chunkID.getRow();
                        _ChunkColIndex = chunkID.getCol();
                        for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                            for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                                value = maskDoubleChunk.getCell(
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        maskNoDataValue,
                                        handleOutOfMemoryError);
                                if (value == maskNoDataValue) {
                                    grid.setCell(
                                            ((long) _ChunkRowIndex * (long) chunkNrows) + (long) chunkCellRowIndex,
                                            ((long) _ChunkColIndex * (long) chunkNcols) + (long) chunkCellColIndex,
                                            _NoDataValue,
                                            handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                }
            } else {
                //( grid.getClass() == Grids_Grid2DSquareCellDouble.class ) {
                double resultNoDataValue
                        = ((Grids_Grid2DSquareCellDouble) grid).get_NoDataValue(
                                handleOutOfMemoryError);
                if (mask.getClass() == Grids_Grid2DSquareCellInt.class) {
                    Grids_Grid2DSquareCellInt maskInt
                            = (Grids_Grid2DSquareCellInt) mask;
                    int maskNoDataValue = maskInt.getNoDataValue(
                            handleOutOfMemoryError);
                    int value;
                    Iterator iterator = maskInt.iterator(
                            handleOutOfMemoryError);
                    Grids_AbstractGrid2DSquareCellIntChunk maskIntChunk;
                    Grids_AbstractGrid2DSquareCellIntChunk resultChunk;
                    while (iterator.hasNext()) {
                        maskIntChunk
                                = (Grids_AbstractGrid2DSquareCellIntChunk) iterator.next();
                        chunkID = maskIntChunk.getChunkID(handleOutOfMemoryError);
                        thisChunkNrows = maskInt.getChunkNRows(
                                chunkID,
                                handleOutOfMemoryError);
                        thisChunkNcols = maskInt.getChunkNCols(
                                chunkID,
                                handleOutOfMemoryError);
                        _ChunkRowIndex = chunkID.getRow();
                        _ChunkColIndex = chunkID.getCol();
                        for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                            for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                                value = maskIntChunk.getCell(
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        maskNoDataValue,
                                        handleOutOfMemoryError);
                                if (value == maskNoDataValue) {
                                    grid.setCell(
                                            ((long) _ChunkRowIndex * (long) chunkNrows) + (long) chunkCellRowIndex,
                                            ((long) _ChunkColIndex * (long) chunkNcols) + (long) chunkCellColIndex,
                                            resultNoDataValue, handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                } else {
                    // ( mask.getClass() == Grids_Grid2DSquareCellDouble.class )
                    Grids_Grid2DSquareCellDouble maskDouble
                            = (Grids_Grid2DSquareCellDouble) mask;
                    double maskNoDataValue = maskDouble.get_NoDataValue(
                            handleOutOfMemoryError);
                    double value;

//                    Iterator iterator = maskDouble.iterator(
//                            handleOutOfMemoryError);
                    Iterator iterator = maskDouble.getGrid2DSquareCellChunkIDHashSet(handleOutOfMemoryError).iterator();

                    Grids_AbstractGrid2DSquareCellDoubleChunk maskDoubleChunk;
                    Grids_AbstractGrid2DSquareCellIntChunk resultChunk;
                    while (iterator.hasNext()) {

//                        maskDoubleChunk =
//                                (Grids_AbstractGrid2DSquareCellDoubleChunk) iterator.next();
                        maskDoubleChunk = (Grids_AbstractGrid2DSquareCellDoubleChunk) mask.getGrid2DSquareCellChunk((Grids_2D_ID_int) iterator.next(), handleOutOfMemoryError);

                        chunkID = maskDoubleChunk.getChunkID(handleOutOfMemoryError);
                        thisChunkNrows = maskDouble.getChunkNRows(
                                chunkID,
                                handleOutOfMemoryError);
                        thisChunkNcols = maskDouble.getChunkNCols(
                                chunkID,
                                handleOutOfMemoryError);
                        _ChunkRowIndex = chunkID.getRow();
                        _ChunkColIndex = chunkID.getCol();
                        for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                            for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                                value = maskDoubleChunk.getCell(
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        maskNoDataValue,
                                        handleOutOfMemoryError);
                                if (value == maskNoDataValue) {
                                    grid.setCell(
                                            ((long) _ChunkRowIndex * (long) chunkNrows) + (long) chunkCellRowIndex,
                                            ((long) _ChunkColIndex * (long) chunkNcols) + (long) chunkCellColIndex,
                                            resultNoDataValue,
                                            handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                mask(
                        grid,
                        mask,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Modifies grid with the values of cells in the range [min,max] set to its
     * noDataValue. (Existing noDataValue cells in grid remain as noDataValue.)
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be masked.
     * @param min The minimum value in the range.
     * @param max The maximum value in the range.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void mask(
            Grids_AbstractGrid2DSquareCell grid,
            double min,
            double max,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            int chunkCellRowIndex;
            int chunkCellColIndex;
            int chunkNrows;
            int chunkNcols;
            int _ChunkRowIndex;
            int _ChunkColIndex;
            //int nChunkRows = grid.getNChunkRows( handleOutOfMemoryError );
            //int nChunkCols = grid.getNChunkCols( handleOutOfMemoryError );
            Grids_2D_ID_int chunkID;
            if (grid.getClass() == Grids_Grid2DSquareCellInt.class) {
                Grids_Grid2DSquareCellInt gridInt = (Grids_Grid2DSquareCellInt) grid;
                int noDataValue = gridInt.getNoDataValue(true);
                int value;
                Iterator iterator = gridInt.iterator(handleOutOfMemoryError);
                Grids_AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk;
                //Iterator gridChunkIterator;
                while (iterator.hasNext()) {
                    _Grid2DSquareCellIntChunk = (Grids_AbstractGrid2DSquareCellIntChunk) iterator.next();
                    chunkID = _Grid2DSquareCellIntChunk.getChunkID(handleOutOfMemoryError);
                    chunkNrows
                            = gridInt.getChunkNRows(chunkID, handleOutOfMemoryError);
                    chunkNcols
                            = gridInt.getChunkNCols(chunkID, handleOutOfMemoryError);
                    _ChunkRowIndex = chunkID.getRow();
                    _ChunkColIndex = chunkID.getCol();
                    for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                        for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                            value = gridInt.getCell(
                                    _Grid2DSquareCellIntChunk,
                                    _ChunkRowIndex,
                                    _ChunkColIndex,
                                    chunkCellRowIndex,
                                    chunkCellColIndex,
                                    handleOutOfMemoryError);
                            if (value >= min && value <= max) {
                                gridInt.setCell(
                                        _Grid2DSquareCellIntChunk,
                                        _ChunkRowIndex,
                                        _ChunkColIndex,
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        noDataValue,
                                        handleOutOfMemoryError);
                            }
                        }
                    }
                }
            } else {
                // ( grid.getClass() == Grids_Grid2DSquareCellDouble.class )
                Grids_Grid2DSquareCellDouble gridDouble = (Grids_Grid2DSquareCellDouble) grid;
                double noDataValue = gridDouble.get_NoDataValue(handleOutOfMemoryError);
                double value;
                Iterator iterator = grid.iterator(handleOutOfMemoryError);
                Grids_AbstractGrid2DSquareCellDoubleChunk gridChunk;
                //Iterator gridChunkIterator;
                while (iterator.hasNext()) {
                    gridChunk = (Grids_AbstractGrid2DSquareCellDoubleChunk) iterator.next();
                    chunkID = gridChunk.getChunkID(handleOutOfMemoryError);
                    chunkNrows = grid.getChunkNRows(chunkID, handleOutOfMemoryError);
                    chunkNcols = grid.getChunkNCols(chunkID, handleOutOfMemoryError);
                    _ChunkRowIndex = chunkID.getRow();
                    _ChunkColIndex = chunkID.getCol();
                    for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
                        for (chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
                            value = gridDouble.getCell(
                                    gridChunk,
                                    _ChunkRowIndex,
                                    _ChunkColIndex,
                                    chunkCellRowIndex,
                                    chunkCellColIndex,
                                    handleOutOfMemoryError);
                            if (value >= min && value <= max) {
                                gridDouble.setCell(
                                        gridChunk,
                                        _ChunkRowIndex,
                                        _ChunkColIndex,
                                        chunkCellRowIndex,
                                        chunkCellColIndex,
                                        noDataValue,
                                        handleOutOfMemoryError);
                            }
                        }
                    }
                }
            }
            //grid.set_Name( grid.getName() + "_mask" );
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                mask(
                        grid,
                        min,
                        max,
                        handleOutOfMemoryError);
            }
            throw a_OutOfMemoryError;
        }
    }

    /**
     * Modifies grid by setting to grid.noDataValue those cells that's centroids
     * intersect the rectangle given by: (xmin,ymin,xmax,ymax).
     *
     *
     * @param grid TheAbstractGrid2DSquareCelll to be masked
     * @param xmin The minimum x-coordinate of the masking rectangle.
     * @param ymin The minimum y-coordinate of the masking rectangle.
     * @param xmax The maximum x-coordinate of the masking rectangle.
     * @param ymax The maximum y-coordinate of the masking rectangle.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void mask(
            Grids_AbstractGrid2DSquareCell grid,
            double xmin,
            double ymin,
            double xmax,
            double ymax,
            boolean handleOutOfMemoryError) {
        try {
            mask(
                    grid,
                    grid.getCellRowIndex(ymax, handleOutOfMemoryError),
                    grid.getCellColIndex(xmin, handleOutOfMemoryError),
                    grid.getCellRowIndex(ymin, handleOutOfMemoryError),
                    grid.getCellColIndex(xmax, handleOutOfMemoryError),
                    handleOutOfMemoryError);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                mask(
                        grid,
                        xmin,
                        ymin,
                        xmax,
                        ymax,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Modifies grid by setting to grid.noDataValue those cells that intersect
     * the rectangle given by:
     * (startRowIndex,startColIndex,endRowIndex,endColIndex).
     *
     * @param grid the Grids_Grid2DSquareCellDouble to be masked
     * @param startRowIndex the index of the first row to be masked
     * @param startColIndex the index of the first column to be masked
     * @param endRowIndex the index of the final row to be masked
     * @param endColIndex the index of the final column to be masked
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void mask(
            Grids_AbstractGrid2DSquareCell grid,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGrid2DSquareCell result;
            long rowIndex;
            long colIndex;
            if (grid.getClass() == Grids_Grid2DSquareCellInt.class) {
                Grids_Grid2DSquareCellInt gridInt = (Grids_Grid2DSquareCellInt) grid;
                int noDataValue = gridInt.getNoDataValue(handleOutOfMemoryError);
                int value;
                for (rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++) {
                    for (colIndex = startColIndex; colIndex <= endColIndex; colIndex++) {
                        gridInt.setCell(
                                rowIndex,
                                colIndex,
                                noDataValue,
                                handleOutOfMemoryError);
                    }
                }
                result = gridInt;
            } else {
                // ( grid.getClass() == Grids_Grid2DSquareCellDouble.class )
                Grids_Grid2DSquareCellDouble gridDouble = (Grids_Grid2DSquareCellDouble) grid;
                double noDataValue = gridDouble.get_NoDataValue(handleOutOfMemoryError);
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int();
                HashSet _ChunkIDHashSet = new HashSet();
                _ChunkIDHashSet.add(chunkID);
                HashMap _Grid2DSquareCell_ChunkIDHashSet_HashMap = new HashMap();
                _Grid2DSquareCell_ChunkIDHashSet_HashMap.put(grid, _ChunkIDHashSet);
                Grids_AbstractGrid2DSquareCellDoubleChunk chunk;
                double value = 0.0d;
//                // TODO: Chunk Processing would be better! the following is a start, but need some intersection type method...
//                int _ChunkCellRowIndex = 0;
//                int _ChunkCellColIndex = 0;
//                //int _Start_ChunkCellRowIndex = 0;
//                //int _Start_ChunkCellColIndex = 0;
//                //int _End_ChunkCellRowIndex = 0;
//                //int _End_ChunkCellColIndex = 0;
//                HashSet _ChunkIDs = grid.getChunkIDs( // Need a method for this...
//                        startRowIndex,
//                        startColIndex,
//                        endRowIndex,
//                        endColIndex,
//                        handleOutOfMemoryError );
//                Iterator _Iterator = _ChunkIDs.iterator();
//                while ( _Iterator.hasNext() ) {
//                    _ChunkID = ( ID ) _Iterator.next();
//                    chunk = gridDouble.getGrid2DSquareCellDoubleChunk(
//                            _ChunkID,
//                            handleOutOfMemoryError,
//                            _Grid2DSquareCell_ChunkIDHashSet_HashMap );
//                    //_Start_ChunkCellRowIndex = chunk.grid.getChunkCellRowIndex( startRowIndex );
//                    for ( rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex ++ ) {
//                        _ChunkCellRowIndex = grid.getChunkCellRowIndex( 
//                                rowIndex, 
//                                handleOutOfMemoryError,
//                                _Grid2DSquareCell_ChunkIDHashSet_HashMap );
//                        for ( colIndex = startColIndex; colIndex <= endColIndex; colIndex ++ ) {
//                            _ChunkCellColIndex = grid.getChunkCellColIndex( 
//                                    colIndex, 
//                                    handleOutOfMemoryError,
//                                    _Grid2DSquareCell_ChunkIDHashSet_HashMap );
//                            if ( chunk.inChunk( _ChunkCellRowIndex, _ChunkCellColIndex, handleOutOfMemoryError, _Grid2DSquareCell_ChunkIDHashSet_HashMap ) ) {
//                                // Is this the best setCell method to call?
//                                gridDouble.setCell(
//                                        rowIndex,
//                                        colIndex,
//                                        noDataValue,
//                                        handleOutOfMemoryError,
//                                        _Grid2DSquareCell_ChunkIDHashSet_HashMap );
//                            }
//                        }
//                    }
//                }
                for (rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++) {
                    for (colIndex = startColIndex; colIndex <= endColIndex; colIndex++) {
                        gridDouble.setCell(
                                rowIndex,
                                colIndex,
                                noDataValue,
                                handleOutOfMemoryError);
                    }
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                mask(
                        grid,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param grid
     * @return a new Grids_Grid2DSquareCellDouble Values are either linearly rescaled
 into the range [min,max]. Or some log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "log" then a log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     *
     * @TODO Improve Memory Handling
     */
    public Grids_Grid2DSquareCellDouble rescale(
            Grids_AbstractGrid2DSquareCell grid,
            String type,
            double min,
            double max,
            boolean handleOutOfMemoryError) {
        if (grid instanceof Grids_Grid2DSquareCellDouble) {
            return rescale((Grids_Grid2DSquareCellDouble) grid,
                    type,
                    min,
                    max,
                    handleOutOfMemoryError);
        } else {
            if (!(grid instanceof Grids_Grid2DSquareCellInt)) {
                throw new UnsupportedOperationException();
            }
            return rescale((Grids_Grid2DSquareCellInt) grid,
                    type,
                    min,
                    max,
                    handleOutOfMemoryError);
        }
    }

    /**
     * @param grid
     * @return a new Grids_Grid2DSquareCellDouble Values are either linearly rescaled
 into the range [min,max]. Or some log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "log" then a log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     *
     * @TODO Improve Memory Handling
     * @TODO Log rescaling implementation is not brilliant and could be
     * parametrised.
     */
    public Grids_Grid2DSquareCellDouble rescale(
            Grids_Grid2DSquareCellDouble grid,
            String type,
            double min,
            double max,
            boolean handleOutOfMemoryError) {
        try {
            int row;
            int col;
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            int nChunkCols = grid.get_NChunkCols(handleOutOfMemoryError);
            int nChunkRows = grid.get_NChunkRows(handleOutOfMemoryError);
            
            int chunkNCols = grid.get_ChunkNCols(handleOutOfMemoryError);
            int chunkNRows = grid.get_ChunkNRows(handleOutOfMemoryError);
            _Grid2DSquareCellDoubleFactory.set_ChunkNRows(chunkNRows);
            _Grid2DSquareCellDoubleFactory.set_ChunkNCols(chunkNCols);
            
            double noDataValue = grid.get_NoDataValue(handleOutOfMemoryError);
            double range = max - min;
            Grids_AbstractGridStatistics stats = grid.getGridStatistics(handleOutOfMemoryError);
            double minGrid = stats.getMinDouble(handleOutOfMemoryError);
            double maxGrid = stats.getMaxDouble(handleOutOfMemoryError);
            double rangeGrid = maxGrid - minGrid;
            double value = noDataValue;
            Grids_Grid2DSquareCellDouble outputGrid;
            //outputGrid = (Grids_Grid2DSquareCellDouble) _Grid2DSquareCellDoubleFactory.create(grid);
            outputGrid = (Grids_Grid2DSquareCellDouble) _Grid2DSquareCellDoubleFactory.create(
                    _Directory, grid, 0, 0, nrows - 1, ncols - 1,
                    ge, handleOutOfMemoryError);
            outputGrid.set_Name(grid.get_Name(handleOutOfMemoryError), handleOutOfMemoryError);
            System.out.println(outputGrid.toString(handleOutOfMemoryError));
            ge.get_AbstractGrid2DSquareCell_HashSet().add(outputGrid);
            int chunkRowIndex = 0;
            int chunkColIndex = 0;
            int chunkCellRowIndex = 0;
            int chunkCellColIndex = 0;
            if (type == null) {
                // if range of either input or output range is zero return min for all non noDataValues
                if (rangeGrid == 0.0d || range == 0.0d) {
                    // Better to go through chunks rather than rows. Though it 
                    // does assume that the structure of the grid and outputGrid 
                    // are the same.
                    for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                    chunkRowIndex, chunkColIndex);
                            int thisChunkNCols = grid.get_ChunkNCols(
                                    chunkColIndex, handleOutOfMemoryError, chunkID);
                            int thisChunkNRows = grid.get_ChunkNRows(
                                    chunkRowIndex, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellDoubleChunk gridChunk;
                            gridChunk = grid.getGrid2DSquareCellDoubleChunk(
                                    chunkID, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellDoubleChunk outputGridChunk;
                            outputGridChunk = outputGrid.getGrid2DSquareCellDoubleChunk(
                                    chunkID, handleOutOfMemoryError);
                            for (chunkCellRowIndex = 0; chunkCellRowIndex < thisChunkNRows; chunkCellRowIndex++) {
                                for (chunkCellColIndex = 0; chunkCellColIndex < thisChunkNCols; chunkCellColIndex++) {
                                    try {
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                grid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    }
                                    try {
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                outputGrid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Better to go through chunks rather than rows. Though it 
                    // does assume that the structure of the grid and outputGrid 
                    // are the same.
                    for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                    chunkRowIndex, chunkColIndex);
                            int thisChunkNCols = grid.get_ChunkNCols(
                                    chunkColIndex, handleOutOfMemoryError, chunkID);
                            int thisChunkNRows = grid.get_ChunkNRows(
                                    chunkRowIndex, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellDoubleChunk gridChunk;
                            gridChunk = grid.getGrid2DSquareCellDoubleChunk(
                                    chunkID, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellDoubleChunk outputGridChunk;
                            outputGridChunk = outputGrid.getGrid2DSquareCellDoubleChunk(
                                    chunkID, handleOutOfMemoryError);
                            for (chunkCellRowIndex = 0; chunkCellRowIndex < thisChunkNRows; chunkCellRowIndex++) {
                                for (chunkCellColIndex = 0; chunkCellColIndex < thisChunkNCols; chunkCellColIndex++) {
                                    try {
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                grid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    }
                                    try {
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    (((value - minGrid) / rangeGrid) * range) + min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                outputGrid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    (((value - minGrid) / rangeGrid) * range) + min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                outputGrid.set_Name( grid.get_Name(handleOutOfMemoryError)+ "_linearRescale", handleOutOfMemoryError );
                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            } else {
                // @TODO this is not a brilliant implementation it could perhaps 
                // do with parameterising the range etc...
                if (type.equalsIgnoreCase("log")) {
                    outputGrid = rescale(
                            outputGrid,
                            null,
                            1.0d,
                            1000000.0d,
                            handleOutOfMemoryError);
                    // Probably better to do this by chunks
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            try {
                                value = grid.getCell(
                                        row,
                                        col,
                                        handleOutOfMemoryError);
                            } catch (OutOfMemoryError oome) {
                                ge.clear_MemoryReserve();
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                        (int) row,
                                        (int) col);
                                long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                        grid, chunkID, handleOutOfMemoryError);
                                if (swap < 1L) {
                                    throw oome;
                                }
                                ge.init_MemoryReserve(handleOutOfMemoryError);
                                value = outputGrid.getCell(
                                        row,
                                        col,
                                        handleOutOfMemoryError);
                            }
                            try {
                                if (value != noDataValue) {
                                    outputGrid.setCell(
                                            row,
                                            col,
                                            Math.log(value),
                                            handleOutOfMemoryError);
                                }
                            } catch (OutOfMemoryError oome) {
                                ge.clear_MemoryReserve();
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                        (int) row,
                                        (int) col);
                                long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                        outputGrid, chunkID, handleOutOfMemoryError);
                                if (swap < 1L) {
                                    throw oome;
                                }
                                ge.init_MemoryReserve(handleOutOfMemoryError);
                                if (value != noDataValue) {
                                    outputGrid.setCell(
                                            row,
                                            col,
                                            Math.log(value),
                                            handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                    outputGrid = rescale(
                            outputGrid,
                            null,
                            min,
                            max,
                            handleOutOfMemoryError);
                    outputGrid.set_Name( grid.get_Name(handleOutOfMemoryError)+ "_logRescale", handleOutOfMemoryError );
                    ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                } else {
                    System.out.println("Unable to rescale: type " + type + "not recognised. Returning a Grid2DSquareCellDouble of _InputGrid.");
                }
            }
            return outputGrid;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return rescale(
                        grid,
                        type,
                        min,
                        max,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @param grid
     * @return a new Grids_Grid2DSquareCellDouble Values are either linearly rescaled
 into the range [min,max]. Or some log rescaling is done
     * @param type If type == null then a linear rescale is done. If type ==
     * "log" then a log rescale is done.
     * @param min The minimum value in the rescaled range.
     * @param max The maximum value in the rescaled range.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     *
     * @TODO Improve Memory Handling
     * @TODO Log rescaling implementation is not brilliant and could be
     * parametrised.
     */
    public Grids_Grid2DSquareCellDouble rescale(
            Grids_Grid2DSquareCellInt grid,
            String type,
            double min,
            double max,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            int nChunkCols = grid.get_NChunkCols(handleOutOfMemoryError);
            int nChunkRows = grid.get_NChunkCols(handleOutOfMemoryError);
            int noDataValue = grid.getNoDataValue(handleOutOfMemoryError);
            double range = max - min;
            Grids_AbstractGridStatistics stats = grid.getGridStatistics(handleOutOfMemoryError);
            double minGrid = stats.getMinDouble(handleOutOfMemoryError);
            double maxGrid = stats.getMaxDouble(handleOutOfMemoryError);
            double rangeGrid = maxGrid - minGrid;
            double value = noDataValue;
            Grids_Grid2DSquareCellDouble outputGrid;
            outputGrid = (Grids_Grid2DSquareCellDouble) _Grid2DSquareCellDoubleFactory.create(grid);
            outputGrid.set_Name(grid.get_Name(handleOutOfMemoryError), handleOutOfMemoryError);
            ge.get_AbstractGrid2DSquareCell_HashSet().add(outputGrid);
            int chunkRowIndex = 0;
            int chunkColIndex = 0;
            int chunkCellRowIndex = 0;
            int chunkCellColIndex = 0;
            if (type == null) {
                // if range of either input or output range is zero return min for all non noDataValues
                if (rangeGrid == 0.0d || range == 0.0d) {
                    // Better to go through chunks rather than rows. Though it 
                    // does assume that the structure of the grid and outputGrid 
                    // are the same.
                    for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                    chunkRowIndex, chunkColIndex);
                            int chunkNCols = grid.get_ChunkNCols(
                                    chunkColIndex, handleOutOfMemoryError, chunkID);
                            int chunkNRows = grid.get_ChunkNRows(
                                    chunkRowIndex, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellIntChunk gridChunk;
                            gridChunk = grid.getGrid2DSquareCellIntChunk(
                                    chunkID, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellDoubleChunk outputGridChunk;
                            outputGridChunk = outputGrid.getGrid2DSquareCellDoubleChunk(
                                    chunkID, handleOutOfMemoryError);
                            for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNRows; chunkCellRowIndex++) {
                                for (chunkCellColIndex = 0; chunkCellColIndex < chunkNCols; chunkCellColIndex++) {
                                    try {
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                grid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    }
                                    try {
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                outputGrid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Better to go through chunks rather than rows. Though it 
                    // does assume that the structure of the grid and outputGrid 
                    // are the same.
                    for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                        for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                    chunkRowIndex, chunkColIndex);
                            int chunkNCols = grid.get_ChunkNCols(
                                    chunkColIndex, handleOutOfMemoryError, chunkID);
                            int chunkNRows = grid.get_ChunkNRows(
                                    chunkRowIndex, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellIntChunk gridChunk;
                            gridChunk = grid.getGrid2DSquareCellIntChunk(
                                    chunkID, handleOutOfMemoryError);
                            Grids_AbstractGrid2DSquareCellDoubleChunk outputGridChunk;
                            outputGridChunk = outputGrid.getGrid2DSquareCellDoubleChunk(
                                    chunkID, handleOutOfMemoryError);
                            for (chunkCellRowIndex = 0; chunkCellRowIndex < chunkNRows; chunkCellRowIndex++) {
                                for (chunkCellColIndex = 0; chunkCellColIndex < chunkNCols; chunkCellColIndex++) {
                                    try {
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                grid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        value = gridChunk.getCell(
                                                chunkCellRowIndex,
                                                chunkCellColIndex,
                                                noDataValue,
                                                handleOutOfMemoryError,
                                                chunkID);
                                    }
                                    try {
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    (((value - minGrid) / rangeGrid) * range) + min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    } catch (OutOfMemoryError oome) {
                                        ge.clear_MemoryReserve();
                                        long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                                outputGrid, chunkID, handleOutOfMemoryError);
                                        if (swap < 1L) {
                                            throw oome;
                                        }
                                        ge.init_MemoryReserve(handleOutOfMemoryError);
                                        if (value != noDataValue) {
                                            outputGridChunk.setCell(
                                                    chunkCellRowIndex,
                                                    chunkCellColIndex,
                                                    (((value - minGrid) / rangeGrid) * range) + min,
                                                    noDataValue,
                                                    handleOutOfMemoryError);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                outputGrid.set_Name( grid.get_Name(handleOutOfMemoryError)+ "_linearRescale", handleOutOfMemoryError );
                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            } else {
                // @TODO this is not a brilliant implementation
                if (type.equalsIgnoreCase("log")) {
                    outputGrid = rescale(
                            outputGrid,
                            null,
                            1.0d,
                            1000000.0d,
                            handleOutOfMemoryError);
                    long row;
                    long col;
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            try {
                                value = grid.getCell(
                                        row,
                                        col,
                                        handleOutOfMemoryError);
                            } catch (OutOfMemoryError oome) {
                                ge.clear_MemoryReserve();
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                        (int) row,
                                        (int) col);
                                long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                        grid, chunkID, handleOutOfMemoryError);
                                if (swap < 1L) {
                                    throw oome;
                                }
                                ge.init_MemoryReserve(handleOutOfMemoryError);
                                value = outputGrid.getCell(
                                        row,
                                        col,
                                        handleOutOfMemoryError);
                            }
                            try {
                                if (value != noDataValue) {
                                    outputGrid.setCell(
                                            row,
                                            col,
                                            Math.log(value),
                                            handleOutOfMemoryError);
                                }
                            } catch (OutOfMemoryError oome) {
                                ge.clear_MemoryReserve();
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                        (int) row,
                                        (int) col);
                                long swap = ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                                        outputGrid, chunkID, handleOutOfMemoryError);
                                if (swap < 1L) {
                                    throw oome;
                                }
                                ge.init_MemoryReserve(handleOutOfMemoryError);
                                if (value != noDataValue) {
                                    outputGrid.setCell(
                                            row,
                                            col,
                                            Math.log(value),
                                            handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                    outputGrid = rescale(
                            outputGrid,
                            null,
                            min,
                            max,
                            handleOutOfMemoryError);
                    //grid.set_Name( grid.getName() + "_logRescale" );
                    ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                } else {
                    System.out.println("Unable to rescale: type " + type + "not recognised. Returning a Grid2DSquareCellDouble of _InputGrid.");
                }
            }
            return outputGrid;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return rescale(
                        grid,
                        type,
                        min,
                        max,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Modifies grid so value of cells with CellIDs in _CellIDs are set to a
     * value a little bit larger.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed.
     * @param _CellIDs The CellIDs of the cells to be processed.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void setValueALittleBitLarger(
            Grids_Grid2DSquareCellDouble grid,
            HashSet _CellIDs,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long cellID;
            double noDataValue = grid.get_NoDataValue(handleOutOfMemoryError);
            Iterator iterator1 = _CellIDs.iterator();
            double thisValue;
            int counter = 0;
            while (iterator1.hasNext()) {
                cellID = ((Grids_2D_ID_long) iterator1.next());
                thisValue = grid.getCell(
                        cellID.getRow(),
                        cellID.getCol(),
                        handleOutOfMemoryError);
                if (thisValue != noDataValue) {
                    grid.setCell(cellID,
                            Grids_Utilities.getValueALittleBitLarger(thisValue),
                            handleOutOfMemoryError);
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                setValueALittleBitLarger(
                        grid,
                        _CellIDs,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Modifies grid so value of cells with CellIDs in _CellIDs are set to a
     * value a little bit smaller.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed.
     * @param _CellIDs The CellIDs of the cells to be processed.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void setValueALittleBitSmaller(
            Grids_Grid2DSquareCellDouble grid,
            HashSet _CellIDs,
            boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long cellID;
            double noDataValue = grid.get_NoDataValue(handleOutOfMemoryError);
            Iterator iterator1 = _CellIDs.iterator();
            double thisValue;
            while (iterator1.hasNext()) {
                cellID = (Grids_2D_ID_long) iterator1.next();
                thisValue = grid.getCell(cellID.getRow(), cellID.getCol(), handleOutOfMemoryError);
                if (thisValue != noDataValue) {
                    grid.setCell(cellID, Grids_Utilities.getValueALittleBitSmaller(thisValue), handleOutOfMemoryError);
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                setValueALittleBitSmaller(
                        grid,
                        _CellIDs,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Adds value to grid for cells with CellID in _CellIDs
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed
     * @param _CellIDs A HashSet containing CellIDs.
     * @param value The value to be added.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            HashSet _CellIDs,
            double value,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            Iterator iterator1 = _CellIDs.iterator();
            while (iterator1.hasNext()) {
                //grid.addToCell( ( CellID ) iterator1.next(), value );
                Grids_2D_ID_long cellID = (Grids_2D_ID_long) iterator1.next();
                if (cellID != null) {
                    grid.addToCell(
                            cellID,
                            value,
                            handleOutOfMemoryError);
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        _CellIDs,
                        value,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Adds value to every cell of grid.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed
     * @param value The value to be added
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            double value,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            long row;
            long col;
            for (row = 0; row < nrows; row++) {
                for (col = 0; col < ncols; col++) {
                    grid.addToCell(
                            row,
                            col,
                            value, handleOutOfMemoryError);
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        value,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Adds value to grid for cells with CellID in _CellIDs
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed
     * @param cellIDs Array of CellIDs.
     * @param value The value to be added.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            Grids_2D_ID_long[] cellIDs,
            double value,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            for (int cellIDIndex = 0; cellIDIndex < cellIDs.length; cellIDIndex++) {
                grid.addToCell(
                        cellIDs[cellIDIndex].getRow(),
                        cellIDs[cellIDIndex].getCol(),
                        value, handleOutOfMemoryError);
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        cellIDs,
                        value,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Add gridToAdd to grid
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed/modified.
     * @param gridToAdd The Grids_Grid2DSquareCellDouble from which values are added.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            Grids_Grid2DSquareCellDouble gridToAdd,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            ge.get_AbstractGrid2DSquareCell_HashSet().add(gridToAdd);
            addToGrid(
                    grid,
                    gridToAdd,
                    1.0d,
                    handleOutOfMemoryError);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        gridToAdd,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Add gridToAdd to grid with values from gridToAdd multiplied by weight.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed/modified.
     * @param gridToAdd The Grids_Grid2DSquareCellDouble from which values are added.
     * @param weight The value gridToAdd values are multiplied by.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            Grids_Grid2DSquareCellDouble gridToAdd,
            double weight,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            ge.get_AbstractGrid2DSquareCell_HashSet().add(gridToAdd);
            addToGrid(
                    grid,
                    gridToAdd,
                    0L,
                    0L,
                    gridToAdd.get_NRows(handleOutOfMemoryError) - 1L,
                    gridToAdd.get_NCols(handleOutOfMemoryError) - 1L,
                    weight,
                    handleOutOfMemoryError);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        gridToAdd,
                        weight,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Add gridToAdd to grid with values from gridToAdd multiplied by weight.
     * Only values of gridToAdd with row index between startRowIndex and
     * endRowIndex, and column index between startColIndex and endColIndex are
     * added.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed.
     * @param gridToAdd The Grids_Grid2DSquareCellDouble from which values are added.
     * @param startRowIndex The index of the first row from which gridToAdd
     * values are added.
     * @param startColIndex the index of the first column from which gridToAdd
     * values are added.
     * @param endRowIndex the index of the final row from which gridToAdd values
     * are added.
     * @param endColIndex the index of the final column from which gridToAdd
     * values are added.
     * @param weight The value gridToAdd values are multiplied by.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            Grids_Grid2DSquareCellDouble gridToAdd,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            double weight,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            ge.get_AbstractGrid2DSquareCell_HashSet().add(gridToAdd);
            BigDecimal[] dimensions = gridToAdd.get_Dimensions(handleOutOfMemoryError);
            BigDecimal[] dimensionConstraints = new BigDecimal[5];
            dimensionConstraints[1] = dimensions[1].add(new BigDecimal(startColIndex).multiply(dimensions[0]));
            dimensionConstraints[2] = dimensions[2].add(new BigDecimal(startRowIndex).multiply(dimensions[0]));
            dimensionConstraints[3] = dimensions[1].add(new BigDecimal(endColIndex - startColIndex + 1L).multiply(dimensions[0]));
            dimensionConstraints[4] = dimensions[2].add(new BigDecimal(endRowIndex - startRowIndex + 1L).multiply(dimensions[0]));
            addToGrid(
                    grid,
                    gridToAdd,
                    startRowIndex,
                    startColIndex,
                    endRowIndex,
                    endColIndex,
                    dimensionConstraints,
                    weight,
                    handleOutOfMemoryError);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        gridToAdd,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        weight,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns a Grids_Grid2DSquareCellDouble with values of grid added with values
 from gridToAdd (with row index between startRowIndex, endRowIndex and
 column index between startColIndex, endColIndex) multiplied by weight.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed.
     * @param gridToAdd The Grids_Grid2DSquareCellDouble from which values are added.
     * @param startRowIndex The index of the first row from which gridToAdd
     * values are added.
     * @param startColIndex The index of the first column from which gridToAdd
     * values are added.
     * @param endRowIndex The index of the final row from which gridToAdd values
     * are added.
     * @param endColIndex The index of the final column from which gridToAdd
     * values are added.
     * @param dimensionConstraints
     * @param weight The value gridToAdd values are multiplied by.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown. TODO: Check that
     * reasonable answers are returned for intersections and aggregations.
     *
     * @todo work needed to handle OutOfMemoryErrors...
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            Grids_Grid2DSquareCellDouble gridToAdd,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            BigDecimal[] dimensionConstraints,
            double weight,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            ge.get_AbstractGrid2DSquareCell_HashSet().add(gridToAdd);
            int _MessageLength = 1000;
            String _Message0 = ge.initString(_MessageLength, handleOutOfMemoryError);
            String _Message = ge.initString(_MessageLength, handleOutOfMemoryError);
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            double noDataValue = grid.get_NoDataValue(handleOutOfMemoryError);
            BigDecimal[] gridDimensions = grid.get_Dimensions(handleOutOfMemoryError);

            long gridToAddNrows = gridToAdd.get_NRows(handleOutOfMemoryError);
            long gridToAddNcols = gridToAdd.get_NCols(handleOutOfMemoryError);
            double gridToAddNoDataValue = gridToAdd.get_NoDataValue(handleOutOfMemoryError);
            BigDecimal[] gridToAddDimensions = gridToAdd.get_Dimensions(handleOutOfMemoryError);

            Grids_Grid2DSquareCellDoubleFactory gridFactory = new Grids_Grid2DSquareCellDoubleFactory(ge, handleOutOfMemoryError);
//            Grids_Grid2DSquareCellDoubleFactory gridFactory = new Grids_Grid2DSquareCellDoubleFactory(
//                    grid.get_Directory(handleOutOfMemoryError),
//                    ge.get_AbstractGrid2DSquareCell_HashSet(),
//                    handleOutOfMemoryError);
            // TODO:
            // Implement higher method to pass in a Grids_AbstractGrid2DSquareCellDoubleChunkFactory.
            //Grid2DSquareCellDoubleFactory gridFactory = new Grids_Grid2DSquareCellDoubleFactory( grid.get_Directory() );

            if ((dimensionConstraints[1].compareTo(gridDimensions[3]) == 1)
                    || (dimensionConstraints[3].compareTo(gridDimensions[1]) == -1)
                    || (dimensionConstraints[2].compareTo(gridDimensions[4]) == 1)
                    || (dimensionConstraints[4].compareTo(gridDimensions[2]) == -1)) {
                return;
            }
            if (gridToAddDimensions[0].compareTo(gridDimensions[0]) == -1) {
                throw new UnsupportedOperationException();
                // TODO:
                //            if ( startRowIndex != 0 || startColIndex != 0 || endRowIndex != gridToAdd.get_NRows() - 1 || endColIndex != gridToAdd.get_NCols() - 1 ) {
                //                println("Might be aggregating more than necessary!!!");
                //            }
                //            Grids_Grid2DSquareCellDouble aggregateGrid2DSquareCellDoubleToAddToAdd = null;
                //            try {
                //                aggregateGrid2DSquareCellDoubleToAddToAdd = aggregate( gridToAdd, "sum", gridDimensions, gridFactory );
                //            } catch ( OutOfMemoryError e ) {
                //                // TODO:
                //                // Clear some memory, cache, use chankRAF etc... try again. Perhaps fail and throw error to deal with higher up...
                //                println( "Warning!!! OutOfMemoryError in Grids_Processor.addToGrid( grid ( ))" );
                //                int outOfMemoryErrorDebug0 = 1;
                //            }
                //            return addToGrid( grid, aggregateGrid2DSquareCellDoubleToAddToAdd, dimensionConstraints, weight );
                //return;
            } else {
                // If gridToAddCellsize is the same and the _AbstractGrid2DSquareCell_HashSet align
                if ((gridToAddDimensions[0].compareTo(gridDimensions[0]) == 0)
                        && ((gridToAddDimensions[1].remainder(gridDimensions[0])).compareTo((gridDimensions[1].remainder(gridDimensions[0]))) == 0)
                        && ((gridToAddDimensions[2].remainder(gridDimensions[0])).compareTo((gridDimensions[2].remainder(gridDimensions[0]))) == 0)) {
                    //println( "Grids Align!" );
                    double x;
                    double y;
                    double value;
                    long row;
                    long col;
                    // TODO:
                    // Make more robust if necessary using xBigDecimal and yBigDecimal
                    // rather than using x and y?
                    // The necessity can be calculated given the precision and size of
                    // the double and the grid cellsize.
                    for (row = startRowIndex; row <= endRowIndex; row++) {
                        y = gridToAdd.getCellYDouble(row, handleOutOfMemoryError);
                        for (col = startColIndex; col <= endColIndex; col++) {
                            x = gridToAdd.getCellXDouble(col, handleOutOfMemoryError);
                            value = gridToAdd.getCell(row, col, handleOutOfMemoryError);
                            if (value != gridToAddNoDataValue) {
                                grid.addToCell(
                                        x,
                                        y,
                                        value * weight,
                                        handleOutOfMemoryError);
                            }
                        }
                    }
                    return;
                } else {
                    // println("Intersection!!!!");
                    // Need to intersect
                    // TODO:
                    // Clip gridToAdd might improve matters here.
                    // Check
                    Grids_Grid2DSquareCellDouble tempGrid1 = null;
                    Grids_Grid2DSquareCellDouble tempGrid2 = null;
                    try {
                        tempGrid1 = (Grids_Grid2DSquareCellDouble) gridFactory.create(nrows, ncols, gridDimensions);
                        tempGrid2 = (Grids_Grid2DSquareCellDouble) gridFactory.create(nrows, ncols, gridDimensions);
                    } catch (OutOfMemoryError e) {

                        _Message = "Warning!!! OutOfMemoryError: Unable to create temprorary grids trying another way but may run out of filespace!";
                        _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                        // TODO:
                        // Clear some memory, cache, use chunkRAF etc... try again. Perhaps fail and throw error to deal with higher up...
                        _Message = "Warning!!! OutOfMemoryError in Grid2DSquareCellProcessor.addToGrid( grid ( ))";
                        _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                        int outOfMemoryErrorDebug1 = 1;

                    }
                    // TODO:
                    // Check scale and rounding appropriate
                    int scale = 324;
                    int roundingMode = BigDecimal.ROUND_HALF_EVEN;
                    BigDecimal gridToAddCellsizeDividedBy2 = gridToAddDimensions[0].divide(new BigDecimal("2"), scale, roundingMode);
                    BigDecimal gridToAddCellsizeSquared = gridToAddDimensions[0].multiply(gridToAddDimensions[0]);
                    double[] bounds = new double[4];
                    Grids_2D_ID_long cellID1;
                    Grids_2D_ID_long cellID2;
                    Grids_2D_ID_long cellID3;
                    Grids_2D_ID_long cellID4;
                    double d1;
                    double d2;
                    double d3;
                    double d4;
                    double x;
                    double y;
                    long row;
                    long col;
                    double areaProportion = 0.0d;
                    // TODO:
                    // precision checking and use of BigDecimal?
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            bounds = grid.getCellBoundsDoubleArray(row, col, handleOutOfMemoryError);
                            x = grid.getCellXDouble(col, handleOutOfMemoryError);
                            y = grid.getCellYDouble(row, handleOutOfMemoryError);
                            cellID1 = gridToAdd.getCellID(bounds[0], bounds[3], handleOutOfMemoryError);
                            cellID2 = gridToAdd.getCellID(bounds[2], bounds[3], handleOutOfMemoryError);
                            cellID3 = gridToAdd.getCellID(bounds[0], bounds[1], handleOutOfMemoryError);
                            cellID4 = gridToAdd.getCellID(bounds[2], bounds[1], handleOutOfMemoryError);
                            if (cellID1.equals(cellID2) && cellID2.equals(cellID3)) {
                                d1 = gridToAdd.getCell(cellID1, handleOutOfMemoryError);
                                if (d1 != gridToAddNoDataValue) {
                                    areaProportion = (gridDimensions[0].multiply(gridDimensions[0]).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue();
                                    tempGrid1.addToCell(row, col, d1 * areaProportion, handleOutOfMemoryError);
                                    tempGrid2.addToCell(row, col, areaProportion, handleOutOfMemoryError);
                                }
                            } else {
                                d1 = gridToAdd.getCell(cellID1, handleOutOfMemoryError);
                                d2 = gridToAdd.getCell(cellID2, handleOutOfMemoryError);
                                d3 = gridToAdd.getCell(cellID3, handleOutOfMemoryError);
                                d4 = gridToAdd.getCell(cellID4, handleOutOfMemoryError);

                                if (!gridToAdd.isInGrid(cellID1, handleOutOfMemoryError) && d1 != gridToAddNoDataValue) {
                                    if (cellID1.equals(cellID2) || cellID1.equals(cellID3)) {
                                        if (cellID1.equals(cellID2)) {
                                            //areaProportion = ( Math.abs( bounds[3] - ( gridToAdd.getCellYDouble( cellID1 ) - gridToAddCellsizeDividedBy2 ) ) * cellsize ) / ( gridToAddCellsize * gridToAddCellsize );
                                            areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(gridToAdd.getCellYBigDecimal(cellID1, handleOutOfMemoryError).subtract(gridToAddCellsizeDividedBy2)).multiply(gridDimensions[0])).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        } else {
                                            //areaProportion = ( Math.abs( ( gridToAdd.getCellXDouble( cellID1 ) + gridToAddCellsizeDividedBy2 ) - bounds[0] ) * cellsize ) / ( gridToAddCellsize * gridToAddCellsize );
                                            areaProportion = Math.abs(((((gridToAdd.getCellXBigDecimal(cellID1, handleOutOfMemoryError).add(gridToAddCellsizeDividedBy2)).subtract(BigDecimal.valueOf(bounds[0]))).multiply(gridDimensions[0])).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        }
                                    } else {
                                        //areaProportion = ( ( Math.abs( bounds[3] - ( gridToAdd.getCellYDouble( cellID1 ) - gridToAddCellsizeDividedBy2 ) ) * Math.abs( ( gridToAdd.getCellXDouble( cellID1 ) + ( gridToAddCellsize / 2.0d ) ) - bounds[0] ) ) / ( gridToAddCellsize * gridToAddCellsize ) );
                                        areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(gridToAdd.getCellYBigDecimal(cellID1, handleOutOfMemoryError).subtract(gridToAddCellsizeDividedBy2))).multiply((gridToAdd.getCellXBigDecimal(cellID1, handleOutOfMemoryError).add(gridToAddCellsizeDividedBy2.subtract(BigDecimal.valueOf(bounds[0])))).divide(gridToAddCellsizeSquared, scale, roundingMode))).doubleValue());
                                    }
                                    tempGrid1.addToCell(row, col, d1 * areaProportion, handleOutOfMemoryError);
                                    tempGrid2.addToCell(row, col, areaProportion, handleOutOfMemoryError);
                                }
                                if (!gridToAdd.isInGrid(cellID2, handleOutOfMemoryError) && d2 != gridToAddNoDataValue) {
                                    if (cellID2.equals(cellID1)) {
                                        if (cellID2.equals(cellID4)) {
                                            //areaProportion = ( Math.abs( bounds[2] - ( gridToAdd.getCellXDouble( cellID2 ) - gridToAddCellsizeDividedBy2 ) ) * cellsize ) / ( gridToAddCellsize * gridToAddCellsize );
                                            areaProportion = Math.abs((((BigDecimal.valueOf(bounds[2]).subtract(gridToAdd.getCellXBigDecimal(cellID2, handleOutOfMemoryError).subtract(gridToAddCellsizeDividedBy2))).multiply(gridDimensions[0])).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        } else {
                                            //areaProportion = ( ( Math.abs( bounds[3] - ( gridToAdd.getCellYDouble( cellID2 ) - gridToAddCellsizeDividedBy2 ) ) * Math.abs( bounds[2] - ( gridToAdd.getCellXDouble( cellID2 ) - ( gridToAddCellsize / 2.0d ) ) ) ) / ( gridToAddCellsize * gridToAddCellsize ) );
                                            areaProportion = Math.abs(((BigDecimal.valueOf(bounds[3]).subtract(gridToAdd.getCellYBigDecimal(cellID2, handleOutOfMemoryError).subtract(gridToAddCellsizeDividedBy2))).multiply(BigDecimal.valueOf(bounds[2]).subtract(gridToAdd.getCellXBigDecimal(cellID2, handleOutOfMemoryError).subtract(gridToAddCellsizeDividedBy2))).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        }
                                        tempGrid1.addToCell(row, col, d2 * areaProportion, handleOutOfMemoryError);
                                        tempGrid2.addToCell(row, col, areaProportion, handleOutOfMemoryError);
                                    }
                                }
                                if (!gridToAdd.isInGrid(cellID3, handleOutOfMemoryError) && d3 != gridToAddNoDataValue) {
                                    if (!cellID3.equals(cellID1)) {
                                        if (cellID3.equals(cellID4)) {
                                            //areaProportion = ( Math.abs( ( gridToAdd.getCellYDouble( cellID3 ) + ( gridToAddCellsize / 2.0d ) ) - bounds[1] ) * cellsize ) / ( gridToAddCellsize * gridToAddCellsize );
                                            areaProportion = Math.abs(((((gridToAdd.getCellYBigDecimal(cellID3, handleOutOfMemoryError).add(gridToAddCellsizeDividedBy2)).subtract(BigDecimal.valueOf(bounds[1]))).multiply(gridDimensions[0])).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        } else {
                                            //areaProportion = ( ( Math.abs( ( gridToAdd.getCellYDouble( cellID3 ) + ( gridToAddCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( gridToAdd.getCellXDouble( cellID3 ) + ( gridToAddCellsize / 2.0d ) ) - bounds[0] ) ) / ( gridToAddCellsize * gridToAddCellsize ) );
                                            areaProportion = Math.abs(((((gridToAdd.getCellYBigDecimal(cellID3, handleOutOfMemoryError).add(gridToAddCellsizeDividedBy2)).subtract(BigDecimal.valueOf(bounds[1]))).multiply((gridToAdd.getCellXBigDecimal(cellID3, handleOutOfMemoryError).add(gridToAddCellsizeDividedBy2)).subtract(BigDecimal.valueOf(bounds[0])))).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        }
                                        tempGrid1.addToCell(row, col, d3 * areaProportion, handleOutOfMemoryError);
                                        tempGrid2.addToCell(row, col, areaProportion, handleOutOfMemoryError);
                                    }
                                }
                                if (!gridToAdd.isInGrid(cellID4, handleOutOfMemoryError) && d4 != gridToAddNoDataValue) {
                                    if (cellID4 != cellID2 && cellID4 != cellID3) {
                                        //areaProportion = ( ( Math.abs( ( gridToAdd.getCellYDouble( cellID4 ) + ( gridToAddCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( gridToAdd.getCellXDouble( cellID4 ) - ( gridToAddCellsize / 2.0d ) ) ) ) / ( gridToAddCellsize * gridToAddCellsize ) );
                                        areaProportion = Math.abs(((((gridToAdd.getCellYBigDecimal(cellID4, handleOutOfMemoryError).add(gridToAddCellsizeDividedBy2)).subtract(BigDecimal.valueOf(bounds[1]))).multiply(BigDecimal.valueOf(bounds[2]).subtract((gridToAdd.getCellXBigDecimal(cellID4, handleOutOfMemoryError)).subtract(gridToAddCellsizeDividedBy2)))).divide(gridToAddCellsizeSquared, scale, roundingMode)).doubleValue());
                                        tempGrid1.addToCell(row, col, d4 * areaProportion, handleOutOfMemoryError);
                                        tempGrid2.addToCell(row, col, areaProportion, handleOutOfMemoryError);
                                    }
                                }
                                //// Check
                                // Check fails due to rounding errors!
                                //if ( cellID1 != Integer.MIN_VALUE && cellID2 != Integer.MIN_VALUE && cellID3 != Integer.MIN_VALUE && cellID4 != Integer.MIN_VALUE && totalArea != 1.0 ) { println( "id = " + i + " : totalArea = " + totalArea + " (cellID1,cellID2,cellID3,cellID4) = (" + cellID1 + "," + cellID2 + "," + cellID3 + "," + cellID4 + ")" ); System.exit(0);}
                            }
                        }
                    }
                    _Message = "tempGrid1";
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    _Message = tempGrid1.toString();
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    _Message = "tempGrid2";
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    _Message = tempGrid2.toString();
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    // The values are normalised by dividing the aggregate Grid sum by the proportion of cells with grid values.
                    for (row = 0; row <= nrows; row++) {
                        for (col = 0; col <= ncols; col++) {
                            d1 = tempGrid2.getCell(row, col, handleOutOfMemoryError);
                            if (!(d1 == 0.0d || d1 == noDataValue)) {
                                //setCell( i, tempGrid2.getCell( i ) );
                                grid.addToCell(row, col, weight * tempGrid1.getCell(row, col, handleOutOfMemoryError) / d1, handleOutOfMemoryError);
                                //addToCell( i, tempGrid1.getCell( i ) / ( Math.pow( ( gridToAddCellsize / cellsize ), 2.0d ) / d1 ) );
                            }
                        }
                    }
                    //                tempGrid1.clear();
                    //                tempGrid2.clear();
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        gridToAdd,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        dimensionConstraints,
                        weight,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns grid with values added from a file.
     *
     * @param grid the Grids_Grid2DSquareCellDouble to be processed
     * @param file the file contining values to be added.
     * @param type the type of file. Supported types include "xyv", "xy", "idxy"
     * )
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     */
    public void addToGrid(
            Grids_Grid2DSquareCellDouble grid,
            File file,
            String type,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            int _MessageLength = 1000;
            String _Message0 = ge.initString(_MessageLength, handleOutOfMemoryError);
            String _Message = ge.initString(_MessageLength, handleOutOfMemoryError);
            if (type.equalsIgnoreCase("xyv")) {
                try {
                    StreamTokenizer st
                            = new StreamTokenizer(
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    new FileInputStream(file))));
                    st.eolIsSignificant(false);
                    st.parseNumbers();
                    st.whitespaceChars(',', ',');
                    st.wordChars('"', '"');
                    int tokenType = st.nextToken();
                    String alternator = "x";
                    double x = 0.0d;
                    double y = 0.0d;
                    double value = 0.0d;
                    while (tokenType != StreamTokenizer.TT_EOF) {
                        switch (tokenType) {
                            case StreamTokenizer.TT_NUMBER:
                                if (alternator.equals("x")) {
                                    x = st.nval;
                                    alternator = "y";
                                } else if (alternator.equals("y")) {
                                    y = st.nval;
                                    alternator = "value";
                                } else {
                                    grid.addToCell(x, y, st.nval, handleOutOfMemoryError);
                                    alternator = "x";
                                }
                                break;
                            default:
                                break;
                        }
                        tokenType = st.nextToken();
                    }
                } catch (java.io.IOException e) {
                    _Message = e.toString();
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    _Message = "in uk.ac.leeds.ccg.grids.AbstractGrid2DSquareCellDouble.addToGrid( file( " + file.toString() + "), type( " + type + " ) )";
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    System.err.println(e.getMessage());
                    //e.printStackTrace();
                }
            }
            if (type.equalsIgnoreCase("xy")) {
                try {
                    StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
                    st.eolIsSignificant(false);
                    st.parseNumbers();
                    st.whitespaceChars(',', ',');
                    st.wordChars('"', '"');
                    int tokenType = st.nextToken();
                    String alternator = "x";
                    double x = 0.0d;
                    double y = 0.0d;
                    double value = 0.0d;
                    while (tokenType != StreamTokenizer.TT_EOF) {
                        switch (tokenType) {
                            case StreamTokenizer.TT_NUMBER:
                                if (alternator.equals("x")) {
                                    x = st.nval;
                                    alternator = "y";
                                } else {
                                    y = st.nval;
                                    grid.addToCell(x, y, 1.0d, handleOutOfMemoryError);
                                    alternator = "x";
                                }
                                break;
                            default:
                                break;
                        }
                        tokenType = st.nextToken();
                    }
                } catch (java.io.IOException e) {
                    _Message = e.toString();
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    _Message = "in uk.ac.leeds.ccg.grids.AbstractGrid2DSquareCellDouble.addToGrid( file( " + file.toString() + "), type( " + type + " ) )";
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    System.err.println(e.getMessage());
                    //e.printStackTrace();
                }
            }
            if (type.equalsIgnoreCase("idxy")) {
                try {
                    StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
                    st.eolIsSignificant(false);
                    st.parseNumbers();
                    st.ordinaryChar('e');
                    st.ordinaryChar('d');
                    st.ordinaryChar('E');
                    st.ordinaryChar('D');
                    int tokenType = st.nextToken();
                    int nextTokenType;
                    String alternator = "id";
                    int id;
                    double x = 0.0d;
                    double y = 0.0d;
                    double value = 0.0d;
                    while (tokenType != StreamTokenizer.TT_EOF) {
                        switch (tokenType) {
                            case StreamTokenizer.TT_NUMBER:
                                if (alternator.equals("id")) {
                                    id = (int) st.nval;
                                    alternator = "x";
                                } else {
                                    if (alternator.equals("x")) {
                                        x = st.nval;
                                        nextTokenType = st.nextToken();
                                        if (nextTokenType != StreamTokenizer.TT_NUMBER) {
                                            nextTokenType = st.nextToken();
                                            nextTokenType = st.nextToken();
                                            x = x * Math.pow(10.0, st.nval);
                                        } else {
                                            st.pushBack();
                                        }
                                        alternator = "y";
                                    } else {
                                        y = st.nval;
                                        nextTokenType = st.nextToken();
                                        if (nextTokenType != StreamTokenizer.TT_NUMBER) {
                                            nextTokenType = st.nextToken();
                                            nextTokenType = st.nextToken();
                                            y = y * Math.pow(10.0, st.nval);
                                        } else {
                                            st.pushBack();
                                        }
                                        alternator = "id";
                                        //println( " x, y = " + x + ", " + y );
                                        grid.addToCell(x, y, 1.0d, handleOutOfMemoryError);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        tokenType = st.nextToken();
                    }
                } catch (java.io.IOException e) {
                    _Message = e.toString();
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    _Message = "in uk.ac.leeds.ccg.grids.AbstractGrid2DSquareCellDouble.addToGrid( file( " + file.toString() + "), type( " + type + " ) )";
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                    System.err.println(e.getMessage());
                    //e.printStackTrace();
                }
            }
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                addToGrid(
                        grid,
                        file,
                        type,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    //    /**
    //     * TODO:
    //     * 1. Documentiation
    //     * 2. Change so Grids_Grid2DSquareCellDouble are Grids_AbstractGrid2DSquareCell
    //     * Returns a Grids_Grid2DSquareCellDouble that's values are those of grid0 minus
    //     * grid1.
    //     * @param data controls if result is produced even if one of the inputs is a
    //     *   NoData value
    //     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
    //     *   in this method then swap operations are initiated prior to retrying.
    //     *   If false then OutOfMemoryErrors are caught and thrown.
    //     * TODO:
    //     * see multiply first
    //     */
    //    public Grids_AbstractGrid2DSquareCell minus (
    //            Grids_AbstractGrid2DSquareCell grid0,
    //            Grids_AbstractGrid2DSquareCell grid1,
    //            boolean data,
    //            Grids_Grid2DSquareCellDoubleFactory gridFactory,
    //            boolean handleOutOfMemoryError ) {
    //        try {
    //            double grid0NoDataValue = grid0.get_NoDataValue();
    //        double grid1NoDataValue = grid1.get_NoDataValue();
    //        long nrows = grid0.get_NRows();
    //        long ncols = grid0.get_NCols();
    //        BigDecimal[] dimensions0 = grid0.get_Dimensions();
    //        Grids_Grid2DSquareCellDouble result = ( Grids_Grid2DSquareCellDouble ) gridFactory.create( grid0.getChunkNRows(this.handleOutOfMemoryErrorFalse), grid0.getChunkNCols(this.handleOutOfMemoryErrorFalse), nrows, ncols, dimensions0, grid0NoDataValue );
    //        double value0;
    //        double value1;
    //        long row;
    //        long col;
    //        if ( data ) {
    //            for ( row = 0; row < nrows; row ++ ) {
    //                for ( col = 0; col < ncols; col ++ ) {
    //                    value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                    if ( value0 == grid0NoDataValue ) {
    //                        value0 = 0.0d;
    //                        value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                        if ( value1 != grid1NoDataValue ) {
    //                            result.setCell( row, col, value0 - value1, this.handleOutOfMemoryError );
    //                        }
    //                    } else {
    //                        value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                        if ( value1 == grid1NoDataValue ) {
    //                            value1 = 0.0d;
    //                        }
    //                        result.setCell( row, col, value0 - value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            }
    //        } else {
    //            for ( row = 0; row < nrows; row ++ ) {
    //                for ( col = 0; col < ncols; col ++ ) {
    //                    value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                    value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                    if ( value0 != grid0NoDataValue && value1 != grid1NoDataValue ) {
    //                        result.setCell( row, col, value0 - value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            }
    //        }
    //        //result.set_Name( "minus_" + grid0.getName( this.handleOutOfMemoryError ) + "_" + grid1.getName( this.handleOutOfMemoryError ) );
    //        return result;
    //    }
    //
    //    /**
    //     * TODO:
    //     * 1. Documentiation
    //     * 2. Change so Grids_Grid2DSquareCellDouble are Grids_AbstractGrid2DSquareCell
    //     * @param data controls if result is produced even if one of the inputs is a
    //     *   NoData value
    //     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
    //     *   in this method then swap operations are initiated prior to retrying.
    //     *   If false then OutOfMemoryErrors are caught and thrown.
    //     * @see Grids_Processor#addToGrid(Grids_Grid2DSquareCellDouble,Grids_Grid2DSquareCellDouble,boolean)
    //     * TODO:
    //     * see multiply first
    //     */
    //    public Grids_Grid2DSquareCellDouble add( Grids_Grid2DSquareCellDouble grid0, Grids_Grid2DSquareCellDouble grid1, boolean data, Grids_Grid2DSquareCellDoubleFactory gridFactory, boolean handleOutOfMemoryError ) {
    //        double grid0NoDataValue = grid0.get_NoDataValue();
    //        double grid1NoDataValue = grid1.get_NoDataValue();
    //        long nrows = grid0.get_NRows();
    //        long ncols = grid0.get_NCols();
    //        BigDecimal[] dimensions0 = grid0.get_Dimensions();
    //        Grids_Grid2DSquareCellDouble result = gridFactory.create( grid0.getChunkNRows(this.handleOutOfMemoryErrorFalse), grid0.getChunkNCols(this.handleOutOfMemoryErrorFalse), nrows, ncols, dimensions0, grid0NoDataValue );
    //        long row;
    //        long col;
    //        double value0;
    //        double value1;
    //        if ( data ) {
    //            for ( row = 0; row < nrows; row ++ ) {
    //                for ( col = 0; col < ncols; col ++ ) {
    //                    value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                    if ( value0 == grid0NoDataValue ) {
    //                        value0 = 0.0d;
    //                        value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                        if ( value1 != grid1NoDataValue ) {
    //                            result.setCell( row, col, value0 + value1, this.handleOutOfMemoryError );
    //                        }
    //                    } else {
    //                        value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                        if ( value1 == grid1NoDataValue ) {
    //                            value1 = 0.0d;
    //                        }
    //                        result.setCell( row, col, value0 + value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            }
    //        } else {
    //            for ( row = 0; row < nrows; row ++ ) {
    //                for ( col = 0; col < ncols; col ++ ) {
    //                    value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                    value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                    if ( value0 != grid0NoDataValue && value1 != grid1NoDataValue ) {
    //                        result.setCell( row, col, value0 + value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            }
    //        }
    //        //result.set_Name( "add_" + grid0.getName( this.handleOutOfMemoryError ) + "_" + grid1.getName( this.handleOutOfMemoryError ) );
    //        return result;
    //    }
    //
    //
    //    /**
    //     * Returns a Grids_AbstractGrid2DSquareCell which is grid0 multiplied
    //     * by grid1 or null if grid0 and grid1
    //     * are not coincident.
    //     * @param grid0
    //     * @param grid1
    //     * @param gridAbstractFactory the Grid2DSquareCellAbstractFactory
    //     *   used to generate the result.
    //     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
    //     *   in this method then swap operations are initiated prior to retrying.
    //     *   If false then OutOfMemoryErrors are caught and thrown.
    //     * @TODO
    //     * Organise for chunk processing.
    //     */
    //    public Grids_AbstractGrid2DSquareCell multiply( Grids_AbstractGrid2DSquareCell grid0, Grids_AbstractGrid2DSquareCell grid1, Grid2DSquareCellAbstractFactory gridAbstractFactory, boolean handleOutOfMemoryError ) {
    //        // Initialisation
    //        Grids_AbstractGrid2DSquareCell result;
    //        long nrows = grid0.get_NRows();
    //        long ncols = grid0.get_NCols();
    //        long row;
    //        long col;
    //
    //        if ( gridAbstractFactory.getClass() == Grids_Grid2DSquareCellIntFactory.class ) {
    //            // Probably better off creating another way...
    //            //BigDecimal[] dimensions0 = grid0.get_Dimensions();
    //            //result = gridAbstractFactory.create( grid0.getChunkNRows(this.handleOutOfMemoryErrorFalse), grid0.getChunkNCols(this.handleOutOfMemoryErrorFalse), nrows, ncols, dimensions0, grid0NoDataValue );
    //            result = ( Grids_Grid2DSquareCellInt ) ( ( Grids_Grid2DSquareCellIntFactory ) gridAbstractFactory ).create( grid0 );
    //        } else {
    //            // ( gridAbstractFactory.getClass() == Grids_Grid2DSquareCellDoubleFactory.class )
    //            result = ( Grids_Grid2DSquareCellDouble ) ( ( Grids_Grid2DSquareCellDoubleFactory ) gridAbstractFactory ).create( grid0 );
    //        }
    //        if ( grid0.getClass() == Grids_Grid2DSquareCellInt.class ) {
    //            Grids_Grid2DSquareCellInt gridInt0 = ( Grids_Grid2DSquareCellInt ) grid0;
    //            int gridInt0NoDataValue = gridInt0.get_NoDataValue( handleOutOfMemoryError );
    //            if ( grid1.getClass() == Grids_Grid2DSquareCellInt.class ) {
    //                Grids_Grid2DSquareCellInt gridInt1 = ( Grids_Grid2DSquareCellInt ) grid1;
    //                int gridInt1NoDataValue = gridInt1.get_NoDataValue( handleOutOfMemoryError );
    //                int value0;
    //                int value1;
    //                for ( row = 0; row < nrows; row ++ ) {
    //                    for ( col = 0; col < ncols; col ++ ) {
    //                        value0 = gridInt0.getCell( row, col, this.handleOutOfMemoryError );
    //                        value1 = gridInt1.getCell( row, col, this.handleOutOfMemoryError );
    //                        result.setCell( row, col, value0 * value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            } else {
    //                Grids_Grid2DSquareCellDouble grid1 = ( Grids_Grid2DSquareCellDouble ) grid1;
    //                double grid1NoDataValue = grid1.get_NoDataValue();
    //                int value0;
    //                double value1;
    //                for ( row = 0; row < nrows; row ++ ) {
    //                    for ( col = 0; col < ncols; col ++ ) {
    //                        value0 = gridInt0.getCell( row, col, this.handleOutOfMemoryError );
    //                        value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                        result.setCell( row, col, value0 * value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            }
    //        } else {
    //            // ( grid0.getClass() == Grids_Grid2DSquareCellDouble.class )
    //            Grids_Grid2DSquareCellDouble grid0 = ( Grids_Grid2DSquareCellDouble ) grid0;
    //            double grid0NoDataValue = grid0.get_NoDataValue();
    //            if ( grid1.getClass() == Grids_Grid2DSquareCellInt.class ) {
    //                Grids_Grid2DSquareCellInt gridInt1 = ( Grids_Grid2DSquareCellInt ) grid1;
    //                int gridInt1NoDataValue = gridInt1.get_NoDataValue( handleOutOfMemoryError );
    //                double value0;
    //                int value1;
    //                for ( row = 0; row < nrows; row ++ ) {
    //                    for ( col = 0; col < ncols; col ++ ) {
    //                        value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                        value1 = gridInt1.getCell( row, col, this.handleOutOfMemoryError );
    //                        result.setCell( row, col, value0 * value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            } else {
    //                Grids_Grid2DSquareCellDouble grid1 = ( Grids_Grid2DSquareCellDouble ) grid1;
    //                double grid1NoDataValue = grid1.get_NoDataValue();
    //                double value0;
    //                double value1;
    //                for ( row = 0; row < nrows; row ++ ) {
    //                    for ( col = 0; col < ncols; col ++ ) {
    //                        value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                        value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                        result.setCell( row, col, value0 * value1, this.handleOutOfMemoryError );
    //                    }
    //                }
    //            }
    //        }
    //        //result.set_Name( "multiply_" + grid0.getName( this.handleOutOfMemoryError ) + "_" + grid1.getName( this.handleOutOfMemoryError ), this.handleOutOfMemoryError );
    //        return result;
    //    }
    //
    //    /**
    //     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
    //     *   in this method then swap operations are initiated prior to retrying.
    //     *   If false then OutOfMemoryErrors are caught and thrown.
    //     * TODO:
    //     * 1. Documentiation
    //     * 2. Change so Grids_Grid2DSquareCellDouble are Grids_AbstractGrid2DSquareCell
    //     */
    //    public Grids_Grid2DSquareCellDouble divide( Grids_Grid2DSquareCellDouble grid0, Grids_Grid2DSquareCellDouble grid1, Grids_Grid2DSquareCellDoubleFactory gridFactory, double defaultValue, boolean handleOutOfMemoryError ) {
    //        double grid0NoDataValue = grid0.get_NoDataValue();
    //        double grid1NoDataValue = grid1.get_NoDataValue();
    //        long nrows = grid0.get_NRows();
    //        long ncols = grid0.get_NCols();
    //        BigDecimal[] dimensions0 = grid0.get_Dimensions();
    //        Grids_Grid2DSquareCellDouble result = gridFactory.create( grid0.getChunkNRows(this.handleOutOfMemoryErrorFalse), grid0.getChunkNCols(this.handleOutOfMemoryErrorFalse), nrows, ncols, dimensions0, grid0NoDataValue );
    //        double value0;
    //        double value1;
    //        long row;
    //        long col;
    //        for ( row = 0; row < nrows; row ++ ) {
    //            for ( col = 0; col < ncols; col ++ ) {
    //                value0 = grid0.getCell( row, col, this.handleOutOfMemoryError );
    //                value1 = grid1.getCell( row, col, this.handleOutOfMemoryError );
    //                result.setCell( row, col, value0 / value1, this.handleOutOfMemoryError );
    //            }
    //        }
    //        //result.set_Name( "divide_" + grid0.getName() + "_" + grid1.getName() );
    //        return result;
    //    }
    //    /**
    //     * Returns an Grids_Grid2DSquareCellDouble at a lower level of resolution than grid.  The result values
    //     * are either the sum, mean, max or min of values in grid depending on statistic.
    //     * xllcorner and yllcorner are the same as grid.
    //     * @param grid - the Grids_Grid2DSquareCellDouble to be processed
    //     * @param cellfactor - the number of times wider/higher the aggregated grid cells are
    //     * @param statistic - "sum", "mean", "max", or "min" depending on what aggregate of values are wanted
    //     * NB. In the calculation of the sum and the mean if there is a cell in grid which has a data value then
    //     *     the result which incorporates that cell has a data value.  For this result cell, any of the cells in
    //     *     grid which have noDataValues their value is taken as that of the average of its nearest cells with
    //     *     a value.
    //     *     In the calculation of the max and the min noDataValues are simply ignored.
    //     *     Formerly noDataValues were treated as the average of values within a result cell.
    //     * TODO:
    //     * 1. Implement median, mode and variance aggregations.
    //     */
    //    public Grids_Grid2DSquareCellDouble aggregate( Grids_Grid2DSquareCellDouble grid, int cellFactor, String statistic ) {
    //        try {
    //            return aggregate( grid, cellFactor, statistic, 0, 0, new Grid2DSquareCellDoubleJAIFactory() );
    //        } catch ( OutOfMemoryError e ) {
    //            return aggregate( grid, cellFactor, statistic, 0, 0, new Grid2DSquareCellDoubleFileFactory() );
    //        }
    //    }
    /**
     * Returns an Grids_Grid2DSquareCellDouble at a lower level of resolution than
 grid. The result values are either the sum, mean, max or min of values in
     * grid depending on statistic.
     *
     * @param grid the Grids_Grid2DSquareCellDouble to be processed
     * @param cellFactor the number of times wider/higher the aggregated grid
     * cells are to be
     * @param statistic "sum", "mean", "max", or "min" depending on what
     * aggregate of values are wanted
     * @param rowOffset the number of rows above or below the origin of grid
     * where the aggregation is to start. > 0 result yllcorner will be above
     * grid yllcorner < 0 result yllcorner will be below grid yllcorner @param
     * colOffset the number of columns ab ove or below the origin of grid where
     * the aggregation is to start. > 0 result xllcorner will be right of grid
     * xllcorner < 0 result xllcorner will be left of grid xllcorner @param
     * gridFactory the Abstract2DSquareCell DoubleFactory used to create result
     * and temporary AbstractGrid2DSquareCellDoubles. @param colOffset @param
     * gridFactory @param handleOutOfMemoryError If true then OutOfMemoryErrors
     * are caught in this method then swap operations are initiated prior to ret
     * r y ing. If false then OutOfMemoryErrors are caught and thrown. NB. In
     * the calculation of the sum and the mean if there is a cell in grid which
     * has a data value then the result which incorporates that cell has a data
     * value. For this result cell, any of the cells in grid which have
     * noDataValues their value is taken as that of the average of its nearest
     * cells with a value. In the calculation of the max and the min
     * noDataValues are simply ignored. Formerly noDataValues were treated as
     * the average of values within a result cell. TODO: implement median, mode
     * and variance aggregations. @return
     */
    public Grids_Grid2DSquareCellDouble aggregate(
            Grids_AbstractGrid2DSquareCell grid,
            int cellFactor,
            String statistic,
            int rowOffset,
            int colOffset,
            Grids_Grid2DSquareCellDoubleFactory gridFactory,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            int _MessageLength = 1000;
            String _Message0 = ge.initString(_MessageLength, handleOutOfMemoryError);
            String _Message = ge.initString(_MessageLength, handleOutOfMemoryError);
            // Initial tests
            if (cellFactor <= 0) {
                _Message = "Warning!!! cellFactor <= 0 : Returning!";
                _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                return null;
            }
            // Initialisation
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            BigDecimal[] dimensions = grid.get_Dimensions(handleOutOfMemoryError);
            double noDataValue = Double.NEGATIVE_INFINITY;
            if (grid.getClass() == Grids_Grid2DSquareCellInt.class) {
                noDataValue = (double) ((Grids_Grid2DSquareCellInt) grid).getNoDataValue(handleOutOfMemoryError);
            } else {
                if (grid.getClass() == Grids_Grid2DSquareCellDouble.class) {
                    noDataValue = ((Grids_Grid2DSquareCellDouble) grid).get_NoDataValue(handleOutOfMemoryError);
                } else {
                    _Message = ("Grid2DSquareCellAbstract not recognised in aggregate( Grid2DSquareCellAbstract( " + grid.toString(handleOutOfMemoryError) + ", cellFactor( " + cellFactor + " ), statistic( " + statistic + " ), rowOffset( " + rowOffset + " ), colOffset( " + colOffset + " ), gridFactory( " + gridFactory + " ),  handleOutOfMemoryError( " + handleOutOfMemoryError + " ) )");
                    //throw new Exception();
                }
            }
            BigDecimal[] resultDimensions = new BigDecimal[5];
            resultDimensions[0] = dimensions[0].multiply(new BigDecimal(Integer.toString(cellFactor)));
            resultDimensions[1] = dimensions[1].add(dimensions[0].multiply(new BigDecimal(Integer.toString(colOffset))));
            resultDimensions[2] = dimensions[2].add(dimensions[0].multiply(new BigDecimal(Integer.toString(rowOffset))));
            //double resultCellsize = cellsize * ( double ) cellFactor;
            //double width = cellsize * ncols;
            //double height = cellsize * nrows;
            //double resultXllcorner = xllcorner + ( colOffset * cellsize );
            //double resultYllcorner = yllcorner + ( rowOffset * cellsize );
            // Calculate resultNrows and resultHeight
            long resultNrows = 1L;
            BigDecimal resultHeight = new BigDecimal(resultDimensions[0].toString());
            //double resultHeight = resultCellsize;
            while (resultDimensions[2].add(resultHeight).compareTo(dimensions[4]) == -1) {
                resultNrows++;
                resultHeight = resultHeight.add(resultDimensions[0]);
            }
            //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
            //    resultNrows ++;
            //    resultHeight += resultCellsize;
            //}
            // Calculate resultNcols and resultWidth
            long resultNcols = 1L;
            BigDecimal resultWidth = new BigDecimal(resultDimensions[0].toString());
            //double resultWidth = resultCellsize;
            while (resultDimensions[1].add(resultWidth).compareTo(dimensions[3]) == -1) {
                resultNrows++;
                resultWidth = resultWidth.add(resultDimensions[0]);
            }
            //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
            //    resultNcols ++;
            //    resultWidth += resultCellsize;
            //}
            resultDimensions[3] = dimensions[1].add(resultWidth);
            resultDimensions[4] = dimensions[2].add(resultHeight);
            // Initialise result
            gridFactory.set_NoDataValue(noDataValue);
            Grids_Grid2DSquareCellDouble result = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);

            long row;
            long col;
            double x;
            double y;
            double value;

            // sum
            if (statistic.equalsIgnoreCase("sum")) {
                Grids_Grid2DSquareCellDouble count = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);
                Grids_Grid2DSquareCellDouble normaliser = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col, handleOutOfMemoryError);
                        y = grid.getCellYDouble(row, handleOutOfMemoryError);
                        if (result.isInGrid(x, y, handleOutOfMemoryError)) {
                            value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                            if (value != noDataValue) {
                                count.addToCell(x, y, 1.0d, handleOutOfMemoryError);
                                result.addToCell(x, y, value, handleOutOfMemoryError);
                            }
                            normaliser.addToCell(x, y, 1.0d, handleOutOfMemoryError);
                        }
                    }
                }
                //            // Add the nearest values for the noDataValues so long as there is a value
                //            for ( row = 0; row < nrows; row ++ ) {
                //                for ( col = 0; col < ncols; col ++ ) {
                //                    x = grid.getCellXDouble( col, this.handleOutOfMemoryError );
                //                    y = grid.getCellYDouble( row, this.handleOutOfMemoryError );
                //                    if ( result.inGrid( x, y, this.handleOutOfMemoryError ) ) {
                //                        if ( dataCount.getCell( x, y, this.handleOutOfMemoryError ) != noDataValue ) {
                //                            result.addToCell( x, y, grid.getNearestValueDouble( row, col, this.handleOutOfMemoryError ), this.handleOutOfMemoryError );
                //                        }
                //                    }
                //                }
                //            }
                // Normalise
                double count0;
                for (row = 0; row < resultNrows; row++) {
                    for (col = 0; col < resultNcols; col++) {
                        count0 = count.getCell(row, col, handleOutOfMemoryError);
                        if (count0 != 0.0d) {
                            result.setCell(row, col, ((result.getCell(row, col, handleOutOfMemoryError) * normaliser.getCell(row, col, handleOutOfMemoryError)) / count0), handleOutOfMemoryError);
                        }
                    }
                }
            }

            // mean
            if (statistic.equalsIgnoreCase("mean")) {
                Grids_Grid2DSquareCellDouble numerator = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);
                Grids_Grid2DSquareCellDouble denominator = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col, handleOutOfMemoryError);
                        y = grid.getCellYDouble(row, handleOutOfMemoryError);
                        if (result.isInGrid(x, y, handleOutOfMemoryError)) {
                            value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                            if (value != noDataValue) {
                                numerator.addToCell(x, y, value, handleOutOfMemoryError);
                                denominator.addToCell(x, y, 1.0d, handleOutOfMemoryError);
                            }
                        }
                    }
                }
                for (row = 0; row < resultNrows; row++) {
                    for (col = 0; col < resultNcols; col++) {
                        value = numerator.getCell(row, col, handleOutOfMemoryError);
                        if (value != noDataValue) {
                            result.setCell(row, col, value / denominator.getCell(row, col, handleOutOfMemoryError), handleOutOfMemoryError);
                        }
                    }
                }
            }

            // min
            if (statistic.equalsIgnoreCase("min")) {
                double min;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col, handleOutOfMemoryError);
                        y = grid.getCellYDouble(row, handleOutOfMemoryError);
                        if (result.isInGrid(x, y, handleOutOfMemoryError)) {
                            value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                            if (value != noDataValue) {
                                min = result.getCell(x, y, handleOutOfMemoryError);
                                if (min != noDataValue) {
                                    result.setCell(x, y, Math.min(min, value), handleOutOfMemoryError);
                                } else {
                                    result.setCell(x, y, value, handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                }
            }

            // max
            if (statistic.equalsIgnoreCase("max")) {
                double max;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        x = grid.getCellXDouble(col, handleOutOfMemoryError);
                        y = grid.getCellYDouble(row, handleOutOfMemoryError);
                        if (result.isInGrid(x, y, handleOutOfMemoryError)) {
                            value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                            if (value != noDataValue) {
                                max = result.getCell(x, y, handleOutOfMemoryError);
                                if (max != noDataValue) {
                                    result.setCell(x, y, Math.max(max, value), handleOutOfMemoryError);
                                } else {
                                    result.setCell(x, y, value, handleOutOfMemoryError);
                                }
                            }
                        }
                    }
                }
            }
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return aggregate(
                        grid,
                        cellFactor,
                        statistic,
                        rowOffset,
                        colOffset,
                        gridFactory,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }

        }
    }

    //    /**
    //     * Returns an Grids_Grid2DSquareCellDouble at a lower level of resolution than grid.  The result values
    //     * are either the sum, mean, max or min of values in grid depending on statistic.
    //     * @param grid - the Grids_Grid2DSquareCellDouble to be processed
    //     * @param resultCellsize - output grid cellsize
    //     * @param statistic - "sum", "mean", "max", or "min" depending on what aggregate of values are wanted
    //     * @param resultXllcorner - the x-coordinate of the aggregate grid lower left corner
    //     * @param resultYllcorner - the y-coordinate of the aggregate grid lower left corner
    //     * Use this aggregate method to force origin of the result to be ( resultXllcorner, resultYllcorner ) and
    //     * if resultCellsize is not an integer multiple of this.cellsize.
    //     * NB. In the calculation of the sum and the mean if there is a cell in grid which has a data value then
    //     *     the result which incorporates that cell has a data value.  For this result cell, any of the cells in
    //     *     grid which have noDataValues their value is taken as that of the average of its nearest cells with
    //     *     a value.
    //     *     In the calculation of the max and the min noDataValues are simply ignored.
    //     *     Formerly noDataValues were treated as the average of values within a result cell.
    //     * TODO: implement median, mode and variance aggregations.
    //     */
    //    public Grids_Grid2DSquareCellDouble aggregate( Grids_Grid2DSquareCellDouble grid, double resultCellsize, String statistic, double resultXllcorner, double resultYllcorner ) {
    //        try {
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grids_Grid2DSquareCellDoubleFactory() );
    //        } catch ( OutOfMemoryError e ) {
    //            return aggregate( grid, resultCellsize, statistic, resultXllcorner, resultYllcorner, new Grid2DSquareCellDoubleFileFactory() );
    //        }
    //    }
    /**
     * Returns an Grids_Grid2DSquareCellDouble at a lower level of resolution than
 grid. The result values are either the sum, mean, max or min of values in
     * grid depending on statistic.
     *
     * @param grid The Grids_Grid2DSquareCellDouble to be processed
     * @param statistic "sum", "mean", "max", or "min" depending on what
     * aggregate of values are wanted
     * @param resultDimensions
     * @param gridFactory The Abstract2DSquareCellDoubleFactory used to create
     * _AbstractGrid2DSquareCell_HashSet
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown. Use this aggregate
     * method if result is to have a new spatial frame. NB. In the calculation
     * of the sum and the mean if there is a cell in grid which has a data value
     * then the result which incorporates that cell has a data value. For this
     * result cell, any of the cells in grid which have noDataValues their value
     * is taken as that of the average of its nearest cells with a value. In the
     * calculation of the max and the min noDataValues are simply ignored.
     * Formerly noDataValues were treated as the average of values within a
     * result cell. TODO: implement median, mode and variance aggregations.
     * <a name="aggregate(AbstractGrid2DSquareCell,
     * String,BigDecimal[],Grid2DSquareCellDoubleFactory,boolean)"></a>
     * @return
     */
    public Grids_Grid2DSquareCellDouble aggregate(
            Grids_AbstractGrid2DSquareCell grid,
            String statistic,
            BigDecimal[] resultDimensions,
            Grids_Grid2DSquareCellDoubleFactory gridFactory,
            boolean handleOutOfMemoryError) {
        try {
            ge.get_AbstractGrid2DSquareCell_HashSet().add(grid);
            int _MessageLength = 1000;
            String _Message0 = ge.initString(_MessageLength, handleOutOfMemoryError);
            String _Message = ge.initString(_MessageLength, handleOutOfMemoryError);
            int scale = 325;
            // Initialistaion
            long nrows = grid.get_NRows(handleOutOfMemoryError);
            long ncols = grid.get_NCols(handleOutOfMemoryError);
            BigDecimal[] dimensions = grid.get_Dimensions(handleOutOfMemoryError);
            double noDataValue = Double.NEGATIVE_INFINITY;
            if (grid.getClass() == Grids_Grid2DSquareCellInt.class) {
                noDataValue = (double) ((Grids_Grid2DSquareCellInt) grid).getNoDataValue(handleOutOfMemoryError);
            } else {
                if (grid.getClass() == Grids_Grid2DSquareCellDouble.class) {
                    noDataValue = ((Grids_Grid2DSquareCellDouble) grid).get_NoDataValue(handleOutOfMemoryError);
                } else {
                    _Message = "Grid2DSquareCellAbstract not recognised in aggregate( Grid2DSquareCellAbstract( " + grid.toString(handleOutOfMemoryError) + ", statistic( " + statistic + " ), resultDimensions( " + resultDimensions + " ), gridFactory( " + gridFactory + " ),  handleOutOfMemoryError( " + handleOutOfMemoryError + " ) )";
                    _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
//throw new Exception();
                }
            }
            double cellsize = resultDimensions[0].doubleValue();

            //double width = cellsize * ncols;
            //double height = cellsize * nrows;
            // Test this is an aggregation
            if (resultDimensions[0].compareTo(dimensions[0]) != 1) {
                _Message = "!!!Warning: Not an aggregation as resultCellsize < cellsize. Returning null!";
                _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                return null;
            }
            // Test for intersection
            if ((resultDimensions[1].compareTo(dimensions[1].add(dimensions[0].multiply(new BigDecimal(Long.toString(ncols))))) == 1) || (resultDimensions[2].compareTo(dimensions[2].add(dimensions[0].multiply(new BigDecimal(Long.toString(nrows))))) == 1)) {
                _Message = "!!!Warning: No intersection for aggregation. Returning null!";
                _Message = ge.println(_Message, _Message0, handleOutOfMemoryError);
                return null;
            }
            // If resultCellsize is an integer multiple of cellsize and grid aligns with result then use
            // a cellFactor aggregation as it should be faster.
            //println("resultCellsize % cellsize == " + ( resultCellsize % cellsize ) );
            //println("resultXllcorner % cellsize = " + ( resultXllcorner % cellsize ) + ", xllcorner % cellsize = " + ( xllcorner % cellsize ) );
            //println("resultYllcorner % cellsize = " + ( resultYllcorner % cellsize ) + ", yllcorner % cellsize = " + ( yllcorner % cellsize ) );
            if (true) {
                BigDecimal t0 = resultDimensions[0].divide(dimensions[0], Math.max(resultDimensions[0].scale(), dimensions[0].scale()) + 2, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t1 = resultDimensions[1].divide(dimensions[0], Math.max(resultDimensions[1].scale(), dimensions[0].scale()) + 2, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t2 = dimensions[1].divide(dimensions[0], Math.max(dimensions[1].scale(), dimensions[0].scale()) + 2, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t3 = resultDimensions[2].divide(dimensions[0], Math.max(resultDimensions[2].scale(), dimensions[0].scale()) + 2, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal t4 = dimensions[2].divide(dimensions[0], Math.max(dimensions[2].scale(), dimensions[0].scale()) + 2, BigDecimal.ROUND_HALF_EVEN);
                if ((t0.compareTo(new BigDecimal(t0.toBigInteger().toString())) == 0)
                        && (t1.compareTo(new BigDecimal(t1.toBigInteger().toString())) == t2.compareTo(new BigDecimal(t2.toBigInteger().toString())))
                        && (t3.compareTo(new BigDecimal(t3.toBigInteger().toString())) == t4.compareTo(new BigDecimal(t4.toBigInteger().toString())))) {
                    int cellFactor = resultDimensions[0].divide(dimensions[0], 2, BigDecimal.ROUND_UNNECESSARY).intValue();
                    int rowOffset = dimensions[2].subtract(resultDimensions[2].divide(dimensions[0], scale, BigDecimal.ROUND_HALF_EVEN)).intValue();
                    int colOffset = dimensions[1].subtract(resultDimensions[1].divide(dimensions[0], scale, BigDecimal.ROUND_HALF_EVEN)).intValue();
                    return aggregate(grid, cellFactor, statistic, rowOffset, colOffset, gridFactory, handleOutOfMemoryError);
                }
            }
            // Calculate resultNrows and resultHeight
            long resultNrows = 1L;
            BigDecimal resultHeight = new BigDecimal(resultDimensions[0].toString());
            //double resultHeight = resultCellsize;
            while (resultDimensions[2].add(resultHeight).compareTo(dimensions[4]) == -1) {
                resultNrows++;
                resultHeight = resultHeight.add(resultDimensions[0]);
            }
            //while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
            //    resultNrows ++;
            //    resultHeight += resultCellsize;
            //}
            // Calculate resultNcols and resultWidth
            long resultNcols = 1L;
            BigDecimal resultWidth = new BigDecimal(resultDimensions[0].toString());
            //double resultWidth = resultCellsize;
            while (resultDimensions[1].add(resultWidth).compareTo(dimensions[3]) == -1) {
                resultNrows++;
                resultWidth = resultWidth.add(resultDimensions[0]);
            }
            //while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
            //    resultNcols ++;
            //    resultWidth += resultCellsize;
            //}
            resultDimensions[3] = dimensions[1].add(resultWidth);
            resultDimensions[4] = dimensions[2].add(resultHeight);

            // Initialise result
            gridFactory.set_NoDataValue(noDataValue);
            Grids_Grid2DSquareCellDouble result = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);

            long row;
            long col;
            double x;
            double y;
            double value;

            // sum
            if (statistic.equalsIgnoreCase("sum")) {
                Grids_Grid2DSquareCellDouble totalValueArea = (Grids_Grid2DSquareCellDouble) gridFactory.create(resultNrows, resultNcols, resultDimensions);
                double areaProportion;
                double[] bounds = new double[4];
                Grids_2D_ID_long[] _CellIDs = new Grids_2D_ID_long[4];

                double count0;
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        bounds = grid.getCellBoundsDoubleArray(row, col, handleOutOfMemoryError);
                        _CellIDs[0] = result.getCellID(bounds[0], bounds[3], handleOutOfMemoryError);
                        _CellIDs[1] = result.getCellID(bounds[2], bounds[3], handleOutOfMemoryError);
                        _CellIDs[2] = result.getCellID(bounds[0], bounds[1], handleOutOfMemoryError);
                        _CellIDs[3] = result.getCellID(bounds[2], bounds[1], handleOutOfMemoryError);
                        value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                        if (value != noDataValue) {
                            if (_CellIDs[0].equals(_CellIDs[1]) && _CellIDs[1].equals(_CellIDs[2])) {
                                result.addToCell(_CellIDs[0], value, handleOutOfMemoryError);
                                totalValueArea.addToCell(_CellIDs[0], 1.0d, handleOutOfMemoryError);
                            } else {
                                if (_CellIDs[0].equals(_CellIDs[1]) || _CellIDs[0].equals(_CellIDs[2])) {
                                    if (_CellIDs[0].equals(_CellIDs[1])) {
                                        areaProportion = (Math.abs(bounds[3] - (result.getCellYDouble(_CellIDs[0], handleOutOfMemoryError) - (resultDimensions[0].doubleValue() / 2.0d))) * cellsize) / (cellsize * cellsize);
                                    } else {
                                        areaProportion = (Math.abs((result.getCellXDouble(_CellIDs[0], handleOutOfMemoryError) + (resultDimensions[0].doubleValue() / 2.0d)) - bounds[0]) * cellsize) / (cellsize * cellsize);
                                    }
                                } else {
                                    areaProportion = ((Math.abs(bounds[3] - (result.getCellYDouble(_CellIDs[0], handleOutOfMemoryError) - (resultDimensions[0].doubleValue() / 2.0d))) * Math.abs((result.getCellXDouble(_CellIDs[0], handleOutOfMemoryError) + (resultDimensions[0].doubleValue() / 2.0d)) - bounds[0])) / (cellsize * cellsize));
                                }
                                result.addToCell(_CellIDs[0], value * areaProportion, handleOutOfMemoryError);
                                totalValueArea.addToCell(_CellIDs[0], areaProportion, handleOutOfMemoryError);
                            }
                            if (!_CellIDs[1].equals(_CellIDs[0])) {
                                if (_CellIDs[1].equals(_CellIDs[3])) {
                                    areaProportion = (Math.abs(bounds[2] - (result.getCellXDouble(_CellIDs[1], handleOutOfMemoryError) - (resultDimensions[0].doubleValue() / 2.0d))) * cellsize) / (cellsize * cellsize);
                                } else {
                                    areaProportion = ((Math.abs(bounds[3] - (result.getCellYDouble(_CellIDs[1], handleOutOfMemoryError) - (resultDimensions[0].doubleValue() / 2.0d))) * Math.abs(bounds[2] - (result.getCellXDouble(_CellIDs[1], handleOutOfMemoryError) - (resultDimensions[0].doubleValue() / 2.0d)))) / (cellsize * cellsize));
                                }
                                result.addToCell(_CellIDs[1], value * areaProportion, handleOutOfMemoryError);
                                totalValueArea.addToCell(_CellIDs[0], areaProportion, handleOutOfMemoryError);
                            }
                            if (!_CellIDs[2].equals(_CellIDs[0])) {
                                if (!_CellIDs[2].equals(_CellIDs[3])) {
                                    areaProportion = (Math.abs((result.getCellYDouble(_CellIDs[2], handleOutOfMemoryError) + (resultDimensions[0].doubleValue() / 2.0d)) - bounds[1]) * cellsize) / (cellsize * cellsize);
                                } else {
                                    areaProportion = ((Math.abs((result.getCellYDouble(_CellIDs[2], handleOutOfMemoryError) + (resultDimensions[0].doubleValue() / 2.0d)) - bounds[1]) * Math.abs((result.getCellXDouble(_CellIDs[2], handleOutOfMemoryError) + (resultDimensions[0].doubleValue() / 2.0d)) - bounds[0])) / (cellsize * cellsize));
                                }
                                result.addToCell(_CellIDs[2], value * areaProportion, handleOutOfMemoryError);
                            }
                            if (!_CellIDs[3].equals(_CellIDs[1]) && !_CellIDs[3].equals(_CellIDs[2])) {
                                areaProportion = ((Math.abs((result.getCellYDouble(_CellIDs[3], handleOutOfMemoryError) + (resultDimensions[0].doubleValue() / 2.0d)) - bounds[1]) * Math.abs(bounds[2] - (result.getCellXDouble(_CellIDs[3], handleOutOfMemoryError) - (resultDimensions[0].doubleValue() / 2.0d)))) / (cellsize * cellsize));
                                result.addToCell(_CellIDs[3], value * areaProportion, handleOutOfMemoryError);
                                totalValueArea.addToCell(_CellIDs[0], areaProportion, handleOutOfMemoryError);
                            }
                        }
                    }
                }
                // Normalise
                double totalValueArea0;
                double resultCellsize = resultDimensions[0].doubleValue();
                for (row = 0; row < resultNrows; row++) {
                    for (col = 0; col < resultNcols; col++) {
                        totalValueArea0 = totalValueArea.getCell(row, col, handleOutOfMemoryError);
                        if (totalValueArea0 != 0.0d) {
                            result.setCell(row, col, ((result.getCell(row, col, handleOutOfMemoryError) * ((resultCellsize - cellsize) * (resultCellsize - cellsize))) / totalValueArea.getCell(row, col, handleOutOfMemoryError)), handleOutOfMemoryError);
                        }
                    }
                }

            }
            //        // Add the nearest values for the noDataValues so long as there is a value
            //            for ( int i = 0; i < nrows; i ++ ) {
            //                for ( int j = 0; j < ncols; j ++ ) {
            //                    bounds = grid.getCellBounds( i, j );
            //                    cellID1 = result.getCellID( bounds[ 0 ], bounds[ 3 ] );
            //                    cellID2 = result.getCellID( bounds[ 2 ], bounds[ 3 ] );
            //                    cellID3 = result.getCellID( bounds[ 0 ], bounds[ 1 ] );
            //                    cellID4 = result.getCellID( bounds[ 2 ], bounds[ 1 ] );
            //                    if ( dataCount.getCell( bounds[ 0 ], bounds[ 3 ] ) != noDataValue ||
            //                    dataCount.getCell( bounds[ 2 ], bounds[ 3 ] ) != noDataValue ||
            //                    dataCount.getCell( bounds[ 0 ], bounds[ 1 ] ) != noDataValue ||
            //                    dataCount.getCell( bounds[ 2 ], bounds[ 1 ] ) != noDataValue ) {
            //                        value = grid.getNearestValueDouble( i, j );
            //                        if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
            //                            if ( cellID1 != Integer.MIN_VALUE ) {
            //                                result.addToCell( cellID1, value );
            //                            }
            //                        } else {
            //                            if ( cellID1 != Integer.MIN_VALUE ) {
            //                                if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
            //                                    if ( cellID1 == cellID2 ) {
            //                                        areaProportion = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
            //                                    } else {
            //                                        areaProportion = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize ) / ( cellsize * cellsize );
            //                                    }
            //                                } else {
            //                                    areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
            //                                }
            //                                result.addToCell( cellID1, value * areaProportion );
            //                            }
            //                            if ( cellID2 != Integer.MIN_VALUE ) {
            //                                if ( cellID2 != cellID1 ) {
            //                                    if ( cellID2 == cellID4 ) {
            //                                        areaProportion = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
            //                                    } else {
            //                                        areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
            //                                    }
            //                                    result.addToCell( cellID2, value * areaProportion );
            //                                }
            //                            }
            //                            if ( cellID3 != Integer.MIN_VALUE ) {
            //                                if ( cellID3 != cellID1 ) {
            //                                    if ( cellID3 == cellID4 ) {
            //                                        areaProportion = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize ) / ( cellsize * cellsize );
            //                                    } else {
            //                                        areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
            //                                    }
            //                                    result.addToCell( cellID3, value * areaProportion );
            //                                }
            //                            }
            //                            if ( cellID4 != Integer.MIN_VALUE ) {
            //                                if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
            //                                    areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
            //                                    result.addToCell( cellID4, value * areaProportion );
            //                                }
            //                            }
            //                        }
            //                    }
            //                }
            //            }
            //        }

            // mean
            if (statistic.equalsIgnoreCase("mean")) {
                double denominator = (resultDimensions[0].doubleValue() * resultDimensions[0].doubleValue()) / (cellsize * cellsize);
                Grids_Grid2DSquareCellDouble sum = aggregate(grid, "sum", resultDimensions, gridFactory, handleOutOfMemoryError);
                addToGrid(result, sum, 1.0d / denominator, handleOutOfMemoryError);
            }

            // max
            if (statistic.equalsIgnoreCase("max")) {
                double max;
                double[] bounds = new double[4];
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                        if (value != noDataValue) {
                            x = grid.getCellXDouble(col, handleOutOfMemoryError);
                            y = grid.getCellYDouble(row, handleOutOfMemoryError);
                            bounds = grid.getCellBoundsDoubleArray(row, col, handleOutOfMemoryError);
                            max = result.getCell(bounds[0], bounds[3], handleOutOfMemoryError);
                            if (max != noDataValue) {
                                result.setCell(bounds[0], bounds[3], Math.max(max, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[0], bounds[3], value, handleOutOfMemoryError);
                            }
                            max = result.getCell(bounds[2], bounds[3], handleOutOfMemoryError);
                            if (max != noDataValue) {
                                result.setCell(bounds[2], bounds[3], Math.max(max, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[2], bounds[3], value, handleOutOfMemoryError);
                            }
                            max = result.getCell(bounds[0], bounds[1], handleOutOfMemoryError);
                            if (max != noDataValue) {
                                result.setCell(bounds[0], bounds[1], Math.max(max, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[0], bounds[1], value, handleOutOfMemoryError);
                            }
                            max = result.getCell(bounds[2], bounds[1], handleOutOfMemoryError);
                            if (max != noDataValue) {
                                result.setCell(bounds[2], bounds[1], Math.max(max, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[2], bounds[1], value, handleOutOfMemoryError);
                            }
                        }
                    }
                }
            }

            // min
            if (statistic.equalsIgnoreCase("min")) {
                double min;
                double[] bounds = new double[4];
                for (row = 0; row < nrows; row++) {
                    for (col = 0; col < ncols; col++) {
                        value = grid.getCellDouble(row, col, handleOutOfMemoryError);
                        if (value != noDataValue) {
                            x = grid.getCellXDouble(col, handleOutOfMemoryError);
                            y = grid.getCellYDouble(row, handleOutOfMemoryError);
                            bounds = grid.getCellBoundsDoubleArray(row, col, handleOutOfMemoryError);
                            min = result.getCell(bounds[0], bounds[3], handleOutOfMemoryError);
                            if (min != noDataValue) {
                                result.setCell(bounds[0], bounds[3], Math.min(min, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[0], bounds[3], value, handleOutOfMemoryError);
                            }
                            min = result.getCell(bounds[2], bounds[3], handleOutOfMemoryError);
                            if (min != noDataValue) {
                                result.setCell(bounds[2], bounds[3], Math.min(min, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[2], bounds[3], value, handleOutOfMemoryError);
                            }
                            min = result.getCell(bounds[0], bounds[1], handleOutOfMemoryError);
                            if (min != noDataValue) {
                                result.setCell(bounds[0], bounds[1], Math.min(min, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[0], bounds[1], value, handleOutOfMemoryError);
                            }
                            min = result.getCell(bounds[2], bounds[1], handleOutOfMemoryError);
                            if (min != noDataValue) {
                                result.setCell(bounds[2], bounds[1], Math.min(min, value), handleOutOfMemoryError);
                            } else {
                                result.setCell(bounds[2], bounds[1], value, handleOutOfMemoryError);
                            }
                        }
                    }
                }
            }

            /*
             // Initialistaion
             int nrows = grid.get_NRows();
             int ncols = grid.get_NCols();
             double xllcorner = grid.getXllcorner();
             double yllcorner = grid.getYllcorner();
             double cellsize = grid.getCellsize();
             double noDataValue = grid.get_NoDataValue();
             double width = cellsize * ncols;
             double height = cellsize * nrows;
             // Test this is an aggregation
             if ( resultCellsize <= cellsize ) {
             println( "!!!Warning: resultCellsize <= cellsize in aggregate( cellsize( " + resultCellsize + " ), statistic( " + statistic + " ), resultXllcorner( " + resultXllcorner + " ), resultYllcorner( " + resultYllcorner + " ), noDataValue( " + noDataValue + " ), gridFactory( " + gridFactory.toString() + " ) ). Returning null!" );
             return null;
             }
             // Test for intersection
             if ( ( resultXllcorner > xllcorner + ( ( double ) ncols * cellsize ) ) || ( resultYllcorner > yllcorner + ( ( double ) nrows * cellsize ) ) ) {
             println( "!!!Warning: No intersection in aggregate( cellsize( " + resultCellsize + " ), statistic( " + statistic + " ), resultXllcorner( " + resultXllcorner + " ), resultYllcorner( " + resultYllcorner + " ), noDataValue( " + noDataValue + " ), gridFactory( " + gridFactory.toString() + " ) ). Returning null!" );
             return null;
             }
             // If resultCellsize is an integer multiple of cellsize and grid aligns with result then use
             // a cellFactor aggregation as it should be faster.
             //println("resultCellsize % cellsize == " + ( resultCellsize % cellsize ) );
             //println("resultXllcorner % cellsize = " + ( resultXllcorner % cellsize ) + ", xllcorner % cellsize = " + ( xllcorner % cellsize ) );
             //println("resultYllcorner % cellsize = " + ( resultYllcorner % cellsize ) + ", yllcorner % cellsize = " + ( yllcorner % cellsize ) );
             if ( ( resultCellsize % cellsize == 0.0d ) && ( ( resultXllcorner % cellsize ) == ( xllcorner % cellsize ) ) && ( ( resultYllcorner % cellsize ) == ( yllcorner % cellsize ) ) ) {
             int cellFactor = ( int ) ( resultCellsize / cellsize );
             int rowOffset = ( int ) ( yllcorner - resultYllcorner / cellsize );
             int colOffset = ( int ) ( xllcorner - resultXllcorner / cellsize );
             return aggregate( grid, cellFactor, statistic, rowOffset, colOffset, gridFactory );
             }
             // Calculate resultNrows and resultHeight
             int resultNrows = 1;
             double resultHeight = resultCellsize;
             while ( ( resultYllcorner + resultHeight ) < ( yllcorner + height ) ) {
             resultNrows ++;
             resultHeight += resultCellsize;
             }
             // Calculate resultNcols and resultWidth
             int resultNcols = 1;
             double resultWidth = resultCellsize;
             while ( ( resultXllcorner + resultWidth ) < ( xllcorner + width ) ) {
             resultNcols ++;
             resultWidth += resultCellsize;
             }
             //println( "resultNcols " + resultNcols + ", resultNrows " + resultNrows );
             //println( "gridToAddNcols " + ncols + ", gridToAddNrows " + nrows );
             // Initialise result
             Grids_Grid2DSquareCellDouble result = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue );
            
             // sum
             if ( statistic.equalsIgnoreCase( "sum" ) ) {
             Grids_Grid2DSquareCellDouble tempGrid1 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             Grids_Grid2DSquareCellDouble tempGrid2 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             double x = 0.0d;
             double y = 0.0d;
             double d1 = noDataValue;
             double areaProportion = 0.0d;
             double[] bounds = new double[ 4 ];
             int cellID1 = -1;
             int cellID2 = -1;
             int cellID3 = -1;
             int cellID4 = -1;
             //double totalArea = 0;
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             cellID1 = result.getCellID( bounds[ 0 ], bounds[ 3 ] );
             cellID2 = result.getCellID( bounds[ 2 ], bounds[ 3 ] );
             cellID3 = result.getCellID( bounds[ 0 ], bounds[ 1 ] );
             cellID4 = result.getCellID( bounds[ 2 ], bounds[ 1 ] );
             if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
             if ( cellID1 != Integer.MIN_VALUE ) {
             areaProportion = 1.0d;
             tempGrid1.addToCell( x, y, d1 * areaProportion );
             tempGrid2.addToCell( x, y, areaProportion );
             }
             } else {
             if ( cellID1 != Integer.MIN_VALUE ) {
             if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
             if ( cellID1 == cellID2 ) {
             areaProportion = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
             } else {
             areaProportion = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize ) / ( cellsize * cellsize );
             }
             } else {
             areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
             }
             tempGrid1.addToCell( cellID1, d1 * areaProportion );
             tempGrid2.addToCell( cellID1, areaProportion );
             }
             if ( cellID2 != Integer.MIN_VALUE ) {
             if ( cellID2 != cellID1 ) {
             if ( cellID2 == cellID4 ) {
             areaProportion = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize ) / ( cellsize * cellsize );
             } else {
             areaProportion = ( ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
             }
             tempGrid1.addToCell( cellID2, d1 * areaProportion );
             tempGrid2.addToCell( cellID2, areaProportion );
             }
             }
             if ( cellID3 != Integer.MIN_VALUE ) {
             if ( cellID3 != cellID1 ) {
             if ( cellID3 == cellID4 ) {
             areaProportion = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize ) / ( cellsize * cellsize );
             } else {
             areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d) ) - bounds[0] ) ) / ( cellsize * cellsize ) );
             }
             tempGrid1.addToCell( cellID3, d1 * areaProportion );
             tempGrid2.addToCell( cellID3, areaProportion );
             }
             }
             if ( cellID4 != Integer.MIN_VALUE ) {
             if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
             areaProportion = ( ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) ) / ( cellsize * cellsize ) );
             tempGrid1.addToCell( cellID4, d1 * areaProportion );
             tempGrid2.addToCell( cellID4, areaProportion );
             }
             }
             // Check fails due to rounding errors!
             //if ( cellID1 != Integer.MIN_VALUE && cellID2 != Integer.MIN_VALUE && cellID3 != Integer.MIN_VALUE && cellID4 != Integer.MIN_VALUE && totalArea != 1.0 ) { println( "id = " + i + " : totalArea = " + totalArea + " (cellID1,cellID2,cellID3,cellID4) = (" + cellID1 + "," + cellID2 + "," + cellID3 + "," + cellID4 + ")" );
             //    throw an exception!!!
             //}
             }
             }
             }
             // The values are normalised by dividing the aggregate Grid sum by the proportion of cells with grid values.
             for ( int i = 0; i < resultNrows * resultNcols; i ++ ) {
             d1 = tempGrid2.getCell( i );
             if ( d1 != 0.0d && d1 != noDataValue ) {
             result.setCell( i, tempGrid1.getCell( i ) / ( Math.pow( ( resultCellsize / cellsize ), 2.0d ) / d1 ) );
             }
             }
             tempGrid1.clear();
             tempGrid2.clear();
             }
            
             // mean
             if ( statistic.equalsIgnoreCase( "mean" ) ) {
             // To calculate the mean and cope with NODATA it is necessary to pass
             // through the data twice or for each aggregated cell get all
             // intersecting cells. This is because each cells area as a proportion
             // of the non noDataValue area of an aggregated cell is needed. This
             // cannot be simply done as it all depends on NODATA. In the
             // implementation below the data is read through twice. First read
             // involves calculating NODATA in each aggregated cell.
             Grids_Grid2DSquareCellDouble tempGrid1 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             Grids_Grid2DSquareCellDouble tempGrid2 = gridFactory.createGrid2DSquareCellDouble( resultNrows, resultNcols, resultXllcorner, resultYllcorner, resultCellsize, noDataValue, 1 );
             double x;
             double y;
             double d1;
             double area;
             double[] bounds = new double[4];
             int cellID1;
             int cellID2;
             int cellID3;
             int cellID4;
             //double totalArea = 0;
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             cellID1 = result.getCellID( bounds[0], bounds[3] );
             cellID2 = result.getCellID( bounds[2], bounds[3] );
             cellID3 = result.getCellID( bounds[0], bounds[1] );
             cellID4 = result.getCellID( bounds[2], bounds[1] );
             if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
             if ( cellID1 != Integer.MIN_VALUE ) {
             area = cellsize * cellsize;
             tempGrid1.addToCell( x, y, area );
             }
             } else {
             if ( cellID1 != Integer.MIN_VALUE ) {
             if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
             if ( cellID1 == cellID2 ) {
             area = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             area = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize );
             }
             } else {
             area = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             tempGrid1.addToCell( cellID1, area );
             }
             if ( cellID2 != Integer.MIN_VALUE ) {
             if ( cellID2 != cellID1 ) {
             if ( cellID2 == cellID4 ) {
             area = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             area = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) );
             }
             tempGrid1.addToCell( cellID2, area );
             }
             }
             if ( cellID3 != Integer.MIN_VALUE ) {
             if ( cellID3 != cellID1 ) {
             if ( cellID3 == cellID4 ) {
             area = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize );
             } else {
             area = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             tempGrid1.addToCell( cellID3, area );
             }
             }
             if ( cellID4 != Integer.MIN_VALUE ) {
             if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
             area = ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) );
             tempGrid1.addToCell( cellID4, area );
             }
             }
             }
             }
             }
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             double areaIntersect;
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             cellID1 = result.getCellID( bounds[0], bounds[3] );
             cellID2 = result.getCellID( bounds[2], bounds[3] );
             cellID3 = result.getCellID( bounds[0], bounds[1] );
             cellID4 = result.getCellID( bounds[2], bounds[1] );
             if ( cellID1 == cellID2 && cellID2 == cellID3 ) {
             if ( cellID1 != Integer.MIN_VALUE ) {
             area = tempGrid1.getCell( x, y );
             result.addToCell( x, y, d1 * ( cellsize * cellsize ) / area );
             }
             } else {
             if ( cellID1 != Integer.MIN_VALUE ) {
             if ( cellID1 == cellID2 || cellID1 == cellID3 ) {
             if ( cellID1 == cellID2 ) {
             areaIntersect = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             areaIntersect = ( Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) * cellsize );
             }
             } else {
             areaIntersect = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID1 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( ( result.getCellXDouble( cellID1 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             area = tempGrid1.getCell( cellID1 );
             result.addToCell( cellID1, d1 * areaIntersect / area );
             }
             if ( cellID2 != Integer.MIN_VALUE ) {
             if ( cellID2 != cellID1 ) {
             if ( cellID2 == cellID4 ) {
             areaIntersect = ( Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * cellsize );
             } else {
             areaIntersect = ( Math.abs( bounds[3] - ( result.getCellYDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID2 ) - ( resultCellsize / 2.0d ) ) ) );
             }
             area = tempGrid1.getCell( cellID2 );
             result.addToCell( cellID2, d1 * areaIntersect / area );
             }
             }
             if ( cellID3 != Integer.MIN_VALUE ) {
             if ( cellID3 != cellID1 ) {
             if ( cellID3 == cellID4 ) {
             areaIntersect = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * cellsize );
             } else {
             areaIntersect = ( Math.abs( ( result.getCellYDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( ( result.getCellXDouble( cellID3 ) + ( resultCellsize / 2.0d ) ) - bounds[0] ) );
             }
             area = tempGrid1.getCell( cellID3 );
             result.addToCell( cellID3, d1 * areaIntersect / area );
             }
             }
             if ( cellID4 != Integer.MIN_VALUE ) {
             if ( cellID4 != cellID2 && cellID4 != cellID3 ) {
             areaIntersect = ( Math.abs( ( result.getCellYDouble( cellID4 ) + ( resultCellsize / 2.0d ) ) - bounds[1] ) * Math.abs( bounds[2] - ( result.getCellXDouble( cellID4 ) - ( resultCellsize / 2.0d ) ) ) );
             area = tempGrid1.getCell( cellID4 );
             result.addToCell( cellID4, d1 * areaIntersect / area );
             }
             }
             }
             }
             }
             tempGrid1.clear();
             }
            
             // max
             if ( statistic.equalsIgnoreCase( "max" ) ) {
             double x;
             double y;
             double d1;
             double d2;
             double[] bounds = new double[4];
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             d2 = result.getCell( bounds[0], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[3], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[3], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[0], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[1], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[1], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[1], Math.max( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[1], d1 );
             }
             }
             }
             }
            
             // min
             if ( statistic.equalsIgnoreCase( "min" ) ) {
             double x;
             double y;
             double d1;
             double d2;
             double[] bounds = new double[4];
             for ( int i = 0; i < nrows * ncols; i ++ ) {
             d1 = grid.getCell( i );
             if ( d1 != noDataValue ) {
             x = grid.getCellXDouble( i );
             y = grid.getCellYDouble( i );
             bounds = grid.getCellBounds( i );
             d2 = result.getCell( bounds[0], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[3], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[3] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[3], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[3], d1 );
             }
             d2 = result.getCell( bounds[0], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[0], bounds[1], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[0], bounds[1], d1 );
             }
             d2 = result.getCell( bounds[2], bounds[1] );
             if ( d2 != noDataValue ) {
             result.setCell( bounds[2], bounds[1], Math.min( d2, d1 ) );
             } else {
             result.setCell( bounds[2], bounds[1], d1 );
             }
             }
             }
             }
             */
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                return aggregate(
                        grid,
                        statistic,
                        resultDimensions,
                        gridFactory,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    //    TODO: Move to a Extended Statistics class
    //    /**
    //     * Returns a double global statistic for an Grids_Grid2DSquareCellDouble grid
    //     * NB. Only for _AbstractGrid2DSquareCell_HashSet in the same spatial frame.
    //     */
    //    public double globalBivariateStatistics( Grids_Grid2DSquareCellDouble grid0, Grids_Grid2DSquareCellDouble grid1, String comparator  ) {
    //        // Initialisation
    //        int grid0Nrows = grid0.get_NRows();
    //        int grid0Ncols = grid0.get_NCols();
    //        double grid0Xllcorner = grid0.getXllcorner();
    //        double grid0Yllcorner = grid0.getYllcorner();
    //        double grid0Cellsize = grid0.getCellsize();
    //        double grid0NoDataValue = grid0.get_NoDataValue();
    //        int grid1Nrows = grid1.get_NRows();
    //        int grid1Ncols = grid1.get_NCols();
    //        double grid1Xllcorner = grid1.getXllcorner();
    //        double grid1Yllcorner = grid1.getYllcorner();
    //        double grid1Cellsize = grid1.getCellsize();
    //        double grid1NoDataValue = grid1.get_NoDataValue();
    //        AbstractGridStatistics grid0Statistics = grid0.getGridStatistics();
    //        AbstractGridStatistics grid1Statistics = grid1.getGridStatistics();
    //        // TODO: Check spatial frame
    //
    //        // Calculation
    //        double thisDistance;
    //        double x0;
    //        double x1;
    //        double y0;
    //        double y1;
    //        double value0;
    //        double value1;
    //        // diff: The sum of all the differences between the grid cells
    //        if ( comparator.equalsIgnoreCase( "diff" ) ) {
    //            double diff = 0.0d;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue ) {
    //                        value1 = grid0.getCell( i, j );
    //                        if ( value1 != grid1NoDataValue ) {
    //                            diff += ( value0 - value1 );
    //                        }
    //                    }
    //                }
    //            }
    //            return diff;
    //        }
    //
    //        // abs: The sum of all the absolute differences between the grid cells
    //        if ( comparator.equalsIgnoreCase( "abs" ) ) {
    //            double abs = 0.0d;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue ) {
    //                        value1 = grid0.getCell( i, j );
    //                        if ( value1 != grid1NoDataValue ) {
    //                            abs += Math.abs( value0 - value1 );
    //                        }
    //                    }
    //                }
    //            }
    //            return abs;
    //        }
    //
    //        // pearsons: The persons correlation coefficient
    //        if ( comparator.equalsIgnoreCase( "pearsons" ) ) {
    //            double pearsons = grid0NoDataValue;
    //            double sum0 = 0.0d;
    //            double sum1 = 0.0d;
    //            double sum0Squared = 0.0d;
    //            double sum1Squared = 0.0d;
    //            double sum01 = 0.0d;
    //            double n = 0.0d;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    value1 = grid1.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue && value1 != grid0NoDataValue ) {
    //                        sum0 += value0;
    //                        sum1 += value1;
    //                        sum0Squared += Math.pow( value0, 2.0d );
    //                        sum1Squared += Math.pow( value1, 2.0d );
    //                        sum01 += value0 * value1;
    //                        n += 1.0d;
    //                    }
    //                }
    //            }
    //            double numerator = ( n * sum01 ) - ( sum0 * sum1 );
    //            double denominator = Math.sqrt( Math.abs( (  ( n * sum0Squared ) - Math.pow( sum0, 2.0d )  ) * ( ( n * sum1Squared ) - Math.pow( sum1, 2.0d ) ) ) );
    //            if ( denominator != 0.0d ) {
    //                pearsons = numerator / denominator;
    //            }
    //            return pearsons;
    //        }
    //
    //        // momentcorrelation: The moment correlation coefficient
    //        if ( comparator.equalsIgnoreCase( "momentCorrelation" ) ) {
    //            double momentCorrelation = grid0NoDataValue;
    //            double mean0 = grid0Statistics.getMean();
    //            double mean1 = grid1Statistics.getMean();
    //            double sd0 = 0.0d;
    //            double sd1 = 0.0d;
    //            double m01 = 0.0d;
    //            double n = 0.0d;
    //            double denominator;
    //            for ( int i = 0; i < grid0Nrows; i ++ ) {
    //                for ( int j = 0; j < grid0Ncols; j ++ ) {
    //                    value0 = grid0.getCell( i, j );
    //                    value1 = grid1.getCell( i, j );
    //                    if ( value0 != grid0NoDataValue && value1 != grid1NoDataValue ) {
    //                        n += 1.0d;
    //                        sd0 += Math.pow( value0 - mean0, 2.0d );
    //                        sd1 += Math.pow( value1 - mean1, 2.0d );
    //                        m01 += ( value0 - mean0 ) * ( value1 - mean1 );
    //                    }
    //                }
    //            }
    //            denominator = Math.sqrt( sd0 ) * Math.sqrt( sd1 );
    //            if ( denominator != 0.0d ) {
    //                momentCorrelation = m01 / denominator;
    //            }
    //            return momentCorrelation;
    //        }
    //        return grid0NoDataValue;
    //    }
    //    /**
    //     * Returns a new Grids_Grid2DSquareCellDouble the values of which are the distance to the nearest data value
    //     * TODO: Optimise as it is currently very slow and inefficient!!!
    //     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
    //     *   in this method then swap operations are initiated prior to retrying.
    //     *   If false then OutOfMemoryErrors are caught and thrown.
    //     */
    //    public Grids_Grid2DSquareCellDouble distanceToDataValue( Grids_Grid2DSquareCellDouble grid0, Grids_Grid2DSquareCellDoubleFactory gridFactory, boolean handleOutOfMemoryError ) {
    //        long nrows = grid0.get_NRows();
    //        long ncols = grid0.get_NCols();
    //        BigDecimal[] dimensions0 = grid0.get_Dimensions();
    //        double noDataValue = grid0.get_NoDataValue();
    //        Grids_Grid2DSquareCellDouble result = gridFactory.create( grid0.getChunkNRows(this.handleOutOfMemoryErrorFalse), grid0.getChunkNCols(this.handleOutOfMemoryErrorFalse), nrows, ncols, dimensions0, noDataValue );
    //        // Calculate distances
    //        long row;
    //        long col;
    //        for ( row = 0; row < nrows; row ++ ) {
    //            for ( col = 0; col < ncols; col ++ ) {
    //                result.setCell( row, col, grid0.getNearestValueDoubleDistance( row, col, this.handleOutOfMemoryError ), this.handleOutOfMemoryError );
    //            }
    //        }
    //        return result;
    //    }
    //    TODO:
    //    /**
    //     * Returns a new Grids_Grid2DSquareCellDouble which is a copy of grid0 but
    //     * with all noDataValues replaced as follows:
    //     * At each iteration we deal with the nearest band of noDataValues.
    //     * (NB. If we dealt only with the nearest noDataValues it may take a very long time to compute!)
    //     * We replace these nearest noDataValues with the average values within distance.
    //     * We then repeat until all cells have a value.
    //     */
    //    public Grids_Grid2DSquareCellDouble replaceNoDataValues( Grids_Grid2DSquareCellDouble grid0, double distance, Grids_Grid2DSquareCellDoubleFactory gridFactory ) {
    //        long nrows = grid0.get_NRows();
    //        long ncols = grid0.get_NCols();
    //        BigDecimal[] dimensions = grid0.get_Dimensions();
    //        double noDataValue = grid0.get_NoDataValue();
    //        Grids_Grid2DSquareCellDouble temp1 = gridFactory.createGrid2DSquareCellDouble( grid0 );
    //        Grids_Grid2DSquareCellDouble temp2 = gridFactory.createGrid2DSquareCellDouble( grid0 );
    //        // Get distances
    //        Grids_Grid2DSquareCellDouble distanceGrid = distanceToDataValue( grid0, gridFactory );
    //        AbstractGridStatistics distanceGridStatistics = distanceGrid.getGridStatistics();
    //        double maxDistance = distanceGridStatistics.getMax();
    //
    //        AbstractGrid2DSquareCellDouble[] geometricDensity = Grid2DSquareCellDoubleProcessorGWS.geometricDensity( grid0, maxDistance, gridFactory );
    //        //return geometricDensity[ geometricDensity.length - 1 ];
    //        return geometricDensity[ geometricDensity.length / 2 ];
    //        /*
    //        int maxCellDistance = ( int ) Math.ceil( maxDistance / cellsize );
    //        boolean alternator = true;
    //        Grids_Grid2DSquareCellDouble thisGrid = null;
    //        Grids_Grid2DSquareCellDouble thatGrid = null;
    //        double thisValue;
    //        double thatValue;
    //        double thisDistance;
    //        for ( int iterations = 1; iterations <= maxCellDistance; iterations ++ ) {
    //            for ( int i = 0; i < nrows; i ++ ) {
    //                for ( int j = 0; j < ncols; j ++ ) {
    //                    thisValue = thisGrid.getCell( i, j );
    //                    thatValue = thatGrid.getCell( i, j );
    //                    thisDistance = distanceGrid.getCell( i, j );
    //                    if ( thatValue != noDataValue ) {
    //                        thisGrid.setCell( i, j, thatValue );
    //                    } else {
    //                        if ( thatValue == noDataValue && thisDistance < ( iterations * cellsize ) ) {
    //                            thisGrid.setCell( i, j, Grid2DSquareCellDoubleProcessor.regionUnivariateStatistics( thatGrid, i, j, "mean", distance, 1.0d, 1.0d, gridFactory ) );
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        thatGrid.clear();
    //        return thisGrid;
    //         */
    //
    //    }
    /**
     * Shuffles dummyDiff numberOfShuffles places
     */
    private void shuffle(double[] dummyDiff, double[] diff, int numberOfShuffles) {
        for (int i = 0; i < dummyDiff.length - numberOfShuffles; i++) {
            dummyDiff[i] = diff[i + numberOfShuffles];
        }
        for (int i = dummyDiff.length - 1; i > dummyDiff.length - 1 + numberOfShuffles; i--) {
            dummyDiff[i] = diff[i - dummyDiff.length + numberOfShuffles];
        }
    }

    /**
     * Returns a double[][] of grid values
     *
     * @param grid
     * @param row
     * @param cellDistance
     * @return
     */
    protected double[][] getRowProcessInitialData(
            Grids_Grid2DSquareCellDouble grid,
            int cellDistance,
            long row) {
        double[][] result = new double[(cellDistance * 2) + 1][(cellDistance * 2) + 1];
        long col;
        long r;
        for (r = -cellDistance; r <= cellDistance; r++) {
            for (col = -cellDistance; col <= cellDistance; col++) {
                try {
                    double value = grid.getCell(
                            r + row,
                            col,
                            ge.HandleOutOfMemoryErrorTrue);
                    result[(int) r + cellDistance][(int) col + cellDistance]
                            = value;
                } catch (ArrayIndexOutOfBoundsException e) {
                    int debug = 1;
                    double value = grid.getCell(
                            r + row,
                            col,
                            ge.HandleOutOfMemoryErrorTrue);
                }
            }
        }
        return result;
    }

    /**
     * Returns a double[][] based on previous which has been shuffled
     *
     * @param grid
     * @param col
     * @param previous
     * @param cellDistance
     * @param row
     * @return
     */
    protected double[][] getRowProcessData(
            Grids_Grid2DSquareCellDouble grid,
            double[][] previous,
            int cellDistance,
            long row,
            long col) {
        double[][] result = previous;
        if (col == 0) {
            return getRowProcessInitialData(
                    grid,
                    cellDistance,
                    row);
        } else {
            // shift columns one left
            for (int i = 0; i <= cellDistance * 2; i++) {
                for (int j = 0; j <= (cellDistance * 2) - 1; j++) {
                    result[i][j] = previous[i][j + 1];
                }
            }
            // getLastColumn
            for (int i = -cellDistance; i <= cellDistance; i++) {
                result[i + cellDistance][cellDistance * 2]
                        = grid.getCell(
                                (long) i + row,
                                (long) col + cellDistance,
                                ge.HandleOutOfMemoryErrorTrue);
            }
        }
        return result;
    }

    //    // TODO:
    //    // tests
    //    private double[][] getChunkProcessInitialData( Grids_Grid2DSquareCellDouble grid, int chunkCells, int rowChunk ) {
    //        double[][] result = new double[ chunkCells * 3 ][ chunkCells * 3 ];
    //        for ( int i = -chunkCells; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //            for ( int j = -chunkCells; j <= ( chunkCells * 2 ) - 1; j ++ ) {
    //                result[ i + chunkCells ][ j + chunkCells ] = grid.getCell( i + ( rowChunk * chunkCells ), j );
    //            }
    //        }
    //        return result;
    //    }
    //
    //    // Needs testing!!
    //    private double[][] getChunkProcessData( Grids_Grid2DSquareCellDouble grid, double[][] previous, int chunkCells, int rowChunk, int colChunk ) {
    //        double[][] result = previous;
    //        if ( colChunk == 0 ) {
    //            return getRowProcessInitialData( grid, chunkCells, rowChunk );
    //        } else {
    //            // shift end columns to start columns
    //            for ( int i = -chunkCells; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //                for ( int j = 0; j <= chunkCells; j ++ ) {
    //                    result[ i + chunkCells ][ j ] = previous[ i + ( chunkCells * 2 ) ][ j + ( chunkCells * 2 ) ];
    //                }
    //            }
    //            // getOtherData
    //            for ( int i = -chunkCells; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //                for ( int j = 0; i <= ( chunkCells * 2 ) - 1; i ++ ) {
    //                    result[ i + chunkCells ][ j + chunkCells ] = grid.getCell( i + ( rowChunk * chunkCells ), j + ( colChunk * chunkCells ) );
    //                }
    //            }
    //        }
    //        return result;
    //    }
    /**
     * <a
     * name="_Output(AbstractGrid2DSquareCell,File,ImageExporter,String[],ESRIAsciiGridExporter,boolean)"></a>
     * For outputting _Grid2DSquareCell to various formats of file. It might be
     * better to have this in exchange.IO class.
     *
     * @param grid
     * @param outputDirectory
     * @param imageExporter
     * @param imageTypes
     * @param eSRIAsciiGridExporter
     * @param handleOutOfMemoryError
     * @throws java.io.IOException
     */
    public void output(
            Grids_AbstractGrid2DSquareCell grid,
            File outputDirectory,
            Grids_ImageExporter imageExporter,
            String[] imageTypes,
            Grids_ESRIAsciiGridExporter eSRIAsciiGridExporter,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            System.out.println("_Output " + grid.toString(handleOutOfMemoryError));
            if (imageExporter == null) {
                imageExporter = new Grids_ImageExporter(ge);
            }
            if (imageTypes == null) {
                imageTypes = new String[1];
                imageTypes[0] = "PNG";
            }
            if (eSRIAsciiGridExporter == null) {
                eSRIAsciiGridExporter = new Grids_ESRIAsciiGridExporter(ge);
            }
            //int _StringLength = 1000;
            String _DotASC = ".asc";
            BigDecimal _BigDecimal_Minus9999Point0 = new BigDecimal("-9999.0");
            String _String;
            File file;
            int i = 0;
            int _ImageTypesLength = imageTypes.length;
            for (i = 0; i < _ImageTypesLength; i++) {
                _String = ge.initString(
                        grid.get_Name(handleOutOfMemoryError),
                        "." + imageTypes[i],
                        handleOutOfMemoryError);
                file = ge.initFile(
                        outputDirectory,
                        _String,
                        handleOutOfMemoryError);
                imageExporter.toGreyScaleImage(
                        grid,
                        this,
                        file,
                        imageTypes[i],
                        handleOutOfMemoryError);
            }
            _String = ge.initString(
                    grid.get_Name(handleOutOfMemoryError),
                    _DotASC,
                    handleOutOfMemoryError);
            file = ge.initFile(
                    outputDirectory,
                    _String,
                    handleOutOfMemoryError);
            eSRIAsciiGridExporter.toAsciiFile(
                    grid,
                    file,
                    _BigDecimal_Minus9999Point0,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        grid,
                        handleOutOfMemoryError) < 1) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                output(
                        grid,
                        outputDirectory,
                        imageExporter,
                        imageTypes,
                        eSRIAsciiGridExporter,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     *
     * @param grid
     * @param processor
     * @param outputDirectory
     * @param ie
     * @param imageTypes
     * @param handleOutOfMemoryError
     * @throws IOException
     */
    public void outputImage(
            Grids_AbstractGrid2DSquareCell grid,
            File outputDirectory,
            Grids_ImageExporter ie,
            String[] imageTypes,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            System.out.println("Output " + grid.toString(handleOutOfMemoryError));
            if (ie == null) {
                ie = new Grids_ImageExporter(ge);
            }
            if (imageTypes == null) {
                imageTypes = new String[1];
                imageTypes[0] = "PNG";
            }
            BigDecimal bigDecimal_Minus9999Point0 = new BigDecimal("-9999.0");
            String string;
            String string_DOT = ".";
            File file;
            int i = 0;
            int _ImageTypesLength = imageTypes.length;
            for (i = 0; i < _ImageTypesLength; i++) {
                string = ge.initString(
                        grid.get_Name(handleOutOfMemoryError),
                        string_DOT + imageTypes[i],
                        handleOutOfMemoryError);
                file = ge.initFile(
                        outputDirectory,
                        string,
                        handleOutOfMemoryError);
                ie.toGreyScaleImage(
                        grid,
                        this,
                        file,
                        imageTypes[i],
                        handleOutOfMemoryError);
            }
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunksExcept_Account(
                        grid,
                        handleOutOfMemoryError) < 1) {
                    throw _OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                outputImage(
                        grid,
                        outputDirectory,
                        ie,
                        imageTypes,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     *
     * @param grid
     * @param outputDirectory
     * @param eSRIAsciiGridExporter
     * @param handleOutOfMemoryError
     * @throws IOException
     */
    public void _OutputESRIAsciiGrid(
            Grids_AbstractGrid2DSquareCell grid,
            File outputDirectory,
            Grids_ESRIAsciiGridExporter eSRIAsciiGridExporter,
            boolean handleOutOfMemoryError)
            throws IOException {
        try {
            System.out.println("_Output " + grid.toString(handleOutOfMemoryError));
            if (eSRIAsciiGridExporter == null) {
                eSRIAsciiGridExporter = new Grids_ESRIAsciiGridExporter(ge);
            }
            String string_DotASC = ".asc";
            BigDecimal bigDecimal_Minus9999Point0 = new BigDecimal("-9999.0");
            String string;
            File file;
            string = ge.initString(
                    grid.get_Name(handleOutOfMemoryError),
                    string_DotASC,
                    handleOutOfMemoryError);
            file = ge.initFile(
                    outputDirectory,
                    string,
                    handleOutOfMemoryError);
            eSRIAsciiGridExporter.toAsciiFile(
                    grid,
                    file,
                    bigDecimal_Minus9999Point0,
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                if (ge.swapToFile_Grid2DSquareCellChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw a_OutOfMemoryError;
                }
                ge.init_MemoryReserve(handleOutOfMemoryError);
                _OutputESRIAsciiGrid(
                        grid,
                        outputDirectory,
                        eSRIAsciiGridExporter,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

}
