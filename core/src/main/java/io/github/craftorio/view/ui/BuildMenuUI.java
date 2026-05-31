package io.github.craftorio.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.ui.BuildTool;
import io.github.craftorio.model.ui.Inventory;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.sprite.GameSprite;

import java.util.HashMap;
import java.util.Map;

public class BuildMenuUI implements UIRenderer {
    private final Stage stage;
    private final BuildTool buildTool;
    private final Inventory inventory;
    private final TextureLoad textures;

    private final Table windowTable;
    private final Table infoTable;
    private final Cell<Table> infoCell;

    private final Image infoIcon;
    private final Label titleLabel;
    private final Table costTable;

    private final Label.LabelStyle titleStyle;
    private final Label.LabelStyle costStyle;
    private final BitmapFont titleFont;
    private final BitmapFont costFont;

    private final Map<BuildingType, Button> buttons = new HashMap<>();

    private final Texture fallbackTexture;
    private final TextureRegion fallbackRegion;
    private final Texture darkBgTexture;
    private final Texture outlineTexture;

    private final NinePatchDrawable selectedOutline;

    private BuildingType lastSelectedType = null;
    private final Map<ItemType, Integer> lastKnownInventory = new HashMap<>();

    public BuildMenuUI(BuildTool buildTool, Inventory inventory, TextureLoad textures) {
        this.stage = new Stage(new ScreenViewport());
        this.buildTool = buildTool;
        this.inventory = inventory;
        this.textures = textures;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter paramTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramTitle.size = 25;
        paramTitle.color = Color.WHITE;
        paramTitle.minFilter = Texture.TextureFilter.Nearest;
        paramTitle.magFilter = Texture.TextureFilter.Nearest;
        this.titleFont = generator.generateFont(paramTitle);

        FreeTypeFontGenerator.FreeTypeFontParameter paramCost = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramCost.size = 19;
        paramCost.color = Color.WHITE;
        paramCost.minFilter = Texture.TextureFilter.Nearest;
        paramCost.magFilter = Texture.TextureFilter.Nearest;
        this.costFont = generator.generateFont(paramCost);

        generator.dispose();

        this.titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        this.costStyle = new Label.LabelStyle(costFont, Color.WHITE);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);
        this.fallbackRegion = new TextureRegion(fallbackTexture);
        pixmap.dispose();

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0.08f, 0.08f, 0.08f, 0.9f);
        bgPixmap.fill();
        this.darkBgTexture = new Texture(bgPixmap);
        TextureRegionDrawable darkBg = new TextureRegionDrawable(new TextureRegion(darkBgTexture));
        bgPixmap.dispose();

        Pixmap outlinePix = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        outlinePix.setColor(new Color(1f, 0.85f, 0.5f, 1f));
        for (int i = 0; i < 4; i++) {
            outlinePix.drawRectangle(i, i, 16 - i * 2, 16 - i * 2);
        }
        this.outlineTexture = new Texture(outlinePix);
        outlinePix.dispose();
        this.selectedOutline = new NinePatchDrawable(new NinePatch(outlineTexture, 4, 4, 4, 4));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.align(Align.bottomRight);

        windowTable = new Table();
        windowTable.setBackground(darkBg);
        windowTable.pad(20);

        // --- ДЕЛАЕМ ГЛАВНОЕ МЕНЮ ТВЕРДЫМ ---
        windowTable.setTouchable(Touchable.enabled);
        windowTable.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        infoTable = new Table();
        infoTable.left();

        infoIcon = new Image();
        infoIcon.setScaling(Scaling.fit);

        Table textTable = new Table();
        titleLabel = new Label("", titleStyle);
        titleLabel.setEllipsis(true);

        Label helpButton = new Label("[?]", titleStyle);
        helpButton.setColor(Color.YELLOW);

        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (lastSelectedType != null) {
                    showDetailedInfoWindow(lastSelectedType);
                }
            }
        });

        costTable = new Table();
        costTable.top().left();

        textTable.add(titleLabel).expandX().fillX().left().row();
        textTable.add(helpButton).right().padRight(10).row();
        textTable.add(costTable).left().top().expandY();

        infoTable.add(infoIcon).size(72, 72).padRight(16).top();
        infoTable.add(textTable).expandX().fillX().height(90).left().top();

        infoCell = windowTable.add(infoTable).left().fillX();
        infoTable.setVisible(false);
        infoCell.height(0).padBottom(0);
        windowTable.row();

        Table gridTable = new Table();
        gridTable.align(Align.topLeft);

        int columns = 5;
        int currentCol = 0;

        for (final BuildingType type : BuildingType.values()) {
            if (type == BuildingType.CORE) continue;

            Button.ButtonStyle btnStyle = new Button.ButtonStyle();
            btnStyle.up = null;
            Button btn = new Button(btnStyle);

            TextureRegion iconRegion = getSafeRegion(textures.get(type));
            if (type == BuildingType.HORIZONTAL_MINER) {
                iconRegion = getSafeRegion(textures.get("horizontal-miner-icon"));
            }
            Image iconImage = new Image(iconRegion);
            iconImage.setScaling(Scaling.fit);
            btn.add(iconImage).expand().fill().pad(6);

            buttons.put(type, btn);

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buildTool.isActive() && buildTool.getPreviewState().selectedType() == type) {
                        buildTool.clearSelection();
                    } else {
                        buildTool.selectBuilding(type);
                    }
                    updateState(true);
                }
            });

            gridTable.add(btn).size(64, 64).pad(3);

            currentCol++;
            if (currentCol >= columns) {
                gridTable.row();
                currentCol = 0;
            }
        }

        windowTable.add(gridTable).fillX();

        rootTable.add(windowTable).pad(25);
        stage.addActor(rootTable);
    }

    private void showDetailedInfoWindow(BuildingType type) {
        final Table overlay = new Table();
        overlay.setFillParent(true);

        overlay.setTouchable(Touchable.enabled);

        TextureRegionDrawable darkOverlay = new TextureRegionDrawable(new TextureRegion(darkBgTexture));
        overlay.setBackground(darkOverlay);

        overlay.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                overlay.remove();
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    overlay.remove();
                    return true; // Съедаем событие, чтобы оно не ушло в игру
                }
                return false;
            }
        });

        Table window = new Table();

        window.setTouchable(Touchable.enabled);
        window.setBackground(darkOverlay);
        window.pad(30);
        window.setBackground(selectedOutline);

        Label title = new Label(type.getDisplayName(), titleStyle);
        title.setColor(Color.ORANGE);

        TextureRegion iconRegion = getSafeRegion(textures.get(type));
        Image icon = new Image(iconRegion);
        icon.setScaling(Scaling.fit);

        // --- ИСПОЛЬЗУЕМ НОВЫЙ КЛАСС ДЛЯ ТЕКСТА ---
        String descText = BuildingInfoProvider.getDetailedText(type);
        Label descLabel = new Label(descText, costStyle);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.center);

        Label closeLabel = new Label("(Click anywhere or press ESC to close)", costStyle);
        closeLabel.setColor(Color.GRAY);

        window.add(title).padBottom(20).row();
        window.add(icon).size(128, 128).padBottom(20).row();
        window.add(descLabel).width(400).padBottom(30).row();
        window.add(closeLabel).row();

        window.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        overlay.add(window);
        stage.addActor(overlay);

        stage.setKeyboardFocus(overlay);
    }

    private void updateInfoPanel(BuildingType type) {
        if (type == null) {
            infoTable.setVisible(false);
            infoCell.height(0).padBottom(0);
            windowTable.pack();
            return;
        }

        infoTable.setVisible(true);
        infoCell.height(90).padBottom(15);

        titleLabel.setText(type.getDisplayName());

        TextureRegion iconRegion = getSafeRegion(textures.get(type));
        if (type == BuildingType.HORIZONTAL_MINER) {
            iconRegion = getSafeRegion(textures.get("horizontal-miner-icon"));
        }
        infoIcon.setDrawable(new TextureRegionDrawable(iconRegion));

        costTable.clearChildren();

        if (type.getCost() != null) {
            for (Map.Entry<ItemType, Integer> entry : type.getCost().entrySet()) {
                ItemType item = entry.getKey();
                int required = entry.getValue();
                int current = inventory.getItems().getOrDefault(item, 0);

                TextureRegion itemRegion = getSafeRegion(textures.get(item));
                Image itemIcon = new Image(itemRegion);
                itemIcon.setScaling(Scaling.fit);

                Label nameLabel = new Label(item.getName() + " ", costStyle);
                nameLabel.setColor(Color.LIGHT_GRAY);

                Label amountLabel = new Label(current + "/" + required, costStyle);
                if (current < required) {
                    amountLabel.setColor(Color.RED);
                } else {
                    amountLabel.setColor(Color.WHITE);
                }

                costTable.add(itemIcon).size(20, 20).padRight(8).padBottom(3);
                costTable.add(nameLabel).left().padBottom(3);
                costTable.add(amountLabel).left().padBottom(3).row();
            }
        }

        windowTable.pack();
    }

    private TextureRegion getSafeRegion(GameSprite sprite) {
        if (sprite != null && sprite.getFirstFrame() != null) {
            return sprite.getFirstFrame();
        }
        return fallbackRegion;
    }

    public Stage getStage() {
        return stage;
    }

    public void update() {
        updateState(false);
    }

    private void updateState(boolean forceUpdate) {
        BuildingType currentSelected = buildTool.isActive() ? buildTool.getPreviewState().selectedType() : null;

        for (Map.Entry<BuildingType, Button> entry : buttons.entrySet()) {
            if (entry.getKey() == currentSelected) {
                entry.getValue().getStyle().up = selectedOutline;
            } else {
                entry.getValue().getStyle().up = null;
            }
        }

        boolean inventoryChanged = false;
        if (currentSelected != null && currentSelected.getCost() != null) {
            for (ItemType item : currentSelected.getCost().keySet()) {
                int currentAmt = inventory.getItems().getOrDefault(item, 0);
                if (lastKnownInventory.getOrDefault(item, -1) != currentAmt) {
                    inventoryChanged = true;
                    lastKnownInventory.put(item, currentAmt);
                }
            }
        }

        if (forceUpdate || currentSelected != lastSelectedType || inventoryChanged) {
            lastSelectedType = currentSelected;
            updateInfoPanel(currentSelected);
        }
    }

    @Override
    public void render() {
        stage.getViewport().apply();
        update();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        if (titleFont != null) titleFont.dispose();
        if (costFont != null) costFont.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (darkBgTexture != null) darkBgTexture.dispose();
        if (outlineTexture != null) outlineTexture.dispose();
    }
}
