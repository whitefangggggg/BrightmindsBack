package com.brightminds.brightminds_backend.model;

public enum GameMode {
    BALLOON,       // Pop balloons with correct answers, bonus for 3 in a row
    TREASURE_HUNT,  // Unlock treasure chests, bonus for completing all questions correctly
    MATCHING,       // Match pairs of related items (MatchingGamePage)
    IMAGE_MULTIPLE_CHOICE, // Select the correct image (ImageMultipleChoiceGame)
    SORTING         // Sort items into categories (LikasYamanGame)
}