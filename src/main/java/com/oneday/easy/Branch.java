package com.oneday.easy;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author zhuangzf
 * @date 2019/5/13 18:34
 */
public class Branch<T> {
    private T value;
    private Supplier fastSupplier;


    private static final Branch<?> EMPTY = new Branch<>();

    public static <T> Branch<T> of(T value) {
        return new Branch<>(value);
    }

    /**
     * 构建快速 if else 语义
     *
     * @param value
     * @return
     */
    public static Branch<Boolean> ofFast(Boolean value) {
        return new Branch<>(value);
    }

    public static <T> Branch<T> of() {
        return new Branch<>();
    }


    private Branch(T value) {
        this();
        this.value = value;
    }

    private Branch() {

    }

    /**
     * 开启链式匹配模式，只要结果是true，则都会执行相应操作
     *
     * @return
     */
    public Chain<T> chain() {
        Chain<T> chain = new Chain<>();
        chain.value = this.value;
        return chain;
    }

    /**
     * 开启If else 模式
     * if(true){
     *     return true;
     * }else{
     *     return false;
     * }
     *
     * @return
     */
    public IfOrElse<T> whenIf() {
        return new IfOrElse<>(value);
    }


    /**
     * 此方法和orElse 构成 if else 语义中需要返回值的处理逻辑
     *
     * @param thenGet
     * @return
     */
    public Branch<Boolean> ifTrue(Supplier thenGet) {
        if ((Boolean) this.value) {
            this.fastSupplier = thenGet;
        }
        return (Branch<Boolean>) this;
    }
    public Object orElse(Supplier orElseSupplier) {
        if (!(Boolean) this.value) {
            return orElseSupplier.get();
        }
        return fastSupplier.get();
    }

    /**
     * if(true){
     *      iDo.justDo();
     * }
     *
     * @param iDo
     */
    public void consumeTrue(IDo iDo) {
        if ((Boolean) this.value) {
            iDo.justDo();
        }
    }


    /**
     * if(true){
     *     throw new RuntimeException
     * }
     *
     *
     * @param exceptionSupplier
     * @param <X>
     * @throws X
     */
    public <X extends Throwable> void thenThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if ((Boolean) this.value) {
            throw exceptionSupplier.get();
        }
    }


    /**
     * 责任链模式 todo 1.加上过滤器模式 2.某个节点抛异常如何处理 3.考虑单独一个类
     *
     * @param <T>
     */
    public final class Chain<T> {

        private Supplier<Boolean> condition;
        private T value;
        private boolean hadMatch;

        public Chain<T> match(Predicate<T> predicate) {
            this.condition = () -> predicate.test(value);
            return this;
        }

        public Chain<T> ifTrue(Supplier resultSupplier) {
            if (checkCondition()) {
                hadMatch = true;
                resultSupplier.get();
            }
            return this;
        }

        public Chain<T> ifTrue(Consumer<T> resultConsume) {
            if (checkCondition()) {
                resultConsume.accept(value);
            }
            return this;
        }


        public boolean checkCondition() {
            return condition.get();
        }

        public void orElse(Supplier resultSupplier) {
            if (!hadMatch) {
                resultSupplier.get();
            }
        }

        public void orElse(Consumer<T> resultConsumer) {
            if (!hadMatch) {
                resultConsumer.accept(value);
            }
        }

    }

    /**
     * 严格遵循if else 语法模式
     *
     * @param <T>
     */
    public static final class IfOrElse<T> {
        private Supplier<Boolean> condition;
        private boolean hadMatch;

        private T value;
        private Object result;

        public IfOrElse(T value) {
            this.value = value;
        }

        public IfOrElse<T> match(Predicate<T> predicate) {
            this.condition = () -> predicate.test(value);
            return this;
        }


        public IfOrElse<T> ifTrue(Supplier resultSupplier) {
            //只允许匹配一次
            if (!hadMatch && condition.get()) {
                this.result = resultSupplier.get();
                hadMatch = true;
            }
            return this;
        }

        public IfOrElse<T> ifTrue(Consumer<T> resultConsume) {
            if (!hadMatch && condition.get()) {
                resultConsume.accept(value);
                hadMatch = true;
            }
            return this;
        }

        public <X extends Throwable> IfOrElse<T> orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            if (result == null && !hadMatch) {
                throw exceptionSupplier.get();
            }
            return this;
        }

        public Object orElseGet(Supplier resultSupplier) {
            if (!hadMatch) {
                return resultSupplier.get();
            }
            return this.result;
        }


    }

}
