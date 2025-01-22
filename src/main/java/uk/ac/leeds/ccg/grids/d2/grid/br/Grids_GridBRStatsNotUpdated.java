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
package uk.ac.leeds.ccg.grids.d2.grid.br;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;

/**
 * Statistic fields are not kept up to date as the underlying data is changed.
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_GridBRStatsNotUpdated extends Grids_GridBRStats {

    private static final long serialVersionUID = 1L;

    /**
     * Is {@code true} if fields are up to date and {@code false} otherwise.
     */
    protected boolean upToDate;

    /**
     * Creates a new instance of Grids_GridIntStatisticsNotUpdated.
     *
     * @param e The grids environment.
     */
    public Grids_GridBRStatsNotUpdated(Grids_Environment e) {
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
     * @return {@link #upToDate}.
     */
    public boolean isUpToDate() {
        return upToDate;
    }

    /**
     * Sets {@link #upToDate} to {@code b}.
     * 
     * @param b The value to set {@link #upToDate} to.
     */
    public void setUpToDate(boolean b) {
        this.upToDate = b;
    }

    /**
     * Updates by going through all values in the grid if the fields are likely not
     * to be up to date. (NB. After calling this it is inexpensive to convert to
     * Grids_GridStatsDouble.)
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
    public long getN() throws IOException, Exception,
            ClassNotFoundException {
        update();
        return n;
    }

    /**
     * @return The sum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigRational getSum() throws IOException, Exception,
            ClassNotFoundException {
        return sum;
    }

    /**
     * Get the sum of all data values.
     *
     * @param update If true then an {@link #update()} is called.
     * @return The sum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigRational getSum(boolean update) throws IOException, Exception,
            ClassNotFoundException {
        if (update) {
            update();
        }
        return sum;
    }

    /**
     * Get the minimum of all data values.
     * @param update If true then an update() is called.
     * @return The minimum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigRational getMin(boolean update) throws IOException, Exception,
            ClassNotFoundException {
        if (update) {
            update();
        }
        return min;
    }

    /**
     * @param update If true then update() is called.
     * @return The maximum of all data values.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigRational getMax(boolean update) throws IOException, Exception,
            ClassNotFoundException {
        if (update) {
            update();
        }
        return max;
    }
}
