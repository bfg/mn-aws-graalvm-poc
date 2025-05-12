package com.github.bfg.awsgraalvm.lambda;

import com.github.bfg.awsgraalvm.aws.SsmService;
import io.micronaut.context.env.Environment;
import io.micronaut.context.env.EnvironmentPropertySource;
import io.micronaut.context.env.PropertySource;
import io.micronaut.context.env.SystemPropertiesPropertySource;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.micronaut.runtime.ApplicationConfiguration.APPLICATION_NAME;


@Controller
public class LambdaController {
  private final SsmService ssm;
  private final Environment env;
  private final String appName;

  public LambdaController(SsmService ssm, Environment env) {
    // Constructor injection of the SSM
    this.ssm = ssm;
    this.env = env;
    this.appName = env.get(APPLICATION_NAME, String.class).orElse("unknown-app");
  }

  @Get
  public Map<String, Object> index() {
    return getAllProps();
  }

  @Get("/ssm")
  public Map<String, String> ssm() {
    val path = "/" + appName + "/";
    return ssm.getParameters(path);
  }


  private Map<String, Object> getAllProps() {
    return env.getPropertySources()
        .stream()
        .map(this::propertiesFromSource)
        .flatMap(it -> it.entrySet().stream())
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
