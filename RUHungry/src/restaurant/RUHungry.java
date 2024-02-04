package restaurant;

import java.util.*;

/**
 * RUHungry is a fictitious restaurant. You will be running RUHungry for a day by seating guests,
 * taking orders, donation requests and restocking the pantry as necessary.
 *
 * <p>Compiling and executing: 1. use the run or debug function to run the driver and test your
 * methods
 *
 * @author Mary Buist
 * @author Kushi Sharma
 */
public class RUHungry {

    /*
     * Instance variables
     */

    // Menu: two parallel arrays. The index in one corresponds to the same index
    // in the other.
    private String[] categoryVar; // array where containing the name of menu
    // categories (e.g. Appetizer, Dessert).
    private MenuNode[] menuVar; // array of lists of MenuNodes where each index is a category.

    // Stock: hashtable using chaining to resolve collisions.
    private StockNode[] stockVar; // array of linked lists of StockNodes (use hashfunction to
    // organize Nodes: id % stockVarSize)
    private int stockVarSize;

    // Transactions: orders, donations, restock transactions are recorded
    private TransactionNode transactionVar; // refers to the first front node in linked list

    // Queue keeps track of people who've left the restaurant
    private Queue<People> leftQueueVar;

    // Tables
    private People[] tables; // array for people who are currently sitting
    private int[][] tablesInfo; // 2-D integer array where the first row contains

    // how many seats there are at each table (each
    // index) and the second row contains "0" or "1",
    // where 1 is the table is not available and 0 the
    // opposite

    /*
     * Default constructor
     */
    public RUHungry()
    {
        categoryVar = null;
        menuVar = null;
        stockVar = null;
        stockVarSize = 0;
        transactionVar = null;
        leftQueueVar = null;
        tablesInfo = null;
        tables = null;
    }

    /*
     * Get/Set methods
     */
    public MenuNode[] getMenu()
    {
        return menuVar;
    }

    public String[] getCategoryArray()
    {
        return categoryVar;
    }

    public StockNode[] getStockVar()
    {
        return stockVar;
    }

    public TransactionNode getFrontTransactionNode()
    {
        return transactionVar;
    }

    public TransactionNode resetFrontNode()
    {
        return transactionVar = null;
    } // method to reset the transactions for a new day

    public Queue<People> getLeftQueueVar()
    {
        return leftQueueVar;
    }

    public int[][] getTablesInfo()
    {
        return tablesInfo;
    }

    /*
     * Menu methods
     */

    /**
     * This method populates the two parallel arrays menuVar and categoryVar.
     *
     * <p>Each index of menuVar corresponds to the same index in categoryVar (a menu category like
     * Appetizers). If index 0 at categoryVar is Appetizers then menuVar at index 0 contains
     * MenuNodes of appetizer dishes.
     *
     * <p>1. read the input file: a) the first number corresponds to the number of categories (aka
     * length of menuVar and categoryVar) b) the next line states the name of the category (populate
     * CategoryVar as you read each category name) c) the next number represents how many dishes are
     * in that category - this will be the size of the linked list in menuVar for this category d)
     * the next line states the name of the dish e) the first number in the next line represents how
     * many ingredient IDs there are f) the next few numbers (all in the 100s) are each the
     * ingredient ID
     *
     * <p>2. As you read through the input file: a) populate the categoryVar array b) populate
     * menuVar depending on which index (aka which category) you are in i) make a dish object (with
     * filled parameters -- don't worry about "price" and "profit" in the dish object for right now)
     * ii) create menuNode and insert at the front of menuVar (NOTE! there will be multiple
     * menuNodes in one index)
     *
     * @param inputFile - use menu.in file which contains all the dishes
     */
    public void menu(String inputFile)
    {
        StdIn.setFile(inputFile);

        int numCategories = Integer.parseInt(StdIn.readLine());

        this.menuVar = new MenuNode[numCategories];
        this.categoryVar = new String[numCategories];

        for (int i = 0; i < numCategories; i++) {
            String categoryName = StdIn.readLine();
            this.categoryVar[i] = categoryName;

            int numDishes = Integer.parseInt(StdIn.readLine());
            for (int j = 0; j < numDishes; j++) {
                String dishName = StdIn.readLine();

                String[] ingredientsLine = StdIn.readLine().split(" ");
                int numIngredients = Integer.parseInt(ingredientsLine[0]);
                int[] stockID = new int[numIngredients];

                for (int k = 0; k < numIngredients; k++) {
                    int ingredientID = Integer.parseInt(ingredientsLine[k + 1]);
                    stockID[k] = ingredientID;
                }

                Dish dish = new Dish(categoryName, dishName, stockID);

                if (menuVar[i] == null) {
                    MenuNode menuNode = new MenuNode(dish, null);
                    menuVar[i] = menuNode;
                } else {
                    MenuNode menuNode = new MenuNode(dish, menuVar[i]);
                    menuVar[i] = menuNode;
                }
            }
        }
    }

