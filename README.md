# agdt-Java-Grids

https://github.com/agdturner/agdt-java-grids

## General description
A Java library for storing and processing lattice based raster data otherwise known as grids. The library is geared for processing multiple input and output grids, each of which may be too large to store in the available fast access memory of a computer (commonly refered to as ram or memory) - in which case, some of the data is stored in slower access storage (commonly refered to as disk).

Currently grids have two spatial dimensions with two orthoganol coordinate axes - X and Y. The lattice points are the intersections of equidistant parallel lines that cut one or other axis orthogonally in a flat plane. A grid can also be imagined as being comprised of square cells on this plane with values covering the cell, with cell sides aligning with the coordinate axes X and Y - arranged in what are referred to respectively as columns and rows. With this arrangement, each lattice point or cell can be referenced by a row (lattice points or cell centroids with the same Y coordinate) and a column (lattice points or cell centroids with the same X coordinate), or a 2D identifier which composes the row and column indexes into a cell ID. 

The library has been used to process many hundreds of grids with many tens of thousands of rows and columns simultaneously using computers with a few hundred megabytes of available fast access memory and a few gigabytes of available disk space.

The nearest lattice point or cell value can be unambiguously determined for all coordinates with the exception of those on the boundaries between cells exactly midway between lattice points. There are either 1, 2 or 4 nearest lattice points or cell centroids for all coordinates (x, y): x being the value of the coordinate on the X axis; and, y being the value of the coordinte on the Y axis.

BigDecimal numbers - decimals with a specified precision and potentially massive magnitude - are used for coordinates and for location, distance and area calculations in the library. This provides greater parity of accuracy irrespective of coordinate value magnitudes compared with using floating point numbers and floating point arithmetic for such calcuations. In general it also allows for greater accuracy in such calculations, although to achieve sufficient accuracy, intermediate calculations may require higher levels of precision and all told the calculations may be far more computationally demanding. 

Currently grids can contain boolean, Boolean, int and double type values, but there are plans to also support BigInteger and BigDecimal values. Each value in a grid is of the same type.

Grids are generally subdivided into chunks with smaller numbers of rows and columns than are in the entire grid (although a grid may be comprised of a single chunk). The number of rows and columns of lattice points (or cell values) in each chunk can be set when the grid is constructed. Those chunks in the last column and last row of chunks may have a smaller number of rows and columns.

Each chunk of each grid may be stored in the fast access memory of the computer and/or on the disk. There are three main different types of chunk containing boolean, Boolean, int, or double values: singlets - where a single value is used to store all the values at each lattice point or cell; maps - where there are default values, BitSets and Maps that indicate the locations at which there are values which are the same; and, arrays - which are 2D and where the first element indexes the row and the second element indexes the column of the cell in the chunk. Each chunk is typically only stored in one of these chunk types, but it is possible to cache different types and use these in different contexts.

There has to be a change in chunk type if a different value is to be set in a chunk currently stored as a singlet type. Whether a map or an array is more approriate depends on the density and variety of values stored, how much the values are changing, the size of each type of chunk, and what they are used for in the context of a data processing workflow. The density and variety of data values, and the number of rows and columns of cells in each chunk are likely to affect the appropriateness of each type of chunk.

It may be sensible to either have the number of rows in each chunk being an exact integer division of the number of rows in the grid (and likewise the number of columns being an exact integer division of the number of columns in the grid); or to set the number of rows in each chunk and the number of columns in each chunk to be the same and process square shaped chunks. But the sense in this all depends on how stripey or chequered the data values are in the grid. Sometimes they are neither, sometimes the user knows apriori, and it is also possible to do some optimisation via some kind of diagnosis.

Always currently the first row and first column in the chunk in the first row and column of chunks is for row 0 and column 0. Whilst the number of rows or columns in any chunk can be as low as 1, at most a chunk must contain fewer that 2147483648 lattice points or cells. However, in way of guidance it is suggested to try chunks with 65536 cells (perhaps with 256 rows and columns) or something between chunks with 256 cells (perhaps 64 rows and columns) and 4194304 cells (perhaps 2048 rows and columns), and to not use chunks with more than 1073741824 cells (perhaps 32768 rows and columns) - unless testing the limits of the library.

Another important limit is that there can be no more than 2147483648 chunks in a single grid. So the theoretical limit for the number of rows (and columns) of a square grid is 2147483648.

For each chunk it is known whether the version cached on disk is up to date.

