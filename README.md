# Data Loading Patterns Based on Sales Database Example

## Components
This project illustrates four simple loading techniques for:
- LoadOnly CacheStore (implemented to load CSV Files)
- Client Ignite DataStreamer of CSV Files
- Client Ignite DataStreamer of JSON Files
- Client Ignite DataStreamer of JDBC Source Systems (from MySQL/MariaDB)

## Additional Items
In addition to the above loaders a number of project/development techniques will be used:
- Web Console-based Configuration as a starting point, followed by a number of changes/enhancements:
- Property file externalized configuration
- Parameterized POM file
- package-based code organization (as opposed to generic names as per Web Console configs)

## Notes
- Data folder includes data for CSV and JSON loads
- Scripts folder includes scripts to create the MySQL/MariaDB database <== this script is similar to that found on the internet, except dates moved to 2019 

## Install, Clone, Config, Build, Run & Load, Load, Load

### 1. Download binary install of Ignite:  
   Change to desired/home directory, e.g. `cd ~`
```
# Download from apache.org
curl https://downloads.apache.org//ignite/2.8.1/apache-ignite-2.8.1-bin.zip --output apache-ignite-2.8.1-bin.zip
# Unzip to current directory
jar -xvf apache-ignite-2.8.1-bin.zip
cd apache-ignite-2.8.1-bin
# Optional copy Ignite REST HTTP library for monitoring
cp -rp libs/optional/ignite-rest-http/ libs
# Optional set IGNITE_HOME to this unzipped folder, e.g. `export IGNITE_HOME=~/apache-ignite-2.8.1-bin`
# Confirm Execute privilege on shell scripts, e.g. `ls - ~/apache-ignite-2.8.1-bin/bin/*.sh`
# If necessary add execute privileges to bin and bin/include directories, e.g. `chmod +x ~/apache-ignite-2.8.1-bin/bin/*.sh && chmod +x ~/apache-ignite-2.8.1-bin/bin/include/*.sh`
```

### 2. Clone GitHub Repository  
   Change to desired/home directory, e.g. `cd ~`
```
# Using SSH: git clone git@github.com:GridGain-Demos/ignite-sales-data-loading-demo.git
git clone https://github.com/GridGain-Demos/ignite-sales-data-loading-demo.git
cd ignite-sales-data-loading-demo
```


### 3. Edit/Copy Project Config Files  
   Change to project directory, e.g. `cd ~/ignite-sales-data-loading-demo`
- pom.xml - check property: `runtime.location` and make sure it applies to your environment
- src/main/resources/sales.properties - make sure the uid/pwd combination is accurate, and that the `dataLocation` is accurate
- Note the Scripts/MySQL-ClassicModels-DB.sql file which can be used to build the MySQL/MariaDB database (for using the JDBC data loader)
- Note the Scripts/.sh & .bat files for starting the server and load processes (and for registring a Windows service)
- Note the salesdataload-load.sh file is fully commented out - Pick and uncomment a desired load routine (e.g. *.LoadCachesFromCsv)!!!
- Copy the load run script to your runtime, e.g. `cp Scripts/salesdataload-load.sh ../apache-ignite-2.8.1-bin/ && chmod +x ../apache-ignite-2.8.1-bin/salesdataload-load.sh`  * Note: we also changed the file to be executable
- Copy the server run script to your runtime, e.g. `cp Scripts/salesdataload-server.sh ../apache-ignite-2.8.1-bin/ && chmod +x ../apache-ignite-2.8.1-bin/salesdataload-server.sh`  * Note: we also changed the file to be executable

### 4. Build & Deploy SalesDataLoaders Maven Project  
   Change to project directory, e.g. `cd ~/ignite-sales-data-loading-demo`
```
mvn clean install
```
*** Confirm the install phase copied the project target & two additional dependency jars to Ignite runtime libs folder; Look for the following traces messages at the end of the Maven install phase:
```
[INFO] --- maven-dependency-plugin:2.8:copy (copy-installed) @ SalesDataLoaders ---
[INFO] Configured Artifact: org.apache.ignite:SalesDataLoaders:0.0.4:jar
[INFO] Configured Artifact: org.apache.commons:commons-csv:1.5:jar
[INFO] Configured Artifact: mysql:mysql-connector-java:5.1.48:jar
[INFO] Copying SalesDataLoaders-0.0.4.jar to /home/glenn/apache-ignite-2.8.1-bin/libs/SalesDataLoaders-0.0.4.jar
[INFO] Copying commons-csv-1.5.jar to /home/glenn/apache-ignite-2.8.1-bin/libs/commons-csv-1.5.jar
[INFO] Copying mysql-connector-java-5.1.48.jar to /home/glenn/apache-ignite-2.8.1-bin/libs/mysql-connector-java-5.1.48.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```
List your Ignite runtime libs location to confirm your file is there, e.g. `ls -l ~/apache-ignite-2.8.1-bin/libs` and lood for `SalesDataLoaders-0.0.4.jar`  
Copy the two extra libraries/dependencies that we included in our project: org.apache.commons:commons-csv:1.5 and mysql:mysql-connector-java:5.1.48


### 5. Run Server
   Change to Ignite runtime home directory, e.g. `cd ~/apache-ignite-2.8.1-bin`  
   Run the supplied startup script `./salesdataload-server.sh`  
   Confirm good startup by watching the logs and looking for the highly visible startup banner:
```
>>> +----------------------------------------------------------------------+
>>> Ignite ver. 2.8.1#20200521-sha1:864220966caa4157c4fee8a1bc85171623963604
>>> +----------------------------------------------------------------------+
>>> OS name: Linux 4.4.0-19041-Microsoft amd64
>>> CPU(s): 12
>>> Heap: 7.0GB
>>> VM name: 4833@GLENN-X1Exteme.localdomain
>>> Ignite instance name: sales
>>> Local node [ID=D1EAA846-18C1-4621-846F-6C1837AC45C9, order=1, clientMode=false]
>>> Local node addresses: [127.0.0.1]
>>> Local ports: TCP:8031 TCP:10831 TCP:11231 TCP:31100 TCP:31200 TCP:31500

[11:31:49,378][INFO][main][IgniteKernal%sales] >>> Ignite cluster is not active (limited functionality available). Use control.(sh|bat) script or IgniteCluster interface to activate.
```

Also note that on first startup, the cluster is not active.   
Use the the control script as instructed to start the cluster, e.g. `bin/control.sh --activate --port 11231` <== note the use of the control port argument as I have changed all ports for this cluster to not interfere with other clusters, and get a deterministic range (as opposed to the default control-port+1)
```
bin/control.sh --activate --port 11231
Control utility [ver. 2.8.1#20200521-sha1:86422096]
2020 Copyright(C) Apache Software Foundation
User: glenn
Time: 2020-06-19T11:36:29.782
Command [ACTIVATE] started
Arguments: --activate --port 11231
--------------------------------------------------------------------------------
Cluster activated
Command [ACTIVATE] finished with code: 0
Control utility has completed execution at: 2020-06-19T11:36:36.630
Execution time: 6848 ms
```
You can also check the Web Console to confirm that the cluster is now active AND all 8 caches/tables have been created, but are empty!

### 6. Load Data, Load Data, Load Data
Assuming in step #3 you edited the  salesdataload-load.bat/.sh script, you can now run this script to load data using your preferred approach:
- csv
- jdbc
- json

Use the following command and check standard out messages:
```
./salesdataload-load.sh
```
