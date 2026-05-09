package io.github.craftorio.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class GameAssetManager implements Disposable {

    public final AssetManager manager;
    public static final String MAIN_ATLAS = "assets/atlas/main_atlas.atlas";
    public GameAssetManager() {
        manager = new AssetManager();
    }
    public void loadAll() {
        manager.load(MAIN_ATLAS, TextureAtlas.class);
        manager.finishLoading();
    }
    public TextureAtlas getMainAtlas() {
        return manager.get(MAIN_ATLAS, TextureAtlas.class);
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
