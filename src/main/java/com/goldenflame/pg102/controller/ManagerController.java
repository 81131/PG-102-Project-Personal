package com.goldenflame.pg102.controller;

import com.goldenflame.pg102.model.Income;
import com.goldenflame.pg102.model.InventoryPurchase;
import com.goldenflame.pg102.model.ManualExpense;
import com.goldenflame.pg102.repository.IncomeRepository;
import com.goldenflame.pg102.repository.InventoryPurchaseRepository;
import com.goldenflame.pg102.repository.ManualExpenseRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final IncomeRepository incomeRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final ManualExpenseRepository manualExpenseRepository;

    public ManagerController(IncomeRepository incomeRepository,
                             InventoryPurchaseRepository inventoryPurchaseRepository,
                             ManualExpenseRepository manualExpenseRepository) {
        this.incomeRepository = incomeRepository;
        this.inventoryPurchaseRepository = inventoryPurchaseRepository;
        this.manualExpenseRepository = manualExpenseRepository;
    }

    @GetMapping("/reports")
    public String showFinancialReports(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        // Set default date range to today if not provided
        if (startDate == null || endDate == null) {
            startDate = LocalDate.now();
            endDate = LocalDate.now();
        }

        // 1. Fetch all data within the date range
        List<Income> incomeList = incomeRepository.findByIncomeDateBetweenOrderByIncomeDateDesc(startDate, endDate);
        List<InventoryPurchase> purchases = inventoryPurchaseRepository.findByPurchaseDateBetweenOrderByPurchaseDateDesc(startDate, endDate);
        List<ManualExpense> manualExpenses = manualExpenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(startDate, endDate);

        // 2. Calculate totals for display and charts
        double totalIncome = incomeList.stream().mapToDouble(Income::getAmount).sum();
        double purchaseExpenses = purchases.stream().mapToDouble(p -> p.getQuantityPurchased() * p.getUnitPrice()).sum();
        double otherExpenses = manualExpenses.stream().mapToDouble(ManualExpense::getAmount).sum();
        double totalExpenses = purchaseExpenses + otherExpenses;
        double netProfit = totalIncome - totalExpenses;

        // 3. Prepare data for the income diversity bar chart
        Map<String, Double> incomeByType = incomeList.stream()
                .collect(Collectors.groupingBy(Income::getIncomeType, Collectors.summingDouble(Income::getAmount)));

        // 4. Add all data to the model
        model.addAttribute("incomeList", incomeList);
        model.addAttribute("purchaseExpenses", purchases);
        model.addAttribute("manualExpenses", manualExpenses);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("netProfit", netProfit);
        model.addAttribute("incomeByType", incomeByType); // For the bar chart
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "manager/reports";
    }

    @PostMapping("/reports/expense/add")
    public String addManualExpense(@RequestParam String description,
                                   @RequestParam float amount,
                                   @RequestParam LocalDate expenseDate,
                                   @RequestParam String category,
                                   RedirectAttributes redirectAttributes) {
        ManualExpense expense = new ManualExpense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setExpenseDate(expenseDate);
        expense.setCategory(category);
        manualExpenseRepository.save(expense);

        redirectAttributes.addFlashAttribute("success", "Manual expense recorded successfully.");
        // Redirect back to the reports page with the same date filter
        return "redirect:/manager/reports?startDate=" + expenseDate + "&endDate=" + expenseDate;
    }
}