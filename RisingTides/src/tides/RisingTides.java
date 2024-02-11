package tides;

import java.util.*;

/**
 * This class contains methods that provide information about select terrains
 * using 2D arrays. Uses floodfill to flood given maps and uses that information
 * to understand the potential impacts. Instance Variables: - a double array for
 * all the heights for each cell - a GridLocation array for the sources of water
 * on empty terrain
 *
 * @author Original Creator Keith Scharz (NIFTY STANFORD)
 * @author Vian Miranda (Rutgers University)
 */
public class RisingTides {
  // Instance variables
  private final double[][] terrain; // an array for all the heights for each
                                    // cell
  private final GridLocation[] sources; // an array for the sources of water on
                                        // empty terrain

  /**
   * DO NOT EDIT! Constructor for RisingTides.
   *
   * @param terrain passes in the selected terrain
   */
  public RisingTides(Terrain terrain) {
    this.terrain = terrain.heights;
    this.sources = terrain.sources;
  }

  /**
   * Find the lowest and highest point of the terrain and output it.
   *
   * @return double[][], with index 0 and index 1 being the lowest and highest
   *         points of the terrain, respectively
   */
  public double[] elevationExtrema() {
    double min = this.terrain[0][0];
    double max = this.terrain[0][0];
    for (double[] row : this.terrain) {
      for (double cell : row) {
        if (cell < min) {
          min = cell;
        }
        if (cell > max) {
          max = cell;
        }
      }
    }
    return new double[] { min, max };
  }

  /**
   * Implement the floodfill algorithm using the provided terrain and sources.
   * <p>
   * All water originates from the source GridLocation. If the height of the
   * water is greater than that of the neighboring terrain, flood the cells.
   * Repeat iteratively till the neighboring terrain is higher than the water
   * height.
   *
   * @param height of the water
   * @return boolean[][], where flooded cells are true, otherwise false
   */
  public boolean[][] floodedRegionsIn(double height) {
    boolean[][] resultingArray = new boolean[terrain.length][terrain[0].length];
    ArrayList<GridLocation> floodedCells = new ArrayList<>();
    final int[][] DIRECTIONS = { { -1, 0 }, // north
        { 1, 0 }, // south
        { 0, -1 }, // west
        { 0, 1 }, // east
    };
    for (GridLocation source : this.sources) {
      floodedCells.add(source);
      resultingArray[source.row][source.col] = true;
    }
    while (floodedCells.size() != 0) {
      GridLocation curr = floodedCells.remove(0);
      for (int[] direction : DIRECTIONS) {
        GridLocation neighbor = new GridLocation(curr.row + direction[0],
            curr.col + direction[1]);
        try {
          if (this.terrain[neighbor.row][neighbor.col] <= height
              && !resultingArray[neighbor.row][neighbor.col]) {
            floodedCells.add(neighbor);
            resultingArray[neighbor.row][neighbor.col] = true;
          }
        } catch (IndexOutOfBoundsException e) {
          // do nothing
        }
      }
    }
    return resultingArray;
  }

  /**
   * Checks if a given cell is flooded at a certain water height.
   *
   * @param height of the water
   * @param cell   location
   * @return boolean, true if cell is flooded, otherwise false
   */
  public boolean isFlooded(double height, GridLocation cell) {
    return this.floodedRegionsIn(height)[cell.row][cell.col];
  }

  /**
   * Given the water height and a GridLocation find the difference between the
   * chosen cells height and the water height.
   * <p>
   * If the return value is negative, the Driver will display "meters below" If
   * the return value is positive, the Driver will display "meters above" The
   * value displayed will be positive.
   *
   * @param height of the water
   * @param cell   location
   * @return double, representing how high/deep a cell is above/below water
   */
  public double heightAboveWater(double height, GridLocation cell) {
    double landHeight = this.terrain[cell.row][cell.col];
    return landHeight - height;
  }

  /**
   * Total land available (not underwater) given a certain water height.
   *
   * @param height of the water
   * @return int, representing every cell above water
   */
  public int totalVisibleLand(double height) {
    int total = 0;
    boolean[][] flooded = this.floodedRegionsIn(height);
    for (int row = 0; row < flooded.length; row++) {
      for (int col = 0; col < flooded[0].length; col++) {
        if (!flooded[row][col]) {
          total++;
        }
      }
    }
    return total;
  }

  /**
   * Given 2 heights, find the difference in land available at each height.
   * <p>
   * If the return value is negative, the Driver will display "Will gain" If the
   * return value is positive, the Driver will display "Will lose" The value
   * displayed will be positive.
   *
   * @param height    of the water
   * @param newHeight the future height of the water
   * @return int, representing the amount of land lost or gained
   */
  public int landLost(double height, double newHeight) {
    return this.totalVisibleLand(height) - this.totalVisibleLand(newHeight);
  }

  /**
   * Count the total number of islands on the flooded terrain.
   * <p>
   * Parts of the terrain are considered "islands" if they are completely
   * surround by water in all 8-directions. Should there be a direction (ie.
   * left corner) where a certain piece of land is connected to another
   * landmass, this should be considered as one island. A better example would
   * be if there were two landmasses connected by one cell. Although seemingly
   * two islands, after further inspection it should be realized this is one
   * single island. Only if this connection were to be removed (height of water
   * increased) should these two landmasses be considered two separate islands.
   *
   * @param height of the water
   * @return int, representing the total number of islands
   */
  public int numOfIslands(double height) {
    int numOfIslands = 0;
    boolean[][] flooded = this.floodedRegionsIn(height);
    WeightedQuickUnionUF uf = new WeightedQuickUnionUF(flooded.length,
        flooded[0].length);
    final int[][] DIRECTIONS = { { -1, -1 }, // top left
        { -1, 0 }, // top middle
        { -1, 1 }, // top right
        { 0, -1 }, // middle left
        { 0, 1 }, // middle right
        { 1, -1 }, // bottom left
        { 1, 0 }, // bottom middle
        { 1, 1 }, // bottom right
    };
    for (int row = 0; row < flooded.length; row++) {
      for (int col = 0; col < flooded[0].length; col++) {
        if (!flooded[row][col]) {
          GridLocation curr = new GridLocation(row, col);
          for (int[] direction : DIRECTIONS) {
            GridLocation neighbor = new GridLocation(row + direction[0],
                col + direction[1]);
            try {
              if (!flooded[neighbor.row][neighbor.col]
                  && !uf.find(neighbor).equals(uf.find(curr))) {
                uf.union(curr, neighbor);
              }
            } catch (IndexOutOfBoundsException e) {
              // do nothing
            }
          }
        }
      }
    }
    for (int row = 0; row < flooded.length; row++) {
      for (int col = 0; col < flooded[0].length; col++) {
        if (!flooded[row][col] && uf.find(new GridLocation(row, col))
            .equals(new GridLocation(row, col))) {
          numOfIslands++;
        }
      }
    }
    return numOfIslands;
  }
}
