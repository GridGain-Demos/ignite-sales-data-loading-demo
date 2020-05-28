package com.gridgain.sales.test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;

import javax.cache.Cache;

import com.gridgain.sales.model.Order;
import com.gridgain.sales.model.OrderDetail;
import com.gridgain.sales.model.OrderDetailKey;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheKeyConfiguration;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.QueryIndexType;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

public class CacheKeyTest {

    private static final Object OrderDetailKey = null;

    public static void main(String[] args) throws Exception {

        // Create Ignite Configuration
        IgniteConfiguration cfg = createConfiguration();

        // Start Ignite
        Ignite ignite = Ignition.start(cfg);

        // Get the two caches from the running ignite instance
        Cache<Integer, Order> oCache = ignite.getOrCreateCache("OrderCache");
        Cache<OrderDetailKey, OrderDetail> odCache = ignite.getOrCreateCache("OrderDetailCache");

        // Put data into the Order Cache
        oCache.put(1, new Order(new Date(2020,05,03), new Date(2020,05,04), null, "Taken", "Order 1 Comments", 1001));
        oCache.put(2, new Order(new Date(2020,05,03), new Date(2020,05,05), null, "Speculated", "Order 2 Comments", 1002));

        // Put data into the OrderDetail Cache
        odCache.put(new OrderDetailKey(1,"A1231"), new OrderDetail(100, BigDecimal.valueOf(123.45), new Short("1")));
        odCache.put(new OrderDetailKey(1,"A1232"), new OrderDetail(50, BigDecimal.valueOf(67.89), new Short("2")));
        odCache.put(new OrderDetailKey(2,"B1231"), new OrderDetail(1, BigDecimal.valueOf(10.0), new Short("1")));
        odCache.put(new OrderDetailKey(2,"B1232"), new OrderDetail(2, BigDecimal.valueOf(20.0), new Short("2")));
        odCache.put(new OrderDetailKey(2,"B1233"), new OrderDetail(3, BigDecimal.valueOf(30.0), new Short("3")));
    }

    /**
     * Configure grid.
     * 
     * @return Ignite configuration.
     * @throws Exception If failed to construct Ignite configuration instance.
     **/
    public static IgniteConfiguration createConfiguration() throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setIgniteInstanceName("sales");

        TcpDiscoverySpi discovery = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList("127.0.0.1:47500..47510"));
        discovery.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(discovery);

        cfg.setCacheKeyConfiguration(new CacheKeyConfiguration[] {
            new CacheKeyConfiguration("com.gridgain.sales.model.OrderDetailKey", "ordernumber")
        });

        DataStorageConfiguration dataStorageCfg = new DataStorageConfiguration();
        DataRegionConfiguration dataRegionCfg = new DataRegionConfiguration();

        dataRegionCfg.setPersistenceEnabled(false);
        dataStorageCfg.setDefaultDataRegionConfiguration(dataRegionCfg);
        cfg.setDataStorageConfiguration(dataStorageCfg);

        cfg.setCacheConfiguration(
            cacheOrderCache(),
            cacheOrderDetailCache()
        );

        return cfg;
    }

    /**
     * Create configuration for cache "OrderCache".
     * 
     * @return Configured cache.
     * @throws Exception if failed to create cache configuration.
     **/
    public static CacheConfiguration cacheOrderCache() throws Exception {
        CacheConfiguration ccfg = new CacheConfiguration();

        ccfg.setName("OrderCache");
        ccfg.setCacheMode(CacheMode.PARTITIONED);
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfg.setCopyOnRead(true);
        ccfg.setSqlSchema("SALES");

        ArrayList<QueryEntity> qryEntities = new ArrayList<>();
        QueryEntity qryEntity = new QueryEntity();
        qryEntity.setKeyType("java.lang.Integer");
        qryEntity.setValueType("com.gridgain.sales.model.Order");
        qryEntity.setKeyFieldName("ordernumber");

        HashSet<String> keyFields = new HashSet<>();
        keyFields.add("ordernumber");
        qryEntity.setKeyFields(keyFields);

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("orderdate", "java.sql.Date");
        fields.put("requireddate", "java.sql.Date");
        fields.put("shippeddate", "java.sql.Date");
        fields.put("status", "java.lang.String");
        fields.put("comments", "java.lang.String");
        fields.put("customernumber", "java.lang.Integer");
        fields.put("ordernumber", "java.lang.Integer");
        qryEntity.setFields(fields);

        ArrayList<QueryIndex> indexes = new ArrayList<>();
        QueryIndex index = new QueryIndex();
        index.setName("customerNumber");
        index.setIndexType(QueryIndexType.SORTED);
        LinkedHashMap<String, Boolean> indFlds = new LinkedHashMap<>();
        indFlds.put("customernumber", false);
        index.setFields(indFlds);
        indexes.add(index);
        qryEntity.setIndexes(indexes);
        qryEntities.add(qryEntity);
        ccfg.setQueryEntities(qryEntities);

        return ccfg;
    }

    /**
     * Create configuration for cache "OrderDetailCache".
     * 
     * @return Configured cache.
     * @throws Exception if failed to create cache configuration.
     **/
    public static CacheConfiguration cacheOrderDetailCache() throws Exception {
        CacheConfiguration ccfg = new CacheConfiguration();

        ccfg.setName("OrderDetailCache");
        ccfg.setCacheMode(CacheMode.PARTITIONED);
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfg.setCopyOnRead(true);
        ccfg.setSqlSchema("SALES");

        ArrayList<QueryEntity> qryEntities = new ArrayList<>();
        QueryEntity qryEntity = new QueryEntity();
        qryEntity.setKeyType("com.gridgain.sales.model.OrderDetailKey");
        qryEntity.setValueType("com.gridgain.sales.model.OrderDetail");

        HashSet<String> keyFields = new HashSet<>();
        keyFields.add("ordernumber");
        keyFields.add("productcode");
        qryEntity.setKeyFields(keyFields);

        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("ordernumber", "java.lang.Integer");
        fields.put("productcode", "java.lang.String");
        fields.put("quantityordered", "java.lang.Integer");
        fields.put("priceeach", "java.math.BigDecimal");
        fields.put("orderlinenumber", "java.lang.Short");
        qryEntity.setFields(fields);

        ArrayList<QueryIndex> indexes = new ArrayList<>();
        QueryIndex index = new QueryIndex();
        index.setName("productCode");
        index.setIndexType(QueryIndexType.SORTED);
        LinkedHashMap<String, Boolean> indFlds = new LinkedHashMap<>();
        indFlds.put("productcode", false);
        index.setFields(indFlds);
        indexes.add(index);
        qryEntity.setIndexes(indexes);
        qryEntities.add(qryEntity);

        ccfg.setKeyConfiguration(new CacheKeyConfiguration[] {
            new CacheKeyConfiguration("com.gridgain.sales.model.OrderDetailKey", "ordernumber")
        });

        ccfg.setQueryEntities(qryEntities);

        return ccfg;
    }
}