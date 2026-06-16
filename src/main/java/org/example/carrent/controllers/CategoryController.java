package org.example.carrent.controllers;

import org.example.carrent.models.VehicleCategoryConfig;
import org.example.carrent.services.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/categories", "/api/categories"})
public class CategoryController {

    private final VehicleCategoryConfigService categoryConfigService;

    public CategoryController(VehicleCategoryConfigService categoryConfigService) {
        this.categoryConfigService = categoryConfigService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return categoryConfigService.findAllCategories();
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        return categoryConfigService.getByCategory(category);
    }
}