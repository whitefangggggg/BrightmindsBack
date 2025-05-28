package com.brightminds.brightminds_backend.dto;

import com.brightminds.brightminds_backend.model.GameMode;
import java.time.LocalDateTime;

public class AssignedGameResponseDto {
    private Long id; // ClassroomGame ID
    private ClassroomInfo classroom;
    private GameInfo game;
    private LocalDateTime deadline;
    private boolean isPremadeAssignment; // ClassroomGame.isPremade
    private String status;
    private Integer maxAttempts;

    public static class ClassroomInfo {
        private Long id;
        private String name;

        public ClassroomInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class GameInfo {
        private Long activityId;
        private String activityName;
        private String description;
        private String subject;
        private GameMode gameMode;
        private boolean isGamePremade; // Game.isPremade

        public GameInfo(Long activityId, String activityName, String description, String subject, GameMode gameMode, boolean isGamePremade) {
            this.activityId = activityId;
            this.activityName = activityName;
            this.description = description;
            this.subject = subject;
            this.gameMode = gameMode;
            this.isGamePremade = isGamePremade;
        }
        public Long getActivityId() { return activityId; }
        public void setActivityId(Long activityId) { this.activityId = activityId; }
        public String getActivityName() { return activityName; }
        public void setActivityName(String activityName) { this.activityName = activityName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public GameMode getGameMode() { return gameMode; }
        public void setGameMode(GameMode gameMode) { this.gameMode = gameMode; }
        public boolean isGamePremade() { return isGamePremade; } // Corrected getter name
        public void setGamePremade(boolean premade) { isGamePremade = premade; } // Corrected setter name
    }

    public static AssignedGameResponseDto from(com.brightminds.brightminds_backend.model.ClassroomGame cg) {
        AssignedGameResponseDto dto = new AssignedGameResponseDto();
        dto.setId(cg.getId());
        dto.setDeadline(cg.getDeadline());
        dto.setPremadeAssignment(cg.isPremade());
        dto.setMaxAttempts(cg.getMaxAttempts());
        // Determine status based on deadline, actual attempts vs maxAttempts, etc.
        // For now, a simple placeholder:
        if (cg.getDeadline() != null && LocalDateTime.now().isAfter(cg.getDeadline())) {
            dto.setStatus("OVERDUE"); // Example status logic
        } else {
            dto.setStatus("PENDING");
        }


        if (cg.getClassroom() != null) {
            dto.setClassroom(new ClassroomInfo(cg.getClassroom().getId(), cg.getClassroom().getName()));
        }

        if (cg.getGame() != null) {
            com.brightminds.brightminds_backend.model.Game gameEntity = cg.getGame();
            dto.setGame(new GameInfo(
                    gameEntity.getActivityId(),
                    gameEntity.getActivityName(),
                    gameEntity.getDescription(),
                    gameEntity.getSubject(),
                    gameEntity.getGameMode(),
                    gameEntity.isPremade()
            ));
        }
        return dto;
    }

    // Getters and Setters for AssignedGameResponseDto
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClassroomInfo getClassroom() { return classroom; }
    public void setClassroom(ClassroomInfo classroom) { this.classroom = classroom; }
    public GameInfo getGame() { return game; }
    public void setGame(GameInfo game) { this.game = game; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public boolean isPremadeAssignment() { return isPremadeAssignment; }
    public void setPremadeAssignment(boolean premadeAssignment) { this.isPremadeAssignment = premadeAssignment; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
}