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
import java.util.UUID;
import java.util.Optional;
import java.util.List;

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
    private final OrderItemRepository orderItemRepository;
    private final RestaurantDeliveryAreaRepository restaurantDeliveryAreaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting demo data seeder...");

        log.info("Seeding users...");
        User admin = findOrCreateUser("Admin", "User", "admin@demo.com", UserRole.ADMIN, "01011111111");
        User owner1 = findOrCreateUser("Restaurant", "Owner 1", "owner1@demo.com", UserRole.RESTAURANT_OWNER, "01022222222");
        User owner2 = findOrCreateUser("Restaurant", "Owner 2", "owner2@demo.com", UserRole.RESTAURANT_OWNER, "01022222223");
        User customer = findOrCreateUser("Hungry", "Customer", "customer@demo.com", UserRole.CUSTOMER, "01033333333");
        User rider = findOrCreateUser("Speedy", "Rider", "rider@demo.com", UserRole.RIDER, "01044444444");
        User rider2 = findOrCreateUser("Jisoo", "Han", "rider2@demo.com", UserRole.RIDER, "01044444445");
        User rider3 = findOrCreateUser("Marco", "Silva", "rider3@demo.com", UserRole.RIDER, "01044444446");
        User reviewer1 = findOrCreateUser("Minji", "Park", "minji@demo.com", UserRole.CUSTOMER, "01055555551");
        User reviewer2 = findOrCreateUser("Daniel", "Cho", "daniel@demo.com", UserRole.CUSTOMER, "01055555552");

        findOrCreateRiderProfile(rider, TransportType.MOTORCYCLE);
        findOrCreateRiderProfile(rider2, TransportType.BICYCLE);
        findOrCreateRiderProfile(rider3, TransportType.CAR);

        log.info("Seeding geography...");
        City seoul = findOrCreateCity("Seoul");
        DeliveryArea gangnam = findOrCreateArea(seoul, "Gangnam");
        List<DeliveryArea> areas = List.of(
                gangnam,
                findOrCreateArea(seoul, "Hongdae"),
                findOrCreateArea(seoul, "Itaewon"),
                findOrCreateArea(seoul, "Jamsil"),
                findOrCreateArea(seoul, "Seongsu")
        );

        log.info("Seeding restaurant data...");
        RestaurantType typePizza = findOrCreateRestaurantType("Pizza");
        RestaurantType typeBurger = findOrCreateRestaurantType("Burger");
        RestaurantType typeKorean = findOrCreateRestaurantType("Korean");
        RestaurantType typeChicken = findOrCreateRestaurantType("Chicken");
        RestaurantType typeNoodles = findOrCreateRestaurantType("Noodles");
        RestaurantType typeJapanese = findOrCreateRestaurantType("Japanese");
        RestaurantType typeCafe = findOrCreateRestaurantType("Café & Dessert");

        Restaurant rest1 = findOrCreateRestaurant("Demo Pizza Place", owner1, typePizza, seoul, "123 Pizza Street", "02-123-4567", "37.4979", "127.0276", "15000",
                "Wood-fired Neapolitan pies with a blistered crust, San Marzano tomatoes and fior di latte.", 30);
        Restaurant rest2 = findOrCreateRestaurant("Demo Burger House", owner2, typeBurger, seoul, "456 Burger Ave", "02-987-6543", "37.5110", "127.0590", "12000",
                "Smashed double patties, potato buns and hand-cut fries. Diner classics done right.", 25);
        Restaurant seoulBowl = findOrCreateRestaurant("Seoul Bowl Co.", owner1, typeKorean, seoul, "18 Gangnam-daero 94-gil", "02-555-0101", "37.4991", "127.0300", "13000",
                "Modern Korean rice bowls: sizzling dolsot bibimbap, soy-glazed bulgogi and house kimchi.", 25);
        Restaurant chicken = findOrCreateRestaurant("Gangnam Fried Chicken", owner2, typeChicken, seoul, "7 Teheran-ro 4-gil", "02-555-0202", "37.4986", "127.0287", "18000",
                "Twice-fried, glass-crisp Korean chicken. Yangnyeom, soy garlic or straight-up original.", 35);
        Restaurant noodle = findOrCreateRestaurant("Hanok Noodle Bar", owner1, typeNoodles, seoul, "32 Insadong-gil", "02-555-0303", "37.5740", "126.9850", "11000",
                "Hand-pulled noodles in slow-simmered broths, from icy naengmyeon to rich jjajangmyeon.", 30);
        Restaurant sushi = findOrCreateRestaurant("Itaewon Sushi Club", owner2, typeJapanese, seoul, "211 Itaewon-ro", "02-555-0404", "37.5345", "126.9940", "20000",
                "Omakase-quality nigiri and inventive rolls, delivered chilled and beautifully packed.", 40);
        Restaurant cafe = findOrCreateRestaurant("Bean & Bloom Café", owner1, typeCafe, seoul, "9 Seongsu-iro 7-gil", "02-555-0505", "37.5446", "127.0557", "9000",
                "Seongsu's favourite bingsu, croffles and single-origin coffee.", 20);

        List<Restaurant> restaurants = List.of(rest1, rest2, seoulBowl, chicken, noodle, sushi, cafe);
        for (Restaurant r : restaurants) {
            for (DeliveryArea area : areas) {
                findOrCreateDeliveryAreaMapping(r, area);
            }
        }

        // Demo Pizza Place
        Category cat1 = findOrCreateCategory(rest1, "Main Course");
        Dish dish1 = findOrCreateDish(rest1, cat1, "Pepperoni Pizza", "18000", "https://example.com/pizza.jpg");
        DishOptionGroup optionGroup = findOrCreateOptionGroup(dish1, "Size");
        findOrCreateOption(optionGroup, "Large", "3000");
        Dish margherita = findOrCreateDish(rest1, cat1, "Margherita", "16000", "San Marzano tomato, fior di latte, basil and olive oil.");
        DishOptionGroup margheritaSize = findOrCreateOptionGroup(margherita, "Size", true, 1);
        findOrCreateOption(margheritaSize, "Regular 10\"", "0");
        findOrCreateOption(margheritaSize, "Large 14\"", "4000");
        DishOptionGroup margheritaExtras = findOrCreateOptionGroup(margherita, "Extra toppings", false, 3);
        findOrCreateOption(margheritaExtras, "Burrata", "4500");
        findOrCreateOption(margheritaExtras, "Prosciutto", "4000");
        findOrCreateOption(margheritaExtras, "Hot honey", "1500");
        findOrCreateDish(rest1, cat1, "Truffle Funghi", "21000", "Wild mushrooms, truffle cream, mozzarella and thyme.");
        Category pizzaSides = findOrCreateCategory(rest1, "Sides & Drinks", 2);
        findOrCreateDish(rest1, pizzaSides, "Garlic Knots", "6000", "Six buttery knots with parmesan and marinara dip.");
        findOrCreateDish(rest1, pizzaSides, "Caesar Salad", "9000", "Little gem, anchovy dressing, garlic croutons.");
        findOrCreateDish(rest1, pizzaSides, "Sparkling Lemonade", "4000", "House-made with Jeju lemons.");

        // Demo Burger House
        Category cat2 = findOrCreateCategory(rest2, "Burgers");
        Dish dish2 = findOrCreateDish(rest2, cat2, "Cheeseburger", "8000", "https://example.com/burger.jpg");
        Dish smash = findOrCreateDish(rest2, cat2, "Double Smash Burger", "12500", "Two smashed patties, American cheese, pickles and house sauce.");
        DishOptionGroup smashAddOns = findOrCreateOptionGroup(smash, "Add-ons", false, 3);
        findOrCreateOption(smashAddOns, "Bacon", "2000");
        findOrCreateOption(smashAddOns, "Extra patty", "3500");
        findOrCreateOption(smashAddOns, "Jalapeños", "800");
        findOrCreateDish(rest2, cat2, "Crispy Chicken Burger", "11000", "Buttermilk-fried thigh, slaw and spicy mayo.");
        Category burgerSides = findOrCreateCategory(rest2, "Sides & Shakes", 2);
        Dish fries = findOrCreateDish(rest2, burgerSides, "Hand-cut Fries", "4500", "Double-fried with sea salt.");
        DishOptionGroup friesSize = findOrCreateOptionGroup(fries, "Size", true, 1);
        findOrCreateOption(friesSize, "Regular", "0");
        findOrCreateOption(friesSize, "Large", "1500");
        findOrCreateDish(rest2, burgerSides, "Vanilla Milkshake", "6000", "Thick, cold and topped with whipped cream.");

        // Seoul Bowl Co.
        Category bowls = findOrCreateCategory(seoulBowl, "Rice Bowls");
        Dish bibimbap = findOrCreateDish(seoulBowl, bowls, "Dolsot Bibimbap", "12000", "Sizzling stone-pot rice, seasoned vegetables, gochujang and a fried egg.");
        DishOptionGroup spice = findOrCreateOptionGroup(bibimbap, "Spice level", true, 1);
        findOrCreateOption(spice, "Mild", "0");
        findOrCreateOption(spice, "Medium", "0");
        findOrCreateOption(spice, "Fire", "0");
        DishOptionGroup protein = findOrCreateOptionGroup(bibimbap, "Add protein", false, 1);
        findOrCreateOption(protein, "Bulgogi beef", "4000");
        findOrCreateOption(protein, "Spicy pork", "3500");
        findOrCreateOption(protein, "Tofu", "2500");
        findOrCreateDish(seoulBowl, bowls, "Bulgogi Rice Bowl", "13500", "Soy-pear marinated ribeye over rice with scallions and sesame.");
        findOrCreateDish(seoulBowl, bowls, "Kimchi Fried Rice", "10500", "Aged kimchi, pork belly and a runny egg.");
        Category koreanSides = findOrCreateCategory(seoulBowl, "Small Plates", 2);
        findOrCreateDish(seoulBowl, koreanSides, "Japchae", "9000", "Sweet potato glass noodles with vegetables and sesame oil.");
        findOrCreateDish(seoulBowl, koreanSides, "Haemul Pajeon", "14000", "Crispy seafood and scallion pancake.");

        // Gangnam Fried Chicken
        Category chickenMains = findOrCreateCategory(chicken, "Chicken");
        Dish original = findOrCreateDish(chicken, chickenMains, "Original Fried Chicken", "19000", "Twice-fried and seriously crunchy.");
        DishOptionGroup portion = findOrCreateOptionGroup(original, "Portion", true, 1);
        findOrCreateOption(portion, "Half", "0");
        findOrCreateOption(portion, "Whole", "8000");
        Dish yangnyeom = findOrCreateDish(chicken, chickenMains, "Yangnyeom Chicken", "21000", "Sticky sweet-spicy glaze with crushed peanuts.");
        DishOptionGroup yangnyeomPortion = findOrCreateOptionGroup(yangnyeom, "Portion", true, 1);
        findOrCreateOption(yangnyeomPortion, "Half", "0");
        findOrCreateOption(yangnyeomPortion, "Whole", "8000");
        findOrCreateDish(chicken, chickenMains, "Soy Garlic Wings", "17000", "Ten wings tossed in soy, garlic and honey.");
        Category chickenSides = findOrCreateCategory(chicken, "Sides", 2);
        findOrCreateDish(chicken, chickenSides, "Pickled Radish", "1500", "The classic chicken companion.");
        findOrCreateDish(chicken, chickenSides, "Cheese Balls", "5000", "Chewy, cheesy, dangerously snackable.");

        // Hanok Noodle Bar
        Category noodles = findOrCreateCategory(noodle, "Noodles");
        findOrCreateDish(noodle, noodles, "Mul Naengmyeon", "11000", "Icy buckwheat noodles in tangy beef broth.");
        findOrCreateDish(noodle, noodles, "Jjajangmyeon", "9500", "Hand-pulled noodles in black bean sauce with pork.");
        Dish kalguksu = findOrCreateDish(noodle, noodles, "Dak Kalguksu", "10000", "Knife-cut noodles in a slow chicken broth.");
        DishOptionGroup kalguksuExtras = findOrCreateOptionGroup(kalguksu, "Extras", false, 2);
        findOrCreateOption(kalguksuExtras, "Mandu (4 pcs)", "4000");
        findOrCreateOption(kalguksuExtras, "Extra noodles", "2000");
        findOrCreateDish(noodle, noodles, "Spicy Jjamppong", "11500", "Fiery seafood noodle soup.");

        // Itaewon Sushi Club
        Category sushiSets = findOrCreateCategory(sushi, "Sets");
        findOrCreateDish(sushi, sushiSets, "Chef's Nigiri Set", "32000", "Twelve pieces of the day's best fish.");
        findOrCreateDish(sushi, sushiSets, "Salmon Lover Set", "26000", "Salmon nigiri, aburi salmon and salmon avocado roll.");
        Category rolls = findOrCreateCategory(sushi, "Rolls", 2);
        findOrCreateDish(sushi, rolls, "Spicy Tuna Roll", "14000", "Tuna, chili mayo, cucumber and tempura flakes.");
        findOrCreateDish(sushi, rolls, "Dragon Roll", "18000", "Shrimp tempura topped with eel and avocado.");

        // Bean & Bloom Café
        Category desserts = findOrCreateCategory(cafe, "Desserts");
        Dish bingsu = findOrCreateDish(cafe, desserts, "Injeolmi Bingsu", "11000", "Milk snow, roasted soybean powder and chewy rice cake.");
        DishOptionGroup bingsuSize = findOrCreateOptionGroup(bingsu, "Size", true, 1);
        findOrCreateOption(bingsuSize, "For one", "0");
        findOrCreateOption(bingsuSize, "To share", "5000");
        findOrCreateDish(cafe, desserts, "Brown Sugar Croffle", "6500", "Croissant waffle, brown sugar and vanilla gelato.");
        Category coffee = findOrCreateCategory(cafe, "Coffee", 2);
        Dish latte = findOrCreateDish(cafe, coffee, "Iced Oat Latte", "5800", "Single-origin espresso over oat milk.");
        DishOptionGroup shots = findOrCreateOptionGroup(latte, "Extra shot", false, 1);
        findOrCreateOption(shots, "Add a shot", "700");
        findOrCreateDish(cafe, coffee, "Dalgona Coffee", "6000", "Whipped coffee cloud on cold milk.");

        log.info("Seeding historical orders and ratings...");
        Address address = findOrCreateAddress(customer, seoul, gangnam, "Customer Home 101");
        Address minjiHome = findOrCreateAddress(reviewer1, seoul, areas.get(1), "22 Wausan-ro, Apt 803");
        Address danielHome = findOrCreateAddress(reviewer2, seoul, areas.get(4), "14 Seongsu-iro, Unit 5");

        createOrderIfNotExists(customer, rest1, address, dish1, 5, "Amazing pizza!");
        createOrderIfNotExists(customer, rest2, address, dish2, 4, "Great burger.");
        createOrderIfNotExists(reviewer1, rest1, minjiHome, margherita, 5, "Best crust in Seoul, arrived piping hot.");
        createOrderIfNotExists(reviewer2, rest2, danielHome, smash, 5, "The smash burger is unreal.");
        createOrderIfNotExists(reviewer1, seoulBowl, minjiHome, bibimbap, 5, "Rice was still crackling in the stone pot!");
        createOrderIfNotExists(reviewer2, seoulBowl, danielHome, bibimbap, 4, "Generous portion, a little mild for me.");
        createOrderIfNotExists(reviewer1, chicken, minjiHome, yangnyeom, 5, "Crunchy even after delivery. Ordering again tonight.");
        createOrderIfNotExists(reviewer2, chicken, danielHome, original, 4, "Solid chicken, fast rider.");
        createOrderIfNotExists(reviewer1, noodle, minjiHome, kalguksu, 4, "Comforting broth, perfect on a rainy day.");
        createOrderIfNotExists(reviewer2, cafe, danielHome, bingsu, 5, "Still frozen when it arrived. Magic.");

        log.info("Demo data seeding completed! (admin id={})", admin.getId());
    }

    private void findOrCreateRiderProfile(User riderUser, TransportType transportType) {
        if (riderProfileRepository.findByUserId(riderUser.getId()).isEmpty()) {
            RiderProfile riderProfile = new RiderProfile();
            riderProfile.setUser(riderUser);
            riderProfile.setTransportType(transportType);
            riderProfile.setAvailability(true);
            riderProfile.setStatus(RiderStatus.ACTIVE);
            riderProfileRepository.save(riderProfile);
        }
    }

    private User findOrCreateUser(String firstName, String lastName, String email, UserRole role, String phone) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).orElseGet(() -> {
            User user = new User();
            user.setName(firstName + " " + lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(role);
            user.setPhone(com.utown.utownbackend.util.PhoneUtil.normalizePhone(phone));
            user.setStatus(UserStatus.ACTIVE);
            return userRepository.save(user);
        });
    }

    private City findOrCreateCity(String name) {
        return cityRepository.findAll().stream().filter(c -> c.getName().equals(name)).findFirst().orElseGet(() -> {
            City city = new City();
            city.setName(name);
            return cityRepository.save(city);
        });
    }

    private DeliveryArea findOrCreateArea(City city, String name) {
        return deliveryAreaRepository.findAll().stream().filter(a -> a.getName().equals(name) && a.getCity().getId().equals(city.getId())).findFirst().orElseGet(() -> {
            DeliveryArea area = new DeliveryArea();
            area.setCity(city);
            area.setName(name);
            return deliveryAreaRepository.save(area);
        });
    }

    private RestaurantType findOrCreateRestaurantType(String name) {
        return restaurantTypeRepository.findAll().stream().filter(t -> t.getName().equals(name)).findFirst().orElseGet(() -> {
            RestaurantType type = new RestaurantType();
            type.setName(name);
            return restaurantTypeRepository.save(type);
        });
    }

    private Restaurant findOrCreateRestaurant(String name, User owner, RestaurantType type, City city, String address, String phone, String lat, String lng, String minOrder,
                                              String description, int deliveryMinutes) {
        Restaurant restaurant = findOrCreateRestaurant(name, owner, type, city, address, phone, lat, lng, minOrder);
        boolean placeholder = restaurant.getDescription() == null || restaurant.getDescription().startsWith("Best demo ");
        if (placeholder) {
            restaurant.setDescription(description);
            restaurant.setDeliveryTimeMinutes(deliveryMinutes);
            restaurant = restaurantRepository.save(restaurant);
        }
        return restaurant;
    }

    private Restaurant findOrCreateRestaurant(String name, User owner, RestaurantType type, City city, String address, String phone, String lat, String lng, String minOrder) {
        Optional<Restaurant> optionalRest = restaurantRepository.findAll().stream().filter(r -> r.getName().equals(name)).findFirst();
        if (optionalRest.isPresent()) {
            return optionalRest.get();
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setOwner(owner);
        restaurant.setType(type);
        restaurant.setCity(city);
        restaurant.setAddress(address);
        restaurant.setDescription("Best demo " + type.getName().toLowerCase() + " in town!");
        restaurant.setPhone(phone);
        restaurant.setLatitude(new BigDecimal(lat));
        restaurant.setLongitude(new BigDecimal(lng));
        restaurant.setMinimumOrderAmount(new BigDecimal(minOrder));
        restaurant.setStatus(RestaurantStatus.OPEN);
        restaurant = restaurantRepository.save(restaurant);

        for (DayOfWeek day : DayOfWeek.values()) {
            RestaurantWorkingHours hours = new RestaurantWorkingHours();
            hours.setRestaurant(restaurant);
            hours.setDayOfWeek(day);
            hours.setOpenTime(LocalTime.of(10, 0));
            hours.setCloseTime(LocalTime.of(23, 0));
            hours.setDayOff(false);
            workingHoursRepository.save(hours);
        }
        return restaurant;
    }

    private Category findOrCreateCategory(Restaurant restaurant, String name) {
        return findOrCreateCategory(restaurant, name, 1);
    }

    private Category findOrCreateCategory(Restaurant restaurant, String name, int priority) {
        return categoryRepository.findAll().stream().filter(c -> c.getName().equals(name) && c.getRestaurant().getId().equals(restaurant.getId())).findFirst().orElseGet(() -> {
            Category category = new Category();
            category.setRestaurant(restaurant);
            category.setName(name);
            category.setPriority(priority);
            return categoryRepository.save(category);
        });
    }

    /** {@code imageOrDescription} is stored as the image URL when it is an http(s) link, otherwise as the description. */
    private Dish findOrCreateDish(Restaurant restaurant, Category category, String name, String price, String imageOrDescription) {
        String img = imageOrDescription;
        return dishRepository.findAll().stream().filter(d -> d.getCategory().getId().equals(category.getId())).filter(d -> d.getName().equals(name)).findFirst().orElseGet(() -> {
                    Dish dish = new Dish();
                    dish.setCategory(category);
                    dish.setName(name);
                    boolean isUrl = img != null && img.startsWith("http");
                    dish.setDescription(isUrl ? "Classic " + name : img);
                    dish.setPrice(new BigDecimal(price));
                    dish.setImageUrl(isUrl ? img : null);
                    dish.setRestaurant(restaurant);
                    dish.setStatus(DishStatus.AVAILABLE);
                    return dishRepository.save(dish);
                });
    }

    private DishOptionGroup findOrCreateOptionGroup(Dish dish, String name) {
        return findOrCreateOptionGroup(dish, name, true, null);
    }

    private DishOptionGroup findOrCreateOptionGroup(Dish dish, String name, boolean required, Integer maxSelections) {
        return dishOptionGroupRepository.findAll().stream().filter(g -> g.getDish().getId().equals(dish.getId())).filter(g -> g.getName().equals(name)).findFirst().orElseGet(() -> {
                    DishOptionGroup optionGroup = new DishOptionGroup();
                    optionGroup.setDish(dish);
                    optionGroup.setName(name);
                    optionGroup.setRequired(required);
                    optionGroup.setMinSelections(required ? 1 : 0);
                    optionGroup.setMaxSelections(maxSelections);
                    return dishOptionGroupRepository.save(optionGroup);
                });
    }

    private DishOption findOrCreateOption(DishOptionGroup group, String name, String price) {
        return dishOptionRepository.findAll().stream().filter(o -> o.getOptionGroup().getId().equals(group.getId())).filter(o -> o.getName().equals(name)).findFirst().orElseGet(() -> {
                    DishOption option = new DishOption();
                    option.setOptionGroup(group);
                    option.setName(name);
                    option.setAdditionalPrice(new BigDecimal(price));
                    option.setStatus(DishOptionStatus.AVAILABLE);
                    return dishOptionRepository.save(option);
                });
    }

    private Address findOrCreateAddress(User user, City city, DeliveryArea area, String line) {
        return addressRepository.findAll().stream().filter(a -> a.getUser().getId().equals(user.getId())).filter(a -> a.getAddressLine().equals(line)).findFirst().orElseGet(() -> {
                    Address address = new Address();
                    address.setUser(user);
                    address.setCity(city);
                    address.setDeliveryArea(area);
                    address.setAddressLine(line);
                    address.setRecipientName(user.getName());
                    address.setPhone(user.getPhone());
                    return addressRepository.save(address);
                });
    }

    private void createOrderIfNotExists(User user, Restaurant restaurant, Address address, Dish dish, int ratingScore, String comment) {
        List<Order> userOrders = orderRepository.findAll().stream().filter(o -> o.getUser().getId().equals(user.getId())).toList();
        if (userOrders.stream().anyMatch(o -> o.getRestaurant().getId().equals(restaurant.getId()))) {
            return; // Order already exists for this restaurant
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setAddress(address);
        order.setStatus(OrderStatus.DELIVERED);
        order.setSubtotal(dish.getPrice());
        order.setDeliveryFee(new BigDecimal("3000"));
        order.setTotalAmount(dish.getPrice().add(new BigDecimal("3000")));
        order.setCurrency("KRW");
        order.setPaymentMethod(PaymentMethod.CARD);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setCreatedAt(LocalDateTime.now().minusDays(2));
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setDish(dish);
        orderItem.setDishName(dish.getName());
        orderItem.setUnitPrice(dish.getPrice());
        orderItem.setQuantity(1);
        orderItem.setSubtotal(dish.getPrice());
        orderItemRepository.save(orderItem);

        Rating rating = new Rating();
        rating.setOrder(order);
        rating.setUser(user);
        rating.setRestaurant(restaurant);
        rating.setScore(ratingScore);
        rating.setComment(comment);
        ratingRepository.save(rating);

        Double avg = ratingRepository.getAverageScoreByRestaurantId(restaurant.getId());
        if (avg != null) {
            restaurant.setAverageRating(BigDecimal.valueOf(avg).setScale(2, java.math.RoundingMode.HALF_UP));
            restaurantRepository.save(restaurant);
        }
    }

    private void findOrCreateDeliveryAreaMapping(Restaurant restaurant, DeliveryArea area) {
        if (!restaurantDeliveryAreaRepository.existsByRestaurantIdAndDeliveryAreaIdAndDeletedAtIsNull(restaurant.getId(), area.getId())) {
            RestaurantDeliveryArea rda = new RestaurantDeliveryArea();
            rda.setRestaurant(restaurant);
            rda.setDeliveryArea(area);
            restaurantDeliveryAreaRepository.save(rda);
        }
    }
}
