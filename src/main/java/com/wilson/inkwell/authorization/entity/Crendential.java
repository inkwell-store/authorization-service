package com.wilson.inkwell.authorization.entity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CREDENTIAL_TBL")
@Setter
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Crendential {

    // I'm using an UUID here as pk. Using an integer or long would be better for
    // perfomance in a real application, but for the sake of simplicity I'm doing
    // things this way. Another possibility would be to use both, the pk as a number
    // and a column for the UUID, then expose the latter.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private UUID id;

    private String email;

    private String password;

    private Instant createdAt;

    @OneToMany(mappedBy = "credential")
    private Set<CredentialRole> credentialRoles;

}