The library attempts to manage with the memory made available to the Java Virtual Machine or with a subset of this made available for the grids environment by when necessary caching and clearing or otherwise clearing data from the fast access memory of the computer and swapping data to and from disk. If the grids environment runs out of suitable chunks to clear, before getting stuck in slow cycles of swapping out chunks only to load them again soon after, then errors or exceptions are thrown out further to a more general processing environment in the hope that some other part of the data processing environment has more approriate data to clear. If this is not the case then the processing is likely to grind on, but is perhaps unlikely to complete in a reasonable time frame... Currently no information is provided to the user if this is happening, but in a future version it might be, and in this way the user can be advised about and consider: changing the chunk structures of the grids; or increasing the size of the virtual machine in which the program is running (which may or may not involve utilising computers with more fast access memory).

## History
The library has evolved since around the year 2000 and is actively being developed. Some more history abou the evolution of the library can be found here: https://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/ 

## Usages
1. The library was originally developed to process geographical data into cross-scale density surfaces. Such surfaces were generated and used in various academic research projects for a range of geographical modelling task, to search for evidence of geographical clustering and to investigate changes in geographical clustering over time. In this respect it has most recently been used in the Digital Welfare Project to reveal changes in the distributions of benefit claimants in Leeds - see: https://github.com/agdturner/agdt-java-project-DigitalWelfare.
2. Processing digital elevation data into geomorphometrics - see: https://github.com/agdturner/agdt-java-project-Geomorphometrics.
3. Producing density plots of lines and points to help reveal relationships between variables.
4. The library has a general utility and works well in conjunction with another generic utility library for processing spatial vector data: https://github.com/agdturner/agdt-java-vector

It would be good to provide more example usages of this library. If you use it please let the developer know and maybe your usage will get added to this list...

## Status, Current Version and platform requirements
1. Version 1.0-SNAPSHOT
Developed and tested on Java 11 using Maven. It is available from Maven Central via: https://mvnrepository.com/artifact/io.github.agdturner/agdt-java-math/1.1.0

To use with Maven add the following dependencies to your POM:
```
<dependency>
    <groupId>io.github.agdturner</groupId>
    <artifactId>agdt-java-grids</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.github.agdturner/agdt-java-math -->
<dependency>
    <groupId>io.github.agdturner</groupId>
    <artifactId>agdt-java-math</artifactId>
    <version>1.1.0</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.github.agdturner/agdt-java-generic -->
<dependency>
    <groupId>io.github.agdturner</groupId>
    <artifactId>agdt-java-generic</artifactId>
    <version>1.1.0</version>
</dependency>
```
2. Version 1.0.x
* The aim is to release this in 2020 after developing unit tests that test every method in the Grids_Processor class.
* Move the processing classes geared for processing digital elevation data Grids_Processor_DEM and for generating geographically weighted statistics Grids_Processor_GWS to other libraries.
3. Version 1.1.x
* The main enhancement aim is to add classes for 2D BigInteger and BigDecimal type numerical grids.
4. Version 2.x
* The main enhancement aim is to add classes for 3D grids.

## Dependencies
agdt-java-generic available via https://github.com/agdturner/agdt-java-generic.
agdt-java-math available via https://github.com/agdturner/agdt-java-math.

There are no third party dependencies except for those used in testing.
Please see the pom.xml for details.

## Contributions and collaboration
Contributors welcome, please contact the developer and we can plan this together.

## Acknowledgements
The development of this library has been supported by numerous academic research grants and the University of Leeds. 

## LICENCE
Please see the standard Apache 2.0 open source LICENCE.

## Footnotes
1. For spatial analysis, there are some theoretical advantages of rasters with values regularly spaced in equidistance triangular arrangements (these can be imagined as cells that are regular hexagons in two dimensions and which can be aggregated into larger regular (triangular or hexagonal) or irregular polyhedral chunks). Both square and equilateral triangle arrangements can be extended to three and more dimensions. In three dimensions (3D), there are a plethora of honeycomb like arrangements that can be used to partition values into chunks or semiregular honeycombs. Rectangular blocks are relatively uniform and generally easier to subdivide and aggregate. Subdivision is also helped by choosing chunk sizes that are easily subdivided into chunks of equal size.

Despite the advantages of triangular arrangements over square arrangements for spatial analysis, contemporary display devices and image formats typically use pixels that if not square, are nearly square. Converting from the squares of a grid to these pixel values by aggregation or disaggregations is generally more strightforward than it would be for triangles notwithstanding how the lattice is orientated on the display screen.

Furthermore the spatial analysis advantages of triangular rasters are typically less than the advantages that can be gained by increasing the resolution of the raster.
