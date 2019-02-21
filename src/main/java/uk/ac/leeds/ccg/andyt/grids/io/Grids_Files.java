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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Strings;

public class Grids_Files extends Generic_Files {

    private Grids_Environment ge;

    protected File GeneratedGridsDir;
    protected File GeneratedGridIntDir;
    protected File GeneratedGridDoubleDir;
    protected File GeneratedProcessorDir;

    protected Grids_Files() {
    }

    public Grids_Files(Grids_Environment ge, File dataDir) {
        this.ge = ge;
        strings = ge.getStrings();
        this.dataDir = dataDir;
    }

    public File getGeneratedGridsDir() {
        if (GeneratedGridsDir == null) {
            GeneratedGridsDir = new File(getGeneratedDataDir(),
                    getStrings().getS_Grids());
        }
        return GeneratedGridsDir;
    }

    public File getGeneratedGridIntDir() {
        if (GeneratedGridIntDir == null) {
            GeneratedGridIntDir = new File(getGeneratedGridsDir(), 
                    getStrings().getS_GridInt());
        }
        return GeneratedGridIntDir;
    }

    public File getGeneratedGridDoubleDir() {
        if (GeneratedGridDoubleDir == null) {
            GeneratedGridDoubleDir = new File(getGeneratedGridsDir(), 
                    getStrings().getS_GridDouble());
        }
        return GeneratedGridDoubleDir;
    }

    public File getGeneratedProcessorDir() {
        if (GeneratedProcessorDir == null) {
            GeneratedProcessorDir = new File(getGeneratedGridsDir(),
                    getStrings().getS_Processor());
        }
        return GeneratedProcessorDir;
    }
    
    public Grids_Strings getStrings() {
        return (Grids_Strings) strings;
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
        File result = null;
        try {
            if ((prefix + suffix).equalsIgnoreCase("")) {
                boolean success = false;
                do {
                    result = getNewFile(dir, prefix, suffix);
                    if (! result.exists()) {
                        success = result.mkdir();
                    }
                } while (!success);
            } else {
                do {
                    result = getNewFile(dir, prefix, suffix);
                } while (!result.createNewFile());
            }
        } catch (IOException ioe0) {
            System.out.println("File " + result.toString());
            ioe0.printStackTrace(System.err);
        }
        return result;
    }

    private File getNewFile(File dir, String prefix, String suffix) {
        //dir.mkdirs();
        File result;
        do {
            result = new File(dir, prefix + System.currentTimeMillis() + suffix);
        } while (result.exists());
        return result;
    }
}
