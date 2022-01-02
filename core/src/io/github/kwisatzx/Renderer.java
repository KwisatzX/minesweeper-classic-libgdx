package io.github.kwisatzx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static io.github.kwisatzx.GameLogic.State.NEUTRAL;
import static io.github.kwisatzx.GameLogic.State.WON;

public class Renderer extends ApplicationAdapter {
    private GameLogic game;
    private BoardGraphics boardGFX;
    private SpriteBatch batch;
    private BitmapFont font;
    private GameLogic.State endMsg = NEUTRAL;

    @Override
    public void create() {
        //TODO:move this into TileGraphics enum
        boardGFX = new BoardGraphics();
        game = new GameLogic(this, boardGFX);
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        drawTiles();
        if (endMsg != NEUTRAL) drawEndMsg();
        batch.end();
    }

    private void drawTiles() {
        BoardGraphics.TileGraphics[][] gfx = boardGFX.getBoardGFX();
        int x = 0, y = 0;
        for (int i = 0; i < gfx.length; i++, x += 26, y = 0) {
            for (int j = 0; j < gfx[0].length; j++, y += 26) {
                batch.draw(gfx[i][j].getTexture(), x, y); //for each field in gfx[][] (representing texture number) grab and draw texture, moving by 26px
            }
        }
    }

    private void drawEndMsg() {
        int msgX = (Options.tileWidth * Options.tilePixelsX) / 2 - 30;
        int msgY = (Options.tileHeight * Options.tilePixelsY) / 2;
        if (endMsg == WON) {
            font.setColor(Color.GREEN);
            font.draw(batch, "You defeated", msgX, msgY);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "You died", msgX, msgY);
        }
    }

    public void displayEndMsg(GameLogic.State state) {
        this.endMsg = state;
    }

    @Override
    public void dispose() {
        System.out.println("Closing main thread...");
        game.terminate();
        game.waitFor();
        System.out.println("Disposing of textures...");
        BoardGraphics.TileGraphics.dispose();
        font.dispose();
        System.out.println("Main thread closing...");
    }
}
