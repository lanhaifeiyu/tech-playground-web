package com.lhfeiyu.tech.DTO;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DataAndTotal<T> {

    private List<T> data;
    private int total;

}
