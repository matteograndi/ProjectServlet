/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Mercato;

import java.io.*;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.*;
import javax.sql.*;
import java.sql.*;

import java.sql.Statement;
//import javax.servlet.RequestDispatcher;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author matteo grandi
 */
public class CreaAcc extends HttpServlet {

    String name, uscita;
    //nomeus è una variabile booleana che mi serve per indicare di continuare i
    //controlli o no dopo il comtrollo del campo username
    //boolean nomeus = false;
    boolean continua = false;

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

        Cookie cookie;
        int autenticato = 0;
        int errore = 0;
        String riga = "";

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                if (cookie.getName().equals("MERCATO")) {
                    autenticato = 1;
                    name = cookie.getValue();
                }
            }
        }
//imposto già il comando di andare a capo alla riga
        riga = "<p>";


        if (autenticato == 0) {

            if (request.getParameter("nomec") == null) {
                errore = 1;
            } else if (request.getParameter("nomec").equalsIgnoreCase("")) {
                riga += "Non hai compilato il campo Nome<p>";
            } else if (request.getParameter("usrc").equalsIgnoreCase("")) {
                riga += "Non hai compilato il campo Username<p>";

            } else {
                try {
                    Class.forName("org.hsqldb.jdbcDriver");

                } catch (Exception e) {
                    System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
                }
                Connection conn;
                boolean trovato = false;
                try {
                    conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                    Statement st = conn.createStatement();

                    String query = "select username from utenti";
                    ResultSet RScons;
                    RScons = st.executeQuery(query);

                    while ((RScons.next() && (!(trovato)))) {
                        String app = RScons.getString("username");
                        // riga += app + "   ---   " + request.getParameter("usrc") + "<p>";
                        if (app.equalsIgnoreCase(request.getParameter("usrc"))) {
                            trovato = true;

                        }
                    }

                    RScons.close();

                    if (trovato) {
                        //significa che è già presenta un utente con quell'username
                        //ne cerco una da comsigliare
                        int numero = 1;
                        trovato = true;
                        while (trovato) {

                            //riga += "<h1>SEI IN NON TROVATO</h1><p>";
                            riga = "<p>Mi spiace ma l'username immesso è già stato inserito<p>";
                            //Cerca è una stringa che fa un controllo del database per trovare un
                            //'username'+n° che non è presente nel database 
                            String cerca = request.getParameter("usrc") + String.valueOf(numero);
                            numero++;


                            //riga += "<p>" + cerca + "<p>";

                            RScons = st.executeQuery(query);
                            //prova per variabile trovato
                            trovato = false;

                            while ((RScons.next() && (!(trovato)))) {
                                String app = RScons.getString("username");
                                //riga += app + "   ---   " + cerca + "<p>";
                                if (app.equalsIgnoreCase(cerca)) {
                                    trovato = true;

                                }
                            }//fine while interno
                            RScons.close();
                            riga += "<font color=\"blue\">Ti consigliamo di iscriverti con il nome " + cerca + "<p></font>";
                        }//fine while esterno che cerca una stringa da consigliare all'utente x logarsi
                    } else {
                        //riga += "<h1>HAI INSERITO UN USERNAME NUOVO</h1>";
                        continua = true;
                    }
                    st.close();
                } catch (Exception e) {
                    System.err.println(e);
                    riga += "C'è stato un errore di connessione al database<p>";
                    riga += e;
                }


            }
            //Questi controlli ramificati fuori dai primi due controlli
            //li ho fatti solo per fare in modo di eseguirli solo se ho inserito uno
            //username utente che non è uguale agli altri

            if (continua) {
                //riga = "QUI ENTRO SOLO X FARE ALTRI CONTROLLI<p>";
                if (request.getParameter("pwdc").equalsIgnoreCase("")) {
                    riga += "Non hai compilato il campo Password<p>";
                } else if (request.getParameter("indc").equalsIgnoreCase("")) {
                    riga += "Non hai compilato il campo Indirizzo<p>";
                } else if (request.getParameter("telc").equalsIgnoreCase("")) {
                    riga += "Non hai compilato il campo Numero telefonico<p>";
                } else if (request.getParameter("mailc").equalsIgnoreCase("")) {
                    riga += "Non hai compilato il campo Mail<p>";
                } else {
                    //riga += "entro in inserisci<p>";
                    try {
                        Class.forName("org.hsqldb.jdbcDriver");

                    } catch (Exception e) {
                        System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
                    }

      UsoData u=new UsoData();
String d1=u.DatainStringa();



Connection conn;

                    try {


                        conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                        Statement st = conn.createStatement();

                        String query = "INSERT INTO utenti VALUES ('" +
                                request.getParameter("usrc") + "','" +
                                request.getParameter("pwdc") + "','" +
                                request.getParameter("nomec") + "','" +
                                request.getParameter("indc") + "','" +
                                request.getParameter("telc") + "','" +
                                request.getParameter("mailc") + "','" +
                                d1+"')";

                        //"insert into utenti values ('%user%','%pwd%','%nome%','%S. Antonio%','%304%','%h@yahoo.it%','%data?%')";






                        ResultSet RScons;
                        RScons = st.executeQuery(query);
                        riga += "<h2><font color=\"magenta\">Ti ho inserito</font></h2><p>";
                        RScons.close();
                        st.close();
                    } catch (Exception e) {
                        riga = "errore durante l'inserimento delle tue informazioni." +
                                "<p>Per favore ritenta";
                    }
                }

            }




            // }

            uscita = "<html><head>" +
                    "<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">" +
                    "<title>Crea Account</title></head><body><center>" +
                    "<h3><font color=\"orage\">PAGINA DI CREAACCOUNT</font></h3>" +
                    "<font color=\"red\">" + riga + "</font>" +
                    "<form method=\"get\" action=\"/Ordinimercato/CreaAcc\" name=\"cookie\" >" +
                    "<table>" +
                    "<tr><td>Nome: </td><td><input name=\"nomec\" size=\"20\" type=\"text\"  ";
            if (errore == 0) {
                String campo = request.getParameter("nomec");
                uscita += "value =" + campo;
            }
            uscita += "></td></tr><tr><td>Username: </td><td><input name=\"usrc\" size=\"20\" type=\"text\"";
            if (errore == 0) {
                String campo = request.getParameter("usrc");
                uscita += "value =" + campo;
            }
            uscita += "></td></tr><tr><td>Password: </td><td><input name=\"pwdc\" size=\"20\" type=\"text\"";
            if (errore == 0) {
                String campo = request.getParameter("pwdc");
                uscita += "value =" + campo;
            }
            uscita += "></td></tr><tr><td>Indirizzo: </td><td><input name=\"indc\" size=\"20\" type=\"text\"";
            if (errore == 0) {
                String campo = request.getParameter("indc");
                uscita += "value =" + campo;
            }
            uscita += "></td></tr><tr><td>Numero telefonico: </td><td><input name=\"telc\" size=\"20\" type=\"text\"";
            if (errore == 0) {
                String campo = request.getParameter("telc");
                uscita += "value =" + campo;
            }
            uscita += "></td></tr><tr><td>Mail: </td><td><input name=\"mailc\" size=\"20\" type=\"text\"";
            if (errore == 0) {
                String campo = request.getParameter("mailc");
                uscita += "value =" + campo;
            }
            uscita += "></table><p><input value=\"Crea Account\" type=\"submit\"></form>" +
                    "<form action=\"/Ordinimercato/Login\">" +
                    "<input value=\"TORNA AL MENU PRINCPALE\" type=\"submit\"></form><p>" +
                    "</center></body></html>";

        } else if (autenticato == 1) {
            //ero autenticato scrivo i parametri
            RequestDispatcher rd = request.getRequestDispatcher("/Login");
            rd.forward(request, response);
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(uscita);
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
