package ru.slisarenko.pxelsoftware.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.slisarenko.pxelsoftware.db.dao.UserDAO;
import ru.slisarenko.pxelsoftware.db.entity.EmailData;
import ru.slisarenko.pxelsoftware.db.entity.PhoneData;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.dto.UserDTO;
import ru.slisarenko.pxelsoftware.dto.filter.UserFilterByNameAndPhoneAndEmailAndDateOfBirth;
import ru.slisarenko.pxelsoftware.exception.UserException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;

    public UserDTO getByName(String name) {
        try {
            var userFromDB = userDAO.getUserByName(name);
            return mapEntityToDTO(userFromDB);
        } catch (UserException userException) {
            return getEmptyUser("The user with the name " + name + " is not unidentified");
        }
    }

    public UserDTO getById(Long id) {
        try {
            var userFromDB = userDAO.getUser(id);
            return mapEntityToDTO(userFromDB);
        } catch (UserException userException) {
            return getEmptyUser("The user with the id " + id + " is not unidentified");
        }
    }

    public UserDTO addEmail(Long id, String email) {
        try {
            var isHaveEmail = doesTheUserHaveAnEmail(id, email);

            return isHaveEmail ? mapEntityToDTO(userDAO.addEmail(id, email))
                    : getEmptyUser("Email " + email + " already exists");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO updateEmail(Long id, String emailOld, String emailNew) {
        try {
            var isHaveEmailOld = doesTheUserHaveAnEmail(id, emailOld);
            var isNotHaveEmailNew = !doesTheUserHaveAnEmail(id, emailNew);

            return isHaveEmailOld && isNotHaveEmailNew ? mapEntityToDTO(userDAO.updateEmail(id, emailOld, emailNew))
                    : getEmptyUser("Incorrect data for updating mail(" + emailOld + ", " + emailNew + ")");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO deleteEmail(Long id, String email) {
        try {
            if(!isOnlyOneEmail(id)) {
                var isHaveEmail = doesTheUserHaveAnEmail(id, email);
                return isHaveEmail ? mapEntityToDTO(userDAO.deleteEmail(id, email))
                        : getEmptyUser("Email " + email + " is not exists");
            }
            return getEmptyUser("Is only one email");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO addPhone(Long id, String phone) {
        try {
            var isHavePhone = doesTheUserHaveAnPhone(id, phone);

            return isHavePhone ? mapEntityToDTO(userDAO.addEmail(id, phone))
                    : getEmptyUser("Phone " + phone + " already exists");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO updatePhone(Long id, String phoneOld, String phoneNew) {
        try {
            var isHavePhoneOld = doesTheUserHaveAnPhone(id, phoneOld);
            var isNotHavePhoneNew = doesTheUserHaveAnPhone(id, phoneNew);

            return isHavePhoneOld && !isNotHavePhoneNew ?  mapEntityToDTO(userDAO.updateEmail(id, phoneOld, phoneNew))
                    : getEmptyUser("Incorrect data for updating mail(" + phoneOld + ", " + phoneNew + ")");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO deletePhone(Long id, String phone) {
        try {
            if(!isOnlyOnePhone(id)) {
                var isHavePhone = doesTheUserHaveAnPhone(id, phone);

                return isHavePhone ? mapEntityToDTO(userDAO.deleteEmail(id, phone))
                        : getEmptyUser("Phone " + phone + " is not exists");
            }
            return getEmptyUser("Is only one phone");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public Page<UserDTO> searchByFilter(UserFilterByNameAndPhoneAndEmailAndDateOfBirth filter, Pageable pageable){
        try{
            var foundUsers = userDAO.searchUsersByFilter(filter, pageable);
            var usersDTO = foundUsers.stream()
                    .map(this::mapEntityToDTO)
                    .toList();
            return new PageImpl<>(usersDTO);
        } catch (UserException userException) {
            var emptyUser = List.of(getEmptyUser(userException.getMessage()));
            return new PageImpl<>(emptyUser);
        }
    }

    private UserDTO mapEntityToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getName())
                .password(user.getPassword())
                .dateOfBirth(user.getDateOfBirth())
                .balance(user.getAccount().getBalance())
                .emails(user.getEmails().stream().map(EmailData::getEmail).collect(Collectors.toList()))
                .phones(user.getPhones().stream().map(PhoneData::getPhone).collect(Collectors.toList()))
                .build();
    }


    private boolean doesTheUserHaveAnEmail(Long id, String email) throws UserException {
        var userFromDB = userDAO.getUser(id);

        return userFromDB.getEmails().stream()
                .map(EmailData::getEmail)
                .toList()
                .contains(email);
    }

    private boolean doesTheUserHaveAnPhone(Long id, String phone) throws UserException {
        var userFromDB = userDAO.getUser(id);

        return userFromDB.getPhones().stream()
                .map(PhoneData::getPhone)
                .toList()
                .contains(phone);
    }

    private boolean isOnlyOneEmail(Long id) throws UserException {
        return userDAO.countEmail(id) > 1;
    }

    private boolean isOnlyOnePhone(Long id) throws UserException {
        return userDAO.countPhone(id) > 1;
    }


    private UserDTO getEmptyUser(String errorMessage) {
        log.error(errorMessage);
        return UserDTO.builder()
                .id(-1L)
                .messageError(errorMessage)
                .build();
    }


}
