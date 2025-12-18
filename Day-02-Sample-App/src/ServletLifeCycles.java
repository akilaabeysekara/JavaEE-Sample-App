import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/slc")
public class ServletLifeCycles extends HttpServlet {
    @Override
    public void init() throws ServletException {
        System.out.println("Servlet Life Cycle Init");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("Servlet Life Cycle doGet");
    }

    @Override
    public void destroy() {
        System.out.println("Servlet Life Cycle Destroy");
    }







}
