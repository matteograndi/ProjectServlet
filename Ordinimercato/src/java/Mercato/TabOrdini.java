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
import java.sql.*;
import java.sql.Statement;

/**
 *
 * @author matteo grandi
 */
public class TabOrdini extends HttpServlet {

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

        Cookie[] cookies = request.getCookies();
        int autenticato = 0;

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                if (cookie.getName().equals("MERCATO")) {
                    autenticato = 1;
                }
            }
        }

        if (autenticato == 1) {
            uscita = "<html><head><meta content=\"text/html; charset=ISO-8859-1\"http-equiv=\"content-type\">" +
                    "<title>Home Page</title></head><body>" +
                    "<form action=\"/Ordinimercato/Logout\">" +
                    "<input value=\"LOGOUT\" type=\"submit\"></form><p>" +
                    "<center><h2>EFFETTUA LA TUA ORDINAZIONE</h2><p><p><p>" +
                    /*
                     * Visualizzo la tabella delle merci caricate nella tabella prodotti
                     */
                    "<form action=\"/Ordinimercato/ConfermaOrdine\">" +
                    "<table border=\"1\" cellpadding=\"2\" cellborder=\"2\"><tbody>" +
                    "<tr><h3><td>Merce</td><td>Descrizione</td>" +
                    "<td>Prezzo</td><td></td><td>Quantità</td></h3></tr>";

            try {
                Class.forName("org.hsqldb.jdbcDriver");
            } catch (Exception e) {
                System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            }
            try {
                Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                Statement st = conn.createStatement();
                //carico la tabella dove ho memorizzato la lista dei prodotti
                String query = "select nome, descrizione, prezzo, immagine from prodotti";

                ResultSet RScons;
                RScons = st.executeQuery(query);
                int indice = 0;
                while (RScons.next()) {

                    String nome = RScons.getString("nome");
                    String descrizione = RScons.getString("descrizione");
                    String prezzo = RScons.getString("prezzo");
                    String immagine = RScons.getString("immagine");
                    //nomeC serve per indicare il titolo dell'immagine da caricare
                    String nomeC = "ing" + indice;
                    indice++;
                    uscita += "<tr><td>" + nome + "</td><td>" + descrizione + "</td>" + "<td>" + prezzo + "</td>" +
                            "<td><img alt=\"ciccio bomba\" src=\"img/" + immagine + "\"></td> " +
                            "<td><select name=" + nomeC + ">" +
                            "<option value=\"0\">0</option>" +
                            "<option value=\"1\">1</option>" +
                            "<option value=\"2\">2</option>" +
                            "<option value=\"3\">3</option>" +
                            "<option value=\"4\">4</option>" +
                            "<option value=\"5\">5</option>" +
                            "<option value=\"10\">10</option>" +
                            "<option value=\"20\">20</option>" +
                            "</select>" +
                            "</td></tr>";
                }
                RScons.close();
                st.close();
            } catch (Exception e) {
                System.err.println(e);
            }

            uscita += "</tbody></table>" +
                    "<form action=\"/Ordinimercato/ConfermaOrdine\">" +
                    "<p><p><input value=\"Effettua la tua prenotazione\" type=\"submit\"></form><p>" +
                    "<form action=\"/Ordinimercato/Welcome\">" +
                    "Torna alla pagina principale cliccando " +
                    "<input value=\"QUI\" type=\"submit\"></form>" +
                    "</center></body></html>";

        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/Welcome");
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
            throws ServletException, IOException {
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
            throws ServletException, IOException {
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
