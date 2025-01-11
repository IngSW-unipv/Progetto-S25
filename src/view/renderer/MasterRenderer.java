package view.renderer;

import controller.event.EventBus;
import controller.event.EventType;
import controller.event.GameEvent;
import controller.event.RenderEvent;
import model.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import view.shader.ShaderProgram;
import view.window.WindowManager;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MasterRenderer implements WorldRenderer, EventListener {
    private ShaderProgram shader;
    private Matrix4f projectionMatrix;
    private Matrix4f modelMatrix;
    private TextureManager textureManager;
    private Map<BlockType, Integer> blockTextureIds;
    private HUDRenderer hudRenderer;
    private WindowManager windowManager;
    private Frustum frustum;
    private Matrix4f projectionViewMatrix;
    private Map<BlockType, BatchedMesh> blockMeshes;
    private ShaderProgram highlightShader;
    private BatchedMesh highlightMesh;
    private int drawCalls;

    public MasterRenderer(WindowManager windowManager) {
        EventBus.getInstance().subscribe(EventType.RENDER, this::onEvent);

        this.windowManager = windowManager;

        shader = new ShaderProgram("resources/shaders/block_vertex.glsl", "resources/shaders/block_fragment.glsl");
        highlightShader = new ShaderProgram(
                "resources/shaders/block_highlight_vertex.glsl",
                "resources/shaders/block_highlight_fragment.glsl"
        );
        highlightMesh = new BatchedMesh();

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

    private void updateProjectionMatrix() {
        projectionMatrix = new Matrix4f().perspective(
            (float) Math.toRadians(60.0f),
            windowManager.getAspectRatio(),
            0.3f,
            100.0f
        );
    }

    public void prepare() {
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
        shader.cleanup();
        hudRenderer.cleanUp();
        highlightShader.cleanup();
        highlightMesh.cleanup();
    }

    @Override
    public void render(List<Block> blocks, Camera camera) {
        long startTime = System.nanoTime();
        updateProjectionMatrix();
        prepare();
        shader.start();

        // Update matrices
        Matrix4f viewMatrix = camera.getViewMatrix();
        shader.loadMatrix("viewMatrix", viewMatrix);
        shader.loadMatrix("projectionMatrix", projectionMatrix);
        shader.loadMatrix("modelMatrix", modelMatrix);

        // Update frustum
        projectionViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustum.update(projectionViewMatrix);

        //drawCalls = 0;

        // Ensure all block textures are loaded
        for (BlockType type : BlockType.values()) {
            loadBlockTexture(type);
        }

        // Filter and group visible blocks by type
        Map<BlockType, List<Block>> blocksByType = blocks.stream()
            .filter(block -> {
                Position pos = block.getPosition();
                return frustum.isBoxInFrustum(pos.x(), pos.y(), pos.z(), 1.0f);
            })
            .collect(Collectors.groupingBy(Block::getType));

        // Update and render batches
        blocksByType.forEach((type, typeBlocks) -> {
            BatchedMesh mesh = blockMeshes.computeIfAbsent(type, k -> new BatchedMesh());
            mesh.clear();

            int vertexOffset = 0;
            for (Block block : typeBlocks) {
                mesh.addBlockMesh(block.getVertices(), block.getIndices(), vertexOffset);
                vertexOffset += block.getVertices().length / 5; // 5 floats per vertex
            }

            mesh.updateGLBuffers();
            textureManager.bindTexture(blockTextureIds.get(type), 0);
            mesh.render();
            //drawCalls++;
        });

        highlightMesh.clear();
        blocks.stream()
                .filter(Block::isHighlighted)
                .forEach(block -> {
                    highlightMesh.addBlockMesh(block.getVertices(), block.getIndices(), 0);
                });

        if (blocks.stream().anyMatch(Block::isHighlighted)) {
            highlightMesh.updateGLBuffers();

            // Abilita il blending per la trasparenza
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            highlightShader.start();
            highlightShader.loadMatrix("viewMatrix", viewMatrix);
            highlightShader.loadMatrix("projectionMatrix", projectionMatrix);
            highlightShader.loadMatrix("modelMatrix", modelMatrix);

            // Disabilita il depth write ma mantieni il depth test
            GL11.glDepthMask(false);
            highlightMesh.render();
            GL11.glDepthMask(true);

            highlightShader.stop();
            GL11.glDisable(GL11.GL_BLEND);
        }

        shader.stop();

        // Render HUD
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        hudRenderer.render();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        //System.out.println("Draw calls: " + drawCalls + " for " + blocks.size() + " blocks");

        //long endTime = System.nanoTime();
        //double ms = (endTime - startTime) * 1e-6;
        //System.out.println("Frame time: " + ms + "ms");
    }

    public void onEvent(GameEvent event) {
        if (event instanceof RenderEvent renderEvent) {
            render(renderEvent.blocks(), renderEvent.camera());
        }
    }
}