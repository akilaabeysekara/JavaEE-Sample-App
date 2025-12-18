package lk.ijse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

@WebServlet(urlPatterns = "/data-formats")
@MultipartConfig(
//        maxFileSize = 1024*1024*10,
//        maxRequestSize = 1024*1024*15
)
public class DataFormatsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String contentType = req.getContentType();

        System.out.println("contentType: "+ contentType);

        //application/x-www-form-urlencoded
//        System.out.println("ID Value: "+req.getParameter("id"));
//        System.out.println("Name Value: "+req.getParameter("name"));

        //multipart/form-data
        System.out.println("ID Value: "+req.getParameter("id"));
        System.out.println("Name Value: "+req.getParameter("name"));
        // 1 read the file
        Part filePart = req.getPart("image");
        System.out.println("Image Value: " + filePart.getSubmittedFileName());
        // 2 create a directory
        File uploadDir=new File("/home/akila/IJSE/InClassWorks/AAD/JavaEE/JavaEE-Tomcat/Day-03-Sample-App2/src/main/resources/images");
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }
        //3 save the file
        //filePart.write(uploadDir.getAbsolutePath()+"/"+filePart.getSubmittedFileName()); // this is also possible
        String fullPath=uploadDir.getAbsolutePath()
                +File.separator
                +filePart.getSubmittedFileName();
        filePart.write(fullPath);

    }
}
