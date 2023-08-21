package ru.ptkom.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ptkom.service.ConfigurationFIleService;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private final static String BORDER = "---------------------------------------------------------------------" +
            "-------------------------------------------------------------------------";
    private Boolean isEnabled = false;

    private final ConfigurationFIleService configurationFIleService;

    public LoggingFilter(ConfigurationFIleService configurationFIleService) {
        this.configurationFIleService = configurationFIleService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        isEnabled = configurationFIleService.getSecurityDebug();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isEnabled) {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            String address = servletRequest.getLocalAddr();
            String cookies = "";
            if (httpRequest.getCookies() != null) {
                cookies = Arrays.stream(httpRequest.getCookies()).map(cookie -> String.format("%s - %s", cookie.getName(), cookie.getValue())).collect(Collectors.joining(System.lineSeparator()));
            }

            String headers = StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(httpRequest.getHeaderNames().asIterator(), Spliterator.ORDERED), false)
                    .map(header ->
                            String.format("%s: %s", header, httpRequest.getHeader(header)))
                    .collect(
                            Collectors.joining("\n")
                    );
            String body = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info(String.format("\n%s\nAddress: %s;\nHeaders: \n%s\nCookies:\n%sBody: %s\n%s", BORDER, address, headers, cookies, body, BORDER));
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
