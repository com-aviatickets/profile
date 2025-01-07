package com.aviatickets.profile.util.http;

import java.util.Collection;

public record PageableResult<T>(
        Collection<? extends T> data,
        Meta meta
) {

}
