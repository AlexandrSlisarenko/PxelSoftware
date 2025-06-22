package ru.slisarenko.pxelsoftware.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.slisarenko.pxelsoftware.db.entity.Account;
import ru.slisarenko.pxelsoftware.db.entity.EmailData;
import ru.slisarenko.pxelsoftware.db.entity.PhoneData;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.db.repositary.UserRepository;
import ru.slisarenko.pxelsoftware.dto.filter.FilterParams;
import ru.slisarenko.pxelsoftware.exception.UserException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserDAOImpl implements UserDAO {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long id) throws UserException {
        try {
            return getUserFromDB(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(e.getMessage());
        }
    }

    @Override
    public User getUserByName(String name) throws UserException {
        try {
            return userRepository.findByName(name);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(e.getMessage());
        }
    }

    @Override
    public User addEmail(Long userId, String email) throws UserException {
        var user = getUserFromDB(userId);

        user.getEmails().add(EmailData.builder()
                .email(email)
                .user(user)
                .build());

        return saveAndFlush(user);
    }

    @Override
    public User updateEmail(Long userId, String emailOld, String emailNew) throws UserException {
        var userFromDB = getUserFromDB(userId);

        userFromDB.getEmails().forEach(email -> {
            email.setEmail(email.getEmail().equals(emailOld) ? emailNew : emailOld);
        });

        return saveAndFlush(userFromDB);
    }

    @Override
    public User deleteEmail(Long userId, String email) throws UserException {
        var userFromDB = getUserFromDB(userId);

        userFromDB.getEmails().remove(
                userFromDB.getEmails().stream()
                        .filter(e -> e.getEmail().equals(email))
                        .findFirst()
                        .orElseThrow());

        return saveAndFlush(userFromDB);
    }

    @Override
    public User addPhone(Long userId, String phone) throws UserException {
        var user = getUserFromDB(userId);

        user.getPhones().add(PhoneData.builder()
                .phone(phone)
                .user(user)
                .build());

        return saveAndFlush(user);
    }

    @Override
    public User updatePhone(Long userId, String phoneOld, String phoneNew) throws UserException {
        var user = getUserFromDB(userId);
        user.getPhones().forEach(phone -> {
            phone.setPhone(phone.getPhone().equals(phoneOld) ? phoneNew : phoneOld);
        });
        return saveAndFlush(user);
    }

    @Override
    public User deletePhone(Long userId, String phone) throws UserException {
        var user = getUserFromDB(userId);
        user.getPhones().remove(user.getPhones().stream()
                .filter(p -> p.getPhone().equals(phone))
                .findFirst()
                .orElseThrow(() -> new UserException("Phone number not found")));
        return saveAndFlush(user);
    }

    @Override
    public Integer countPhone(Long userId) throws UserException {
        var user = getUserFromDB(userId);
        return user.getPhones().size();
    }

    @Override
    public Integer countEmail(Long userId) throws UserException {
        var user = getUserFromDB(userId);
        return user.getEmails().size();
    }

    @Override
    public Page<User> searchUsersByFilter(FilterParams filter, Pageable pageable) throws UserException {
        try {
            return userRepository.searchUsersByFilter(filter, pageable);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(e.getMessage(), e);
        }
    }

    public Optional<User> createUser(String username, String password, LocalDate dateOfBirth, String email, String phone) {
        var user = User.builder()
                .name(username)
                .password(password)
                .dateOfBirth(dateOfBirth)
                .build();
        var userFromDB = userRepository.saveAndFlush(user);
        var account = Account.builder()
                .user(userFromDB)
                .balance(new BigDecimal(55))
                .build();
        var emailData = EmailData.builder()
                .email(email)
                .user(userFromDB)
                .build();
        var phoneData = PhoneData.builder()
                .phone(phone)
                .user(userFromDB)
                .build();
        userFromDB.setAccount(account);
        userFromDB.getEmails().add(emailData);
        userFromDB.getPhones().add(phoneData);
        try {
            return Optional.of(userRepository.saveAndFlush(user));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    private User getUserFromDB(Long Id) throws UserException {
        return userRepository.findById(Id).orElseThrow(() -> new UserException("User not found"));
    }

    private User saveAndFlush(User user) throws UserException {
        try {
            return userRepository.saveAndFlush(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserException(e.getMessage(), e);
        }
    }

}
