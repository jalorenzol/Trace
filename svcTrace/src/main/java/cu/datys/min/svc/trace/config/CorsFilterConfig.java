package cu.datys.min.svc.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CorsFilterConfig implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin;
        String credentialFlag;
        if (request.getHeader("Origin") == null) {
            origin = "*";
            credentialFlag = "false";
        } else {
            origin = request.getHeader("Origin");
            credentialFlag = "true";
        }

        // origin headers
        response.addHeader("Access-Control-Allow-Origin", origin.toString());
        response.setHeader("Access-Control-Allow-Credentials", credentialFlag);

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            ////log.info("Received OPTIONS request from origin:" + origin.toString());
            response.setHeader("Access-Control-Max-Age", "3600");

            String methods = request.getHeader("Access-Control-Request-Method");
            if (!StringUtils.hasText(methods)) {
                methods = "HEAD,OPTIONS,GET,POST,PUT,DELETE,PATCH";
            }
            response.setHeader("Access-Control-Allow-Methods", methods);

            String headers = request.getHeader("Access-Control-Request-Headers");
            if (!StringUtils.hasText(headers)) {
                headers = "Origin, X-Requested-With, Content-Type, Accept, x-auth";
            }
            response.setHeader("Access-Control-Allow-Headers", headers);
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
