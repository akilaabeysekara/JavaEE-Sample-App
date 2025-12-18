package lk.ijse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/json")
public class JSONProcessingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject customer = new JsonObject();
        customer.addProperty("id","101");
        customer.addProperty("name","Akila Abeysekara");
        customer.addProperty("email","example@gmail.com");
        customer.addProperty("address","Kandy");
        customer.addProperty("age","25");
        customer.addProperty("contact","0723645746");

        JsonObject address1 = new JsonObject();
        address1.addProperty("no", "7/1");
        address1.addProperty("street", "Kandy Road");
        address1.addProperty("city","Kandy");
        address1.addProperty("zipcode","600001");

        JsonObject address2 = new JsonObject();
        address2.addProperty("no", "12/1");
        address2.addProperty("street", "Maharagama Road");
        address2.addProperty("city","Colombo");
        address2.addProperty("zipcode","000001");

        JsonArray address = new JsonArray();
        address.add(address1);
        address.add(address2);
        customer.add("address",address);
        resp.setContentType("application/json");
        resp.getWriter().write(customer.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject customer = gson.fromJson(req.getReader(),JsonObject.class);
//        for (String key : customer.keySet()) {
//            JsonElement value = customer.get(key);
//
//            if (value.isJsonNull()){
//                customer.remove(key);
//            }
//        }
        System.out.println(customer);
        System.out.println(customer.get("name").getAsString());
    }
}
