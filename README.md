# FileWatcher
This is the source code for the FileWatcher Cytoscape app.

Its in a BETA Stadium and currently offers to listen to changes in xls and csv files.

## Building and Installation:

To download best clone this repository using git (or your favorite git tool)
```
git clone https://github.com/tpfau/FileWatcher.git 
```
To build the app, you will need maven installed on your system. Then, if you have cloned this repository go to the repository directory and run maven:

```
cd FileWatcher
mvn install
```

Finally copy target/FileWatcher-0.0.1.jar to your Cytoscape app directory or install the file from within the Cytoscape app menu.

## Usage:

When installed select a network that you want to listen to changes in a xls or csv file.  
Go to Apps -> Add File To Watch  
You can select a file to watch and the column to map as well as the Cytoscape table to map to.  
The data from the excel sheet will be loaded automatically into cytoscape (be aware, that while each column can contain different datatypes in excel or csv files, they have to be consistent for Cytoscape, so only use one type of data per column).
Columns not in the Cytoscape network will be added.
