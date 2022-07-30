package com.langong.emcservice.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.langong.emcservice.annotation.Wrapper;

import java.lang.reflect.Field;

public class WrapperHelp {
    public static <T> QueryWrapper<T> getWrapper(T t) throws IllegalAccessException {
        QueryWrapper<T> qw = new QueryWrapper<>();
        Class<?> cls = t.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Object val = f.get(t);
            if ("serialVersionUID".equals(f.getName())) {
                continue;
            }
            Wrapper w = f.getAnnotation(Wrapper.class);
            String colName = StringUtil.toUnderlineCase(f.getName());
            if (w == null) {
                if (val != null && !"".equals(val)) {
                    qw.eq(colName, val);
                }
            } else {
                if (w.Nullable() && (val == null || "".equals(val))) {
                    continue;
                }
                switch (w.type()) {
                    case EQ:
                        qw.eq(colName, val);
                        break;
                    case NE:
                        qw.ne(colName, val);
                        break;
                    case GT:
                        qw.gt(colName, val);
                        break;
                    case GE:
                        qw.ge(colName, val);
                        break;
                    case LT:
                        qw.lt(colName, val);
                        break;
                    case LE:
                        qw.le(colName, val);
                        break;
                    case LIKE:
                        qw.like(colName, val);
                        break;
                    case NOT_LIKE:
                        qw.notLike(colName, val);
                        break;
                    case LIKE_LEFT:
                        qw.likeLeft(colName, val);
                        break;
                    case LIKE_RIGHT:
                        qw.likeRight(colName, val);
                        break;
                    case IN:
                        qw.in(colName, val);
                        break;
                    case NOT_IN:
                        qw.notIn(colName, val);
                        break;
                    default:
                        break;
                }
                //orderBy
                if(w.ASC()){
                    qw.orderByAsc(colName);
                }else if(w.DESC()){
                    qw.orderByDesc(colName);
                }
            }
        }

        return qw;
    }
}
