package com.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloServletTest {

    @Test
    public void testDoGetOutputsHelloWorld() throws Exception {
        // Mock request and response
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // Capture output
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        // Call the servlet
        HelloServlet servlet = new HelloServlet();
        servlet.doGet(request, response);

        writer.flush(); 

        // Assert that output contains "Hello, World!"
        String output = stringWriter.toString();
        assertTrue(output.contains("Hello, World!"));
    }
}
