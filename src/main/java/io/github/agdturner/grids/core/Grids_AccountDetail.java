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
package io.github.agdturner.grids.core;

import io.github.agdturner.grids.d2.grid.Grids_Grid;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A POJO for accounting which chunks have been cleared in memory management.
 *
 * @author Andy Turner
 */
public class Grids_AccountDetail {

    /**
     * Details of what chunks have been cleared.
     */
    public HashMap<Grids_Grid, Set<Grids_2D_ID_int>> detail;
    
        /**
     * An indicator of the success of making sufficient memory available.
     */
    public boolean success;

    public Grids_AccountDetail() {
        detail = new HashMap<>();
        success = false;
    }

    public void add(Grids_AccountDetail a) {
        if (!detail.isEmpty()) {
            Set<Grids_Grid> as = a.detail.keySet();
            Set<Grids_Grid> s = detail.keySet();
            Iterator<Grids_Grid> ite = s.iterator();
            while (ite.hasNext()) {
                Grids_Grid g = ite.next();
                if (as.contains(g)) {
                    a.detail.get(g).addAll(detail.get(g));
                } else {
                    a.detail.put(g, detail.get(g));
                }
            }
        }
        success = a.success;
    }

}
