# AGDT-Java-Grids

https://github.com/agdturner/grids

A Java library for storing and processing two-dimensional (2D) lattice based raster data otherwise known as grids. The library is geared for processing multiple input and output grids, each of which may be too large to store in the fast access memory of a computer (commonly refered to as ram or memory) - in which case, some of the data must be stored in slower access storage (commonly refered to as disk).

The two dimensions X and Y are orthoganol. Lattice points are arranged on equidistant parallel lines in each dimension, so in 2D the grid can also be imagined as being comprised of square cells with a value covering the cell or located at the cell centre (centroid) where the cells align with the coordinate axes X and Y and are arranged respectively in columns and rows. So, the cell value, or nearest lattice point value can be unambiguously given for all locations (coordinates) with the exception of those on the boundaries between cells. There is either 1, 2 or 4 nearest cell centroid or lattice point for any pair of coordinates (x, y). With this arrangement, each lattice point or cell is referenced by a row (lattice points or cell centroids with the same y) and a column (lattice points or cell centroids with the same x) composed into a single 2D identifier called a cell ID. (see footnote 1 for a brief outline of the design decision not to use traingular arrangements of points) 

The library has been used to process many hundreds of grids with many tens of thousands of rows and columns simultaneously using computers with a few hundred megabytes of fast access memory and a few gigabytes of available disk space.

Typically, each grid is subdivided into chunks with a smaller number of rows and columns in each chunk (although a grid may be comprised of a single chunk). All the chunks within a grid represent lattice point or cell values all of the same specific type. Currently boolean, Boolean, int and double type value grids are supported, but there are plans to also support BigInteger and BigDecimal values. The number of rows and column of lattice points or cells values in each chunk can be set when the grid is constructed. Those chunks in the last column and last row of chunks may have a smaller number of rows and columns.

Each chunk of each grid may be stored in the fast access memory of the computer and/or on the disk. There are three main different types of chunk containing boolean, Boolean, int, or double values: singlets - where a single value is used to store all the values at each lattice point or cell; maps - where there are default values and BitSets and Maps that indicate the locations at which there are values which are the same; and, arrays - which are 2D and where the first element indexes the row and the second element indexes the column of the chunk for the lattice or cell value. Each chunk is typically only stored in one type, but it is possible to cache out different types and swap between these. There has to be a chunged in chunk type if a singlet type storage has been used, but a different value is to be set at some location in the chunk to change if more than one type of value is to be stored that chunk. Whether a map or an array is more approriate depends on the density and variety of values stored, how much the values are changing and the importance of compactness of data storage of each chunk. For efficiency, it is the density of data values in the chunks of the entire grid and the number of rows and columns in each chunk that are likely to have the biggest efects. Often it is most sensible to either have the number of rows in each chunk being an exact integer division of the number of rows in the grid (and likewise the number of columns being an exact integer division of the number of columns in the grid); or to set the number of rows in each chunk and the number of columns in each chunk to be the same and process square shaped chunks. But this all depends on how stripey or chequered the data values are in the grid. Sometimes they are neither, but sometimes the user knows and can thus make adjustments to improve efficiency. Always currently the first row and first column in the chunk in the first row and column of chunks is for row 0 and column 0. Whilst the number of rows or columns in any chunk can be as low as 1 at most the chunk must contain fewer that 2147483648 lattice points or cells. It is suggested to use 65536 (256 x 256) or something between 256 (64 x 64) and 4194304 (2048 x 2048) and not go beyond 1073741824 (32768 X 32768) unless really testing the limits of the library. A further limit is that there can be no more than 2147483648 chunks in a single grid. So the theoretical limit for the number of rows (and columns) of a square grid is 2147483648. For many applications this will be sufficient and 
currently only super computers could handle anything nearly so large.

For each chunk it is known whether the version cached on disk is up to date.

The library attempts to prevent and handle OutOfMemoryErrors. If however the grids enviornment does not have approriate memory to clear then OutOfMemoryErrors are thrown upwards in the hope that some other part of the data processing environment has more approriate data to clear. If this is not the case then the processing is likely to grind on, but is perhaps unlikely to complete in a reasonable time frame. Currently no information is provided to the user if this is happeneing, but in a future version this feature can be added so that the user is aware that they should either consider changing the data structure increase the size of the virtual machine in which the program is running and/or re-run the process on a computer with more memory.

The library has evolved since around the year 2000 and is actively being developed. Some more history abou the evolution of the library can be found here: https://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/ 

## Usages
1. The library was originally developed to process geographical data into cross-scale density surfaces. Such surfaces were used in various academic research projects for a range of geographical modelling tasks and to explore for evidence of geographical clustering and changes to geographical clustering over time. In this respect it has most recently been used in the Digital Welfare Project to reveal changes in the distributions of benefit claimants in Leeds - see: https://github.com/agdturner/agdt-java-project-DigitalWelfare.
2. Processing digital elevation data into geomorphometrics - see: https://github.com/agdturner/agdt-java-project-Geomorphometrics.
3. Producing density plots of lines and points to help reveal relationships between variables.
4. The library has a general utility and works well in conjunction with another generic utility library for processing spatial vector data: https://github.com/agdturner/agdt-java-generic-vector

More example usages are wanted. If you use this library please get in contact to add your usage to this list.

## Code status and development roadmap
This code is actively being developed.
For a 1.0.0 release, the plan is:
* To provide a rationalised documented well tested code base. The processing classes geared for processing digital elevation data and for generating geographically weighted statistics will be moved to other libraries. The testing will focus on the Grids_Processing methods which should cover things that need testing for users. It is not envisaged to have a full test suite at this stage to test absoultely everything and provide in that way robustness for development going forwards (although that would be a good thing to have, it would come at a cost of not doing other things that could be more important!)
* Push artifacts to Maven Central: https://mvnrepository.com/repos/central
Version 2 aims to provide support for BigInteger and BigDecimal type numerical grids.
Version 3 aims to provide support for 3D grids.

## Dependencies
There are no third party dependencies except for those used in testing.
Please see the pom.xml for details.

## Contributions and collaboration
Currently there is no community development of this library and there are no known users other than the developer. Developing a community and sustaining software development over th lon-term is hard. If you find this software useful and you want to help develop it, then please contact the developer and we can try to come up with a plan. It might be better ultimately to try to integrate this code into another code base or for you to simply fork the library, but let us first consider if and how best to work together.

## Acknowledgements
A very early version of this library was based on code developed by James MacGill and Ian Turton that formed part of GeoTools. The development has been supported by numerous research grants and the University of Leeds. 

## LICENCE
Please see the standard Apache 2.0 open source LICENCE.

1. For spatial analysis, there are some theoretical advantages of rasters with values regularly spaced in equidistance triangular arrangements (forming cells that are regular hexagons in the two dimensions and which can be aggregated into larger hexagonal chunks). Both the square and triangle arrangement can be extended to three dimensional, but in three dimensions chunks in a triangular arangement are far more complicated in the general case. Aslo, despite the advantage of the triangular arrangement for spatial analysis, contemporary visualisation is typically arranged using pixels that if not square, are rectangular, and converting from squares to these rectangles is far easier than it would be from triangles. Furthermore the spatial analysis advantages of triangualr rasters are typically less than the advantages that can be gained by incresing the resolution (the density) of the raster. Hence the design decision to go with squares.
