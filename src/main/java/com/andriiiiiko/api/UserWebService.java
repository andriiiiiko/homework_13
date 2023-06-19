package com.andriiiiiko.api;

import com.andriiiiiko.models.Comment;
import com.andriiiiiko.models.Post;
import com.andriiiiiko.models.Task;
import com.andriiiiiko.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserWebService {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final Gson GSON = new Gson();
    private static final String RELATIVE_PATH = "src/main/resources/";

    public static User createUser(User user) {
        try {
            String jsonBody = GSON.toJson(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return GSON.fromJson(response.body(), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User updateUser(User user, int id) {
        try {
            String jsonBody = GSON.toJson(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return GSON.fromJson(response.body(), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteUser(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List getAllUsers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users"))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return GSON.fromJson(response.body(), List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Optional<User> getUserById(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.ofNullable(GSON.fromJson(response.body(), User.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<List<User>> getUserByUsername(String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users?username=" + username))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            Type userListType = new TypeToken<List<User>>() {
            }.getType();
            return Optional.ofNullable(GSON.fromJson(response.body(), userListType));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static List<Comment> getUserCommentsFromLastPost(int userId) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + userId + "/posts"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Post> posts = GSON.fromJson(response.body(), new TypeToken<List<Post>>() {}.getType());

            if (!posts.isEmpty()) {
                int lastPostId = posts.get(posts.size() - 1).getId();
                request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/posts/" + lastPostId + "/comments"))
                        .GET()
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Type commentListType = new TypeToken<List<Comment>>() {}.getType();
                return GSON.fromJson(response.body(), commentListType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printUserPostComments(int userId) {
        List<Comment> comments = UserWebService.getUserCommentsFromLastPost(userId);
        if (comments != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(comments);

            String fileName = "user-" + userId + "-post-" + comments.get(0).getPostId() + "-comments.json";
            try (FileWriter fileWriter = new FileWriter(RELATIVE_PATH + fileName)) {
                fileWriter.write(json);
                System.out.println("Comments saved to " + RELATIVE_PATH + fileName);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Failed to retrieve comments for user " + userId);
        }
    }

    public static List<Task> getOpenTasksForUser(int userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + userId + "/todos?completed=false"))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                String body = response.body();
                Task[] tasks = GSON.fromJson(body, Task[].class);
                return Arrays.asList(tasks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
