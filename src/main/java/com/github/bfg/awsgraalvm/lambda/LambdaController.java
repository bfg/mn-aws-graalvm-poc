package com.github.bfg.awsgraalvm.lambda;

import com.github.bfg.awsgraalvm.aws.ConfigService;
import com.github.bfg.awsgraalvm.aws.SsmService;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.val;

import java.util.Map;


@Controller
public class LambdaController {
  private final SsmService ssm;
  private final ConfigService cfgSvc;
  private final String appName;

  public LambdaController(SsmService ssm,
                          ConfigService cfgSvc,
                          @Value("${micronaut.application.name:unknown-app}") String appName) {
    // Constructor injection of the SSM
    this.ssm = ssm;
    this.cfgSvc = cfgSvc;
    this.appName = appName;
  }

  @Get
  public Map<String, Object> index() {
    return cfgSvc.get();
  }

  @Get("/ssm")
  public Map<String, String> ssm() {
    val path = "/" + appName + "/";
    return ssm.getParameters(path);
  }
}
