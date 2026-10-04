package com.utown.utownbackend.seeder;

import com.utown.utownbackend.entity.*;
import com.utown.utownbackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Profile("demo")
@RequiredArgsConstructor
public class DemoDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RiderProfileRepository riderProfileRepository;
    private final CityRepository cityRepository;
    private final DeliveryAreaRepository deliveryAreaRepository;
    private final RestaurantTypeRepository restaurantTypeRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantWorkingHoursRepository workingHoursRepository;
    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final RatingRepository ratingRepository;
    private final DishOptionGroupRepository dishOptionGroupRepository;
    private final DishOptionRepository dishOptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting demo data seeder...");

        if (userRepository.findByEmailAndDeletedAtIsNull("admin@demo.com").isPresent()) {
            log.info("Demo data already seeded. Skipping.");
            return;
        }

        log.info("Seeding users...");
        User admin = createUser("Admin", "User", "admin@demo.com", UserRole.ADMIN, "01011111111");
        User owner = createUser("Restaurant", "Owner", "owner@demo.com", UserRole.RESTAURANT_OWNER, "01022222222");
        User customer = createUser("Hungry", "Customer", "customer@demo.com", UserRole.CUSTOMER, "01033333333");
        User rider = createUser("Speedy", "Rider", "rider@demo.com", UserRole.RIDER, "01044444444");

        RiderProfile riderProfile = new RiderProfile();
        riderProfile.setUser(rider);
        riderProfile.setTransportType(TransportType.MOTORCYCLE);
        riderProfile.setAvailability(true);
        riderProfile.setStatus(RiderStatus.ACTIVE);
        riderProfileRepository.save(riderProfile);

        log.info("Seeding geography...");
        City seoul = new City();
        seoul.setName("Seoul");
        seoul = cityRepository.save(seoul);

        DeliveryArea gangnam = new DeliveryArea();
        gangnam.setCity(seoul);
        gangnam.setName("Gangnam");
        gangnam = deliveryAreaRepository.save(gangnam);

        log.info("Seeding restaurant data...");
        RestaurantType typePizza = new RestaurantType();
        typePizza.setName("Pizza");
        typePizza = restaurantTypeRepository.save(typePizza);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Demo Pizza Place");
        restaurant.setOwner(owner);
        restaurant.setType(typePizza);
        restaurant.setCity(seoul);
        restaurant.setAddress("123 Pizza Street");
        restaurant.setDescription("Best demo pizza in town!");
        restaurant.setPhone("02-123-4567");
        restaurant.setLatitude(new BigDecimal("37.4979"));
        restaurant.setLongitude(new BigDecimal("127.0276"));
        restaurant.setMinimumOrderAmount(new BigDecimal("15000"));
        restaurant.setStatus(RestaurantStatus.OPEN);
        restaurant = restaurantRepository.save(restaurant);

        // Working hours for every day
        for (DayOfWeek day : DayOfWeek.values()) {
            RestaurantWorkingHours hours = new RestaurantWorkingHours();
            hours.setRestaurant(restaurant);
            hours.setDayOfWeek(day);
            hours.setOpenTime(LocalTime.of(10, 0));
            hours.setCloseTime(LocalTime.of(23, 0));
            hours.setDayOff(false);
            workingHoursRepository.save(hours);
        }

        Category category = new Category();
        category.setRestaurant(restaurant);
        category.setName("Main Course");
        category.setPriority(1);
        category = categoryRepository.save(category);

        Dish dish = new Dish();
        dish.setCategory(category);
        dish.setName("Pepperoni Pizza");
        dish.setDescription("Classic pepperoni");
        dish.setPrice(new BigDecimal("18000"));
        dish.setImageUrl("https://example.com/pizza.jpg");
        dish.setRestaurant(restaurant);
        dish.setStatus(DishStatus.AVAILABLE);
        dish = dishRepository.save(dish);

        DishOptionGroup optionGroup = new DishOptionGroup();
        optionGroup.setDish(dish);
        optionGroup.setName("Size");
        optionGroup.setRequired(true);
        optionGroup = dishOptionGroupRepository.save(optionGroup);

        DishOption option = new DishOption();
        option.setOptionGroup(optionGroup);
        option.setName("Large");
        option.setAdditionalPrice(new BigDecimal("3000"));
        option.setStatus(DishOptionStatus.AVAILABLE);
        dishOptionRepository.save(option);

        log.info("Seeding historical orders and ratings...");
        Address address = new Address();
        address.setUser(customer);
        address.setCity(seoul);
        address.setDeliveryArea(gangnam);
        address.setAddressLine("Customer Home 101");
        address.setRecipientName("Hungry Customer");
        address.setPhone("01033333333");
        address = addressRepository.save(address);

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setUser(customer);
        order.setRestaurant(restaurant);
        order.setAddress(address);
        order.setStatus(OrderStatus.DELIVERED);
        order.setSubtotal(new BigDecimal("18000"));
        order.setDeliveryFee(new BigDecimal("3000"));
        order.setTotalAmount(new BigDecimal("21000"));
        order.setCurrency("KRW");
        order.setPaymentMethod(PaymentMethod.CARD);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setCreatedAt(LocalDateTime.now().minusDays(2));
        order = orderRepository.save(order);

        Rating rating = new Rating();
        rating.setOrder(order);
        rating.setUser(customer);
        rating.setRestaurant(restaurant);
        rating.setScore(5);
        rating.setComment("Amazing pizza!");
        ratingRepository.save(rating);

        log.info("Demo data seeding completed!");
    }

    private User createUser(String firstName, String lastName, String email, UserRole role, String phone) {
        User user = new User();
        user.setName(firstName + " " + lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123")); // Documented password
        user.setRole(role);
        user.setPhone(phone);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
