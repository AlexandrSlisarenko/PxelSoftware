package ru.slisarenko.pxelsoftware.db.repositary;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.slisarenko.pxelsoftware.db.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
