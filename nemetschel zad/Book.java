public class Book {
    private String title;
    private String author;
    private String date;
    private String genre;

    public Book(String title, String author, String date, String genre) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return title + " | " + author + " | " + date + " | " + genre;
    }
}
