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
import org.apache.ignite.configuration.IgniteConfiguration;

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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

/**
 * Ignite Client loads data from CSV files to Caches via Ignite DataStreamer
 */
public class LoadCachesFromCsv {

    private static final Properties props = new Properties();

    /**
     * Start an Ignite Client and perform CSV Reading -> Cache Writing (via Ignite DataStreamer)
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If failed.
     */
    public static void main(String[] args) throws IgniteException, IOException {
        String dataLocation = "/data/sales/"; // default location
        DecimalFormat numFormat = (DecimalFormat)NumberFormat.getCurrencyInstance();
        String symbol = numFormat.getCurrency().getSymbol();
        numFormat.setNegativePrefix("-"+symbol);
        numFormat.setNegativeSuffix("");
        CSVParser csvParser = null;
        int n = 0; // record counter

        try (InputStream in = IgniteConfiguration.class.getClassLoader().getResourceAsStream("sales.properties")) {
            props.load(in);
            dataLocation = props.getProperty("dataLocation"); // <====================================================================================
            System.out.println(">>>>>>>>>>>>>>>>> loaded properties sales.properties; dataLocation set to: " + dataLocation);
        }
        catch (Exception ignored) {
            System.out.println(">>>>>>>>>>>>>>>>> Failed loading properties; using default dataLocation: " + dataLocation);
        }

        try (Ignite ignite = Ignition.start("sales-client.xml")){
            System.out.println(">>> CSV Stream Loading caches:");

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Office
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>> OfficeCache...");
            Reader reader = Files.newBufferedReader(Paths.get(dataLocation + "office.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(
                reader, 
                CSVFormat.DEFAULT
                    .withEscape('\\')
                    .withQuoteMode(QuoteMode.NONE)
                    .withFirstRecordAsHeader()
                    .withTrim()
            );

            try (IgniteDataStreamer<String, Office> streamer = ignite.dataStreamer("OfficeCache")){
				n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	String k = ParseTypes.parseString(csvRecord.get(0));
                    Office v = null;
                    try {
                        v = new Office(
                            ParseTypes.parseString(csvRecord.get(1)),
                            ParseTypes.parseString(csvRecord.get(2)),
                            ParseTypes.parseString(csvRecord.get(3)),
                            ParseTypes.parseString(csvRecord.get(4)),
                            ParseTypes.parseString(csvRecord.get(5)),
                            ParseTypes.parseString(csvRecord.get(6)),
                            ParseTypes.parseString(csvRecord.get(7)),
                            ParseTypes.parseString(csvRecord.get(8))
                        );
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading Office: " + e);
            } finally {
                System.out.println(">>> Load Office count: " + n);
            }

            /*
             * ------------------------------------------------------------------------------------------------------------
             * ProductLine
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> ProductLineCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "productline.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).withFirstRecordAsHeader().withTrim());

            try (IgniteDataStreamer<String, ProductLine> streamer = ignite.dataStreamer("ProductLineCache")){
				n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	String k = ParseTypes.parseString(csvRecord.get(0));
                    ProductLine v = null;
                    try {
                        v = new ProductLine(
                            ParseTypes.parseString(csvRecord.get(1)),
                            ParseTypes.parseString(csvRecord.get(2)),
                            ParseTypes.parseBytes(csvRecord.get(3))
                        );
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading ProductLine: " + e);
            } finally {
                System.out.println(">>> Load ProductLine count: " + n);
            }

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Product
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> ProductCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "product.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).withFirstRecordAsHeader().withTrim());

            try (IgniteDataStreamer<String, Product> streamer = ignite.dataStreamer("ProductCache")){
				n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	String k = ParseTypes.parseString(csvRecord.get(0));
                    Product v = null;
                    try {
                        v = new Product(
                            ParseTypes.parseString(csvRecord.get(1)),
                            ParseTypes.parseString(csvRecord.get(2)),
                            ParseTypes.parseString(csvRecord.get(3)),
                            ParseTypes.parseString(csvRecord.get(4)),
                            ParseTypes.parseString(csvRecord.get(5)),
                            ParseTypes.parseShort(csvRecord.get(6)),
                            ParseTypes.parseBigDecimal(csvRecord.get(7)),
                            ParseTypes.parseBigDecimal(csvRecord.get(8))
                        );
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading Product: " + e);
            } finally {
                System.out.println(">>> Load Product count: " + n);
            }

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Employee
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> EmployeeCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "employee.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).withFirstRecordAsHeader().withTrim());

            try (IgniteDataStreamer<Integer, Employee> streamer = ignite.dataStreamer("EmployeeCache")){
				n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	Integer k = ParseTypes.parseInteger(csvRecord.get(0));
                    Employee v = null;
                    try {
                        v = new Employee(
                            ParseTypes.parseString(csvRecord.get(1)),
                            ParseTypes.parseString(csvRecord.get(2)),
                            ParseTypes.parseString(csvRecord.get(3)),
                            ParseTypes.parseString(csvRecord.get(4)),
                            ParseTypes.parseString(csvRecord.get(5)),
                            ParseTypes.parseInteger(csvRecord.get(6)),
                            ParseTypes.parseString(csvRecord.get(7))
                        );
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading Employee: " + e);
            } finally {
                System.out.println(">>> Load Employee count: " + n);
            }

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Customer
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> CustomerCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "customer.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).withFirstRecordAsHeader().withTrim());

            try (IgniteDataStreamer<Integer, Customer> streamer = ignite.dataStreamer("CustomerCache")){
            	n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	Integer k = ParseTypes.parseInteger(csvRecord.get(0));
                    Customer v = null;
                    try {
                        v = new Customer(
                            ParseTypes.parseString(csvRecord.get(1)),
                            ParseTypes.parseString(csvRecord.get(2)),
                            ParseTypes.parseString(csvRecord.get(3)),
                            ParseTypes.parseString(csvRecord.get(4)),
                            ParseTypes.parseString(csvRecord.get(5)),
                            ParseTypes.parseString(csvRecord.get(6)),
                            ParseTypes.parseString(csvRecord.get(7)),
                            ParseTypes.parseString(csvRecord.get(8)),
                            ParseTypes.parseString(csvRecord.get(9)),
                            ParseTypes.parseString(csvRecord.get(10)),
                            ParseTypes.parseInteger(csvRecord.get(11)),
                            ParseTypes.parseBigDecimal(csvRecord.get(12))
                        );
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading Customer: " + e);
            } finally {
                System.out.println(">>> Load Customer count: " + n);
            }

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Order
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> OrderCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "order.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).withFirstRecordAsHeader().withTrim());

            try (IgniteDataStreamer<Integer, Order> streamer = ignite.dataStreamer("OrderCache")){
				n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	Integer k = ParseTypes.parseInteger(csvRecord.get(0));
                    Order v = null;
                    try {
                        v = new Order(
                            ParseTypes.parseDate(csvRecord.get(1)),
                            ParseTypes.parseDate(csvRecord.get(2)),
                            ParseTypes.parseDate(csvRecord.get(3)),
                            ParseTypes.parseString(csvRecord.get(4)),
                            ParseTypes.parseString(csvRecord.get(5)),
                            ParseTypes.parseInteger(csvRecord.get(6))
                        );
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading Order: " + e);
            } finally {
                System.out.println(">>> Load Order count: " + n);
            }

            /*
             * OrderDetail
             **/
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> OrderDetailCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "orderdetail.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, 
                CSVFormat.DEFAULT
                .withEscape('\\')
                .withQuoteMode(QuoteMode.NONE)
                .withFirstRecordAsHeader()
                .withTrim()
            );

            try (IgniteDataStreamer<OrderDetailKey, OrderDetail> streamer = ignite.dataStreamer("OrderDetailCache")){
                for (CSVRecord csvRecord : csvParser) {
                	OrderDetailKey k = new OrderDetailKey (
                        ParseTypes.parseInteger(csvRecord.get(0)),
                        ParseTypes.parseString(csvRecord.get(1))
                    );
                    OrderDetail v = null;
                    try {
                        v = new OrderDetail(
                            ParseTypes.parseInteger(csvRecord.get(2)),
                            ParseTypes.parseBigDecimal(csvRecord.get(3)),
                            ParseTypes.parseShort(csvRecord.get(4))
                        );
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading OrderDetail: " + e);
            } finally {
                System.out.println(">>> Load OrderDetail count: " + n);
            }

            /*
             * ------------------------------------------------------------------------------------------------------------
             * Payment
             * ------------------------------------------------------------------------------------------------------------
             */
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> PaymentCache...");
            reader = Files.newBufferedReader(Paths.get(dataLocation + "payment.csv"), StandardCharsets.UTF_8);
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).withFirstRecordAsHeader().withTrim());

            try (IgniteDataStreamer<PaymentKey, Payment> streamer = ignite.dataStreamer("PaymentCache")){
				n = 0; // record counter
                for (CSVRecord csvRecord : csvParser) {
                	PaymentKey k = new PaymentKey(
                        ParseTypes.parseInteger(csvRecord.get(0)),
                        ParseTypes.parseString(csvRecord.get(1))
                    );
                    Payment v = null;
                    try {
                        v = new Payment(
                            ParseTypes.parseDate(csvRecord.get(2)),
                            ParseTypes.parseBigDecimal(csvRecord.get(3))
                        );
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    streamer.addData(k, v);
                	n += 1;
                }
            } catch (Exception e ) {
                System.out.println("Caught Exception - loading Payment: " + e);
            } finally {
                System.out.println(">>> Load Payment count: " + n);
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            csvParser.close();
        }
        
        System.out.println(">>> CSV Stream Loading caches - COMPLETE!!!");
    }
}
