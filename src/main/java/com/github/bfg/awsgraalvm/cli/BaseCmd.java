package com.github.bfg.awsgraalvm.cli;

import com.github.bfg.awsgraalvm.utils.Utils;
import io.micronaut.core.annotation.ReflectiveAccess;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;

import java.io.PrintStream;
import java.util.concurrent.Callable;

/**
 * Base class for writing CLI sub-command implementations
 */
@Slf4j
@Command(
    sortSynopsis = false,
    sortOptions = false,

    mixinStandardHelpOptions = true,
//    versionProvider = GitInfoVersionProvider::class,

    descriptionHeading = "͏\n",
    parameterListHeading = "\nPARAMETERS:\n",
    commandListHeading = "\nCOMMANDS:\n",
    usageHelpWidth = 120
)
@ReflectiveAccess
abstract class BaseCmd implements Callable<Integer> {
  protected PrintStream stdout = System.out;
  protected PrintStream stderr = System.err;

  /**
   * Returns the standard output stream
   */
  protected PrintStream stdout() {
    return stdout;
  }

  /**
   * Writes to the [stdout] stream
   *
   * @param fmt  the format string
   * @param args the format arguments
   */
  protected void print(String fmt, Object... args) {
    val out = stdout();
    val str = String.format(fmt, args);

    out.print(str);
  }

  /**
   * Writes to the [stdout] stream with a newline at the end
   *
   * @param fmt  the format string
   * @param args the format arguments
   */
  protected void println(String fmt, Object... args) {
    print(fmt + "\n", args);
  }

  /**
   * Returns the standard error stream
   */
  protected PrintStream stderr() {
    return stderr;
  }

  /**
   * Prints given message to the [stderr] stream
   *
   * @param fmt  the format string
   * @param args the format arguments
   */
  protected void printerr(String fmt, Object... args) {
    val out = stderr();
    val str = String.format(fmt, args);

    out.print(str);
  }

  /**
   * Prints given message to the [stderr] stream with a newline at the end
   *
   * @param fmt  the format string
   * @param args the format arguments
   */
  protected void printerrln(String fmt, Object... args) {
    printerr(fmt + "\n", args);
  }

  /**
   * Issues fatal error and exits the JVM with exit status 1
   *
   * @param fmt  the format string
   * @param args the format arguments
   */
  protected int die(String fmt, Object... args) {
    printerrln("FATAL: " + fmt, args);
    return exit(1);
  }

  /**
   * Immediately exits the JVM with the given exit status
   *
   * @param exitStatus the exit status
   */
  protected int exit(int exitStatus) {
    System.exit(exitStatus);
    return exitStatus;
  }

  /**
   * The actual call() implementation
   */
  abstract int doCall();

  /**
   * The callable implementation that wraps the [doCall] method. This is because it seems that micronaut-picocli returns
   * undefined result to the caller.
   */
  public final Integer call() {
    val exitStatus = Utils.timeFunc("doCall", this::doCall);

    // exit immediately, because PicoCliRunner does not return the exit status from callables it invokes
    return exit(exitStatus);
  }
}
