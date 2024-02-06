package com.bruynhuis.galago.sprite.physics.debug;

import com.bruynhuis.galago.sprite.physics.Dyn4jAppState;
import com.bruynhuis.galago.util.SpatialUtils;
import static com.google.typography.font.sfntly.table.opentype.ScriptTag.java;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ndebruyn
 */
public class Dyn4JDebugAppState extends AbstractAppState {

  private Dyn4jAppState dyn4jAppState;
  private Node rootNode;
  private SimpleApplication app;
  private HashMap<String, Spatial> shapes = new HashMap<>();
  private ArrayList<Spatial> removeList = new ArrayList<>();

  public Dyn4JDebugAppState(Dyn4jAppState dyn4jAppState) {
    this.dyn4jAppState = dyn4jAppState;
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    this.app = (SimpleApplication) app;
    rootNode = new Node("Debug Node");
    super.initialize(stateManager, app);
  }

  @Override
  public void setEnabled(boolean enabled) {

    if (enabled) {
      this.app.getRootNode().attachChild(this.rootNode);
    } else {
      this.app.getRootNode().detachChild(this.rootNode);
    }

    super.setEnabled(enabled);
  }

  @Override
  public void update(float tpf) {
    //TODO: Render the debug shapes
    List<Body> bodies = dyn4jAppState.getPhysicsSpace().getPhysicsWorld().getBodies();
    if (bodies != null && bodies.size() > 0) {
      for (Body body : bodies) {
        for (BodyFixture fixture : body.getFixtures()) {
          Spatial spatial = shapes.get(fixture.getShape().getUserData().toString());
          if (spatial == null) {
            spatial = createShape(body, fixture.getShape());
            if (spatial != null) {
              rootNode.attachChild(spatial);
              shapes.put(fixture.getShape().getUserData().toString(), spatial);
            }
          }

          if (spatial != null) {
//            System.out.println("WorldPos: " + body.getWorldCenter());
            spatial.setLocalTranslation((float) body.getTransform().getTranslationX() + (float) fixture.getShape().getCenter().x, (float) body.getTransform().getTranslationY() + (float) fixture.getShape().getCenter().y, 10);
            spatial.setLocalRotation(spatial.getLocalRotation().fromAngleAxis((float) body.getTransform().getRotationAngle(), Vector3f.UNIT_Z));
          }
        }
      }

//      for (Spatial spatial : shapes.values()) {
//        if (bodies != null && bodies.size() > 0) {
//          for (Body body : bodies) {
//            boolean inBodies = false;
//            for (BodyFixture fixture : body.getFixtures()) {
//              if (fixture.getId().toString().equals(spatial.getName())) {
//                inBodies = true;
//              }
//            }
//            if (!inBodies) {
//              removeList.add(spatial);
//            }
//          }
//        }
//      }
    }

    removeList.clear();

    for (Spatial spatial : shapes.values()) {
      BodyFixture fixture = getBodyFixtureById(bodies, spatial.getName());
      if (fixture == null) {
        removeList.add(spatial);
      }
    }

    for (Spatial spatial : removeList) {
      spatial.removeFromParent();
      shapes.remove(spatial.getName(), spatial);
    }

//    System.out.println("Shape count = " + shapes.size() + "; Body count = " + bodies.size());

  }

  private BodyFixture getBodyFixtureById(List<Body> bodies, String id) {
    BodyFixture fix = null;
    for (Body body : bodies) {
      for (BodyFixture fixture : body.getFixtures()) {
        if (fixture.getShape().getUserData().toString().equals(id)) {
          fix = fixture;
          break;
        }
      }
    }
    return fix;
  }

  private Spatial createShape(Body body, Convex convex) {
    if (convex != null) {
//      System.out.println("Create shape: " + convex);
      if (convex instanceof Rectangle) {
        return createRectangleShape(body, (Rectangle) convex);
      } else if (convex instanceof Ellipse) {
        return createEllipseShape(body, (Ellipse) convex);
      }

    }
    return null;
  }

  private Spatial createRectangleShape(Body body, Rectangle rectangle) {
    WireBox mesh = new WireBox((float) rectangle.getWidth() * 0.5f, (float) rectangle.getHeight() * 0.5f, 0.1f);
    Geometry g = new Geometry(rectangle.getUserData().toString(), mesh);
    Material material = SpatialUtils.addColor(g, ColorRGBA.Blue, true);
    g.setQueueBucket(RenderQueue.Bucket.Translucent);
    return g;
  }

  private Spatial createEllipseShape(Body body, Ellipse ellipse) {
    float scaleX = 1;
    float scaleY = 1;
    float halfWidth = (float) ellipse.getHalfWidth();
    float halfHeight = (float) ellipse.getHalfHeight();
    float radius = 1;

    if (halfWidth < halfHeight) {
      scaleX = halfWidth / halfHeight;
      radius = halfHeight;
    } else {
      scaleY = halfHeight / halfWidth;
      radius = halfWidth;
    }

    WireSphere mesh = new WireSphere(radius);
    Geometry g = new Geometry(ellipse.getUserData().toString(), mesh);
    Material material = SpatialUtils.addColor(g, ColorRGBA.Orange, true);
    g.setQueueBucket(RenderQueue.Bucket.Translucent);
    g.setLocalScale(scaleX, scaleY, 1);
    return g;
  }

  @Override
  public void cleanup() {
    super.cleanup();
    this.rootNode.detachAllChildren();
    this.rootNode.removeFromParent();
  }
}
