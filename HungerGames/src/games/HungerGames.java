package games;

import java.util.*;

/**
 * This class contains methods to represent the Hunger Games using BSTs. Moves
 * people from input files to districts, eliminates people from the game, and
 * determines a possible winner.
 *
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {
  private ArrayList<District> districts; // all districts in Panem.
  private TreeNode game; // root of the BST. The BST contains districts that are
                         // still in the game.

  /**
   * ***** DO NOT REMOVE OR UPDATE this method ********* Default constructor,
   * initializes a list of districts.
   */
  public HungerGames() {
    districts = new ArrayList<>();
    game = null;
    StdRandom.setSeed(2023);
  }

  /**
   * ***** DO NOT REMOVE OR UPDATE this method ********* Sets up Panem, the
   * universe in which the Hunger Games takes place. Reads districts and people
   * from the input file.
   *
   * @param filename will be provided by client to read from using StdIn
   */
  public void setupPanem(String filename) {
    StdIn.setFile(filename); // open the file - happens only once here
    setupDistricts(filename);
    setupPeople(filename);
  }

  /**
   * Reads the following from input file: - Number of districts - District ID's
   * (insert in order of insertion) Insert districts into the districts
   * ArrayList in order of appearance.
   *
   * @param filename will be provided by client to read from using StdIn
   */
  public void setupDistricts(String filename) {
    StdIn.setFile(filename);
    int numDistricts = StdIn.readInt();
    for (int i = 0; i < numDistricts; i++) {
      int id = StdIn.readInt();
      District district = new District(id);
      this.districts.add(district);
    }
  }

  /**
   * Reads the following from input file (continues to read from the SAME input
   * file as setupDistricts()): Number of people Space-separated: first name,
   * last name, birth month (1-12), age, district id, effectiveness Districts
   * will be initialized to the instance variable districts
   * <p>
   * Persons will be added to corresponding district in districts defined by
   * districtID
   *
   * @param filename will be provided by client to read from using StdIn
   */
  public void setupPeople(String filename) {
    StdIn.setFile(filename);
    int numDistricts = StdIn.readInt();
    for (int i = 0; i <= numDistricts; i++) {
      StdIn.readLine();
    }
    int numPeople = StdIn.readInt();
    StdIn.readLine();
    for (int i = 0; i < numPeople; i++) {
      String[] personData = StdIn.readLine().split(" ");
      String firstName = personData[0];
      String lastName = personData[1];
      int birthMonth = Integer.parseInt(personData[2]);
      int age = Integer.parseInt(personData[3]);
      int districtId = Integer.parseInt(personData[4]);
      int effectiveness = Integer.parseInt(personData[5]);
      // Create the person
      Person person = new Person(birthMonth, firstName, lastName, age,
          districtId, effectiveness);
      person.setTessera(age >= 12 && age < 18);
      // Add the person to their corresponding district
      District district = this.districts.stream()
          .filter(d -> d.getDistrictID() == districtId).findFirst().get();
      if (birthMonth % 2 == 0) {
        district.addEvenPerson(person);
      } else if (birthMonth % 2 == 1) {
        district.addOddPerson(person);
      }
    }
  }

  /**
   * Adds a district to the game BST. If the district is already added, do
   * nothing
   *
   * @param root        the TreeNode root which we access all the added
   *                      districts
   * @param newDistrict the district we wish to add
   */
  public void addDistrictToGame(TreeNode root, District newDistrict) {
    this.districts.remove(newDistrict);
    if (this.game == null) {
      this.game = new TreeNode(newDistrict, null, null);
    } else {
      if (newDistrict.getDistrictID() < root.getDistrict().getDistrictID()) {
        if (root.getLeft() == null) {
          root.setLeft(new TreeNode(newDistrict, null, null));
        } else {
          this.addDistrictToGame(root.getLeft(), newDistrict);
        }
      } else if (newDistrict.getDistrictID() > root.getDistrict()
          .getDistrictID()) {
        if (root.getRight() == null) {
          root.setRight(new TreeNode(newDistrict, null, null));
        } else {
          this.addDistrictToGame(root.getRight(), newDistrict);
        }
      }
    }
  }

  /**
   * Searches for a district inside of the BST given the district id.
   *
   * @param id the district to search
   * @return the district if found, null if not found
   */
  public District findDistrict(int id) {
    TreeNode current = this.game;
    while (current != null) {
      int districtId = current.getDistrict().getDistrictID();
      if (districtId == id) {
        return current.getDistrict();
      } else if (districtId > id) {
        current = current.getLeft();
      } else {
        current = current.getRight();
      }
    }
    return null;
  }

  /**
   * Selects two duelers from the tree, following these rules: - One odd person
   * and one even person should be in the pair. - Dueler with Tessera (age
   * 12-18, use tessera instance variable) must be retrieved first. - Find the
   * first odd person and even person (separately) with Tessera if they exist. -
   * If you can't find a person, use StdRandom.uniform(x) where x is the
   * respective population size to obtain a dueler. - Add odd person dueler to
   * person1 of new DuelerPair and even person dueler to person2. - People from
   * the same district cannot fight against each other.
   *
   * @return the pair of dueler retrieved from this method.
   */
  public DuelPair selectDuelers() {
    Person oddDueler = null;
    Person evenDueler = null;
    Stack<TreeNode> preOrderTraversal = new Stack<>();
    ArrayList<Integer> selectedDistrictIDs = new ArrayList<>();
    // In this traversal we only focus on finding duelers with tessera
    preOrderTraversal.push(this.game);
    while (!preOrderTraversal.isEmpty()) {
      if (oddDueler != null && evenDueler != null) {
        break;
      }
      TreeNode node = preOrderTraversal.pop();
      if (node.getRight() != null) {
        preOrderTraversal.push(node.getRight());
      }
      if (node.getLeft() != null) {
        preOrderTraversal.push(node.getLeft());
      }
      if (oddDueler == null) {
        oddDueler = node.getDistrict().getOddPopulation().stream()
            .filter(Person::getTessera).findFirst().orElse(null);
        if (oddDueler != null) {
          selectedDistrictIDs.add(node.getDistrict().getDistrictID());
          continue;
        }
      }
      if (evenDueler == null) {
        evenDueler = node.getDistrict().getEvenPopulation().stream()
            .filter(Person::getTessera).findFirst().orElse(null);
        if (evenDueler != null)
          selectedDistrictIDs.add(node.getDistrict().getDistrictID());
      }
    }
    // We must do a second traversal of the tree to find random duelers if
    // they didn't exist previously
    preOrderTraversal.clear();
    preOrderTraversal.push(this.game);
    while (!preOrderTraversal.isEmpty()) {
      if (oddDueler != null && evenDueler != null) {
        break;
      }
      TreeNode node = preOrderTraversal.pop();
      if (node.getRight() != null) {
        preOrderTraversal.push(node.getRight());
      }
      if (node.getLeft() != null) {
        preOrderTraversal.push(node.getLeft());
      }
      if (selectedDistrictIDs.contains(node.getDistrict().getDistrictID())) {
        continue;
      }
      if (oddDueler == null) {
        oddDueler = node.getDistrict().getOddPopulation().get(
            StdRandom.uniform(node.getDistrict().getOddPopulation().size()));
        continue;
      }
      evenDueler = node.getDistrict().getEvenPopulation().get(
          StdRandom.uniform(node.getDistrict().getEvenPopulation().size()));
    }
    this.findDistrict(oddDueler.getDistrictID()).getOddPopulation()
        .remove(oddDueler);
    this.findDistrict(evenDueler.getDistrictID()).getEvenPopulation()
        .remove(evenDueler);
    return new DuelPair(oddDueler, evenDueler);
  }

  /**
   * Deletes a district from the BST when they are eliminated from the game.
   * Districts are identified by id's. If district does not exist, do nothing.
   * <p>
   * This is similar to the BST delete we have seen in class.
   *
   * @param id the ID of the district to eliminate
   */
  public void eliminateDistrict(int id) {
    // Find district
    TreeNode target = this.game;
    TreeNode parent = null;
    while (target != null) {
      int districtId = target.getDistrict().getDistrictID();
      if (districtId == id) {
        break;
      } else if (districtId > id) {
        parent = target;
        target = target.getLeft();
      } else if (districtId < id) {
        parent = target;
        target = target.getRight();
      }
    }
    // If target is null, the district is not found
    if (target == null)
      return;
    // Remove from districts array
    this.districts.remove(target.getDistrict());
    // Case 1: No children
    if (target.getLeft() == null && target.getRight() == null) {
      if (parent == null) {
        game = null;
      } else if (parent.getLeft() == target) {
        parent.setLeft(null);
      } else {
        parent.setRight(null);
      }
    }
    // Case 2: One child
    else if (target.getLeft() == null || target.getRight() == null) {
      TreeNode childNode = (target.getLeft() != null) ? target.getLeft()
          : target.getRight();
      if (parent == null) {
        game = childNode;
      } else if (parent.getLeft() == target) {
        parent.setLeft(childNode);
      } else {
        parent.setRight(childNode);
      }
    }
    // Case 3: Two children
    else {
      TreeNode successor = target.getRight();
      TreeNode successorParent = target;
      while (successor.getLeft() != null) {
        successorParent = successor;
        successor = successor.getLeft();
      }
      if (successorParent != target) {
        successorParent.setLeft(successor.getRight());
      } else {
        successorParent.setRight(successor.getRight());
      }
      target.setDistrict(successor.getDistrict());
    }
  }

  /**
   * Eliminates a dueler from a pair of duelers. - Both duelers in the DuelPair
   * argument given will duel - Winner gets returned to their District -
   * Eliminate a District if it only contains a odd person population or even
   * person population
   *
   * @param pair of persons to fight each other.
   */
  public void eliminateDueler(DuelPair pair) {
    Person oddDueler = pair.getPerson1();
    Person evenDueler = pair.getPerson2();
    // Incomplete pair, we send them back to their district
    if (oddDueler == null || evenDueler == null) {
      if (oddDueler != null) {
        District district = this.findDistrict(oddDueler.getDistrictID());
        district.addOddPerson(oddDueler);
      } else if (evenDueler != null) {
        District district = this.findDistrict(evenDueler.getDistrictID());
        district.addEvenPerson(evenDueler);
      }
    }
    // Duel
    Person winner = oddDueler.duel(evenDueler);
    Person loser = winner == oddDueler ? evenDueler : oddDueler;
    // Send the winner back
    District winnerDistrict = this.findDistrict(winner.getDistrictID());
    District loserDistrict = this.findDistrict(loser.getDistrictID());
    if (winner == oddDueler) {
      winnerDistrict.addOddPerson(oddDueler);
    } else if (winner == evenDueler) {
      winnerDistrict.addEvenPerson(evenDueler);
    }
    // Eliminate district
    int winnerDistrictOddPopulation = winnerDistrict.getOddPopulation().size();
    int winnerDistrictEvenPopulation = winnerDistrict.getEvenPopulation()
        .size();
    int winnerDistrictTotalPopulation = winnerDistrictOddPopulation
        + winnerDistrictEvenPopulation;
    int loserDistrictOddPopulation = loserDistrict.getOddPopulation().size();
    int loserDistrictEvenPopulation = loserDistrict.getEvenPopulation().size();
    int loserDistrictTotalPopulation = loserDistrictOddPopulation
        + loserDistrictEvenPopulation;
    if (winnerDistrictTotalPopulation == 0 || winnerDistrictEvenPopulation == 0
        || winnerDistrictOddPopulation == 0) {
      this.eliminateDistrict(winnerDistrict.getDistrictID());
    }
    if (loserDistrictTotalPopulation == 0 || loserDistrictEvenPopulation == 0
        || loserDistrictOddPopulation == 0) {
      this.eliminateDistrict(loserDistrict.getDistrictID());
    }
  }

  /**
   * ***** DO NOT REMOVE OR UPDATE this method *********
   * <p>
   * Obtains the list of districts for the Driver.
   *
   * @return the ArrayList of districts for selection
   */
  public ArrayList<District> getDistricts() {
    return this.districts;
  }

  /**
   * ***** DO NOT REMOVE OR UPDATE this method *********
   * <p>
   * Returns the root of the BST
   */
  public TreeNode getRoot() {
    return game;
  }
}
