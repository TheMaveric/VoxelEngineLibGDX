package com.kushagra.game.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.kushagra.game.engine.application.Player;
import com.kushagra.game.engine.domain.Chunk;
import com.kushagra.game.engine.domain.OpenSimplexNoise;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class VoxelEngineApplication extends ApplicationAdapter {
    private static final int CHUNK_SIZE = 1;
    private static final int RENDER_DISTANCE = 4;
    private static final float MOVE_SPEED = 10f;
    private static final Long seed = 0L;
    private static final float GRAVITY = -10f;
    private static OpenSimplexNoise openSimplexNoise;
    private Environment environment;
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Set<ModelInstance> instances;
    private CameraInputController cameraController;
    private Set<Chunk> chunks;
    private Player player;

    private FPSLogger fpsLogger;

    @Override
    public void create() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.7f, 0.7f, 0.7f, 1f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(1f, 1f, 1f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        modelBatch = new ModelBatch();

        instances = new HashSet<>();

        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.set(Color.WHITE, 1f, -1f, -0.5f); // Set light color and direction
        environment.add(directionalLight);

        openSimplexNoise = new OpenSimplexNoise();

        player = new Player(camera.position.cpy(), MOVE_SPEED);

        fpsLogger = new com.badlogic.gdx.graphics.FPSLogger();
        generateWorld();
    }

    private void generateWorld() {
        chunks = new HashSet<>();
        for (int x = -RENDER_DISTANCE; x <= RENDER_DISTANCE; x++) {
            for (int y = -RENDER_DISTANCE; y <= RENDER_DISTANCE; y++) {
                for (int z = -RENDER_DISTANCE; z <= RENDER_DISTANCE; z++) {
                    float posX = x * CHUNK_SIZE;
                    float posY = y * CHUNK_SIZE;
                    float posZ = z * CHUNK_SIZE;
                    Vector3 vector = new Vector3(posX, posY, posZ);
                    vector.sub(camera.position);
                    Chunk chunk = new Chunk(CHUNK_SIZE, posX, posY, posZ, openSimplexNoise, 31415629L);
                    chunks.add(chunk);
                }
            }
        }
        for (Chunk chunk : chunks) {
            chunk.getChunkModelInstances().forEach(modelInstance ->
                    instances.add(modelInstance));
        }
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        Vector3 cameraDirection = camera.direction;
        Vector3 up = new Vector3(0, 1, 0);
        player.setGrounded(false);
        player.updatePosition(cameraDirection, up, deltaTime);
        player.applyGravity(GRAVITY, deltaTime);
        //player.updatePosition(camera.direction, camera.up.cpy().crs(camera.direction).nor(), deltaTime);
        camera.position.set(player.getPosition());
        camera.update();

        // Update the light position to match the player's position
        PointLight pointLight = new PointLight();
        pointLight.set(Color.WHITE, player.getPosition(), 10f); // Set light color, position, and intensity
        environment.clear();
        environment.add(pointLight);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Check if any chunks have changed and regenerate the world
        if (hasChunksChanged()) {
            System.out.println("BANA RHA HU");
            regenerateWorld();
        }

        modelBatch.begin(camera);

        for (ModelInstance instance : instances) {
            if (isVisible(instance)) {
                modelBatch.render(instance, environment);
            }
        }

        modelBatch.end();

        fpsLogger.log();
    }

    private void regenerateWorld() {
        instances.clear();
        generateWorld();

        for (Chunk chunk : chunks) {
            chunk.setChanged(false);
        }
    }

    private boolean hasChunksChanged() {
        for (Chunk chunk : chunks) {
            if (chunk.hasChanged()) {
                return true;
            }
        }
        return false;
    }

    private boolean isVisible(ModelInstance instance) {
        Vector3 cameraPosition = camera.position;
        Vector3 instancePosition = instance.transform.getTranslation(new Vector3());
        float distance = cameraPosition.dst(instancePosition);
        return distance <= RENDER_DISTANCE * CHUNK_SIZE;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }
}
