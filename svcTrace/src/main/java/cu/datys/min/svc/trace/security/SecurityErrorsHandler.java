package cu.datys.min.svc.trace.security;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@ControllerAdvice
public class SecurityErrorsHandler implements SecurityAdviceTrait {
}