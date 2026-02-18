package com.bbsoft.spring_backend_starter.service.helper;

import com.bbsoft.spring_backend_starter.config.ObjectMapperProvider;
import com.bbsoft.spring_backend_starter.repository.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectMergerTest {

    private static final long USER_ID = 1L;
    private static final String USERNAME = "username";
    private static final String ENCODED_PASSWORD = "password";

    private final ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider();
    private final ObjectMerger objectMerger = new ObjectMerger(objectMapperProvider.getObjectMapper());

    @Test
    public void shallowMerge_ShouldReturnNullWhenTargetIsNull() {
        Assertions.assertNull(objectMerger.shallowMerge(null, createUser(), User.class, false));
    }

    @Test
    public void shallowMerge_ShouldReturnTargetWhenSourceIsNull() {
        // GIVEN
        var target = createUser();
        // THEN
        Assertions.assertEquals(target, objectMerger.shallowMerge(target, null, User.class, false));
    }

    @Test
    public void shallowMerge_ShouldMergeNonNulls() {
        // GIVEN
        var target = createUser();
        var source = createUser();
        source.setId(null);
        source.setEmail("email");
        // WHEN
        var merged = objectMerger.shallowMerge(target, source, User.class, false);
        // THEN
        Assertions.assertEquals(target.getId(), merged.getId());
        Assertions.assertEquals(target.getUsername(), merged.getUsername());
        Assertions.assertEquals(source.getEmail(), merged.getEmail());
        Assertions.assertEquals(target.getEncodedPassword(), merged.getEncodedPassword());
    }

    @Test
    public void shallowMerge_ShouldMergeNulls() {
        // GIVEN
        var target = createUser();
        var source = createUser();
        source.setId(null);
        source.setUsername(null);
        // WHEN
        var merged = objectMerger.shallowMerge(target, source, User.class, true);
        // THEN
        Assertions.assertNull(merged.getId());
        Assertions.assertNull(merged.getUsername());
        Assertions.assertNull(merged.getEmail());
        Assertions.assertEquals(target.getEncodedPassword(), merged.getEncodedPassword());
    }

    private User createUser() {
        return User.builder()
            .id(USER_ID)
            .username(USERNAME)
            .encodedPassword(ENCODED_PASSWORD)
            .build();
    }
}
