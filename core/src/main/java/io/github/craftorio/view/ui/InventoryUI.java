package io.github.craftorio.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.craftorio.ui.Inventory;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.view.TextureLoad;

import java.util.Map;



public class InventoryUI implements UIRenderer{

    private Stage stage;
    private Table windowTable;
    private Inventory inventory;
    private Label.LabelStyle labelStyle;
    private Texture bgTexture;
    private TextureLoad textures;

    private BitmapFont customFont;

    public InventoryUI(TextureLoad textures, Inventory inventory) {
        this.inventory = inventory;
        this.textures = textures;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 25;
        parameter.color = Color.WHITE;

        this.customFont = generator.generateFont(parameter);
        generator.dispose();

        this.stage = new Stage(new ScreenViewport());

        this.labelStyle = new Label.LabelStyle(customFont, Color.WHITE);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left();

        windowTable = new Table();
        windowTable.pad(15);


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        pixmap.dispose();

        windowTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

        rootTable.add(windowTable).pad(20);

        stage.addActor(rootTable);
    }

    public void update() {
        windowTable.clearChildren();

        Label title = new Label("Inventory", labelStyle);
        windowTable.add(title).colspan(3).center().padBottom(10).row();

        Map<ItemType, Integer> items = inventory.getItems();

        if (items.isEmpty()) {
            windowTable.add(new Label("Empty", labelStyle)).colspan(3).center().row();
        } else {
            for (Map.Entry<ItemType, Integer> entry : items.entrySet()) {
                String itemName = entry.getKey().getName() + ":";
                String itemCount = String.valueOf(entry.getValue());

                TextureRegion region = textures.get(entry.getKey()).getFirstFrame();

                Image icon = new Image(region);


                Label nameLabel = new Label(itemName, labelStyle);
                Label countLabel = new Label(itemCount, labelStyle);

                windowTable.add(icon).size(30, 30).padRight(8);
                windowTable.add(nameLabel).left().padRight(15);
                windowTable.add(countLabel).right().row();
            }
        }
    }

    public void render() {
        stage.getViewport().apply();
        update();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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

        if (customFont != null) {
            customFont.dispose();
        }
    }
}
