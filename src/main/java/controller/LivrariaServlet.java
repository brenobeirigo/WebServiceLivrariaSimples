/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.LivrariaDAOException;
import dao.LivrariaDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Livro;

/**
 *
 * @author BBEIRIGO
 */
public class LivrariaServlet extends HttpServlet {

    private static class ServletUtil {

        public static void writeJSON(HttpServletResponse response, String json) throws IOException {
            if (json != null) {
                try (PrintWriter writer = response.getWriter()) {
                    response.setContentType("application/json;charset=UTF-8");
                    writer.write(json);
                }
            }
        }
    }

    private static class RegexUtil {

        private static final Pattern regexAll = Pattern.compile("/livraria");
        private static final Pattern regexByIsbn = Pattern.compile("/livraria/isbn/([0-9]*)");
        private static final Pattern regexByName = Pattern.compile("/livraria/name/([a-zA-Z 0-9]*)");

        public static Long MatchIsbn(String requestUri) throws ServletException {
            Matcher matcher = regexByIsbn.matcher(requestUri);
            if (matcher.find() && matcher.groupCount() > 0) {
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        Long isbn = Long.parseLong(s);
                        return isbn;
                    }
                }
            }
            return null;
        }

        public static String MatchName(String requestUri) throws ServletException {

            Matcher matcher = regexByName.matcher(requestUri);

            if (matcher.find() && matcher.groupCount() > 0) {
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        String name = s;
                        return name;
                    }
                }
            }
            return null;
        }

        public static boolean MatchLivraria(String requestUri) throws ServletException {
            Matcher matcher = regexAll.matcher(requestUri);
            if (matcher.find() && matcher.end() == requestUri.length()) {
                return true;
            }
            return false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Decodifica a URI
        String requestUri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
        LivrariaDao a = new LivrariaDao();
        Long isbn = RegexUtil.MatchIsbn(requestUri);
        String name = RegexUtil.MatchName(requestUri);
        if (isbn != null) {
            try {
                Livro l = a.procurarLivro(isbn);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(l);
                ServletUtil.writeJSON(response, json);
            } catch (LivrariaDAOException ex) {
                Logger.getLogger(LivrariaServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (name != null) {
            try {
                Livro l = a.procurarLivro(name);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(l);
                ServletUtil.writeJSON(response, json);
            } catch (LivrariaDAOException ex) {
                Logger.getLogger(LivrariaServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (RegexUtil.MatchLivraria(requestUri)) {
            try {
                List<Livro> livros = a.todosLivros();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(livros);
                ServletUtil.writeJSON(response, json);
            } catch (LivrariaDAOException ex) {
                Logger.getLogger(LivrariaServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String edicao = request.getParameter("edicao");
        String publicacao = request.getParameter("publicacao");
        String descricao = request.getParameter("descricao");
        System.out.println(isbn + "  " + titulo + "  " + edicao + "  " + publicacao + "  " + descricao);
        LivrariaDao dao = new LivrariaDao();
        try {
            dao.salvar(new Livro(Long.valueOf(isbn), titulo, Integer.valueOf(edicao), Integer.valueOf(publicacao), descricao));
        } catch (LivrariaDAOException ex) {
            Logger.getLogger(LivrariaServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
