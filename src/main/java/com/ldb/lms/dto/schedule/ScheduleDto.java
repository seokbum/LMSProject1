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
    private Date scheduleDate;

    public String getScheduleDateFormatted() {
        if (this.scheduleDate == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(this.scheduleDate);
    }
}