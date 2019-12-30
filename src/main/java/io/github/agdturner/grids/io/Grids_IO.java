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
package io.github.agdturner.grids.io;

import javax.imageio.ImageIO;

/**
 * For Input and Output.
 * 
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_IO {

    /**
     * @param writerType - usually a well known text String for an image type
     * @return {@code true} if writerType is available.
     */
    public static boolean isImageWriterAvailable(String writerType) {
        boolean r = false;
        String[] writerTypes = ImageIO.getWriterMIMETypes();
        for (String writerType1 : writerTypes) {
            if (writerType1.equalsIgnoreCase("image/" + writerType)) {
                r = true;
            }
        }
        return r;
    }
}
