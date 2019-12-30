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
package uk.ac.leeds.ccg.grids.d2.stats;

import java.io.IOException;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import java.math.BigInteger;

/**
 * Statistic fields are not kept up to date as the underlying data is changed.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_StatsNotUpdatedInt extends Grids_StatsInt {

    private static final long serialVersionUID = 1L;

    /**
     * Is true iff fields are upToDate else is false.
     */
    protected boolean upToDate;

    /**
     * Creates a new instance.
     *
     * @param e The grids environment.
     */
    public Grids_StatsNotUpdatedInt(Grids_Environment e) {
        super(e);
    }

    /**
     * @return {@code false}.
     */
    @Override
    public boolean isUpdated() {
        return false;
    }

    /**
     * @return {@link #upToDate}
     */
    public boolean isUpToDate() {
        return upToDate;
    }

    /**
     * Sets {@link #upToDate} to {@code b}.
     *
     * @param b What {@link #upToDate} is set to.
     */
    public void setUpToDate(boolean b) {
        upToDate = b;
    }

    /**
     * Updates by going through all values in grid if they might not be up to
     * date. (NB. After calling this it is inexpensive to convert to
     * Grids_GridIntStats.)
     *
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void update() throws IOException, Exception, ClassNotFoundException {
        if (!isUpToDate()) {
            super.update();
            setUpToDate(true);
        }
    }

    /**
     * Get the number of cells with data values.
     *
     * @return The number of cells with data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigInteger getN() throws IOException, Exception, ClassNotFoundException {
        update();
        return n;
    }

    /**
     * Get the sum of all data values.
     *
     * @return The sum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigDecimal getSum() throws IOException, Exception,
            ClassNotFoundException {
        update();
        return sum;
    }

    /**
     * Get the minimum of all data values.
     *
     * @param update If true then update() is called.
     * @return The minimum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Integer getMin(boolean update) throws IOException, Exception,
            ClassNotFoundException {
        if (update) {
            update();
        }
        return min;
    }

    /**
     * Get the maximum of all data values.
     *
     * @param update If true then update() is called.
     * @return The maximum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Integer getMax(boolean update) throws IOException, Exception,
            ClassNotFoundException {
        if (update) {
            update();
        }
        return max;
    }
}
