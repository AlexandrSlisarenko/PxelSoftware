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
import ru.slisarenko.pxelsoftware.dto.TransferDTO;
import ru.slisarenko.pxelsoftware.dto.UserDTO;
import ru.slisarenko.pxelsoftware.dto.filter.UserFilterByNameAndPhoneAndEmailAndDateOfBirth;
import ru.slisarenko.pxelsoftware.exception.TransferException;
import ru.slisarenko.pxelsoftware.exception.UserException;
import ru.slisarenko.pxelsoftware.mapper.UserDTOMapper;

import java.util.List;

import static ru.slisarenko.pxelsoftware.config.Constants.*;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;
    private final UserDTOMapper userDTOMapper;

    public UserDTO getByName(String name) {
        try {
            var userFromDB = userDAO.getUserByName(name);
            return userDTOMapper.userToUserDTO(userFromDB);
        } catch (UserException userException) {
            return getEmptyUser("The user with the name " + name + " is not unidentified");
        }
    }

    public UserDTO getById(Long id) {
        try {
            var userFromDB = userDAO.getUser(id);
            return userDTOMapper.userToUserDTO(userFromDB);
        } catch (UserException userException) {
            return getEmptyUser("The user with the id " + id + " is not unidentified");
        }
    }

    public UserDTO addEmail(Long id, String email) {
        try {
            var isHaveEmail = doesTheUserHaveAnEmail(id, email);

            return isHaveEmail ? userDTOMapper.userToUserDTO(userDAO.addEmail(id, email))
                    : getEmptyUser("Email " + email + " already exists");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO updateEmail(Long id, String emailOld, String emailNew) {
        try {
            var isHaveEmailOld = doesTheUserHaveAnEmail(id, emailOld);
            var isNotHaveEmailNew = !doesTheUserHaveAnEmail(id, emailNew);

            return isHaveEmailOld && isNotHaveEmailNew ? userDTOMapper.userToUserDTO(userDAO.updateEmail(id, emailOld, emailNew))
                    : getEmptyUser("Incorrect data for updating mail(" + emailOld + ", " + emailNew + ")");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO deleteEmail(Long id, String email) {
        try {
            if (!isOnlyOneEmail(id)) {
                var isHaveEmail = doesTheUserHaveAnEmail(id, email);
                return isHaveEmail ? userDTOMapper.userToUserDTO(userDAO.deleteEmail(id, email))
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

            return isHavePhone ? userDTOMapper.userToUserDTO(userDAO.addEmail(id, phone))
                    : getEmptyUser("Phone " + phone + " already exists");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO updatePhone(Long id, String phoneOld, String phoneNew) {
        try {
            var isHavePhoneOld = doesTheUserHaveAnPhone(id, phoneOld);
            var isNotHavePhoneNew = doesTheUserHaveAnPhone(id, phoneNew);

            return isHavePhoneOld && !isNotHavePhoneNew ? userDTOMapper.userToUserDTO(userDAO.updateEmail(id, phoneOld, phoneNew))
                    : getEmptyUser("Incorrect data for updating mail(" + phoneOld + ", " + phoneNew + ")");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public UserDTO deletePhone(Long id, String phone) {
        try {
            if (!isOnlyOnePhone(id)) {
                var isHavePhone = doesTheUserHaveAnPhone(id, phone);

                return isHavePhone ? userDTOMapper.userToUserDTO(userDAO.deleteEmail(id, phone))
                        : getEmptyUser("Phone " + phone + " is not exists");
            }
            return getEmptyUser("Is only one phone");
        } catch (UserException userException) {
            return getEmptyUser(userException.getMessage());
        }
    }

    public Page<UserDTO> searchByFilter(UserFilterByNameAndPhoneAndEmailAndDateOfBirth filter, Pageable pageable) {
        try {
            var foundUsers = userDAO.searchUsersByFilter(filter, pageable);
            var usersDTO = foundUsers.stream()
                    .map(userDTOMapper::userToUserDTO)
                    .toList();
            return new PageImpl<>(usersDTO);
        } catch (UserException userException) {
            var emptyUser = List.of(getEmptyUser(userException.getMessage()));
            return new PageImpl<>(emptyUser);
        }
    }



    public String protectedTransfer(String sendingUserName, TransferDTO transferData) throws UserException, TransferException {
        var sendingUser = getByName(sendingUserName);
        isEnoughMoney(sendingUser, transferData);
        transfer(sendingUser.getId(), transferData);
        return TRANSFER_COMPLETED;
    }

    private void transfer(Long sendingUserId, TransferDTO transferData) throws UserException, TransferException {
        userDAO.updateUserBalance(sendingUserId, transferData.getTransferAmount(), SEND);
        userDAO.updateUserBalance(transferData.getAcceptingUserId(), transferData.getTransferAmount(), ACCEPT);
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

    private boolean isEnoughMoney(UserDTO sendingUser, TransferDTO transferData) {
        if (sendingUser.getBalance().longValue() > transferData.getTransferAmount()) {
            return true;
        } else {
            throw new TransferException("Transfer amount more balance sending user");
        }
    }


    private UserDTO getEmptyUser(String errorMessage) {
        log.error(errorMessage);
        return UserDTO.builder()
                .id(-1L)
                .messageError(errorMessage)
                .build();
    }


}
