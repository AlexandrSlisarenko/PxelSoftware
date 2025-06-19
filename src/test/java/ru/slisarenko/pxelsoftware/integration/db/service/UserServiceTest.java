package ru.slisarenko.pxelsoftware.integration.db.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.service.UserService;
import ru.slisarenko.pxelsoftware.dto.filter.UserFilterByNameAndPhoneAndEmailAndDateOfBirth;
import ru.slisarenko.pxelsoftware.integration.db.annotation.IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    void checkGetUserById() {
        var user = userService.getById(3L);
        assertNotNull(user);
    }

    @Test
    void checkGetUserByIdThrowsException() {
        assertEquals(-1L, (long) userService.getById(300L).getId());
    }

    @Test
    void checkAddRecurringEmail() {
        var checkedUser = userService.addEmail(3L, "test@gmail.com");
        assertEquals(-1L, checkedUser.getId());
    }

    @Test
    void checkErrorFormatEmail() {
        var checkedUser = userService.addEmail(3L, "testgmail.com");
        assertEquals(-1L, checkedUser.getId());
    }

    @Test
    void checkFilter() {
        var typeSort = Sort.sort(User.class);
        var sortingParameter = typeSort.by(User::getDateOfBirth).descending();
        var pageable = PageRequest.of(1, 1, sortingParameter);
        var filter = UserFilterByNameAndPhoneAndEmailAndDateOfBirth.builder()
                .name("testName4")
                .build();
        var result = userService.searchByFilter(filter, pageable);
        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }


}