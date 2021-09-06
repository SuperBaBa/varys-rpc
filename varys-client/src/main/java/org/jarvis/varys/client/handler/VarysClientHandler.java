package org.jarvis.varys.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jarvis.varys.dto.VarysResponse;


public class VarysClientHandler extends SimpleChannelInboundHandler<VarysResponse> {

    private VarysResponse varysResponse;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VarysResponse msg) throws Exception {
        System.out.println("channel read ...");
        varysResponse = msg;
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public VarysResponse getVarysResponse() {
        return varysResponse;
    }
}
