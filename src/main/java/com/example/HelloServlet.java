package com.example;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter()
            .append("<!DOCTYPE html>")
            .append("<html><head><title>Hello</title></head><body>")
            .append("<h1>Hello, World!</h1>")
            .append("<p>Built on " + java.time.LocalDate.now() + "</p>")
            .append("</body></html>");
    }
}
