# Voxel Engine

Un motore voxel in Java basato su pattern MVC, utilizzando OpenGL per il rendering.

## Requisiti di Sistema
- Java 17+
- OpenGL 3.3+
- 4GB RAM minimo
- GPU compatibile con OpenGL 3.3

## Dipendenze
- LWJGL 3
- JOML per matematica
- STB per texture loading

## Installazione
1. Clona il repository
2. Assicurati di avere Java 17+ installato
3. Esegui `gradle build`
4. Avvia con `gradle run`

## Struttura Progetto
```
src/
├── controller/     # Controllers MVC
├── model/         # Data models
├── view/          # Rendering e UI
├── config/        # Configurazioni
└── resources/     # Assets e shaders
```

## Features
- Generazione procedurale terreno
- Fisica blocchi realistica
- Sistema eventi
- Rendering ottimizzato
- Gestione chunk
- Configurazione salvabile

## Controlli
- WASD: Movimento
- Space: Salto
- Mouse: Camera
- Click SX: Rompi blocco
- Click DX: Piazza blocco
- ESC: Menu
- F11: Fullscreen

## License
MIT License