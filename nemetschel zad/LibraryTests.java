// Тестове за задачата с библиотеката.
// Стартиране: javac -encoding UTF-8 *.java && java LibraryTests
public class LibraryTests {
    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===== Тестове - Библиотека =====");

        testValidTitle();
        testValidAuthor();
        testValidDate();
        testValidGenre();
        testMenuChoice();
        testCriteria();
        testAddBook();
        testMaxBooks();
        testSortByTitle();
        testSortByAuthor();
        testSortByGenre();
        testSortByTwoCriteria();
        testSpecialAlwaysFirst();
        testSpecialDifferentWriting();
        testSortEmptyAndOne();

        System.out.println();
        System.out.println("Успешни: " + passed + ", неуспешни: " + failed);
        if (failed > 0) {
            System.out.println("ИМА ГРЕШКИ!");
            System.exit(1);
        }
        System.out.println("Всички тестове минаха успешно.");
    }

    static void check(boolean condition, String description) {
        if (condition) {
            passed++;
            System.out.println("[УСПЕХ] " + description);
        } else {
            failed++;
            System.out.println("[ГРЕШКА] " + description);
        }
    }

    static String titles(Library lib) {
        String result = "";
        for (int i = 0; i < lib.getCount(); i++) {
            if (i > 0) {
                result += ", ";
            }
            result += lib.getBook(i).getTitle();
        }
        return result;
    }

    static Library sampleLibrary() {
        Library lib = new Library();
        lib.addBook("Под игото", "Иван Вазов", "01.01.1894", "Роман");
        lib.addBook("Бай Ганьо", "Алеко Константинов", "01.01.1895", "Сатира");
        lib.addBook("Да убиеш присмехулник", "Харпър Ли", "11.07.1960", "Роман");
        lib.addBook("1984", "Джордж Оруел", "08.06.1949", "Антиутопия");
        lib.addBook("Война и мир", "Лев Толстой", "01.01.1869", "Роман");
        return lib;
    }

    static void testValidTitle() {
        check(Validator.checkTitle("Под игото") == null, "Нормално заглавие е валидно");
        check(Validator.checkTitle("1984") == null, "Заглавие само от цифри е валидно");
        check(Validator.checkTitle("") != null, "Празно заглавие е невалидно");
        check(Validator.checkTitle("    ") != null, "Заглавие само от интервали е невалидно");
        check(Validator.checkTitle(null) != null, "Липсващо заглавие (null) е невалидно");
        check(Validator.checkTitle("!!!???") != null, "Заглавие само от знаци е невалидно");
        check(Validator.checkTitle("а".repeat(101)) != null, "Заглавие над 100 символа е невалидно");
    }

    static void testValidAuthor() {
        check(Validator.checkAuthor("Иван Вазов") == null, "Нормален автор е валиден");
        check(Validator.checkAuthor("J. R. R. Tolkien") == null, "Автор с точки е валиден");
        check(Validator.checkAuthor("Жан-Пол Сартр") == null, "Автор с тире е валиден");
        check(Validator.checkAuthor("") != null, "Празен автор е невалиден");
        check(Validator.checkAuthor("И") != null, "Автор с 1 буква е невалиден");
        check(Validator.checkAuthor("Иван123") != null, "Автор с цифри е невалиден");
        check(Validator.checkAuthor("Иван@Вазов") != null, "Автор със специални знаци е невалиден");
        check(Validator.checkAuthor("..--") != null, "Автор без нито една буква е невалиден");
    }

    static void testValidDate() {
        check(Validator.checkDate("11.07.1960") == null, "Нормална дата е валидна");
        check(Validator.checkDate("29.02.2020") == null, "29 февруари във високосна година е валидна");
        check(Validator.checkDate("29.02.2021") != null, "29 февруари в невисокосна година е невалидна");
        check(Validator.checkDate("31.04.2000") != null, "31 април е невалидна дата");
        check(Validator.checkDate("00.01.2000") != null, "Ден 00 е невалиден");
        check(Validator.checkDate("10.13.2000") != null, "Месец 13 е невалиден");
        check(Validator.checkDate("1.1.2000") != null, "Дата без водещи нули е невалидна");
        check(Validator.checkDate("2000-01-01") != null, "Дата в друг формат е невалидна");
        check(Validator.checkDate("аа.бб.вввв") != null, "Дата с букви е невалидна");
        check(Validator.checkDate("01.01.1300") != null, "Година преди 1400 е невалидна");
        check(Validator.checkDate("01.01.3000") != null, "Дата в бъдещето е невалидна");
        check(Validator.checkDate("") != null, "Празна дата е невалидна");
    }

    static void testValidGenre() {
        check(Validator.checkGenre("Роман") == null, "Нормален жанр е валиден");
        check(Validator.checkGenre("Научна фантастика") == null, "Жанр от две думи е валиден");
        check(Validator.checkGenre("") != null, "Празен жанр е невалиден");
        check(Validator.checkGenre("Р") != null, "Жанр от 1 буква е невалиден");
        check(Validator.checkGenre("Роман2") != null, "Жанр с цифри е невалиден");
    }

    static void testMenuChoice() {
        check(Validator.isMenuChoice("3", 0, 4), "Избор 3 от меню 0-4 е валиден");
        check(Validator.isMenuChoice(" 1 ", 0, 4), "Избор с интервали около числото е валиден");
        check(!Validator.isMenuChoice("5", 0, 4), "Избор 5 от меню 0-4 е невалиден");
        check(!Validator.isMenuChoice("-1", 0, 4), "Отрицателен избор е невалиден");
        check(!Validator.isMenuChoice("абв", 0, 4), "Избор с букви е невалиден");
        check(!Validator.isMenuChoice("", 0, 4), "Празен избор е невалиден");
        check(!Validator.isMenuChoice("99999999999", 0, 4), "Много голямо число не чупи програмата");
    }

    static void testCriteria() {
        int[] c = Validator.parseCriteria("2 1");
        check(c != null && c.length == 2 && c[0] == 2 && c[1] == 1, "\"2 1\" се чете като автор, заглавие");
        c = Validator.parseCriteria("2,1");
        check(c != null && c.length == 2 && c[0] == 2 && c[1] == 1, "\"2,1\" (със запетая) се чете правилно");
        c = Validator.parseCriteria("321");
        check(c != null && c.length == 3 && c[0] == 3 && c[2] == 1, "\"321\" (без разделител) се чете правилно");
        check(Validator.parseCriteria("4") == null, "Критерий 4 е невалиден");
        check(Validator.parseCriteria("1 1") == null, "Повторен критерий е невалиден");
        check(Validator.parseCriteria("а") == null, "Критерий с букви е невалиден");
        check(Validator.parseCriteria("") == null, "Празен критерий е невалиден");
    }

    static void testAddBook() {
        Library lib = new Library();
        check(lib.addBook("Под игото", "Иван Вазов", "01.01.1894", "Роман") == null, "Добавяне на валидна книга");
        check(lib.getCount() == 1, "След добавяне има 1 книга");
        check(lib.addBook("под  игото", "ИВАН ВАЗОВ", "01.01.1894", "Роман") != null,
                "Същата книга (с други главни букви и интервали) не се добавя втори път");
        check(lib.addBook("Под игото", "Друг Автор", "01.01.1894", "Роман") == null,
                "Същото заглавие от друг автор се добавя");
        check(lib.addBook("", "Иван Вазов", "01.01.1894", "Роман") != null, "Книга без заглавие не се добавя");
        check(lib.addBook("Нещо", "Иван Вазов", "32.01.1894", "Роман") != null, "Книга с грешна дата не се добавя");
        check(lib.getCount() == 2, "Невалидните книги не са добавени");
    }

    static void testMaxBooks() {
        Library lib = new Library();
        for (int i = 1; i <= 100; i++) {
            lib.addBook("Книга " + i, "Автор", "01.01.2000", "Роман");
        }
        check(lib.getCount() == 100, "Могат да се добавят точно 100 книги");
        check(lib.isFull(), "Библиотеката е пълна при 100 книги");
        check(lib.addBook("Книга 101", "Автор", "01.01.2000", "Роман") != null, "101-вата книга не се добавя");
        check(lib.getCount() == 100, "Броят остава 100");
    }

    static void testSortByTitle() {
        Library lib = sampleLibrary();
        lib.sort(new int[]{Library.BY_TITLE});
        check(titles(lib).equals("Да убиеш присмехулник, 1984, Бай Ганьо, Война и мир, Под игото"),
                "Сортиране по заглавие: " + titles(lib));
    }

    static void testSortByAuthor() {
        Library lib = sampleLibrary();
        lib.sort(new int[]{Library.BY_AUTHOR});
        // Алеко, Джордж, Иван, Лев (Харпър Ли е първа, защото е специалната книга)
        check(titles(lib).equals("Да убиеш присмехулник, Бай Ганьо, 1984, Под игото, Война и мир"),
                "Сортиране по автор: " + titles(lib));
    }

    static void testSortByGenre() {
        Library lib = sampleLibrary();
        lib.sort(new int[]{Library.BY_GENRE});
        check(lib.getBook(0).getTitle().equals("Да убиеш присмехулник"), "По жанр - специалната книга е първа");
        check(lib.getBook(1).getGenre().equals("Антиутопия"), "По жанр - след нея е Антиутопия");
        check(lib.getBook(4).getGenre().equals("Сатира"), "По жанр - последна е Сатира");
    }

    static void testSortByTwoCriteria() {
        Library lib = sampleLibrary();
        lib.sort(new int[]{Library.BY_GENRE, Library.BY_TITLE});
        check(titles(lib).equals("Да убиеш присмехулник, 1984, Война и мир, Под игото, Бай Ганьо"),
                "Сортиране по жанр, после по заглавие: " + titles(lib));

        Library lib2 = new Library();
        lib2.addBook("Б книга", "Автор А", "01.01.2000", "Роман");
        lib2.addBook("А книга", "Автор Б", "01.01.2000", "Роман");
        lib2.addBook("В книга", "Автор А", "01.01.2000", "Роман");
        lib2.sort(new int[]{Library.BY_AUTHOR, Library.BY_TITLE});
        check(titles(lib2).equals("Б книга, В книга, А книга"), "Сортиране по автор, после по заглавие: " + titles(lib2));
    }

    static void testSpecialAlwaysFirst() {
        int[][] allCriteria = {{1}, {2}, {3}, {1, 2}, {2, 3}, {3, 2, 1}};
        for (int i = 0; i < allCriteria.length; i++) {
            Library lib = new Library();
            lib.addBook("Аааа", "Аааа", "01.01.2000", "Аааа");
            lib.addBook("Да убиеш присмехулник", "Яяяя", "11.07.1960", "Яяяя");
            lib.addBook("Ббб", "Ббб", "01.01.2000", "Ббб");
            lib.sort(allCriteria[i]);
            check(lib.getBook(0).getTitle().equals("Да убиеш присмехулник"),
                    "\"Да убиеш присмехулник\" е първа при критерий вариант " + (i + 1));
        }
    }

    static void testSpecialDifferentWriting() {
        Library lib = new Library();
        lib.addBook("Аааа", "Аааа", "01.01.2000", "Роман");
        lib.addBook("  да  УБИЕШ присмехулник ", "Харпър Ли", "11.07.1960", "Роман");
        lib.sort(new int[]{Library.BY_TITLE});
        check(Library.isSpecial(lib.getBook(0)), "Специалната книга се разпознава и с малки/главни букви и интервали");

        Library lib2 = new Library();
        lib2.addBook("Аааа", "Аааа", "01.01.2000", "Роман");
        lib2.addBook("„Да убиеш присмехулник“", "Харпър Ли", "11.07.1960", "Роман");
        lib2.sort(new int[]{Library.BY_TITLE});
        check(Library.isSpecial(lib2.getBook(0)), "Специалната книга се разпознава и в кавички");

        Library lib3 = new Library();
        lib3.addBook("Аааа", "Аааа", "01.01.2000", "Роман");
        lib3.addBook("Да убиеш присмехулник 2", "Някой", "01.01.2000", "Роман");
        lib3.sort(new int[]{Library.BY_TITLE});
        check(!Library.isSpecial(lib3.getBook(1)) && lib3.getBook(0).getTitle().equals("Аааа"),
                "Подобно, но различно заглавие НЕ се слага първо");
    }

    static void testSortEmptyAndOne() {
        Library lib = new Library();
        lib.sort(new int[]{1});
        check(lib.getCount() == 0, "Сортиране на празна библиотека не чупи програмата");
        lib.addBook("Само една", "Автор", "01.01.2000", "Роман");
        lib.sort(new int[]{2});
        check(lib.getCount() == 1 && lib.getBook(0).getTitle().equals("Само една"), "Сортиране на една книга");
    }
}
