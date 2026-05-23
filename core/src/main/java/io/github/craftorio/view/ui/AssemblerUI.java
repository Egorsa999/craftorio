package io.github.craftorio.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.item.Recipe;
import io.github.craftorio.model.building.production.Assembler;
import io.github.craftorio.view.TextureLoad;

import java.util.Map;

public class AssemblerUI {

    private Stage stage;
    private Table rootTable;
    private Table windowTable;

    private Table inputSlotsTable;
    private Table outputSlotTable;

    private Label.LabelStyle labelStyle;
    private Label.LabelStyle closeButtonStyle;

    private Texture bgTexture;
    private Texture slotBgTexture;
    private Texture selectedSlotBgTexture;

    private Texture pbBgTexture;
    private Texture pbKnobTexture;
    private Texture pbEmptyTexture;

    private TextureRegionDrawable slotDrawable;
    private TextureRegionDrawable selectedSlotDrawable;
    private ProgressBar.ProgressBarStyle progressBarStyle;

    private BitmapFont customFont;

    private Assembler currentAssembler;
    private TextureLoad textures;

    private ProgressBar currentProgressBar;

    public AssemblerUI(TextureLoad textures) {
        this.textures = textures;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.color = Color.WHITE;
        this.customFont = generator.generateFont(parameter);
        generator.dispose();

        this.stage = new Stage(new ScreenViewport());

        this.labelStyle = new Label.LabelStyle(customFont, Color.WHITE);
        this.closeButtonStyle = new Label.LabelStyle(customFont, Color.FIREBRICK);

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        windowTable = new Table();
        windowTable.pad(20);

        inputSlotsTable = new Table();
        outputSlotTable = new Table();


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        windowTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        pixmap.dispose();

        Pixmap slotPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        slotPixmap.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        slotPixmap.fill();
        slotBgTexture = new Texture(slotPixmap);
        slotDrawable = new TextureRegionDrawable(new TextureRegion(slotBgTexture));
        slotPixmap.dispose();

        Pixmap selectedPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        selectedPixmap.setColor(0.2f, 0.6f, 0.2f, 0.8f);
        selectedPixmap.fill();
        selectedSlotBgTexture = new Texture(selectedPixmap);
        selectedSlotDrawable = new TextureRegionDrawable(new TextureRegion(selectedSlotBgTexture));
        selectedPixmap.dispose();


        Pixmap pbBgPix = new Pixmap(1, 10, Pixmap.Format.RGBA8888);
        pbBgPix.setColor(0.2f, 0.2f, 0.2f, 1f);
        pbBgPix.fill();
        pbBgTexture = new Texture(pbBgPix);
        pbBgPix.dispose();

        Pixmap pbKnobPix = new Pixmap(1, 10, Pixmap.Format.RGBA8888);
        pbKnobPix.setColor(0.0f, 0.8f, 0.0f, 1f);
        pbKnobPix.fill();
        pbKnobTexture = new Texture(pbKnobPix);
        pbKnobPix.dispose();

        Pixmap emptyPix = new Pixmap(1, 10, Pixmap.Format.RGBA8888);
        emptyPix.setColor(0, 0, 0, 0f);
        emptyPix.fill();
        pbEmptyTexture = new Texture(emptyPix);
        emptyPix.dispose();

        progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = new TextureRegionDrawable(new TextureRegion(pbBgTexture));
        progressBarStyle.knob = new TextureRegionDrawable(new TextureRegion(pbEmptyTexture));
        progressBarStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(pbKnobTexture));


