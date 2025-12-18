package lk.ijse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class CrosFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods", "DELETE, PUT, GET, POST, OPTIONS");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.addHeader("Access-Control-Expose-Headers", "Content-Type");
        resp.addHeader("Access-Control-Max-Age", "3600");

        if (req.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}