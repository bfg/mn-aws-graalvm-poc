package com.github.bfg.awsgraalvm;

import com.github.bfg.awsgraalvm.cli.CliCmdRunner;
import io.micronaut.function.aws.runtime.MicronautLambdaRuntime;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Singleton
public class Application {
  public static final long STARTED_AT = System.currentTimeMillis();

  @SneakyThrows
  public static void main(String... args) {
    // PicoCliRunner.run/call and Micronaut.run() don't play well together,
    // so we need to decide which one to use based on the cli arguments
    if (shouldInvokeCliApp(Arrays.asList(args))) {
      CliCmdRunner.main(args);
    } else if (shouldInvokeFunction()) {
      MicronautLambdaRuntime.main(args);
    } else {
      // remove `run` from the args
      val filteredArgs = Stream.of(args)
          .filter(it -> !it.equals("run"))
          .toList();
      Micronaut.run(filteredArgs.toArray(new String[0]));
    }
  }

  private static boolean shouldInvokeFunction() {
    return Optional.ofNullable(System.getenv("AWS_LAMBDA_FUNCTION_NAME"))
        .map(String::trim)
        .map(it -> !it.isEmpty())
        .orElse(false);
  }

  private static boolean shouldInvokeCliApp(List<String> args) {
    val cliArgs = Set.of("-h", "--help", "-V", "--version");
    val hasCliArg = args.stream().anyMatch(cliArgs::contains);
    if (hasCliArg) {
      return true;
    }

    // application server doesn't support any sub-commands, only configuration parameter options
    // in a form of `-property.name=value` or `--property.name=value`, so we can safely assume that
    // if we remove all items starting with `-` and `run`, we will be left with the actual sub-command,
    // if any, so we can be sure that we should run the CLI app
    val hasCliArgs = args.stream()
        .filter(it -> !it.startsWith("-"))
        .filter(it -> !it.equals("run"))
        .toList()
        .isEmpty();

    return !hasCliArgs;
  }
}
