/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.examples;

import java.io.File;
import java.io.IOException;
import uk.ac.leeds.ccg.andyt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 *
 * @author geoagdt
 */
public class Grids_ProcessTimeSeries extends Grids_Processor {

    /**
     * @throws IOException 
     */
    private Grids_ProcessTimeSeries() throws IOException {
        super();
    }

    public Grids_ProcessTimeSeries(Grids_Environment ge) throws IOException {
        super(ge);
    }

    public static void main(String[] args) {
        try {
            Grids_Environment e = new Grids_Environment(new Generic_Environment());
            Grids_ProcessTimeSeries p;
            p = new Grids_ProcessTimeSeries(e);
            p.run();
        } catch (IOException | Error e) {
            e.printStackTrace(System.err);
        }
    }

    public void run() {
        File indir = files.getInputDir();
        indir = new File(indir, "NIMROD_ASCII");
        System.out.println(indir);
        File[] files = indir.listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);

        }
    }

}
