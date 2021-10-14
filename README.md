# [ccg-grids](https://github.com/agdturner/ccg-grids)

A modularised Java library for storing and processing lattice based raster data otherwise known as grids.

## Latest versioned release
```
<!-- https://mvnrepository.com/artifact/io.github.agdturner/agdt-java-grids -->
<dependency>
    <groupId>io.github.agdturner</groupId>
    <artifactId>agdt-java-grids</artifactId>
    <version>1.1</version>
</dependency>
```
[JAR](https://repo1.maven.org/maven2/io/github/agdturner/agdt-java-grids/1.1/agdt-java-grids-1.1.jar)

## General description
Grids is geared for processing multiple large input and output data grids in workflows which may involve other data not stored as data grids. Each data grid may be too large to store in the available fast access memory of the computer (memory) - in which case, some of the data is stored in slower access storage (disk).

Data grids have two spatial dimensions with two orthoganol coordinate axes - X and Y. The lattice points are the intersections of equidistant parallel lines that cut one or other axis orthogonally in a flat plane. A grid can also be imagined as being comprised of square cells on this plane with values covering the cell, with cell sides aligning with the coordinate axes X and Y - arranged in what are referred to respectively as columns and rows. With this arrangement, each lattice point or cell can be referenced by a row (lattice points or cell centroids with the same Y coordinate) and a column (lattice points or cell centroids with the same X coordinate), or a 2D identifier which composes the row and column indexes into a cell ID. 

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

## Example uses
1. The library was originally developed to process geographical data into cross-scale density surfaces. Such surfaces were generated and used in various academic research projects for a range of geographical modelling task, to search for evidence of geographical clustering and to investigate changes in geographical clustering over time. In this respect it has most recently been used in the Digital Welfare Project to reveal changes in the distributions of benefit claimants in Leeds - see: https://github.com/agdturner/agdt-java-project-DigitalWelfare.
2. Processing digital elevation data into geomorphometrics - see: https://github.com/agdturner/agdt-java-project-Geomorphometrics.
3. Producing density plots of lines and points to help reveal relationships between variables.
4. The library has a general utility and works well in conjunction with another generic utility library for processing spatial vector data: https://github.com/agdturner/agdt-java-vector

## Dependencies
- [agdt-java-generic](https://github.com/agdturner/agdt-java-generic)
- [agdt-java-math](https://github.com/agdturner/agdt-java-math)
- Please see the [POM](https://github.com/agdturner/agdt-java-grids/blob/master/pom.xml) for details.

## Development Plans
# Version 2.0
* Support 3D grids.
* Push the processing classes geared for processing digital elevation data Grids_Processor_DEM and for generating geographically weighted statistics Grids_Processor_GWS to other libraries.
* Develop in an [agile](https://en.wikipedia.org/wiki/Agile_software_development) way.

## Contributions
- Welcome.

## LICENCE
- [APACHE LICENSE, VERSION 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Acknowledgements and thanks
- The [University of Leeds](http://www.leeds.ac.uk) and externally funded research grants have supported the development of this library.
- Thank you [openJDK](https://openjdk.java.net/) contributors and all involved in creating the platform.
- Thank you developers and maintainers of other useful Java libraries that provide inspiration.
- Thank you [GeoTools](http://www.geotools.org) developers - a very early version of this library was based on the old GeoTools 1 Raster class.
- Thank you developers and maintainers of [Apache Maven](https://maven.apache.org/), [Apache NetBeans](https://netbeans.apache.org/), and [git](https://git-scm.com/) which I use for developing code.
- Thank you developers and maintainers of [GitHub](http://github.com) for supporting the development of this code and for providing a means of creating a community of users and  developers.
- Thank you developers, maintainers and contributors of relevent content on:
-- [Wikimedia](https://www.wikimedia.org/) projects, in particular the [English language Wikipedia](https://en.wikipedia.org/wiki/Main_Page)
-- [StackExchange](https://stackexchange.com), in particular [StackOverflow](https://stackoverflow.com/).
- Information that has helped me develop this library is cited in the source code.
- Thank you to those that supported me personally and all who have made a positive contribution to society. Let us try to look after each other, look after this world, make space for wildlife, and engineer knowledge :)

## Footnotes
1. For spatial analysis, especially with regard Earth systems modelling, there are some theoretical advantages of rasters with values regularly spaced in a more triangular arrangement (these can be imagined as cells that are hexagonal in two dimensions and which can be aggregated into larger (triangular, hexagonal or polyhedral chunks). Both square and triangular arrangements can be extended to three and more dimensions. In three dimensions (3D), there are a plethora of honeycomb like arrangements that can be used to partition values into chunks or semiregular honeycombs.
