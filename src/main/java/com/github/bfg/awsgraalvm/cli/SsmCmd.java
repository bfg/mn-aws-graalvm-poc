package com.github.bfg.awsgraalvm.cli;

import com.github.bfg.awsgraalvm.aws.SsmService;
import com.github.bfg.awsgraalvm.utils.Utils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@Requires(env = "cli")
@Command(
    name = "ssm",
    description = "Queries AWS SSM")
@ReflectiveAccess
public final class SsmCmd extends BaseCmd {
  private final SsmService ssmSvc;

  @Parameters
  private List<String> paths = List.of();

  public SsmCmd(SsmService ssmSvc) {
    this.ssmSvc = ssmSvc;
    log.info("created SSM command with service: {}", ssmSvc);
  }

  @Override
  @SneakyThrows
  int doCall() {
    return Utils.timeFunc("ssm command doCall", () -> doIt());
  }

  @SneakyThrows
  private int doIt() {
    if (paths.isEmpty()) {
      return die("no application paths specified. Run with --help for instructions.");
    }

    val map = paths.stream()
        .map(it -> ssmSvc.getParameters(it))
        .flatMap(it -> it.entrySet().stream())
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));


    map.forEach((k, v) -> {
      println(String.format("%-20.20s = %s", k, v));
    });
    return 0;
  }
}
