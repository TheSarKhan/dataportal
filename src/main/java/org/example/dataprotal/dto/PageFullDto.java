package org.example.dataprotal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.enums.PageType;
import org.example.dataprotal.model.page.Subpage;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageFullDto {
    String name;
    List<Subpage> subpageList;
    Boolean mainPage;
    PageType pageType;
}
