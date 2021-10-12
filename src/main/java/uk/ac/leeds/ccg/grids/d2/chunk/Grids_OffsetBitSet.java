/*
 * Copyright 2021 Centre for Computational Geography.
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

import java.util.BitSet;

/**
 * A POJO for an int and a BitSet.
 *
 * @author Andy Turner
 */
public class Grids_OffsetBitSet {

    /**
     * offset
     */
    public int offset;

    /**
     * bitSet
     */
    public BitSet bitSet;

    /**
     * Create a new instance.
     *
     * @param offset
     */
    public Grids_OffsetBitSet(int offset) {
        this.offset = offset;
        bitSet = new BitSet();
    }
}
