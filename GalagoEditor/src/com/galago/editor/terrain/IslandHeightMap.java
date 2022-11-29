package com.galago.editor.terrain;

import com.jme3.math.Vector2f;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author ndebruyn
 */
public class IslandHeightMap extends AbstractHeightMap {

  private static final Logger logger = Logger.getLogger(HillHeightMap.class.getName());
  
  private int iterations; // how many hills to generate
  private float minRadius; // the minimum size of a hill radius
  private float maxRadius; // the maximum size of a hill radius
  private long seed; // the seed for the random number generator

  /**
   * Constructor sets the attributes of the hill system and generates the
   * height map.
   *
   * @param size       size the size of the terrain to be generated
   * @param iterations the number of hills to grow
   * @param minRadius  the minimum radius of a hill
   * @param maxRadius  the maximum radius of a hill
   * @param seed       the seed to generate the same heightmap again
   * @throws Exception if size of the terrain is not greater than zero, or the number of
   *                   iterations is not greater than zero
   */
  public IslandHeightMap(int size, int iterations, float minRadius,
                         float maxRadius, long seed) throws Exception {
    if (size <= 0 || iterations <= 0 || minRadius <= 0 || maxRadius <= 0
            || minRadius >= maxRadius) {
      throw new Exception(
              "Either size of the terrain is not greater than zero, "
                      + "or number of iterations is not greater than zero, "
                      + "or minimum or maximum radius are not greater than zero, "
                      + "or minimum radius is greater than maximum radius, "
                      + "or power of flattening is below one");
    }
    logger.fine("Constructing hill heightmap using seed: " + seed);
    this.size = size;
    this.seed = seed;
    this.iterations = iterations;
    this.minRadius = minRadius;
    this.maxRadius = maxRadius;

    load();
  }

  /**
   * Constructor sets the attributes of the hill system and generates the
   * height map by using a random seed.
   *
   * @param size       size the size of the terrain to be generated
   * @param iterations the number of hills to grow
   * @param minRadius  the minimum radius of a hill
   * @param maxRadius  the maximum radius of a hill
   * @throws Exception if size of the terrain is not greater than zero, or number of
   *                   iterations is not greater than zero
   */
  public IslandHeightMap(int size, int iterations, float minRadius,
                         float maxRadius) throws Exception {
    this(size, iterations, minRadius, maxRadius, new Random().nextLong());
  }

  /**
   * Generates a heightmap using the Hill Algorithm and the attributes set by
   * the constructor or the setters.
   */
  @Override
  public boolean load() {
    // clean up data if needed.
    if (null != heightData) {
      unloadHeightMap();
    }
    heightData = new float[size * size];
    float[][] tempBuffer = new float[size][size];
    Random random = new Random(seed);

    // Add the hills
    for (int i = 0; i < iterations; i++) {
      addHill(tempBuffer, random);
    }

    // transfer temporary buffer to final heightmap
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        setHeightAtPoint(tempBuffer[i][j], j, i);
      }
    }

    normalizeTerrain(NORMALIZE_RANGE);

    flattenOutside();

    logger.fine("Created Heightmap using the Hill Algorithm");

    return true;
  }

  protected void flattenOutside() {
    int padding = (int)((float)size*0.1f);
    int radiusMap = ((int)(size*0.5f))-padding;
    Vector2f center = new Vector2f(radiusMap+padding, radiusMap+padding);
    Vector2f point = new Vector2f(0, 0);
    float distance = 0;
    float calcHeight = 0;
    float distPer = 0;
//    System.out.println("Radius of map = " + radiusMap);

    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {

        point.set(x, y);
        distance = point.distance(center);
        if (distance > radiusMap) {
//          System.out.println("Distance = " + distance);
          heightData[x + y * size] = 0;

        } else if (distance < radiusMap*0.5f) {
//
//          if (heightData[x + y * size] > 100 && heightData[x + y * size] < 120) {
//            heightData[x + y * size] = 110;
//
//          }

        } else {
          distPer = 1f-(distance/(radiusMap));
          calcHeight = 255f*distPer*1.6f;
          if (heightData[x + y * size] > calcHeight) {
            heightData[x + y * size] = calcHeight;
          }

        }

//        float height = heightData[x + y * size];
//
//        if (height > 110 && height < 120) {
//          heightData[x + y * size] = 110;
//          System.out.println("Height: " + height);
//        }
      }
    }
  }

  /**
   * Generates a new hill of random size and height at a random position in
   * the heightmap. This is the actual Hill algorithm. The <code>Random</code>
   * object is used to guarantee the same heightmap for the same seed and
   * attributes.
   *
   * @param tempBuffer the temporary height map buffer
   * @param random     the random number generator
   */
  protected void addHill(float[][] tempBuffer, Random random) {
    // Pick the radius for the hill
    float radius = randomRange(random, minRadius, maxRadius);

    // Pick a centerpoint for the hill
    float x = randomRange(random, -radius, size + radius);
    float y = randomRange(random, -radius, size + radius);

    float radiusSq = radius * radius;
    float distSq;
    float height;

    // Find the range of hills affected by this hill
    int xMin = Math.round(x - radius - 1);
    int xMax = Math.round(x + radius + 1);

    int yMin = Math.round(y - radius - 1);
    int yMax = Math.round(y + radius + 1);

    // Don't try to affect points outside the heightmap
    if (xMin < 0) {
      xMin = 0;
    }
    if (xMax > size) {
      xMax = size - 1;
    }

    if (yMin < 0) {
      yMin = 0;
    }
    if (yMax > size) {
      yMax = size - 1;
    }

    for (int i = xMin; i <= xMax; i++) {
      for (int j = yMin; j <= yMax; j++) {
        distSq = (x - i) * (x - i) + (y - j) * (y - j);
        height = radiusSq - distSq;

        if (height > 0) {
          tempBuffer[i][j] += height;
        }
      }
    }
  }

  private float randomRange(Random random, float min, float max) {
    return (random.nextInt() * (max - min) / Integer.MAX_VALUE) + min;
  }

  /**
   * Sets the number of hills to grow. More hills usually mean a nicer
   * heightmap.
   *
   * @param iterations the number of hills to grow
   * @throws Exception if iterations is not greater than zero
   */
  public void setIterations(int iterations) throws Exception {
    if (iterations <= 0) {
      throw new Exception(
              "Number of iterations is not greater than zero");
    }
    this.iterations = iterations;
  }

  /**
   * Sets the minimum radius of a hill.
   *
   * @param maxRadius the maximum radius of a hill
   * @throws Exception if the maximum radius is not greater than zero or not greater
   *                   than the minimum radius
   */
  public void setMaxRadius(float maxRadius) throws Exception {
    if (maxRadius <= 0 || maxRadius <= minRadius) {
      throw new Exception("The maximum radius is not greater than 0, "
              + "or not greater than the minimum radius");
    }
    this.maxRadius = maxRadius;
  }

  /**
   * Sets the maximum radius of a hill.
   *
   * @param minRadius the minimum radius of a hill
   * @throws Exception if the minimum radius is not greater than zero or not
   *                   lower than the maximum radius
   */
  public void setMinRadius(float minRadius) throws Exception {
    if (minRadius <= 0 || minRadius >= maxRadius) {
      throw new Exception("The minimum radius is not greater than 0, "
              + "or not lower than the maximum radius");
    }
    this.minRadius = minRadius;
  }
}