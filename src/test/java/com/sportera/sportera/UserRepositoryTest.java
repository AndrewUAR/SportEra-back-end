package com.sportera.sportera;

import com.sportera.sportera.models.User;
import com.sportera.sportera.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void should_find_no_users_if_repository_is_empty() {
        Iterable<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    public void should_store_a_user() {
        User user = userRepository.save(TestUtil.createValidUser());

        assertThat(user).hasFieldOrPropertyWithValue("firstName", "test-user");
        assertThat(user).hasFieldOrPropertyWithValue("lastName", "test-user");
        assertThat(user).hasFieldOrPropertyWithValue("email", "test@gmail.com");
        assertThat(user).hasFieldOrPropertyWithValue("password", "P4ssword");
        assertThat(user).hasFieldOrPropertyWithValue("isActive", false);
    }

    @Test
    public void should_find_all_users() {
        User user1 = TestUtil.createValidUser();
        testEntityManager.persist(user1);

        User user2 = TestUtil.createValidUser2();
        testEntityManager.persist(user2);

        User user3 = TestUtil.createValidUser3();
        testEntityManager.persist(user3);

        Iterable<User> users = userRepository.findAll();
        assertThat(users).hasSize(3).contains(user1, user2, user3);
    }

    @Test
    public void findByEmail_whereUserExists_returnsUser() {

        testEntityManager.persist(TestUtil.createValidUser());

        User inDB = userRepository.findByEmail("test@gmail.com");
        assertThat(inDB).isNotNull();
    }

    @Test
    public void findByEmail_whereUserDoesNotExist_returnsNull() {
        User inDB = userRepository.findByEmail("nonexistinguser@gmail.com");
        assertThat(inDB).isNull();
    }
}
