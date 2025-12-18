import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    private static final List<Customer> customers = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        if (id == null || id.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customerDB",
                    "root", "Ijse@1234");
            String query = "INSERT INTO customer(id,name,address) VALUES(?,?,?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, address);
            int rowInserted = statement.executeUpdate();
            if (rowInserted == 0) throw new SQLException("Error in inserting data");

            Customer customer = new Customer(id, name, address);
            customers.add(customer);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().println(customer.toJson());

        } catch (ClassNotFoundException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customerDB",
                    "root", "Ijse@1234");
            String query = "SELECT * FROM customer";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            boolean first = true;
            while (resultSet.next()) {
                if (!first) sb.append(',');
                Customer c = new Customer(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("address")
                );
                sb.append(c.toJson());
                first = false;
            }
            sb.append(']');
            resp.getWriter().println(sb.toString());

        } catch (ClassNotFoundException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // UPDATE
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/customerDB",
                    "root","Ijse@1234");
            String query="update customer set name=?,address=? where id=?";
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,address);
            preparedStatement.setString(3,id);
            int rowInserted=preparedStatement.executeUpdate();
            if (rowInserted>0){
                resp.getWriter().println("Customer updated successfully");
            }else {
                resp.getWriter().println("Customer not updated");
            }
            connection.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/customerDB",
                    "root", "Ijse@1234");
            String query = "DELETE FROM customer WHERE id=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, id);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                synchronized (customers) {
                    Iterator<Customer> iterator = customers.iterator();
                    while (iterator.hasNext()) {
                        Customer customer = iterator.next();
                        if (customer.getId().equals(id)) {
                            iterator.remove();
                            break;
                        }
                    }
                }
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (ClassNotFoundException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}