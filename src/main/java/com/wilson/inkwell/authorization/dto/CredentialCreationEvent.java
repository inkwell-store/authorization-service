package com.wilson.inkwell.authorization.dto;

import java.io.Serializable;
import java.util.UUID;

public record CredentialCreationEvent(String email, UUID credentialKey) implements Serializable {

}
