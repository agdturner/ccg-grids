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
package io.github.agdturner.grids.io;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Defaults;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Files;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_Strings;
// The following imports are used in Javadoc documentation:
import io.github.agdturner.grids.process.Grids_Processor;

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
    protected Generic_Path GeneratedGridBigIntegerDir;

    /**
     * Indicates where grids holding {@code int} values are to be stored.
     */
    protected Generic_Path GeneratedGridIntDir;

    /**
     * Indicates where grids holding {@link BigDecimal} values are to be stored.
     */
    protected Generic_Path GeneratedGridBigDecimalDir;

    /**
     * Indicates where grids holding {@code double} values are to be stored.
     */
    protected Generic_Path GeneratedGridDoubleDir;

    /**
     * Indicates where grids holding {@code float} values are to be stored.
     */
    protected Generic_Path GeneratedGridFloatDir;

    /**
     * Indicates where grids holding {@code Boolean} values are to be stored.
     */
    protected Generic_Path GeneratedGridBooleanDir;

    /**
     * Indicates where grids holding {@code boolean} values are to be stored.
     */
    protected Generic_Path GeneratedGridBinaryDir;

    /**
     * Indicates where grids processed using a {@link #Grids_Processor} are to
     * be stored.
     */
    protected Generic_Path GeneratedProcessorDir;

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
     * If {@link #GeneratedGridBigIntegerDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #GeneratedGridBigIntegerDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridBigIntegerDir() throws IOException {
        if (GeneratedGridBigIntegerDir == null) {
            GeneratedGridBigIntegerDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_BigInteger));
        }
        return GeneratedGridBigIntegerDir;
    }

    /**
     * If {@link #GeneratedGridIntDir} is {@code null} it will be initialised.
     *
     * @return {@link #GeneratedGridIntDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridIntDir() throws IOException {
        if (GeneratedGridIntDir == null) {
            GeneratedGridIntDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridInt));
        }
        return GeneratedGridIntDir;
    }

    /**
     * If {@link #GeneratedGridBigDecimalDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #GeneratedGridBigDecimalDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridBigDecimalDir() throws IOException {
        if (GeneratedGridBigDecimalDir == null) {
            GeneratedGridBigDecimalDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_BigDecimal));
        }
        return GeneratedGridBigDecimalDir;
    }

    /**
     * If {@link #GeneratedGridDoubleDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #GeneratedGridDoubleDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridDoubleDir() throws IOException {
        if (GeneratedGridDoubleDir == null) {
            GeneratedGridDoubleDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridDouble));
        }
        return GeneratedGridDoubleDir;
    }

    /**
     * If {@link #GeneratedGridFloatDir} is {@code null} it will be initialised.
     *
     * @return {@link #GeneratedGridFloatDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridFloatDir() throws IOException {
        if (GeneratedGridFloatDir == null) {
            GeneratedGridFloatDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridFloat));
        }
        return GeneratedGridFloatDir;
    }

    /**
     * If {@link #GeneratedGridBooleanDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #GeneratedGridBooleanDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridBooleanDir() throws IOException {
        if (GeneratedGridBooleanDir == null) {
            GeneratedGridBooleanDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridBoolean));
        }
        return GeneratedGridBooleanDir;
    }

    /**
     * If {@link #GeneratedGridBooleanDir} is {@code null} it will be
     * initialised.
     *
     * @return {@link #GeneratedGridBinaryDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedGridBinaryDir() throws IOException {
        if (GeneratedGridBinaryDir == null) {
            GeneratedGridBinaryDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_GridBinary));
        }
        return GeneratedGridBinaryDir;
    }

    /**
     * If {@link #GeneratedProcessorDir} is {@code null} it will be initialised.
     *
     * @return {@link #GeneratedProcessorDir} initialised first if it is
     * {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Generic_Path getGeneratedProcessorDir() throws IOException {
        if (GeneratedProcessorDir == null) {
            GeneratedGridBooleanDir = new Generic_Path(Paths.get(
                    getGeneratedDir().toString(), Grids_Strings.s_Processor));
        }
        return GeneratedProcessorDir;
    }
}
