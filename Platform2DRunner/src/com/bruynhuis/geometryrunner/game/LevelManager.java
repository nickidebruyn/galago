/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.game;

import java.util.ArrayList;
import java.util.Iterator;
import com.bruynhuis.geometryrunner.MainApplication;

/**
 *
 * @author nidebruyn
 */
public class LevelManager {

    protected MainApplication mainApplication;
    protected ArrayList<LevelDefinition> levels = new ArrayList<LevelDefinition>();

    public LevelManager(MainApplication mainApplication1) {
        this.mainApplication = mainApplication1;
        initLevels();
    }

    private void initLevels() {
        LevelDefinition ld = new LevelDefinition(1, "level001", "level001.map", "A Hero in training. You are an explorer of a multi dimensional universe. Pass throught the portal to jump to a new universe.", 20);
        levels.add(ld);
        
        ld = new LevelDefinition(2, "level002", "level002.map", "Jumping jack.", 20);
        levels.add(ld);
        
        ld = new LevelDefinition(3, "level003", "level003.map", "Jumping jack.", 20);
        levels.add(ld);
        
    }

    public ArrayList<LevelDefinition> getLevels() {
        return levels;
    }

    public LevelDefinition getLevelDefinitionByName(String name) {
        LevelDefinition ld = null;
        for (int i = 0; i < levels.size(); i++) {
            LevelDefinition levelDefinition = levels.get(i);
            if (levelDefinition.getLevelName().equals(name)) {
                ld = levelDefinition;
            }
        }
        return ld;
    }

    /**
     * Helper method that will return the next available level and null if
     * nothing was found.
     *
     * @param currentLevelId
     * @return
     */
    public LevelDefinition getNextLevel(int currentLevelId) {
        LevelDefinition levelDefinition = null;
        for (Iterator<LevelDefinition> it = levels.iterator(); it.hasNext();) {
            LevelDefinition levelDefinition1 = it.next();
            if (levelDefinition1.getUid() == currentLevelId) {
                if (it.hasNext()) {
                    levelDefinition = it.next();
                }
            }

        }
        return levelDefinition;
    }
    
    public LevelDefinition getLevelById(int levelId) {
        LevelDefinition levelDefinition = null;
        for (Iterator<LevelDefinition> it = levels.iterator(); it.hasNext();) {
            LevelDefinition levelDefinition1 = it.next();
            if (levelDefinition1.getUid() == levelId) {
                levelDefinition = levelDefinition1;
            }

        }
        return levelDefinition;
    }
}
