package com.aviatickets.profile.util.http;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@SuppressWarnings("unused")
public class HttpUtils {

    public static <T> PageableResult<T> pageableOk(Page<T> page) {
        Meta meta = composeMetaObject(page);
        return new PageableResult<>(page.getContent(), meta);
    }

    private static <T> Meta composeMetaObject(Page<T> page) {
        return new Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getSort().stream().findFirst().map(Sort.Order::getProperty).orElse(null),
                page.getSort().stream().findFirst().map(Sort.Order::getDirection).orElse(null)
        );
    }

}
