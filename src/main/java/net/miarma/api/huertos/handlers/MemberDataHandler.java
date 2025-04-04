package net.miarma.api.huertos.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.http.QueryParams;
import net.miarma.api.huertos.entities.MemberEntity;
import net.miarma.api.huertos.services.MemberService;
import net.miarma.api.util.JsonUtil;

@SuppressWarnings("unused")
public class MemberDataHandler {
    private final MemberService memberService;

    public MemberDataHandler(Pool pool) {
        this.memberService = new MemberService(pool);
    }

    public void getAll(RoutingContext ctx) {
        QueryParams params = QueryParams.from(ctx);

        memberService.getAll(params)
            .onSuccess(members -> JsonUtil.sendJson(ctx, ApiStatus.OK, members))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void getById(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.request().getParam("id"));

        memberService.getById(id)
            .onSuccess(member -> {
                if (member == null) {
                    JsonUtil.sendJson(ctx, ApiStatus.NOT_FOUND, null, "Member not found");
                } else {
                    JsonUtil.sendJson(ctx, ApiStatus.OK, member);
                }
            })
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void create(RoutingContext ctx) {
        MemberEntity member = Constants.GSON.fromJson(ctx.body().asString(), MemberEntity.class);
        System.out.println(member);

        memberService.create(member)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.CREATED, result))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void update(RoutingContext ctx) {
        MemberEntity member = Constants.GSON.fromJson(ctx.body().asString(), MemberEntity.class);

        memberService.update(member)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }

    public void delete(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.request().getParam("id"));

        memberService.delete(id)
            .onSuccess(result -> JsonUtil.sendJson(ctx, ApiStatus.NO_CONTENT, null))
            .onFailure(err -> JsonUtil.sendJson(ctx, ApiStatus.fromException(err), null, err.getMessage()));
    }
}