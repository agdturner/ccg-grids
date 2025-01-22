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

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBD;

/**
 * For statistics of grids and chunks of type BigDecimal. The {@link #min} and
 * {@link #max} are initialised as follows:
 * <ul>
 * <li>{@code min = BigDecimal.valueOf(Double.MAX_VALUE);}<li>
 * <li>{@code max = min.negate();}</li>
 * </ul>
 * To support more extreme values there is work to be done!
 *
 * @author Andy Turner
 * @version 1.0
 */
public abstract class Grids_StatsBD extends Grids_StatsNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the minimum value.
     */
    protected BigDecimal min;

    /**
     * For storing the maximum value.
     */
    protected BigDecimal max;

    /**
     * Create a new instance.
     *
     * @param ge Grids_Environment
     */
    public Grids_StatsBD(Grids_Environment ge) {
        super(ge);
        init0();
    }

    /**
     * The {@link #min} and {@link #max} are initialised as follows:
     * <ul>
     * <li>{@code min = BigDecimal.valueOf(Double.MAX_VALUE);}<li>
     * <li>{@code max = min.negate();}</li>
     * </ul>
     */
    private void init0() {
        min = BigDecimal.valueOf(Double.MAX_VALUE);
        max = min.negate();
    }

    /**
     * For re-initialising.
     */
    @Override
    protected void init() {
        super.init();
        init0();
    }

    /**
     * @return true - some stats are kept up to date as the underlying data
     * changes.
     */
    @Override
    public boolean isUpdated() {
        return true;
    }

    /**
     * @return (Grids_GridBD) grid.
     */
    @Override
    public Grids_GridBD getGrid() {
        return (Grids_GridBD) grid;
    }

    /**
     * @param v The value replacing a value.
     */
    protected void update(BigDecimal v) {
        n += 1;
        setSum(sum.add(BigRational.valueOf(v)));
        if (v.compareTo(min) == -1) {
            nMin = 1;
            min = v;
        } else {
            if (v == min) {
                nMin++;
            }
        }
        if (v.compareTo(max) == 1) {
            nMax = 1;
            max = v;
        } else {
            if (v == max) {
                nMax++;
            }
        }
    }

    /**
     * @return The minimum of all data values in {@link #grid}.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigDecimal getMin(boolean update) throws IOException, Exception, ClassNotFoundException {
        if (nMin < 1) {
            if (update) {
                update();
            }
        }
        return min;
    }

    /**
     * Set min.
     *
     * @param min What {@link #min} is set to.
     */
    public void setMin(BigDecimal min) {
        this.min = min;
    }

    /**
     * @return The maximum of all data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigDecimal getMax(boolean update) throws IOException, Exception, ClassNotFoundException {
        if (nMax < 1) {
            if (update) {
                update();
            }
        }
        return max;
    }

    /**
     * Set max.
     *
     * @param max What {@link #max} is set to.
     */
    public void setMax(BigDecimal max) {
        this.max = max;
    }
}
