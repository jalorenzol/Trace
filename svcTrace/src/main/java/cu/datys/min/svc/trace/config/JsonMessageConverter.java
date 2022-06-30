package cu.datys.min.svc.trace.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
@Slf4j
public class JsonMessageConverter implements MessageConverter {

    private ObjectMapper mapper;

    public JsonMessageConverter() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException {
        String json;
        try {
            json = mapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new MessageConversionException("Message cannot be parsed. ", e);
        }
        TextMessage message = session.createTextMessage();
        message.setText(json);
        return message;
    }

    @Override
    public JSONObject fromMessage(Message messageAis) throws JMSException, MessageConversionException {
        String payload;
        JSONObject aisMssgs = null;
        if (messageAis instanceof BytesMessage) {
            BytesMessage bMessage = (BytesMessage) messageAis;
            int payloadLength = (int) bMessage.getBodyLength();
            byte payloadBytes[] = new byte[payloadLength];
            bMessage.readBytes(payloadBytes);
            payload = new String(payloadBytes);
            aisMssgs = new JSONObject(payload);
        }
        return aisMssgs;
    }
}
