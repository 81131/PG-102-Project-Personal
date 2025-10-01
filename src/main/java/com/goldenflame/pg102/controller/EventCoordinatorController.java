package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.EventBooking;
import com.goldenflame.pg102.repository.EventBookingRepository;
import com.goldenflame.pg102.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events/manage")
public class EventCoordinatorController {

    private final EventBookingRepository eventBookingRepository;
    private final NotificationService notificationService;

    public EventCoordinatorController(EventBookingRepository eventBookingRepository, NotificationService notificationService) {
        this.eventBookingRepository = eventBookingRepository;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String showEventDashboard(Model model) {
        // Fetch all bookings from the database, ordered by the most recent
        model.addAttribute("bookings", eventBookingRepository.findAllByOrderByEventDateTimeDesc());
        return "events/manage";
    }

    @PostMapping("/approve/{id}")
    public String approveBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        EventBooking booking = eventBookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        booking.setStatus("APPROVED_PENDING_PAYMENT");
        eventBookingRepository.save(booking);

        // Notify customer
        notificationService.createNotification(booking.getUser(),
                "Your event booking #" + id + " has been approved! Please proceed with the payment.",
                "/events/my-bookings"); // A future page for the customer

        redirectAttributes.addFlashAttribute("success", "Booking #" + id + " has been approved.");
        return "redirect:/events/manage";
    }

    @PostMapping("/reject/{id}")
    public String rejectBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        EventBooking booking = eventBookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        booking.setStatus("REJECTED");
        eventBookingRepository.save(booking);

        // Notify customer
        notificationService.createNotification(booking.getUser(),
                "We're sorry, but your event booking #" + id + " has been rejected.",
                "/events/my-bookings");

        redirectAttributes.addFlashAttribute("success", "Booking #" + id + " has been rejected.");
        return "redirect:/events/manage";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        EventBooking booking = eventBookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking Id:" + id));
        // Only cancel if it was approved but not yet paid
        if ("APPROVED_PENDING_PAYMENT".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            eventBookingRepository.save(booking);

            // Notify customer
            notificationService.createNotification(booking.getUser(),
                    "Your event booking #" + id + " has been cancelled by the coordinator.",
                    "/events/my-bookings");

            redirectAttributes.addFlashAttribute("success", "Booking #" + id + " has been cancelled.");
        } else {
            redirectAttributes.addFlashAttribute("error", "This booking cannot be cancelled.");
        }
        return "redirect:/events/manage";
    }
}