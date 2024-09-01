package com.v.game.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public int getNettyPort(){
        return 9090;
    }
}
