package com.test.db.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseOperationDto {

    private long totalCount;
    private long insertCount;
    private double insertTime;
    private long selectCount;
    private double selectTime;
}
