package com.pay.pal.service.impl;

import com.pay.pal.model.User;
import com.pay.pal.repository.IUserRepository;
import com.pay.pal.service.IServiceUser;
import com.pay.pal.vo.RequestUserVo;
import com.pay.pal.vo.ResponseUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ServiceUserImpl implements IServiceUser {

    @Autowired
    IUserRepository userRepository;


    @Override
    public ResponseUserVo save(RequestUserVo user) {
        User newUser = new User();
        newUser.setName(user.name());
        newUser.setLastName(user.lastName());
        newUser.setYear(user.year());

        userRepository.save(newUser);

        return new ResponseUserVo(
                newUser.getName(),
                newUser.getLastName(),
                newUser.getYear()
        );

    }

    @Override
    public List<ResponseUserVo> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new ResponseUserVo(
                        user.getName(),
                        user.getLastName(),
                        user.getYear()
                ))
                .toList();
    }

    @Override
    public ResponseUserVo update(Long id, RequestUserVo user) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userToUpdate.setName(user.name());
        userToUpdate.setLastName(user.lastName());
        userToUpdate.setYear(user.year());

        User updatedUser = userRepository.save(userToUpdate);

        return new ResponseUserVo(
                updatedUser.getName(),
                updatedUser.getLastName(),
                updatedUser.getYear()
        );
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        userRepository.deleteById(id);
    }
}
