package dao;

/**
 *
 * @author BBEIRIGO
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Livro;
import util.ConnectionFactory;

public class LivrariaDao implements InterfaceLivrosDAO {

    private String server;
    private String port;
    private String db;
    private String user;
    private String password;

    private Connection geraConexao() throws LivrariaDAOException {
        Connection conn;
        try {
            conn = ConnectionFactory.getConnection(server, port, db, user, password);
        } catch (Exception e) {
            throw new LivrariaDAOException("Falha na conexão.", e);
        }
        return conn;
    }

    public LivrariaDao() {
        this.server = "localhost";
        this.port = "3306";
        this.db = "livraria";
        this.user = "root";
        this.password = "123456";
    }

    public LivrariaDao(String server, String port, String bd, String user, String password) {
        this.server = server;
        this.port = port;
        this.db = bd;
        this.user = user;
        this.password = password;
    }

    @Override
    public void salvar(Livro livro) throws LivrariaDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        if (livro == null) {
            throw new LivrariaDAOException("O valor passado não pode ser nulo.");
        }

        try {
            String SQL = "INSERT INTO livro (isbn, titulo, edicao, ano_publicacao, descricao) values (?, ?, ?, ?, ?)";
            conn = geraConexao();
            ps = conn.prepareStatement(SQL);
            ps.setLong(1, livro.getIsbn());
            ps.setString(2, livro.getTitulo());
            ps.setInt(3, livro.getEdicao());
            ps.setInt(4, livro.getPublicacao());
            ps.setString(5, livro.getDescricao());
            /*Caso seja necessário trabalhar com datas ou timestamps utilizar setDate e setTime em conjunto com o Calendar. Exemplo:
            //Cria objeto calendar
            Calendar c = Calendar.getInstance();
            //Armazena data
            c.set(2016, 01, 01, 23, 45, 0);
            //Altera statement
            ps.setTime(x, c.getTime());
             */
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new LivrariaDAOException("Erro ao inserir dados: \"" + livro.getIsbn() + "\", \"" + livro.getTitulo() + "\", \"" + livro.getEdicao() + "\", \"" + livro.getPublicacao() + "\", \"" + livro.getDescricao() + "\".", sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
    }

    @Override
    public void excluir(Livro livro) throws LivrariaDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        if (livro == null) {
            throw new LivrariaDAOException("O valor passado não pode ser nulo.");
        }
        try {
            conn = geraConexao();
            ps = conn.prepareStatement("delete from livro where isbn=?");
            ps.setLong(1, livro.getIsbn());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new LivrariaDAOException("Erro ao excluir livro de ISBN = " + livro.getIsbn() + ".", sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
    }

    @Override
    public void atualizar(Livro livro) throws LivrariaDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        if (livro == null) {
            throw new LivrariaDAOException("O valor passado não pode ser nulo.");
        }
        try {
            String SQL = "UPDATE livro SET titulo=?, edicao=?, ano_publicacao=?, descricao=? where isbn=?";
            conn = geraConexao();
            ps = conn.prepareStatement(SQL);
            ps.setString(1, livro.getTitulo());
            ps.setInt(2, livro.getEdicao());
            ps.setInt(3, livro.getPublicacao());
            ps.setString(4, livro.getDescricao());
            ps.setLong(5, livro.getIsbn());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new LivrariaDAOException("Erro ao atualizar dados do livro com ISBN = " + livro.getIsbn() + ".", sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
    }

    public List<Livro> todosLivros() throws LivrariaDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = geraConexao();
            ps = conn.prepareStatement("select * from livro");
            rs = ps.executeQuery();
            List<Livro> list = new ArrayList<Livro>();
            while (rs.next()) {
                int isbn = rs.getInt(1);
                String titulo = rs.getString(2);
                int edicao = rs.getInt(3);
                int publicacao = rs.getInt(4);
                String descricao = rs.getString(5);
                list.add(new Livro(isbn, titulo, edicao, publicacao, descricao));
            }
            return list;
        } catch (SQLException sqle) {
            throw new LivrariaDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
    }

    public Livro procurarLivro(long isbn) throws LivrariaDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = geraConexao();
            ps = conn.prepareStatement("select * from livro where isbn =?");
            ps.setLong(1, isbn);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new LivrariaDAOException("Não foi encontrado nenhum registro com o ISBN: " + isbn + ".");
            }
            String titulo = rs.getString(2);
            int edicao = rs.getInt(3);
            int publicacao = rs.getInt(4);
            String descricao = rs.getString(5);
            return new Livro(isbn, titulo, edicao, publicacao, descricao);
        } catch (SQLException sqle) {
            throw new LivrariaDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
    }
    
    @Override
     public Livro procurarLivro(String dado) throws LivrariaDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = geraConexao();
            ps = conn.prepareStatement("select * from livro where titulo like ?");
            ps.setString(1, "%"+dado+"%");
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new LivrariaDAOException("Não foi encontrado nenhum registro com a busca: " + dado + ".");
            }
            long isbn = rs.getLong(1);
            String titulo = rs.getString(2);
            int edicao = rs.getInt(3);
            int publicacao = rs.getInt(4);
            String descricao = rs.getString(5);
            return new Livro(isbn, titulo, edicao, publicacao, descricao);
        } catch (SQLException sqle) {
            throw new LivrariaDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
    }
}
