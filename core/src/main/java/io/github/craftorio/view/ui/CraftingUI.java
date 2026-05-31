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
import io.github.craftorio.model.item.LiquidType;
import io.github.craftorio.model.building.production.Craftable;
import io.github.craftorio.model.building.production.CraftModule;
import io.github.craftorio.view.TextureLoad;

import java.util.Locale;
import java.util.Map;

public class CraftingUI implements UIRenderer {

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

    private Craftable currentCraftable;
    private TextureLoad textures;

    private ProgressBar currentProgressBar;

    public CraftingUI(TextureLoad textures) {
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

    public void show(Craftable craftable) {
        this.currentCraftable = craftable;
        updateUI();
        rootTable.setVisible(true);
    }

    private void updateUI() {
        windowTable.clearChildren();
        if (currentCraftable == null) return;

        CraftModule module = currentCraftable.getCraftModule();
        boolean supportsItems = module.getMaxItemCapacity() > 0;
        boolean supportsLiquids = module.getMaxLiquidCapacity() > 0f;

        Table headerTable = new Table();
        Label title = new Label(currentCraftable.getBuildingName(), labelStyle);

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
        for (Recipe recipe : module.getAllowedRecipes()) {
            boolean needsItems = !recipe.getInputItems().isEmpty() || !recipe.getOutputItems().isEmpty();
            boolean needsLiquids = !recipe.getInputLiquids().isEmpty() || !recipe.getOutputLiquids().isEmpty();

            if (needsItems && !supportsItems) continue;
            if (needsLiquids && !supportsLiquids) continue;

            Table recipeRow = new Table();
            if (module.getRecipe() == recipe) {
                recipeRow.setBackground(selectedSlotDrawable);
            } else {
                recipeRow.setBackground(slotDrawable);
            }
            recipeRow.pad(10);

            for (Map.Entry<ItemType, Integer> outItem : recipe.getOutputItems().entrySet()) {
                recipeRow.add(new Image(textures.get(outItem.getKey()).getFirstFrame())).size(32, 32).padRight(5);
                recipeRow.add(new Label("x" + outItem.getValue(), labelStyle)).padRight(15);
            }
            for (Map.Entry<LiquidType, Float> outLiq : recipe.getOutputLiquids().entrySet()) {
                recipeRow.add(new Image(textures.get(outLiq.getKey()).getFirstFrame())).size(32, 32).padRight(5);
                recipeRow.add(new Label("x" + String.format(Locale.US, "%.1f", outLiq.getValue()), labelStyle)).padRight(15);
            }

            float seconds = recipe.getCraftTicks() / 60f;
            recipeRow.add(new Label(String.format(Locale.US, "%.1fs", seconds), labelStyle)).padRight(15);

            Table ingTable = new Table();
            for (Map.Entry<ItemType, Integer> inItem : recipe.getInputItems().entrySet()) {
                ingTable.add(new Image(textures.get(inItem.getKey()).getFirstFrame())).size(24, 24).padRight(2);
                ingTable.add(new Label("x" + inItem.getValue(), labelStyle)).padRight(10);
            }
            for (Map.Entry<LiquidType, Float> inLiq : recipe.getInputLiquids().entrySet()) {
                ingTable.add(new Image(textures.get(inLiq.getKey()).getFirstFrame())).size(24, 24).padRight(2);
                ingTable.add(new Label("x" + String.format(Locale.US, "%.1f", inLiq.getValue()), labelStyle)).padRight(10);
            }
            recipeRow.add(ingTable).expandX().left();

            recipeRow.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    module.setRecipe(recipe);
                    updateUI();
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

        outputSection.add(outputSlotTable);

        bottomTable.add(outputSection).right();
        windowTable.add(bottomTable).fillX().padTop(20);
    }

    private void updateInventories() {
        if (currentCraftable == null) return;
        CraftModule module = currentCraftable.getCraftModule();
        Recipe recipe = module.getRecipe();

        inputSlotsTable.clearChildren();
        outputSlotTable.clearChildren();

        if (recipe != null) {
            for (Map.Entry<ItemType, Integer> entry : recipe.getInputItems().entrySet()) {
                int currentAmount = module.getInputItems().getOrDefault(entry.getKey(), 0);
                inputSlotsTable.add(createItemSlot(entry.getKey(), currentAmount)).size(48, 48).padRight(10);
            }
            for (Map.Entry<LiquidType, Float> entry : recipe.getInputLiquids().entrySet()) {
                float currentAmount = module.getInputLiquids().getOrDefault(entry.getKey(), 0f);
                inputSlotsTable.add(createLiquidSlot(entry.getKey(), currentAmount)).size(48, 48).padRight(10);
            }

            for (Map.Entry<ItemType, Integer> entry : recipe.getOutputItems().entrySet()) {
                int currentAmount = module.getOutputItems().getOrDefault(entry.getKey(), 0);
                outputSlotTable.add(createItemSlot(entry.getKey(), currentAmount)).size(48, 48).padRight(10);
            }
            for (Map.Entry<LiquidType, Float> entry : recipe.getOutputLiquids().entrySet()) {
                float currentAmount = module.getOutputLiquids().getOrDefault(entry.getKey(), 0f);
                outputSlotTable.add(createLiquidSlot(entry.getKey(), currentAmount)).size(48, 48).padRight(10);
            }
        }
    }

    private Table createItemSlot(ItemType type, int amount) {
        Table slot = new Table();
        slot.setBackground(slotDrawable);
        TextureRegion reg = textures.get(type).getFirstFrame();
        slot.add(new Image(reg)).size(24, 24).padBottom(2).row();
        slot.add(new Label(String.valueOf(amount), labelStyle));
        return slot;
    }

    private Table createLiquidSlot(LiquidType type, float amount) {
        Table slot = new Table();
        slot.setBackground(slotDrawable);
        TextureRegion reg = textures.get(type).getFirstFrame();
        slot.add(new Image(reg)).size(24, 24).padBottom(2).row();
        slot.add(new Label(String.format(Locale.US, "%.1f", amount), labelStyle));
        return slot;
    }

    public void close() {
        rootTable.setVisible(false);
        this.currentCraftable = null;
    }

    public void render() {
        if (rootTable.isVisible()) {
            if (currentCraftable != null) {
                if (currentProgressBar != null) {
                    currentProgressBar.setValue(currentCraftable.getCraftModule().getProgress());
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
