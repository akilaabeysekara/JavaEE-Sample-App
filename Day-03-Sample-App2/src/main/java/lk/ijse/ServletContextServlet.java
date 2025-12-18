package lk.ijse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

@WebListener
public class ServletContextServlet implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/customerDB");
        ds.setUsername("root");
        ds.setPassword("Ijse@1234");
        ds.setMaxTotal(50);
        ds.setMaxIdle(100);
        // Set the datasource as a ServletContext attribute
        servletContext.setAttribute("datasource", ds);
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce){
        System.out.println("ServletContext Servlet Destroyed");
    }
}
