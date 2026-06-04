package io.github.craftorio.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.craftorio.model.building.production.Rocket;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.LiquidType;
import io.github.craftorio.view.TextureLoad;

import java.util.Locale;

public class RocketUI implements UIRenderer {

    private Stage stage;
    private Table rootTable;
    private Table windowTable;

    private Label.LabelStyle titleStyle;
    private Label.LabelStyle labelStyle;
    private Label.LabelStyle smallLabelStyle;
    private Label.LabelStyle closeButtonStyle;
    private Label.LabelStyle btnLabelStyle;

    private Texture bgTexture;
    private Texture pbBgTexture;
    private Texture pbEmptyTexture;
    private Texture btnRedTex, btnGreenTex;
    private TextureRegionDrawable btnRedDrawable, btnGreenDrawable;

    private Texture pbMicrochipTex, pbSteelTex, pbFuelTex;

    private ProgressBar.ProgressBarStyle microchipBarStyle, steelBarStyle, fuelBarStyle;

    private BitmapFont customFont;
    private BitmapFont largeFont;
    private BitmapFont smallFont;

    private Rocket currentSilo;
    private TextureLoad textures;

    private ProgressBar barMicrochip, barSteel, barFuel;
    private Label lblMicrochip, lblSteel, lblFuel, btnStartLabel;
    private Table btnStartTable;

    public RocketUI(TextureLoad textures) {
        this.textures = textures;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.color = Color.WHITE;
        this.customFont = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter largeParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        largeParam.size = 28;
        largeParam.color = Color.WHITE;
        this.largeFont = generator.generateFont(largeParam);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 14;
        smallParam.color = Color.LIGHT_GRAY;
        this.smallFont = generator.generateFont(smallParam);

        generator.dispose();

        this.stage = new Stage(new ScreenViewport());
        this.labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        this.smallLabelStyle = new Label.LabelStyle(smallFont, Color.LIGHT_GRAY);
        this.titleStyle = new Label.LabelStyle(largeFont, Color.ORANGE);
        this.closeButtonStyle = new Label.LabelStyle(customFont, Color.FIREBRICK);
        this.btnLabelStyle = new Label.LabelStyle(largeFont, Color.WHITE);

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        windowTable = new Table();
        windowTable.pad(20);
        windowTable.setTouchable(Touchable.enabled);
        windowTable.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.95f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        windowTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        pixmap.dispose();

        Pixmap redPix = new Pixmap(200, 60, Pixmap.Format.RGBA8888);
        redPix.setColor(new Color(0.4f, 0.0f, 0.0f, 1f));
        redPix.fill();
        redPix.setColor(new Color(0.8f, 0.1f, 0.1f, 1f));
        redPix.fillRectangle(4, 4, 192, 52);
        btnRedTex = new Texture(redPix);
        btnRedDrawable = new TextureRegionDrawable(new TextureRegion(btnRedTex));
        redPix.dispose();

        Pixmap greenPix = new Pixmap(200, 60, Pixmap.Format.RGBA8888);
        greenPix.setColor(new Color(0.0f, 0.4f, 0.0f, 1f));
        greenPix.fill();
        greenPix.setColor(new Color(0.1f, 0.8f, 0.1f, 1f));
        greenPix.fillRectangle(4, 4, 192, 52);
        btnGreenTex = new Texture(greenPix);
        btnGreenDrawable = new TextureRegionDrawable(new TextureRegion(btnGreenTex));
        greenPix.dispose();

        pbBgTexture = createColorTexture(new Color(0.2f, 0.2f, 0.2f, 1f));
        pbEmptyTexture = createColorTexture(new Color(0, 0, 0, 0f));
        pbMicrochipTex = createColorTexture(new Color(0.0f, 0.8f, 0.0f, 1f));
        pbSteelTex = createColorTexture(new Color(0.6f, 0.6f, 0.6f, 1f));
        pbFuelTex = createColorTexture(new Color(1.0f, 0.5f, 0.0f, 1f));

        int barThickness = 30;

        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(new TextureRegion(pbBgTexture));
        bgDrawable.setMinWidth(barThickness);

        TextureRegionDrawable emptyKnob = new TextureRegionDrawable(new TextureRegion(pbEmptyTexture));
        emptyKnob.setMinHeight(0);
        emptyKnob.setMinWidth(barThickness);

        microchipBarStyle = new ProgressBar.ProgressBarStyle(bgDrawable, emptyKnob);
        TextureRegionDrawable mcKnobBefore = new TextureRegionDrawable(new TextureRegion(pbMicrochipTex));
        mcKnobBefore.setMinWidth(barThickness);
        microchipBarStyle.knobBefore = mcKnobBefore;

        steelBarStyle = new ProgressBar.ProgressBarStyle(bgDrawable, emptyKnob);
        TextureRegionDrawable steelKnobBefore = new TextureRegionDrawable(new TextureRegion(pbSteelTex));
        steelKnobBefore.setMinWidth(barThickness);
        steelBarStyle.knobBefore = steelKnobBefore;

        fuelBarStyle = new ProgressBar.ProgressBarStyle(bgDrawable, emptyKnob);
        TextureRegionDrawable fuelKnobBefore = new TextureRegionDrawable(new TextureRegion(pbFuelTex));
        fuelKnobBefore.setMinWidth(barThickness);
        fuelBarStyle.knobBefore = fuelKnobBefore;

        rootTable.add(windowTable).minWidth(500).minHeight(450);
        stage.addActor(rootTable);
        rootTable.setVisible(false);
    }

