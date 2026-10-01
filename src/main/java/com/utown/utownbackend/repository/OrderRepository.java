package com.utown.utownbackend.repository;

import com.utown.utownbackend.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = {"user", "restaurant", "address"})
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@org.springframework.data.repository.query.Param("id") Long id);

    @EntityGraph(attributePaths = {
            "restaurant",
            "restaurant.owner",
            "user"
    })
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findWithUserAndRestaurantOwnerById(@org.springframework.data.repository.query.Param("id") Long id);

}

