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
import java.util.ArrayList;

import java.sql.*;
import java.sql.Statement;

/**
 *
 * @author matteo grandi
 */
public class ConfermaOrdine extends HttpServlet {

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
        float totale = 0;

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

            if (request.getParameter("pulsante_premuto") == null) {

                HttpSession session = request.getSession();
                ArrayList InsOrdine = new ArrayList();
                session.setAttribute("InsOrdine", InsOrdine);

                /**
                 * E' una funzione per dire che ho premuto o no il tasto "SI"
                 */
                uscita = "<html><head>" +
                        "<script language=javascript>" +
                        "function continua()" +
                        "{" + "document.getElementById(\"pulsante_premuto\").value=\"fatto\";" +
                        "cookie.submit(); " + "}" +
                        "</script>" +

                        "<meta content=\"text/html; charset=ISO-8859-1\"http-equiv=\"content-type\">" +
                        "<title>Home Page</title></head><body>" +
                        "<form action=\"/Ordinimercato/Logout\">" +
                        "<input value=\"LOGOUT\" type=\"submit\"></form><p>" +
                        "<center><h2>QUESTA E' LA TUA LISTA ORDINI</h2><p><p>" +
                        "<table border=\"1\" cellpadding=\"2\" cellborder=\"2\"><tbody>" +
                        "<tr><td><h3>Merce</h3></td><td><h3>Prezzo</h3></td>" +
                        "<td></td><td><h3>Quantità</h3></td></tr>";

                try {
                    Class.forName("org.hsqldb.jdbcDriver");
                } catch (Exception e) {
                    System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
                }

                try {
                    String ordin = "INSERT INTO ORDINI VALUES(";
                    String ordin1 = ",'" + name + "',";

                    //creo la data in stringa
                    //questo è per acquisire la data
                    UsoData u = new UsoData();
                    String s = u.DataRestituzione();

                    Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                    Statement st = conn.createStatement();

                    String query = "select nome, prezzo, immagine from prodotti";
                    //String query = "insert into utenti values ('%user%','%pwd%','%nome%','%S. Antonio%','%304%','%h@yahoo.it%','%data?%')";

                    ResultSet RScons = st.executeQuery(query);

                    int indice = 0;
                    while (RScons.next()) {

                        String n = RScons.getString("nome");
                        String p = RScons.getString("prezzo");
                        String i = RScons.getString("immagine");
                        //nomeC è una stringa che uso per creare il nome dell'immagine
                        //corrispondente al prodotto utilizzato
                        String nomeC = "ing" + indice;
                        //rievo il valore corrispondente di quella merce ordinata
                        //N.B. può valere anche zero, in tal caso non viene visualizzata quella merce
                        String prova = request.getParameter(nomeC);
                        indice++;
                        if (!(prova.equalsIgnoreCase("0"))) {
                            uscita += "<tr><td>" + n + "</td><td>" + p + "</td>" +
                                    "<td><img alt=\"ciccio bomba\" src=\"img/" + i + "\"></td>" +
                                    "<td>  " + prova + "</td></tr>";

                            float app = Float.parseFloat(p) * Integer.parseInt(prova);
                            totale += app;

                            String comando = "INSERT INTO LISTEPROD VALUES(";
                            String comando1 = ",'" + n + "'," + prova + "," + app + ");";
//inserisco il comando spezzato in 2 parti nell'array
                            InsOrdine.add(comando);
                            InsOrdine.add(comando1);
                        }
                    }
                    ordin1 += totale + ",'" + s + "');";
                    InsOrdine.add(ordin);
                    InsOrdine.add(ordin1);

                    RScons.close();
                    st.close();
                } catch (Exception e) {
                    System.err.println(e);
                  }

                uscita += "</tbody></table><p><p><p>" +
                        "<h3><font color=\"purple\">TOTALE = " + totale + "</font></h3><p><p>" +
                        "Confermi la tua ordinazione? " +
                        "<form method=\"get\" action=\"/Ordinimercato/ConfermaOrdine\" name=\"cookie\">" +
                        "<input value=\"SI\" onclick=\"continua()\" type=\"submit\">" +
                        "<input type=\"hidden\" id=\"pulsante_premuto\" name=\"pulsante_premuto\" value=\"aaaa\">" +
                        "</form><p>" +
                        "<form action=\"/Ordinimercato/Welcome\">" +
                        "<input value=\"NO\" type=\"submit\"></form>" +
                        "</center></body></html>";
            } else {
                //Qui entro la seconda volta quando ho confermato la mia ordinazione
                //Praticamente inserisco i valori che ho appena creato

                HttpSession session = request.getSession();
                ArrayList InsOrdine = (ArrayList) session.getAttribute("InsOrdine");

                try {
                    Class.forName("org.hsqldb.jdbcDriver");
                } catch (Exception e) {
                    System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
                }

                try {
                    Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                    Statement st = conn.createStatement();
                    ResultSet RScons;
                    String query = "SELECT id_ordine FROM ordini";
                    RScons = st.executeQuery(query);


                    //scandisco tutte le righe per trovare il numero indice più alto
                    int n, np = 0;
                    
                    while (RScons.next()) {

                        n = Integer.parseInt(RScons.getString("id_ordine"));
                        if (n > np) {
                            np = n;
                        }
                    }
                    np++;

                    for (int i = 0; i < InsOrdine.size(); i++) {
                        String comando;
                        comando = (String) InsOrdine.get(i) + np;
                        i++;
                        /**
                         * Leggo dall'array 2 righe per volta e formo il comando
                         * di inserimento nelle tabelle
                         */
                                     comando += (String) InsOrdine.get(i);
                        RScons = st.executeQuery(comando);
                    }

                    RScons.close();
                } catch (Exception e) {
                    System.err.println(e);
                }

                uscita = "<HTML><HEAD><TITLE>Redirect...</TITLE>" +
                        "<META HTTP-EQUIV=\"REFRESH\" CONTENT=\"0; URL=/Ordinimercato/ListeOrdini\">" +
                        "</HEAD><BODY>Redirect in corso...</BODY></HTML>";
            }

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
