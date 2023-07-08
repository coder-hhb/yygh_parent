package com.hhb.yygh.hosp.bean;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Actor")
public class Actor {
    private String id;
    private String actorName;
    private boolean gender;
    private Date brith;
}
