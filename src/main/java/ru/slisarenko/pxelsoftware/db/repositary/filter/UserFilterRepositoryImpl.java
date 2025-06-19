package ru.slisarenko.pxelsoftware.db.repositary.filter;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.db.querydsl.QPredicates;
import ru.slisarenko.pxelsoftware.dto.filter.FilterParams;
import ru.slisarenko.pxelsoftware.dto.filter.UserFilterByNameAndPhoneAndEmailAndDateOfBirth;

import java.util.Collections;
import java.util.List;

import static ru.slisarenko.pxelsoftware.db.entity.QUser.user;

@RequiredArgsConstructor
public class UserFilterRepositoryImpl implements UserFilterRepository {

    private final EntityManager entityManager;

    @Override
    public Page<User> searchUsersByFilter(FilterParams filterNameAndPhoneAndEmailAndDateOfBirth, Pageable pageable) {
        var filterClass = UserFilterByNameAndPhoneAndEmailAndDateOfBirth.class;

        if(isFilterSuitableForSearch(filterClass, filterNameAndPhoneAndEmailAndDateOfBirth)) {
            var filter = (UserFilterByNameAndPhoneAndEmailAndDateOfBirth) filterNameAndPhoneAndEmailAndDateOfBirth;

            var predicates = QPredicates.builder()
                    .and(filter.getDateOfBirth(), user.dateOfBirth::before)
                    .and(filter.getPhone(), user.phones.any().phone::eq)
                    .and(filter.getName(), user.name::startsWith)
                    .and(filter.getEmail(), user.emails.any().email::eq)
                    .build();

            var countAllUsers = new JPAQuery<User>(entityManager)
                    .select(user)
                    .from(user)
                    .where(predicates)
                    .stream()
                    .count();

            var users = new JPAQuery<User>(entityManager)
                    .select(user)
                    .from(user)
                    .where(predicates)
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch();

            return PageableExecutionUtils.getPage(users, pageable, () -> countAllUsers);
        }
        return Page.empty();
    }

    private boolean isFilterSuitableForSearch(Class clazz, FilterParams filter) {
        return filter.getClass().equals(clazz);
    }
}
