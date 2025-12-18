package lk.ijse;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/api/v1/customer")
public class DBCPServlet extends HttpServlet {
    BasicDataSource ds;
    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ds = (BasicDataSource) servletContext.getAttribute("datasource");
    }

    // CREATE
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO customer(id,name,address) VALUES (?,?,?)")) {

            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            int rowInserted = preparedStatement.executeUpdate();

            if (rowInserted > 0) {
                resp.getWriter().println("Customer saved successfully");
            } else {
                resp.getWriter().println("Customer not saved");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // READ
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String id = req.getParameter("id");

        try (Connection connection = ds.getConnection()) {
            String query = (id == null) ?
                    "SELECT * FROM customer" :
                    "SELECT * FROM customer WHERE id=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                if (id != null) {
                    preparedStatement.setString(1, id);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String cusId = resultSet.getString("id");
                        String name = resultSet.getString("name");
                        String address = resultSet.getString("address");
                        resp.getWriter().println(cusId + "," + name + "," + address);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // UPDATE 
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "update customer set name=?,address=? where id=?")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, id);
            int rowUpdated = preparedStatement.executeUpdate();

            if (rowUpdated > 0) {
                resp.getWriter().println("Customer updated successfully");
            } else {
                resp.getWriter().println("Customer not updated");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        try (Connection connection = ds.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "delete from customer where id=?")) {

            preparedStatement.setString(1, id);
            int rowDeleted = preparedStatement.executeUpdate();

            if (rowDeleted > 0) {
                resp.getWriter().println("Customer deleted successfully");
            } else {
                resp.getWriter().println("Customer not deleted");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}