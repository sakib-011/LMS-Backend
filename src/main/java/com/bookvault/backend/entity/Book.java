package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String category;

    @Column(unique = true)
    private String isbn;

    private String publisher;

    @Column(name = "publication_year")
    private Integer year;

    private Double rating;

    private Integer ratingCount;

    @Column(length = 2000)
    private String description;

    private String coverColor;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;


    @Column(name = "cloudinary_public_id")
    private String cloudinaryPublicId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_tags", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "tag")
    private List<String> tags;

    private Integer physicalCopies;

    private Integer physicalAvailable;

    private Boolean hasDigital;

    private Integer pages;

    private String edition;

    private Double gateScore;

    private String physicalStacks;

    public Book() {}

    public Book(String id, String title, String author, String category, String isbn, String publisher, Integer year, Double rating, Integer ratingCount, String description, String coverColor, String imageUrl, String cloudinaryPublicId, List<String> tags, Integer physicalCopies, Integer physicalAvailable, Boolean hasDigital, Integer pages, String edition, Double gateScore, String physicalStacks) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.publisher = publisher;
        this.year = year;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.description = description;
        this.coverColor = coverColor;
        this.imageUrl = imageUrl;
        this.cloudinaryPublicId = cloudinaryPublicId;
        this.tags = tags;
        this.physicalCopies = physicalCopies;
        this.physicalAvailable = physicalAvailable;
        this.hasDigital = hasDigital;
        this.pages = pages;
        this.edition = edition;
        this.gateScore = gateScore;
        this.physicalStacks = physicalStacks;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverColor() { return coverColor; }
    public void setCoverColor(String coverColor) { this.coverColor = coverColor; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getCloudinaryPublicId() { return cloudinaryPublicId; }
    public void setCloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Integer getPhysicalCopies() { return physicalCopies; }
    public void setPhysicalCopies(Integer physicalCopies) { this.physicalCopies = physicalCopies; }

    public Integer getPhysicalAvailable() { return physicalAvailable; }
    public void setPhysicalAvailable(Integer physicalAvailable) { this.physicalAvailable = physicalAvailable; }

    public Boolean getHasDigital() { return hasDigital; }
    public void setHasDigital(Boolean hasDigital) { this.hasDigital = hasDigital; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public Double getGateScore() { return gateScore; }
    public void setGateScore(Double gateScore) { this.gateScore = gateScore; }

    public String getPhysicalStacks() { return physicalStacks; }
    public void setPhysicalStacks(String physicalStacks) { this.physicalStacks = physicalStacks; }

    public static BookBuilder builder() { return new BookBuilder(); }

    public static class BookBuilder {
        private String id;
        private String title;
        private String author;
        private String category;
        private String isbn;
        private String publisher;
        private Integer year;
        private Double rating;
        private Integer ratingCount;
        private String description;
        private String coverColor;
        private String imageUrl;
        private String cloudinaryPublicId;
        private List<String> tags;
        private Integer physicalCopies;
        private Integer physicalAvailable;
        private Boolean hasDigital;
        private Integer pages;
        private String edition;
        private Double gateScore;
        private String physicalStacks;

        public BookBuilder id(String id) { this.id = id; return this; }
        public BookBuilder title(String title) { this.title = title; return this; }
        public BookBuilder author(String author) { this.author = author; return this; }
        public BookBuilder category(String category) { this.category = category; return this; }
        public BookBuilder isbn(String isbn) { this.isbn = isbn; return this; }
        public BookBuilder publisher(String publisher) { this.publisher = publisher; return this; }
        public BookBuilder year(Integer year) { this.year = year; return this; }
        public BookBuilder rating(Double rating) { this.rating = rating; return this; }
        public BookBuilder ratingCount(Integer ratingCount) { this.ratingCount = ratingCount; return this; }
        public BookBuilder description(String description) { this.description = description; return this; }
        public BookBuilder coverColor(String coverColor) { this.coverColor = coverColor; return this; }
        public BookBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public BookBuilder cloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; return this; }
        public BookBuilder tags(List<String> tags) { this.tags = tags; return this; }
        public BookBuilder physicalCopies(Integer physicalCopies) { this.physicalCopies = physicalCopies; return this; }
        public BookBuilder physicalAvailable(Integer physicalAvailable) { this.physicalAvailable = physicalAvailable; return this; }
        public BookBuilder hasDigital(Boolean hasDigital) { this.hasDigital = hasDigital; return this; }
        public BookBuilder pages(Integer pages) { this.pages = pages; return this; }
        public BookBuilder edition(String edition) { this.edition = edition; return this; }
        public BookBuilder gateScore(Double gateScore) { this.gateScore = gateScore; return this; }
        public BookBuilder physicalStacks(String physicalStacks) { this.physicalStacks = physicalStacks; return this; }

        public Book build() {
            return new Book(id, title, author, category, isbn, publisher, year, rating, ratingCount, description, coverColor, imageUrl, cloudinaryPublicId, tags, physicalCopies, physicalAvailable, hasDigital, pages, edition, gateScore, physicalStacks);
        }
    }
}
