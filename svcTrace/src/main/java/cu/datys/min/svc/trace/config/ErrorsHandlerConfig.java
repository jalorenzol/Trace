package cu.datys.min.svc.trace.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.general.ProblemAdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@ControllerAdvice
public class ErrorsHandlerConfig implements SecurityAdviceTrait, ProblemAdviceTrait {
}
