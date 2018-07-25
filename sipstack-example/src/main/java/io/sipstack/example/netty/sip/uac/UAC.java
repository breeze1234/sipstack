/**
 * 
 */
package io.sipstack.example.netty.sip.uac;

import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.header.ContactHeader;
import io.pkts.packet.sip.header.ContactHeader.Builder;
import io.pkts.packet.sip.header.FromHeader;
import io.pkts.packet.sip.header.ViaHeader;
import io.sipstack.example.netty.sip.SimpleSipStack;
import io.sipstack.netty.codec.sip.Connection;
import io.pkts.packet.sip.address.SipURI;

/**
 * @author jonas@jonasborjesson.com
 */
public final class UAC {

    private final SimpleSipStack stack;

    // we will be using the same from for all requests
    private final FromHeader from = FromHeader.with().user("+1111").host("10.100.57.139").port(5060).build();

    /**
     * 
     */
    public UAC(final SimpleSipStack stack) {
        this.stack = stack;
    }

    public void send() throws Exception {
        final String host = "10.100.56.118";
        final int port = 12345;
        final Connection connection = this.stack.connect(host, port);
        this.from.setParameter(Buffers.wrap("tag"), FromHeader.generateTag());
        final ViaHeader via =
                ViaHeader.with().host(host).port(port).branch(ViaHeader.generateBranch()).transportUDP().build();
        
//        ContactHeader ch = (ContactHeader) Builder.internalBuild(null,null);
        
        final SipRequest invite = SipRequest.invite("sip:+13127771449@10.100.56.118:12345;transport=udp;").from(UAC.this.from).via(via)
//        		.contact()
        		.build();
      
        System.out.println("invite:"+invite.toString());
        connection.send(invite);
    }

    public static void main(final String[] args) throws Exception {
        final String ip = "10.100.57.139";
        final int port = 5060;
        final String transport = "udp";

        final UACHandler handler = new UACHandler();
        final SimpleSipStack stack = new SimpleSipStack(handler, ip, port);
        final UAC uac = new UAC(stack);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.err.println("ok, sending");
                    uac.send();
                } catch (final Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
        stack.run();
    }

}
