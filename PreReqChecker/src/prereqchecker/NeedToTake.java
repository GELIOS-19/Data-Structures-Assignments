package prereqchecker;

import java.util.*;

/**
 * Steps to implement this class main method:
 *
 * Step 1:
 * AdjListInputFile name is passed through the command line as args[0]
 * Read from AdjListInputFile with the format:
 * 1. a (int): number of courses in the graph
 * 2. a lines, each with 1 course ID
 * 3. b (int): number of edges in the graph
 * 4. b lines, each with a source ID
 *
 * Step 2:
 * NeedToTakeInputFile name is passed through the command line as args[1]
 * Read from NeedToTakeInputFile with the format:
 * 1. One line, containing a course ID
 * 2. c (int): Number of courses
 * 3. c lines, each with one course ID
 *
 * Step 3:
 * NeedToTakeOutputFile name is passed through the command line as args[2]
 * Output to NeedToTakeOutputFile with the format:
 * 1. Some number of lines, each with one course ID
 */
public class NeedToTake {
  public static void main(String[] args) {
    if (args.length < 3) {
      System.out.println(
          "Usage: java -cp bin prereqchecker.NeedToTake <adjacency list input file> <need to take input file> <need to take output file>");
      return;
    }

    String adjListInputFile = args[0];
    String needToTakeInputFile = args[1];
    String outputFile = args[2];

    // Step 1
    Map<String, List<String>> adjList = AdjList.getAdjList(adjListInputFile);

    // Step 2
    StdIn.setFile(needToTakeInputFile);

    String target = StdIn.readLine();
    int numCourses = Integer.parseInt(StdIn.readLine());
    Set<String> takenCourses = new HashSet<>();
    for (int i = 0; i < numCourses; i++) {
      takenCourses.add(StdIn.readLine());
    }

    Set<String> completedCourses = AdjList.getAllPrereqs(adjList, takenCourses);

    // Step 3
    StdOut.setFile(outputFile);

    Set<String> needToTakeCourses =
        getNeedToTakeCourses(target, adjList, completedCourses);

    for (String courseID : needToTakeCourses) {
      StdOut.println(courseID);
    }
  }

  private static Set<String>
  getNeedToTakeCourses(String target, Map<String, List<String>> adjList,
                       Set<String> completedCourses) {
    Set<String> needToTakeCourses =
        AdjList.getAllPrereqs(adjList, new HashSet<>(Arrays.asList(target)));
    needToTakeCourses.removeAll(completedCourses);
    needToTakeCourses.remove(target);

    return needToTakeCourses;
  }
}
