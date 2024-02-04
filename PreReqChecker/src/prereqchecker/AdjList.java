package prereqchecker;

import java.util.*;

/**
 * Steps to implement this class main method:
 *
 * <p>Step 1: AdjListInputFile name is passed through the command line as args[0] Read from
 * AdjListInputFile with the format: 1. a (int): number of courses in the graph 2. a lines, each
 * with 1 course ID 3. b (int): number of edges in the graph 4. b lines, each with a source ID
 *
 * <p>Step 2: AdjListOutputFile name is passed through the command line as args[1] Output to
 * AdjListOutputFile with the format: 1. c lines, each starting with a different course ID, then
 * listing all of that course's prerequisites (space separated)
 */
public class AdjList {
    public static String[] courseIDs;

    public static void main(String[] args) {
        if (args.length < 2) {
            StdOut.println(
                    "Execute: java -cp bin prereqchecker.AdjList <adjacency list INput file> <adjacency list OUTput file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        // Step 1
        Map<String, List<String>> adjList = getAdjList(inputFile);

        // Step 2
        StdOut.setFile(outputFile);

        for (String courseID : courseIDs) {
            StdOut.print(courseID);
            List<String> prereqs = adjList.getOrDefault(courseID, Collections.emptyList());
            for (String prereq : prereqs) {
                StdOut.print(" " + prereq);
            }
            StdOut.println();
        }
    }

    public static Map<String, List<String>> getAdjList(String adjListInputFile) {
        StdIn.setFile(adjListInputFile);

        int numCourses = Integer.parseInt(StdIn.readLine());
        courseIDs = new String[numCourses];
        for (int i = 0; i < numCourses; i++) {
            courseIDs[i] = StdIn.readLine();
        }

        int numEdges = Integer.parseInt(StdIn.readLine());
        Map<String, List<String>> adjList =
                new HashMap<
                        String, List<String>>(); // Create an adjacency list while parsing the input
        for (int i = 0; i < numEdges; i++) {
            String[] edge = StdIn.readLine().split(" ");
            String source = edge[0];
            String target = edge[1];

            adjList.putIfAbsent(source, new ArrayList<String>());
            adjList.get(source).add(target);
        }

        return adjList;
    }

    public static Set<String> getAllPrereqs(
            Map<String, List<String>> adjList, Set<String> courses) {
        Queue<String> q = new LinkedList<>();
        q.addAll(courses);

        Set<String> allPrereqs = new HashSet<>();
        while (!q.isEmpty()) {
            String courseID = q.remove();
            List<String> coursePrereqs = adjList.get(courseID);
            if (coursePrereqs != null) {
                q.addAll(coursePrereqs);
            }
            allPrereqs.add(courseID);
        }

        return allPrereqs;
    }
}
