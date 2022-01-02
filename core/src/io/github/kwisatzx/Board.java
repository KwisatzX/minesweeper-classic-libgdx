package io.github.kwisatzx;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class Board {
    private final GameLogic gameLogic;
    private Field[][] fields;
    private int w;
    private int h;

    public Board(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    // @formatter:off
    private boolean isCorrectField(int x, int y) {
        return  (x >= 0) && (y >= 0) &&
                (x < w) && (y < h) &&
                (fields[x][y] != null);
    }
    // @formatter:on

    private void tryExecutingOnField(int x, int y, BiConsumer<Field, Point> consumer) {
        if (isCorrectField(x, y)) consumer.accept(fields[x][y], new Point(x, y));
    }

    //TODO: change BiConsumer to maybe Function or w/e with ? types after learning generics
    private void executeOnAllFieldsAround(int x, int y, BiConsumer<Field, Point> consumer) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i == x && j == y) continue;
                tryExecutingOnField(i, j, consumer);
            }
        }
    }

    private List<Field> listOfAllFieldsAround(int x, int y) {
        List<Field> fieldsAround = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i == x && j == y) continue;
                if (isCorrectField(i, j)) fieldsAround.add(fields[i][j]);
            }
        }
        return fieldsAround;
    }

    // @formatter:off
    private boolean isBombAroundField(Point field, Point bombXY) {
        return (bombXY.x < field.x + 2 && bombXY.x > field.x - 2) &&
               (bombXY.y < field.y + 2 && bombXY.y > field.y - 2); //5x5
    }
    // @formatter:on

    //TODO: refactor into smaller methods
    public void generateNew(int w, int h, int mines, int fieldX, int fieldY) {
        fields = new Field[w][h];
        this.w = w;
        this.h = h;

        //planting mines
        Random rng = new Random();
        for (int i = 0; i < mines; i++) {
            int x, y;
            do {
                x = rng.nextInt(w);
                y = rng.nextInt(h);
            } while (isBombAroundField(new Point(fieldX, fieldY), new Point(x, y))
                    || fields[x][y] != null);
            fields[x][y] = new Field(-1);
        }

        //create fields with values corresponding to nearby bombs
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (fields[x][y] != null) continue;
                int bombCount = (int) listOfAllFieldsAround(x, y).stream()
                        .filter(field -> field.getValue() == -1)
                        .count();
                fields[x][y] = new Field(bombCount);
            }
        }
    }


    //state: 0 - continue, -1 = lose, 1 = win
    //TODO: change state to enum
    //TODO: refactor into smaller methods
    public void leftClick(int x, int y) {
        if (fields[x][y].isMarked()) return; //can't click marked field
        if (fields[x][y].getValue() == -1) { //clicked mine
            fields[x][y].setRed(true);
            fields[x][y].setDiscovered(true);
            //reveal the other mines
            for (Field[] fX : fields) {
                for (Field fY : fX) {
                    if (fY.getValue() == -1) fY.setDiscovered(true);
                }
            }
            gameLogic.setState(GameLogic.State.LOST);
        } else if (fields[x][y].getValue() == 0) clickedBlankField(x, y);
        else if (fields[x][y].isDiscovered()) { //reveal fields around if bombs are marked
            int markedFieldsCount = (int) listOfAllFieldsAround(x, y).stream()
                    .filter(field -> field.isMarked())
                    .count();
            if (markedFieldsCount == fields[x][y].getValue()) {
                executeOnAllFieldsAround(x, y, (field, p) -> {
                    if (field.getValue() == -1 && !field.isMarked()) leftClick(p.x, p.y);
                    else if (!field.isMarked()) { //do manually or recursive stack overflow
                        if (field.getValue() == 0) clickedBlankField(p.x, p.y);
                        else field.setDiscovered(true);
                    }
                });
            }
        } else fields[x][y].setDiscovered(true);
        if (checkVictory()) gameLogic.setState(GameLogic.State.WON);
    }

    private void clickedBlankField(int x, int y) {
        fields[x][y].setDiscovered(true);
        revealAroundField(x, y);
    }

    public void rightClick(int x, int y) {
        if (fields[x][y].isMarked()) fields[x][y].setMarked(false);
        else if (!fields[x][y].isDiscovered()) {
            fields[x][y].setMarked(true);
        }
    }

    private boolean checkForBlankField(Field field) {
        if (field.getValue() == 0 && !field.isDiscovered()) {
            field.setDiscovered(true);
            return true;
        }
        field.setDiscovered(true);
        return false;
    }

    private void revealAroundField(int x, int y) {
        executeOnAllFieldsAround(x, y, (field, point) -> {
            if (checkForBlankField(field)) revealAroundField(point.x, point.y);
        });
    }

    //check if every tile except bombs is discovered
    //TODO:refactor into simple for each loops
    public boolean checkVictory() {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields[0].length; j++) {
                if (fields[i][j].getValue() != -1) {
                    if (!fields[i][j].isDiscovered()) return false;
                }
            }
        }
        return true;
    }

    public void updateGFX(BoardGraphics boardGFX) {
        boardGFX.updateBoardGFX(fields);
    }
}
