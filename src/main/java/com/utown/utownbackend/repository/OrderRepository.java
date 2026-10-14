package com.utown.utownbackend.repository;

import com.utown.utownbackend.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = {"user", "restaurant", "address"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "restaurant",
            "restaurant.owner",
            "user"
    })
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findWithUserAndRestaurantOwnerById(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = {"user", "restaurant", "address"})
    List<Order> findAll(Specification<Order> spec);

}

