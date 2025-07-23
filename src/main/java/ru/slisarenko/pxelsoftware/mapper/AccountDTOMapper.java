package ru.slisarenko.pxelsoftware.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.slisarenko.pxelsoftware.db.entity.Account;
import ru.slisarenko.pxelsoftware.dto.AccountDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountDTOMapper {
    AccountDTO userAccountToAccountDTOMapper(Account account);
}
