package dk.goombah.backend;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageTools {

    public static InputStream smallifyImage(MultipartFile source, int width, int height)
    {
        try {
            Image image = ImageIO.read(source.getInputStream()).getScaledInstance(width, height, Image.SCALE_DEFAULT);;

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, null);
            graphics2D.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "JPG", baos);

            return new ByteArrayInputStream(baos.toByteArray());

        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
