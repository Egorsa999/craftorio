package io.github.craftorio.view.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.craftorio.model.building.BuildingType;
import io.github.craftorio.BalanceConfig;
import io.github.craftorio.model.building.defense.Turret;
import io.github.craftorio.model.entity.BulletType;
import io.github.craftorio.model.generator.ResourceType;
import io.github.craftorio.model.item.ItemType;
import io.github.craftorio.model.generator.TerrainType;
import io.github.craftorio.model.item.LiquidType;
import io.github.craftorio.view.TextureLoad;
import io.github.craftorio.view.sprite.GameSprite;

import java.util.Locale;

public class BuildingInfoWindow {

    private final Stage stage;
    private final TextureLoad textures;
    private final Label.LabelStyle titleStyle;
    private final Label.LabelStyle costStyle;
    private final Texture darkBgTexture;
    private final NinePatchDrawable selectedOutline;
    private final TextureRegion fallbackRegion;

    public BuildingInfoWindow(Stage stage, TextureLoad textures, Label.LabelStyle titleStyle,
                              Label.LabelStyle costStyle, Texture darkBgTexture,
                              NinePatchDrawable selectedOutline, TextureRegion fallbackRegion) {
        this.stage = stage;
        this.textures = textures;
        this.titleStyle = titleStyle;
        this.costStyle = costStyle;
        this.darkBgTexture = darkBgTexture;
        this.selectedOutline = selectedOutline;
        this.fallbackRegion = fallbackRegion;
    }

    public void show(BuildingType type) {
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
                    return true;
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

        String descText = BuildingInfoProvider.getDescription(type);
        Label descLabel = new Label(descText, costStyle);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.center);

        Table statsTable = new Table();
        statsTable.add(new Label("Size: " + type.getWidth() + "x" + type.getHeight(), costStyle)).left().row();
        statsTable.add(new Label("Max HP: " + type.getMaxHP(), costStyle)).left().row();
        statsTable.add(new Label("Walkable: " + (type.getWalkable() ? "Yes" : "No"), costStyle)).left().padBottom(15).row();

        if (type == BuildingType.MINER) {
            statsTable.add(new Label("Mining Speed:", costStyle)).left().padBottom(5).row();

            for (ResourceType resource : ResourceType.values()) {
                if (resource == ResourceType.NONE || resource.getDrop() == null) continue;

                float ticksRequired = (BalanceConfig.MINER_TICKS * resource.getMiningDifficulty());
                float itemsPerSecond = 1f / (ticksRequired / 60f);

                addResourceInfo(statsTable, resource.getDrop(), itemsPerSecond);
            }
        }

        if (type == BuildingType.HORIZONTAL_MINER){
            statsTable.add(new Label("Mining Speed:", costStyle)).left().padBottom(5).row();

            float ticksRequired = BalanceConfig.HORIZONTAL_MINER_TICKS;
            float itemsPerSecond = 1f / (ticksRequired / 60f);

            addResourceInfo(statsTable, ItemType.STONE, itemsPerSecond);
        }

        if (type == BuildingType.COAL_POWER_GENERATOR) {
            Label powerHeader = new Label("Power and Fuel:", costStyle);
            statsTable.add(powerHeader).left().padBottom(5).row();

            Label outputLabel = new Label("- Output: " + String.format(Locale.US, "%.0f W", BalanceConfig.COAL_GENERATOR_POWER_PRODUCTION), costStyle);
            outputLabel.setColor(Color.YELLOW);
            statsTable.add(outputLabel).left().padBottom(5).row();

            float consumeSeconds = BalanceConfig.COAL_GENERATOR_COAL_FRAME_TIME / 60f;
            float coalPerSecond = 1f / consumeSeconds;

            statsTable.add(new Label("- Consumes:", costStyle)).left().row();
            addResourceInfo(statsTable, ItemType.COAL, coalPerSecond);
        }

        if (type == BuildingType.ASSEMBLER || type == BuildingType.FURNACE || type == BuildingType.CHEMICAL_PLANT) {
            Label powerHeader = new Label("Power:", costStyle);
            statsTable.add(powerHeader).left().padBottom(5).row();

            float powerConsumption = 0f;
            switch (type) {
                case ASSEMBLER: powerConsumption = BalanceConfig.ASSEMBLER_POWER_CONSUMPTION; break;
                case FURNACE: powerConsumption = BalanceConfig.FURNACE_POWER_CONSUMPTION; break;
                case CHEMICAL_PLANT: powerConsumption = BalanceConfig.CHEMICAL_PLANT_POWER_CONSUMPTION; break;
                default: break;
            }

            Label consumeLabel = new Label("- Consumes: " + String.format(Locale.US, "%.0f W", powerConsumption), costStyle);
            consumeLabel.setColor(Color.YELLOW);
            statsTable.add(consumeLabel).left().padBottom(5).row();
        }

