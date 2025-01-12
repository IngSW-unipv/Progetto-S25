package view.renderer;

import controller.event.EventBus;
import controller.event.EventType;
import controller.event.GameEvent;
import controller.event.RenderEvent;
import model.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import view.shader.ShaderProgram;
import view.window.WindowManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MasterRenderer implements WorldRenderer {
    private ShaderProgram blockShader;
    private ShaderProgram highlightShader;
    private ShaderProgram breakingShader;
    private Matrix4f projectionMatrix;
    private Matrix4f modelMatrix;
    private TextureManager textureManager;
    private Map<BlockType, Integer> blockTextureIds;
    private HUDRenderer hudRenderer;
    private WindowManager windowManager;
    private Frustum frustum;
    private Matrix4f projectionViewMatrix;
    private Map<BlockType, BatchedMesh> blockMeshes;
    private BatchedMesh highlightMesh;
    private BatchedMesh breakingMesh;

    public MasterRenderer(WindowManager windowManager) {
        EventBus.getInstance().subscribe(EventType.RENDER, this::onEvent);

        this.windowManager = windowManager;

        blockShader = new ShaderProgram("resources/shaders/block_vertex.glsl", "resources/shaders/block_fragment.glsl");
        highlightShader = new ShaderProgram("resources/shaders/block_highlight_vertex.glsl", "resources/shaders/block_highlight_fragment.glsl");
        breakingShader = new ShaderProgram("resources/shaders/block_breaking_vertex.glsl", "resources/shaders/block_breaking_fragment.glsl");

        highlightMesh = new BatchedMesh();
        breakingMesh = new BatchedMesh();

        textureManager = new TextureManager();
        blockTextureIds = new HashMap<>();
        blockMeshes = new HashMap<>();

        updateProjectionMatrix();
        modelMatrix = new Matrix4f().identity();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);

        hudRenderer = new HUDRenderer();
        frustum = new Frustum();
        projectionViewMatrix = new Matrix4f();
    }

    @Override
    public void render(List<Block> blocks, Camera camera) {
        updateProjectionMatrix();
        prepare();

        Matrix4f viewMatrix = camera.getViewMatrix();
        projectionViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustum.update(projectionViewMatrix);

        // Breaking animation rendering
        breakingShader.start();
        breakingShader.loadMatrix("viewMatrix", viewMatrix);
        breakingShader.loadMatrix("projectionMatrix", projectionMatrix);
        breakingShader.loadMatrix("modelMatrix", modelMatrix);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (Block block : blocks) {
            if (block.getBreakProgress() > 0) {
                BatchedMesh breakMesh = new BatchedMesh();
                breakMesh.addBlockMesh(block.getVertices(), block.getIndices(), 0);
                breakMesh.updateGLBuffers();

                textureManager.bindTexture(blockTextureIds.get(block.getType()), 0);
                int breakProgressLocation = GL20.glGetUniformLocation(breakingShader.getProgramID(), "breakProgress");
                GL20.glUniform1f(breakProgressLocation, block.getBreakProgress());

                breakMesh.render();
                breakMesh.cleanup();
            }
        }

        breakingShader.stop();
        GL11.glDisable(GL11.GL_BLEND);

        // Normal block rendering
        blockShader.start();
        blockShader.loadMatrix("viewMatrix", viewMatrix);
        blockShader.loadMatrix("projectionMatrix", projectionMatrix);
        blockShader.loadMatrix("modelMatrix", modelMatrix);

        // Load textures
        for (BlockType type : BlockType.values()) {
            loadBlockTexture(type);
        }

        Map<BlockType, List<Block>> blocksByType = blocks.stream()
                .filter(block -> {
                    Position pos = block.getPosition();
                    return frustum.isBoxInFrustum(pos.x(), pos.y(), pos.z(), 1.0f);
                })
                .collect(Collectors.groupingBy(Block::getType));

        blocksByType.forEach((type, typeBlocks) -> {
            BatchedMesh mesh = blockMeshes.computeIfAbsent(type, k -> new BatchedMesh());
            mesh.clear();

            int vertexOffset = 0;
            for (Block block : typeBlocks) {
                mesh.addBlockMesh(block.getVertices(), block.getIndices(), vertexOffset);
                vertexOffset += block.getVertices().length / 5;
            }

            mesh.updateGLBuffers();
            textureManager.bindTexture(blockTextureIds.get(type), 0);
            mesh.render();
        });

        blockShader.stop();

        // Highlight rendering
        highlightMesh.clear();
        blocks.stream()
                .filter(Block::isHighlighted)
                .forEach(block -> {
                    highlightMesh.addBlockMesh(block.getVertices(), block.getIndices(), 0);
                });

        if (blocks.stream().anyMatch(Block::isHighlighted)) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);

            highlightShader.start();
            highlightShader.loadMatrix("viewMatrix", viewMatrix);
            highlightShader.loadMatrix("projectionMatrix", projectionMatrix);
            highlightShader.loadMatrix("modelMatrix", modelMatrix);

            highlightMesh.updateGLBuffers();
            highlightMesh.render();
            highlightShader.stop();

            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
        }

        // Render HUD
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        hudRenderer.render();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(60.0f),
                windowManager.getAspectRatio(),
                0.3f,
                100.0f
        );
    }

    private void prepare() {
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void loadBlockTexture(BlockType type) {
        if (!blockTextureIds.containsKey(type)) {
            int textureId = textureManager.loadTexture(type.getTexturePath());
            blockTextureIds.put(type, textureId);
        }
    }

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

    public void onEvent(GameEvent event) {
        if (event instanceof RenderEvent renderEvent) {
            render(renderEvent.blocks(), renderEvent.camera());
        }
    }
}