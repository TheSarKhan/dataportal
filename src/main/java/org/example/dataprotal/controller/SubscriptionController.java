package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.SubscriptionRequest;
import org.example.dataprotal.model.user.Subscription;
import org.example.dataprotal.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize( "hasRole('ADMIN')")
@RequestMapping("/api/v1/subscription")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Subscription> create(@RequestBody SubscriptionRequest request){
        return ResponseEntity.ok(subscriptionService.createSubscription(request));
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAll(){
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getById(@PathVariable Long id){
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Subscription> update(@PathVariable Long id,
                                               @RequestBody SubscriptionRequest request){
        return ResponseEntity.ok(subscriptionService.updateSubscription(id,request));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        subscriptionService.deleteSubscriptionById(id);
        return ResponseEntity.ok().build();
    }
}
