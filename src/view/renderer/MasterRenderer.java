package view.renderer;

import controller.event.*;
import model.block.*;
import model.player.Camera;
import model.world.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import util.PerformanceMetrics;
import view.shader.ShaderProgram;
import view.window.WindowManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core rendering system managing all graphics pipelines
 * Coordinates shaders, meshes, textures and render passes
 */
public class MasterRenderer implements WorldRenderer {
    /** OpenGL shading programs */
    private ShaderProgram blockShader;
    private ShaderProgram highlightShader;
    private ShaderProgram breakingShader;

    /** View transformation matrices */
    private Matrix4f projectionMatrix;
    private final Matrix4f modelMatrix = new Matrix4f().identity();
    private final Matrix4f projectionViewMatrix = new Matrix4f();

    /** Asset managers */
    private final TextureManager textureManager;
    private final Map<BlockType, Integer> blockTextureIds = new HashMap<>();

    /** Geometric data */
    private final Map<BlockType, BatchedMesh> blockMeshes = new HashMap<>();
    private final BatchedMesh highlightMesh = new BatchedMesh();
    private final BatchedMesh breakingMesh = new BatchedMesh();

    /** Render subsystems */
    private final HUDRenderer hudRenderer;
    private final WindowManager windowManager;
    private final Frustum frustum = new Frustum();


    /**
     * Creates renderer and initializes graphics subsystems
     */
    public MasterRenderer(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.textureManager = new TextureManager();
        this.hudRenderer = new HUDRenderer();

        initializeOpenGL();
        initializeShaders();
        updateProjectionMatrix();

        EventBus.getInstance().subscribe(EventType.RENDER, this::onEvent);
    }

