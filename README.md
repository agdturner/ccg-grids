# agdt-grids
A Java library for 2D square celled spatial raster data processing. It allows for many hundreds of input and output grids with many thousands of rows and columns to be processed on computers with a few hundred megabytes of fast access memory and a few gigabytes of available disk space.

Grids are made up of chunks (parts) which are swapped in and out of fast access memory from files to handle large volumes of data. There are limits to the volumes of data that can be processed in a reasonable time frame on a single CPU. There is no reason why many thousands of grids cannot be processed and indeed this library has been used to process large numbers of grids with 256 rows and 256 columns which are commonly served by Web Map Tile Servers.

The library makes an attempt to handle memory and the swapping of files to prevent OutOfMemoryErrors and also to recover if OutOfMemoryErrors are thrown.

The library was originally developed to process geographical data and was originally applied to develop some geographical mapping techniques to examine densities across a range of scales and explore and visualise geographical clustering. It has been used also to process digital elevation model data and produce things like density plots of lines and points to more clearly show and analyse distributions from these data which might otherwise be hard to see or identify.
