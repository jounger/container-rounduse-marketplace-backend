package com.crm.websocket.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private JwtUntils jwtUntils;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue", "/user");
    config.setUserDestinationPrefix("/user");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/stomp").setAllowedOrigins("http://localhost:8080").withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
          List<String> authorization = accessor.getNativeHeader("Authorization");
          logger.info("Authorization-ws: {}", authorization);

          String accessToken = authorization.get(0).split(" ")[1];
          if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7, accessToken.length());
          }
          Authentication authentication;
          try {
            authentication = authUserByToken(accessToken);
            accessor.setUser(authentication);
          } catch (ServletException | IOException e) {
            logger.error("Cannot set user authentication: {}", e);
          }
        }
        return message;
      }
    });
  }

  private UsernamePasswordAuthenticationToken authUserByToken(String jwt) throws ServletException, IOException {
    try {
      if (jwt != null && jwtUntils.validateJwtToken(jwt) != null) {
        String username = jwtUntils.getUsernameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }
    return null;
  }
}