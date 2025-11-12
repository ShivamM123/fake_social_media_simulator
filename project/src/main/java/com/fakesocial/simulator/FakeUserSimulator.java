package com.fakesocial.simulator;

import com.fakesocial.dao.CommentDAO;
import com.fakesocial.dao.PostDAO;
import com.fakesocial.dao.UserDAO;
import com.fakesocial.model.Post;
import com.fakesocial.model.User;
import com.fakesocial.util.AIGenerator;
import com.fakesocial.util.AuthUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FakeUserSimulator {
    private static final List<String> FAKE_USERNAMES = Arrays.asList(
            "Alice123", "BobTheBuilder", "Charlie99", "DianaWonder", "EveOnline",
            "FrankSinatra", "GraceHopper", "HenryVIII", "IvyLeague", "JackSparrow"
    );
    
    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final Random random;
    private ScheduledExecutorService executorService;
    private List<User> fakeUsers;
    
    public FakeUserSimulator() {
        this.userDAO = new UserDAO();
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
        this.random = new Random();
        initializeFakeUsers();
    }
    
    private void initializeFakeUsers() {
        try {
            fakeUsers = userDAO.getFakeUsers();
            
            if (fakeUsers.isEmpty()) {
                System.out.println("Creating fake users...");
                for (String username : FAKE_USERNAMES) {
                    try {
                        String email = username.toLowerCase() + "@fake.com";
                        String password = AuthUtil.hashPassword("password123");
                        int userId = userDAO.createUser(username, email, password, true, false);
                        User user = userDAO.getUserById(userId);
                        if (user != null) {
                            fakeUsers.add(user);
                        }
                    } catch (SQLException e) {
                        System.err.println("Could not create user " + username + ": " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Found " + fakeUsers.size() + " existing fake users.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing fake users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void start() {
        if (executorService != null && !executorService.isShutdown()) {
            return;
        }
        
        executorService = Executors.newScheduledThreadPool(2);
        
        // Generate AI posts every 10-20 seconds
        executorService.scheduleAtFixedRate(() -> {
            try {
                createAIPost();
            } catch (Exception e) {
                System.err.println("Error creating AI post: " + e.getMessage());
            }
        }, 10, 10 + random.nextInt(10), TimeUnit.SECONDS);
        
        // Generate AI comments every 15-25 seconds
        executorService.scheduleAtFixedRate(() -> {
            try {
                createAIComment();
            } catch (Exception e) {
                System.err.println("Error creating AI comment: " + e.getMessage());
            }
        }, 15, 15 + random.nextInt(10), TimeUnit.SECONDS);
        
        System.out.println("AI Post & Comment Simulator started!");
    }
    
    public void stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            System.out.println("AI Simulator stopped!");
        }
    }
    
    private void createAIPost() {
        if (fakeUsers == null || fakeUsers.isEmpty()) {
            return;
        }
        
        try {
            User randomUser = fakeUsers.get(random.nextInt(fakeUsers.size()));
            String aiPost = AIGenerator.generatePost();
            
            postDAO.createPost(randomUser.getId(), aiPost, true);
            System.out.println("AI Post by " + randomUser.getUsername() + ": " + aiPost);
        } catch (SQLException e) {
            System.err.println("Error creating AI post: " + e.getMessage());
        }
    }
    
    private void createAIComment() {
        try {
            List<Post> posts = postDAO.getAllPosts();
            if (posts.isEmpty() || fakeUsers == null || fakeUsers.isEmpty()) {
                return;
            }
            
            // Randomly comment on a post
            if (random.nextDouble() < 0.3) { // 30% chance to comment
                Post randomPost = posts.get(random.nextInt(posts.size()));
                User randomUser = fakeUsers.get(random.nextInt(fakeUsers.size()));
                String aiComment = AIGenerator.generateComment();
                
                commentDAO.createComment(randomPost.getId(), randomUser.getId(), aiComment, true);
                System.out.println("AI Comment by " + randomUser.getUsername() + " on post " + randomPost.getId() + ": " + aiComment);
            }
        } catch (SQLException e) {
            System.err.println("Error creating AI comment: " + e.getMessage());
        }
    }
    
    public List<User> getFakeUsers() {
        return fakeUsers;
    }
}
