package com.QAPP.api.utility;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsPojo {
    private String to;
    private String message;
}