        rootTable.add(windowTable).minWidth(500).minHeight(400);
        stage.addActor(rootTable);
        rootTable.setVisible(false);
    }

    private void updateUI() {
        windowTable.clearChildren();

        Table headerTable = new Table();
        Label title = new Label("Assembler", labelStyle);

        Label closeButton = new Label("[ X ]", closeButtonStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });

        headerTable.add(title).expandX().left();
        headerTable.add(closeButton).right();
        windowTable.add(headerTable).expandX().fillX().padBottom(20).row();

        Table recipesTable = new Table();
        for (Recipe recipe : Recipe.values()) {
            Table recipeRow = new Table();

            if (currentAssembler != null && currentAssembler.getRecipe() == recipe) {
                recipeRow.setBackground(selectedSlotDrawable);
            } else {
                recipeRow.setBackground(slotDrawable);
            }
            recipeRow.pad(10);

            TextureRegion outReg = textures.get(recipe.getOutput()).getFirstFrame();
            recipeRow.add(new Image(outReg)).size(32, 32).padRight(5);
            recipeRow.add(new Label("x" + recipe.getOutputAmount(), labelStyle)).padRight(15);

            float seconds = recipe.getCraftTicks() / 60f;
            recipeRow.add(new Label(String.format("%.1fs", seconds), labelStyle)).padRight(15);

            Table ingTable = new Table();
            for (Map.Entry<ItemType, Integer> entry : recipe.getInputs().entrySet()) {
                TextureRegion inReg = textures.get(entry.getKey()).getFirstFrame();
                ingTable.add(new Image(inReg)).size(24, 24).padRight(2);
                ingTable.add(new Label("x" + entry.getValue(), labelStyle)).padRight(10);
            }
            recipeRow.add(ingTable).expandX().left();

            recipeRow.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (currentAssembler != null) {
                        currentAssembler.setRecipe(recipe);
                        updateUI();
                    }
                }
            });

            recipesTable.add(recipeRow).expandX().fillX().padBottom(10).row();
        }

        windowTable.add(recipesTable).expand().top().fillX().row();

        Table bottomTable = new Table();

        bottomTable.add(inputSlotsTable).left().expandX();

        Table outputSection = new Table();
        currentProgressBar = new ProgressBar(0f, 1f, 0.01f, false, progressBarStyle);
        outputSection.add(currentProgressBar).width(120).padRight(15);
        outputSection.add(outputSlotTable).size(48, 48);

        bottomTable.add(outputSection).right();

        windowTable.add(bottomTable).fillX().padTop(20);
    }

    private void updateInventories() {
        if (currentAssembler == null) return;

        inputSlotsTable.clearChildren();
        Map<ItemType, Integer> inputs = currentAssembler.getInputInventory();
        int inputCount = 0;

        if (inputs != null) {
            for (Map.Entry<ItemType, Integer> entry : inputs.entrySet()) {
                Table slot = new Table();
                slot.setBackground(slotDrawable);

                TextureRegion reg = textures.get(entry.getKey()).getFirstFrame();
                slot.add(new Image(reg)).size(24, 24).padBottom(2).row();
                slot.add(new Label(String.valueOf(entry.getValue()), labelStyle));

                inputSlotsTable.add(slot).size(48, 48).padRight(10);
                inputCount++;
            }
        }

        for (int i = inputCount; i < 3; i++) {
            Table slot = new Table();
            slot.setBackground(slotDrawable);
            inputSlotsTable.add(slot).size(48, 48).padRight(10);
        }

        outputSlotTable.clearChildren();
        outputSlotTable.setBackground(slotDrawable);

        Map<ItemType, Integer> outputs = currentAssembler.getOutputInventory();
        if (outputs != null && !outputs.isEmpty()) {
            Map.Entry<ItemType, Integer> outEntry = outputs.entrySet().iterator().next();

            TextureRegion reg = textures.get(outEntry.getKey()).getFirstFrame();
            outputSlotTable.add(new Image(reg)).size(24, 24).padBottom(2).row();
            outputSlotTable.add(new Label(String.valueOf(outEntry.getValue()), labelStyle));
        }
    }

    public void show(Assembler assembler) {
        this.currentAssembler = assembler;
        updateUI();
        rootTable.setVisible(true);
    }

    public void close() {
        rootTable.setVisible(false);
        this.currentAssembler = null;
    }

    public void render() {
        if (rootTable.isVisible()) {
            if (currentAssembler != null) {
                if (currentProgressBar != null) {
                    currentProgressBar.setValue(currentAssembler.getProgress());
                }
                updateInventories();
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
        slotBgTexture.dispose();
        selectedSlotBgTexture.dispose();
        pbBgTexture.dispose();
        pbKnobTexture.dispose();
        pbEmptyTexture.dispose();
        if (customFont != null) customFont.dispose();
    }
}
