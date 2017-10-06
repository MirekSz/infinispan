package hello;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.remoting.rpc.RpcManagerImpl;
import org.infinispan.stats.CacheContainerStats;
import org.infinispan.stats.ClusterCacheStats;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InfinispanMap {

	public static void main(String[] args) throws Exception {
		// Construct a simple local cache manager with default configuration
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
		GlobalConfiguration build = global.build();
		DefaultCacheManager cacheManager = new DefaultCacheManager(build);
		// Define local cache configuration
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.clustering().cacheMode(CacheMode.REPL_SYNC).jmxStatistics().enable();
		Configuration build2 = configurationBuilder.build();
		cacheManager.defineConfiguration("local", build2);
		// Obtain the local cache
		Cache<String, String> cache = cacheManager.getCache("local");

		cacheManager.getStats().setStatisticsEnabled(true);
		// Store a value
		System.out.printf("key = %s\n", cache.get("key"));
		cache.put("key", "value" + System.currentTimeMillis());
		// Retrieve the value and print it out
		System.out.printf("key = %s\n", cache.getAdvancedCache().getCacheEntry("key").getMetadata());
		// Stop the cache manager and release all resources
		CacheContainerStats stats = cacheManager.getStats();
		ClusterCacheStats component = cache.getAdvancedCache().getComponentRegistry()
				.getComponent(ClusterCacheStats.class);
		RpcManagerImpl component2 = (RpcManagerImpl) cache.getAdvancedCache().getComponentRegistry()
				.getComponent(org.infinispan.remoting.rpc.RpcManager.class);
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(objectMapper.writeValueAsString(stats));
		System.out.println(objectMapper.writeValueAsString(component));
		System.out.println(objectMapper.writeValueAsString(component2.getAverageReplicationTime()));
		Thread.sleep(10000);
		cacheManager.stop();

	}
}
