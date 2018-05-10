package cn.chinogo.search.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 监听商品添加
 * 
 * @author chinotan
 */
public class ItemAddListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ItemAddListener.class);

    @Override
    public void onMessage(Message message) {

    }
}
