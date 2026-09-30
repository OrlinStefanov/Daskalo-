import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

// Тестове за задачата с телефонния указател.
// Стартиране: javac -encoding UTF-8 *.java && java PhoneBookTests
public class PhoneBookTests {
    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("===== Тестове - Телефонен указател =====");

        testNormalizeValid();
        testNormalizeInvalid();
        testCheckName();
        testAdd();
        testGetNumber();
        testRemove();
        testChangeNumber();
        testSortedOrder();
        testLoadFromFile();
        testMissingFile();

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

    static void testNormalizeValid() {
        check("+359878123456".equals(PhoneBook.normalize("+359878123456")), "+359878123456 остава същия");
        check("+359878123456".equals(PhoneBook.normalize("0878123456")), "0878123456 става +359878123456");
        check("+359878123456".equals(PhoneBook.normalize("00359878123456")), "00359878123456 става +359878123456");
        check("+359889999999".equals(PhoneBook.normalize("0889999999")), "Оператор 88 е валиден");
        check("+359892000000".equals(PhoneBook.normalize("0892000000")), "Оператор 89 с цифра 2 е валиден");
        check("+359878123456".equals(PhoneBook.normalize("  0878123456  ")), "Интервали около номера се пренебрегват");
    }

    static void testNormalizeInvalid() {
        check(PhoneBook.normalize("0868123456") == null, "Оператор 86 е невалиден");
        check(PhoneBook.normalize("0908123456") == null, "Оператор 90 е невалиден");
        check(PhoneBook.normalize("0871123456") == null, "Цифра 1 след оператора е невалидна");
        check(PhoneBook.normalize("0870123456") == null, "Цифра 0 след оператора е невалидна");
        check(PhoneBook.normalize("087812345") == null, "Твърде къс номер е невалиден");
        check(PhoneBook.normalize("08781234567") == null, "Твърде дълъг номер е невалиден");
        check(PhoneBook.normalize("087812345а") == null, "Номер с буква е невалиден");
        check(PhoneBook.normalize("0878 123456") == null, "Номер с интервал по средата е невалиден");
        check(PhoneBook.normalize("0878-123-456") == null, "Номер с тирета е невалиден");
        check(PhoneBook.normalize("+3590878123456") == null, "+359 и 0 заедно е невалидно");
        check(PhoneBook.normalize("359878123456") == null, "Без + отпред е невалидно");
        check(PhoneBook.normalize("+359878123456 ") != null, "Интервал накрая се пренебрегва");
        check(PhoneBook.normalize("+449878123456") == null, "Чужд код на държава е невалиден");
        check(PhoneBook.normalize("00878123456") == null, "00 без 359 е невалидно");
        check(PhoneBook.normalize("") == null, "Празен номер е невалиден");
        check(PhoneBook.normalize(null) == null, "Липсващ номер (null) е невалиден");
    }

    static void testCheckName() {
        check(PhoneBook.checkName("Иван Петров") == null, "Нормално име е валидно");
        check(PhoneBook.checkName("Анна-Мария") == null, "Име с тире е валидно");
        check(PhoneBook.checkName("") != null, "Празно име е невалидно");
        check(PhoneBook.checkName("   ") != null, "Име само от интервали е невалидно");
        check(PhoneBook.checkName("И") != null, "Име от 1 буква е невалидно");
        check(PhoneBook.checkName("Иван1") != null, "Име с цифри е невалидно");
        check(PhoneBook.checkName("Иван, Петров") != null, "Име със запетая е невалидно");
        check(PhoneBook.checkName("а".repeat(51)) != null, "Име над 50 символа е невалидно");
    }

    static void testAdd() {
        PhoneBook pb = new PhoneBook();
        check(pb.add("Иван", "0878123456") == null, "Добавяне на валидна двойка");
        check(pb.size() == 1, "След добавяне размерът е 1");
        check(pb.add("Мария", "0868123456") != null, "Добавяне с невалиден номер не става");
        check(pb.add("", "0878123456") != null, "Добавяне с празно име не става");
        check(pb.add("Иван", "0888123456") != null, "Добавяне на вече съществуващо име не става");
        check(pb.add("иван", "0888123456") != null, "Името не зависи от главни/малки букви");
        check(pb.size() == 1, "Невалидните добавяния не променят размера");
    }

    static void testGetNumber() {
        PhoneBook pb = new PhoneBook();
        pb.add("Иван", "0878123456");
        pb.add("Мария", "00359888654321");
        check("+359878123456".equals(pb.getNumber("Иван")), "Номерът на Иван е в нормализиран вид");
        check("+359888654321".equals(pb.getNumber("Мария")), "Номерът на Мария е в нормализиран вид");
        check("+359878123456".equals(pb.getNumber("  иван ")), "Търсене с малки букви и интервали");
        check(pb.getNumber("Петър") == null, "Търсене на несъществуващо име връща null");
        check(pb.getNumber("") == null, "Търсене с празно име връща null");
        check(pb.getNumber(null) == null, "Търсене с null не чупи програмата");
    }

