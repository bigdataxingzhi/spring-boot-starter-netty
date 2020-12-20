package org.springframework.boot.netty.support;


import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValidateUtil {

    private ValidateUtil() {
    }

    /**
     * 验证器
     */
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    /**
     * 校验实体，返回实体所有属性的校验结果
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> BindingResult validateEntity(T obj) {
        //解析校验结果
        Set<ConstraintViolation<T>> validateSet = validator.validate(obj, Default.class);
        return buildValidationResult(validateSet);
    }

    public static <T> BindingResult validateEntity(T obj,Class<?>[] groups) {
        //解析校验结果
        Set<ConstraintViolation<T>> validateSet = validator.validate(obj, groups);
        return buildValidationResult(validateSet);
    }

    /**
     * 校验指定实体的指定属性是否存在异常
     *
     * @param obj
     * @param propertyName
     * @param <T>
     * @return
     */
    public static <T> BindingResult validateProperty(T obj, String propertyName) {
        Set<ConstraintViolation<T>> validateSet = validator.validateProperty(obj, propertyName, Default.class);
        return buildValidationResult(validateSet);
    }

    /**
     * 将异常结果封装返回
     *
     * @param validateSet
     * @param <T>
     * @return
     */
    private static <T> BindingResult buildValidationResult(Set<ConstraintViolation<T>> validateSet) {
        BindingResult bindingResult = new BindingResult();
        if (!CollectionUtils.isEmpty(validateSet)) {
            bindingResult.setHasErrors(true);
            Map<String, String> errorMsgMap = new HashMap<>();
            for (ConstraintViolation<T> constraintViolation : validateSet) {
                errorMsgMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            }
            bindingResult.setErrorMsg(errorMsgMap);
        }
        bindingResult.setHasErrors(false);
        return bindingResult;
    }
}