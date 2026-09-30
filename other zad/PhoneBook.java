import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PhoneBook {
    private Node root;

    public PhoneBook() {
        root = null;
    }

    // чете указател от файл, на всеки ред: име, номер
    public PhoneBook(String fileName) {
        root = null;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                int comma = line.lastIndexOf(',');
                if (comma == -1) {
                    continue;
                }
                String name = line.substring(0, comma).trim();
                String number = line.substring(comma + 1).trim();
                if (!name.isEmpty() && isValid(number)) {
                    add(name, number);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Грешка при четене на файла: " + e.getMessage());
        }
    }

    // проверява дали номерът е валиден
    public static boolean isValid(String number) {
        return normalize(number) != null;
    }

    // връща номера във вида +359XXXXXXXXX или null ако е невалиден
    public static String normalize(String number) {
        if (number == null) {
            return null;
        }
        String rest;
        if (number.startsWith("+359")) {
            rest = number.substring(4);
        } else if (number.startsWith("00359")) {
            rest = number.substring(5);
        } else if (number.startsWith("0")) {
            rest = number.substring(1);
        } else {
            return null;
        }

        if (rest.length() != 9) {
            return null;
        }
        for (int i = 0; i < rest.length(); i++) {
            if (!Character.isDigit(rest.charAt(i))) {
                return null;
            }
        }

        String operator = rest.substring(0, 2);
        if (!operator.equals("87") && !operator.equals("88") && !operator.equals("89")) {
            return null;
        }
        if (rest.charAt(2) < '2') {
            return null;
        }

        return "+359" + rest;
    }

    // добавя двойка, ако името го има - сменя номера
    public boolean add(String name, String number) {
        String normalized = normalize(number);
        if (normalized == null) {
            return false;
        }
        root = insert(root, name, normalized);
        return true;
    }

    private Node insert(Node node, String name, String number) {
        if (node == null) {
            return new Node(name, number);
        }
        int cmp = name.compareTo(node.name);
        if (cmp < 0) {
            node.left = insert(node.left, name, number);
        } else if (cmp > 0) {
            node.right = insert(node.right, name, number);
        } else {
            node.number = number;
        }
        return node;
    }

    public String getNumber(String name) {
        Node current = root;
        while (current != null) {
            int cmp = name.compareTo(current.name);
            if (cmp == 0) {
                return current.number;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    public boolean remove(String name) {
        if (getNumber(name) == null) {
            return false;
        }
        root = delete(root, name);
        return true;
    }

    private Node delete(Node node, String name) {
        if (node == null) {
            return null;
        }
        int cmp = name.compareTo(node.name);
        if (cmp < 0) {
            node.left = delete(node.left, name);
        } else if (cmp > 0) {
            node.right = delete(node.right, name);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            // два наследника - взимаме най-малкия от дясното поддърво
            Node min = node.right;
            while (min.left != null) {
                min = min.left;
            }
            node.name = min.name;
            node.number = min.number;
            node.right = delete(node.right, min.name);
        }
        return node;
    }

    // отпечатва всички двойки подредени по име (inorder обхождане)
    public void print() {
        if (root == null) {
            System.out.println("Указателят е празен.");
            return;
        }
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.name + " - " + node.number);
        printInOrder(node.right);
    }
}
