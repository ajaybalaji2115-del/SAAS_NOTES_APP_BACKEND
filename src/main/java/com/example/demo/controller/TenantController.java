package com.example.demo.controller;

import com.example.demo.entity.SubscriptionPlan;
import com.example.demo.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    
    
   

    @PostMapping("/{slug}/upgrade")
    public ResponseEntity<Void> upgradeTenant(@PathVariable String slug, HttpServletRequest request) {
        try {
            // ✅ Correctly calls the upgradeTenant method with the PRO plan
            tenantService.upgradeTenantPlan(slug, SubscriptionPlan.PRO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
