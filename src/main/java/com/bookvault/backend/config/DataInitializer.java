package com.bookvault.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bookvault.backend.entity.*;
import com.bookvault.backend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private DigitalBookRepository digitalBookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Seed Default Users
        User student = User.builder()
                .id("STU-2024-0440")
                .studentId("STU-2024-0440")
                .name("Sakib Shourov")
                .email("student@university.edu")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .status("Active")
                .department("Computer Science")
                .phone("+880 1700 000440")
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build();
        userRepository.save(student);

        User moderator = User.builder()
                .id("MOD-101")
                .studentId("MOD-101")
                .name("Sarah Jenkins")
                .email("moderator@university.edu")
                .password(passwordEncoder.encode("password123"))
                .role(Role.MODERATOR)
                .status("Active")
                .department("Circulation Desk")
                .phone("+880 1700 000101")
                .createdAt(LocalDateTime.now().minusYears(1))
                .build();
        userRepository.save(moderator);

        User admin = User.builder()
                .id("ADM-001")
                .studentId("ADM-001")
                .name("System Administrator")
                .email("admin@university.edu")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ADMINISTRATOR)
                .status("Active")
                .department("Library Operations")
                .phone("+880 1700 000001")
                .createdAt(LocalDateTime.now().minusYears(2))
                .build();
        userRepository.save(admin);

        // 2. Seed Books Catalog (Matching frontend mockData.ts)
        Book b1 = Book.builder()
                .id("b1")
                .title("Introduction to Algorithms")
                .author("Thomas H. Cormen")
                .category("Computer Science")
                .isbn("978-0-262-03384-8")
                .publisher("MIT Press")
                .year(2022)
                .rating(4.8)
                .ratingCount(2847)
                .description("The world's leading textbook on algorithms. Extensively revised and updated, this edition covers van Emde Boas trees and multithreaded algorithms.")
                .coverColor("#2D3748")
                .tags(List.of("algorithms", "data structures", "programming"))
                .physicalCopies(8)
                .physicalAvailable(3)
                .hasDigital(true)
                .pages(1292)
                .edition("4th Ed.")
                .gateScore(9.82)
                .physicalStacks("Stack 2A")
                .build();

        Book b2 = Book.builder()
                .id("b2")
                .title("Clean Code")
                .author("Robert C. Martin")
                .category("Computer Science")
                .isbn("978-0-13-235088-4")
                .publisher("Prentice Hall")
                .year(2008)
                .rating(4.6)
                .ratingCount(5621)
                .description("A handbook of agile software craftsmanship. Even bad code can function. But if code isn't clean, it can bring an organization to its knees.")
                .coverColor("#1A202C")
                .tags(List.of("software engineering", "best practices", "refactoring"))
                .physicalCopies(5)
                .physicalAvailable(0)
                .hasDigital(true)
                .pages(431)
                .edition("1st Ed.")
                .gateScore(6.15)
                .physicalStacks("Stack 5C")
                .build();

        Book b3 = Book.builder()
                .id("b3")
                .title("The Art of Computer Programming")
                .author("Donald E. Knuth")
                .category("Computer Science")
                .isbn("978-0-201-89683-1")
                .publisher("Addison-Wesley")
                .year(2011)
                .rating(4.9)
                .ratingCount(1203)
                .description("The bible of all fundamental algorithms and the work that taught many of today's software developers most of what they know.")
                .coverColor("#744210")
                .tags(List.of("algorithms", "mathematics", "computer science"))
                .physicalCopies(4)
                .physicalAvailable(2)
                .hasDigital(false)
                .pages(672)
                .edition("3rd Ed.")
                .gateScore(5.81)
                .physicalStacks("Stack 1B")
                .build();

        Book b4 = Book.builder()
                .id("b4")
                .title("A Brief History of Time")
                .author("Stephen Hawking")
                .category("Science")
                .isbn("978-0-553-38016-3")
                .publisher("Bantam Books")
                .year(1998)
                .rating(4.7)
                .ratingCount(12456)
                .description("Hawking's account of the history of the universe, from the big bang to black holes, written for a general audience.")
                .coverColor("#1C4532")
                .tags(List.of("physics", "cosmology", "science"))
                .physicalCopies(10)
                .physicalAvailable(7)
                .hasDigital(true)
                .pages(212)
                .edition("2nd Ed.")
                .gateScore(8.15)
                .physicalStacks("Stack 3D")
                .build();

        Book b5 = Book.builder()
                .id("b5")
                .title("Sapiens: A Brief History of Humankind")
                .author("Yuval Noah Harari")
                .category("History")
                .isbn("978-0-06-231609-7")
                .publisher("Harper Collins")
                .year(2015)
                .rating(4.5)
                .ratingCount(18920)
                .description("From a renowned historian comes a groundbreaking narrative of humanity's creation and evolution.")
                .coverColor("#553C1E")
                .tags(List.of("history", "anthropology", "evolution"))
                .physicalCopies(6)
                .physicalAvailable(1)
                .hasDigital(true)
                .pages(443)
                .edition("1st Ed.")
                .gateScore(5.50)
                .physicalStacks("Stack 4A")
                .build();

        Book b6 = Book.builder()
                .id("b6")
                .title("The Lean Startup")
                .author("Eric Ries")
                .category("Business")
                .isbn("978-0-307-88791-7")
                .publisher("Crown Business")
                .year(2011)
                .rating(4.3)
                .ratingCount(9832)
                .description("How today's entrepreneurs use continuous innovation to create radically successful businesses.")
                .coverColor("#234E52")
                .tags(List.of("entrepreneurship", "startup", "innovation"))
                .physicalCopies(7)
                .physicalAvailable(4)
                .hasDigital(true)
                .pages(336)
                .edition("1st Ed.")
                .gateScore(7.25)
                .physicalStacks("Stack 6B")
                .build();

        bookRepository.saveAll(List.of(b1, b2, b3, b4, b5, b6));

        // 3. Seed Borrowings (Empty initially; populated dynamically when books are issued by Admin/Moderator)
        // borrowingRepository remains empty for fresh state

        // 4. Seed Reservations
        Reservation res1 = Reservation.builder()
                .user(student)
                .book(b2)
                .reservedDate(LocalDate.now().minusDays(3))
                .expiryDate(LocalDate.now().plusDays(5))
                .queuePosition(1)
                .status("ready")
                .pickupDeadline(LocalDate.now().plusDays(2))
                .build();
        reservationRepository.save(res1);

        // 5. Seed Book Requests
        BookRequest req1 = BookRequest.builder()
                .user(student)
                .title("Deep Learning")
                .author("Ian Goodfellow")
                .isbn("978-0-262-03561-3")
                .reason("Required for Machine Learning course project.")
                .status("approved")
                .submittedDate(LocalDate.now().minusDays(10))
                .notes("Will be acquired next week.")
                .build();
        bookRequestRepository.save(req1);

        // 6. Seed Digital Books
        DigitalBook dig1 = DigitalBook.builder()
                .user(student)
                .book(b1)
                .addedDate(LocalDate.now().minusDays(30))
                .lastRead(LocalDate.now().minusDays(1))
                .progress(45)
                .bookmarked(true)
                .currentPage(582)
                .build();
        digitalBookRepository.save(dig1);

        // 7. Seed Reviews
        Review rev1 = Review.builder()
                .user(student)
                .book(b6)
                .rating(5)
                .comment("Transformative read for software entrepreneurs.")
                .date(LocalDate.now().minusDays(14))
                .helpful(12)
                .build();
        reviewRepository.save(rev1);

        // 8. Seed Wishlist
        Wishlist w1 = Wishlist.builder()
                .user(student)
                .book(b3)
                .addedDate(LocalDate.now().minusDays(5))
                .build();
        wishlistRepository.save(w1);

        // 9. Seed Notifications
        Notification n1 = Notification.builder()
                .user(student)
                .title("Book Ready for Pickup")
                .description("'Clean Code' is ready for pickup at the main circulation desk.")
                .createdAt(LocalDateTime.now().minusHours(2))
                .isUnread(true)
                .type("reservation")
                .icon("fas fa-check-circle")
                .build();
        notificationRepository.save(n1);

        // 10. Seed Audit Logs
        AuditLog log1 = AuditLog.builder()
                .userEmail(student.getEmail())
                .action("User Login")
                .category("Authentication")
                .ipAddress("127.0.0.1")
                .timestamp(LocalDateTime.now().minusMinutes(30))
                .details("Successful student authentication session")
                .build();
        auditLogRepository.save(log1);

        // 11. Seed Fines (Empty initially; populated dynamically when loans become overdue)
        // fineRepository remains empty initially

        // 12. Seed System Settings
        SystemSetting s1 = SystemSetting.builder()
                .settingKey("FINE_PER_DAY")
                .settingValue("5.00")
                .description("Daily penalty rate in USD for overdue loans")
                .build();
        SystemSetting s2 = SystemSetting.builder()
                .settingKey("MAX_LOAN_DAYS")
                .settingValue("14")
                .description("Maximum standard borrow period for students")
                .build();
        systemSettingRepository.saveAll(List.of(s1, s2));

        System.out.println("=== BookGrid Initial Seed Data Successfully Initialized ===");
    }
}
