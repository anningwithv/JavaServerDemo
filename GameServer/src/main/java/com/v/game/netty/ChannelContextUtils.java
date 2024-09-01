package com.v.game.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelContextUtils {

    private static final ConcurrentHashMap<String, Channel> UserChannelMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChannelGroup> UserChannelGroupMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void addContext(String userId, Channel channel) {
        String channelId = channel.id().toString();
        AttributeKey key = null;
        if (!AttributeKey.exists(channelId)) {
            key = AttributeKey.newInstance(channelId);
        } else {
            key = AttributeKey.valueOf(channelId);
        }
        channel.attr(key).set(userId);

        UserChannelMap.put(userId, channel);
    }
}
