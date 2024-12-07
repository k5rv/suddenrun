package com.ksaraev.spotify.client.feign.decoders;

import com.ksaraev.spotify.client.exception.SpotifyClientDecodingErrorResponseIsNullException;
import com.ksaraev.spotify.client.feign.exception.FeignExceptionHandler;
import com.ksaraev.spotify.client.feign.exception.HandleFeignException;
import feign.Feign;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
@RequiredArgsConstructor
public class SpotifyClientFeignErrorDecoder implements ErrorDecoder {
  private final ApplicationContext applicationContext;
  private final ErrorDecoder.Default defaultErrorDecoder = new Default();
  private final Map<String, FeignExceptionHandler> feignExceptionHandlers;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    collectFeignExceptionHandlers();
  }

  @Override
  public Exception decode(String methodKey, Response response) {
    if (response == null) {
      throw new SpotifyClientDecodingErrorResponseIsNullException();
    }
    FeignExceptionHandler handler = feignExceptionHandlers.get(methodKey);
    if (handler == null) return defaultErrorDecoder.decode(methodKey, response);

    Exception exception = handler.handle(response);
    if (exception == null) return defaultErrorDecoder.decode(methodKey, response);

    return exception;
  }

  private void collectFeignExceptionHandlers() {
    Map<String, Object> feignClients = applicationContext.getBeansWithAnnotation(FeignClient.class);

    List<Method> clientMethods =
        feignClients.values().stream()
            .map(Object::getClass)
            .map(aClass -> aClass.getInterfaces()[0])
            .map(ReflectionUtils::getDeclaredMethods)
            .flatMap(Arrays::stream)
            .toList();

    for (Method method : clientMethods) {
      HandleFeignException annotation = method.getAnnotation(HandleFeignException.class);
      if (annotation != null) {
        String configKey = Feign.configKey(method.getDeclaringClass(), method);
        FeignExceptionHandler handlerBean = applicationContext.getBean(annotation.value());
        feignExceptionHandlers.put(configKey, handlerBean);
      }
    }
  }
}
