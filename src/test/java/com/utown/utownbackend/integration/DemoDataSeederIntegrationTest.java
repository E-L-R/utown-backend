package com.utown.utownbackend.integration;

import com.utown.utownbackend.entity.Restaurant;
import com.utown.utownbackend.repository.DishRepository;
import com.utown.utownbackend.repository.OrderRepository;
import com.utown.utownbackend.repository.RestaurantRepository;
import com.utown.utownbackend.repository.RiderProfileRepository;
import com.utown.utownbackend.seeder.DemoDataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "demo"})
@Testcontainers(disabledWithoutDocker = true)
class DemoDataSeederIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

    @Autowired
    private DemoDataSeeder seeder;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RiderProfileRepository riderProfileRepository;

    @Test
    void seederPopulatesDemoCatalogAndIsIdempotent() {
        // Already ran once on startup under the demo profile; a second run must not duplicate anything.
        long restaurants = restaurantRepository.count();
        long dishes = dishRepository.count();
        long orders = orderRepository.count();

        seeder.run(new DefaultApplicationArguments());

        assertThat(restaurants).isGreaterThanOrEqualTo(7);
        assertThat(dishes).isGreaterThanOrEqualTo(30);
        assertThat(riderProfileRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(restaurantRepository.count()).isEqualTo(restaurants);
        assertThat(dishRepository.count()).isEqualTo(dishes);
        assertThat(orderRepository.count()).isEqualTo(orders);

        Restaurant pizza = restaurantRepository.findAll().stream()
                .filter(r -> r.getName().equals("Demo Pizza Place"))
                .findFirst()
                .orElseThrow();
        assertThat(pizza.getDescription()).doesNotStartWith("Best demo ");
        assertThat(pizza.getAverageRating()).isPositive();
    }
}
