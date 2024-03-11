package com.ingeniorx.butterscotch.java.upstreamconsumer.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity
@Table(name="remediation_queue")
public class RemediationQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String is_cep_flag;
}
