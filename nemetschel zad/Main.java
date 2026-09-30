import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {
    static Library library = new Library();
    static Scanner sc;

    public static void main(String[] args) {
        sc = createScanner();
        int choice;

        do {
            System.out.println();
            System.out.println("===== Библиотека (" + library.getCount() + "/" + Library.MAX_BOOKS + " книги) =====");
            System.out.println("1. Добави книга");
            System.out.println("2. Покажи всички книги");
            System.out.println("3. Сортирай книгите");
            System.out.println("4. Зареди примерни книги");
            System.out.println("0. Изход");
            choice = readChoice("Избор: ", 0, 4);

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    library.print();
                    break;
                case 3:
                    sortBooks();
                    break;
                case 4:
                    int before = library.getCount();
                    library.loadSamples();
                    System.out.println("Добавени са " + (library.getCount() - before) + " примерни книги.");
                    break;
                case 0:
                    System.out.println("Довиждане!");
                    break;
            }
        } while (choice != 0);
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

    // пита докато не се въведе число в интервала
    static int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = readLine();
            if (Validator.isMenuChoice(line, min, max)) {
                return Integer.parseInt(line.trim());
            }
            System.out.println("Невалиден избор! Въведи число от " + min + " до " + max + ".");
        }
    }

    static void addBook() {
        if (library.isFull()) {
            System.out.println("Библиотеката е пълна (максимум " + Library.MAX_BOOKS + " книги).");
            return;
        }

        String title;
        String author;
        while (true) {
            title = readValid("Заглавие: ", 1);
            author = readValid("Автор: ", 2);
            if (!library.contains(title, author)) {
                break;
            }
            System.out.println("Тази книга от този автор вече е въведена! Въведи друга.");
        }
        String date = readValid("Дата на издаване (дд.мм.гггг): ", 3);
        String genre = readValid("Жанр: ", 4);

        String error = library.addBook(title, author, date, genre);
        if (error == null) {
            System.out.println("Книгата е добавена.");
        } else {
            System.out.println("Грешка: " + error);
        }
    }

    // пита докато не се въведе валидна стойност
    // field: 1 - заглавие, 2 - автор, 3 - дата, 4 - жанр
    static String readValid(String prompt, int field) {
        while (true) {
            System.out.print(prompt);
            String value = readLine();
            String error;
            if (field == 1) {
                error = Validator.checkTitle(value);
            } else if (field == 2) {
                error = Validator.checkAuthor(value);
            } else if (field == 3) {
                error = Validator.checkDate(value);
            } else {
                error = Validator.checkGenre(value);
            }
            if (error == null) {
                return value.trim();
            }
            System.out.println("Грешка: " + error);
        }
    }

    static void sortBooks() {
        if (library.getCount() == 0) {
            System.out.println("Няма книги за сортиране.");
            return;
        }
        System.out.println("По какво да се сортира?");
        System.out.println("1 - заглавие, 2 - автор, 3 - жанр");
        System.out.println("Може и няколко наведнъж, например: 2 1 или 2,1 (първо по автор, после по заглавие)");
        System.out.println("0 - назад");

        int[] criteria;
        while (true) {
            System.out.print("Избор: ");
            String line = readLine().trim();
            if (line.equals("0")) {
                return;
            }
            criteria = Validator.parseCriteria(line);
            if (criteria != null) {
                break;
            }
            System.out.println("Невалиден избор! Използвай само цифрите 1, 2 и 3 без повторения.");
        }

        library.sort(criteria);
        System.out.println("Книгите са сортирани:");
        library.print();
    }
}
