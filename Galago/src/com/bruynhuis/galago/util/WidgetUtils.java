/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;

/**
 *
 * @author nicki
 */
public class WidgetUtils {
    
    public static void moveTo(Widget widget, float duration, float targetX, float targetY, float delay) {
        Tween.to(widget, WidgetAccessor.POS_XY, duration)
                .target(targetX, targetY)
                .delay(delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        
    }
    
    public static void fadeTo(Widget widget, float duration, float target, float delay) {
        Tween.to(widget, WidgetAccessor.OPACITY, duration)
                .target(target)
                .delay(delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        
    }
    
}
