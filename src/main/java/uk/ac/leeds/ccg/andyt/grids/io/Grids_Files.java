/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.io;

import java.io.File;
import java.io.IOException;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Strings;

public class Grids_Files extends Generic_Files {

    protected File GeneratedGridsDir;
    protected File GeneratedGridIntDir;
    protected File GeneratedGridDoubleDir;
    protected File GeneratedProcessorDir;

    protected Grids_Files() {
        super();
    }

    public Grids_Files(File dataDir) {
        super(dataDir);
    }

    public File getGeneratedGridsDir() {
        if (GeneratedGridsDir == null) {
            GeneratedGridsDir = new File(getGeneratedDataDir(),
                    Grids_Strings.s_Grids);
        }
        return GeneratedGridsDir;
    }

    public File getGeneratedGridIntDir() {
        if (GeneratedGridIntDir == null) {
            GeneratedGridIntDir = new File(getGeneratedGridsDir(), 
                    Grids_Strings.s_GridInt);
        }
        return GeneratedGridIntDir;
    }

    public File getGeneratedGridDoubleDir() {
        if (GeneratedGridDoubleDir == null) {
            GeneratedGridDoubleDir = new File(getGeneratedGridsDir(), 
                    Grids_Strings.s_GridDouble);
        }
        return GeneratedGridDoubleDir;
    }

    public File getGeneratedProcessorDir() {
        if (GeneratedProcessorDir == null) {
            GeneratedProcessorDir = new File(getGeneratedGridsDir(),
                    Grids_Strings.s_Processor);
        }
        return GeneratedProcessorDir;
    }

    /**
     * Returns a newly created file in System.getProperty("user.dir").
     *
     * @return
     */
    public File createNewFile() {
        //return createNewFile(new File(System.getProperty("java.io.tmpdir")));
        //return createNewFile(new File(System.getProperty("user.home")));
        return createNewFile(new File(System.getProperty("user.dir")));
    }

    /**
     * Returns a newly created File.
     *
     * @param dir
     * @return
     */
    public File createNewFile(File dir) {
        return createNewFile(dir, "", "");
    }

    /**
     * Returns a newly created File.
     *
     * @param dir
     * @param prefix
     * @param suffix
     * @return
     */
    public File createNewFile(File dir, String prefix, String suffix) {
        dir.mkdirs();
        File r = null;
        try {
            if ((prefix + suffix).equalsIgnoreCase("")) {
                boolean success = false;
                do {
                    r = getNewFile(dir, prefix, suffix);
                    if (! r.exists()) {
                        success = r.mkdir();
                    }
                } while (!success);
            } else {
                do {
                    r = getNewFile(dir, prefix, suffix);
                } while (!r.createNewFile());
            }
        } catch (IOException ioe0) {
            System.out.println("File " + r.toString());
            ioe0.printStackTrace(System.err);
        }
        return r;
    }

    private File getNewFile(File dir, String prefix, String suffix) {
        //dir.mkdirs();
        File r;
        do {
            r = new File(dir, prefix + System.currentTimeMillis() + suffix);
        } while (r.exists());
        return r;
    }
}
