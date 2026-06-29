package com.lovable.codifyAI.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ResourceNotFoundException extends  RuntimeException{
    String resourceName;
    String resourceId;

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(String.format("%s with id %s not found", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}
