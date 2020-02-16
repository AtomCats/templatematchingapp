package Model;

import Service.CardMatcherService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public enum CardsTemplate {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("Jack"),
    QUEEN("Queen"),
    KING("King"),
    ACE("Ace");

    private String cardName;
    private List<MatchingPoint> matchingPoints;

    CardsTemplate() { }

    CardsTemplate(String cardName, List<MatchingPoint> matchingPoints) {
        this.cardName = cardName;
        this.matchingPoints.addAll(matchingPoints);
    }

    CardsTemplate(String cardName) {
        this.cardName = cardName;
    }

    public void setMatchingPoints(List<MatchingPoint> matchingPoints) {
        this.matchingPoints = matchingPoints;
    }

    public List<MatchingPoint> getMatchingPoints() {
        return matchingPoints;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    static {
        for (CardsTemplate template : CardsTemplate.values()) {
            try {
                BufferedImage templateImg = ImageIO.read(CardsTemplate.class.getClassLoader().getResource("NewTemplates/" + template.getCardName().toLowerCase() + ".png").openStream());
                template.setMatchingPoints(CardMatcherService.matchingPointsExtractor(CardMatcherService.cropObjectFromImage(templateImg)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
