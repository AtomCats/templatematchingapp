import Service.CardMatcherService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Application {

    public static void main(String[] args) {

        //File source = new File(args[0]);
        File source = new File("C:\\Users\\Admin\\Desktop\\AibekAbykeev's_test_task\\AibekAbykeev's_test_task\\images\\");
        if(source.exists() && source.isDirectory() && source.canRead()) {
            int counter = 1;
            for (File imgFile : source.listFiles()) {
                try {
                    System.out.println(CardMatcherService.matchCardsWithTemplates(imgFile, counter));
                    counter ++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
