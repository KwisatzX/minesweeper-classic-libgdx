package io.github.kwisatzx;

/*
    A single field. Can be a number(0~8) or a bomb(-1). Undiscovered or discovered, marked (flagged) or not.
*/
//TODO: change value to enum with int values?
public class Field {
    private final int value;
    private boolean discovered = false;
    private boolean marked = false;
    private boolean red = false;

    public Field(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isRed() {
        return red;
    }

    public void setRed(boolean red) {
        this.red = red;
    }
}
