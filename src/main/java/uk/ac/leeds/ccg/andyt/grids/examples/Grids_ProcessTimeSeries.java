/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.examples;

import java.io.File;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;

/**
 *
 * @author geoagdt
 */
public class Grids_ProcessTimeSeries extends Grids_Processor {
    
    private Grids_ProcessTimeSeries(){
    }
    
    public Grids_ProcessTimeSeries(Grids_Environment ge){
        super(ge);
    }

    public static void main(String[] args) {
        Grids_Environment ge;
        ge = new Grids_Environment(new File(System.getProperty("user.dir")));
        Grids_ProcessTimeSeries p;
        p = new Grids_ProcessTimeSeries(ge);
        p.run();
    }
    
    public void run(){
        File indir;
        indir = Files.getInputDataDir();
        indir = new File(indir, "NIMROD_ASCII");
        System.out.println(indir);
        File[] files = indir.listFiles();
        for (int i = 0; i < files.length; i ++) {
            System.out.println(files[i]);
            
        }
    }
    
}
