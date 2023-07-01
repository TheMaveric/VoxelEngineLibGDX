package com.kushagra.game.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.kushagra.game.engine.application.player.Player;
import com.kushagra.game.engine.domain.chunk.Chunk;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;
import static com.kushagra.game.engine.domain.chunk.Chunk.CHUNK_SIZE;

@Slf4j
public class VoxelEngineApplication implements ApplicationListener {

    private static final int WORLD_SIZE = 8;
    private static final int WORLD_HEIGHT = 255;
    private static final int RENDER_DISTANCE = CHUNK_SIZE * 4;
    private static final Long seed = 31415628L;
    private final Vector3 tmpVec = new Vector3(); // Reusable Vector3 instance for center calculations
    private Environment environment;
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Array<ModelInstance> instances;
    private CameraInputController cameraController;
    private Array<Chunk> chunks;
    private Player player;
    private FPSLogger fpsLogger;
    private Frustum cameraFrustum;
    private ObjectPool<Vector3> vector3Pool;
    private ObjectPool<BoundingBox> boundingBoxPool;
    private ObjectPool<Matrix4> matrix4Pool;
    private ModelBuilder modelBuilder;
    private int currentPlayerChunkX;
    private int currentPlayerChunkY;
    private int currentPlayerChunkZ;

    //private PerspectiveCamera viewModelCamera;
    //private Viewport viewModelViewport;

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(1f, 1f, 1f);
        //camera.position.set(player.getPosition());
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = RENDER_DISTANCE;
        camera.update();
        cameraFrustum = camera.frustum;
        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);


        //viewModelCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //viewModelViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), viewModelCamera);


        modelBatch = new ModelBatch();

        instances = new Array<>();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.7f, 0.7f, 0.7f, 1f));
        // Add sunlight
        DirectionalLight directionalSunlight = new DirectionalLight();
        directionalSunlight.set(Color.WHITE, new Vector3(-1f, -0.8f, -0.2f)); // Set light color and direction
        environment.add(directionalSunlight);

        modelBuilder = new ModelBuilder();

        Model playerModel = modelBuilder.createSphere(1f, 1f, 1f, 16, 16, new Material(ColorAttribute.createDiffuse(Color.RED)), Position | VertexAttributes.Usage.Normal);
        player = new Player(camera.position.cpy(), playerModel, camera);
        ModelInstance playerInstance = new ModelInstance(playerModel);
        player.setViewModelInstance(playerInstance);
        instances.add(playerInstance);
        fpsLogger = new FPSLogger();

        vector3Pool = new ObjectPool<>(64, Vector3::new); // Initialize vector3Pool
        boundingBoxPool = new ObjectPool<>(64, BoundingBox::new); // Initialize boundingBoxPool
        matrix4Pool = new ObjectPool<>(64, Matrix4::new);
        chunks = new Array<>();

        //FileHandle file = Gdx.files.internal("save.txt");
        //String save;
        initializeWorld(0,0,0);
        /*try {
            save = file.readString();
            initializeWorld(Integer.parseInt(save.split(" ")[0]), Integer.parseInt(save.split(" ")[1]), Integer.parseInt(save.split(" ")[2]));
        } catch (Exception e) {
            save = generateFullWorld();
            try {
                FileUtils.writeStringToFile(new File("save.txt"), save, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }*/
    }

    private String generateFullWorld() {
        Array<Chunk> newChunks = new Array<>();

        for (int x = -WORLD_SIZE / (2 * CHUNK_SIZE); x <= WORLD_SIZE / (2 * CHUNK_SIZE); x++) {
            for (int y = -WORLD_HEIGHT / (2 * CHUNK_SIZE); y <= WORLD_HEIGHT / (2 * CHUNK_SIZE); y++) {
                for (int z = -WORLD_SIZE / (2 * CHUNK_SIZE); z <= WORLD_SIZE / (2 * CHUNK_SIZE); z++) {
                    boolean chunkExists = false;

                    for (Chunk chunk : chunks) {
                        Vector3 chunkCorner = new Vector3(x * CHUNK_SIZE, y * CHUNK_SIZE, z * CHUNK_SIZE);
                        if (chunkCorner.equals(chunk.getPosition())) {
                            newChunks.add(chunk);
                            chunkExists = true;
                            break;
                        }
                    }

                    if (!chunkExists) {
                        Chunk chunk = new Chunk(CHUNK_SIZE, CHUNK_SIZE, x * CHUNK_SIZE, y * CHUNK_SIZE, z * CHUNK_SIZE, seed, modelBuilder);
                        chunks.add(chunk);
                    }
                }
            }
        }

        instances.clear();
        for (Chunk chunk : chunks) {
            chunk.getChunkModelInstances().forEach(modelInstance -> {
                if (isVisible(modelInstance)) {
                    instances.add(modelInstance);
                }
            });
        }

        // Update the current player chunk position
        int playerChunkX = (int) (player.getPosition().x / CHUNK_SIZE);
        int playerChunkY = (int) (player.getPosition().y / CHUNK_SIZE);
        int playerChunkZ = (int) (player.getPosition().z / CHUNK_SIZE);
        currentPlayerChunkX = playerChunkX;
        currentPlayerChunkY = playerChunkY;
        currentPlayerChunkZ = playerChunkZ;
        return currentPlayerChunkX + " " + currentPlayerChunkY + " " + currentPlayerChunkZ;
    }

    private void initializeWorld(int playerX, int playerY, int playerZ) {
        int playerChunkX = (playerX / CHUNK_SIZE);
        int playerChunkY = (playerY / CHUNK_SIZE);
        int playerChunkZ = (playerZ / CHUNK_SIZE);
        generateWorld(playerChunkX, playerChunkY, playerChunkZ);
    }

    private void updateWorld() {
        int playerChunkX = (int) (player.getPosition().x / CHUNK_SIZE);
        int playerChunkY = (int) (player.getPosition().y / CHUNK_SIZE);
        int playerChunkZ = (int) (player.getPosition().z / CHUNK_SIZE);

        //if (playerChunkX != currentPlayerChunkX || playerChunkY != currentPlayerChunkY || playerChunkZ != currentPlayerChunkZ) {
        generateWorld(playerChunkX, playerChunkY, playerChunkZ);
        //}
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }


    private void generateWorld(int playerChunkX, int playerChunkY, int playerChunkZ) {
        Array<Chunk> newChunks = new Array<>();
        for (int x = playerChunkX - 1; x <= playerChunkX + 1; x++) {
            for (int z = playerChunkZ - 1; z <= playerChunkZ + 1; z++) {
                boolean chunkExists = false;
                for (Chunk chunk : chunks) {
                    Vector3 chunkCorner = new Vector3(x * CHUNK_SIZE, 0, z * CHUNK_SIZE);
                    if (chunkCorner.equals(chunk.getPosition())) {
                        newChunks.add(chunk);
                        chunkExists = true;
                        break;
                    }
                }

                if (!chunkExists) {
                    Chunk chunk = new Chunk(CHUNK_SIZE, CHUNK_SIZE, x * CHUNK_SIZE, 0, z * CHUNK_SIZE, seed, modelBuilder);
                    chunks.add(chunk);
                }
            }
        }

        instances.clear();
        for (Chunk chunk : newChunks) {
            chunk.getChunkModelInstances().forEach(modelInstance -> {
                if (isVisible(modelInstance)) {
                    instances.add(modelInstance);
                }
            });
        }

        currentPlayerChunkX = playerChunkX;
        currentPlayerChunkY = playerChunkY;
        currentPlayerChunkZ = playerChunkZ;
        try {
            FileUtils.writeStringToFile(new File("save.txt"), currentPlayerChunkX + " " + currentPlayerChunkY + " " + currentPlayerChunkZ, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Update player's physics
        player.update(deltaTime, instances);

        // Update the camera position to match the player's position
        camera.position.set(player.getPosition());
        camera.update();

        // Update the frustum for culling
        cameraFrustum = camera.frustum;

        // Check if the player has changed chunks
        updateWorld();

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        // Clear the screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        //GL40.glEnable(GL40.GL_CULL_FACE);
        //GL40.glCullFace(GL40.GL_BACK);
        // Render the player's model
        //modelBatch.begin(viewModelCamera);
        //modelBatch.render(player.getViewModelInstance());
        //modelBatch.end();
        // Render the models
        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();


        // Log the FPS
        fpsLogger.log();

        //System.out.println(player.getPosition());
        //System.out.println(player.getModelInstance().transform);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    private boolean isVisible(ModelInstance instance) {
        // Get the instance's transformation matrix
        Matrix4 instanceTransform = matrix4Pool.obtain().set(instance.transform);

        // Get the instance's bounding box
        BoundingBox instanceBounds = boundingBoxPool.obtain().set(instance.calculateBoundingBox(new BoundingBox()));
        instanceBounds.mul(instanceTransform);

        // Get the center of the instance's bounding box
        Vector3 instanceCenter = instanceBounds.getCenter(tmpVec);

        // Calculate the distance between the instance's center and the camera position
        float distance = camera.position.dst(instanceCenter);

        // Check if the distance is within the rendering range
        if (distance > (float) RENDER_DISTANCE) {
            matrix4Pool.free(instanceTransform);
            boundingBoxPool.free(instanceBounds);
            return false; // Distance is greater than the rendering range, not visible
        }

        // Check if the bounding box intersects with the camera frustum
        if (!cameraFrustum.boundsInFrustum(instanceBounds)) {
            matrix4Pool.free(instanceTransform);
            boundingBoxPool.free(instanceBounds);
            return false; // Bounding box is outside the frustum, not visible
        }

        // Perform face culling
        if (shouldCullFaces(instanceTransform, instanceBounds)) {
            matrix4Pool.free(instanceTransform);
            boundingBoxPool.free(instanceBounds);
            return false; // Faces should be culled, not visible
        }

        matrix4Pool.free(instanceTransform);
        boundingBoxPool.free(instanceBounds);
        return true; // Faces should be rendered, visible
    }


    private boolean shouldCullFaces(Matrix4 transform, BoundingBox bounds) {
        // Get the transformed direction vectors of the camera
        Vector3 cameraForward = new Vector3(0, 0, -1).rot(transform).nor();
        Vector3 cameraUp = new Vector3(0, 1, 0).rot(transform).nor();
        Vector3 cameraRight = new Vector3(1, 0, 0).rot(transform).nor();

        // Get the center of the bounding box
        Vector3 center = bounds.getCenter(tmpVec);

        // Calculate the vector from the camera to the center of the bounding box
        Vector3 cameraToCenter = tmpVec.set(center).sub(camera.position).nor();

        // Calculate the dot products between the camera direction vectors and the vector to the center of the bounding box
        float dotForward = cameraForward.dot(cameraToCenter);
        float dotUp = cameraUp.dot(cameraToCenter);
        float dotRight = cameraRight.dot(cameraToCenter);

        // Check if the dot products indicate that the faces should be culled
        return dotForward >= 0 && dotUp >= 0 && dotRight >= 0;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
    }
}
