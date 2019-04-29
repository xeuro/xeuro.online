package com.appspot.euro2ether.servlets;

import com.appspot.euro2ether.service.BitcoinusService;
import com.appspot.euro2ether.service.ServletUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

@WebServlet(name = "EuroBalanceServlet")
public class EuroBalanceServlet extends HttpServlet {


    private static final Logger LOG = Logger.getLogger(EuroBalanceServlet.class.getName());
    private static final Gson GSON = new Gson();

    /*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
*/

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            HashMap<String, Double> balance = BitcoinusService.getBalance("EUR");
            ServletUtils.sendJsonResponse(response, GSON.toJson(balance));

        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new IOException("ERROR");
        }

    }

}
