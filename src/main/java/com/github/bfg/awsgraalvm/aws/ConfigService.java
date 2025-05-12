package com.github.bfg.awsgraalvm.aws;

import io.micronaut.context.env.Environment;
import io.micronaut.context.env.EnvironmentPropertySource;
import io.micronaut.context.env.PropertySource;
import io.micronaut.context.env.SystemPropertiesPropertySource;
import jakarta.inject.Singleton;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class ConfigService {
  private final Environment env;

  ConfigService(Environment env) {
    this.env = env;
  }

  public Map<String, Object> get() {
    return env.getPropertySources()
        .stream()
        .map(this::propertiesFromSource)
        .flatMap(it -> it.entrySet().stream())
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
  }

  private Map<String, Object> propertiesFromSource(PropertySource src) {
    if (src instanceof EnvironmentPropertySource || src instanceof SystemPropertiesPropertySource) {
      return Map.of();
    }
    return StreamSupport.stream(src.spliterator(), false)
        .collect(Collectors.toMap(it -> it, it -> env.getProperty(it, String.class).orElse("")));
  }
}
