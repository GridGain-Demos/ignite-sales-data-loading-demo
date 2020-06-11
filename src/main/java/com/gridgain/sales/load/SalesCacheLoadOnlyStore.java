package com.gridgain.sales.load;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import javax.cache.integration.CacheLoaderException;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
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

/**
 * Sales CSV LoadOnly CacheStore data loader
 * 
 * @param <K>
 * @param <V>
 */
public class SalesCacheLoadOnlyStore<K, V> extends CacheLoadOnlyStoreAdapter<Object, Object, CSVRecord>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Reader reader;
    private String csvFileName;
    private String fileType;

    /** private static function to set reader etc. */
    private static void setReader(String fn) throws IOException {
        reader = Files.newBufferedReader(Paths.get(fn), StandardCharsets.UTF_8);
    }

    /** Empty Constructor. */
    public SalesCacheLoadOnlyStore() {
        System.out.println(">>> SalesCacheLoadOnlyStore (LoadOnly type) null constructor...");
        // System.out.println(">>> SalesCacheLoadOnlyStore (LoadOnly type) null
        // constructor; default to loading orders (order.csv)...");
        // this.csvFileName = "/data/sales/order.csv";
    }

    public SalesCacheLoadOnlyStore(String csvFileName) {
        System.out.println(
                ">>> SalesCacheLoadOnlyStore (LoadOnly type) with csv FileName " + csvFileName + " constructed.");
        this.csvFileName = csvFileName;
    }

    /** {@inheritDoc} */
    @Override
    protected Iterator<CSVRecord> inputIterator(@Nullable Object... args) throws CacheLoaderException {
        System.out.println(">>> SalesCacheLoadOnlyStore inputIterator called with args: " + Arrays.toString(args));

        /*
         * We have added support for over-riding the configured csvFileName
         * 
         * If an argument was supplied at cache.Load(newFilenameArgs), then handle; Else
         * just use the configured csvFileName to determine what the fileType was by
         * looking at the name
         */
        String file = null; // used to hold just the file name (no path)
        if (args != null && args.length != 0) {
            // if a parameter is supplied to the cache.load() method, handle this
            csvFileName = args[0].toString(); // the only supported arg is a new csvFileName

            // Check if supplied args[0] / csvFileName was just a file or full path to file?
            String[] csvFileArgTokens = args[0].toString()
                    .split(Matcher.quoteReplacement(System.getProperty("file.separator"))); // Either short like:
                                                                                            // [office.csv] or long
                                                                                            // like: [C:, data, sales,
                                                                                            // office.csv]

            // check the number of tokens in the file arg supplied to decide what to do
            if (csvFileArgTokens.length == 1) {
                // a single element, not a path, i.e. [office.csv]
                System.out.println(">>> SalesCacheLoadOnlyStore inputIterator; csvFileArgTokens: "
                        + Arrays.toString(csvFileArgTokens));
                file = csvFileArgTokens[0];
            } else {
                // a path was supplied, i.e. [C:, data, sales, office.csv]
                System.out.println(">>> SalesCacheLoadOnlyStore inputIterator; csvFileArgTokens: "
                        + Arrays.toString(csvFileArgTokens));
                file = csvFileArgTokens[(csvFileArgTokens.length - 1)]; // i.e. the last token when spliting by
                                                                        // file.separator
            }
        } else {
            // no load argument, so figure out fileType by looking at the last part of the
            // configured csvFileName
            String[] csvFileNameTokens = csvFileName.split(Matcher.quoteReplacement(System.getProperty("file.separator")));

            // check the number of tokens in the csvFileName to decide what to do
            if (csvFileNameTokens.length == 1) {
                // a single element, not a path, i.e. [office.csv]
                System.out.println(">>> SalesCacheLoadOnlyStore inputIterator; csvFileNameTokens: "
                        + Arrays.toString(csvFileNameTokens));
                file = csvFileNameTokens[0];
            } else {
                // a path was supplied, i.e. [C:, data, sales, office.csv]
                System.out.println(">>> SalesCacheLoadOnlyStore inputIterator; csvFileNameTokens: "
                        + Arrays.toString(csvFileNameTokens));
                file = csvFileNameTokens[(csvFileNameTokens.length - 1)]; // i.e. the last token when spliting by
                                                                          // file.separator
            }
        }

        // we figured out the filename (either from supplied args or from the cacheStore
        // configured parameter)
        fileType = (file.split("\\.(?=[^\\.]+$)"))[0].toLowerCase(); // i.e. take the lowercase of the first element of
                                                                     // tokens in this form: [office, csv]
        System.out.println(">>> SalesCacheLoadOnlyStore inputIterator: csvFileName:" + csvFileName + "; fileType: " + fileType);

        try {
            // System.out.println(">>> SalesCacheLoadOnlyStore.inputIterator() set File
            // Reader for: " + csvFileName + " ...");
            setReader(csvFileName);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withEscape('\\')
                .withQuoteMode(QuoteMode.NONE)
                .withFirstRecordAsHeader()
                .withTrim());
            Iterator csvIterator = csvParser.iterator();

            /**
             * Iterator return CSVRecords
             * 
             * Then loader calls parse(CSVRecord)
             */
            return new Iterator<CSVRecord>() {

                /** {@inheritDoc} */
                @Override
                public boolean hasNext() {
                    if (!csvIterator.hasNext()) {
                        try {
                            // try {
                            //     Thread.sleep(1000); // file contention on Windows <== should not happen when running single node on a machine
                            // } catch (InterruptedException e) {
                                reader.close();
                            // }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                    return true;
                }

                /** {@inheritDoc} */
                @Override public CSVRecord next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return (CSVRecord)csvIterator.next();
                }

                /** {@inheritDoc} */
                @Override public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    @Override @Nullable
    protected IgniteBiTuple<Object, Object> parse(CSVRecord csvRecord, @Nullable Object... args) {
        if (args != null && args.length != 0)
        {
            // we do not handle a runtime arg for parse
            //System.out.println(">>> SalesCacheLoadOnlyStore parse; WE DO NOT HANDLE RUNTIME ARGS HERE: " + Arrays.toString(args));
        }

        Object key = null;
        Object value = null;

        switch (fileType) {
            case "customer" :
                Integer  customerK = ParseTypes.parseInteger(csvRecord.get(0));
                Customer customerV = null;
                try {
                    customerV = new Customer(
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
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = customerK;
                value = customerV;
                break;

            case "employee" :
                Integer employeeK = ParseTypes.parseInteger(csvRecord.get(0));
                Employee employeeV = null;
                try {
                    employeeV = new Employee(
                        ParseTypes.parseString(csvRecord.get(1)),
                        ParseTypes.parseString(csvRecord.get(2)),
                        ParseTypes.parseString(csvRecord.get(3)),
                        ParseTypes.parseString(csvRecord.get(4)),
                        ParseTypes.parseString(csvRecord.get(5)),
                        ParseTypes.parseInteger(csvRecord.get(6)),
                        ParseTypes.parseString(csvRecord.get(7))
                );
                } catch (NumberFormatException e) {
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = employeeK;
                value = employeeV;
                break;

            case "office" :
                String officeK = ParseTypes.parseString(csvRecord.get(0));
                Office officeV = null;
                try {
                    officeV = new Office(
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
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = officeK;
                value = officeV;
                break;

            case "order" :
                Integer orderK = ParseTypes.parseInteger(csvRecord.get(0));
                Order orderV = null;
                try {
                    orderV = new Order(
                        ParseTypes.parseDate(csvRecord.get(1)),
                        ParseTypes.parseDate(csvRecord.get(2)),
                        ParseTypes.parseDate(csvRecord.get(3)),
                        ParseTypes.parseString(csvRecord.get(4)),
                        ParseTypes.parseString(csvRecord.get(5)),
                        ParseTypes.parseInteger(csvRecord.get(6))
                );
                } catch (NumberFormatException e) {
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = orderK;
                value = orderV;
                break;

            case "orderdetail" :
                OrderDetailKey orderdetailK = null;
                OrderDetail orderdetailV = null;
                try {
                    orderdetailK = new OrderDetailKey(ParseTypes.parseInteger(csvRecord.get(0)),csvRecord.get(1));
                    orderdetailV = new OrderDetail(
                        ParseTypes.parseInteger(csvRecord.get(2)),
                        ParseTypes.parseBigDecimal(csvRecord.get(3)),
                        ParseTypes.parseShort(csvRecord.get(4))
                );
                } catch (NumberFormatException e) {
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = orderdetailK;
                value = orderdetailV;
                break;

            case "payment" :
                PaymentKey paymentK = new PaymentKey(
                    ParseTypes.parseInteger(csvRecord.get(0)),
                    ParseTypes.parseString(csvRecord.get(1))
            );
                Payment paymentV = null;
                try {
                    paymentV = new Payment(
                        ParseTypes.parseDate(csvRecord.get(2)),
                        ParseTypes.parseBigDecimal(csvRecord.get(3))
                );
                } catch (NumberFormatException e) {
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = paymentK;
                value = paymentV;
                break;

            case "product" :
                String productK = ParseTypes.parseString(csvRecord.get(0));
                Product productV = null;
                try {
                    productV = new Product(
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
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = productK;
                value = productV;
                break;

            case "productline" :
                String productlineK = csvRecord.get(0);
                ProductLine productlineV = null;
                try {
                    productlineV = new ProductLine(
                        ParseTypes.parseString(csvRecord.get(1)),
                        ParseTypes.parseString(csvRecord.get(2)),
                        ParseTypes.parseBytes(csvRecord.get(3))
                );
                } catch (NumberFormatException e) {
                    System.out.println(">>> SalesCacheLoadOnlyStore parse(" + fileType + ") exception: " + e);
                    e.printStackTrace();
                }
                key = productlineK;
                value = productlineV;
                break;
        }

        return new IgniteBiTuple<Object, Object>(key,value);
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        System.out.println(">>> SalesCacheLoadOnlyStore: setCsvFileName(" + csvFileName + ")...");
        this.csvFileName = csvFileName;
    }

}
