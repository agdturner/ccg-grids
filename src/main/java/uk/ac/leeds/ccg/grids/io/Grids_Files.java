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
package uk.ac.leeds.ccg.grids.io;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import uk.ac.leeds.ccg.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.generic.io.Generic_Files;
import uk.ac.leeds.ccg.grids.core.Grids_Strings;
import uk.ac.leeds.ccg.io.IO_Path;

/**
 * For conveniently initialising and locating files and directories.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_Files extends Generic_Files {

    private static final long serialVersionUID = 1L;

    /**
     * Indicates where grids holding {@link BigInteger} values are to be stored.
     */
    protected IO_Path generatedGridBigIntegerDir;

    /**
     * Indicates where grids holding {@code int} values are to be stored.
     */
    protected IO_Path generatedGridIntDir;

    /**
     * Indicates where grids holding {@link BigDecimal} values are to be stored.
     */
    protected IO_Path generatedGridBigDecimalDir;

    /**
     * Indicates where grids holding {@code double} values are to be stored.
     */
    protected IO_Path generatedGridDoubleDir;

    /**
     * Indicates where grids holding {@code float} values are to be stored.
     */
    protected IO_Path generatedGridFloatDir;

    /**
     * Indicates where grids holding {@code Boolean} values are to be stored.
     */
    protected IO_Path generatedGridBooleanDir;

    /**
     * Indicates where grids holding {@code boolean} values are to be stored.
     */
    protected IO_Path generatedGridBinaryDir;

    /**
     * Indicates where grids are to be stored.
     */
    protected IO_Path generatedProcessorDir;

    /**
     * Creates a new instance.
     *
     * @throws IOException If encountered.
     */
    public Grids_Files() throws IOException {
        this(new Generic_Defaults(Paths.get(System.getProperty("user.home"),
                Grids_Strings.s_grids)));
    }

    /**
     * Creates a new instance using {@code d}.
     *
     * @param d This contains details of the directory which will be used.
     * @throws IOException If encountered.
     */
    public Grids_Files(Generic_Defaults d) throws IOException {
        super(d);
    }

    /**
     * If {@link #generatedGridBigIntegerDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #generatedGridBigIntegerDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridBigIntegerDir() throws IOException {
        if (generatedGridBigIntegerDir == null) {
            generatedGridBigIntegerDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_BigInteger));
        }
        return generatedGridBigIntegerDir;
    }

    /**
     * If {@link #generatedGridIntDir} is {@code null} it will be initialised.
     *
     * @return {@link #generatedGridIntDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridIntDir() throws IOException {
        if (generatedGridIntDir == null) {
            generatedGridIntDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridInt));
        }
        return generatedGridIntDir;
    }

    /**
     * If {@link #generatedGridBigDecimalDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #generatedGridBigDecimalDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridBigDecimalDir() throws IOException {
        if (generatedGridBigDecimalDir == null) {
            generatedGridBigDecimalDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_BigDecimal));
        }
        return generatedGridBigDecimalDir;
    }

    /**
     * If {@link #generatedGridDoubleDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #generatedGridDoubleDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridDoubleDir() throws IOException {
        if (generatedGridDoubleDir == null) {
            generatedGridDoubleDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridDouble));
        }
        return generatedGridDoubleDir;
    }

    /**
     * If {@link #generatedGridFloatDir} is {@code null} it will be initialised.
     *
     * @return {@link #generatedGridFloatDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridFloatDir() throws IOException {
        if (generatedGridFloatDir == null) {
            generatedGridFloatDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridFloat));
        }
        return generatedGridFloatDir;
    }

    /**
     * If {@link #generatedGridBooleanDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #generatedGridBooleanDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridBooleanDir() throws IOException {
        if (generatedGridBooleanDir == null) {
            generatedGridBooleanDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridBoolean));
        }
        return generatedGridBooleanDir;
    }

    /**
     * If {@link #generatedGridBooleanDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #generatedGridBinaryDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedGridBinaryDir() throws IOException {
        if (generatedGridBinaryDir == null) {
            generatedGridBinaryDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridBinary));
        }
        return generatedGridBinaryDir;
    }

    /**
     * If {@link #generatedProcessorDir} is {@code null} it will be initialised.
     *
     * @return {@link #generatedProcessorDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public IO_Path getGeneratedProcessorDir() throws IOException {
        if (generatedProcessorDir == null) {
            generatedGridBooleanDir = new IO_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_Processor));
        }
        return generatedProcessorDir;
    }
}
