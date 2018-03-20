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
import java.io.PrintWriter;
import java.sql.Time;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.utilities.time.Generic_Time;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
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
public class Grids_ConvertToTimeSeries
        extends Grids_Processor {

    private long Time;
    boolean HandleOutOfMemoryError;
    String Filename;
    Generic_Environment e;

    protected Grids_ConvertToTimeSeries() {
    }

    /**
     * Creates a new RoofGeneralisation using specified Directory. WARNING:
     * Files in the specified Directory may get overwritten.
     *
     * @param ge
     */
    public Grids_ConvertToTimeSeries(Grids_Environment ge) {
        super(ge);
        Time = System.currentTimeMillis();
        HandleOutOfMemoryError = true;
        e = new Generic_Environment(ge.getFiles(), ge.getStrings());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File dir = new File(System.getProperty("user.dir"));
        System.out.print("" + dir.toString());
        if (dir.exists()) {
            System.out.println(" exists.");
            dir.mkdirs();
        } else {
            System.out.println(" does not exist.");
        }
        Grids_Environment ge = new Grids_Environment(dir);
        Grids_ConvertToTimeSeries t = new Grids_ConvertToTimeSeries(ge);
        t.run();
    }

    public void run() {
        try {
            ge.setProcessor(this);
            Grids_Files gf;
            gf = ge.getFiles();
            Grids_Strings gs;
            gs = ge.getStrings();
            String name;
            name = "NIMROD_ASCII";
            File indir;
            indir = new File(gf.getInputDataDir(gs), name);
            File outdir;
            outdir = new File(gf.getOutputDataDir(gs), name);
            File outf;
            outf = new File(outdir, name + "timeseries.txt");
            outdir.mkdirs();
            PrintWriter pw;
            pw = Generic_StaticIO.getPrintWriter(outf, false);
            File gendir;
            gendir = new File(gf.getGeneratedDataDir(gs), name);
            File[] files;
            files = indir.listFiles();
            TreeMap<Generic_Time, Grids_GridDouble> grids;
            grids = new TreeMap<>();
            String time;
            int year;
            int month;
            int day;
            int hour;
            int minute;
            int second = 0;
            Generic_Time t;
            Grids_GridDouble g;
            String[] split;
            String fn;
            File dir;
            Long nrows = null;
            Long ncols = null;
            for (int i = 0; i < files.length; i++) {
                fn = files[i].getName();
                if (fn.endsWith(".asc")) {
                    System.out.println(fn);
                    dir = new File(gendir, fn);
                    split = fn.split("Grid_");
                    time = split[1].substring(0, split[1].length() - 4);
                    year = Integer.valueOf(time.substring(0, 4));
                    month = Integer.valueOf(time.substring(4, 6));
                    day = Integer.valueOf(time.substring(6, 8));
                    hour = Integer.valueOf(time.substring(8, 10));
                    minute = Integer.valueOf(time.substring(10, 12));
                    System.out.println("" + year + " " + month + " " + day + " " + hour + " " + minute);
                    t = new Generic_Time(e, year, month, day, hour, minute, second);
                    g = GridDoubleFactory.create(dir, files[i]);
                    if (nrows == null) {
                        nrows = g.getNRows();
                    } else {
                        if (nrows != g.getNRows()) {
                            System.out.println("Warning mismatching nrows");
                        }
                    }
                    if (ncols == null) {
                        ncols = g.getNCols();
                    } else {
                        if (ncols != g.getNCols()) {
                            System.out.println("Warning mismatching ncols");
                        }
                    }
                    grids.put(t, g);
                }
            }
            Iterator<Generic_Time> ite;
            // Write header
            pw = Generic_StaticIO.getPrintWriter(outf, false);
            pw.print("ID,row,col");
            ite = grids.keySet().iterator();
            while (ite.hasNext()) {
                t = ite.next();
                pw.print("," + t.getYYYYMMDDHHMM());
            }
            pw.println();
            Grids_2D_ID_long ID;
            double v;
            boolean writeIDRC = true;
            // Write values
            for (long r = 0; r < nrows; r++) {
                for (long c = 0; c < ncols; c++) {
                    ite = grids.keySet().iterator();
                    while (ite.hasNext()) {
                        t = ite.next();
                        g = grids.get(t);
                        if (writeIDRC) {
                            ID = g.getCellID(r, c);
                            pw.print(ID.toString() + "," + r + "," + c);
                            writeIDRC = false;
                        }
                        v = g.getCell(r, c);
                        pw.print("," + v);
                    }
                    pw.println();
                }
                writeIDRC = true;
            }
            pw.close();
        } catch (Exception | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public long getTime() {
        return Time;
    }
}