    /**
     * Sets up OpenGL state and features
     */
    private void initializeOpenGL() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);
    }

    /**
     * Initializes shader programs
     */
    private void initializeShaders() {
        blockShader = new ShaderProgram(
                "resources/shaders/block_vertex.glsl",
                "resources/shaders/block_fragment.glsl"
        );

        highlightShader = new ShaderProgram(
                "resources/shaders/block_highlight_vertex.glsl",
                "resources/shaders/block_highlight_fragment.glsl"
        );

        breakingShader = new ShaderProgram(
                "resources/shaders/block_breaking_vertex.glsl",
                "resources/shaders/block_breaking_fragment.glsl"
        );
    }

    /**
     * Updates perspective projection
     */
    private void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(60.0f),
                windowManager.getAspectRatio(),
                0.3f,
                100.0f
        );
    }

    /**
     * Handles incoming render events
     */
    public void onEvent(GameEvent event) {
        if (event instanceof RenderEvent renderEvent) {
            render(renderEvent.blocks(), renderEvent.camera(), renderEvent.world());
            renderEvent.world().update(renderEvent.camera().getPosition(), projectionViewMatrix);
        }
    }

    /**
     * Renders complete frame including world, effects and HUD
     */
    @Override
    public void render(List<Block> blocks, Camera camera, World world) {
        updateProjectionMatrix();
        PerformanceMetrics.startFrame();
        prepareFrame();

        Matrix4f viewMatrix = camera.getViewMatrix();
        updateProjectionView(viewMatrix);
        float ambientLight = world.getDayNightCycle().getAmbientLight();

        renderBreakingEffects(blocks, viewMatrix, ambientLight);
        renderBlocks(blocks, viewMatrix, ambientLight);
        renderHighlights(blocks, viewMatrix);
        renderHUD();
    }

    /**
     * Clears framebuffer and sets background
     */
    private void prepareFrame() {
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Updates view matrices and frustum
     */
    private void updateProjectionView(Matrix4f viewMatrix) {
        projectionViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustum.update(projectionViewMatrix);
    }

    /**
     * Renders blocks with breaking animation
     */
    private void renderBreakingEffects(List<Block> blocks, Matrix4f viewMatrix, float ambientLight) {
        breakingShader.start();
        setupBreakingShader(viewMatrix, ambientLight);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderBreakingBlocks(blocks);

        breakingShader.stop();
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Configures breaking effect shader
     */
    private void setupBreakingShader(Matrix4f viewMatrix, float ambientLight) {
        breakingShader.loadMatrix("viewMatrix", viewMatrix);
        breakingShader.loadMatrix("projectionMatrix", projectionMatrix);
        breakingShader.loadMatrix("modelMatrix", modelMatrix);
        GL20.glUniform1f(
                GL20.glGetUniformLocation(breakingShader.getProgramID(), "ambientLight"),
                ambientLight
        );
    }

    /**
     * Renders blocks with active break progress
     */
    private void renderBreakingBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            if (block.getBreakProgress() > 0) {
                renderBreakingBlock(block);
            }
        }
    }

    /**
     * Renders single breaking block effect
     */
    private void renderBreakingBlock(Block block) {
        BatchedMesh breakMesh = new BatchedMesh();
        breakMesh.addBlockMesh(block.getVertices(), block.getIndices(), 0, block.getLightLevel());
        breakMesh.updateGLBuffers();

        textureManager.bindTexture(blockTextureIds.get(block.getType()), 0);
        GL20.glUniform1f(
                GL20.glGetUniformLocation(breakingShader.getProgramID(), "breakProgress"),
                block.getBreakProgress()
        );

        breakMesh.render();
        breakMesh.cleanup();
    }

    /**
     * Renders world blocks with batching
     */
    private void renderBlocks(List<Block> blocks, Matrix4f viewMatrix, float ambientLight) {
        blockShader.start();
        setupBlockShader(viewMatrix, ambientLight);
        ensureBlockTextures();

        Map<BlockType, List<Block>> blocksByType = blocks.stream()
                .filter(this::isBlockInView)
                .collect(Collectors.groupingBy(Block::getType));

        renderBlockBatches(blocksByType);
        PerformanceMetrics.updateFrameMetrics();

        blockShader.stop();
    }

    /**
     * Configures block rendering shader
     */
    private void setupBlockShader(Matrix4f viewMatrix, float ambientLight) {
        blockShader.loadMatrix("viewMatrix", viewMatrix);
        blockShader.loadMatrix("projectionMatrix", projectionMatrix);
        blockShader.loadMatrix("modelMatrix", modelMatrix);
        GL20.glUniform1f(
                GL20.glGetUniformLocation(blockShader.getProgramID(), "ambientLight"),
                ambientLight
        );
    }

    /**
     * Checks if block is within view frustum
     */
    private boolean isBlockInView(Block block) {
        Vector3f pos = block.getPosition();
        boolean inView = frustum.isBoxInFrustum(pos.x(), pos.y(), pos.z(), 1.0f);
        PerformanceMetrics.logChunk(!inView);
        return inView;
    }

    /**
     * Loads and caches block textures
     */
    private void ensureBlockTextures() {
        for (BlockType type : BlockType.values()) {
            blockTextureIds.computeIfAbsent(type,
                    t -> textureManager.loadTexture(t.getTexturePath())
            );
        }
    }

    /**
     * Renders batched blocks by type
     */
    private void renderBlockBatches(Map<BlockType, List<Block>> blocksByType) {
        blocksByType.forEach((type, typeBlocks) -> {
            BatchedMesh mesh = blockMeshes.computeIfAbsent(type, k -> new BatchedMesh());
            mesh.clear();

            int vertexOffset = 0;
            for (Block block : typeBlocks) {
                mesh.addBlockMesh(
                        block.getVertices(),
                        block.getIndices(),
                        vertexOffset,
                        block.getLightLevel()
                );
                vertexOffset += block.getVertices().length / 5;
            }

            mesh.updateGLBuffers();
            textureManager.bindTexture(blockTextureIds.get(type), 0);
            PerformanceMetrics.logBlocks(typeBlocks.size(), typeBlocks.size(), 0);
            mesh.render();
        });
    }

    /**
     * Renders block highlights
     */
    private void renderHighlights(List<Block> blocks, Matrix4f viewMatrix) {
        highlightMesh.clear();
        blocks.stream()
                .filter(Block::isHighlighted)
                .forEach(block -> highlightMesh.addBlockMesh(
                        block.getVertices(),
                        block.getIndices(),
                        0,
                        block.getLightLevel()
                ));

        if (blocks.stream().anyMatch(Block::isHighlighted)) {
            setupHighlightRendering(viewMatrix);
            highlightMesh.updateGLBuffers();
            highlightMesh.render();
            restoreHighlightRendering();
        }
    }

    /**
     * Configures highlight rendering state
     */
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

    /**
     * Restores OpenGL state after highlights
     */
    private void restoreHighlightRendering() {
        highlightShader.stop();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Renders HUD overlay
     */
    private void renderHUD() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        hudRenderer.render();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Cleans up graphics resources
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
}