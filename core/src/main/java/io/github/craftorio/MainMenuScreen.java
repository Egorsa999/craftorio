package io.github.craftorio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;

import java.util.ArrayList;
import java.util.List;

public class MainMenuScreen implements Screen {

    private final MainGame game;
    private Stage stage;

    private BitmapFont customFont;
    private BitmapFont titleFont;
    private BitmapFont smallFont;
    private TextureAtlas atlas;

    private boolean isMusicPlaying = false;
    private boolean isLoading = false;
    private Table rootTable;
    private Table loadingOverlay;
    private Label dotsLabel;
    private float stateTime = 0f;

    private Music backgroundMusic;

    private List<Texture> generatedTextures = new ArrayList<>();

    public MainMenuScreen(final MainGame game) {



        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        TextureRegion bgRegion = atlas.findRegion("menu-background");

        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setFillParent(true);
            stage.addActor(bgImage);
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter paramTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramTitle.size = 54;
        paramTitle.color = Color.WHITE;
        paramTitle.shadowOffsetX = 3;
        paramTitle.shadowOffsetY = 3;
        paramTitle.shadowColor = new Color(0, 0, 0, 0.8f);
        titleFont = generator.generateFont(paramTitle);

        FreeTypeFontGenerator.FreeTypeFontParameter paramBig = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramBig.size = 28;
        paramBig.color = Color.WHITE;
        customFont = generator.generateFont(paramBig);

        FreeTypeFontGenerator.FreeTypeFontParameter paramSmall = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramSmall.size = 18;
        paramSmall.color = Color.LIGHT_GRAY;
        smallFont = generator.generateFont(paramSmall);
        generator.dispose();

        Texture btnUpTex = createRectTexture(300, 80, new Color(0.6f, 0.3f, 0.0f, 1f), new Color(1.0f, 0.6f, 0.1f, 1f));
        Texture btnDownTex = createRectTexture(300, 80, new Color(0.4f, 0.2f, 0.0f, 1f), new Color(0.8f, 0.4f, 0.0f, 1f));

        TextButton.TextButtonStyle playBtnStyle = new TextButton.TextButtonStyle();
        playBtnStyle.up = new TextureRegionDrawable(btnUpTex);
        playBtnStyle.down = new TextureRegionDrawable(btnDownTex);
        playBtnStyle.font = customFont;
        playBtnStyle.fontColor = Color.WHITE;
        playBtnStyle.downFontColor = Color.LIGHT_GRAY;

        CheckBox.CheckBoxStyle toggleStyle = new CheckBox.CheckBoxStyle();
        toggleStyle.checkboxOn = new TextureRegionDrawable(createToggleTexture(true));
        toggleStyle.checkboxOff = new TextureRegionDrawable(createToggleTexture(false));
        toggleStyle.font = smallFont;
        toggleStyle.fontColor = Color.WHITE;

        Texture darkTex = createSolidColorTexture(new Color(0.05f, 0.05f, 0.05f, 0.85f));
        TextureRegionDrawable rightPanelBg = new TextureRegionDrawable(darkTex);

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.left();
        stage.addActor(rootTable);

        Table sidePanel = new Table();
        sidePanel.setBackground(rightPanelBg);
        sidePanel.pad(40);
        sidePanel.top();

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label("CRAFTORIO", titleStyle);

        TextButton playButton = new TextButton("START GAME", playBtnStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLoading();
            }
        });

        CheckBox musicToggle = new CheckBox("  Enable Music", toggleStyle);
        musicToggle.setChecked(true);
        musicToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onMusicToggled(musicToggle.isChecked());

            }
        });
        onMusicToggled(true);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu-ost.ogg"));
        backgroundMusic.setLooping(true);

        CheckBox resourcesToggle = new CheckBox("  Infinite Resources", toggleStyle);
        resourcesToggle.setChecked(false);
        onResourcesToggled(false);
        resourcesToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onResourcesToggled(resourcesToggle.isChecked());
            }
        });

        CheckBox enemiesToggle = new CheckBox("  Disable Enemies", toggleStyle);
        enemiesToggle.setChecked(false);
        enemiesToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onEnemiesToggled(enemiesToggle.isChecked());
            }
        });
        onEnemiesToggled(false);

        sidePanel.add(titleLabel).padTop(60).padBottom(60).row();
        sidePanel.add(playButton).width(300).height(80).padBottom(50).row();

        Table settingsTable = new Table();
        settingsTable.add(musicToggle).left().padBottom(20).row();
        settingsTable.add(resourcesToggle).left().padBottom(20).row();
        settingsTable.add(enemiesToggle).left().row();

        sidePanel.add(settingsTable).expandX().left().padLeft(20);

        rootTable.add(sidePanel).width(450).expandY().fillY();

        createLoadingOverlay();
    }

    private void createLoadingOverlay() {
        loadingOverlay = new Table();
        loadingOverlay.setFillParent(true);
        loadingOverlay.setTouchable(Touchable.enabled);
        loadingOverlay.setVisible(false);

        Texture fullDarkTex = createSolidColorTexture(new Color(0f, 0f, 0f, 0.9f));
        loadingOverlay.setBackground(new TextureRegionDrawable(fullDarkTex));

        Table textContainer = new Table();

        Label.LabelStyle loadingStyle = new Label.LabelStyle(customFont, Color.WHITE);
        Label loadingLabel = new Label("Loading", loadingStyle);

        dotsLabel = new Label("", loadingStyle);
        dotsLabel.setAlignment(Align.left);

        textContainer.add(loadingLabel).padRight(10f);
        textContainer.add(dotsLabel).width(50f);

        loadingOverlay.add(textContainer).center();

        stage.addActor(loadingOverlay);
    }

    private void showLoading() {
        isLoading = true;
        loadingOverlay.setVisible(true);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        }, 3f);
    }

    private void onMusicToggled(boolean isEnabled) {
        GameConfig.MUTE_MUSIC = !isEnabled;
    }

    private void onResourcesToggled(boolean isEnabled) {
        GameConfig.INFINITY_RESOURCES = isEnabled;
    }

    private void onEnemiesToggled(boolean isDisabled) {
        GameConfig.SPAWN_ENEMY = !isDisabled;
    }

    private Texture createRectTexture(int w, int h, Color borderColor, Color fillColor) {
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setColor(borderColor);
        pix.fill();
        pix.setColor(fillColor);
        pix.fillRectangle(6, 6, w - 12, h - 12);
        Texture tex = new Texture(pix);
        pix.dispose();
        generatedTextures.add(tex);
        return tex;
    }

    private Texture createToggleTexture(boolean isOn) {
        int w = 70;
        int h = 34;
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        pix.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
        pix.fill();
        pix.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
        pix.drawRectangle(0, 0, w, h);

        if (isOn) {
            pix.setColor(new Color(0.2f, 0.8f, 0.2f, 1f));
            pix.fillRectangle(w - 32, 2, 30, 30);
            pix.setColor(new Color(0.4f, 1.0f, 0.4f, 1f));
            pix.fillRectangle(w - 30, 4, 26, 8);
        } else {
            pix.setColor(new Color(0.8f, 0.2f, 0.2f, 1f));
            pix.fillRectangle(2, 2, 30, 30);
            pix.setColor(new Color(1.0f, 0.4f, 0.4f, 1f));
            pix.fillRectangle(4, 4, 26, 8);
        }

        Texture tex = new Texture(pix);
        pix.dispose();
        generatedTextures.add(tex);
        return tex;
    }

    private Texture createSolidColorTexture(Color color) {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color);
        pix.fill();
        Texture tex = new Texture(pix);
        pix.dispose();
        generatedTextures.add(tex);
        return tex;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (isMusicPlaying && GameConfig.MUTE_MUSIC){
            backgroundMusic.pause();
            isMusicPlaying = false;
        }

        if (!isMusicPlaying && !GameConfig.MUTE_MUSIC){
            backgroundMusic.play();
            isMusicPlaying = true;
        }

        if (isLoading && loadingOverlay.isVisible()) {
            stateTime += delta;
            int numDots = ((int) Math.floor(stateTime * 2)) % 4;

            String dotsText = "";
            for (int i = 0; i < numDots; i++) {
                dotsText += ".";
            }
            dotsLabel.setText(dotsText);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        customFont.dispose();
        titleFont.dispose();
        smallFont.dispose();
        atlas.dispose();
        for (Texture tex : generatedTextures) {
            tex.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}
