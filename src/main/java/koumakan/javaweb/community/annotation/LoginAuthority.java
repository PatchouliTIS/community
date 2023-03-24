package koumakan.javaweb.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)             // 注解用于方法上
@Retention(RetentionPolicy.RUNTIME)     // 作用期位于运行时
public @interface LoginAuthority {
}
