package com.github.webee.urirouter.middlewares;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.github.webee.urirouter.core.RouteContext;
import com.github.webee.urirouter.core.Handler;
import com.github.webee.urirouter.core.Middleware;
import com.github.webee.urirouter.core.URIRouters;
import com.github.webee.urirouter.handlers.ActivityHandler;
import com.github.webee.urirouter.handlers.ArbitrationProxyActivity;

/**
 * Created by webee on 17/2/20.
 */

public class LoginMiddleware implements Middleware {
    private String loginPath;
    private IsLoginChecker isLoginChecker;

    public LoginMiddleware(String loginPath, IsLoginChecker isLoginChecker) {
        this.loginPath = loginPath;
        this.isLoginChecker = isLoginChecker;
    }

    @Override
    public Handler process(final Handler next) {
        return new MiddlewareHandler(this, next) {
            @Override
            public void handling(Handler next, RouteContext ctx) {
                if (!ctx.request.uri.getPath().equals(loginPath)) {
                    if (!isLoginChecker.check(ctx.context)) {
                        Log.d("LOGIN MID", "not login");
                        // 跳转到登录
                        Bundle data = new Bundle();
                        data.putParcelable(ArbitrationProxyActivity.EXTRA_TARGET, ctx.request.uri);
                        data.putBundle(ArbitrationProxyActivity.EXTRA_TARGET_CTX_DATA, ctx.data.bundle);
                        data.putBundle(ArbitrationProxyActivity.EXTRA_TARGET_REQ_DATA, ctx.request.data);

                        data.putParcelable(ArbitrationProxyActivity.EXTRA_ARBITRATOR, Uri.parse(loginPath));

                        URIRouters.route(ActivityHandler.ARBITRATION_PROXY_PATH)
                                .withContext(ctx.context)
                                .withCtxData(ctx.data)
                                .withReqData(data)
                                .open();
                        return;
                    }
                }
                next.handle(ctx);
            }
        };
    }

    public interface IsLoginChecker {
        boolean check(android.content.Context context);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "loginPath='" + loginPath + '\'' +
                '}';
    }
}
