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
- Data folder includes data for CSV and JSON loads (Orders and Payments also have a 2019 version that moves dates from 2003/2004/2005 to 2017/2018/2019)
- Scripts folder includes scripts to create the MySQL/MariaDB database;
   --> Use *-2019.sql script for an update that creates the data with more recent dates (to 2019).

## Install, Devel, Build & Run

1. Clone GitHub Repository
```
# Using SSH: git clone git@github.com:GridGain-Demos/ignite-sales-data-loading-demo.git
git clone https://github.com/GridGain-Demos/ignite-sales-data-loading-demo.git
cd ignite-sales-data-loading-demo


2. Download binary install of Ignite:
```
# Download from apache.org
curl https://downloads.apache.org//ignite/2.8.1/apache-ignite-2.8.1-bin.zip --output apache-ignite-2.8.1-bin.zip
# Unzip to current directory
jar -xvf apache-ignite-2.8.1-bin.zip
cd apache-ignite-2.8.1-bin
# Optional copy Ignite REST HTTP library for monitoring
cp -rp libs/optional/ignite-rest-http/ libs

```

2. 