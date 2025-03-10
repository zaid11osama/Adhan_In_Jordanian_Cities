package com.arabbank.hdf.uam.brain.mail;

import com.arabbank.hdf.uam.brain.utils.JpaConverterJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "UAM_CONFIG_EMAIL_ITEM")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailItem {
    @Id
    private int id;
    private String itemCode;
    private String itemName;
    private String itemDesc;
    @Convert(converter = JpaConverterJson.class)
    private List<String> toAddresses;
    @Convert(converter = JpaConverterJson.class)
    private List<String> ccAddresses;
    private boolean enabled;
}
