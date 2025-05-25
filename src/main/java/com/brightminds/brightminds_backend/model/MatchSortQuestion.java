package com.brightminds.brightminds_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "match_sort_questions")
@Data
public class MatchSortQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question prompt cannot be blank")
    @Column(nullable = false)
    private String prompt; // e.g., "Match the pairs" or "Sort the steps"

    private String image; // URL or path to question image (optional)

    @ElementCollection
    @CollectionTable(name = "match_sort_pairs", joinColumns = @JoinColumn(name = "question_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "left", column = @Column(name = "pair_left")),
        @AttributeOverride(name = "right", column = @Column(name = "pair_right"))
    })
    private List<Pair> pairs; // For matching: list of pairs; for sorting: list with correct order

    @NotBlank(message = "Type cannot be blank")
    @Column(nullable = false)
    private String type; // "MATCHING" or "SORTING"

    @Embeddable
    @Data
    public static class Pair {
        private String left;
        private String right;
    }
}