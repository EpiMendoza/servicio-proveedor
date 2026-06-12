package com.pay.pal.web;

import com.pay.pal.service.IServiceUser;
import com.pay.pal.vo.RequestUserVo;
import com.pay.pal.vo.ResponseUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/user/")
public class ControllerUser {

    @Autowired
    IServiceUser serviceUser;

    @PostMapping("save")
    public ResponseEntity<ResponseUserVo> saveUser(@RequestBody RequestUserVo user) {
        ResponseUserVo response = serviceUser.save(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("list")
    public ResponseEntity<List<ResponseUserVo>> getUsers(){
        List<ResponseUserVo> listUsers = serviceUser.listUsers();
        return ResponseEntity.ok(listUsers);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ResponseUserVo> updateUser(@PathVariable Long id, @RequestBody RequestUserVo user) {
        ResponseUserVo response = serviceUser.update(id, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        serviceUser.delete(id);
        return ResponseEntity.noContent().build();
    }

}
