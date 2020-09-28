package com.lhfeiyu.tech.filter;

/**
 * https://www.javazhiyin.com/45056.html
 */
/*@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
@Configuration
public class CorsFilterBak implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        //String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", "*");
        //https://juejin.im/post/5c2490ba6fb9a049ff4e2eca
        //The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'. The credentials mode of requests initiated by the XMLHttpRequest is controlled by the withCredentials attribute.
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        chain.doFilter(req, res);
    }

}*/
