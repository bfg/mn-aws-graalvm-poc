package com.github.bfg.awsgraalvm.cli;

import com.github.bfg.awsgraalvm.aws.ConfigService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;

@Slf4j
@Singleton
@Requires(env = "cli")
@Command(
    name = "config",
    description = "Displays micronaut config properties.")
@ReflectiveAccess
public final class ConfigCmd extends BaseCmd {
  private final ConfigService cfgSvc;

  @Parameters
  private List<String> paths = List.of();

  public ConfigCmd(ConfigService cfgSvc) {
    this.cfgSvc = cfgSvc;
  }

  @Override
  @SneakyThrows
  int doCall() {
    val cfg = cfgSvc.get();

    cfg.entrySet().stream()
        .filter(it -> isValidPath(it.getKey()))
        .forEach(it -> {
          println("%-40.40s = %s", it.getKey(), it.getValue());
        });

    return 0;
  }

  private boolean isValidPath(String path) {
    if (paths.isEmpty()) return true;

    return paths.stream().anyMatch(path::startsWith);
  }
}
