package co.com.crediya.consumer.mapper;

import co.com.crediya.consumer.dto.UserResponse;
import co.com.crediya.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

       User toModel(UserResponse dto);


}
