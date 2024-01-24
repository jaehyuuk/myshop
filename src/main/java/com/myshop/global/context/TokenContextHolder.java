package com.myshop.global.context;

public class TokenContextHolder  {
    private static final ThreadLocal<TokenContext> contextHolder = new ThreadLocal<>();

    /**
     * @name : setContext
     * @param : context
     * @description : TokenContext 셋팅한다.
     */
    public static void setContext(TokenContext context) {
        contextHolder.set(context);
    }

    /**
     * @name : getContext/product/supply/price/target/item/check
     * @return : TokenContext
     * @description : TokenContext를 얻는다.
     */
    public static TokenContext getContext() {
        TokenContext context = contextHolder.get();
        if (context == null) {
            context = new TokenContext();
            contextHolder.set(context);
        }
        return context;
    }

    /**
     * @name : clearContext
     * @description : context를 clear 한다.
     */
    public static void clearContext() {
        contextHolder.remove();
    }
}
