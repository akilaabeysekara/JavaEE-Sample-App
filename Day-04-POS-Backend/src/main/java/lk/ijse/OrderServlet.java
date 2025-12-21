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

@WebServlet("/api/v1/order")
public class OrderServlet extends HttpServlet {
    BasicDataSource ds;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ds = (BasicDataSource) servletContext
                .getAttribute("datasource");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        try {
            Connection connection = ds.getConnection();
            String query = "SELECT * FROM orders";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> orders = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> order = new HashMap<>();
                order.put("orderId", resultSet.getString("orderId"));
                order.put("customerId", resultSet.getString("customerId"));
                order.put("itemCode", resultSet.getString("itemCode"));
                order.put("qty", resultSet.getString("qty"));
                orders.add(order);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(orders);

            resp.setContentType("application/json");
            resp.getWriter().write(jsonResponse);

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
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            Gson gson = new Gson();
            JsonObject order = gson.fromJson(request.getReader(), JsonObject.class);

            if (order.get("orderId") == null || order.get("customerId") == null ||
                    order.get("itemCode") == null || order.get("qty") == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                JsonObject errorJson = new JsonObject();
                errorJson.addProperty("status", "error");
                errorJson.addProperty("message", "Missing required fields");
                response.getWriter().write(errorJson.toString());
                return;
            }

            String orderId = order.get("orderId").getAsString();
            String customerId = order.get("customerId").getAsString();
            String itemCode = order.get("itemCode").getAsString();
            String qty = order.get("qty").getAsString();

            Connection connection = ds.getConnection();
            String query = "INSERT INTO orders (orderId,customerId,itemCode,qty) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, orderId);
            preparedStatement.setString(2, customerId);
            preparedStatement.setString(3, itemCode);
            preparedStatement.setString(4, qty);

            response.setContentType("application/json");

            int rowInserted = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowInserted > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Order saved successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Order not saved");
            }
            response.getWriter().write(responseJson.toString());

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
        resp.setHeader("Access-Control-Allow-Origin", "*");
        try {
            Gson gson = new Gson();
            JsonObject order = gson.fromJson(req.getReader(), JsonObject.class);
            String orderId = order.get("orderId").getAsString();
            String customerId = order.get("customerId").getAsString();
            String itemCode = order.get("itemCode").getAsString();
            String qty = order.get("qty").getAsString();

            Connection connection = ds.getConnection();
            String query = "UPDATE orders SET customerId=?, itemCode=?, qty=? WHERE orderId=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customerId);
            preparedStatement.setString(2, itemCode);
            preparedStatement.setString(3, qty);
            preparedStatement.setString(4, orderId);

            resp.setContentType("application/json");

            int rowsUpdated = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowsUpdated > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Order updated successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Order not found");
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
        resp.setHeader("Access-Control-Allow-Origin", "*");
        try {
            String id = req.getParameter("id");

            Connection connection = ds.getConnection();
            String query = "DELETE FROM orders WHERE orderId=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);

            resp.setContentType("application/json");

            int rowsDeleted = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowsDeleted > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Order deleted successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Order not found");
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
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}