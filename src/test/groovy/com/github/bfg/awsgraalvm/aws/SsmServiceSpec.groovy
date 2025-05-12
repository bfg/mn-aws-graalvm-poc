package com.github.bfg.awsgraalvm.aws

import groovy.util.logging.Slf4j
import io.micronaut.context.env.Environment
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Slf4j
@Stepwise
@MicronautTest
class SsmServiceSpec extends Specification {
  @Inject
  Environment env

  @Inject
  SsmService ssm

  @Shared
  String appName

  def "should contain correct app name"() {
    def appName = env.getProperty("micronaut.application.name", String).get()

    expect:
    appName == "my-app"

    cleanup:
    this.appName = appName
  }

  def "should return parameters"() {
    given:
    def path = "/${appName}/"

    when:
    def params = ssm.getParameters(path)
    log.info("retrieved {} parameters: {}", params.size(), params)

    then:
    params.size() >= 5
    params.each { param ->
      assert param.key.startsWith(path)
      assert !param.value.isBlank()
    }
    params.values().toSet().size() == params.size()
  }
}
