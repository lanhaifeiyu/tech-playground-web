package com.lhfeiyu.tech.service;

import com.lhfeiyu.tech.DTO.RtvConsoleUser;

public interface IConsoleUserService {

    RtvConsoleUser selectById (int id);

    RtvConsoleUser selectByUserNameAndPsw (String name, String psw);

}