        if (type == BuildingType.BELT || type == BuildingType.JUNCTION || type == BuildingType.ROUTER) {
            statsTable.add(new Label("Logistics:", costStyle)).left().padBottom(5).row();

            statsTable.add(new Label("- Speed: " + String.format(Locale.US, "%.2f", BalanceConfig.CONVEYOR_SPEED / BalanceConfig.CONVEYOR_ITEM_SIZE) + " items/s", costStyle)).left().padBottom(5).row();
        }

        if (type == BuildingType.PUMP) {
            statsTable.add(new Label("Pumping Speed:", costStyle)).left().padBottom(5).row();

            for (TerrainType terrain : TerrainType.values()) {
                if (terrain.getLiquidType() == null) continue;

                float unitsPerSecond = BalanceConfig.PUMP_PRODUCTION_RATE * terrain.getRatio();
                addLiquidInfo(statsTable, terrain.getLiquidType(), unitsPerSecond);
            }
        }

        if (type == BuildingType.PIPE) {
            statsTable.add(new Label("Liquid Storage:", costStyle)).left().padBottom(5).row();
            statsTable.add(new Label("- Capacity: " + String.format(Locale.US, "%.1f", BalanceConfig.PIPE_CAPACITY) + " L", costStyle)).left().padBottom(5).row();
        }

        if (type == BuildingType.TURRET) {
            statsTable.add(new Label("Combat Stats:", costStyle)).left().padBottom(5).row();

            statsTable.add(new Label("- Range: " + String.format(Locale.US, "%.1f", BalanceConfig.TURRET_RANGE) + " blocks", costStyle)).left().row();

            float shotsPerSecond = 60f / BalanceConfig.TURRET_FIRE_COOLDOWN;
            statsTable.add(new Label("- Fire Rate: " + String.format(Locale.US, "%.1f", shotsPerSecond) + "/s", costStyle)).left().padBottom(10).row();

            statsTable.add(new Label("Accepted Ammo:", costStyle)).left().padBottom(5).row();

            for (BulletType bullet : Turret.getAcceptedAmmo()) {
                Table ammoRow = new Table();
                TextureRegion dropIcon = getSafeRegion(textures.get(bullet.getItemType()));

                ammoRow.add(new Image(dropIcon)).size(24, 24).padRight(10);

                String ammoText = bullet.getItemType().getName() + " (Dmg: " + bullet.getDamage() + ", Spd: " + String.format(Locale.US, "%.2f", bullet.getSpeed()) + ")";
                ammoRow.add(new Label(ammoText, costStyle)).left();

                statsTable.add(ammoRow).left().padBottom(5).row();
            }
        }

        if (type == BuildingType.LASER_TURRET) {
            statsTable.add(new Label("Combat Stats:", costStyle)).left().padBottom(5).row();

            statsTable.add(new Label("- Range: " + String.format(Locale.US, "%.1f", BalanceConfig.LASER_TURRET_RANGE) + " blocks", costStyle)).left().row();
            statsTable.add(new Label("- Damage: " + String.format(Locale.US, "%.0f", BalanceConfig.LASER_TURRET_DAMAGE) + " DPS", costStyle)).left().padBottom(10).row();

            statsTable.add(new Label("Power:", costStyle)).left().padBottom(5).row();

            Label consumeLabel = new Label("- Consumes: " + String.format(Locale.US, "%.0f W", BalanceConfig.LASER_TURRET_POWER_CONSUMPTION) + " (when firing)", costStyle);
            consumeLabel.setColor(Color.YELLOW);
            statsTable.add(consumeLabel).left().padBottom(5).row();
        }

        Label closeLabel = new Label("(Click anywhere or press ESC to close)", costStyle);
        closeLabel.setColor(Color.GRAY);

        window.add(title).padBottom(15).row();
        window.add(icon).size(128, 128).padBottom(15).row();
        window.add(descLabel).width(400).padBottom(20).row();
        window.add(statsTable).left().padBottom(25).row();
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

    private void addResourceInfo(Table statsTable, ItemType type, float itemsPerSecond){
        Table resourceRow = new Table();
        TextureRegion dropIcon = getSafeRegion(textures.get(type));

        resourceRow.add(new Image(dropIcon)).size(24, 24).padRight(10);
        resourceRow.add(new Label(type.getName() + ": " + String.format(Locale.US, "%.2f", itemsPerSecond) + "/s", costStyle)).left();
        statsTable.add(resourceRow).left().padBottom(5).row();
    }

    private void addLiquidInfo(Table statsTable, LiquidType type, float unitsPerSecond){
        Table liquidRow = new Table();
        TextureRegion liquidIcon = getSafeRegion(textures.get(type));

        liquidRow.add(new Image(liquidIcon)).size(24, 24).padRight(10);
        liquidRow.add(new Label(type.getName() + ": " + String.format(Locale.US, "%.2f", unitsPerSecond) + "/s", costStyle)).left();
        statsTable.add(liquidRow).left().padBottom(5).row();
    }

    private TextureRegion getSafeRegion(GameSprite sprite) {
        if (sprite != null && sprite.getFirstFrame() != null) {
            return sprite.getFirstFrame();
        }
        return fallbackRegion;
    }
}
