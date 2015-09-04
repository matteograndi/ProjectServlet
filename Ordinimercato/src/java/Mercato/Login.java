/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Mercato;

import java.io.*;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Statement;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author matteo grandi
 */
public class Login extends HttpServlet {

    String name;
    String uscita;
    String riga;
    int cont = 0;
    // Cookie cookie;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Cookie[] cookies = request.getCookies();
        Cookie[] cookies = request.getCookies();

        Cookie cookie;
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

        if (autenticato == 0) {
            riga = "";
            if (request.getParameter("nome") == null) { //qui entro la prima volta
                riga = "";
                /**
                 * La pagina visualizzata è sempre la stessa
                 * L'unica cosa che cambia è la visualizzazione del messaggio
                 * contenuto in "riga" e "riga1"
                 * che cambiano il valore a seconda della scelto che ho fatto
                 */
            } else if (request.getParameter("nome").equalsIgnoreCase("")) {
                /*
                 * Non ho compilato uno dei due campi in entrata 
                 */
                riga = "Immetti il tuo nome";
            } else if (request.getParameter("pwd").equalsIgnoreCase("")) {
                riga = "Immetti la password";
            } else {
                /*
                 * Ho compilato i campi
                 * Controllo di aver messo i campi giusti
                 */
                String nome = request.getParameter("nome");
                String pwd = request.getParameter("pwd");
                //datalog è una stringa che memorizza la data dell'utente loggato
                String datalog="";

                //devo metterla globale per evitare il problema di scrivere maiuscolo
                //o minuscolo perchè la EqualIgnoreCase me lo permette ma == no
                //la variabile pernde il valore del username della tabella utenti
                String username = "";

                try {
                    Class.forName("org.hsqldb.jdbcDriver");
                } catch (Exception e) {
                    System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
                }

                try {
                    Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                    Statement st = conn.createStatement();
                    String query = "select username, password, dataul from utenti";
                    ResultSet RScons;
                    RScons = st.executeQuery(query);

                    boolean trovato = false;

                    while ((RScons.next()) && !(trovato)) {
                        /**
                         * Controllo se nella tabella utenti sono presenti il nome
                         * e password che ho immesso
                         */
                        username = RScons.getString("username");
                        String p = RScons.getString("password");
                        datalog = RScons.getString("dataul");

                        if ((nome.equalsIgnoreCase(RScons.getString("username"))) && (pwd.equalsIgnoreCase(RScons.getString("password")))) {
                            trovato = true;
                        }
                    }

                    if (trovato) {
                        /**
                         * Qui vengo nel caso ho inserito i dati in maniera corretta
                         */
                        //HttpSession ses = request.getSession();
                        Cookie cooki = new Cookie("MERCATO", nome);//nome cookie, contenuto cookie
                        cooki.setMaxAge(-1); //a fine sessione il cookie muore
                        response.addCookie(cooki);
                        //creo un cookie anche per memorizzarmi il valore della data dell'utente
                        //registrato nella tabella
                        //mi serve per visualizzare la data nella pagina di welcome

                        Cookie dataul = new Cookie("DataLogin", datalog);
                        dataul.setMaxAge(-1);
                        response.addCookie(dataul);
          
                        riga = "NO";

                        uscita = "<HTML><HEAD><TITLE>Redirect...</TITLE>" +
                        "<META HTTP-EQUIV=\"REFRESH\" CONTENT=\"0; URL=/Ordinimercato/Welcome\">" +
                        "</HEAD><BODY>Redirect in corso...</BODY></HTML>";
                        /**
                         *  Modifico il campo data dell'utente con la data odierna
                         */
                        try {
                            Connection conn1 = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
                            Statement st1 = conn1.createStatement();
                            UsoData u = new UsoData();
                            //ricavo la data odierna
                            String d = u.DatainStringa();
                            query = "UPDATE utenti SET dataul = '" + d + "' WHERE username = '" + username + "'";
                            ResultSet RScons1 = st1.executeQuery(query);
                            RScons1.close();
                            st1.close();
                        } catch (Exception e) {
                            System.err.println(e);
                        }

                    } else {
                        /**
                         * Qui invece vengo se i dati inseriti non sono corretti
                         * con trovato=false
                         * praticamente faccio visualizzare il messaggio che i campi immessi
                         * non sono registrati nel database
                         */
                        riga = "Campo Username o Password inseriti errati. <p>";
                    }
                    RScons.close();
                    st.close();

                } catch (Exception e) {
                    System.err.println(e);
                    riga = "Il databese non è temporaneamente disponibile, prova più tardi<p>";
                }
            }

            if (!(riga.equalsIgnoreCase("NO"))) {
                //Se riga="NO" significa che il login è stato svolto correttamente
                //quindi non visualizzo più il contenuto html della servlet Login
                uscita = "<html> <head> " +
                        "<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">" +
                        "<title>LOGIN</title> </head> <body>" +
                        "<h1><center><font color=\"green\">SITO DI ORDINAZIONI ALIMENTARI</font></center></h1><p><p>" +
                        "<center><font color=\"red\">" + riga + "</font>" +
                        "<form method=\"get\" action=\"/Ordinimercato/Login\" name=\"cookie\" >" +
                        "Nome: &nbsp; <input name=\"nome\" size=\"20\" type=\"text\">" +
                        "   Password: &nbsp; <input name=\"pwd\" size=\"20\" type=\"password\"><p>" +
                        "<p><input value=\"Entra\" type=\"submit\">" +
                        "</center></form><hr>" +
                        "<form action=\"/Ordinimercato/CreaAcc\">" +
                        "Se invece sei un utente nuovo premi il bottone qui sotto<p>" +
                        "<input value=\"Crea Account\"  type=\"submit\">" +
                        "</form></body></html>";
            }

        } else if (autenticato == 1) {
            //ero autenticato scrivo i parametri
            RequestDispatcher rd = request.getRequestDispatcher("/Welcome");
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
