package com.wilson.inkwell.authorization.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Credential {

    // I'm using an UUID here as pk. Using an integer or long would be better for
    // perfomance in a real application, but for the sake of simplicity I'm doing
    // things this way. Another possibility would be to use both, the pk as a number
    // and a column for the UUID, then expose the latter.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(unique = true)
    private String email;

    private String password;

    private Instant createdAt;

    // It's useful to initialize the Set here because if Hibernate queries the
    // entity and doesn't find a valid value it will populate it as a null, and
    // trying to access it later will result in a NullPointerException. Also, it's
    // easier for to call add() without having to initialize it myself
    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL)
    private Set<CredentialRole> credentialRoles = new HashSet<>();

}
