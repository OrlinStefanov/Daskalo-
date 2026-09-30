import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

// Телефонен указател, пазен в двоично наредено дърво (по име).
public class PhoneBook {
    public static final int MAX_NAME = 50;

    private Node root;
    private int size;

    // информация от последното зареждане от файл
    private int loadedCount;
    private ArrayList<String> loadErrors = new ArrayList<String>();

    public PhoneBook() {
        root = null;
        size = 0;
    }

    // чете указател от файл, на всеки ред: име, номер
    public PhoneBook(String fileName) {
        this();
        loadFromFile(fileName);
    }

    public int size() {
        return size;
    }

    public int getLoadedCount() {
        return loadedCount;
    }

    public ArrayList<String> getLoadErrors() {
        return loadErrors;
    }

    // Зарежда двойки от файл. Невалидните редове се пропускат.
    // Връща false ако файлът не може да се прочете.
    public boolean loadFromFile(String fileName) {
        loadedCount = 0;
        loadErrors.clear();

        if (fileName == null || fileName.trim().isEmpty()) {
            loadErrors.add("Не е зададено име на файл.");
            return false;
        }
        File file = new File(fileName.trim());
        if (!file.exists()) {
            loadErrors.add("Файлът \"" + fileName + "\" не съществува.");
            return false;
        }
        if (!file.isFile()) {
            loadErrors.add("\"" + fileName + "\" не е файл.");
            return false;
        }

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // махаме BOM ако файлът е записан с Notepad
                if (lineNumber == 1 && line.startsWith("﻿")) {
                    line = line.substring(1);
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    loadErrors.add("Ред " + lineNumber + ": трябва да е във вида \"име, номер\".");
                    continue;
                }
                String name = parts[0];
                String number = parts[1].trim();
                String error = checkName(name);
                if (error != null) {
                    loadErrors.add("Ред " + lineNumber + ": " + error);
                    continue;
                }
                if (normalize(number) == null) {
                    loadErrors.add("Ред " + lineNumber + ": невалиден номер \"" + number + "\".");
                    continue;
                }
                if (getNumber(name) != null) {
                    loadErrors.add("Ред " + lineNumber + ": името \"" + cleanName(name) + "\" вече е записано.");
                    continue;
                }
                add(name, number);
                loadedCount++;
            }
            reader.close();
        } catch (IOException e) {
            loadErrors.add("Грешка при четене на файла: " + e.getMessage());
            return false;
        }
        return true;
    }

    // Проверка на име. Връща null ако е валидно, иначе текст с грешката.
    public static String checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Името не може да е празно.";
        }
        name = name.trim();
        if (name.length() < 2) {
            return "Името е твърде кратко (поне 2 символа).";
        }
        if (name.length() > MAX_NAME) {
            return "Името е по-дълго от " + MAX_NAME + " символа.";
        }
        boolean hasLetter = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (c != ' ' && c != '-' && c != '.') {
                return "Името може да съдържа само букви, интервали, тирета и точки.";
            }
        }
        if (!hasLetter) {
            return "Името трябва да съдържа поне една буква.";
        }
        return null;
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
        number = number.trim();
        String rest;
        if (number.startsWith("+359")) {
            rest = number.substring(4);
        } else if (number.startsWith("00359")) {
            rest = number.substring(5);
        } else if (number.startsWith("0") && !number.startsWith("00")) {
            rest = number.substring(1);
        } else {
            return null;
        }

        // трябва да останат точно 9 цифри: 2 за оператора, 1 от 2 до 9 и още 6
        if (rest.length() != 9) {
            return null;
        }
        for (int i = 0; i < rest.length(); i++) {
            if (rest.charAt(i) < '0' || rest.charAt(i) > '9') {
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

    // махаме излишните интервали от името
    private static String cleanName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    // Добавя двойка. Връща null при успех или текст с грешката.
    public String add(String name, String number) {
        String error = checkName(name);
        if (error != null) {
            return error;
        }
        String normalized = normalize(number);
        if (normalized == null) {
            return "Невалиден български мобилен номер.";
        }
        name = cleanName(name);
        if (getNumber(name) != null) {
            return "Името \"" + name + "\" вече е записано.";
        }
        root = insert(root, name, normalized);
        size++;
        return null;
    }

    // Сменя номера на съществуващо име. Връща null при успех или текст с грешката.
    public String changeNumber(String name, String number) {
        if (name == null || getNumber(name) == null) {
            return "Няма такова име.";
        }
        String normalized = normalize(number);
        if (normalized == null) {
            return "Невалиден български мобилен номер.";
        }
        Node node = find(cleanName(name));
        node.number = normalized;
        return null;
    }

    // сравнява имена без значение от главни/малки букви
    private static int compareNames(String a, String b) {
        return a.compareToIgnoreCase(b);
    }

    private Node insert(Node node, String name, String number) {
        if (node == null) {
            return new Node(name, number);
        }
        int cmp = compareNames(name, node.name);
        if (cmp < 0) {
            node.left = insert(node.left, name, number);
        } else if (cmp > 0) {
            node.right = insert(node.right, name, number);
        }
        return node;
    }

    private Node find(String name) {
        Node current = root;
        while (current != null) {
            int cmp = compareNames(name, current.name);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    // връща номера за даденото име или null ако го няма
    public String getNumber(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Node node = find(cleanName(name));
        if (node == null) {
            return null;
        }
        return node.number;
    }

    // изтрива по име, връща true ако е имало такова
    public boolean remove(String name) {
        if (getNumber(name) == null) {
            return false;
        }
        root = delete(root, cleanName(name));
        size--;
        return true;
    }

    private Node delete(Node node, String name) {
        if (node == null) {
            return null;
        }
        int cmp = compareNames(name, node.name);
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

    // всички двойки подредени по име (inorder обхождане)
    public ArrayList<String> getAll() {
        ArrayList<String> list = new ArrayList<String>();
        inOrder(root, list);
        return list;
    }

    private void inOrder(Node node, ArrayList<String> list) {
        if (node == null) {
            return;
        }
        inOrder(node.left, list);
        list.add(node.name + " - " + node.number);
        inOrder(node.right, list);
    }

    public void print() {
        if (root == null) {
            System.out.println("Указателят е празен.");
            return;
        }
        ArrayList<String> list = getAll();
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i));
        }
    }
}
