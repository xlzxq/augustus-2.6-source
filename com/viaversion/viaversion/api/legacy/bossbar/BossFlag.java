// 
// Decompiled by Procyon v0.5.36
// 

package com.viaversion.viaversion.api.legacy.bossbar;

public enum BossFlag
{
    DARKEN_SKY(1), 
    PLAY_BOSS_MUSIC(2), 
    CREATE_FOG(4);
    
    private final int id;
    
    private BossFlag(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
}
