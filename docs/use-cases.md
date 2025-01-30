# Casi d'Uso - Voxel Engine

## 1. Gestione Mondo

### UC1: Creazione Nuovo Mondo
- **Attore principale**: Giocatore
- **Precondizioni**: Nessuna
- **Flusso principale**:
  1. Il giocatore seleziona "Play" dal view.menu principale
  2. Il giocatore seleziona "New World"
  3. Il giocatore inserisce un seed (opzionale)
  4. Il sistema genera il mondo utilizzando il seed
  5. Il sistema posiziona il giocatore nel punto di spawn
- **Postcondizioni**: Nuovo mondo generato e caricato
- **Eccezioni**: Errori di generazione mondo

### UC2: Gestione Chunk
- **Attore principale**: Sistema
- **Precondizioni**: Mondo caricato
- **Flusso principale**:
  1. Il sistema monitora la posizione del giocatore
  2. Il sistema carica i chunk entro il render distance
  3. Il sistema scarica i chunk fuori dal render distance
- **Postcondizioni**: Chunk aggiornati secondo posizione giocatore

## 2. Interazione Giocatore

### UC3: Movimento Giocatore
- **Attore principale**: Giocatore
- **Precondizioni**: Mondo caricato
- **Flusso principale**:
  1. Il giocatore usa WASD per movimento orizzontale
  2. Il giocatore usa spazio per saltare
  3. Il sistema applica fisica e collisioni
  4. Il sistema aggiorna la posizione della camera
- **Estensioni**:
  - Shift per velocità ridotta
- **Eccezioni**: Collisioni con blocchi

### UC4: Modifica Blocchi
- **Attore principale**: Giocatore
- **Precondizioni**: Mondo caricato
- **Flusso principale**:
  1. Il giocatore mira a un blocco
  2. Click sinistro per distruggere
  3. Click destro per piazzare
  4. Il sistema aggiorna la mesh del chunk
- **Eccezioni**: Bedrock non distruggibile

## 3. Sistema Grafico

### UC5: Rendering Mondo
- **Attore principale**: Sistema
- **Precondizioni**: Mondo caricato
- **Flusso principale**:
  1. Il sistema identifica blocchi visibili
  2. Il sistema aggiorna le mesh dei chunk
  3. Il sistema applica shaders e textures
  4. Il sistema renderizza la scena
- **Estensioni**:
  - Gestione trasparenze
  - Frustum culling

### UC6: Gestione HUD
- **Attore principale**: Sistema
- **Precondizioni**: Mondo caricato
- **Flusso principale**:
  1. Il sistema renderizza il mirino
  2. Il sistema mostra l'evidenziazione blocco
  3. Il sistema visualizza l'animazione rottura blocco

## 4. Configurazione

### UC7: Gestione Impostazioni
- **Attore principale**: Giocatore
- **Precondizioni**: Nessuna
- **Flusso principale**:
  1. Il giocatore accede al view.menu impostazioni
  2. Il giocatore modifica parametri:
     - Render distance
     - Sensibilità mouse
     - Velocità movimento
     - Parametri fisici
  3. Il sistema salva le modifiche
- **Postcondizioni**: Configurazione aggiornata

### UC8: Gestione Finestra
- **Attore principale**: Giocatore
- **Precondizioni**: Gioco avviato
- **Flusso principale**:
  1. Il giocatore può alternare fullscreen/windowed
  2. Il giocatore può chiudere il gioco
  3. Il sistema gestisce il ridimensionamento
- **Postcondizioni**: Stato finestra aggiornato

## Requisiti non funzionali
1. **Performance**
   - Rendering fluido (60+ FPS)
   - Caricamento chunk efficiente
   - Gestione memoria ottimizzata

2. **Usabilità**
   - Controlli intuitivi
   - UI responsiva
   - Feedback visivo chiaro

3. **Architettura**
   - Pattern MVC rigoroso
   - Sistema eventi robusto
   - Codice modulare e manutenibile