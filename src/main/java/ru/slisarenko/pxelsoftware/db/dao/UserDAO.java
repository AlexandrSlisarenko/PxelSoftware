package ru.slisarenko.pxelsoftware.db.dao;

import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.db.repositary.filter.UserFilterRepository;
import ru.slisarenko.pxelsoftware.exception.UserException;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDAO extends UserFilterRepository {

    User getUser(Long id) throws UserException;

    User getUserByName(String name) throws UserException;

    User addEmail(Long userId, String email) throws UserException;

    User updateEmail(Long userId, String emailOld, String emailNew) throws UserException;

    User deleteEmail(Long userId, String email) throws UserException;

    User addPhone(Long userId, String phone) throws UserException;

    User updatePhone(Long userId, String phoneOld, String phoneNew) throws UserException;

    User deletePhone(Long userId, String phone) throws UserException;

    Integer countPhone(Long userId) throws UserException;

    Integer countEmail(Long userId) throws UserException;

    Optional<User> createUser(String username, String password, LocalDate dateOfBirth, String email, String phone);


}
