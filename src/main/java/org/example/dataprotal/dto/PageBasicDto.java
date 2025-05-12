package org.example.dataprotal.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.enums.PageType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageBasicDto {
    String name;
    Boolean mainPage;
    PageType pageType;
}
