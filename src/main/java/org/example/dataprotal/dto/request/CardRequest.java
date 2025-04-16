package org.example.dataprotal.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRequest {

    String title;

    String description;

    String topic;

    String subTopic;

}
