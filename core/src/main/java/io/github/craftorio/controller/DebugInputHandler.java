package io.github.craftorio.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import io.github.craftorio.model.enemy.EnemyType;
import io.github.craftorio.model.enemy.WaveSpawner;
import io.github.craftorio.ui.BuildTool;
import io.github.craftorio.view.CameraManager;

public class DebugInputHandler extends InputAdapter {
    private final WaveSpawner waveSpawner;
    private final CameraManager camera;
    private final BuildTool buildTool;

    public DebugInputHandler(WaveSpawner waveSpawner, CameraManager camera, BuildTool buildTool) {
        this.waveSpawner = waveSpawner;
        this.camera = camera;
        this.buildTool = buildTool;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT && !buildTool.isActive()) {
            Vector3 worldCoords = camera.getCamera().unproject(new Vector3(screenX, screenY, 0));
            waveSpawner.spawnEnemy(worldCoords.x, worldCoords.y, EnemyType.FAT_ENEMY);
//            waveSpawner.spawnWave(2);
            return false;
        }
        return false;
    }
}
