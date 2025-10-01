package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.Income;
import com.goldenflame.pg102.repository.IncomeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final IncomeRepository incomeRepository;

    public ManagerController(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @GetMapping("/reports")
    public String showFinancialReports(@RequestParam(name = "period", defaultValue = "daily") String period, Model model) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period.toLowerCase()) {
            case "weekly":
                startDate = endDate.with(DayOfWeek.MONDAY);
                break;
            case "monthly":
                startDate = endDate.withDayOfMonth(1);
                break;
            case "daily":
            default:
                startDate = endDate;
                break;
        }

        List<Income> incomeList = incomeRepository.findByIncomeDateBetweenOrderByIncomeDateDesc(startDate, endDate);
        double totalIncome = incomeList.stream().mapToDouble(Income::getAmount).sum();

        model.addAttribute("incomeList", incomeList);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("selectedPeriod", period);

        return "manager/reports"; // Path to the new template
    }
}