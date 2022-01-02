package io.github.kwisatzx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Arrays;

import static io.github.kwisatzx.BoardGraphics.TileGraphics.*;

public class BoardGraphics {
    private final TileGraphics[][] boardGFX;

    public BoardGraphics() {
        boardGFX = new TileGraphics[Options.tileWidth][Options.tileHeight];
        for (TileGraphics[] x : boardGFX) Arrays.fill(x, UNDISCOVERED);
    }

    //translate Field objects to their corresponding texture number
    synchronized public void updateBoardGFX(Field[][] fields) {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields[0].length; j++) {
                if (fields[i][j].isMarked()) boardGFX[i][j] = FLAG;
                else if (!fields[i][j].isDiscovered()) boardGFX[i][j] = UNDISCOVERED;
                else if (fields[i][j].getValue() == -1) {
                    if (fields[i][j].isRed()) boardGFX[i][j] = RED_BOMB;
                    else boardGFX[i][j] = BOMB;
                }
                else boardGFX[i][j] = getByValue(fields[i][j].getValue());
            }
        }
    }

    synchronized public TileGraphics[][] getBoardGFX() {
        return boardGFX;
    }

    enum TileGraphics {
        NUMBER_0("tile0"),
        NUMBER_1("tile1"),
        NUMBER_2("tile2"),
        NUMBER_3("tile3"),
        NUMBER_4("tile4"),
        NUMBER_5("tile5"),
        NUMBER_6("tile6"),
        NUMBER_7("tile7"),
        NUMBER_8("tile8"),
        BOMB("tileBomb"),
        RED_BOMB("tileBombRed"),
        FLAG("tileFlag"),
        UNDISCOVERED("tileNormal");

        private final Texture texture;

        TileGraphics(String fileName) {
            texture = new Texture(Gdx.files.internal("core/assets/" + fileName + ".png"));
        }

        public Texture getTexture() {
            return texture;
        }

        public static TileGraphics getByValue(int value) {
            return values()[value];
        }

        public static void dispose() {
            for (TileGraphics tile : values()) tile.getTexture().dispose();
        }
    }
}
