package com.QAPP.api.utility;
import com.QAPP.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendSMS {

    @Autowired
    private static SmsService service;
    @Autowired
    private static SimpMessagingTemplate webSocket;

    public static void sendSMS(SmsPojo sms) {
        try{
            service.send(sms);
        }
        catch(Exception e){
            //webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": Error sending the SMS: "+e.getMessage());
            throw e;
        }
        //webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": SMS has been sent!: "+sms.getTo());
   }

}
