package view.renderer;

import model.Block;

import java.util.List;

public interface WorldObserver {
    void onWorldUpdate(List<Block> visibleBlocks);
}