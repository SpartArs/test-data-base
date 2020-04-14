package com.test.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Historical {

    private long laboratoryId;
    private long sampleId;
    private long stepId;
    private Date stepBegin;
    private Date stepEnd;

    @Override
    public String toString() {
        return "Historical{" +
                "laboratoryId=" + laboratoryId +
                ", sampleId=" + sampleId +
                ", stepId=" + stepId +
                ", stepBegin=" + stepBegin +
                ", stepEnd=" + stepEnd +
                '}';
    }

}