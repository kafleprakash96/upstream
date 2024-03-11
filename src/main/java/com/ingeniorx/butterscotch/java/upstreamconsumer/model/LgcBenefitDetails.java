package com.ingeniorx.butterscotch.java.upstreamconsumer.model;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity
@Table(name="lgc_benefit_Details")
public class LgcBenefitDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="benefit_details",columnDefinition = "CLOB")
    @Lob
    private String benefitDetails;

    private String fileName;
    private String benefitPlanId;
}
