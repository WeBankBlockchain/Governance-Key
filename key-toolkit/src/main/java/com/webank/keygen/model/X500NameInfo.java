package com.webank.keygen.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class X500NameInfo {

    private String commonName;

    private String localityName;

    private String stateOrProvinceName;

    private String organizationName;

    private String organizationalUnitName;

    private String countryName;

    private String streetAddress;

    private String emailAddress;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (commonName != null) {
            builder.append("CN=").append(commonName);
        }
        if (organizationName != null) {
            builder.append(",O=").append(organizationName);
        }
        if (organizationalUnitName != null) {
            builder.append(",OU=").append(organizationalUnitName);
        }
        if (localityName != null) {
            builder.append(",L=").append(localityName);
        }
        if (stateOrProvinceName != null) {
            builder.append(",ST=").append(stateOrProvinceName);
        }
        if (countryName != null) {
            builder.append(",C=").append(countryName);
        }
        if (streetAddress != null) {
            builder.append(",STREET=").append(streetAddress);
        }
        if (streetAddress != null) {
            builder.append(",emailAddress=").append(emailAddress);
        }
        return builder.toString();
    }


}
