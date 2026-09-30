public class Library {
    public static final int MAX_BOOKS = 100;
    public static final String SPECIAL_TITLE = "Да убиеш присмехулник";

    public static final int BY_TITLE = 1;
    public static final int BY_AUTHOR = 2;
    public static final int BY_GENRE = 3;

    private Book[] books = new Book[MAX_BOOKS];
    private int count = 0;

    public int getCount() {
        return count;
    }

    public Book getBook(int index) {
        if (index < 0 || index >= count) {
            return null;
        }
        return books[index];
    }

    public boolean isFull() {
        return count >= MAX_BOOKS;
    }

    // Добавя книга. Връща null при успех или текст с грешката.
    public String addBook(String title, String author, String date, String genre) {
        if (isFull()) {
            return "Библиотеката е пълна (максимум " + MAX_BOOKS + " книги).";
        }
        String error = Validator.checkTitle(title);
        if (error == null) {
            error = Validator.checkAuthor(author);
        }
        if (error == null) {
            error = Validator.checkDate(date);
        }
        if (error == null) {
            error = Validator.checkGenre(genre);
        }
        if (error != null) {
            return error;
        }
        if (contains(title, author)) {
            return "Тази книга от този автор вече е въведена.";
        }
        books[count] = new Book(clean(title), clean(author), date.trim(), clean(genre));
        count++;
        return null;
    }

    public boolean contains(String title, String author) {
        for (int i = 0; i < count; i++) {
            if (normalize(books[i].getTitle()).equals(normalize(title))
                    && normalize(books[i].getAuthor()).equals(normalize(author))) {
                return true;
            }
        }
        return false;
    }

    // махаме излишните интервали
    private static String clean(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    // за сравнение - малки букви, без кавички и излишни интервали
    public static String normalize(String s) {
        if (s == null) {
            return "";
        }
        s = s.replaceAll("[\"'„“”«»]", "");
        return clean(s).toLowerCase();
    }

    public static boolean isSpecial(Book b) {
        return normalize(b.getTitle()).equals(normalize(SPECIAL_TITLE));
    }

    // сравнява две книги по избраните критерии
    public static int compare(Book a, Book b, int[] criteria) {
        // "Да убиеш присмехулник" винаги е първа
        boolean aSpecial = isSpecial(a);
        boolean bSpecial = isSpecial(b);
        if (aSpecial && !bSpecial) {
            return -1;
        }
        if (bSpecial && !aSpecial) {
            return 1;
        }

        for (int i = 0; i < criteria.length; i++) {
            int result = 0;
            if (criteria[i] == BY_TITLE) {
                result = normalize(a.getTitle()).compareTo(normalize(b.getTitle()));
            } else if (criteria[i] == BY_AUTHOR) {
                result = normalize(a.getAuthor()).compareTo(normalize(b.getAuthor()));
            } else if (criteria[i] == BY_GENRE) {
                result = normalize(a.getGenre()).compareTo(normalize(b.getGenre()));
            }
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    // bubble sort
    public void sort(int[] criteria) {
        if (criteria == null || criteria.length == 0) {
            return;
        }
        for (int i = 0; i < count - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < count - 1 - i; j++) {
                if (compare(books[j], books[j + 1], criteria) > 0) {
                    Book temp = books[j];
                    books[j] = books[j + 1];
                    books[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public void print() {
        if (count == 0) {
            System.out.println("Няма въведени книги.");
            return;
        }
        System.out.println("№ | Заглавие | Автор | Дата | Жанр");
        System.out.println("-----------------------------------");
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + books[i]);
        }
    }

    public void loadSamples() {
        addBook("Война и мир", "Лев Толстой", "01.01.1869", "Роман");
        addBook("Под игото", "Иван Вазов", "01.01.1894", "Роман");
        addBook("Да убиеш присмехулник", "Харпър Ли", "11.07.1960", "Роман");
        addBook("Бай Ганьо", "Алеко Константинов", "01.01.1895", "Сатира");
        addBook("Хари Потър и философският камък", "Джоан Роулинг", "26.06.1997", "Фентъзи");
        addBook("Железният светилник", "Димитър Талев", "01.01.1952", "Роман");
        addBook("1984", "Джордж Оруел", "08.06.1949", "Антиутопия");
    }
}
