package view.renderer;

import controller.event.EventBus;
import controller.event.EventType;
import controller.event.GameEvent;
import controller.event.RenderEvent;
import model.block.Block;
import model.block.BlockType;
import model.player.Camera;
import model.world.Frustum;
import model.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import view.shader.ShaderProgram;
import view.window.WindowManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Orchestrates rendering of game world, blocks, effects and HUD.
 * Manages shader programs, textures and view matrices for rendering pipeline.
 */
public class MasterRenderer implements WorldRenderer {
    /** Shader programs for different rendering passes */
    private ShaderProgram blockShader;
    private ShaderProgram highlightShader;
    private ShaderProgram breakingShader;

    /** View matrices */
    private Matrix4f projectionMatrix;
    private final Matrix4f modelMatrix;
    private final Matrix4f projectionViewMatrix = new Matrix4f();

    /** Texture management */
    private final TextureManager textureManager;
    private final Map<BlockType, Integer> blockTextureIds = new HashMap<>();

    /** Mesh storage */
    private final Map<BlockType, BatchedMesh> blockMeshes = new HashMap<>();
    private final BatchedMesh highlightMesh;
    private final BatchedMesh breakingMesh;

    /** Render components */
    private final HUDRenderer hudRenderer;
    private final WindowManager windowManager;
    private final Frustum frustum = new Frustum();


    /**
     * Initializes renderer components and subscribes to render events
     */
    public MasterRenderer(WindowManager windowManager) {
        this.windowManager = windowManager;
        EventBus.getInstance().subscribe(EventType.RENDER, this::onEvent);

        // Initialize shaders
        initializeShaders();

        // Create meshes
        highlightMesh = new BatchedMesh();
        breakingMesh = new BatchedMesh();

        // Setup textures and state
        textureManager = new TextureManager();
        updateProjectionMatrix();
        modelMatrix = new Matrix4f().identity();

        // Configure OpenGL state
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);

