import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Application {

    public static void main(String[] args) {

        File source = new File(args[0]);
        if(source.exists() && source.isFile() && source.canRead()) {
            try {
                BufferedImage img = ImageIO.read(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
