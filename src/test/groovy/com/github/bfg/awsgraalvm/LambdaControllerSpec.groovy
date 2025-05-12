package com.github.bfg.awsgraalvm

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@Slf4j
class LambdaControllerSpec extends Specification {
  static def jsonParser = new JsonSlurper()

  static {
    System.setProperty("aws.region", "us-east-1")
    System.setProperty("aws.endpointUrl", "http://localhost:4566")
  }

  @Shared
  @AutoCleanup
  ApiGatewayProxyRequestEventFunction handler = new ApiGatewayProxyRequestEventFunction()

  static def toJson(Object o){
    def json = JsonOutput.toJson(o)
    JsonOutput.prettyPrint(json, true)
  }

  def "root handler"() {
    given:
    def request = new APIGatewayProxyRequestEvent()
    request.path = "/"
    request.httpMethod = HttpMethod.GET.toString()

    when:
    def response = handler.handleRequest(request, new MockLambdaContext())
    def body = jsonParser.parseText(response.body)
    def bodyJSON = toJson(body)
    log.info("got response: $bodyJSON")

    then:
    response.statusCode == 200
    body.size() > 10
  }

  def "ssm handler"() {
    given:
    def request = new APIGatewayProxyRequestEvent()
    request.path = "/ssm"
    request.httpMethod = HttpMethod.GET.toString()

    when:
    def response = handler.handleRequest(request, new MockLambdaContext())
    def body = jsonParser.parseText(response.body)
    def bodyJSON = toJson(body)
    log.info("got response: $bodyJSON")

    then:
    response.statusCode == 200
    body.size() > 5
  }
}
