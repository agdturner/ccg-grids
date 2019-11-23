# Grids

https://github.com/agdturner/agdt-java-generic-grids

A Java library for 2D square celled spatial raster data processing. The library evolved to be capable of processing many hundreds of input and output grids each with potentially many thousands of rows and columns using computers with a few hundred megabytes of fast access memory and a few gigabytes of available disk space.

Each individual 2D square celled spatial raster data layer (grid) contains a specific type of value and is comprised of chunks. Currently boolean, int and double type value grids are supported. The number of rows and columns in a typical chunk are set. Those chunks in the last column and last row of chunks may have a smaller number of rows and columns.

Each chunk of each grid may be stored either in the fast access memeory or on the disk using a few different supported structures. For each different type of grid (those containing boolean, int, or double values) the values in each chunk can be stored in a two dimensional array. For numerical types, there are other options which might be more efficient.

Most of the methods attempt to prevent OutOfMemoryErrors being thrown and also recover if they are. This happens by attempting to swap chunks between the fast access memory and disk as necessary.

The library has evolved since around the year 2000 and is actively being developed. Some more details about the library can be found here: https://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/grids/ 

## Usages
1. The library was originally developed to process geographical data into cross-scale density surfaces. Such surfaces were used in various projects for a range of geographical modelling tasks and to explore for evidence of geographical clustering. In this respect it has most recently been used in the Digital Welfare Project to reveal a change in the distributions of benefit claimants in Leeds - see: https://github.com/agdturner/agdt-java-project-DigitalWelfare.
2. Processing digital elevation data into geomorphometrics - see: https://github.com/agdturner/agdt-java-project-Geomorphometrics.
3. Producing density plots of lines and points to help reveal relationships between variables.
4. The library has a general utility and might work well in conjunction with another generic utility library for processing spatial vector data: https://github.com/agdturner/agdt-java-generic-vector

More example usages are wanted. If you use this library please add to this list.

## Code status and development roadmap
This code is actively being developed.
For a 1.0.0 release, the plan is:
1. To remove dependencies on JAI and to move the JAI dependent code to a plugin or delete it permanently as there is not much advnatage gained from using this.
2. To develop some more usage examples to showcase what the library can do.
3. To produce more unit tests to test the core functionality and capabilities of the library.
4. To update the source code documentation.
For a 2.0.0 release the plan is to additionally support BigInteger and BigDecimal type numerical grids.
For a 3.0.0 Support 3D grids.

## Dependencies
Please see the pom.xml for details.

## Contributions
Please raise issues and submit pull requests in the usual way. Contributions will be acknowledged.

## Acknowledgements
A very early version of this library was based on some raster code from GeoTools originally developed by James MacGill and Ian Turton. The development has been supported by numerous research grants and the University of Leeds. 

## LICENCE
Please see the standard Apache 2.0 open source LICENCE.
