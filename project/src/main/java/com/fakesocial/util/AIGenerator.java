package com.fakesocial.util;

import java.util.Random;

public class AIGenerator {
    private static final Random random = new Random();
    
    private static final String[] POST_TEMPLATES = {
        "Just had an amazing experience today! 🌟",
        "Life is full of surprises, and today was no exception!",
        "Feeling grateful for all the little things in life. 💙",
        "Can't believe how fast time flies! It's already been a great week.",
        "Sometimes the best moments happen when you least expect them.",
        "Coffee and coding - the perfect combination! ☕",
        "Beautiful weather calls for a beautiful day! ☀️",
        "Learning something new every single day. Growth is amazing!",
        "Weekend vibes are here! Time to relax and enjoy. 🎉",
        "Technology never ceases to amaze me. The future is now!",
        "Random thought: Why do we call it 'building' when we're actually creating?",
        "The world is full of possibilities if you're willing to explore.",
        "Sunset views always remind me of how beautiful life can be. 🌅",
        "Productivity mode: ON! Let's get things done today! 💪",
        "Sometimes a simple walk can clear your mind completely.",
        "New ideas are popping up everywhere! Innovation is exciting!",
        "Food for thought: What if we all tried to be a little kinder today?",
        "Music is the soundtrack of our lives. What's playing for you? 🎵",
        "Nature has a way of putting everything in perspective.",
        "Grateful for friends, family, and all the support around me."
    };
    
    private static final String[] COMMENT_TEMPLATES = {
        "Totally agree with this!",
        "This is so inspiring! ✨",
        "Thanks for sharing!",
        "Love this! ❤️",
        "Couldn't have said it better!",
        "This made my day!",
        "So true!",
        "Exactly what I needed to hear!",
        "Amazing perspective!",
        "Well said!",
        "This resonates with me!",
        "Great point!",
        "I feel the same way!",
        "Thanks for this!",
        "Beautiful thoughts!",
        "This is awesome!",
        "Perfect timing for this post!",
        "I needed to see this today!",
        "So relatable!",
        "Keep sharing these thoughts!"
    };
    
    public static String generatePost() {
        return POST_TEMPLATES[random.nextInt(POST_TEMPLATES.length)];
    }
    
    public static String generateComment() {
        return COMMENT_TEMPLATES[random.nextInt(COMMENT_TEMPLATES.length)];
    }
}