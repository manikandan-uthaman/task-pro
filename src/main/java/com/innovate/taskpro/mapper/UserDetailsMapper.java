package com.innovate.taskpro.mapper;

import com.innovate.taskpro.UserDto;
import com.innovate.taskpro.entity.UserBO;
import org.springframework.stereotype.Component;


@Component
public class UserDetailsMapper {

    public UserDto mapUserBoToDto(UserBO userBO) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userBO.getUserId());
        userDto.setUserName(userBO.getUserName());
        userDto.setFirstName(userBO.getFirstName());
        userDto.setLastName(userBO.getLastName());
        userDto.setEmail(userBO.getEmail());
        userDto.setPhone(userBO.getPhone());
        userDto.setIsActive(userBO.getIsActive());
        userDto.setRole(userBO.getRole());
        return userDto;
    }
}