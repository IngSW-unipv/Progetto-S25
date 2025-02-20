# Technical Glossary

## Architectural Concepts

### MVC (Model-View-Controller)
The core architectural pattern separating the application into three interconnected components:
- Model: Data and business logic
- View: User interface and display
- Controller: Input handling and control flow

### Event Bus
A centralized communication system implementing the publisher-subscriber pattern for decoupled component interaction.

### Component
A self-contained module of code with well-defined responsibilities and interfaces.

## World Structure

### Block
The fundamental cubic unit of the game world, characterized by:
- Position (x, y, z coordinates)
- Type (defining appearance and behavior)
- Properties (opacity, hardness, etc.)
- State (visible, highlighted, breaking)

### Chunk
A discrete section of the world (typically 16x16x16 abstractBlocks) that:
- Forms the basic unit of world loading/unloading
- Contains multiple abstractBlocks
- Manages abstractBlock visibility and updates
- Optimizes rendering and memory usage

### Voxel
A volumetric pixel representing a single point in three-dimensional space, synonymous with Block in this context.

## Graphics Systems

### Rendering Pipeline
The sequence of steps transforming 3D scene data into 2D images:
1. Vertex processing
2. Primitive assembly
3. Rasterization
4. Fragment processing
5. Frame buffer operations

### Frustum Culling
An optimization technique that:
- Determines visibility based on camera view
- Eliminates off-screen geometry
- Reduces rendering overhead
- Improves performance

### Occlusion Culling
A rendering optimization that:
- Identifies hidden geometry
- Skips rendering of obscured objects
- Reduces GPU workload
- Improves frame rate

### Mesh
A collection of geometric data including:
- Vertices (3D points)
- Indices (triangle definitions)
- UV coordinates (texture mapping)
- Normals (lighting information)

### Shader
A GPU program type:
- Vertex Shader: Processes vertices
- Fragment Shader: Processes pixels
- Processes rendering data
- Defines visual appearance

### VAO (Vertex Array Object)
An OpenGL object that:
- Stores vertex attribute configurations
- Manages vertex buffer bindings
- Optimizes state changes
- Improves rendering efficiency

### VBO (Vertex Buffer Object)
A GPU memory buffer that:
- Stores vertex data
- Enables efficient data transfer
- Optimizes rendering performance
- Reduces CPU-GPU communication

## Game Systems

### Ray Casting
A geometric technique that:
- Projects rays from camera
- Detects abstractBlock intersections
- Enables abstractBlock selection
- Supports interaction

### Perlin Noise
A procedural generation algorithm providing:
- Coherent random values
- Natural-looking terrain
- Smooth transitions
- Deterministic output

### Physics System
A simulation component managing:
- Collision detection
- Movement calculation
- Gravity effects
- Player interaction

### Batch Rendering
An optimization technique that:
- Combines multiple draw calls
- Reduces GPU state changes
- Improves rendering performance
- Manages similar geometry

## Technical Framework

### LWJGL (Lightweight Java Game Library)
A Java library providing:
- OpenGL bindings
- GLFW integration
- Input handling
- System access

### GLFW (Graphics Library Framework)
A window management library offering:
- Window creation
- Input processing
- OpenGL context management
- Cross-platform support

### JOML (Java OpenGL Math Library)
A mathematics library providing:
- Vector operations
- Matrix transformations
- Geometric calculations
- Optimization utilities

### OpenGL
A graphics API that:
- Defines rendering operations
- Manages GPU communication
- Provides graphics pipeline
- Enables 3D rendering

## Performance Concepts

### Frame Time
The duration required to:
- Process game logic
- Update physics
- Render scene
- Present frame

### Garbage Collection
Memory management process that:
- Reclaims unused memory
- Manages object lifecycle
- Prevents memory leaks
- Optimizes performance

### Memory Pool
Resource management technique that:
- Preallocates objects
- Reduces allocation overhead
- Minimizes fragmentation
- Improves performance

### Threading
Concurrent execution model for:
- Parallel processing
- Background operations
- Resource loading
- Performance optimization