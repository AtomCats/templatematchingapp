package Service;

import Model.CardModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CardMatcherService {
    private int sourceWidth;
    private int sourceHeight;
    private int gap;
    private final Color cardColorWhite = Color.WHITE;
    private final Color cardColorOther = new Color(120,120,120);
    private final List<CardModel> cardsList = new ArrayList<CardModel>();

    public CardMatcherService(BufferedImage source){
        this.sourceWidth = source.getWidth();
        this.sourceHeight = source.getHeight();
    }

    public List<CardModel> getCardsImages(BufferedImage source) {
        boolean allFound = false;
        final CardModel firstCard = getFirstCard(source);
        cardsList.add(firstCard);

        int possibleCardX = firstCard.getX() + firstCard.getWidth() + gap;
        while (!allFound) {
            final CardModel card = new CardModel();

            if (new Color(source.getRGB(possibleCardX,sourceHeight/2)).equals(cardColorWhite) ||
                    new Color(source.getRGB(possibleCardX,sourceHeight/2)).equals(cardColorOther)) {
                card.setHeight(firstCard.getHeight());
                card.setWidth(firstCard.getWidth());
                card.setY(firstCard.getY());
                card.setX(firstCard.getX() + firstCard.getWidth() + gap);
                cardsList.add(card);
            }
        }
        return cardsList;
    }

    private CardModel getFirstCard (BufferedImage source) {
        final CardModel firstCard = new CardModel();
        final int width = sourceWidth;
        final int height = sourceHeight;

        getFirstCardX(source, firstCard);
        getFirstCardY(source, firstCard);
        gap = calculateGapBetweenCards(source, firstCard);
    }

    private void getFirstCardX (BufferedImage source, CardModel firstCard) {
        boolean gotWidth = false;
        int middleHeight = sourceHeight % 2;
        int cardWidth = 0;
        int cardX = 0;
        int width = 0;

        while (!gotWidth) {
            if (new Color(source.getRGB(width,middleHeight)).equals(cardColorWhite) ||
                    new Color(source.getRGB(width,middleHeight)).equals(cardColorOther)) {
                if (cardWidth == 0) {
                    cardX = width;
                }
                cardWidth++;
            } else if(cardWidth != 0) {
                gotWidth = true;
                firstCard.setX(cardX);
                firstCard.setWidth(cardWidth);
            }
            width ++;
        }
    }

    private void getFirstCardY (BufferedImage source, CardModel firstCard) {
        boolean gotHeight = false;
        boolean reverse = false;
        int height = sourceHeight % 2;
        int cardHeight = 0;

        while(!gotHeight) {
            if(!reverse) {
                if (new Color(source.getRGB(firstCard.getWidth(),height)).equals(cardColorWhite) ||
                        new Color(source.getRGB(firstCard.getWidth(),height)).equals(cardColorOther)) {
                    cardHeight++;
                } else if(cardHeight != 0) {
                    reverse = true;
                    height = height % 2;
                }
                height++;
            } else {
                if (new Color(source.getRGB(firstCard.getWidth(),height)).equals(cardColorWhite) ||
                        new Color(source.getRGB(firstCard.getWidth(),height)).equals(cardColorOther)) {
                    cardHeight++;
                } else if(cardHeight != 0) {
                    firstCard.setY(height++);
                    firstCard.setHeight(cardHeight);
                    gotHeight = true;
                }
                height--;
            }
        }
    }

    private int calculateGapBetweenCards(BufferedImage source, CardModel firstCard) {
        int gap = 0;
        for (int width = firstCard.getX() + firstCard.getWidth(); width < sourceWidth; width++) {
            if (!new Color(source.getRGB(firstCard.getWidth(),sourceHeight)).equals(cardColorWhite) ||
                    !new Color(source.getRGB(firstCard.getWidth(),sourceHeight)).equals(cardColorOther)) {
                gap++;
            } else {
                break;
            }
        }
        return gap;
    }
}
