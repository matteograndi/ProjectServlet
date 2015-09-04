/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Mercato;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author matteo grandi
 */
public class Welcome extends HttpServlet {

    String name, dat;
    String uscita;
    Cookie cookie;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response.setContentType("text/html;charset=UTF-8");


        Cookie[] cookies = request.getCookies();


        int autenticato = 0;

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                if (cookie.getName().equals("MERCATO")) {
                    autenticato = 1;
                    name = cookie.getValue();
                }
            }
        }

        if (autenticato == 1) {
            /*
             * Siccome i cookie "Mercato" e "DataLogin" sono stati creati assieme
             * se è presente uno, è presente anche l'altro
             * Da qui ricavo anche la data dell'ultimo login effettuato
             */
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                if (cookie.getName().equals("DataLogin")) {
                    dat = cookie.getValue();
                }

            }

            UsoData u = new UsoData();
            uscita = "<html><head><meta content=\"text/html; charset=ISO-8859-1\"http-equiv=\"content-type\">" +
                    "<title>Home Page</title></head><body>" +
                    "<form action=\"/Ordinimercato/Logout\">" +
                    "<input value=\"LOGOUT\" type=\"submit\"></form><p>" +
                    "<center><font color=\"green\"><h1>Home Page di " +
                    name + "</h1></font><p><font color=\"orange\"><h3>";
            /*
             * DEVO FARE IL CONTROLLO DELLA DATA
             * se ha un certo valore di una data già passata (12/07/2003) allora vuol dire che
             * è la prima volta che l'utente si loga nel sito
             */
            if (dat.equalsIgnoreCase("12/7/2003")) {
                uscita += "Questa è la prima volta che ti connetti";
            } else {
                uscita += "Data ultimo accesso: " + dat;
            }

            uscita += "</h3></font></h1><p><p>" +
                    "Se vuoi effettuare un'ordinazione clicca qui sotto<p>" +
                    "<form action=\"/Ordinimercato/TabOrdini\">" +
                    "<input value=\"effettuare un'ordinazione\" type=\"submit\"></form><p>" +
                    "<form action=\"/Ordinimercato/ListeOrdini\">" +
                    "Se vuoi controllare la tua lista degli ordini clicca<p>" +
                    "<input value=\"Controlla la tua lista ordini\" type=\"submit\"></form>" +
                    "</center></body></html>";

        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/Login");
            rd.forward(request, response);
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(uscita);
        out.close();

    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException,
            IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    }
