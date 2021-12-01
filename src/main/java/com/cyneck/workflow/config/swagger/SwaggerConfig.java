package com.cyneck.workflow.config.swagger;

import com.cyneck.workflow.common.UserInfoConstant;
import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/20 23:12
 **/
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@Slf4j
public class SwaggerConfig  {
    @Value("${swagger.enable:true}")
    private boolean swaggerEnable;
    private static String BASE_PACKAGE = "com.cyneck.workflow";
    private static String VERSION = "v1.0.0";

    @Bean
    public Docket createRestApi() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name(UserInfoConstant.USER_ID)
                .description("当前用户id")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build());
//        parameters.add(new ParameterBuilder()
//                .name("companyid")
//                .description("单位id")
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(true)
//                .build());
        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(parameters)
                .enable(swaggerEnable)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))//这种方式我们可以通过指定包名的方式，让Swagger 只去某些包下面扫描
//                .apis(RequestHandlerSelectors.any())//这种方式我们可以通过指定包名的方式，让Swagger 只去某些包下面扫描
                .paths(PathSelectors.any())//这种方式可以通过筛选 API 的 url 来进行过滤。
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("安恒信息", "http://www.example.com", "admin@example.com");
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2")
                .description("工作流引擎服务接口")
                .contact(contact)
                .version(VERSION)
                .build();
    }


//    @Override
//    public void apply(ModelPropertyContext context) {
//        //如果不支持swagger的话，直接返回
//        if (!swaggerEnable) {
//            return;
//        }
//
//        //获取当前字段的类型
//        final Class fieldType = context.getBeanPropertyDefinition().get().getField().getRawType();
//
//        //为枚举字段设置注释
//        descForEnumFields(context, fieldType);
//    }
//
//    @Override
//    public boolean supports(DocumentationType documentationType) {
//        return false;
//    }
//
//    /**
//     * 为枚举字段设置注释
//     */
//    private void descForEnumFields(ModelPropertyContext context, Class fieldType) {
//        Optional<ApiModelProperty> annotation = Optional.absent();
//
//        if (context.getAnnotatedElement().isPresent()) {
//            annotation = annotation
//                    .or(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
//        }
//        if (context.getBeanPropertyDefinition().isPresent()) {
//            annotation = annotation.or(Annotations.findPropertyAnnotation(
//                    context.getBeanPropertyDefinition().get(),
//                    ApiModelProperty.class));
//        }
//
//        //没有@ApiModelProperty 或者 notes 属性没有值，直接返回
//        if (!annotation.isPresent() || StringUtils.isBlank((annotation.get()).notes())) {
//            return;
//        }
//
//        //@ApiModelProperties中的notes指定的class类型
//        Class rawPrimaryType;
//        try {
//            rawPrimaryType = Class.forName((annotation.get()).notes());
//        } catch (ClassNotFoundException e) {
//            //如果指定的类型无法转化，直接忽略
//            return;
//        }
//
//        //如果对应的class是一个@SwaggerDisplayEnum修饰的枚举类，获取其中的枚举值
//        Object[] subItemRecords = null;
//        SwaggerDisplayEnum swaggerDisplayEnum = AnnotationUtils
//                .findAnnotation(rawPrimaryType, SwaggerDisplayEnum.class);
//        if (null != swaggerDisplayEnum && Enum.class.isAssignableFrom(rawPrimaryType)) {
//            subItemRecords = rawPrimaryType.getEnumConstants();
//        }
//        if (null == subItemRecords) {
//            return;
//        }
//
//
//        final List<String> displayValues = Arrays.stream(subItemRecords).filter(Objects::nonNull).map(item -> {
//            return item.toString();
//        }).filter(Objects::nonNull).collect(Collectors.toList());
//
//        String joinText = " (" + String.join("; ", displayValues) + ")";
//        try {
//            Field mField = ModelPropertyBuilder.class.getDeclaredField("description");
//            mField.setAccessible(true);
//            joinText = mField.get(context.getBuilder()) + joinText;
//        } catch (Exception e) {
//        }
//
//        final ResolvedType resolvedType = context.getResolver().resolve(fieldType);
//        context.getBuilder().description(joinText).type(resolvedType);
//    }


}
