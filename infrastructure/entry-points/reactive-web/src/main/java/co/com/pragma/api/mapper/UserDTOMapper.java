package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RegisterUserDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    RegisterUserDTO toResponse(User user);
    User toEntity(RegisterUserDTO dto);
}
