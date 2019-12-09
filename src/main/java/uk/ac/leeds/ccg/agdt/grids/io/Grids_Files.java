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
package uk.ac.leeds.ccg.agdt.grids.io;

import java.io.File;
import java.io.IOException;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Strings;

/**
 * 
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Files extends Generic_Files {

    protected File GeneratedGridIntDir;
    protected File GeneratedGridDoubleDir;
    protected File GeneratedGridBinaryDir;
    protected File GeneratedProcessorDir;

    public Grids_Files() throws IOException {
        this(getDefaultDir());
    }

    public Grids_Files(File dir) throws IOException {
        super(dir);
    }

    /**
     * @return
     * {@code return new File(getDefaultGenericDir(), Grids_Strings.s_grids)}
     */
    public static File getDefaultDir() {
        return new File(getDefaultGenericDir(), Grids_Strings.s_grids);
    }

    /**
     * Make and return a directory called {@link Grids_Strings#s_grids} in
     * {@code dataDir}.
     *
     * @param dataDir The directory in which to make a new directory called
     * {@link Grids_Strings#s_grids}.
     * @return A directory called {@link Grids_Strings#s_grids} in
     * {@code dataDir}.
     */
    public static File getDir(File dataDir) {
        File r = new File(dataDir, Grids_Strings.s_grids);
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
