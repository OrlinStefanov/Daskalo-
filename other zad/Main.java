import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");

        System.out.print("Име на файл (Enter за phones.txt): ");
        String fileName = sc.nextLine().trim();
        if (fileName.isEmpty()) {
            fileName = "phones.txt";
        }
        PhoneBook book = new PhoneBook(fileName);

        String choice;
        do {
            System.out.println();
            System.out.println("===== Телефонен указател =====");
            System.out.println("1. Добави");
            System.out.println("2. Изтрий по име");
            System.out.println("3. Търси номер по име");
            System.out.println("4. Покажи всички");
            System.out.println("0. Изход");
            System.out.print("Избор: ");
            choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Име: ");
                String name = sc.nextLine().trim();
                System.out.print("Номер: ");
                String number = sc.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Името не може да е празно!");
                } else if (book.add(name, number)) {
                    System.out.println("Добавено.");
                } else {
                    System.out.println("Невалиден номер!");
                }
            } else if (choice.equals("2")) {
                System.out.print("Име: ");
                String name = sc.nextLine().trim();
                if (book.remove(name)) {
                    System.out.println("Изтрито.");
                } else {
                    System.out.println("Няма такова име.");
                }
            } else if (choice.equals("3")) {
                System.out.print("Име: ");
                String name = sc.nextLine().trim();
                String number = book.getNumber(name);
                if (number != null) {
                    System.out.println(name + " - " + number);
                } else {
                    System.out.println("Няма такова име.");
                }
            } else if (choice.equals("4")) {
                book.print();
            } else if (!choice.equals("0")) {
                System.out.println("Невалиден избор!");
            }
        } while (!choice.equals("0"));

        System.out.println("Довиждане!");
        sc.close();
    }
}
