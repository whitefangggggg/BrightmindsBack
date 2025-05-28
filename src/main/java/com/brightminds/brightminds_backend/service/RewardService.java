package com.brightminds.brightminds_backend.service;

import com.brightminds.brightminds_backend.model.*;
import com.brightminds.brightminds_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private ClassroomScoreRepository classroomScoreRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Transactional
    public void awardTopPerformersBadges(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classroomId));

        List<ClassroomScore> topScores = classroomScoreRepository.findByClassroomOrderByTotalScoreDesc(classroom);

        if (topScores.size() >= 3) {
            // Award badges to top 3 performers
            awardBadge(topScores.get(0), classroom, 1);
            awardBadge(topScores.get(1), classroom, 2);
            awardBadge(topScores.get(2), classroom, 3);
        } else if (topScores.size() == 2) {
            // Award badges to top 2 performers
            awardBadge(topScores.get(0), classroom, 1);
            awardBadge(topScores.get(1), classroom, 2);
        } else if (topScores.size() == 1) {
            // Award badge to the only performer
            awardBadge(topScores.get(0), classroom, 1);
        }
    }

    private void awardBadge(ClassroomScore score, Classroom classroom, int position) {
        Student student = score.getStudent();
        String studentName = student.getFirstName() + " " + student.getLastName();
        String positionText = getPositionText(position);
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        String badgeMessage = String.format(
            "This Badge is awarded to %s for placing %s on %s in the classroom %s",
            studentName,
            positionText,
            formattedDate,
            classroom.getName()
        );

        Reward badge = new Reward();
        badge.setStudent(student);
        badge.setBadgeName(positionText + " Place Badge");
        badge.setEarnedFor(badgeMessage);
        rewardRepository.save(badge);
    }

    private String getPositionText(int position) {
        switch (position) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return position + "th";
        }
    }
} 