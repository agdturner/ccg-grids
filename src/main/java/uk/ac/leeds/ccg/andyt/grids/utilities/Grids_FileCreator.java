/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.utilities;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Grids_FileCreator {
    
    /** Creates a new instance of FileCreator */
    public Grids_FileCreator() {
    }
    
    
    
    /**
     * Returns a newly created temporary file.
     * Default parent directory to System.getProperty( "java.io.tmpdir" ).
     * //Default parent directory to System.getProperty( "user.dir" ).
     * //Default parent directory to System.getProperty( "user.home" ).
     * @return 
     */
    public static File createTempFile() {
        return createTempFile( new File( System.getProperty( "java.io.tmpdir" ) ) );
        //return createTempFile( null );
    }
    
    /**
     * Returns a newly created temporary file.
     * @param parentDirectory .
     * Default extension to nothing.
     * @return 
     */
    public static File createTempFile(
            File parentDirectory ) {
        return createTempFile(
                parentDirectory,
                "",
                "" );
    }
    
    /**
     * Returns a newly created temporary file.
     * @param parentDirectory .
     * @param prefix If not 3 characters long, this will be padded with "x"
     * characters.
     * @param suffix If null the file is appended with ".tmp".
     * Default extension to nothing.
     * @return 
     */
    public static File createTempFile(
            File parentDirectory,
            String prefix,
            String suffix ) {
        File file = null;
        while ( prefix.length() < 3 ) {
            prefix = prefix + "x";
        }
        boolean abstractFileCreated = false;
        do {
            try {
                file = File.createTempFile(
                        prefix + Long.toString( System.currentTimeMillis() ),
                        suffix,
                        parentDirectory );
                abstractFileCreated = true;
            } catch ( IOException e ) {
                // File must have already existed or disc space full or something.
            }
        } while ( ! abstractFileCreated );
        file.deleteOnExit();
        return createNewFile( parentDirectory, file.getName() );
    }
    
    /**
     * Returns a newly created file.
     * //Default parent directory to System.getProperty( "java.io.tmpdir" ).
     * Default parent directory to System.getProperty( "user.dir" ).
     * //Default parent directory to System.getProperty( "user.home" ).
     * @return 
     */
    public static File createNewFile() {
        //return createNewFile( new File( System.getProperty( "java.io.tmpdir" ) ) );
        return createNewFile( new File( System.getProperty( "user.dir" ) ) );
    }
    
    /**
     * Returns a newly created File.
     * @param parentDirectory
     * Default extension prefix and suffix nothing.
     * @return 
     */
    public static File createNewFile( File parentDirectory ) {
        return createNewFile(
                parentDirectory, "", "");
    }
    
    /**
     * Returns a newly created File.
     * @param parentDirectory
     * @param prefix
     * @param suffix
     * @return 
     */
    public static File createNewFile(
            File parentDirectory,
            String prefix,
            String suffix ) {
        File file;
        do {
            file = new File(
                    parentDirectory,
                    prefix + Long.toString( System.currentTimeMillis() ) + suffix );
        } while ( file.exists() );
        try {
            if ( (prefix + suffix).equalsIgnoreCase( "" ) ) {
                file.mkdir();
            } else {
                file.createNewFile();
            }
        } catch ( IOException ioe0 ) {
            System.out.println( "File " + file.toString() );
            ioe0.toString();
            ioe0.printStackTrace();
        }
        return file;
    }
    
    /**
     * Returns a newly created File which is a file if the filename. or a directory.
     * @param parentDirectory
     * @param filename
     * @return 
     */
    public static File createNewFile(
            File parentDirectory,
            String filename ) {
        File file = new File( parentDirectory, filename );
        try {
            if ( filename.charAt( filename.length() - 4 ) != '.' ) {
                file.mkdir();
            } else {
                file.createNewFile();
            }
        } catch ( IOException ioe0 ) {
            System.out.println( "File " + file.toString() );
            ioe0.toString();
            ioe0.printStackTrace();
        }
        return file;
    }
}