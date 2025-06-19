package ru.slisarenko.pxelsoftware.integration.db.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import ru.slisarenko.pxelsoftware.db.dao.UserDAO;
import ru.slisarenko.pxelsoftware.exception.UserException;
import ru.slisarenko.pxelsoftware.integration.db.annotation.IntegrationTest;

import java.time.LocalDate;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@IntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOImplTest {

    private static Long userTestId;
    private static String oldPhone;
    private static String newPhone;
    private static String oldEmail;
    private static String newEmail;
    private static String password;
    private static String userName;
    private static LocalDate birthday;

    @Autowired
    private UserDAO userDAO;


    @BeforeAll
    static void initVariables() {

        var passwordEncoder = new BCryptPasswordEncoder();

        var random = new Random();
        userName = "testName" + random.nextInt(1000);
        oldPhone = "123456789" + random.nextInt(100);
        newPhone = "123456789" + random.nextInt(100);
        oldEmail = "test" + random.nextInt(1000) + "@gmail.com";
        newEmail = "test" + random.nextInt(1000) + "@gmail.com";
        password = "{bcrypt}" + passwordEncoder.encode(userName + "123");
        birthday = LocalDate.of(1900 + random.nextInt(100), random.nextInt(1, 12), random.nextInt(1, 31));
    }

    @Test
    void checkGetUser() {
        try {
            var user = userDAO.getUser(123L);
            assertNotNull(user);
        } catch (UserException e) {
            log.error(e.getMessage());
        } finally {
            log.info("Checking user");
        }
    }

    @Test
    @Commit
    @Order(1)
    void checkGetUserDTO() {

        var user = userDAO.createUser(userName, password, birthday, oldEmail, oldPhone).get();
        userTestId = user.getId();
        assertNotNull(user);
    }

    @Test
    @Order(2)
    void addEmail() {
        try {
            var user = userDAO.addEmail(userTestId, newEmail);
            assertNotNull(user);
            assertThat(user.getEmails()).hasSize(2);
        } catch (UserException e) {
            log.error(e.getMessage());
        } finally {
            log.info("Checking user");
        }
    }

    @Test
    @Order(3)
    void updateEmail() {
        try {
            var user = userDAO.updateEmail(userTestId, oldEmail, newEmail);
            assertNotNull(user);
            assertThat(user.getEmails().stream().filter(e -> e.equals(newEmail)).toList()).hasSize(1);
        } catch (UserException e) {
            log.error(e.getMessage());
        } finally {
            log.info("Checking user");
        }
    }

    @Test
    @Order(4)
    void deleteEmail() {
        try {
            var user = userDAO.addEmail(userTestId, newEmail);
            user = userDAO.deleteEmail(userTestId, newEmail);
            assertThat(user.getEmails()).hasSize(1);
        } catch (UserException e) {
            log.error(e.getMessage());
        } finally {
            log.info("Checking user");
        }
    }

    @Test
    @Order(5)
    void addPhone() {
        try {
            var user = userDAO.addPhone(userTestId, newPhone);
            assertNotNull(user);
            assertThat(user.getPhones()).hasSize(2);
        } catch (UserException e) {
            log.error(e.getMessage());
        } finally {
            log.info("Checking user");
        }

    }

    @Test
    @Order(6)
    void updatePhone() {
        try {
            var user = userDAO.updatePhone(userTestId, oldPhone, newPhone);
            assertNotNull(user);
            assertThat(user.getPhones().stream().filter(t -> t.equals(newPhone)).toList()).hasSize(1);
        } catch (UserException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    @Order(7)
    void deletePhone() {
        try {
            var user = userDAO.addPhone(userTestId, newPhone);
            user = userDAO.deletePhone(userTestId, newPhone);
            assertThat(user.getPhones()).hasSize(1);
        } catch (UserException e) {
            log.error(e.getMessage());
        }
    }
}