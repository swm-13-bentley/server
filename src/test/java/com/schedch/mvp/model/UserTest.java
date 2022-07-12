package com.schedch.mvp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    @Test
    public void create_user_test() throws Exception {
        //given
        String username = "username";

        //when

        //then
        User user = new User(username);

    }
}