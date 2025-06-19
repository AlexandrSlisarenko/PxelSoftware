package ru.slisarenko.pxelsoftware.db.repositary.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.dto.filter.FilterParams;
import ru.slisarenko.pxelsoftware.exception.UserException;

public interface UserFilterRepository {

    Page<User> searchUsersByFilter(FilterParams filter, Pageable pageable) throws UserException;

}
