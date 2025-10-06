package com.resiliencepatterns.poc;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.resiliencepatterns.poc.infrastructure.clients")
@EnableCaching
public class ResiliencePatternsPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResiliencePatternsPocApplication.class, args);
	}

  /**
   * 🗄️ Configuración de Cache
   */
  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(List.of(
        new ConcurrentMapCache("external-api-cache")
    ));
    return cacheManager;
  }

}
