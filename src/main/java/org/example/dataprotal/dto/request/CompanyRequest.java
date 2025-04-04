package org.example.dataprotal.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyRequest {
    String companyName;
    String companyRegisterNumber;
    int createYear;
    String address;
    String cityAndRegion;
    String website;
    String contactName;
    String contactEmail;
    String contactPhone;
}
