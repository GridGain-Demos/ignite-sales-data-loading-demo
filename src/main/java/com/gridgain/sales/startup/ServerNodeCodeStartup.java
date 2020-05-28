package com.gridgain.sales.startup;

import com.gridgain.sales.config.ServerConfigurationFactory;
import org.apache.ignite.Ignition;

/** This file was generated by Ignite Web Console (04/23/2020, 16:26) **/
public class ServerNodeCodeStartup {
    /**
     * Start up node with specified configuration.
     * 
     * @param args Command line arguments, none required.
     * @throws Exception If failed.
     **/
    public static void main(String[] args) throws Exception {
        Ignition.start(ServerConfigurationFactory.createConfiguration());
    }
}