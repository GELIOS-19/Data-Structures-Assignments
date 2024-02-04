package prereqchecker;

import java.util.*;

/**
 *
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
 * EligibleInputFile name is passed through the command line as args[1]
 * Read from EligibleInputFile with the format:
 * 1. c (int): Number of courses
 * 2. c lines, each with 1 course ID
 *
 * Step 3:
 * EligibleOutputFile name is passed through the command line as args[2]
 * Output to EligibleOutputFile with the format:
 * 1. Some number of lines, each with one course ID
 */
public class Eligible {
  public static void main(String[] args) {
    if (args.length < 3) {
      StdOut.println(
          "Execute: java -cp bin prereqchecker.Eligible <adjacency list INput file> <eligible INput file> <eligible OUTput file>");
      return;
    }

    String adjListInputFile = args[0];
    String eligibleInputFile = args[1];
    String outputFile = args[2];

    // Step 1
    Map<String, List<String>> adjList = AdjList.getAdjList(adjListInputFile);

    // Step 2
    StdIn.setFile(eligibleInputFile);

    int numCourses = Integer.parseInt(StdIn.readLine());
    Set<String> takenCourses = new HashSet<>();
    for (int i = 0; i < numCourses; i++) {
      takenCourses.add(StdIn.readLine());
    }

    Set<String> completedCourses = AdjList.getAllPrereqs(adjList, takenCourses);

    // Step 3
    StdOut.setFile(outputFile);

    Set<String> eligibleCourses = getEligibleCourses(adjList, completedCourses);
    for (String courseID : eligibleCourses) {
      StdOut.println(courseID);
    }
  }

  private static Set<String>
  getEligibleCourses(Map<String, List<String>> adjList,
                     Set<String> completedCourses) {
    Set<String> eligibleCourses = new HashSet<>();
    for (String course : adjList.keySet()) {
      if (!completedCourses.contains(course)) {
        List<String> prereqs = adjList.get(course);
        if (prereqs == null || completedCourses.containsAll(prereqs)) {
          eligibleCourses.add(course);
        }
      }
    }
    return eligibleCourses;
  }
}
