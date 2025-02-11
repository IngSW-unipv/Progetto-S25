# Voxel Engine Project

A high-performance voxel engine implemented in Java, featuring strict Model-View-Controller architecture. This academic project demonstrates advanced 3D graphics techniques, efficient data structures, and clean architectural principles.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![LWJGL Version](https://img.shields.io/badge/LWJGL-3.3.2-green)](https://www.lwjgl.org/)

## Features

### World Generation
- Procedural terrain using multi-octave Perlin noise
- Infinite world generation capabilities
- Dynamic chunk loading/unloading
- Customizable world seeds

### Graphics Engine
- OpenGL 3.3 Core Profile rendering
- Advanced shader pipeline
- Frustum and occlusion culling
- Dynamic lighting system
- Day/night cycle with smooth transitions
- Efficient batch rendering

### Physics & Interaction
- Real-time collision detection
- Rigid body physics simulation
- Block manipulation (placement/destruction)
- Ray casting for block selection
- Player movement with jumping and gravity

### Performance
- Optimized chunk mesh generation
- Multi-threaded world generation
- Efficient memory management
- Render distance configuration
- Performance monitoring tools

### User Interface
- Settings management
- Performance metrics display

## Prerequisites

### Required Software
- Java Development Kit (JDK) 17 or higher
- Git (for version control)

### Dependencies
- LWJGL 3.3.2 (OpenGL, GLFW, STB)
- JOML 1.10.5 (Math library)

## Getting Started

### Building from Source
```bash
# Clone repository
git clone https://github.com/IngSW-unipv/Progetto-S25.git
cd Progetto-S25

# Build project
mvn clean install
```

### Running the Application
```bash
# Run with default settings
java -jar target/voxel-engine-1.0.jar
```

## Controls

### Movement
- `W/A/S/D`: Basic movement
- `Space`: Jump
- `Left Shift`: Sprint
- `Mouse`: Look around

### Block Interaction
- `Right Click`: Break block
- `Left Click`: Place block

### System Controls
- `F11`: Toggle fullscreen
- `ESC`: Menu/Exit

## Architecture

### Model
- World state management
- Physics calculations
- Entity management
- Data persistence

### View
- OpenGL rendering
- Window management
- User interface
- Shader system

### Controller
- Input processing
- Game logic
- Event handling
- State management

## Project Structure
```
SandboxProject/
├── docs/                          # Documentation
│   ├── design-patterns.md         # Design patterns
│   ├── glossary.md               # Technical glossary
│   ├── readme.md                 # This file
│   ├── requirements.md           # Requirements specification
│   ├── use-cases.md             # Use cases
│   └── vision-doc.md            # Vision document
│
├── resources/                    # Resource files
│   ├── shaders/                 # GLSL shader files~~~~
│   │   ├── block_breaking_fragment.glsl
│   │   ├── block_breaking_vertex.glsl
│   │   ├── block_fragment.glsl
│   │   ├── block_highlight_fragment.glsl
│   │   ├── block_highlight_vertex.glsl
│   │   ├── block_vertex.glsl
│   │   ├── hud_fragment.glsl
│   │   └── hud_vertex.glsl
│   └── textures/                # Game textures
│
└── src/                         # Source code
    │   Main                     # main class
    │
    └── config/                  # Configuration
    │   ├── ConfigManager        # Settings manager
    │   ├── GameConfig          # Game constants
    │   └── game_config.properties
    │
    ├── controller/             # Controllers
    │   ├── event/             # Event system
    │   │   ├── EventBus
    │   │   ├── EventListener
    │   │   ├── EventType
    │   │   ├── GameEvent
    │   │   ├── InputAction
    │   │   ├── InputEvent
    │   │   ├── MenuAction
    │   │   ├── MenuEvent
    │   │   ├── RenderEvent
    │   │   └── WorldGenerationEvent
    │   │
    │   ├── game/              # Game control
    │   │   └── GameController
    │   │
    │   ├── input/             # Input handling
    │   │   ├── InputController
    │   │   └── PlayerController
    │   │
    │   └── menu/              # Menu control
    │       └── MenuController
    │
    ├── model/                 # Game model
    │   ├── block/            # Block system
    │   │   ├── Block
    │   │   ├── BlockDirection
    │   │   ├── BlockModification
    │   │   └── BlockType
    │   │
    │   ├── game/             # Game state
    │   │   ├── GameState
    │   │   └── Model
    │   │
    │   ├── physics/          # Physics engine
    │   │   ├── BoundingBox
    │   │   ├── CollisionSystem
    │   │   └── PhysicsSystem
    │   │
    │   ├── player/           # Player handling
    │   │   ├── Camera
    │   │   ├── Player
    │   │   └── RayCaster
    │   │
    │   ├── save/             # Save system
    │   │   ├── WorldManager
    │   │   └── WorldSaveData
    │   │
    │   └── world/            # World system
    │       ├── Chunk
    │       ├── ChunkLoader
    │       ├── ChunkLoadTask
    │       ├── DayNightCycle
    │       ├── Frustum
    │       ├── OcclusionCulling
    │       ├── PerlinNoiseGenerator
    │       ├── World
    │       └── WorldData
    │
    ├── util/                 # Utilities
    │   └── PerformanceMetrics
    │
    └── view/                 # View system
        ├── menu/             # Menu UI
        │   ├── MainMenuPanel
        │   ├── MenuView
        │   ├── NewWorldDialog
        │   ├── SettingsPanel
        │   ├── WorldListDialog
        │   └── WorldSelectPanel
        │
        ├── renderer/         # Rendering
        │   ├── BatchedMesh
        │   ├── HUDRenderer
        │   ├── MasterRenderer
        │   ├── TextureManager
        │   └── WorldRenderer
        │
        ├── shader/           # Shader management
        │   ├── ShaderProgram
        │   └── ShaderUtils
        │
        ├── window/           # Window management
        │   └── WindowManager
        │
        └── View             # Main view class
        
```

## Documentation

### Design Documents
- [Vision Document](vision-doc.md): Project overview and goals
- [Requirements](requirements.md): Functional/non-functional requirements
- [Use Cases](use-cases.md): User interaction flows~~~~
- [Design Patterns](design-patterns.md): Architectural patterns
- [Technical Glossary](glossary.md): Term definitions