package com.sportera.sportera.repositories;

import com.sportera.sportera.TestUtil;
import com.sportera.sportera.models.ConfirmationToken;
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
public class ConfirmationTokenRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void cleanUp() {
        confirmationTokenRepository.deleteAll();
    }

    @Test
    public void injectedComponentsAreNotNull() {
        assertThat(testEntityManager).isNotNull();
        assertThat(confirmationTokenRepository).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void should_find_no_confirmationTokens_if_repository_is_empty() {
        Iterable<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAll();

        assertThat(confirmationTokens).isEmpty();
    }

    @Test
    public void should_store_a_confirmationToken() {
        User user = TestUtil.createValidUser();
        userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);
        assertThat(confirmationToken).hasFieldOrPropertyWithValue("user", user);
        assertThat(confirmationToken.getConfirmationToken()).isNotNull();
        assertThat(confirmationToken.getCreateDate()).isNotNull();
    }

    @Test
    public void should_find_all_confirmationTokens() {
        User user1 = TestUtil.createValidUser();
        userRepository.save(user1);
        User user2 = TestUtil.createValidUser2();
        userRepository.save(user2);

        ConfirmationToken confirmationToken1 = new ConfirmationToken(user1);
        confirmationTokenRepository.save(confirmationToken1);
        ConfirmationToken confirmationToken2 = new ConfirmationToken(user2);
        confirmationTokenRepository.save(confirmationToken2);

        Iterable<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAll();
        assertThat(confirmationTokens).hasSize(2).contains(confirmationToken1, confirmationToken2);
    }

    @Test
    public void should_find_confirmationToken_by_userId() {
        User user = TestUtil.createValidUser();
        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        ConfirmationToken foundConfirmationToken = confirmationTokenRepository.findByUser(user);
        System.out.println(foundConfirmationToken);
        assertThat(confirmationToken).isEqualTo(foundConfirmationToken);
    }

    @Test
    public void should_delete_confirmationToken_by_id() {
        User user = TestUtil.createValidUser();
        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        confirmationTokenRepository.deleteById(confirmationToken.getId());
    }

    @Test
    public void confirmationToken_should_be_deleted_automatically_after_user_was_deleted() {
        User user = TestUtil.createValidUser();
        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        userRepository.deleteById(user.getId());
        confirmationTokenRepository.findById(confirmationToken.getId()).isEmpty();
    }

    @Test
    public void should_delete_all_confirmationTokens() {
        User user1 = TestUtil.createValidUser();
        userRepository.save(user1);
        User user2 = TestUtil.createValidUser2();
        userRepository.save(user2);

        ConfirmationToken confirmationToken1 = new ConfirmationToken(user1);
        confirmationTokenRepository.save(confirmationToken1);
        ConfirmationToken confirmationToken2 = new ConfirmationToken(user2);
        confirmationTokenRepository.save(confirmationToken2);

        confirmationTokenRepository.deleteAll();
        assertThat(confirmationTokenRepository.findAll()).isEmpty();
    }

}
