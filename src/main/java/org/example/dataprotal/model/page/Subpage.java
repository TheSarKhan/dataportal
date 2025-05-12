package org.example.dataprotal.model.page;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subpage {
    String name;
    String pageLink;
}