    static PhoneBook bigBook() {
        PhoneBook pb = new PhoneBook();
        pb.add("Мария", "0878000001");
        pb.add("Георги", "0878000002");
        pb.add("Стоян", "0878000003");
        pb.add("Ани", "0878000004");
        pb.add("Иван", "0878000005");
        pb.add("Петър", "0878000006");
        pb.add("Яна", "0878000007");
        return pb;
    }

    static void testRemove() {
        PhoneBook pb = bigBook();
        check(pb.remove("Ани"), "Изтриване на листо (без наследници)");
        check(pb.getNumber("Ани") == null, "Ани вече я няма");
        check(pb.remove("Стоян"), "Изтриване на възел с два наследника");
        check(pb.getNumber("Петър") != null && pb.getNumber("Яна") != null, "Наследниците на Стоян са запазени");
        check(pb.remove("Мария"), "Изтриване на корена");
        check(pb.size() == 4, "Размерът е 4 след 3 изтривания");
        check(pb.getAll().get(0).startsWith("Георги") && pb.getAll().get(3).startsWith("Яна"),
                "Редът по име е запазен след изтриванията");
        check(!pb.remove("Непознат"), "Изтриване на несъществуващо име връща false");
        check(!pb.remove(""), "Изтриване с празно име връща false");
        check(pb.size() == 4, "Неуспешното изтриване не променя размера");

        PhoneBook one = new PhoneBook();
        one.add("Иван", "0878123456");
        check(one.remove("Иван") && one.size() == 0 && one.getAll().isEmpty(), "Изтриване на единствения запис");
    }

    static void testChangeNumber() {
        PhoneBook pb = new PhoneBook();
        pb.add("Иван", "0878123456");
        check(pb.changeNumber("Иван", "0899999999") == null, "Смяна на номер");
        check("+359899999999".equals(pb.getNumber("Иван")), "Новият номер е записан нормализиран");
        check(pb.changeNumber("Иван", "123") != null, "Смяна с невалиден номер не става");
        check("+359899999999".equals(pb.getNumber("Иван")), "Старият номер остава при грешка");
        check(pb.changeNumber("Петър", "0878123456") != null, "Смяна за несъществуващо име не става");
    }

    static void testSortedOrder() {
        PhoneBook pb = bigBook();
        ArrayList<String> all = pb.getAll();
        String[] expected = {"Ани", "Георги", "Иван", "Мария", "Петър", "Стоян", "Яна"};
        boolean ok = all.size() == expected.length;
        for (int i = 0; ok && i < expected.length; i++) {
            if (!all.get(i).startsWith(expected[i] + " - ")) {
                ok = false;
            }
        }
        check(ok, "Всички двойки се извеждат подредени по име: " + all);
        check(new PhoneBook().getAll().isEmpty(), "Празен указател връща празен списък");
    }

    static void testLoadFromFile() throws IOException {
        File f = File.createTempFile("phones", ".txt");
        f.deleteOnExit();
        Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        w.write("﻿Иван Петров, 0878123456\n");   // BOM от Notepad
        w.write("Мария, +359888654321\n");
        w.write("Георги, 00359899111222\n");
        w.write("\n");                                // празен ред
        w.write("Петър, 0868123456\n");               // невалиден оператор
        w.write("Без запетая 0878123456\n");          // липсва запетая
        w.write("Иван Петров, 0888000000\n");         // повтарящо се име
        w.write(", 0878123456\n");                    // без име
        w.write("Ани, 0878 123456\n");                // интервал в номера
        w.write("Боби, 0878123456, 0878123456\n");    // твърде много полета
        w.close();

        PhoneBook pb = new PhoneBook(f.getPath());
        check(pb.size() == 3, "От файла са заредени само 3-те валидни записа");
        check(pb.getLoadedCount() == 3, "Броят заредени е 3");
        check(pb.getLoadErrors().size() == 6, "6 невалидни реда са отчетени (празният ред се пропуска тихо)");
        check("+359878123456".equals(pb.getNumber("Иван Петров")), "Първият ред се чете въпреки BOM");
        check("+359899111222".equals(pb.getNumber("Георги")), "Номерът от файла е нормализиран");
        check(pb.getNumber("Петър") == null, "Записът с невалиден номер не е зареден");
        check("+359878123456".equals(pb.getNumber("Иван Петров")), "При повторено име остава първият номер");
    }

    static void testMissingFile() {
        PhoneBook pb = new PhoneBook();
        check(!pb.loadFromFile("няма_такъв_файл_12345.txt"), "Липсващ файл връща false");
        check(pb.getLoadErrors().size() == 1, "Има съобщение за грешка при липсващ файл");
        check(!pb.loadFromFile(""), "Празно име на файл връща false");
        check(!pb.loadFromFile("."), "Папка вместо файл връща false");
        check(pb.size() == 0, "Указателят остава празен");
    }
}
