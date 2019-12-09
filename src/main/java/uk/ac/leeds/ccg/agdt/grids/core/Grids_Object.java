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
package uk.ac.leeds.ccg.agdt.grids.core;

import java.io.Serializable;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;

/**
 *
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_Object implements Serializable {
    
    /**
     * A reference to Grids_Environment.
     */
    public transient Grids_Environment env;

    public Grids_Object() {
    }
    
    public Grids_Object(Grids_Environment e) {
        this.env = e;
    }
    
    
}
