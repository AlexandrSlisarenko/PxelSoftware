package ru.slisarenko.pxelsoftware.db.repositary;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.db.repositary.filter.UserFilterRepository;
import ru.slisarenko.pxelsoftware.dto.UserIdDTO;

public interface UserRepository extends JpaRepository<User, Long>, UserFilterRepository {

    User findByName(@Length(max = 500) String name);
    UserIdDTO findIdByName(@Length(max = 500) String name);
}

