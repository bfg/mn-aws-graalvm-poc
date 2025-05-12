package com.github.bfg.awsgraalvm

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@Slf4j
@MicronautTest(environments = "ec2")
class LambdaControllerSpec extends Specification {
  static def jsonParser = new JsonSlurper()

  static {
    System.setProperty("micronaut.environments", "ec2,test")
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

    when:
    def awsSsmVars = [
        'a', 'b', 'c', 'd', 'foo.bar'
    ]

    then:
    // make sure that stuff gets pulled in from aws ssm
    awsSsmVars.each {
      assert body[it].isEmpty() == false
      assert body[it].contains("-val-")
    }

    // make sure that app config gets something from aws ssm
    !body['app.foo'].isBlank()
    body['app.foo']== body['foo.bar']
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
