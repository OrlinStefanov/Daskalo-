import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner sc;

    public static void main(String[] args) {
        sc = createScanner();

        PhoneBook book = new PhoneBook();
        System.out.print("Име на файл (Enter за phones.txt): ");
        String fileName = readLine().trim();
        if (fileName.isEmpty()) {
            fileName = "phones.txt";
        }
        boolean ok = book.loadFromFile(fileName);
        if (ok) {
            System.out.println("Заредени записи: " + book.getLoadedCount());
        }
        ArrayList<String> errors = book.getLoadErrors();
        if (!errors.isEmpty()) {
            System.out.println(ok ? "Пропуснати редове:" : "Файлът не е зареден:");
            for (int i = 0; i < errors.size(); i++) {
                System.out.println("  " + errors.get(i));
            }
            if (!ok) {
                System.out.println("Започваме с празен указател.");
            }
        }

        int choice;
        do {
            System.out.println();
            System.out.println("===== Телефонен указател (" + book.size() + " записа) =====");
            System.out.println("1. Добави");
            System.out.println("2. Изтрий по име");
            System.out.println("3. Търси номер по име");
            System.out.println("4. Покажи всички");
            System.out.println("5. Промени номер");
            System.out.println("0. Изход");
            choice = readChoice("Избор: ", 0, 5);

            if (choice == 1) {
                String name = readName();
                if (book.getNumber(name) != null) {
                    System.out.println("Това име вече е записано. Използвай \"Промени номер\".");
                    continue;
                }
                String number = readNumber();
                String error = book.add(name, number);
                if (error == null) {
                    System.out.println("Добавено: " + name.trim() + " - " + PhoneBook.normalize(number));
                } else {
                    System.out.println("Грешка: " + error);
                }
            } else if (choice == 2) {
                String name = readName();
                if (book.remove(name)) {
                    System.out.println("Изтрито.");
                } else {
                    System.out.println("Няма такова име.");
                }
            } else if (choice == 3) {
                String name = readName();
                String number = book.getNumber(name);
                if (number != null) {
                    System.out.println(name.trim() + " - " + number);
                } else {
                    System.out.println("Няма такова име.");
                }
            } else if (choice == 4) {
                book.print();
            } else if (choice == 5) {
                String name = readName();
                if (book.getNumber(name) == null) {
                    System.out.println("Няма такова име.");
                    continue;
                }
                String number = readNumber();
                String error = book.changeNumber(name, number);
                if (error == null) {
                    System.out.println("Номерът е сменен.");
                } else {
                    System.out.println("Грешка: " + error);
                }
            }
        } while (choice != 0);

        System.out.println("Довиждане!");
    }

    // Чете в кодировката на конзолата, иначе на Windows кирилицата се чупи
    static Scanner createScanner() {
        Console console = System.console();
        if (console != null) {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, console.charset()));
            return new Scanner(System.in, console.charset());
        }
        return new Scanner(System.in);
    }

    static String readLine() {
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Край на входа. Довиждане!");
            System.exit(0);
        }
        return sc.nextLine();
    }

    static int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = readLine().trim();
            boolean digits = !line.isEmpty() && line.length() <= 3;
            for (int i = 0; i < line.length() && digits; i++) {
                if (line.charAt(i) < '0' || line.charAt(i) > '9') {
                    digits = false;
                }
            }
            if (digits) {
                int n = Integer.parseInt(line);
                if (n >= min && n <= max) {
                    return n;
                }
            }
            System.out.println("Невалиден избор! Въведи число от " + min + " до " + max + ".");
        }
    }

    // пита докато не се въведе валидно име
    static String readName() {
        while (true) {
            System.out.print("Име: ");
            String name = readLine();
            String error = PhoneBook.checkName(name);
            if (error == null) {
                return name.trim();
            }
            System.out.println("Грешка: " + error);
        }
    }

    // пита докато не се въведе валиден номер
    static String readNumber() {
        while (true) {
            System.out.print("Номер (+359..., 00359... или 08...): ");
            String number = readLine().trim();
            if (PhoneBook.isValid(number)) {
                return number;
            }
            System.out.println("Грешка: невалиден български мобилен номер. Пример: 0878123456");
        }
    }
}
