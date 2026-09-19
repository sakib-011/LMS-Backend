package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "acquisitions")
public class Acquisition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String supplier;

    private String requestedBy;

    private Double cost;

    private String status;

    private LocalDate orderDate;

    public Acquisition() {}

    public Acquisition(String id, String title, String author, String supplier, String requestedBy, Double cost, String status, LocalDate orderDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.supplier = supplier;
        this.requestedBy = requestedBy;
        this.cost = cost;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public static AcquisitionBuilder builder() { return new AcquisitionBuilder(); }

    public static class AcquisitionBuilder {
        private String id;
        private String title;
        private String author;
        private String supplier;
        private String requestedBy;
        private Double cost;
        private String status;
        private LocalDate orderDate;

        public AcquisitionBuilder id(String id) { this.id = id; return this; }
        public AcquisitionBuilder title(String title) { this.title = title; return this; }
        public AcquisitionBuilder author(String author) { this.author = author; return this; }
        public AcquisitionBuilder supplier(String supplier) { this.supplier = supplier; return this; }
        public AcquisitionBuilder requestedBy(String requestedBy) { this.requestedBy = requestedBy; return this; }
        public AcquisitionBuilder cost(Double cost) { this.cost = cost; return this; }
        public AcquisitionBuilder status(String status) { this.status = status; return this; }
        public AcquisitionBuilder orderDate(LocalDate orderDate) { this.orderDate = orderDate; return this; }

        public Acquisition build() {
            return new Acquisition(id, title, author, supplier, requestedBy, cost, status, orderDate);
        }
    }
}
