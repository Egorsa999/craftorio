package io.github.craftorio;

import com.badlogic.gdx.Game;

public class MainGame extends Game {

    @Override
    public void create() {
        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
