package com.homeserver.Controllers;

import com.homeserver.util.ApiResponse;
import com.homeserver.util.alert.AlertService;
import com.homeserver.util.alert.EmailRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/alert")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // Never returns the raw address - only whether one is configured and a masked preview.
    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> getEmail() {
        boolean configured = alertService.checkEmail();
        String masked = configured ? alertService.getMaskedEmail() : null;
        return ResponseEntity.ok(Map.of(
                "configured", configured,
                "email", masked == null ? "" : masked
        ));
    }

    @PostMapping("/email")
    public ResponseEntity<ApiResponse> setEmail(@RequestBody EmailRequest request) {
        try {
            alertService.updateEmail(request.email());
            return ResponseEntity.ok(new ApiResponse(true, "Alert email saved"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Failed to save alert email"));
        }
    }

    @DeleteMapping("/email")
    public ResponseEntity<ApiResponse> clearEmail() {
        alertService.clearEmail();
        return ResponseEntity.ok(new ApiResponse(true, "Alert email removed"));
    }
}