    private Texture createColorTexture(Color color) {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color);
        pix.fill();
        Texture tex = new Texture(pix);
        pix.dispose();
        return tex;
    }

    public void show(Rocket silo) {
        this.currentSilo = silo;
        buildUI();
        rootTable.setVisible(true);
    }

    private void buildUI() {
        windowTable.clearChildren();
        if (currentSilo == null) return;

        Table headerTable = new Table();
        headerTable.add(new Label("Rocket", titleStyle)).left().expandX();
        Label closeButton = new Label("[ X ]", closeButtonStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        headerTable.add(closeButton).right();
        windowTable.add(headerTable).expandX().fillX().padBottom(20).row();

        Table columnsTable = new Table();

        Table colMicrochip = new Table();
        colMicrochip.add(new Image(textures.get(ItemType.CHIP).getFirstFrame())).size(40).padBottom(5).row();
        colMicrochip.add(new Label("Chips", smallLabelStyle)).padBottom(10).row();
        barMicrochip = new ProgressBar(0f, 1f, 0.01f, true, microchipBarStyle);
        colMicrochip.add(barMicrochip).width(30).height(200).padBottom(15).row();
        lblMicrochip = new Label("0 / " + Rocket.REQUIRED_MICROCHIPS, labelStyle);
        lblMicrochip.setAlignment(Align.center);
        colMicrochip.add(lblMicrochip).center();

        Table colSteel = new Table();
        colSteel.add(new Image(textures.get(ItemType.STEEL).getFirstFrame())).size(40).padBottom(5).row();
        colSteel.add(new Label("Steel", smallLabelStyle)).padBottom(10).row();
        barSteel = new ProgressBar(0f, 1f, 0.01f, true, steelBarStyle);
        colSteel.add(barSteel).width(30).height(200).padBottom(15).row();
        lblSteel = new Label("0 / " + Rocket.REQUIRED_STEEL, labelStyle);
        lblSteel.setAlignment(Align.center);
        colSteel.add(lblSteel).center();

        Table colFuel = new Table();
        colFuel.add(new Image(textures.get(LiquidType.ROCKET_FUEL).getFirstFrame())).size(40).padBottom(5).row();
        colFuel.add(new Label("Rocket Fuel", smallLabelStyle)).padBottom(10).row();
        barFuel = new ProgressBar(0f, 1f, 0.01f, true, fuelBarStyle);
        colFuel.add(barFuel).width(30).height(200).padBottom(15).row();
        lblFuel = new Label("0 / " + (int)Rocket.REQUIRED_FUEL, labelStyle);
        lblFuel.setAlignment(Align.center);
        colFuel.add(lblFuel).center();

        columnsTable.add(colMicrochip).padRight(40).align(Align.bottom);
        columnsTable.add(colSteel).padRight(40).align(Align.bottom);
        columnsTable.add(colFuel).align(Align.bottom);

        windowTable.add(columnsTable).padBottom(35).row();

        btnStartTable = new Table();
        btnStartTable.setBackground(btnRedDrawable);
        btnStartLabel = new Label("LAUNCH ROCKET", btnLabelStyle);
        btnStartTable.add(btnStartLabel).padLeft(30).padRight(30).padTop(10).padBottom(10);

        btnStartTable.setTouchable(Touchable.enabled);
        btnStartTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentSilo != null && currentSilo.isReadyToLaunch()) {
                    currentSilo.launch();
                    close();
                }
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (currentSilo != null && currentSilo.isReadyToLaunch()) {
                    btnStartTable.setColor(0.8f, 0.8f, 0.8f, 1f);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                btnStartTable.setColor(1f, 1f, 1f, 1f);
            }
        });
        windowTable.add(btnStartTable).expandX().center();
    }

    public void close() {
        rootTable.setVisible(false);
        this.currentSilo = null;
    }

    public void render() {
        stage.getViewport().apply();
        if (rootTable.isVisible() && currentSilo != null) {

            lblMicrochip.setText(currentSilo.getCurrentMicrochips() + " / " + Rocket.REQUIRED_MICROCHIPS);
            barMicrochip.setValue((float) currentSilo.getCurrentMicrochips() / Rocket.REQUIRED_MICROCHIPS);

            lblSteel.setText(currentSilo.getCurrentSteel() + " / " + Rocket.REQUIRED_STEEL);
            barSteel.setValue((float) currentSilo.getCurrentSteel() / Rocket.REQUIRED_STEEL);

            lblFuel.setText(String.format(Locale.US, "%.0f / %.0f", currentSilo.getCurrentFuel(), Rocket.REQUIRED_FUEL));
            barFuel.setValue(currentSilo.getCurrentFuel() / Rocket.REQUIRED_FUEL);

            if (currentSilo.isReadyToLaunch()) {
                btnStartTable.setBackground(btnGreenDrawable);
                btnStartLabel.setColor(Color.WHITE);
            } else {
                btnStartTable.setBackground(btnRedDrawable);
                btnStartLabel.setColor(Color.LIGHT_GRAY);
            }

            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
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
        bgTexture.dispose();
        pbBgTexture.dispose();
        pbEmptyTexture.dispose();
        pbMicrochipTex.dispose();
        pbSteelTex.dispose();
        pbFuelTex.dispose();
        btnRedTex.dispose();
        btnGreenTex.dispose();
        customFont.dispose();
        largeFont.dispose();
        smallFont.dispose();
    }
}
