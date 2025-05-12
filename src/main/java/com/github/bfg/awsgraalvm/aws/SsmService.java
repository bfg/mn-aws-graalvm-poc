package com.github.bfg.awsgraalvm.aws;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public final class SsmService {
  private final SsmClient ssm;

  public SsmService(SsmClient ssm) {
    this.ssm = ssm;

    log.info("created SSM service with client: {}", ssm);
  }

  public Map<String, String> getParameters(String path) {
    log.info("getting parameters for path: {}", path);
    return ssm.getParametersByPath(b -> b.path(path).recursive(true).withDecryption(true))
        .parameters()
        .stream()
        .sorted(Comparator.comparing(Parameter::name))
        .collect(Collectors.toMap(Parameter::name, Parameter::value, (a, b) -> b, LinkedHashMap::new));
  }
}
