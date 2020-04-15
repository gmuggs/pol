# Patterns-of-Life Simulation

Location-based social networks (LBSNs) have been studied extensively in recent years. However, utilizing real-world LBSN data sets in such studies yields several weaknesses: sparse and small data sets, privacy concerns, and a lack of authoritative ground-truth. To overcome these weaknesses, we leverage a large-scale geospatial simulation to create a framework to simulate human behavior and to create synthetic but realistic LBSN data based on human patterns of life. Such data not only captures the location of users over time but also their interactions via social networks. Patterns of life are simulated by giving agents (i.e., people) an array of 'needs' that they aim to satisfy, e.g., agents go home when they are tired, to restaurants when they are hungry, to work to cover their financial needs, and to recreational sites to meet friends and satisfy their social needs. While existing real-world LBSN data sets are trivially small, the proposed framework provides a source for massive LBSN benchmark data that closely mimics the real-world. As such it allows us to capture 100% of the (simulated) population without any data uncertainty, privacy-related concerns, or incompleteness. It allows researchers to see the (simulated) world through the lens of an omniscient entity having perfect data. Our framework is made available to the community. In addition, we provide a series of simulated benchmark LBSN data sets using different real-world urban environments obtained from OpenStreetMap. The simulation software and data sets which comprise gigabytes of spatio-temporal and temporal social network data are made available to the research community.


# Location-Based Social Network Data Generation Framework

The framework utilizes and extends the MASON (Multi-Agent Simulation of Neighborhoods) open-source simulation toolkit and its GIS extension, GeoMASON. MASON is a fast discrete-event multi-agent simulation library core developed in Java. It is designed to be the foundation for sizeable custom-purpose Java simulations by providing the basic run-time infrastructure for simulation development.


## Structure of project


The structure of the project and its summary are described as follows:
- `src/edu/gmu/mason/vanilla`: Core models including agents and needs
- `src/edu/gmu/mason/vanilla/db`: Classes related to schema
- `src/edu/gmu/mason/vanilla/environment`: Environments such as a building and a building unit
- `src/edu/gmu/mason/vanilla/gui`: Utilities related to GUI
- `src/edu/gmu/mason/vanilla/log`: Classes related to the logging system used to generate data 
- `src/edu/gmu/mason/vanilla/utils`: Other utilities


## How to compile and build a jar file

The simplest way to compile the code is to import `pom.xml` as a Maven project. All dependent libraries are described in `pom.xml`. Most of library can be found in Maven Central Repository. However, there are four jar files you must set up manually before building your Maven project. The four jar files are located in `src/main/resources/libs/`. The following are commands that you can use to build local Maven repositories, assuming that Maven is installed in your computer.


```
mvn install:install-file -Dfile=src/main/resources/libs/jts-1.13.1.jar -DgroupId=com.vividsolutions -DartifactId=jts -Dversion=1.13.1 -Dpackaging=jar 
mvn install:install-file -Dfile=src/main/resources/libs/geomason-1.5.2.jar -DgroupId=sim.util.geo -DartifactId=geomason -Dversion=1.5.2 -Dpackaging=jar 
mvn install:install-file -Dfile=src/main/resources/libs/mason-19.jar -DgroupId=sim -DartifactId=mason -Dversion=19 -Dpackaging=jar 
mvn install:install-file -Dfile=src/main/resources/libs/mason-tools-1.0.jar -DgroupId=at.granul -DartifactId=mason-tools -Dversion=1.0 -Dpackaging=jar
```

You can create a single (executable) jar file by using the following command.

```
mvn org.apache.maven.plugins:maven-compiler-plugin:3.1:compile org.apache.maven.plugins:maven-assembly-plugin:3.1.0:single
```

It will generate `vanilla-0.1-jar-with-dependencies.jar` in directory `target`. It will include all dependencies.


# How to run a simulation

There are two ways to run a simulation: (1) GUI and (2) headless. For the GUI version, run the main method in `src/edu/gmu/mason/vanilla/WorldModelUI.java`. For the headless version, invoke the main method in `src/edu/gmu/mason/vanilla/WorldModel.java` with appropriate arguments. 


```
java [Log4j2-configuration] [log-directory] [log-types] -jar vanilla-0.1-jar-with-dependencies.jar [simulation-configuration] [simulation-stop]
```


`[Log4j2-configuration]`: In order to enable the logging mechanism designed in the project, you must add the following VM arguments.

```
-Dlog4j2.configurationFactory=edu.gmu.mason.vanilla.log.CustomConfigurationFactory
```

`[log-directory]`: Log output path directory. e.g., `logs`

```
-Dlog.rootDirectory=[root-directory]
```

`[log-types]`: Logging types


```
-Dsimulation.test=[flexibility | qoi | all]
```


`[simulation-configuration]`: Model configuration file path

```
-configuration [filename]
```

`[simulation-stop]`: At steps (Integer) to stop. e.g., `288`

```
-until [steps]
```


The following command was a complete example that uses all configurations.

```
java -Dlog4j2.configurationFactory=edu.gmu.mason.vanilla.log.CustomConfigurationFactory -Dlog.rootDirectory=logs -Dsimulation.test=all -jar vanilla-0.1-jar-with-dependencies.jar -configuration parameters.properties -until 8640
```


# Load maps

Default maps are located in `src/main/resource/campus_data/`. The current version of this project includes four maps (i.e., `gmu_campus`, `french_quarter`, `virtual_city(large)`, `virtual_city(small)`) complaint with simulation, which requires the following three ESRI shapefiles: 
- `buildings`: They represent 2D polygonal footprints of buildings. It should include `neighbor` (neighborhood id: Integer), `id` (building id: Integer), `function` (building type: Integer), and `degree` (attractiveness of building: Double) fields.
- `buildingUnits`: They are a unit in a building such as a restaurant and an apartment unit. They are a point object.
- `walkways`: It is a spatial network consisting of roads represented as a polyline. The network should be a connected graph.

Note that multi geometry type such as multipoint and multipolygons are not supported. In order to load different maps, you have two options.
- Copy maps into `src/main/resource/campus_data/`. 
- Set the location of maps in the resources directory with parameter `maps`. For instance, you can load the GMU campus maps by setting `maps` configuration as follows.

```
maps = gmu_campus
```


# Resources

Joon-Seok Kim, Hyunjee Jin, Hamdi Kavak, Ovi Chris Rouly, Andrew Crooks, Dieter Pfoser, Carola Wenk and Andreas Züfle, <i>Location-Based Social Network Data Generation Based on Patterns of Life</i>, IEEE International Conference on Mobile Data Management (MDM 2020) (Accepted)

Project Website: [https://mdm2020.joonseok.org/](https://mdm2020.joonseok.org/)


