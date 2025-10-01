package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.*;
import com.goldenflame.pg102.repository.*;
import com.goldenflame.pg102.service.NotificationService;
import com.goldenflame.pg102.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events")
public class EventController {

    private final CatalogueItemRepository catalogueItemRepository;
    private final EventBookingRepository eventBookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;
    private final IncomeRepository incomeRepository;
    private final OrderService orderService;

    public EventController(CatalogueItemRepository catalogueItemRepository,
                           EventBookingRepository eventBookingRepository,
                           UserRepository userRepository,
                           NotificationService notificationService,
                           PaymentRepository paymentRepository,
                           IncomeRepository incomeRepository,
                           OrderService orderService) {
        this.catalogueItemRepository = catalogueItemRepository;
        this.eventBookingRepository = eventBookingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.paymentRepository = paymentRepository;
        this.incomeRepository = incomeRepository;
        this.orderService = orderService;
    }

    @GetMapping("/book")
    public String showEventBookingPage(Model model) {
        // Fetch all items that are in the "Event Package" category
        List<CatalogueItem> eventPackages = catalogueItemRepository.findByCategory_Name("Event Package");
        model.addAttribute("eventPackages", eventPackages);

        // Fetch dates that are already booked to disable them on the frontend
        List<LocalDate> unavailableDates = eventBookingRepository.findAll().stream()
                .filter(booking -> !booking.getStatus().equals("CANCELLED") && !booking.getStatus().equals("REJECTED"))
                .map(booking -> booking.getEventDateTime().toLocalDate())
                .collect(Collectors.toList());
        model.addAttribute("unavailableDates", unavailableDates);

        return "events/book"; // The new customer-facing template
    }

    @PostMapping("/book/request")
    public String requestEventBooking(@RequestParam Long packageId,
                                      @RequestParam LocalDate eventDate,
                                      @RequestParam LocalTime eventTime,
                                      @RequestParam int numberOfGuests,
                                      @RequestParam(required = false) String specialRequests,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        CatalogueItem selectedPackage = catalogueItemRepository.findById(packageId).orElseThrow();
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

        // Server-side check for date availability
        boolean isDateTaken = eventBookingRepository.findAll().stream()
                .anyMatch(booking -> booking.getEventDateTime().toLocalDate().equals(eventDate)
                        && !booking.getStatus().equals("CANCELLED") && !booking.getStatus().equals("REJECTED"));

        if (isDateTaken) {
            redirectAttributes.addFlashAttribute("error", "Sorry, the selected date is no longer available. Please choose another date.");
            return "redirect:/events/book";
        }

        EventBooking booking = new EventBooking();
        booking.setUser(currentUser);
        booking.setEventPackage(selectedPackage);
        booking.setEventDateTime(eventDateTime);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setSpecialRequests(specialRequests);
        booking.setStatus("PENDING_APPROVAL");

        EventBooking savedBooking = eventBookingRepository.save(booking);

        // Notify Event Coordinators
        List<User> coordinators = userRepository.findByRole_Name("ROLE_EVENT_COORDINATOR");
        for (User coordinator : coordinators) {
            notificationService.createNotification(coordinator,
                    "New event booking request #" + savedBooking.getId() + " for " + eventDate,
                    "/events/manage");
        }

        redirectAttributes.addFlashAttribute("success", "Your booking request has been sent! An event coordinator will review it shortly.");
        return "redirect:/events";
    }


    @GetMapping("/my-bookings")
    public String showMyBookings(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<EventBooking> bookings = eventBookingRepository.findByUserOrderByEventDateTimeDesc(currentUser);
        model.addAttribute("bookings", bookings);
        return "events/my-bookings";
    }


    @GetMapping("/payment/{id}")
    public String showEventPaymentPage(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        EventBooking booking = eventBookingRepository.findById(id).orElseThrow();
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Security check: ensure the booking belongs to the current user
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/events/my-bookings";
        }

        // Calculate the total price based on your logic
        float totalPrice = booking.getEventPackage().getPrice() * booking.getNumberOfGuests();
        if (booking.getNumberOfGuests() > 50 && booking.getEventPackage().getBasePrice() != null) {
            totalPrice += booking.getEventPackage().getBasePrice();
        }

        model.addAttribute("booking", booking);
        model.addAttribute("totalPrice", totalPrice);
        return "events/payment";
    }

    // ADD THIS METHOD TO PROCESS THE PAYMENT
    @PostMapping("/payment/submit")
    public String processEventPayment(@RequestParam Long bookingId,
                                      @RequestParam String paymentMethod,
                                      @RequestParam(required = false) String cardNumber,
                                      @RequestParam(required = false) String cvc,
                                      @RequestParam(required = false) Integer expiryMonth,
                                      @RequestParam(required = false) Integer expiryYear,
                                      RedirectAttributes redirectAttributes) {

        EventBooking booking = eventBookingRepository.findById(bookingId).orElseThrow();

        // Card validation
        if ("CARD".equals(paymentMethod)) {
            if (!orderService.validateCard(cardNumber, cvc, expiryMonth, expiryYear)) {
                redirectAttributes.addFlashAttribute("error", "Invalid card details. Please check and try again.");
                return "redirect:/events/payment/" + bookingId;
            }
        }

        // Calculate final price again for security
        float totalPrice = booking.getEventPackage().getPrice() * booking.getNumberOfGuests();
        if (booking.getNumberOfGuests() > 50 && booking.getEventPackage().getBasePrice() != null) {
            totalPrice += booking.getEventPackage().getBasePrice();
        }

        // Create Payment record
        Payment payment = new Payment();
        payment.setAmount(totalPrice);
        payment.setMethod(paymentMethod);
        payment.setStatus("COMPLETED"); // Assume payment is successful
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        // Create Income record
        Income income = new Income();
        income.setPayment(savedPayment);
        income.setAmount(totalPrice);
        income.setIncomeType("EVENT_BOOKING");
        income.setIncomeDate(LocalDate.now());
        incomeRepository.save(income);

        // Update booking status
        booking.setPayment(savedPayment);
        booking.setStatus("CONFIRMED");
        eventBookingRepository.save(booking);

        redirectAttributes.addFlashAttribute("success", "Payment successful! Your event is confirmed.");
        return "redirect:/events/my-bookings";
    }
}