package prereqchecker;

import java.util.*;

/**
 * Steps to implement this class main method:
 *
 * <p>Step 1: AdjListInputFile name is passed through the command line as args[0] Read from
 * AdjListInputFile with the format: 1. a (int): number of courses in the graph 2. a lines, each
 * with 1 course ID 3. b (int): number of edges in the graph 4. b lines, each with a source ID
 *
 * <p>Step 2: ValidPreReqInputFile name is passed through the command line as args[1] Read from
 * ValidPreReqInputFile with the format: 1. 1 line containing the proposed advanced course 2. 1 line
 * containing the proposed prereq to the advanced course
 *
 * <p>Step 3: ValidPreReqOutputFile name is passed through the command line as args[2] Output to
 * ValidPreReqOutputFile with the format: 1. 1 line, containing either the word "YES" or "NO"
 */
public class ValidPrereq {
  public static void main(String[] args) {
    if (args.length < 3) {
      StdOut.println(
          "Execute: java -cp bin prereqchecker.ValidPrereq <adjacency list INput file> <valid"
              + " prereq INput file> <valid prereq OUTput file>");
      return;
    }
    String adjListInputFile = args[0];
    String validPrereqInputFile = args[1];
    String outputFile = args[2];
    // Step 1
    Map<String, List<String>> adjList = AdjList.getAdjList(adjListInputFile);
    // Step 2
    StdIn.setFile(validPrereqInputFile);
    String advancedCourse = StdIn.readLine();
    String proposedPrereq = StdIn.readLine();
    // Step 3
    boolean isPossible = !createsCycle(adjList, proposedPrereq, advancedCourse);
    StdOut.setFile(outputFile);
    if (isPossible) {
      StdOut.println("YES");
    } else {
      StdOut.println("NO");
    }
  }

  // Recursive method to detect a cycle in the graph
  private static boolean hasCycle(
      Map<String, List<String>> adjList, String current, String target, Set<String> visited) {
    // If the current course is the target, a cycle would be created
    if (current.equals(target)) {
      return true;
    }
    // Skip processing if the current course is already visited
    if (visited.contains(current)) {
      return false;
    }
    visited.add(current);
    // Recursively check all predecessors to see if the target can
    // be reached
    for (String neighbor : adjList.getOrDefault(current, Collections.emptyList())) {
      if (hasCycle(adjList, neighbor, target, visited)) {
        return true;
      }
    }
    return false;
  }

  // Method to determine if adding a new prerequisite creates a cycle
  private static boolean createsCycle(
      Map<String, List<String>> adjList, String newPrereq, String start) {
    Set<String> visited = new HashSet<>();
    return hasCycle(adjList, newPrereq, start, visited);
  }
}
