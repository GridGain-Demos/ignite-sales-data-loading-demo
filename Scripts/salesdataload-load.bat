@ECHO OFF
ECHO Edit this file and choose the load method of choice (CacheStore, CSV, or JDBC)...
setlocal
REM set MAIN_CLASS=com.gridgain.sales.load.LoadCachesFromCacheStore && bin\ignite.bat -v -J-Djava.net.preferIPv4Stack=true sales-client.xml
REM set MAIN_CLASS=com.gridgain.sales.load.LoadCachesFromCsv && bin\ignite.bat -v -J-Djava.net.preferIPv4Stack=true sales-client.xml
REM set MAIN_CLASS=com.gridgain.sales.load.LoadCachesFromJdbc && bin\ignite.bat -v -J-Djava.net.preferIPv4Stack=true sales-client.xml
endlocal
