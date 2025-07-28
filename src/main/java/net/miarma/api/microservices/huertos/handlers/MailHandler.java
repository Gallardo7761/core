package net.miarma.api.microservices.huertos.handlers;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.ConfigManager;
import net.miarma.api.common.Constants;
import net.miarma.api.common.http.ApiStatus;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.microservices.huertos.entities.MemberEntity;
import net.miarma.api.microservices.huertos.mail.Attachment;
import net.miarma.api.microservices.huertos.mail.ImapReader;
import net.miarma.api.microservices.huertos.mail.Mail;
import net.miarma.api.microservices.huertos.mail.MailCredentials;
import net.miarma.api.microservices.huertos.mail.MailManager;
import net.miarma.api.microservices.huertos.services.MemberService;

public class MailHandler {
	private final MailManager mailManager;
    private final WorkerExecutor mailExecutor;
    private final MemberService memberService;

    public MailHandler(Vertx vertx, Pool pool) {
        this.mailManager = new MailManager(vertx);
        this.mailExecutor = vertx.createSharedWorkerExecutor("mail-worker-pool", 10);
        this.memberService = new MemberService(pool);
    }

    public void sendMail(RoutingContext ctx) {
        Mail mail = Json.decodeValue(ctx.body().asString(), Mail.class);
        List<Attachment> attachments = mail.getAttachments();

        resolveMailCredentials(ctx, res -> {
            if (res.failed()) {
                ctx.response().setStatusCode(ApiStatus.UNAUTHORIZED.getCode()).end("Unauthorized: " + res.cause().getMessage());
                return;
            }

            MailCredentials creds = res.result();

            if (!attachments.isEmpty()) {
                mailManager.sendEmailWithAttachment(mail, attachments, creds.username(), creds.password(), result -> {
                    if (result.succeeded()) {
                        ctx.response().setStatusCode(ApiStatus.OK.getCode()).end("Email has been sent successfully");
                    } else {
                        Constants.LOGGER.error(result.cause().getMessage());
                        ctx.response().setStatusCode(ApiStatus.INTERNAL_SERVER_ERROR.getCode()).end("Error sending email: " + result.cause().getMessage());
                    }
                });
            } else {
                mailManager.sendEmail(mail, creds.username(), creds.password(), result -> {
                    if (result.succeeded()) {
                        ctx.response().setStatusCode(ApiStatus.OK.getCode()).end("Email has been sent successfully");
                    } else {
                        Constants.LOGGER.error(result.cause().getMessage());
                        ctx.response().setStatusCode(ApiStatus.INTERNAL_SERVER_ERROR.getCode()).end("Error sending email: " + result.cause().getMessage());
                    }
                });
            }
        });
    }


    public void getFolder(RoutingContext ctx) {
    	String folderParam = ctx.pathParam("folder");

        if (folderParam == null) {
            ctx.response().setStatusCode(ApiStatus.BAD_REQUEST.getCode()).end("Missing folder name");
            return;
        }
        
        resolveMailCredentials(ctx, res -> {
            if (res.failed()) {
                ctx.response().setStatusCode(ApiStatus.UNAUTHORIZED.getCode()).end("Unauthorized: " + res.cause().getMessage());
                return;
            }

            MailCredentials creds = res.result();

            mailExecutor.executeBlocking(() ->
                ImapReader.listInboxEmails(folderParam, creds.username(), creds.password())
            ).onSuccess(mails -> {
                ctx.response().putHeader("Content-Type", "application/json").end(Json.encodePrettily(mails));
            }).onFailure(err -> {
                ctx.response().setStatusCode(500).end("Error reading folder: " + err.getMessage());
            });
        });

    }
    
    public void getMail(RoutingContext ctx) {
    	String folderParam = ctx.pathParam("folder");
        String indexParam = ctx.pathParam("index");

        if (folderParam == null) {
            ctx.response().setStatusCode(ApiStatus.BAD_REQUEST.getCode()).end("Missing folder name");
            return;
        }
        int index;
        try {
            index = Integer.parseInt(indexParam);
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Index must be a number");
            return;
        }

        resolveMailCredentials(ctx, res -> {
            if (res.failed()) {
                ctx.response().setStatusCode(ApiStatus.UNAUTHORIZED.getCode()).end("Unauthorized: " + res.cause().getMessage());
                return;
            }

            MailCredentials creds = res.result();

            mailExecutor.executeBlocking(() ->
                ImapReader.getEmailByIndex(folderParam, creds.username(), creds.password(), index)
            ).onSuccess(mail -> {
                ctx.response().putHeader("Content-Type", "application/json").end(Json.encodePrettily(mail));
            }).onFailure(err -> {
                ctx.response().setStatusCode(500).end("Error getting email: " + err.getMessage());
            });
        });

    }
    
    private void resolveMailCredentials(RoutingContext ctx, Handler<AsyncResult<MailCredentials>> handler) {
        String token = ctx.request().getHeader("Authorization");
        System.out.println(token);
        
        if (token == null || !token.startsWith("Bearer ")) {
            handler.handle(Future.failedFuture("Missing or invalid Authorization header"));
            return;
        }

        int userId = JWTManager.getInstance().extractUserId(token.replace("Bearer ", ""));
        System.out.println(userId);
        
        memberService.getById(userId).onComplete(ar -> {
            if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            MemberEntity member = ar.result();
            String email = member.getEmail();
            System.out.println(email);
            
            if (email == null || !email.contains("@")) {
                handler.handle(Future.failedFuture("Invalid member email"));
                return;
            }

            String role = email.split("@")[0];
            System.out.println(role);
            String password = ConfigManager.getInstance().getStringProperty("smtp.password." + role);
            System.out.println(password);

            if (password == null) {
                handler.handle(Future.failedFuture("No password found for user"));
                return;
            }

            handler.handle(Future.succeededFuture(new MailCredentials(email, password)));
        });
    }



}
