package com.sportera.sportera.repositories;

import com.sportera.sportera.TestUtil;
import com.sportera.sportera.models.User;
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
    public void injectedComponentsAreNotNull() {
        assertThat(testEntityManager).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void should_find_no_users_if_repository_is_empty() {
        Iterable<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    public void should_store_a_user() {
        User user = userRepository.save(TestUtil.createValidUser());

        assertThat(user).hasFieldOrPropertyWithValue("username", "test-user");
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
    public void should_find_user_by_id() {
        User user1 = TestUtil.createValidUser();
        testEntityManager.persist(user1);

        User user2 = TestUtil.createValidUser2();
        testEntityManager.persist(user2);

        User foundUser = userRepository.findById(user2.getId()).get();
        assertThat(foundUser).isEqualTo(user2);
    }

    @Test
    public void findByEmail_whereUserExists_returnsUser() {

        testEntityManager.persist(TestUtil.createValidUser());

        User inDB = userRepository.findByEmailIgnoreCase("test@gmail.com");
        assertThat(inDB).isNotNull();
    }

    @Test
    public void findByEmail_whereUserDoesNotExist_returnsNull() {
        User inDB = userRepository.findByEmailIgnoreCase("nonexistinguser@gmail.com");
        assertThat(inDB).isNull();
    }

    @Test
    public void should_update_user_by_id() {
        User user1 = TestUtil.createValidUser();
        testEntityManager.persist(user1);

        User user2 = TestUtil.createValidUser2();
        testEntityManager.persist(user2);

        User userToUpdate = userRepository.findById(user2.getId()).get();
        userToUpdate.setUsername("Updated username");
        userToUpdate.setEmail("updated@test.com");

        userRepository.save(userToUpdate);

        User checkUser = userRepository.findById(user2.getId()).get();

        assertThat(checkUser.getId()).isEqualTo(user2.getId());
        assertThat(checkUser.getUsername()).isEqualTo(userToUpdate.getUsername());
        assertThat(checkUser.getEmail()).isEqualTo(userToUpdate.getEmail());
    }

    @Test
    public void should_delete_user_by_id() {
        User user1 = TestUtil.createValidUser();
        testEntityManager.persist(user1);

        User user2 = TestUtil.createValidUser2();
        testEntityManager.persist(user2);

        User user3 = TestUtil.createValidUser3();
        testEntityManager.persist(user3);

        userRepository.deleteById(user2.getId());

        Iterable<User> users = userRepository.findAll();

        assertThat(users).hasSize(2).contains(user1, user3);
    }

    @Test
    public void should_delete_all_users() {
        User user1 = TestUtil.createValidUser();
        testEntityManager.persist(user1);

        User user2 = TestUtil.createValidUser2();
        testEntityManager.persist(user2);

        userRepository.deleteAll();
        assertThat(userRepository.findAll()).isEmpty();
    }
}
