package com.pay.pal.service;

import com.pay.pal.vo.RequestUserVo;
import com.pay.pal.vo.ResponseUserVo;

import java.util.List;


public interface IServiceUser {
    ResponseUserVo save(RequestUserVo user);

    List<ResponseUserVo> listUsers();

    ResponseUserVo update(Long id, RequestUserVo user);

    void delete(Long id);
}
