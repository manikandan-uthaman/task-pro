package com.innovate.taskpro.service;

import com.innovate.taskpro.UserDto;
import com.innovate.taskpro.entity.UserBO;
import com.innovate.taskpro.mapper.UserDetailsMapper;
import com.innovate.taskpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsMapper userDetailsMapper;

    @Override
    public UserDto loadUserByUsername(String username) throws UsernameNotFoundException {
        UserBO user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userDetailsMapper.mapUserBoToDto(user);
    }
}
