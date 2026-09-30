import java.time.DateTimeException;
import java.time.LocalDate;

// Всички методи връщат null ако стойността е валидна,
// иначе връщат текст с грешката.
public class Validator {
    public static final int MAX_TITLE = 100;
    public static final int MAX_AUTHOR = 60;
    public static final int MAX_GENRE = 30;

    public static String checkTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Заглавието не може да е празно.";
        }
        title = title.trim();
        if (title.length() > MAX_TITLE) {
            return "Заглавието е по-дълго от " + MAX_TITLE + " символа.";
        }
        if (!hasLetterOrDigit(title)) {
            return "Заглавието трябва да съдържа поне една буква или цифра.";
        }
        return null;
    }

    public static String checkAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return "Авторът не може да е празен.";
        }
        author = author.trim();
        if (author.length() < 2) {
            return "Името на автора е твърде кратко (поне 2 символа).";
        }
        if (author.length() > MAX_AUTHOR) {
            return "Името на автора е по-дълго от " + MAX_AUTHOR + " символа.";
        }
        for (int i = 0; i < author.length(); i++) {
            char c = author.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '.' && c != '-' && c != '\'') {
                return "Авторът може да съдържа само букви, интервали, точки, тирета и апостроф.";
            }
        }
        if (!hasLetter(author)) {
            return "Авторът трябва да съдържа поне една буква.";
        }
        return null;
    }

    public static String checkGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return "Жанрът не може да е празен.";
        }
        genre = genre.trim();
        if (genre.length() < 2) {
            return "Жанрът е твърде кратък (поне 2 символа).";
        }
        if (genre.length() > MAX_GENRE) {
            return "Жанрът е по-дълъг от " + MAX_GENRE + " символа.";
        }
        for (int i = 0; i < genre.length(); i++) {
            char c = genre.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '-') {
                return "Жанрът може да съдържа само букви, интервали и тирета.";
            }
        }
        return null;
    }

    // датата трябва да е във формат дд.мм.гггг, да съществува и да не е в бъдещето
    public static String checkDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return "Датата не може да е празна.";
        }
        date = date.trim();
        String[] parts = date.split("\\.");
        if (parts.length != 3 || parts[0].length() != 2 || parts[1].length() != 2 || parts[2].length() != 4) {
            return "Датата трябва да е във формат дд.мм.гггг (например 05.03.1999).";
        }
        for (int i = 0; i < parts.length; i++) {
            if (!isDigits(parts[i])) {
                return "Датата трябва да съдържа само цифри и точки.";
            }
        }
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        if (year < 1400) {
            return "Годината трябва да е поне 1400.";
        }
        if (month < 1 || month > 12) {
            return "Месецът трябва да е между 01 и 12.";
        }
        LocalDate d;
        try {
            d = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return "Такава дата не съществува.";
        }
        if (d.isAfter(LocalDate.now())) {
            return "Датата на издаване не може да е в бъдещето.";
        }
        return null;
    }

    // избор от меню - трябва да е цяло число между min и max
    public static boolean isMenuChoice(String text, int min, int max) {
        if (text == null) {
            return false;
        }
        text = text.trim();
        if (text.isEmpty() || text.length() > 3 || !isDigits(text)) {
            return false;
        }
        int n = Integer.parseInt(text);
        return n >= min && n <= max;
    }

    // Чете критериите за сортиране, например "2 1", "2,1" или "21".
    // Връща масив с критериите или null ако въведеното е невалидно.
    public static int[] parseCriteria(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        int[] temp = new int[3];
        int n = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ' || c == ',' || c == ';') {
                continue;
            }
            if (c < '1' || c > '3') {
                return null;
            }
            int value = c - '0';
            for (int j = 0; j < n; j++) {
                if (temp[j] == value) {
                    return null; // повтаря се
                }
            }
            temp[n] = value;
            n++;
        }
        if (n == 0) {
            return null;
        }
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = temp[i];
        }
        return result;
    }

    private static boolean isDigits(String s) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    private static boolean hasLetter(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasLetterOrDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetterOrDigit(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
