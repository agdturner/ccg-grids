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
import uk.ac.leeds.ccg.agdt.generic.core.Generic_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.process.Grids_Processor;

/**
 *
*
 * @author Andy Turner
 * @version 1.0.0
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
