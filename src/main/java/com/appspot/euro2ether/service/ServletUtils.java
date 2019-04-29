package com.appspot.euro2ether.service;

import com.appspot.euro2ether.returns.HttpRequestWrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;

public class ServletUtils {

    /* ---- Logger: */
    private static final Logger LOG = Logger.getLogger(ServletUtils.class.getName());


    public static HashMap<String, String> requestHeadersToMap(HttpServletRequest request) {

        HashMap<String, String> headersMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String headerName = enumeration.nextElement();
                headersMap.put(headerName, request.getHeader(headerName));
            }
        }
        return headersMap;
    }

    public static HashMap<String, String> requestParametersToMap(HttpServletRequest request) {

        HashMap<String, String> parametersMap = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String parameterName = enumeration.nextElement();
                parametersMap.put(parameterName, request.getParameter(parameterName));
            }
        }
        return parametersMap;
    }

    public static HashMap<String, Cookie> requestCookiesToMap(HttpServletRequest request) {

        HashMap<String, Cookie> cookiesMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookiesMap.put(cookie.getName(), cookie);
            }
        }
        return cookiesMap;
    }

    public static HttpRequestWrapper getAllRequestData(HttpServletRequest request) {
        return new HttpRequestWrapper(
                ServletUtils.requestHeadersToMap(request),
                ServletUtils.requestCookiesToMap(request),
                ServletUtils.requestParametersToMap(request)
        );
    }

    public static void sendJsonResponse(HttpServletResponse response, final String jsonStr) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
        PrintWriter pw = response.getWriter(); //get the stream to write the data
        pw.println(jsonStr);
        pw.close(); //closing the stream

    } // end of sendJsonResponse()

    /* see:
     * https://stackoverflow.com/questions/4050087/how-to-obtain-the-last-path-segment-of-an-uri
     * */
    public static String getUrlKey(HttpServletRequest request) {

        // https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getRequestURI--
        String requestURI = request.getRequestURI();
        LOG.warning("request.getRequestURI(): " + request.getRequestURI());

        String urlKey = requestURI.substring(requestURI.lastIndexOf('/') + 1);

        return urlKey;
    }

}
