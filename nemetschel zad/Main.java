import java.util.Scanner;

public class Main {
    static final int MAX_BOOKS = 100;
    static final String SPECIAL_TITLE = "Да убиеш присмехулник";

    static Book[] books = new Book[MAX_BOOKS];
    static int count = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        int choice;

        do {
            System.out.println();
            System.out.println("===== Библиотека =====");
            System.out.println("1. Добави книга");
            System.out.println("2. Покажи всички книги");
            System.out.println("3. Сортирай книгите");
            System.out.println("0. Изход");
            System.out.print("Избор: ");
            choice = readInt(sc);

            switch (choice) {
                case 1:
                    addBook(sc);
                    break;
                case 2:
                    printBooks();
                    break;
                case 3:
                    sortMenu(sc);
                    break;
                case 0:
                    System.out.println("Довиждане!");
                    break;
                default:
                    System.out.println("Невалиден избор!");
            }
        } while (choice != 0);

        sc.close();
    }

    static int readInt(Scanner sc) {
        String line = sc.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static void addBook(Scanner sc) {
        if (count >= MAX_BOOKS) {
            System.out.println("Не може да има повече от " + MAX_BOOKS + " книги!");
            return;
        }
        System.out.print("Заглавие: ");
        String title = sc.nextLine().trim();
        System.out.print("Автор: ");
        String author = sc.nextLine().trim();
        System.out.print("Дата на издаване (дд.мм.гггг): ");
        String date = sc.nextLine().trim();
        System.out.print("Жанр: ");
        String genre = sc.nextLine().trim();

        if (title.isEmpty() || author.isEmpty()) {
            System.out.println("Заглавието и авторът не може да са празни!");
            return;
        }

        books[count] = new Book(title, author, date, genre);
        count++;
        System.out.println("Книгата е добавена.");
    }

    static void printBooks() {
        if (count == 0) {
            System.out.println("Няма въведени книги.");
            return;
        }
        System.out.println("Заглавие | Автор | Дата | Жанр");
        System.out.println("------------------------------");
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + books[i]);
        }
    }

    static void sortMenu(Scanner sc) {
        System.out.println("По какво да се сортира?");
        System.out.println("Въведи едно или повече от: 1 - заглавие, 2 - автор, 3 - жанр");
        System.out.println("(например 2 1 - първо по автор, после по заглавие)");
        System.out.print("Избор: ");
        String[] parts = sc.nextLine().trim().split("\\s+");

        int[] criteria = new int[3];
        int n = 0;
        for (int i = 0; i < parts.length && n < 3; i++) {
            if (parts[i].equals("1") || parts[i].equals("2") || parts[i].equals("3")) {
                criteria[n] = Integer.parseInt(parts[i]);
                n++;
            }
        }

        if (n == 0) {
            System.out.println("Не е избран критерий!");
            return;
        }

        sortBooks(criteria, n);
        printBooks();
    }

    // сравнява две книги по избраните критерии
    static int compare(Book a, Book b, int[] criteria, int n) {
        // "Да убиеш присмехулник" винаги е първа
        boolean aSpecial = a.getTitle().equalsIgnoreCase(SPECIAL_TITLE);
        boolean bSpecial = b.getTitle().equalsIgnoreCase(SPECIAL_TITLE);
        if (aSpecial && !bSpecial) {
            return -1;
        }
        if (bSpecial && !aSpecial) {
            return 1;
        }

        for (int i = 0; i < n; i++) {
            int result = 0;
            if (criteria[i] == 1) {
                result = a.getTitle().compareToIgnoreCase(b.getTitle());
            } else if (criteria[i] == 2) {
                result = a.getAuthor().compareToIgnoreCase(b.getAuthor());
            } else if (criteria[i] == 3) {
                result = a.getGenre().compareToIgnoreCase(b.getGenre());
            }
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    // bubble sort
    static void sortBooks(int[] criteria, int n) {
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (compare(books[j], books[j + 1], criteria, n) > 0) {
                    Book temp = books[j];
                    books[j] = books[j + 1];
                    books[j + 1] = temp;
                }
            }
        }
    }
}
