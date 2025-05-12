package com.github.bfg.awsgraalvm

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class AwsGraalvmSpec extends Specification {

  @Inject
  EmbeddedApplication application

  def 'test it works'() {
    expect:
    application.running
  }
}
