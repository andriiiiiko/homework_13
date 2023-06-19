package com.andriiiiiko.api;

import com.andriiiiiko.models.*;

import java.util.List;
import java.util.Optional;

import static com.andriiiiiko.api.UserWebService.getOpenTasksForUser;
import static com.andriiiiiko.api.UserWebService.printUserPostComments;

public class Application {
    public static void main(String[] args) {
        // Task №1
        // 1. Create a new user
        User user = createDefaultUser();
        User createdUser = UserWebService.createUser(user);
        printUser(createdUser);

        // 2. Update the user
        user.setName("John Doe");
        user.setUsername("john.doe");
        User updatedUser = UserWebService.updateUser(user, 1);
        printUser(updatedUser);

        // 3. Delete the user
        boolean isDeleted = UserWebService.deleteUser(1);
        System.out.println("isDeleted: " + isDeleted);

        // 4. Get all users
        List<User> allUsers = UserWebService.getAllUsers();
        System.out.println("All users: " + allUsers);

        // 5. Get user by id
        Optional<User> userById = UserWebService.getUserById(1);
        System.out.println("User by id: " + userById);

        // 6. Get user by username
        Optional<List<User>> userByUsername = UserWebService.getUserByUsername("Kamren");
        printUser(userByUsername.get().get(0));


        // Task №2
        printUserPostComments(1);

        // Task №3
        printOpenTasksForUser(2);
    }

    private static void printUser(User user) {
        System.out.println("id: " + user.getId());
        System.out.println("name: " + user.getName());
        System.out.println("username: " + user.getUsername());
        System.out.println("email: " + user.getEmail());
        System.out.println("address: " + user.getAddress());
        System.out.println("phone: " + user.getPhone());
        System.out.println("website: " + user.getWebsite());
        System.out.println("company: " + user.getCompany());
    }

    private static User createDefaultUser() {
        User user = new User();
        user.setId(11);
        user.setName("Ervin Howell");
        user.setUsername("Antonette");
        user.setEmail("Shanna@melissa.tv");

        Address address = new Address();
        address.setStreet("Victor Plains");
        address.setSuite("Suite 879");
        address.setCity("Wisokyburgh");
        address.setZipcode("90566-7771");

        Geo geo = new Geo();
        geo.setLat("-43.9509");
        geo.setLng("-34.4618");

        address.setGeo(geo);
        user.setAddress(address);

        user.setPhone("010-692-6593 x09125");
        user.setWebsite("anastasia.net");

        Company company = new Company();
        company.setName("Deckow-Crist");
        company.setCatchPhrase("Proactive didactic contingency");
        company.setBs("synergize scalable supply-chains");

        user.setCompany(company);

        return user;
    }

    private static void printOpenTasksForUser(int userId) {
        List<Task> tasks = getOpenTasksForUser(userId);

        if (tasks != null) {
            System.out.println("Open tasks for user with id " + userId + ":");
            for (Task task : tasks) {
                System.out.println(task.getId() + " " + task.getTitle());
            }
        } else {
            System.out.println("No open tasks for user with id " + userId);
        }
    }
}