    /**
     * Find and return the MenuNode that contains the dish with dishName in the menuVar.
     *
     * <p>** GIVEN METHOD ** ** DO NOT EDIT **
     *
     * @param dishName - the name of the dish
     * @return the dish object corresponding to searched dish, null if dishName is not found.
     */
    public MenuNode findDish(String dishName)
    {
        MenuNode menuNode = null;

        // Search all categories since we don't know which category dishName is at
        for (int category = 0; category < menuVar.length; category++) {
            MenuNode ptr = menuVar[category]; // set ptr at the front (first menuNode)

            while (ptr != null) { // while loop that searches the LL of the category
                // to find the itemOrdered
                if (ptr.getDish().getDishName().equals(dishName)) {
                    return ptr;
                } else {
                    ptr = ptr.getNextMenuNode();
                }
            }
        }
        return menuNode;
    }

    /**
     * Find integer that corresponds to the index in menuVar and categoryVar arrays that has that
     * category
     *
     * <p>** GIVEN METHOD ** ** DO NOT EDIT **
     *
     * @param category - the category name
     * @return index of category in categoryVar
     */
    public int findCategoryIndex(String category)
    {
        int index = 0;
        for (int i = 0; i < categoryVar.length; i++) {
            if (category.equalsIgnoreCase(categoryVar[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    /*
     * Stockroom methods
     */

    /**
     * PICK UP LINE OF THE METHOD: *can I insert myself into your life? cuz you always help me sort
     * out my problems and bring stability to my mine*
     *
     * <p>*********** This method adds a StockNode into the stockVar hashtable.
     *
     * <p>1. get the id of the given newNode and use a hash function to get the index at which the
     * newNode is being inserted.
     *
     * <p>HASH FUNCTION: id % stockVarSize
     *
     * <p>2. insert at the front of the linked list at the specific index
     *
     * @param newNode - StockNode that needs to be inserted into StockVar
     */
    public void addStockNode(StockNode newNode)
    {
        int ingredientID = newNode.getIngredient().getID();
        int itemHash = this.hash(ingredientID);

        StockNode front = stockVar[itemHash];
        newNode.setNextStockNode(front);
        stockVar[itemHash] = newNode;
    }

    private int hash(int ingredientID)
    {
        return ingredientID % stockVarSize;
    }

    /**
     * This method deletes an ingredient (aka the specific stockNode) from stockVar.
     *
     * <p>1. find the node that corresponds to the ingredient (based on the ingredientName)
     *
     * <p>2. delete the node from stockVar a) you have to visit each index and look at each node in
     * the corresponding linked list b) this is NOT efficient. Hashtables are not good if you can't
     * use the key to find the item.
     *
     * @param ingredientName - name of the ingredient
     */
    public void deleteStockNode(String ingredientName)
    {
        for (int i = 0; i < this.stockVarSize; i++) {
            StockNode ptr = this.stockVar[i];
            StockNode prev = null;

            while (ptr != null) {
                if (ptr.getIngredient().getName().equalsIgnoreCase(ingredientName)) {
                    if (prev == null) {
                        stockVar[i] = ptr.getNextStockNode();
                    } else {
                        prev.setNextStockNode(ptr.getNextStockNode());
                    }
                    break;
                }

                prev = ptr;
                ptr = ptr.getNextStockNode();
            }
        }
    }

    /**
     * This method finds an ingredient from StockVar (given the ingredientID)
     *
     * <p>1. find the node based upon the ingredient ID (you can go to the specific index using the
     * hash function!) (a) this is an efficient search as it looks only at the linked list which the
     * key hash to 2. find and return the node
     *
     * @param ingredientID - the ID of the ingredient
     * @return the StockNode corresponding to the ingredientID, null otherwise
     */
    public StockNode findStockNode(int ingredientID)
    {
        int itemHash = this.hash(ingredientID);
        StockNode front = this.stockVar[itemHash];

        StockNode ptr = front;
        while (ptr != null) {
            if (ptr.getIngredient().getID() == ingredientID) {
                return ptr;
            }

            ptr = ptr.getNextStockNode();
        }

        return null;
    }

    /**
     * This method is to find an ingredient from StockVar (given the ingredient name).
     *
     * <p>** GIVEN METHOD ** ** DO NOT EDIT **
     *
     * @param ingredientName - the name of the ingredient
     * @return the specific ingredient StockNode, null otherwise
     */
    public StockNode findStockNode(String ingredientName)
    {
        StockNode stockNode = null;

        for (int index = 0; index < stockVar.length; index++) {
            StockNode ptr = stockVar[index];

            while (ptr != null) {
                if (ptr.getIngredient().getName().equalsIgnoreCase(ingredientName)) {
                    return ptr;
                } else {
                    ptr = ptr.getNextStockNode();
                }
            }
        }

        return stockNode;
    }

    /**
     * This method updates the stock amount of an ingredient.
     *
     * <p>1. you will be given the ingredientName **OR** the ingredientID: a) the ingredientName is
     * NOT null: find the ingredient and add the given stock amount to the current stock amount b)
     * the ingredientID is NOT -1: find the ingredient and add the given stock amount to the current
     * stock amount
     *
     * <p>(FOR FUTURE USE) SOMETIMES THE STOCK AMOUNT TO ADD MAY BE NEGATIVE (TO REMOVE STOCK)
     *
     * @param ingredientName - the name of the ingredient
     * @param ingredientID - the id of the ingredient
     * @param stockAmountToAdd - the amount to add to the current stock amount
     */
    public void updateStock(String ingredientName, int ingredientID, int stockAmountToAdd)
    {
        StockNode stockNode = null;
        if (ingredientName != null) {
            stockNode = this.findStockNode(ingredientName);
        } else if (!(ingredientID < 0)) {
            stockNode = this.findStockNode(ingredientID);
        }

        if (stockNode != null) {
            Ingredient ingredient = stockNode.getIngredient();
            ingredient.setStockLevel(ingredient.getStockLevel() + stockAmountToAdd);
        }
    }

    /**
     * PICK UP LINE OF THE METHOD: *are you a single ‘for’ loop? cuz i only have i’s for you*
     *
     * <p>*********** This method goes over menuVar to update the price and profit of each dish,
     * using the stockVar hashtable to lookup for ingredient's costs.
     *
     * <p>1. For each dish in menuVar, add up the cost for each ingredient (found in stockVar), and
     * multiply the total by 1.2 to get the final price. a) update the price of each dish HINT! -->
     * you can use the methods you've previously made!
     *
     * <p>2. Calculate the profit of each dish by getting the totalPrice of ingredients and
     * subtracting from the price of the dish itself.
     */
    public void updatePriceAndProfit()
    {
        for (int i = 0; i < this.menuVar.length; i++) {
            MenuNode front = this.menuVar[i];

            MenuNode ptr = front;
            while (ptr != null) {
                Dish dish = ptr.getDish();
                int[] stockID = dish.getStockID();
                double cost = 0d;
                final double MARKUP = 1.2;

                for (int j = 0; j < stockID.length; j++) {
                    StockNode stockNode = this.findStockNode(stockID[j]);

                    if (stockNode != null) {
                        Ingredient ingredient = stockNode.getIngredient();
                        cost += ingredient.getCost();
                    }
                }

                dish.setPriceOfDish(MARKUP * cost);
                dish.setProfit(dish.getPriceOfDish() - cost);

                ptr = ptr.getNextMenuNode();
            }
        }
    }

    /**
     * PICK UP LINE OF THE METHOD: *are you a decimal? cuz the thought of you always floats in my
     * head and the two of use would make double*
     *
     * <p>************ This method initializes and populates stockVar which is a hashtable where
     * each index contains a linked list with StockNodes.
     *
     * <p>1. set and read the inputFile (stock.in): a) first integer (on line 1) is the size of
     * StockVar *** update stockVarSize AND create the stockVar array *** b) first integer of next
     * line represents the ingredientID i) example: 101 on line 2 c) use StdIn.readChar() to get rid
     * of the space between the id and the name d) the string that follows is the ingredient name
     * (NOTE! --> there are spaces between certain strings) i) example: Lettuce e) the double on the
     * next line corresponds to the ingredient's cost i) example: 3.12 on line 3 f) the next integer
     * is the stock amount for that ingredient i) example: 30 on line 3
     *
     * <p>2. create a Ingredient object followed by a StockNode then add to stockVar HINT! --> you
     * may use previous methods written to help you!
     *
     * @param inputFile - the input file with the ingredients and all their information (stock.in)
     */
    public void createStockHashTable(String inputFile)
    {
        StdIn.setFile(inputFile);

        this.stockVarSize = Integer.parseInt(StdIn.readLine());
        this.stockVar = new StockNode[stockVarSize];

        while (!StdIn.isEmpty()) {
            String[] firstLine = StdIn.readLine().split(" ");
            String[] secondLine = StdIn.readLine().split(" ");

            int id = Integer.parseInt(firstLine[0]);
            String name = String.join(" ", Arrays.copyOfRange(firstLine, 1, firstLine.length)).trim();
            double price = Double.parseDouble(secondLine[0]);
            int amount = Integer.parseInt(secondLine[1]);

            Ingredient ingredient = new Ingredient(id, name, amount, price);
            StockNode stockNode = new StockNode(ingredient, null);

            this.addStockNode(stockNode);
        }
    }

    /*
     * Transaction methods
     */

    /**
     * This method adds a TransactionNode to the END of the transactions linked list. The front of
     * the list is transactionVar.
     *
     * <p>1. create a new TransactionNode with the TransactionData paramenter. 2. add the
     * TransactionNode at the end of the linked list transactionVar.
     *
     * @param data - TransactionData node to be added to transactionVar
     */
    public void addTransactionNode(TransactionData data)
    { // method adds new transactionNode
        // to the end of LL
        TransactionNode transactionNode = new TransactionNode(data, null);

        if (this.transactionVar == null) {
            this.transactionVar = transactionNode;
        } else {
            TransactionNode ptr = this.transactionVar;
            while (ptr.getNext() != null) {
                ptr = ptr.getNext();
            }

            ptr.setNext(transactionNode);
        }
    }

    /**
     * PICK UP LINE OF THE METHOD: *are you the break command? cuz everything else stops when I see
     * you*
     *
     * <p>************* This method checks if there's enough in stock to prepare a dish.
     *
     * <p>1. use findDish() method to find the menuNode node for dishName
     *
     * <p>2. retrieve the Dish, then traverse ingredient array within the Dish
     *
     * <p>3. return boolean based on whether you can sell the dish or not HINT! --> once you
     * determine you can't sell the dish, break and return
     *
     * @param dishName - String of dish that's being requested
     * @param numberOfDishes - int of how many of that dish is being ordered
     * @return boolean
     */
    public boolean checkDishAvailability(String dishName, int numberOfDishes)
    {
        MenuNode menuNode = this.findDish(dishName);
        Dish dish = menuNode.getDish();

        for (int stockID : dish.getStockID()) {
            StockNode stockNode = this.findStockNode(stockID);
            Ingredient ingredient = stockNode.getIngredient();

            if (ingredient.getStockLevel() < numberOfDishes) {
                return false;
            }
        }

        return true;
    }

    /**
     * PICK UP LINE OF THE METHOD: *if you were a while loop and I were a boolean, we could run
     * together forever because I’ll always stay true to you*
     *
     * <p>*************** This method simulates a customer ordering a dish. Use the
     * checkDishAvailability() method to check whether the dish can be ordered.
     *
     * <p>If the dish can be prepared - create a TransactionData object of type "order" where the
     * item is the dishName, the amount is the quantity being ordered, and profit is the dish profit
     * multiplied by quantity. - then add the transaction as a successful transaction (call
     * addTransactionNode()) and updates the stock accordingly.
     *
     * <p>If the dish cannot be prepared - create a TransactionData object of type "order" where the
     * item is the dishName, the amount is the quantity being ordered, and profit is 0 (zero). -
     * then add the transaction as an UNsuccessful transaction and, - simulate the customer trying
     * to order other dishes in the same category linked list: - if the dish that comes right after
     * the dishName can be prepared, great. If not, try the next one and so on. - you might have to
     * traverse through the entire category searching for a dish that can be prepared. If you reach
     * the end of the list, start from the beginning until you have visited EVERY dish in the
     * category. - It is possible that no dish in the entire category can be prepared. - Note: the
     * next dish the customer chooses is always the one that comes right after the one that could
     * not be prepared.
     *
     * @param dishName - String of dish that's been ordered
     * @param quantity - int of how many of that dish has been ordered
     */
    public void order(String dishName, int quantity)
    {
        MenuNode menuNode = this.findDish(dishName);
        Dish dish = menuNode.getDish();

        this.orderVisitedMenuNodes.add(menuNode);

        if (this.checkDishAvailability(dishName, quantity)) {
            TransactionData transaction = new TransactionData(
                "order", dishName, quantity, dish.getProfit() * quantity, true);
            this.addTransactionNode(transaction);

            for (int stockID : dish.getStockID()) {
                this.updateStock(null, stockID, -quantity);
            }

            this.orderVisitedMenuNodes.clear();
        } else {
            TransactionData transaction = new TransactionData("order", dishName, quantity, 0, false);
            this.addTransactionNode(transaction);

            MenuNode nextMenuNode = menuNode.getNextMenuNode();

            if (nextMenuNode == null) {
                int categoryID = this.findCategoryIndex(dish.getCategory());
                nextMenuNode = this.menuVar[categoryID];
            }

            if (this.orderVisitedMenuNodes.contains(nextMenuNode)) {
                this.orderVisitedMenuNodes.clear();
                return;
            }

            Dish nextDish = nextMenuNode.getDish();
            this.order(nextDish.getDishName(), quantity);
        }
    }

    private ArrayList<MenuNode> orderVisitedMenuNodes = new ArrayList<>();

    /**
     * This method returns the total profit for the day
     *
     * <p>The profit is computed by traversing the transaction linked list (transactionVar) adding
     * up all the profits for the day
     *
     * @return profit - double value of the total profit for the day
     */
    public double profit()
    {
        double profit = 0d;

        TransactionNode ptr = this.transactionVar;
        while (ptr != null) {
            profit += ptr.getData().getProfit();
            ptr = ptr.getNext();
        }

        return profit;
    }

    /**
     * This method simulates donation requests, successful or not.
     *
     * <p>1. check whether the profit is > 50 and whether there's enough ingredients in stock.
     *
     * <p>2. add transaction to transactionVar
     *
     * @param ingredientName - String of ingredient that's been requested
     * @param quantity - int of how many of that ingredient has been ordered
     * @return void
     */
    public void donation(String ingredientName, int quantity)
    {
        StockNode stockNode = this.findStockNode(ingredientName);
        Ingredient ingredient = stockNode.getIngredient();
        double profit = this.profit();

        if (profit > 50d && ingredient.getStockLevel() >= quantity) {
            TransactionData transaction = new TransactionData("donation", ingredientName, quantity, 0, true);
            this.addTransactionNode(transaction);

            this.updateStock(null, ingredient.getID(), -quantity);
        } else {
            TransactionData transaction = new TransactionData("donation", ingredientName, quantity, 0, false);
            this.addTransactionNode(transaction);
        }
    }

    /**
     * This method simulates restock orders
     *
     * <p>1. check whether the profit is sufficient to pay for the total cost of ingredient a) (how
     * much each ingredient costs) * (quantity) b) if there is enough profit, adjust stock and
     * profit accordingly
     *
     * <p>2. add transaction to transactionVar
     *
     * @param ingredientName - ingredient that's been requested
     * @param quantity - how many of that ingredient needs to be ordered
     */
    public void restock(String ingredientName, int quantity)
    {
        StockNode stockNode = this.findStockNode(ingredientName);
        Ingredient ingredient = stockNode.getIngredient();
        double profit = this.profit();

        double cost = ingredient.getCost() * quantity;

        if (cost <= profit) {
            TransactionData transaction = new TransactionData("restock", ingredientName, quantity, -cost, true);
            this.addTransactionNode(transaction);

            this.updateStock(null, ingredient.getID(), quantity);
        } else {
            TransactionData transaction = new TransactionData("restock", ingredientName, quantity, 0, false);
            this.addTransactionNode(transaction);
        }
    }

    /*
     * Seat guests/customers methods
     */

    /**
     * Method to populate tables (which is a 2d integer array) based upon input file
     *
     * <p>First row of tables[][]: contains the total number of seats available at a table (each
     * table is an index in first row) Second row of tables[][]: initializes all indices to 0 0 -->
     * table is available 1 --> table is occupied
     *
     * <p>** GIVEN METHOD ** ** DO NOT EDIT **
     *
     * @param inputFile - tables1.in (contains all the tables in the RUHungry restaurant)
     * @return void (aka nothing)
     */
    public void createTables(String inputFile)
    {
        StdIn.setFile(inputFile);
        int numberOfTables = StdIn.readInt();
        tablesInfo = new int[2][numberOfTables];
        tables = new People[numberOfTables];

        for (int t = 0; t < numberOfTables; t++) {
            tablesInfo[0][t] = StdIn.readInt() * StdIn.readInt();
        }
    }

    /**
     * PICK UP LINE OF THE METHOD: *are you a linked list? cuz nothing could stock up to you and
     * you’re pretty queue(te)*
     *
     * <p>*************** This method simulates seating guests at tables. You are guaranteed to be
     * able to sit everyone from the waitingQueue eventually.
     *
     * <p>1. initialize leftQueueVar a People queue that represents the people that have left the
     * restaurant
     *
     * <p>2. while there are people waiting to be sat: - Starting from index 0 (zero), seat the next
     * party in the first available table that fits their party. - If there is no available table
     * for the next party, kick a party out from the tables array: 1. starting at index 0 (zero),
     * find the first table big enough to hold the next party in line. 2. remove the current party,
     * add them to the leftQueueVar. 3. seat the next party in line.
     *
     * <p>tableInfo contains the number of seats per table as well as if the table is occupied or
     * not. tables contains the People object (party) currently at the table.
     *
     * <p>Note: After everyone has been seated (waitingQueue is empty), remove all the parties from
     * tables and add then to the leftQueueVar.
     *
     * @param waitingQueue - queue containing people waiting to be seated (each element in queue is
     *     a People <-- you are given this class!)
     */
    public void seatAllGuests(Queue<People> waitingQueue)
    {

        // WRITE YOUR CODE HERE
    }
}