        hudRenderer = new HUDRenderer();
    }

    private void initializeShaders() {
        blockShader = new ShaderProgram("resources/shaders/block_vertex.glsl",
                "resources/shaders/block_fragment.glsl");
        highlightShader = new ShaderProgram("resources/shaders/block_highlight_vertex.glsl",
                "resources/shaders/block_highlight_fragment.glsl");
        breakingShader = new ShaderProgram("resources/shaders/block_breaking_vertex.glsl",
                "resources/shaders/block_breaking_fragment.glsl");
    }

    private void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(
            (float) Math.toRadians(60.0f),
            windowManager.getAspectRatio(),
            0.3f,
            100.0f
        );
    }

    /**
     * Cleans up renderer resources
     */
    public void cleanUp() {
        blockMeshes.values().forEach(BatchedMesh::cleanup);
        textureManager.cleanup();
        blockShader.cleanup();
        highlightShader.cleanup();
        breakingShader.cleanup();
        hudRenderer.cleanUp();
        highlightMesh.cleanup();
        breakingMesh.cleanup();
    }

    /**
     * Handles render events
     */
    public void onEvent(GameEvent event) {
        if (event instanceof RenderEvent renderEvent) {
            render(renderEvent.blocks(), renderEvent.camera(), renderEvent.world());
            renderEvent.world().update(renderEvent.camera().getPosition(), projectionViewMatrix);
        }
    }

    /**
     * Performs complete render pass of world, effects and HUD
     */
    @Override
    public void render(List<Block> blocks, Camera camera, World world) {
        updateProjectionMatrix();
        prepareFrame();

        Matrix4f viewMatrix = camera.getViewMatrix();
        updateProjectionView(viewMatrix);
        float ambientLight = world.getDayNightCycle().getAmbientLight();

        // Render phases
        renderBreakingEffects(blocks, viewMatrix, ambientLight);
        renderBlocks(blocks, viewMatrix, ambientLight);
        renderHighlights(blocks, viewMatrix);
        renderHUD();
    }

    private void prepareFrame() {
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void updateProjectionView(Matrix4f viewMatrix) {
        projectionViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustum.update(projectionViewMatrix);
    }

    private void renderBreakingEffects(List<Block> blocks, Matrix4f viewMatrix, float ambientLight) {
        breakingShader.start();
        setupBreakingShader(viewMatrix, ambientLight);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderBreakingBlocks(blocks);

        breakingShader.stop();
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void setupBreakingShader(Matrix4f viewMatrix, float ambientLight) {
        breakingShader.loadMatrix("viewMatrix", viewMatrix);
        breakingShader.loadMatrix("projectionMatrix", projectionMatrix);
        breakingShader.loadMatrix("modelMatrix", modelMatrix);
        GL20.glUniform1f(GL20.glGetUniformLocation(breakingShader.getProgramID(), "ambientLight"),
                ambientLight);
    }

    private void renderBreakingBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getBreakProgress() > 0) {
                renderBreakingBlock(block);
            }
        }
    }

    private void renderBreakingBlock(Block block) {
        BatchedMesh breakMesh = new BatchedMesh();
        breakMesh.addBlockMesh(block.getVertices(), block.getIndices(), 0, block.getLightLevel());
        breakMesh.updateGLBuffers();

        textureManager.bindTexture(blockTextureIds.get(block.getType()), 0);
        GL20.glUniform1f(GL20.glGetUniformLocation(breakingShader.getProgramID(), "breakProgress"), block.getBreakProgress());

        breakMesh.render();
        breakMesh.cleanup();
    }

    private void renderBlocks(List<Block> blocks, Matrix4f viewMatrix, float ambientLight) {
        blockShader.start();
        setupBlockShader(viewMatrix, ambientLight);

        ensureBlockTextures();

        Map<BlockType, List<Block>> blocksByType = blocks.stream()
            .filter(this::isBlockInView)
            .collect(Collectors.groupingBy(Block::getType));

        renderBlockBatches(blocksByType);

        blockShader.stop();
    }

    private void setupBlockShader(Matrix4f viewMatrix, float ambientLight) {
        blockShader.loadMatrix("viewMatrix", viewMatrix);
        blockShader.loadMatrix("projectionMatrix", projectionMatrix);
        blockShader.loadMatrix("modelMatrix", modelMatrix);
        GL20.glUniform1f(GL20.glGetUniformLocation(blockShader.getProgramID(), "ambientLight"), ambientLight);
    }

    private boolean isBlockInView(Block block) {
        Vector3f pos = block.getPosition();
        return frustum.isBoxInFrustum(pos.x(), pos.y(), pos.z(), 1.0f);
    }

    private void ensureBlockTextures() {
        for (BlockType type : BlockType.values()) {
            if (!blockTextureIds.containsKey(type)) {
                blockTextureIds.put(type, textureManager.loadTexture(type.getTexturePath()));
            }
        }
    }

    private void renderBlockBatches(Map<BlockType, List<Block>> blocksByType) {
        blocksByType.forEach((type, typeBlocks) -> {
            BatchedMesh mesh = blockMeshes.computeIfAbsent(type, k -> new BatchedMesh());
            mesh.clear();

            int vertexOffset = 0;
            for (Block block : typeBlocks) {
                mesh.addBlockMesh(block.getVertices(), block.getIndices(), vertexOffset, block.getLightLevel());
                vertexOffset += block.getVertices().length / 5;
            }

            mesh.updateGLBuffers();
            textureManager.bindTexture(blockTextureIds.get(type), 0);
            mesh.render();
        });
    }

    private void renderHighlights(List<Block> blocks, Matrix4f viewMatrix) {
        highlightMesh.clear();
        blocks.stream()
            .filter(Block::isHighlighted)
            .forEach(block -> highlightMesh.addBlockMesh(block.getVertices(), block.getIndices(), 0, block.getLightLevel()
            ));

        if (blocks.stream().anyMatch(Block::isHighlighted)) {
            setupHighlightRendering(viewMatrix);
            highlightMesh.updateGLBuffers();
            highlightMesh.render();
            restoreHighlightRendering();
        }
    }

    private void setupHighlightRendering(Matrix4f viewMatrix) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glLineWidth(3.0f);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        highlightShader.start();
        highlightShader.loadMatrix("viewMatrix", viewMatrix);
        highlightShader.loadMatrix("projectionMatrix", projectionMatrix);
        highlightShader.loadMatrix("modelMatrix", modelMatrix);
    }

    private void restoreHighlightRendering() {
        highlightShader.stop();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderHUD() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        hudRenderer.render();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}