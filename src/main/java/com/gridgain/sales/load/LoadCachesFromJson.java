/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gridgain.sales.load;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;

import com.gridgain.sales.model.Customer;
import com.gridgain.sales.model.Employee;
import com.gridgain.sales.model.Office;
import com.gridgain.sales.model.Order;
import com.gridgain.sales.model.OrderDetail;
import com.gridgain.sales.model.OrderDetailKey;
import com.gridgain.sales.model.Payment;
import com.gridgain.sales.model.PaymentKey;
import com.gridgain.sales.model.Product;
import com.gridgain.sales.model.ProductLine;
import com.gridgain.sales.utility.ParseTypes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Starts up an empty node with example compute configuration.
 */
public class LoadCachesFromJson {

    static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Start up an empty node with example compute configuration.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If failed.
     */
    public static void main(String[] args) throws IgniteException, IOException {
        DecimalFormat numFormat = (DecimalFormat)NumberFormat.getCurrencyInstance();
        String symbol = numFormat.getCurrency().getSymbol();
        numFormat.setNegativePrefix("-"+symbol);
        numFormat.setNegativeSuffix("");

        try (Ignite ignite = Ignition.start("sales-client.xml")){

            System.out.println(">>> JSON Stream Loading caches:");

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Customers.json
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> CustomerCache...");
            //Reader reader = Files.newBufferedReader(Paths.get("Data/Customers.json"), StandardCharsets.UTF_8);

            JsonFactory factory = new JsonFactory();

            //read json file data to String
    		byte[] jsonData = Files.readAllBytes(Paths.get("Data/Customers.json"));
    		
            ObjectMapper mapper = new ObjectMapper(factory);
            JsonNode CustomerNode = mapper.readTree(jsonData);  

            try (IgniteDataStreamer<Integer, Customer> streamer = ignite.dataStreamer("CustomerCache")){

	            Iterator<Map.Entry<String,JsonNode>> customerIterator = CustomerNode.fields();
	            while (customerIterator.hasNext()) {
	
	                Map.Entry<String,JsonNode> rootNode = customerIterator.next();
	                System.out.println("Key: " + rootNode.getKey() + "\tValue:" + rootNode.getValue());

	                JsonNode CustomerRootValue = rootNode.getValue();
	                String CustomerRootKey = rootNode.getKey();
	                
	                if (CustomerRootKey.equalsIgnoreCase("Customer")) {

	    	           	int n = 0; // record counter
	    	           	Iterator<JsonNode> Customer = CustomerRootValue.elements();
	    	           	//Iterator<Map.Entry<String,JsonNode>> customerIterator = CustomerValue.fields();
	    	            while (Customer.hasNext()) {
	                    	JsonNode customer = Customer.next();
	                    	System.out.println(
                                "customer.customerNumber: " + customer.get("customerNumber") + 
                                "; customer.contactlastname: " + customer.get("contactlastname") + 
                                "; customer.contactfirstname: " + customer.get("contactfirstname"));
			                Integer k = ParseTypes.parseInteger(customer.get("customerNumber").asText());
                            Customer v = null;
		                    try {
		                        v = new Customer(
                                    customer.get("customername").toString(),
                                    customer.get("contactlastname").toString(),
                                    customer.get("contactfirstname").toString(),
                                    customer.get("phone").toString(),
                                    customer.get("addressline1").toString(),
                                    customer.get("addressline2").toString(),
                                    customer.get("city").toString(),
                                    customer.get("state").toString(),
                                    customer.get("postalcode").toString(),
                                    customer.get("country").toString(),
                                    ParseTypes.parseInteger(customer.get("salesrepemployeenumber").asText()),
                                    ParseTypes.parseBigDecimal(customer.get("creditlimit").asText())
                                );
		                    } catch (NumberFormatException e) {
		                        // TODO Auto-generated catch block
		                        e.printStackTrace();
		                    }
		                    streamer.addData(k, v);
	    	            } // End while Customer array iterator
	                } // end if this is a Customer entry
	            } // End while Customer iterator

            }

            System.out.println(">>> JSON Stream Loading caches - COMPLETE!!!");

        }
    }

}
