nssm.exe install Ignite-Sales C:\apps\ignite-2.8.0\bin\ignite.bat
nssm.exe set Ignite-Sales AppParameters "-v -J-Djava.net.preferIPv4Stack=true -J-DIGNITE_CLUSTER_ID_AND_TAG_FEATURE=true -J-DIGNITE_JETTY_PORT=8031 sales-server.xml"
nssm.exe set Ignite-Sales AppDirectory C:\apps\ignite-2.8.0
nssm.exe set Ignite-Sales AppExit Default Restart
nssm.exe set Ignite-Sales DisplayName Ignite-Sales
nssm.exe set Ignite-Sales Description "Ignite - Sales Node #1"
nssm.exe set Ignite-Sales ObjectName LocalSystem
nssm.exe set Ignite-Sales Start SERVICE_DEMAND_START
nssm.exe set Ignite-Sales Type SERVICE_WIN32_OWN_PROCESS

nssm.exe install Ignite-Sales-2 C:\apps\ignite-2.8.0\bin\ignite.bat
nssm.exe set Ignite-Sales-2 AppParameters "-v -J-Djava.net.preferIPv4Stack=true -J-DIGNITE_CLUSTER_ID_AND_TAG_FEATURE=true -J-DIGNITE_JETTY_PORT=8032 sales-server.xml"
nssm.exe set Ignite-Sales-2 AppDirectory C:\apps\ignite-2.8.0
nssm.exe set Ignite-Sales-2 AppExit Default Restart
nssm.exe set Ignite-Sales-2 DisplayName Ignite-Sales-2
nssm.exe set Ignite-Sales-2 Description "Ignite - Sales Node #2"
nssm.exe set Ignite-Sales-2 ObjectName LocalSystem
nssm.exe set Ignite-Sales-2 Start SERVICE_DEMAND_START
nssm.exe set Ignite-Sales-2 Type SERVICE_WIN32_OWN_PROCESS

nssm.exe install Ignite-Sales-3 C:\apps\ignite-2.8.0\bin\ignite.bat
nssm.exe set Ignite-Sales-3 AppParameters "-v -J-Djava.net.preferIPv4Stack=true -J-DIGNITE_CLUSTER_ID_AND_TAG_FEATURE=true -J-DIGNITE_JETTY_PORT=8033 sales-server.xml"
nssm.exe set Ignite-Sales-3 AppDirectory C:\apps\ignite-2.8.0
nssm.exe set Ignite-Sales-3 AppExit Default Restart
nssm.exe set Ignite-Sales-3 DisplayName Ignite-Sales-3
nssm.exe set Ignite-Sales-3 Description "Ignite - Sales Node #3"
nssm.exe set Ignite-Sales-3 ObjectName LocalSystem
nssm.exe set Ignite-Sales-3 Start SERVICE_DEMAND_START
nssm.exe set Ignite-Sales-3 Type SERVICE_WIN32_OWN_PROCESS

nssm.exe install Ignite-Sales-WCAgent C:\apps\gridgain-web-console-agent\gridgain-web-console-agent.bat
nssm.exe set Ignite-Sales-WCAgent AppParameters "-c sales.properties"
nssm.exe set Ignite-Sales-WCAgent AppDirectory C:\apps\gridgain-web-console-agent
nssm.exe set Ignite-Sales-WCAgent AppExit Default Restart
nssm.exe set Ignite-Sales-WCAgent DisplayName Ignite-Sales-WCAgent
nssm.exe set Ignite-Sales-WCAgent Description "Ignite - Sales [WebConsoleAgent]"
nssm.exe set Ignite-Sales-WCAgent ObjectName LocalSystem
nssm.exe set Ignite-Sales-WCAgent Start SERVICE_DEMAND_START
nssm.exe set Ignite-Sales-WCAgent Type SERVICE_WIN32_OWN_PROCESS
