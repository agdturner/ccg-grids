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

package uk.ac.leeds.ccg.grids.d2;

import java.io.Serializable;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 * A POJO point.
 * @author Andy Turner
 */
public class Grids_Point implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * The x coordinate.
     */
    public Math_BigRational x;

    /**
     * The y coordinate.
     */
    public Math_BigRational y;
    
    /**
     * Create a new instance.
     * 
     * @param x What{@link #x} is set to.
     * @param y What{@link #y} is set to.
     */
    public Grids_Point(Math_BigRational x, Math_BigRational y) {
        this.x = x;
        this.y = y;
    }
    
}
