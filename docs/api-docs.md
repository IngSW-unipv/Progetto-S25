# API Documentation

## Event System
### EventBus
La classe centrale per la gestione eventi.
```java
EventBus.getInstance().post(event);
EventBus.getInstance().subscribe(EventType.INPUT, listener);
```

### Event Types
- INPUT: Eventi input utente
- RENDER: Eventi rendering
- WORLD_GENERATION: Eventi generazione mondo
- GAME_STATE: Eventi stato gioco

## Configuration
### GameConfig
Configurazione centrale del gioco:
```java
GameConfig.RENDER_DISTANCE = 3;
GameConfig.CAMERA_MOVE_SPEED = 10f;
GameConfig.GRAVITY = -5f;
```

## Shader System
### ShaderProgram
```java
ShaderProgram shader = new ShaderProgram(vertexPath, fragmentPath);
shader.start();
shader.loadMatrix("projectionMatrix", matrix);
shader.stop();
```

## Block System
### BlockType
Definisce i tipi di blocchi disponibili:
- DIRT
- GRASS
- STONE
- BEDROCK

### World Generation
```java
World world = new World(position, seed);
world.placeBlock(position, type);
world.destroyBlock(position);
```

## Physics System
### Collision Detection
```java
BoundingBox box = new BoundingBox(width, height, depth);
CollisionSystem.checkCollision(box);
```

## Rendering Pipeline
### MasterRenderer
```java
renderer.render(blocks, camera);
renderer.cleanUp();
```

## Texture System
### TextureManager
```java
int textureID = textureManager.loadTexture(path);
textureManager.bindTexture(textureID, slot);
```