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

@WebServlet("/api/v1/item")
public class ItemServlet extends HttpServlet {
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
            String query = "SELECT * FROM item";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> items = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> item = new HashMap<>();
                item.put("code", resultSet.getString("code"));
                item.put("name", resultSet.getString("name"));
                item.put("qty", resultSet.getString("qty"));
                item.put("price", resultSet.getString("price"));
                items.add(item);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(items);

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
            JsonObject item = gson.fromJson(request.getReader(), JsonObject.class);

            if (item.get("code") == null || item.get("name") == null || item.get("qty") == null || item.get("price") == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                JsonObject errorJson = new JsonObject();
                errorJson.addProperty("status", "error");
                errorJson.addProperty("message", "Missing required fields");
                response.getWriter().write(errorJson.toString());
                return;
            }

            String code = item.get("code").getAsString();
            String name = item.get("name").getAsString();
            int qty = item.get("qty").getAsInt();
            double price = item.get("price").getAsDouble();

            Connection connection = ds.getConnection();
            String query = "INSERT INTO item (code,name,qty,price) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, qty);
            preparedStatement.setDouble(4, price);

            response.setContentType("application/json");

            int rowInserted = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowInserted > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Item saved successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Item not saved");
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
            JsonObject item = gson.fromJson(req.getReader(), JsonObject.class);
            String code = item.get("code").getAsString();
            String name = item.get("name").getAsString();
            int qty = item.get("qty").getAsInt();
            double price = item.get("price").getAsDouble();

            Connection connection = ds.getConnection();
            String query = "UPDATE item SET name=?, qty=?, price=? WHERE code=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, qty);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, code);

            resp.setContentType("application/json");

            int rowsUpdated = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowsUpdated > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Item updated successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Item not found");
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
            String code = req.getParameter("code");

            Connection connection = ds.getConnection();
            String query = "DELETE FROM item WHERE code=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, code);

            resp.setContentType("application/json");

            int rowsDeleted = preparedStatement.executeUpdate();
            JsonObject responseJson = new JsonObject();
            if (rowsDeleted > 0) {
                responseJson.addProperty("status", "success");
                responseJson.addProperty("message", "Item deleted successfully");
            } else {
                responseJson.addProperty("status", "error");
                responseJson.addProperty("message", "Item not found");
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