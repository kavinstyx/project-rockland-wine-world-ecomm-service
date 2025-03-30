package com.commonlibrary.logfilter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.util.*;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private  final  String body;
    private final Map<String, List<String>> customHeaders = new HashMap<>();


    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        //read the body of the request
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine())!= null) {
            stringBuilder.append(line);
        }
        body = stringBuilder.toString();
        reader.close();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final  ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setReadListener(ReadListener listener) {
                throw new RuntimeException("Not implemented");
            }

        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBody(){
        return this.body;
    }

    @Override
    public String getHeader(String name) {
        List<String> values = customHeaders.get(name);
        if (values != null && !values.isEmpty())
        {
            return values.get(0);   //return the first value
        }
        return super.getHeader(name);
    }

    public void setHeader(String name, String value )
    {
        customHeaders.computeIfAbsent(name, k -> new ArrayList<>()).clear();
        customHeaders.get(name).add(value);
    }


}
