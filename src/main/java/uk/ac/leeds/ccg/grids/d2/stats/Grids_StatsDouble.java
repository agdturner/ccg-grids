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
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;

/**
 * Grids_GridDouble statistics. For statistics of grids and chunks of type 
 * double.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_StatsDouble extends Grids_StatsNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the minimum value.
     */
    protected double min;

    /**
     * For storing the maximum value.
     */
    protected double max;

    public Grids_StatsDouble(Grids_Environment ge) {
        super(ge);
        init0();
    }
    
    /**
     * Initialises the statistics by setting min equal to Double.MAX_VALUE and
     * max = -Double.MAX_VALUE.
     */
    private void init0() {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
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
     * @return (Grids_GridDouble) grid.
     */
    @Override
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) grid;
    }

    /**
     * @param v The value replacing a value.
     */
    protected void update(double v) {
        n += 1;
        setSum(sum.add(new BigDecimal(v)));
        if (v < min) {
            nMin = 1;
            min = v;
        } else {
            if (v == min) {
                nMin++;
            }
        }
        if (v > max) {
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
    public Double getMin(boolean update) throws IOException, Exception, ClassNotFoundException {
        if (nMin < 1) {
            if (update) {
                update();
            }
        }
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return The maximum of all data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Double getMax(boolean update) throws IOException, Exception, ClassNotFoundException {
        if (nMax < 1) {
            if (update) {
                update();
            }
        }
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
