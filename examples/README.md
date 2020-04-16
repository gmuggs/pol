# Examples of configuration and run script

The `.properties` files are a configuration file loading different maps. The default value of a parameter is set if the parameter value is not specified in the configuration file. For example, `gmu_campus.properties` configures a simulation that initially instantiates 1000 agents that walk in George Mason University campus at approximately 1.4m/s. The `parameters.properties` file lists all parameters with default values. 

Each `.sh` file is an example of scripts used to run simulation with `vanilla-0.1-jar-with-dependencies.jar` and a configuration file. Note that the scripts assume that `vanilla-0.1-jar-with-dependencies.jar` exists in `../target/` directory. Or, you can modify the jar file path, i.e., `../target/vanilla-0.1-jar-with-dependencies.jar`, accordingly. In this directory, `run_french_quarter.sh`, `run_gmu_campus.sh`, `run_virtual_city_large.sh`, `run_virtual_city_small.sh` load `french_quarter.properties`, `gmu_campus.properties`, `virtual_city_large.properties`, and `virtual_city_small.properties`, respectively.

