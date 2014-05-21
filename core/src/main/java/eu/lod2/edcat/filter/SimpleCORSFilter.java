package eu.lod2.edcat.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * SimpleCORSFilter responds to all requests with certain Access-Control-* headers.
 * In this case, the headers are set to allow POST, GET, PUT, OPTIONS, or DELETE requests from clients originated from any host.
 * The results of a preflight request may be cached for up to 1 day. And the request may include an x-requested-with header.
 * based on https://spring.io/guides/gs/rest-service-cors/
 */
@Component
public class SimpleCORSFilter implements Filter {

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) res;
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
    response.setHeader("Access-Control-Max-Age", "86400");
    response.setHeader("Access-Control-Allow-Headers", "DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization");
    chain.doFilter(req, res);
  }

  public void init(FilterConfig filterConfig) {}

  public void destroy() {}

}