package com.bruynhuis.galago.ui.effect;

import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author ndebruyn
 */
public class HoverColorEffect extends Effect {

  private ColorRGBA outColor;
  private ColorRGBA overColor;

  public HoverColorEffect(Widget widget, ColorRGBA outColor, ColorRGBA overColor) {
    super(widget);
    this.outColor = outColor;
    this.overColor = overColor;
  }

  @Override
  protected void doShow() {
    if (this.widget instanceof TouchButton) {
      ((TouchButton)widget).setTextColor(outColor);
    }

  }

  @Override
  protected void doHide() {

  }

  @Override
  protected void doTouchDown() {

  }

  @Override
  protected void doTouchUp() {

  }

  @Override
  protected void doEnabled(boolean enabled) {

  }

  @Override
  protected void doSelected() {

  }

  @Override
  protected void doUnselected() {

  }

  @Override
  protected void doHoverOver() {
    if (this.widget instanceof TouchButton) {
      ((TouchButton)widget).setTextColor(overColor);
        System.out.println("Hover color: " + overColor);
    }

  }

  @Override
  protected void doHoverOff() {
    if (this.widget instanceof TouchButton) {
      ((TouchButton)widget).setTextColor(outColor);
    }
  }

  @Override
  protected void controlUpdate(float tpf) {

  }
}

