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
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Strings;

public class Grids_Files extends Generic_Files {

    protected File GeneratedGridIntDir;
    protected File GeneratedGridDoubleDir;
    protected File GeneratedGridBinaryDir;
    protected File GeneratedProcessorDir;

    public Grids_Files(File dir) throws IOException {
        super(dir);
    }
    
    /**
     * {@code return new File(Generic_Files.getDefaultDir(), Grids_Strings.s_Grids);}
     *
     * @return A default directory called Grids in {@link Generic_Files.getDefaultDir()}.
     */
    public static File getDefaultDir() {
        return new File(Generic_Defaults.getDefaultDir(), Grids_Strings.s_Grids);
    }
    
    /**
     * @param dataDir
     * @return A directory called {@link Grids_Strings#s_Grids} 
     * in {@code dataDir}.
     */
    public static File getDir(File dataDir) {
        File r = new File(dataDir, Grids_Strings.s_Grids);
        r.mkdir();
        return r;
    }

    public File getGeneratedGridIntDir() {
        if (GeneratedGridIntDir == null) {
            GeneratedGridIntDir = new File(getGeneratedDir(), 
                    Grids_Strings.s_GridInt);
        }
        return GeneratedGridIntDir;
    }

    public File getGeneratedGridDoubleDir() {
        if (GeneratedGridDoubleDir == null) {
            GeneratedGridDoubleDir = new File(getGeneratedDir(), 
                    Grids_Strings.s_GridDouble);
        }
        return GeneratedGridDoubleDir;
    }

    public File getGeneratedGridBinaryDir() {
        if (GeneratedGridBinaryDir == null) {
            GeneratedGridBinaryDir = new File(getGeneratedDir(), 
                    Grids_Strings.s_GridBinary);
        }
        return GeneratedGridBinaryDir;
    }

    public File getGeneratedProcessorDir() {
        if (GeneratedProcessorDir == null) {
            GeneratedProcessorDir = new File(getGeneratedDir(),
                    Grids_Strings.s_Processor);
        }
        return GeneratedProcessorDir;
    }
}
