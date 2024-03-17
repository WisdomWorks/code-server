package com.example.code_server.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Submission {
    String submissionId;

    String problemId;

    String language;

    String source;

    String judgeId;

    int priority;
}
