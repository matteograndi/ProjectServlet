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
public class ListeOrdini extends HttpServlet {

    String name;
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
                    name = cookie.getValue();
                }
            }
        }

        if (autenticato == 1) {
            /**
             * Faccio la scansione degli ordini della tabella ordini
             * Praticamente è qui che fccio il controllo di cancellare gli ordini effettuati
             * nelle vecchie date
             */
            try {
                Class.forName("org.hsqldb.jdbcDriver");
            } catch (Exception e) {
                System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            }
            try {
                Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                Statement st = conn.createStatement();

                String query = "select id_ordine,dataor from ordini";
                ResultSet RScons;
                ResultSet RScons1;
                RScons = st.executeQuery(query);

                while (RScons.next()) {

                    UsoData u = new UsoData();
                    int ris = u.confrontaStringa(RScons.getString("dataor"));
                    /**
                     * Se il risultato di quella chiamata alla funzione è uguale a zero 
                     * vuol dire che la data è vecchia e quindi devo cancellare le rispettive 
                     * righe di quell'ordine dalle tabelle ordini e listeprod
                     */
                    if (ris == 0) {
                        query = "DELETE FROM listeprod WHERE id_ordine=" + RScons.getString("id_ordine");
                        String query1 = "DELETE FROM ordini WHERE id_ordine=" + RScons.getString("id_ordine");

                        try {
                            Connection conn1 = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                            Statement st1 = conn1.createStatement();
                            RScons1 = st1.executeQuery(query);
                            RScons1 = st1.executeQuery(query1);
                            RScons1.close();
                            st1.close();
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                    }
                }
                RScons.close();
                st.close();

            } catch (Exception e) {
                System.err.println(e);
            }

            /**
             * QUI COMINCIA LA PARTE DI VISUALIZZAZIONE PAGINA
             */
            uscita = "<html><head><meta content=\"text/html; charset=ISO-8859-1\"http-equiv=\"content-type\">" +
                    "<title>Home Page</title></head><body>" +
                    "<form action=\"/Ordinimercato/Logout\">" +
                    "<input value=\"LOGOUT\" type=\"submit\"></form><p>" +
                    "<center><h2>ECCO LE TUE LISTE ORDINI</h2><p><p>";

            try {
                Class.forName("org.hsqldb.jdbcDriver");
            } catch (Exception e) {
                System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            }
            try {
                Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                Statement st = conn.createStatement();

                String query = "select id_ordine,prezzo, dataor from ordini WHERE utente LIKE '" + name + "'";
                ResultSet RScons;
                ResultSet RScons1;
                /**
                 * Ho gli ordini eseguiti dall'utente loggato
                 */
                RScons = st.executeQuery(query);

                while (RScons.next()) {
                    String ordine = RScons.getString("id_ordine");
                    uscita += "<p><p><h3><font color=\"green\">Da ritirare il giorno " + RScons.getString("dataor") + "</font></h3><p>";

                    query = "select prodotto,quantita,costo FROM listeprod WHERE id_ordine=" + ordine;
                    try {
                        Connection conn1 = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                        Statement st1 = conn1.createStatement();
                        /**
                         * Per ogni singolo numero d'ordine visualizzo la sua merce
                         */
                        RScons1 = st1.executeQuery(query);
                        uscita += "<table border=\"1\" cellpadding=\"2\" cellborder=\"2\"><tbody>" +
                                "<tr><td><h3>Merce</h3></td><td><h4>Quantita<p>ordinata</h4></td>" +
                                "<td><h3>Prezzo</h3></td></tr>";

                        while (RScons1.next()) {
                            uscita += "<tr><td>" + RScons1.getString("prodotto") + "</td><td>" + RScons1.getString("quantita") +
                                    "</td><td>" + RScons1.getString("costo") + "</td></tr><p></p></p>";
                        }
                        uscita += "<tr><td></td><td></td><td><font color=\"blue\">" + RScons.getString("prezzo") + "</font></td></tr>" +
                                "</tbody></table>";
                        RScons1.close();
                        st1.close();

                    } catch (Exception e) {
                    }
                }

                RScons.close();
                st.close();
            } catch (Exception e) {
                System.err.println(e);
            }

            uscita += "<form action=\"/Ordinimercato/Welcome\">" +
                    "<input value=\"Torna alla pagina principale\" type=\"submit\"></form>" +
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
