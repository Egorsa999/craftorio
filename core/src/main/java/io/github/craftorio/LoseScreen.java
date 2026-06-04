package io.github.craftorio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoseScreen implements Screen {

    private final MainGame game;
    private final Stage stage;

    private BitmapFont titleFont;
    private BitmapFont customFont;
    private TextureAtlas atlas;

    private Texture btnUpTex;
    private Texture btnDownTex;

    private Music backgroundMusic;

    public LoseScreen(final MainGame game) {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/lose.ogg"));
        backgroundMusic.setLooping(true);
        if (!GameConfig.MUTE_MUSIC)backgroundMusic.play();

        this.game = game;
        this.stage = new Stage(new ScreenViewport());


        atlas = new TextureAtlas(Gdx.files.internal("atlas/main_atlas.atlas"));
        TextureRegion bgRegion = atlas.findRegion("menu-background");

        if (bgRegion != null) {
            Image bgImage = new Image(bgRegion);
            bgImage.setFillParent(true);
            bgImage.setColor(0.15f, 0.15f, 0.15f, 1f);
            stage.addActor(bgImage);
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter paramTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramTitle.size = 72;
        paramTitle.color = new Color(1.0f, 0.2f, 0.2f, 1f);
        paramTitle.shadowOffsetX = 4;
        paramTitle.shadowOffsetY = 4;
        paramTitle.shadowColor = new Color(0, 0, 0, 0.9f);
        titleFont = generator.generateFont(paramTitle);

        FreeTypeFontGenerator.FreeTypeFontParameter paramBig = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramBig.size = 28;
        paramBig.color = Color.WHITE;
        customFont = generator.generateFont(paramBig);
        generator.dispose();

        btnUpTex = createRectTexture(300, 80, new Color(0.6f, 0.3f, 0.0f, 1f), new Color(1.0f, 0.6f, 0.1f, 1f));
        btnDownTex = createRectTexture(300, 80, new Color(0.4f, 0.2f, 0.0f, 1f), new Color(0.8f, 0.4f, 0.0f, 1f));

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(btnUpTex);
        btnStyle.down = new TextureRegionDrawable(btnDownTex);
        btnStyle.font = customFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.LIGHT_GRAY;

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label("YOU LOSE...", titleStyle);

        TextButton menuButton = new TextButton("BACK TO MENU", btnStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                });
            }
        });

        table.add(titleLabel).padBottom(80).row();
        table.add(menuButton).width(300).height(80);
    }

    private Texture createRectTexture(int w, int h, Color borderColor, Color fillColor) {
        Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pix.setColor(borderColor);
        pix.fill();
        pix.setColor(fillColor);
        pix.fillRectangle(6, 6, w - 12, h - 12);
        Texture tex = new Texture(pix);
        pix.dispose();
        return tex;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
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
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        titleFont.dispose();
        customFont.dispose();
        if (atlas != null) atlas.dispose();
        if (btnUpTex != null) btnUpTex.dispose();
        if (btnDownTex != null) btnDownTex.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}
