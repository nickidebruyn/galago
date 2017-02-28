/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.messages;

import com.bruynhuis.galago.app.BaseApplication;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The message manager can be used to pass messages between classes in the system.
 *
 * @author nidebruyn
 */
public class MessageManager {
    
    private BaseApplication application;
    private ArrayList<MessageListener> messageListeners = new ArrayList<MessageListener>();
    
    public MessageManager(BaseApplication baseApplication) {
        this.application = baseApplication;
    }
    
    public void destroy() {
        messageListeners.clear();
    }

    public void addMessageListener(MessageListener messageListener) {
        this.messageListeners.add(messageListener);
    }
    
    public void removeMessageListener(MessageListener messageListener) {
        this.messageListeners.remove(messageListener);        
    }
    
    public void sendMessage(String message, Object object) {
        for (Iterator<MessageListener> it = messageListeners.iterator(); it.hasNext();) {
            MessageListener messageListener = it.next();
            messageListener.messageReceived(message, object);
        }
    }
    
}
