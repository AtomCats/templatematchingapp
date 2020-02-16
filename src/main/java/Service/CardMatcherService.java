package Service;

import Model.Point;
import Model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class CardMatcherService {
    private final static Color CARD_COLOR_WHITE = Color.WHITE;
    private final static Color CARD_COLOR_OTHER = new Color(120,120,120);

    private CardMatcherService(){
    }

    public static String matchCardsWithTemplates(File fileName, int fileNumber) throws IOException {
        List<CardModel> cards = getCardsImages(thresholdImage(ImageIO.read(fileName), 128));
        StringBuilder result = new StringBuilder();
        result.append(fileNumber).append(") ").append(fileName).append("\t");

        for(CardModel card : cards) {
            extractObjectsFromCard(card)
                    .forEach(image -> {
//                        File f = new File("C:\\Users\\Admin\\IdeaProjects\\templatematchingapp\\src\\main\\resources\\temp");
//                        try {
//                            ImageIO.write(image, "png", f);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        List<MatchingPoint> matchingPoints = matchingPointsExtractor(image);
                        if(card.getValue() == null) {
                            card.setValue(((Map.Entry<String, CardsTemplate>)compareMatchingPoints(matchingPoints,true)).getValue().getCardName());
//                            card.setCardSuite(((Map.Entry<String, CardSuiteTemplate>)compareMatchingPoints(matchingPoints,false)).getValue().getSuitName());
//                            for (CardsTemplate value : CardsTemplate.values()) {
//                                if (matchingPoints.removeAll(value.getMatchingPoints()))
//                                if (value.getMatchingPoints().containsAll(matchingPoints)) {
//                                    card.setValue(value.getCardName());
//                                }
//                            }
                        } else {
                            card.setCardSuite(((Map.Entry<String, CardSuiteTemplate>)compareMatchingPoints(matchingPoints,false)).getValue().getSuitName());
//                            for (CardSuiteTemplate suit : CardSuiteTemplate.values()) {
//                                if (suit.getMatchingPoints().containsAll(matchingPoints)) {
//                                    card.setCardSuite(suit.getSuitName());
//                                }
//                            }
                        }
                    });
            result.append(card.getValue()).append(card.getCardSuite()).append(" ");
        }

        return result.toString();
    }

    private static List<BufferedImage> extractObjectsFromCard(CardModel card) {
        List<BufferedImage> cardObjects = new ArrayList<>();
        final BufferedImage cardImage = card.getImage();

        //crop card value
        cardObjects.add(cropObjectFromImage(cardImage));
        //crop card suit (hardcoded because not enough time to implement search)
        cardObjects.add(cropObjectFromImage(cardImage.getSubimage(cardImage.getWidth() - 37,
                cardImage.getHeight() - 36, 37, 36)));
        return cardObjects;
    }

    private static List<CardModel> getCardsImages(BufferedImage source) {
        boolean allFound = false;
        final CardModel firstCard = getFirstCard(source);
        int gap = calculateGapBetweenCards(source, firstCard);
        final List<CardModel> cardsList = new ArrayList<>();

        cardsList.add(firstCard);

        int possibleCardX = firstCard.getX() + firstCard.getWidth() + gap;
        int possibleCardY = firstCard.getY() + (firstCard.getHeight() / 2);
        while (possibleCardX < (source.getWidth() - firstCard.getX())) {
            final CardModel card = new CardModel();

            if (new Color(source.getRGB(possibleCardX,possibleCardY)).equals(CARD_COLOR_WHITE) ||
                    new Color(source.getRGB(possibleCardX,possibleCardY)).equals(CARD_COLOR_OTHER)) {
                card.setHeight(firstCard.getHeight());
                card.setWidth(firstCard.getWidth());
                card.setY(firstCard.getY());
                card.setX(possibleCardX);
                card.setImage(source.getSubimage(card.getX(),card.getY(), card.getWidth(), card.getHeight()));
                cardsList.add(card);
                possibleCardX +=card.getWidth();
            }
            possibleCardX+=gap;
        }
        return cardsList;
    }

    private static CardModel getFirstCard(BufferedImage source) {
        final CardModel firstCard = new CardModel();
        final int width = source.getWidth();
        final int height = source.getHeight();

        getFirstCardXY(source, firstCard);
        firstCard.setImage(source.getSubimage(firstCard.getX(), firstCard.getY(), firstCard.getWidth(), firstCard.getHeight()));

        return firstCard;
    }

    private static void getFirstCardXY(BufferedImage source, CardModel firstCard) {
        boolean gotWidth = false;
        int middleHeight = (source.getHeight() / 2) + 50;
        int cardMiddleHeight = 0;
        int cardWidth = 0;
        int width = 0;



        //get card startX coordinate because we need it and for getting card width
        boolean gotX = false;
        while (!gotX) {
            Color pointColor = new Color(source.getRGB(width, middleHeight));
            if (pointColor.equals(CARD_COLOR_WHITE) || pointColor.equals(CARD_COLOR_OTHER)) {
                firstCard.setX(width);
                    gotX = true;
            }
            width++;
        }

        getFirstCardY(source, firstCard);
        cardMiddleHeight = firstCard.getY() + (firstCard.getHeight() / 2);


//        while (cardWidth >=0) {
//            if (new Color(source.getRGB(width,middleHeight)).equals(cardColorWhite) ||
//                    new Color(source.getRGB(width,middleHeight)).equals(cardColorOther)) {
//                if (cardX == 0) {
//                    cardX = width;
//                    cardWidth--;
//                }
//            } else if (cardX != 0){
//                cardWidth--;
//            }
//            width ++;
//        }
        //width--;
        int cardHeightY = firstCard.getY() + firstCard.getHeight();
        while (!gotWidth && cardMiddleHeight < cardHeightY) {
            Color pointColor = new Color(source.getRGB(width, cardMiddleHeight));
            if (pointColor.equals(CARD_COLOR_WHITE) || pointColor.equals(CARD_COLOR_OTHER)) {
                width ++;
                cardWidth++;
            } else {
                cardMiddleHeight++;
            }
        }
            firstCard.setWidth(cardWidth);
    }

    private static void getFirstCardY(BufferedImage source, CardModel firstCard) {
        boolean gotHeight = false;
        boolean reverse = false;
        int cardMiddleHeight = (source.getHeight() / 2) + 50;
        int height = (source.getHeight() / 2) + 50;
        int cardHeight = 0;
        int cardHeightEnd = 0;

        while(!gotHeight) {
            if(!reverse) {
                if (new Color(source.getRGB(firstCard.getX(),height)).equals(CARD_COLOR_WHITE) ||
                        new Color(source.getRGB(firstCard.getX(),height)).equals(CARD_COLOR_OTHER)) {
                    cardHeight++;
                } else if(cardHeight != 0) {
                    reverse = true;
                    cardHeightEnd = height;
                    height = cardMiddleHeight - 1;
                    continue;
                }
                height++;
            } else {
                if (new Color(source.getRGB(firstCard.getX(),height)).equals(CARD_COLOR_WHITE) ||
                        new Color(source.getRGB(firstCard.getX(),height)).equals(CARD_COLOR_OTHER)) {
                    cardHeight++;
                } else if(cardHeight != 0) {
                    firstCard.setY(cardHeightEnd - cardHeight);
                    firstCard.setHeight(cardHeight);
                    gotHeight = true;
                }
                height--;
            }
        }
    }

    private static int calculateGapBetweenCards(BufferedImage source, CardModel firstCard) {
        int gap = 0;
        boolean gotGap = false;
        int startWidth = firstCard.getX() + firstCard.getWidth();
        int cardMiddleHeight = firstCard.getY() + (firstCard.getHeight() / 2);

        while (!gotGap) {
            if (new Color(source.getRGB(startWidth, cardMiddleHeight)).equals(CARD_COLOR_WHITE) ||
                    new Color(source.getRGB(startWidth, cardMiddleHeight)).equals(CARD_COLOR_OTHER)) {
                if(gap == 0) {
                    startWidth++;
                    continue;
                } else {
                    gotGap = true;
                }
                gap++;
            } else {
                gap++;
            }
            startWidth++;

        }
//        for (int width = firstCard.getX() + firstCard.getWidth(); width < sourceWidth; width++) {
//            if (!new Color(source.getRGB(firstCard.getWidth(),sourceHeight)).equals(cardColorWhite) ||
//                    !new Color(source.getRGB(firstCard.getWidth(),sourceHeight)).equals(cardColorOther)) {
//                gap++;
//            } else {
//                break;
//            }
//        }
        return gap;
    }

    public static List<MatchingPoint> matchingPointsExtractor(BufferedImage source) {
        List<MatchingPoint> matchingPoints = new ArrayList<>();
        BufferedImage object = cropObjectFromImage(source);

        for (int h = 0; h < object.getHeight(); h++) {
            for (int w = 0; w < object.getWidth(); w++) {
                final MatchingPoint matchingPoint = new MatchingPoint();
//                matchingPoint.setMainPoint(new Point(w, h, new Color(object.getRGB(w, h))));
                matchingPoint.setMainPoint(new Point(w, h, new Color(object.getRGB(w, h))));
                getSurroundingPoints(matchingPoint, object);
                matchingPoints.add(matchingPoint);
            }
        }
        return matchingPoints;
    }

    private static void getSurroundingPoints(MatchingPoint point, BufferedImage source) {
        final int width = source.getWidth();
        final int height = source.getHeight();
        ArrayList<Point> surroundings = new ArrayList<Point>(8);

        for (int h = point.getMainPoint().getY() - 1; h < point.getMainPoint().getY() + 1; h++) {
            for (int w = point.getMainPoint().getX() - 1; w < point.getMainPoint().getX() + 1; w++) {
                if (h >= 0 && h <= height && w >= 0 && w <= width) {
                    if (w == point.getMainPoint().getX() && h == point.getMainPoint().getY()) {
                        continue;
                    }
                    try {
                        surroundings.add(new Point(w, h, new Color(source.getRGB(w, h))));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("X = " + w + " Y = " + h);
                    }
                } else {
                    surroundings.add(null);
                }
            }
        }

        point.setSurroundingPoints(surroundings);
    }

    //For templates
    public static BufferedImage cropObjectFromImage(BufferedImage source) {
        Point startPoint = new Point(0, 0);
        Point endPoint = new Point(0, 0);

        final List<Point> points = new ArrayList<>();
        boolean shouldStop = false;

        for (int h = 0; h < source.getHeight(); h++) {
            int pointCounter = 0;
            for (int w = 0; w < source.getWidth(); w++) {
                MatchingPoint point = new MatchingPoint(new Point(w, h, new Color(source.getRGB(w, h))));
                getSurroundingPoints(point, source);
                if (isEmptyPoint(point)) {
                    pointCounter++;
                }
                if(!isEmptyPoint(point)) {
                    points.add(point.getMainPoint());
                }
            }
            if (pointCounter == source.getWidth() && !points.isEmpty()) {
                break;
            }
        }

        points.stream()
                .map(Point::getY)
                .min(Integer::compare)
                .ifPresent(startPoint::setY);

        points.stream()
                .map(Point::getX)
                .min(Integer::compare)
                .ifPresent(startPoint::setX);

        points.stream()
                .map(Point::getY)
                .max(Integer::compare)
                .ifPresent(endPoint::setY);

        points.stream()
                .map(Point::getX)
                .max(Integer::compare)
                .ifPresent(endPoint::setX);

        return source.getSubimage(startPoint.getX(), startPoint.getY(),
                endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());

    }

    private static boolean isEmptyPoint(MatchingPoint point) {
        return Stream.concat(point.getSurroundingPoints().stream(), Stream.of(point.getMainPoint()))
                .filter(Objects::nonNull)
                .allMatch(dot -> dot.getColor().equals(CARD_COLOR_WHITE) || dot.getColor().equals(CARD_COLOR_OTHER));
    }

    private static BufferedImage thresholdImage(BufferedImage image, int threshold) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(image, 0, 0, null);
        WritableRaster raster = result.getRaster();
        int[] pixels = new int[image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            raster.getPixels(0, y, image.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] < threshold) pixels[i] = 0;
                else pixels[i] = 255;
            }
            raster.setPixels(0, y, image.getWidth(), 1, pixels);
        }
        return result;
    }

    private static Map.Entry<String, ? extends Template> compareMatchingPoints (List<MatchingPoint> actual, boolean comparingValue) {
        final HashMap comparingTable = new HashMap();
        if(comparingValue) {

            for (CardsTemplate cardTemplate : CardsTemplate.values()) {
                List<MatchingPoint> clonedActual = new ArrayList<>(actual);
                clonedActual.retainAll(cardTemplate.getMatchingPoints());
                comparingTable.put((float)actual.size() / clonedActual.size(), cardTemplate);
            }
        } else {
            for (CardSuiteTemplate suitTemplate : CardSuiteTemplate.values()) {
                List<MatchingPoint> clonedActual = new ArrayList<>(actual);
                clonedActual.retainAll(suitTemplate.getMatchingPoints());
                comparingTable.put((float)actual.size() / clonedActual.size(), suitTemplate);
            }
        }

        ;
        return Collections.min(comparingTable.entrySet(), Map.Entry.comparingByKey());
    }

}
