package com.ldb.lms.dto.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleDto {
    private Integer scheduleId;
    private String scheduleTitle;
    private String scheduleDescription;
    private Date scheduleStartDate; 
    private Date scheduleEndDate;  
    private String semesterType;    

    public String getScheduleStartDateFormatted() {
        if (this.scheduleStartDate == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(this.scheduleStartDate);
    }

    public String getScheduleEndDateFormatted() { 
        if (this.scheduleEndDate == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(this.scheduleEndDate);
    }
}