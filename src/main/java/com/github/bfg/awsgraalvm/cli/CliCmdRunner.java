package com.github.bfg.awsgraalvm.cli;

import com.github.bfg.awsgraalvm.utils.Utils;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.core.annotation.ReflectiveAccess;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;

@Slf4j
@Command(
    name = "aws-graalvm", description = {"aws command command runner\n"},
    subcommands = {
        SsmCmd.class,
    })
@ReflectiveAccess
public class CliCmdRunner extends BaseCmd {
  public static void main(String... args) {
    // WARNING: PicocliRunner.call() always returns null, we must make sure that exit() gets called BY THE
    //          cli command callables themselves!!!
    log.info("starting mn clicmdrunner after: {}", Utils.elapsedSinceStartupStr());
    val exitStatus = Utils.timeFunc("mn clicmdrunner action", () -> PicocliRunner.call(CliCmdRunner.class, args));
    System.exit(0);
  }

  @Override
  int doCall() {
    return die("no sub-command specified. Run with --help for instructions.");
  }
}
