package com.utown.utownbackend.security;

import com.utown.utownbackend.repository.DeliveryAssignmentRepository;
import com.utown.utownbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;
    private final DeliveryAssignmentRepository deliveryAssignmentRepository;

    public boolean isCustomer(Authentication authentication, Long orderId) {
        if (authentication == null || orderId == null) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            return false;
        }

        Long userId = principal.getId();

        return orderRepository.findWithUserAndRestaurantOwnerById(orderId)
                .map(order ->
                        order.getUser() != null
                                && userId != null
                                && userId.equals(order.getUser().getId())
                )
                .orElse(false);
    }

    public boolean isRestaurantOwner(Authentication authentication, Long orderId) {
        if (authentication == null || orderId == null) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            return false;
        }

        Long userId = principal.getId();

        return orderRepository.findWithUserAndRestaurantOwnerById(orderId)
                .map(order ->
                        order.getRestaurant() != null
                                && order.getRestaurant().getOwner() != null
                                && userId != null
                                && userId.equals(
                                order.getRestaurant().getOwner().getId()
                        )
                )
                .orElse(false);
    }

    public boolean isAssignedRider(Authentication authentication, Long orderId) {
        if (authentication == null || orderId == null) {
            return false;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            return false;
        }

        Long userId = principal.getId();

        return userId != null && deliveryAssignmentRepository.existsByOrderIdAndRiderUserId(orderId, userId);
    }
}
