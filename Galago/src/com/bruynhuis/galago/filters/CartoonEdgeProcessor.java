/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.filters;

import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;

/**
 *
 * @author NideBruyn
 */
public class CartoonEdgeProcessor implements SceneProcessor {

    private RenderManager rm;
    private ViewPort vp;
    private AppProfiler prof;

    public CartoonEdgeProcessor() {
    }

    @Override
    public void initialize(final RenderManager rm, final ViewPort vp) {
        this.rm = rm;
        this.vp = vp;
    }

    @Override
    public void reshape(final ViewPort vp, final int w, final int h) {
    }

    @Override
    public boolean isInitialized() {
        return this.rm != null;
    }

    @Override
    public void preFrame(final float tpf) {
    }

    @Override
    public void postQueue(final RenderQueue rq) {
        this.rm.setForcedTechnique("CartoonEdge");
        this.rm.renderViewPortQueues(this.vp, false);
        this.rm.setForcedTechnique(null);
    }

    @Override
    public void postFrame(final FrameBuffer out) {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void setProfiler(AppProfiler profiler) {
        this.prof = profiler;
    }
}
