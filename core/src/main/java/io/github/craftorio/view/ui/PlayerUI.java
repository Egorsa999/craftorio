package io.github.craftorio.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container; // НЕ ЗАБУДЬТЕ ЭТОТ ИМПОРТ
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.craftorio.GameConfig;
import io.github.craftorio.model.entity.Player;
import io.github.craftorio.view.CameraManager;

public class PlayerUI implements UIRenderer {
    private final Stage stage;
    private final Player player;
    private final CameraManager cameraManager;

    private final ProgressBar digProgressBar;
    private final Container<ProgressBar> barContainer; // НАША КОРОБКА ДЛЯ МАСШТАБИРОВАНИЯ

    private final Texture bgTexture;
    private final Texture knobBeforeTexture;
    private final Texture emptyTexture;

    private final Vector3 screenPos = new Vector3();

    public PlayerUI(Player player, CameraManager cameraManager) {
        this.player = player;
        this.cameraManager = cameraManager;
        this.stage = new Stage(new ScreenViewport());

        Pixmap bgPix = new Pixmap(1, 6, Pixmap.Format.RGBA8888);
        bgPix.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        bgPix.fill();
        bgTexture = new Texture(bgPix);
        bgPix.dispose();

        Pixmap knobPix = new Pixmap(1, 6, Pixmap.Format.RGBA8888);
        knobPix.setColor(0.2f, 0.8f, 0.2f, 1f);
        knobPix.fill();
        knobBeforeTexture = new Texture(knobPix);
        knobPix.dispose();

        Pixmap emptyPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        emptyPix.setColor(0, 0, 0, 0f);
        emptyPix.fill();
        emptyTexture = new Texture(emptyPix);
        emptyPix.dispose();

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = new TextureRegionDrawable(new TextureRegion(bgTexture));
        style.knob = new TextureRegionDrawable(new TextureRegion(emptyTexture));
        style.knobBefore = new TextureRegionDrawable(new TextureRegion(knobBeforeTexture));

        digProgressBar = new ProgressBar(0f, 1f, 0.01f, false, style);

        barContainer = new Container<>(digProgressBar);
        barContainer.setTransform(true);
        barContainer.size(50, 10);
        barContainer.fill();
        barContainer.setOrigin(com.badlogic.gdx.utils.Align.center);
        barContainer.setVisible(false);

        // Добавляем на сцену КОНТЕЙНЕР, а не сам бар
        stage.addActor(barContainer);
    }

    @Override
    public void render() {
        stage.getViewport().apply();

        if (player.isDigging()) {
            barContainer.setVisible(true); // Показываем контейнер
            digProgressBar.setValue(player.getDigTimer() * GameConfig.TICK_TIME);

            float currentZoom = cameraManager.getCamera().zoom;
            float scale = 1f / currentZoom;

            // Масштабируем контейнер!
            barContainer.setScale(scale);

            screenPos.set(player.playerX, player.playerY + 0.8f, 0);
            cameraManager.getCamera().project(screenPos);

            // Сдвигаем на 25 влево (это половина от базовой ширины 50),
            // масштаб автоматически применится от центра благодаря setOrigin!
            // Говорим: "Поставь свой ЦЕНТР ровно в эту координату X и Y"
            barContainer.setPosition(screenPos.x, screenPos.y, com.badlogic.gdx.utils.Align.center);

        } else {
            barContainer.setVisible(false);
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        bgTexture.dispose();
        knobBeforeTexture.dispose();
        emptyTexture.dispose();
    }
}
