package org.example.dataprotal.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicRequest {
    String subTopic;
    String topic;
}
