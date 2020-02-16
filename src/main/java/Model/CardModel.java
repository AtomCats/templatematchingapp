package Model;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class CardModel {
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage image;
    private String value;
    private String cardSuite;
}
