package com.innovate.taskpro;

import com.innovate.taskpro.entity.UserBO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserDto implements UserDetails {

    private String userId;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private Boolean isActive;
    private Boolean isLocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDto build(UserBO userBO) {
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
