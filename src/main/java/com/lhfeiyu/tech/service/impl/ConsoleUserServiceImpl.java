package com.lhfeiyu.tech.service.impl;

import com.zom.statistics.DTO.RtvConsoleUser;
import com.zom.statistics.dao.mapper.common.ConsoleUserMapper;
import com.zom.statistics.service.IConsoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsoleUserServiceImpl implements IConsoleUserService {

    @Autowired
    private ConsoleUserMapper consoleUserMapper;

    @Override
    public RtvConsoleUser selectById(int id) {
        return consoleUserMapper.selectById(id);
    }

    @Override
    public RtvConsoleUser selectByUserNameAndPsw(String name, String psw) {
        return consoleUserMapper.selectByNameAndPsw(name, psw);
    }
}
