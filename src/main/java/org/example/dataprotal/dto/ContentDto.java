package org.example.dataprotal.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.model.page.SubContent;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentDto {
    String pageName;
    String title;
    String topic;
    List<SubContent> subContents;
}
