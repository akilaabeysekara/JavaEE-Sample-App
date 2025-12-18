package lk.ijse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
    import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/customer")
public class CustomerServlet extends HttpServlet {
    BasicDataSource ds;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ds = (BasicDataSource) servletContext
                .getAttribute("datasource");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = ds.getConnection();
            String query = "SELECT * FROM customer";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> customers = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> customer = new HashMap<>();
                customer.put("id", resultSet.getString("id"));
                customer.put("name", resultSet.getString("name"));
                customer.put("address", resultSet.getString("address"));
                customers.add(customer);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(customers);

            resp.setContentType("application/json");
            resp.getWriter().write(jsonResponse);

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Database error occurred");
            errorResponse.put("message", e.getMessage());

            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Gson gson = new Gson();
            JsonObject customer = gson.fromJson(request.getReader(), JsonObject.class);

            if (customer.get("cid") == null || customer.get("cname") == null || customer.get("caddress") == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                JsonObject errorJson = new JsonObject();
                errorJson.addProperty("status", "error");
                errorJson.addProperty("message", "Missing required fields");
                response.getWriter().write(errorJson.toString());
                return;
            }

            String id = customer.get("cid").getAsString();
            String name = customer.get("cname").getAsString();
            String address = customer.get("caddress").getAsString();

            Connection connection = ds.getConnection();
            String query = "INSERT INTO customer (id,name,address) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);

            response.setContentType("application/json");

            int rowInserted = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowInserted > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Customer saved successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Customer not saved");
            }
            response.getWriter().println(gson.toJson(responseJson));

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", e.getMessage());
            response.getWriter().write(errorJson.toString());
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Gson gson = new Gson();
            JsonObject customer = gson.fromJson(req.getReader(), JsonObject.class);
            String id = customer.get("cid").getAsString();
            String name = customer.get("cname").getAsString();
            String address = customer.get("caddress").getAsString();

            Connection connection = ds.getConnection();
            String query = "UPDATE customer SET name=?, address=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, id);

            resp.setContentType("application/json");

            int rowsUpdated = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowsUpdated > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Customer updated successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Customer not found");
            }
            resp.getWriter().write(responseJson.toString());

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", e.getMessage());
            resp.getWriter().write(errorJson.toString());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String id = req.getParameter("id");

            Connection connection = ds.getConnection();
            String query = "DELETE FROM customer WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);

            resp.setContentType("application/json");

            int rowsDeleted = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowsDeleted > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Customer deleted successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Customer not found");
            }
            resp.getWriter().write(responseJson.toString());

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", e.getMessage());
            resp.getWriter().write(errorJson.toString());
        }
    }
}