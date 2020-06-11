#!/bin/bash
#echo "Edit this file and choose the load method of choice (CacheStore, CSV, or JDBC)..."
#MAIN_CLASS=com.gridgain.sales.load.LoadCachesFromCacheStore
#MAIN_CLASS=com.gridgain.sales.load.LoadCachesFromJdbc
MAIN_CLASS=com.gridgain.sales.load.LoadCachesFromCsv
echo MAIN_CLASS=$MAIN_CLASS
bin/ignite.sh -v -J-Djava.net.preferIPv4Stack=true sales-client.xml
