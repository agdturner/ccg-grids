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
package uk.ac.leeds.ccg.grids.memory;

/**
 * A POJO for accounting how many chunks have been cleared in memory management.
 *
 * @author Andy Turner
 */
public class Grids_Account {

    /**
     * A count of how many chunks have been cleared.
     */
    public long detail;
    
    /**
     * An indicator of the success of making sufficient memory available.
     */
    public boolean success;

    /**
     * Create a new instance.
     */
    public Grids_Account() {
        detail = 0;
        success = false;
    }

    /**
     * Add to account.
     * @param a What is added.
     */
    public void add(Grids_Account a) {
        detail += a.detail;
        success = a.success;
    }

    /**
     * Add to account.
     */
    public void add() {
        detail += 1;
    }

    /**
     * Add to account.
     * 
     * @param i What is added.
     */
    public void add(int i) {
        detail += i;
    }

}
