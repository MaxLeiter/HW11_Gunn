
import java.io.BufferedReader;
import java.io.InputStreamReader;
/*
 *Michael Gunn
 *HW #11
 *1/31/17
 */

public class AVL_Tree<Key extends Comparable<Key>, Value> {

    private static enum Balance {

        LEFT,
        EVEN,
        RIGHT;

        public String toString() {
            switch (this) {
                case LEFT:
                    return "/";
                case EVEN:
                    return "-";
                case RIGHT:
                    return "\\";
                default:
                    return "";
            }
        }
    }

    private class Node {

        Key key;
        Value value; 
        Node left;
        Node right;
        Balance balance;
        boolean grew;
        

        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.balance = Balance.EVEN;
            this.grew = false;
        }
    }
    
    private class Result {
        Node node;
        boolean grew;
        public Result (Node node, boolean grew) {
            this.node = node;
            this.grew = grew;
        }
    }
    
    private Result taller (Node node) {
        return new Result (node, true);
    }
    
    private Result sameHeight (Node node) {
        return new Result (node, false);
    }
    
    private Node root;

    public AVL_Tree() {
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    private int size(Node node) {
        if (node != null) {
            return size(node.left) + size(node.right) + 1;
        } else {
            return 0;
        }
    }

    public int size() {
        return size(this.root);
    }

    private int height(Node node) {
        if (node != null) {
            return 1 + Math.max(height(node.left), height(node.right));
        } else {
            return 0;
        }
    }

    public int height() {
        return height(this.root);
    }

    public Key min() {
        Key result = null;
        Node rover = this.root;
        while (rover != null) {
            result = rover.key;
            rover = rover.left;
        }
        return result;
    }

    public Key max() {
        Key result = null;
        Node rover = this.root;
        while (rover != null) {
            result = rover.key;
            rover = rover.right;
        }
        return result;
    }

    public Key floor(Key key) {
        Key result = null;
        Node rover = this.root;
        while (rover != null) {
            int compare = key.compareTo(rover.key);
            if (compare < 0) {
                rover = rover.left;
            } else if (compare > 0) {
                result = rover.key;
                rover = rover.right;
            } else {
                return rover.key;
            }
        }
        return result;
    }

    public Key ceiling(Key key) {
        Key result = null;
        Node rover = this.root;
        while (rover != null) {
            int compare = key.compareTo(rover.key);
            if (compare < 0) {
                result = rover.key;
                rover = rover.left;
            } else if (compare > 0) {
                rover = rover.right;
            } else {
                return rover.key;
            }
        }
        return result;
    }

    public Value find(Key key) {
        Node rover = this.root;
        while (rover != null) {
            int compare = key.compareTo(rover.key);
            if (compare < 0) {
                rover = rover.left;
            } else if (compare > 0) {
                rover = rover.right;
            } else {
                return rover.value;
            }
        }
        return null;
    }

    public boolean contains(Key key) {
        Node rover = this.root;
        while (rover != null) {
            int compare = key.compareTo(rover.key);
            if (compare < 0) {
                rover = rover.left;
            } else if (compare > 0) {
                rover = rover.right;
            } else {
                return true;
            }
        }
        return false;
    }

    private Result rebalanceLeftSubtree(Node node) {
        //do I have to track whether or not it already had one child (see if it grew 
        //or just turned into 2 kids) before I shift the balance factor over by 1?
        System.out.println("rebalance left");
        switch (node.balance) {
            case LEFT:
                if (node.left.balance == Balance.RIGHT) {
                    node = rotateLeftThenRight(node);
                    return sameHeight(node);
                }
                else {
                    node = rotateRight(node);
                    return sameHeight(node);
                }
            case EVEN:
                node.balance = Balance.LEFT;
                return taller(node);
            case RIGHT:
                node.balance = Balance.EVEN;
                return sameHeight(node);
        }
        return null; //unreachable
    }

    private Result rebalanceRightSubtree(Node node) {
        //do I have to track whether or not it already had one child (see if it grew 
        //or just turned into 2 kids) before I shift the balance factor over by 1?
        System.out.println("rebalance right");
        switch (node.balance) {
            case LEFT:
                node.balance = Balance.EVEN;
                return sameHeight(node);
            case EVEN:
                node.balance = Balance.RIGHT;
                return taller(node);
            case RIGHT:
                if (node.right.balance == Balance.LEFT) {
                    node = rotateRightThenLeft(node);
                    return sameHeight(node);
                }
                else {
                    node = rotateLeft(node);
                    return sameHeight(node);
                }
        }
        return null;
    }

   private Node rotateLeft(Node node) {                 
//           a
//            \ 
//             b
//              \
//               c
       System.out.println("called rotate left");
       Node a = node;
       Node b = node.right;
       a.right = b.left;
       b.left = a;
       b.balance = Balance.EVEN;
       a.balance = Balance.EVEN;
       return b;
   }
   
   private Node rotateRight(Node node) {
//            c
//           / 
//          b
//         /
//        a
       System.out.println("called rotate right");
       Node c = node;
       Node b = node.left;
       c.left = b.right;
       b.right = c;
       c.balance = Balance.EVEN;
       b.balance = Balance.EVEN;
       return b;
   }
   
   private Node rotateRightThenLeft(Node node) {
//           a          a
//            \          \
//             c -->      b  --> balanced
//            /            \
//           b              c
       System.out.println("called rotate right then left");
       Balance b = node.left.right.balance; //maybe get null pointer here
       node.right = rotateRight(node.right);
       node = rotateLeft(node);
//       Node a = node;
//       Node c = node.right;
//       Node b = node.right.left;
//       //right around the right side
//       a.right = b;
//       b.right = c;
//       
//       //left around the whole thing
//       a.right = b.left;
//       b.left = a;
//       return b;
       switch (b) {
           case LEFT:
               node.balance = Balance.RIGHT;
               node.left.balance = Balance.EVEN;
               node.left.right.balance = Balance.EVEN;
           case RIGHT:
               node.balance = Balance.EVEN;
               node.left.right.balance = Balance.EVEN;
               node.left.balance = Balance.LEFT;
               
           case EVEN:
               node.balance = Balance.EVEN;
               node.left.balance = Balance.EVEN;
               node.left.right.balance = Balance.EVEN;
       }
       return node;
   }
   
   private Node rotateLeftThenRight(Node node) {
//           c           c
//          /           /
//         a     -->   b  --> balanced
//          \         /
//           b       a
       System.out.println("called rotate left then right");
       if (node.left.right.balance == Balance.LEFT) {
           node.balance = Balance.RIGHT;
       }
       else if (node.left.right.balance == Balance.EVEN) {
           node.balance = Balance.EVEN;
       }
       node.left = rotateLeft(node.left);
       node.balance = Balance.EVEN;
       return rotateRight(node);
//       Node c = node;
//       Node a = c.left;
//       Node b = a.right;
//       
//       //left around the left side
//       c.left = b;
//       b.left = a;
//       
//       //right around the entire thing
//       c.left = b.right;
//       b.right = c;
//       return node;
   }
   
    private Result add(Node node, Key key, Value value) {
        if (node == null) {
            return taller(new Node(key, value));

        } else {
            int compare = key.compareTo(node.key);
            if (compare < 0) {
                Result r = add(node.left, key, value);
                node.left = r.node;
                if (r.grew == true) {
                    return rebalanceLeftSubtree(node);
                }
                else {
                    return sameHeight(r.node);
                }

            } else if (compare > 0) {
                Result r = add(node.right, key, value);
                node.right = r.node;
                if (r.grew == true) {
                    return rebalanceRightSubtree(node);
                }
                else {
                    return sameHeight(r.node);
                }
            } else {
                node.key = key;
                node.value = value;
                return sameHeight(node);
            }
        }
    }

    public void add(Key key, Value value) {
        this.root = add(this.root, key, value).node; //will have a root with the result subclass thing
    }

    public static interface Visit<Key, Value> {

        public void visit(Key key, Value value);
    }

    private void traverse(Node node, Visit<Key, Value> visit) {
        if (node != null) {
            traverse(node.left, visit);
            visit.visit(node.key, node.value);
            traverse(node.right, visit);
        }
    }

    public void traverse(Visit<Key, Value> visit) {
        traverse(root, visit);
    }

    private void print(Key key, Value value, Balance balance, int level) {
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += "  ";
        }
        if (value != null) {
            System.out.println(indent + key + "(" + balance + "): " + value);
        } else {
            System.out.println(indent + key);
        }
    }

    private void print(Node node, int level) {
        if (node != null) {
            print(node.left, level + 1);
            print(node.key, node.value, node.balance, level);
            print(node.right, level + 1);
        }
    }

    public void print() {
        int level = 0;
        print(this.root, level);
    }

    private static String getArgument(String line, int index) {
        String[] words = line.split("\\s");
        return words.length > index ? words[index] : "";
    }

    private static String getCommand(String line) {
        return getArgument(line, 0);
    }

    private static String getLine(BufferedReader input) {
        System.out.print("Command: ");
        try {
            return input.readLine().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        AVL_Tree<String, String> tree = new AVL_Tree<>();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // Allow the user to enter commands on standard input:
        //
        //   size              prints the number of nodes in the tree
        //   height            prints the height of the tree
        //   min               prints the minimm value in the tree
        //   max               prints the maximum value in the tree
        //   floor <key>       prints the largest value in tree no greater than key
        //   ceiling <key>     prints the smallest value in tree no less than key
        //   contains <key>    prints true if a value is in the tree; false if not
        //   add <key>         adds an item to the tree
        //   remove <key>      removes an item from the tree (if present)
        //   clear             removes all items from the tree
        //   print             prints the tree
        //   exit              quit the program
        String line = getLine(input);
        while (line != null) {
            String command = getCommand(line);
            String arg = getArgument(line, 1);

            switch (command) {
                case "size":
                    System.out.println(tree.size());
                    break;

                case "height":
                    System.out.println(tree.height());
                    break;

                case "contains":
                    System.out.println(tree.contains(arg));
                    break;

                case "find":
                    System.out.println(tree.find(arg));
                    break;

                case "min":
                case "minimum":
                    System.out.println(tree.min());
                    break;

                case "max":
                case "maximum":
                    System.out.println(tree.max());
                    break;

                case "floor":
                    System.out.println(tree.floor(arg));
                    break;

                case "ceiling":
                    System.out.println(tree.ceiling(arg));
                    break;

                case "add":
                case "insert":
                    tree.add(arg, getArgument(line, 2));
                    break;

                case "clear":
                    tree = new AVL_Tree();
                    break;

                case "tree":
                case "print":
                    tree.print();
                    break;

                case "end":
                case "exit":
                case "quit":
                    return;

                default:
                    System.out.println("Invalid command: " + command);
                    break;
            }

            line = getLine(input);
        }
    }
}
