package Model;

import Service.CardMatcherService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public enum CardSuiteTemplate {
    HEART("H"),
    HEART1("H"),
    DIAMOND("D"),
    SPADE("S"),
    CLUB("C");

    static {
        for (CardSuiteTemplate template : CardSuiteTemplate.values()) {
            try {
                BufferedImage templateImg = ImageIO.read(CardSuiteTemplate.class.getClassLoader().getResource("newTemplates/" + template.name().toLowerCase() + ".png").openStream());
                template.setMatchingPoints(CardMatcherService.matchingPointsExtractor(CardMatcherService.cropObjectFromImage(templateImg)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String suitName;
    private List<MatchingPoint> matchingPoints;

    CardSuiteTemplate() {
    }

    CardSuiteTemplate(String suitName, List<MatchingPoint> matchingPoints) {
        this.suitName = suitName;
        this.matchingPoints = matchingPoints;
    }

    CardSuiteTemplate(String suitName) {
        this.suitName = suitName;
    }

    public List<MatchingPoint> getMatchingPoints() {
        return matchingPoints;
    }

    public void setMatchingPoints(List<MatchingPoint> matchingPoints) {
        this.matchingPoints = (matchingPoints);
    }

    public String getSuitName() {
        return suitName;
    }

    public void setSuitName(String suitName) {
        this.suitName = suitName;
    }
}
