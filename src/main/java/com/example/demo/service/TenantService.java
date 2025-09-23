// src/main/java/com/example/demo/service/TenantService.java
package com.example.demo.service;

import com.example.demo.entity.Tenant;
import com.example.demo.entity.SubscriptionPlan; // Import SubscriptionPlan if it's in a separate file
import com.example.demo.repository.TenantRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    // Note: The createTenant method parameter was 'Tenant.Plan plan', changed to 'SubscriptionPlan plan'
    public Tenant createTenant(String slug, String name, SubscriptionPlan plan) {
        if (tenantRepository.findBySlug(slug).isPresent()) {
            throw new RuntimeException("Tenant already exists with slug: " + slug);
        }
        // ✅ Now Tenant.builder() should work because of @Builder annotation
        Tenant tenant = Tenant.builder()
                .slug(slug)
                .name(name)
                .plan(plan) // Use the corrected parameter type
                .build();
        return tenantRepository.save(tenant);
    }

    public Tenant getTenantBySlug(String slug) {
        return tenantRepository.findBySlug(slug).orElseThrow(
                () -> new RuntimeException("Tenant not found with slug: " + slug)
        );
    }

    // You might also want a method to upgrade the tenant plan
    public Tenant upgradeTenantPlan(String slug, SubscriptionPlan newPlan) {
        Tenant tenant = getTenantBySlug(slug); // Re-use existing method
        tenant.setPlan(newPlan);
        return tenantRepository.save(tenant);
    }
}