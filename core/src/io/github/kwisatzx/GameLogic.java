package io.github.kwisatzx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.awt.*;

import static io.github.kwisatzx.GameLogic.State.*;

public class GameLogic extends InputAdapter implements Runnable {
    private final Thread thread;
    private final Board board;
    private boolean exit = false;
    private boolean firstClick = true;
    private State state = NEUTRAL;

    private final Renderer app;
    private final BoardGraphics boardGFX;

    public GameLogic(Renderer app, BoardGraphics boardGFX) {
        this.app = app;
        this.boardGFX = boardGFX;
        Gdx.input.setInputProcessor(this);
        board = new Board(this);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (!exit) {
            if (!firstClick) {
                board.updateGFX(boardGFX);
                //TODO: add ending/cleanup code for starting again, independent of how it's called
                if (state != NEUTRAL) {
                    app.displayEndMsg((state == WON) ? WON : LOST);
                }
            }

            synchronized (this) {
                try { wait(100); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
        System.out.println("GameLogic thread closing...");
    }

    public void generate(int fieldX, int fieldY) {
        board.generateNew(Options.tileWidth, Options.tileHeight, Options.mines, fieldX, fieldY);
    }

    public void waitFor() {
        try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public synchronized void terminate() {
        exit = true;
        notify();
    }

    enum State {
        LOST,
        NEUTRAL,
        WON;
    }

    //--INPUT HANDLING--
    //TODO:violates single responsibility principle?
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Point xy = mapScreenToField();
        if (firstClick) {
            generate(xy.x, xy.y);
            firstClick = false;
            touchDown(screenX, screenY, pointer, button);
        } else {
            if (state != NEUTRAL) return true; //nothing happens if game is over
            if (button == Input.Buttons.LEFT) board.leftClick(xy.x, xy.y);
            if (button == Input.Buttons.RIGHT) board.rightClick(xy.x, xy.y);
        }
        return true;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Point mapScreenToField() {
        int x = Gdx.input.getX();
        int y = Options.HEIGHT-Gdx.input.getY();
        int fieldX = (int) Math.floor(x / Options.tilePixelsX);
        if (fieldX >= Options.tileWidth) fieldX = Options.tileWidth-1;
        if (fieldX < 0) fieldX = 0;
        int fieldY = (int) Math.floor(y / Options.tilePixelsY);
        if (fieldY >= Options.tileHeight) fieldY = Options.tileHeight-1;
        if (fieldY < 0) fieldY = 0;
        return new Point(fieldX, fieldY);
    }
}
