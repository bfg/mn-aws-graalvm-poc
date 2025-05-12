package com.github.bfg.awsgraalvm.utils;

import com.github.bfg.awsgraalvm.Application;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

@Slf4j
@UtilityClass
public class Utils {
  public Duration elapsedSince(Instant startedAt) {
    return Duration.between(startedAt, Instant.now());
  }

  public String elapsedSinceStr(Instant startedAt) {
    return fmtDur(elapsedSince(startedAt));
  }

  public Duration elapsedSince(long startedAtMillis) {
    return elapsedSince(Instant.ofEpochMilli(startedAtMillis));
  }

  public Duration elapsedSinceStartup() {
    return elapsedSince(Application.STARTED_AT);
  }

  public String elapsedSinceStartupStr() {
    return fmtDur(elapsedSinceStartup());
  }

  /**
   * Runs a function and logs the time it took to run.
   *
   * @param msg      function description, used for logging
   * @param supplier function to run
   * @param <T>      return type
   * @return function return type
   */
  public <T> T timeFunc(String msg, Supplier<T> supplier) {
    val startedAt = Instant.now();
    log.info("running `{}` (since startup: {})", msg, elapsedSinceStartupStr());

    T result = supplier.get();
    val durCall = elapsedSince(startedAt);
    val durStartup = elapsedSinceStartup();

    log.info("done `{}` (duration={}, since startup: {})", msg, fmtDur(durCall), fmtDur(durStartup));
    return result;
  }

  public String fmtDur(Duration duration) {
    val numSeconds = duration.toSeconds();
    return (numSeconds > 0) ?
        String.format("%d.%03d ms", numSeconds, duration.toMillisPart())
        :
        String.format("%d ms", duration.toMillisPart());
  }
}
