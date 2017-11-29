package com.tongbanjie.raft.core.remoting.support.netty;

import com.alibaba.fastjson.JSON;
import com.tongbanjie.raft.core.enums.RemotingCommandState;
import com.tongbanjie.raft.core.enums.RemotingCommandType;
import com.tongbanjie.raft.core.peer.RaftPeer;
import com.tongbanjie.raft.core.protocol.*;
import com.tongbanjie.raft.core.remoting.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import sun.rmi.runtime.Log;

/***
 *
 * @author banxia
 * @date 2017-11-22 15:15:49
 */
public class RemotingCommandProcessor {

    private RaftPeer peer;

    public RemotingCommandProcessor(RaftPeer peer) {
        this.peer = peer;
    }


    /**
     * 选举请求
     *
     * @param ctx
     * @param msg
     */
    public void electionVoteHandler(ChannelHandlerContext ctx, RemotingCommand msg) {


        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setRequestId(msg.getRequestId());
        remotingCommand.setCommandType(RemotingCommandType.ELECTION.getValue());

        try {
            ElectionRequest electionRequest = JSON.parseObject(msg.getBody(), ElectionRequest.class);


            ElectionResponse electionResponse = this.peer.electionVoteHandler(electionRequest);
            remotingCommand.setBody(JSON.toJSONString(electionResponse));
            remotingCommand.setState(RemotingCommandState.SUCCESS.getValue());

        } catch (Exception e) {
            e.printStackTrace();
            remotingCommand.setState(RemotingCommandState.SUCCESS.getValue());
            remotingCommand.setBody("无效的数据");
        }

        ctx.writeAndFlush(remotingCommand);

    }

    /**
     * 追加日志
     *
     * @param ctx
     * @param msg
     */
    public void appendEntriesHandler(ChannelHandlerContext ctx, RemotingCommand msg) {


        String body = msg.getBody();
        AppendEntriesRequest appendEntriesRequest = JSON.parseObject(body, AppendEntriesRequest.class);


        AppendEntriesResponse appendEntriesResponse = this.peer.appendEntriesHandler(appendEntriesRequest);

        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setRequestId(msg.getRequestId());
        remotingCommand.setState(RemotingCommandState.SUCCESS.getValue());
        remotingCommand.setBody(JSON.toJSONString(appendEntriesResponse));
        remotingCommand.setCommandType(RemotingCommandType.APPEND.getValue());
        ctx.writeAndFlush(remotingCommand);
    }
}
