package io.github.craftorio.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.craftorio.model.enemy.SpawnDirection;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.sprite.GameSprite;

import java.util.Locale;

public class WaveUI implements UIRenderer {

    private final Stage stage;
    private final WaveSpawner waveSpawner;

    private final BitmapFont customFont;
    private final Texture bgTexture;

    private final Table rootTable;
    private final Label waveNumberLabel;
    private final Label timerLabel;
    private final Image directionArrow;

    private final Cell<Image> arrowCell;

    public WaveUI(TextureLoad textures, WaveSpawner waveSpawner) {
        this.stage = new Stage(new ScreenViewport());
        this.waveSpawner = waveSpawner;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 25;
        parameter.color = Color.WHITE;

        this.customFont = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle internalStyle = new Label.LabelStyle(customFont, Color.WHITE);

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().right();
        rootTable.pad(20);

        waveNumberLabel = new Label("Wave: 1", internalStyle);
        waveNumberLabel.setColor(Color.ORANGE);
        waveNumberLabel.setAlignment(Align.center);

        timerLabel = new Label("00:00", internalStyle);
        timerLabel.setAlignment(Align.center);

        TextureRegion arrowRegion = getSafeRegion(textures.get("wave-arrow"));
        directionArrow = new Image(arrowRegion);
        directionArrow.setScaling(Scaling.fit);

        directionArrow.setSize(64, 64);
        directionArrow.setOrigin(32, 32);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        pixmap.dispose();

        Table infoTable = new Table();
        infoTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        infoTable.pad(15);

        infoTable.add(waveNumberLabel).center().padBottom(5).row();

        arrowCell = infoTable.add(directionArrow).size(64, 64).center().padBottom(5);
        infoTable.row();

        infoTable.add(timerLabel).center();

        rootTable.add(infoTable).right();
        stage.addActor(rootTable);
    }

    @Override
    public void render() {
        stage.getViewport().apply();

        updateLogic();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void updateLogic() {
        int waveNum = waveSpawner.getCurrentWaveNumber();
        float timeLeft = waveSpawner.getTimeRemainingUntilNextWave();
        SpawnDirection dir = waveSpawner.getCurrentWaveDirection();

        boolean isActive = waveSpawner.isWaveActive();
        int enemyCount = waveSpawner.getActiveEnemies().size();

        if (waveSpawner.isInfiniteMode()) {
            waveNumberLabel.setText("Infinite wave!");
            waveNumberLabel.setColor(Color.RED);

            timerLabel.setVisible(false);

        } else if (waveSpawner.isPreparingForInfinite()) {
            waveNumberLabel.setText("Final wave incoming!");
            waveNumberLabel.setColor(Color.RED);

            timerLabel.setVisible(true);
            int minutes = (int) (timeLeft / 60);
            int seconds = (int) (timeLeft % 60);
            timerLabel.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));

            if (timeLeft <= 10f && timeLeft > 0) {
                timerLabel.setColor(Color.RED);
            } else {
                timerLabel.setColor(Color.WHITE);
            }

        } else {
            waveNumberLabel.setText("Wave: " + waveNum);
            waveNumberLabel.setColor(Color.ORANGE);

            timerLabel.setVisible(true);

            if (isActive) {
                timerLabel.setText("Enemies: " + enemyCount);
                timerLabel.setColor(Color.WHITE);
            } else {
                // Если волна мертва - показываем таймер до следующей
                int minutes = (int) (timeLeft / 60);
                int seconds = (int) (timeLeft % 60);
                timerLabel.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));

                if (timeLeft <= 10f && timeLeft > 0) {
                    timerLabel.setColor(Color.RED);
                } else {
                    timerLabel.setColor(Color.WHITE);
                }
            }
        }

        if (dir != null && !isActive && !waveSpawner.isInfiniteMode()) {
            directionArrow.setVisible(true);
            arrowCell.size(64, 64).padBottom(5);
            switch (dir) {
                case NORTH: directionArrow.setRotation(0); break;
                case WEST:  directionArrow.setRotation(90); break;
                case SOUTH: directionArrow.setRotation(180); break;
                case EAST:  directionArrow.setRotation(-90); break;
            }
        } else {
            directionArrow.setVisible(false);
            arrowCell.size(0, 0).padBottom(0);
        }
    }
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (customFont != null) customFont.dispose();
    }

    private TextureRegion getSafeRegion(GameSprite sprite) {
        if (sprite != null && sprite.getFirstFrame() != null) {
            return sprite.getFirstFrame();
        }
        return new TextureRegion();
    }
}
