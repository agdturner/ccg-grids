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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.agdt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.agdt.generic.time.Generic_Time;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.process.Grids_Processor;

/**
 * Converts a series of NIMROD derived ASCIIGRID files into a time series format
 * for a landscape evolution model. Originally developed for Eleanor Pearson.
 *
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ConvertToTimeSeries extends Grids_Processor {

    private static final long serialVersionUID = 1L;

    private long Time;
    boolean HandleOutOfMemoryError;
    String Filename;

    /**
     * @throws IOException 
     */
    protected Grids_ConvertToTimeSeries() throws IOException {
        super();
    }

    /**
     * @param e
     */
    public Grids_ConvertToTimeSeries(Grids_Environment e) throws IOException {
        super(e);
        Time = System.currentTimeMillis();
        HandleOutOfMemoryError = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Grids_Environment ge = new Grids_Environment(new Generic_Environment());
            Grids_ConvertToTimeSeries t = new Grids_ConvertToTimeSeries(ge);
            t.run();
        } catch (IOException | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public void run() {
        try {
            env.setProcessor(this);
            String name = "NIMROD_ASCII";
            File indir  = new File(files.getInputDir(), name);
            File outdir  = new File(files.getOutputDir(), name);
            File outf  = new File(outdir, name + "timeseries.csv");
            outdir.mkdirs();
            PrintWriter pw = env.env.io.getPrintWriter(outf, false);
            File gendir  = new File(files.getGeneratedDir(), name);
            File[] fs  = indir.listFiles();
            TreeMap<Generic_Time, Grids_GridDouble> grids  = new TreeMap<>();
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
            for (File file : fs) {
                fn = file.getName();
                if (fn.endsWith(".asc")) {
                    System.out.println(fn);
                    dir = new File(gendir, fn);
                    split = fn.split("rid_");
                    time = split[1].substring(0, split[1].length() - 4);
                    year = Integer.valueOf(time.substring(0, 4));
                    month = Integer.valueOf(time.substring(4, 6));
                    day = Integer.valueOf(time.substring(6, 8));
                    hour = Integer.valueOf(time.substring(8, 10));
                    minute = Integer.valueOf(time.substring(10, 12));
                    System.out.println("" + year + " " + month + " " + day + " " + hour + " " + minute);
                    t = new Generic_Time(env.env, year, month, day, hour, minute, second);
                    g = GridDoubleFactory.create(dir, file);
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
            pw.print("ID,row,col");
            ite = grids.keySet().iterator();
            while (ite.hasNext()) {
                t = ite.next();
                pw.print("," + t.getYYYYMMDDHHMM());
            }
            pw.println();
            double v;
            boolean writeIDRC = true;
            double ndv;
            // Write values
            for (long r = 0; r < nrows; r++) {
                for (long c = 0; c < ncols; c++) {
                    ite = grids.keySet().iterator();
                    while (ite.hasNext()) {
                        t = ite.next();
                        g = grids.get(t);
                        if (writeIDRC) {
                            pw.print((r * ncols + c) + "," + r + "," + c);
                            writeIDRC = false;
                        }
                        v = g.getCell(r, c);
                        ndv = g.getNoDataValue();
                        if (v == ndv) {
                            pw.print(",0");
                        } else {
                            pw.print("," + v);
                        }
                    }
                    writeIDRC = true;
                    pw.println();
                }
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
