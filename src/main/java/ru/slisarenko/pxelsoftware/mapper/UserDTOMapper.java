package ru.slisarenko.pxelsoftware.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.slisarenko.pxelsoftware.db.entity.User;
import ru.slisarenko.pxelsoftware.dto.UserProfileDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserDTOMapper {

    @Mapping(target = "emails",
            expression = """
                    java(user.getEmails().stream()
                        .map(ru.slisarenko.pxelsoftware.db.entity.EmailData::getEmail)
                        .collect(java.util.stream.Collectors.toList())
                    )""")
    @Mapping(target = "phones",
            expression = """
                    java(user.getPhones().stream()
                        .map(ru.slisarenko.pxelsoftware.db.entity.PhoneData::getPhone)
                        .collect(java.util.stream.Collectors.toList())
                        )""")
    @Mapping(target = "balance",
            expression = "java(user.getAccount().getBalance())")
    UserProfileDTO userToUserDTO(User user);

}
