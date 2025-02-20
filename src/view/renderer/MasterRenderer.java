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
 *
 * @see view.View
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
            render(renderEvent.abstractBlocks(), renderEvent.camera(), renderEvent.world());
            renderEvent.world().update(renderEvent.camera().getPosition(), projectionViewMatrix);
        }
    }

    /**
     * Renders complete frame including world, effects and HUD
     */
    @Override
    public void render(List<AbstractBlock> abstractBlocks, Camera camera, World world) {
        updateProjectionMatrix();
        prepareFrame();

        Matrix4f viewMatrix = camera.getViewMatrix();
        updateProjectionView(viewMatrix);
        float ambientLight = world.getDayNightCycle().getAmbientLight();

        renderBreakingEffects(abstractBlocks, viewMatrix, ambientLight);
        renderBlocks(abstractBlocks, viewMatrix, ambientLight);
        renderHighlights(abstractBlocks, viewMatrix);
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
    private void renderBreakingEffects(List<AbstractBlock> abstractBlocks, Matrix4f viewMatrix, float ambientLight) {
        breakingShader.start();
        setupBreakingShader(viewMatrix, ambientLight);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderBreakingBlocks(abstractBlocks);

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
    private void renderBreakingBlocks(List<AbstractBlock> abstractBlocks) {
        for (AbstractBlock abstractBlock : abstractBlocks) {
            if (abstractBlock.getBreakProgress() > 0) {
                renderBreakingBlock(abstractBlock);
            }
        }
    }

    /**
     * Renders single breaking block effect
     */
    private void renderBreakingBlock(AbstractBlock abstractBlock) {
        BatchedMesh breakMesh = new BatchedMesh();
        breakMesh.addBlockMesh(abstractBlock.getVertices(), abstractBlock.getIndices(), 0, abstractBlock.getLightLevel());
        breakMesh.updateGLBuffers();

        textureManager.bindTexture(blockTextureIds.get(abstractBlock.getType()), 0);
        GL20.glUniform1f(
                GL20.glGetUniformLocation(breakingShader.getProgramID(), "breakProgress"),
                abstractBlock.getBreakProgress()
        );

        breakMesh.render();
        breakMesh.cleanup();
    }

    /**
     * Renders world blocks with batching
     */
    private void renderBlocks(List<AbstractBlock> abstractBlocks, Matrix4f viewMatrix, float ambientLight) {
        blockShader.start();
        setupBlockShader(viewMatrix, ambientLight);
        ensureBlockTextures();

        Map<BlockType, List<AbstractBlock>> blocksByType = abstractBlocks.stream()
                .filter(this::isBlockInView)
                .collect(Collectors.groupingBy(AbstractBlock::getType));

        renderBlockBatches(blocksByType);
        System.out.println(PerformanceMetrics.getMetricsString());

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
     * @param abstractBlock Block to check visibility
     * @return true if block is in view frustum
     */
    private boolean isBlockInView(AbstractBlock abstractBlock) {
        Vector3f pos = abstractBlock.getPosition();
        return frustum.isBoxInFrustum(pos.x(), pos.y(), pos.z(), 1.0f);
    }

    /**
     * Loads and caches block textures
     */
    private void ensureBlockTextures() {
        for (BlockType type : BlockType.values()) {
            if (!blockTextureIds.containsKey(type)) {
                Vector3f tempPos = new Vector3f(0, 0, 0);
                AbstractBlock block = BlockFactory.createBlock(type, tempPos);
                blockTextureIds.put(type, textureManager.loadTexture(block.getTexturePath()));
            }
        }
    }

    /**
     * Renders batched blocks by type
     */
    private void renderBlockBatches(Map<BlockType, List<AbstractBlock>> blocksByType) {
        blocksByType.forEach((type, typeBlocks) -> {
            BatchedMesh mesh = blockMeshes.computeIfAbsent(type, k -> new BatchedMesh());
            mesh.clear();

            int vertexOffset = 0;
            for (AbstractBlock abstractBlock : typeBlocks) {
                mesh.addBlockMesh(
                    abstractBlock.getVertices(),
                    abstractBlock.getIndices(),
                    vertexOffset,
                    abstractBlock.getLightLevel()
                );
                vertexOffset += abstractBlock.getVertices().length / 5;
            }

            mesh.updateGLBuffers();
            textureManager.bindTexture(blockTextureIds.get(type), 0);
            mesh.render();
        });
    }

    /**
     * Renders block highlights
     */
    private void renderHighlights(List<AbstractBlock> abstractBlocks, Matrix4f viewMatrix) {
        highlightMesh.clear();
        abstractBlocks.stream()
                .filter(AbstractBlock::isHighlighted)
                .forEach(block -> highlightMesh.addBlockMesh(
                        block.getVertices(),
                        block.getIndices(),
                        0,
                        block.getLightLevel()
                ));

        if (abstractBlocks.stream().anyMatch(AbstractBlock::isHighlighted)) {
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