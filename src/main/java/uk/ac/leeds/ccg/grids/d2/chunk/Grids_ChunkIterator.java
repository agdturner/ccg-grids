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
package uk.ac.leeds.ccg.grids.d2.chunk;

import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;

/**
 *
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkIterator extends Grids_Object {

    private static final long serialVersionUID = 1L;
    
    /**
     * The grid.
     */
    protected Grids_Grid grid;
    
    /**
     * The grid chunk.
     */
    protected Grids_Chunk chunk;
    
    /**
     * Create a new instance.
     * 
     * @param chunk What {@link #chunk} is set to.
     */
    public Grids_ChunkIterator(Grids_Chunk chunk) {
        super(chunk.env);
        this.chunk = chunk;
        grid = this.chunk.getGrid();        
    }
    
    /**
     * @return {@code true} if there is a next. 
     */
    public abstract boolean hasNext();
    
}
