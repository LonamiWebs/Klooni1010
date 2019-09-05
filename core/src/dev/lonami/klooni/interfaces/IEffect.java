package dev.lonami.klooni.interfaces;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import dev.lonami.klooni.game.Cell;

public interface IEffect {
    void setInfo(Cell deadCell, Vector2 culprit);

    void draw(Batch batch);

    boolean isDone();
}
