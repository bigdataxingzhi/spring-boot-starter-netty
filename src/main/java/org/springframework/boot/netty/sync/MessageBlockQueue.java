package org.springframework.boot.netty.sync;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.boot.netty.listener.Message;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Author: huoxingzhi
 * Date: 2020/12/10
 * Email: hxz_798561819@163.com
 */
public class MessageBlockQueue {


    private static final ConcurrentHashMap<Integer, BlockingQueue<Message>> blockingQueueConcurrentHashMap;

    private static final Object lockObject = new Object();

    public static Boolean isNeedInit = true;

    public static volatile Boolean shutdown = Boolean.FALSE;

    static  {
        blockingQueueConcurrentHashMap = new ConcurrentHashMap<>(16);
    }

    public static void initMessageQueue(int mapCapacity,int queueCapacity){

        if(isNeedInit){
            synchronized(MessageBlockQueue.lockObject){
                if(isNeedInit){
                    for (int init = 0; init < mapCapacity; init++){
                        blockingQueueConcurrentHashMap.put(init,new LinkedBlockingQueue<>(queueCapacity));
                    }
                    isNeedInit = false;
                }
            }
        }


    }

    public static ConcurrentHashMap<Integer, BlockingQueue<Message>> blockingQueueMap(){
        return blockingQueueConcurrentHashMap;
    }


    public static Message getMessage(int mapIndex, long timeout, TimeUnit timeUnit){
        Message pollMessage = null;
        try {
            pollMessage = blockingQueueMap().get(mapIndex).poll(timeout, timeUnit);
        } catch (InterruptedException e) {
        }
        return pollMessage;
    }

    public static void setMessage(int mapIndex, Message message){
        try {
            blockingQueueMap().get(mapIndex).put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long getSize(Set<Integer> mapIndexes){
        long totalCount;
        synchronized(MessageBlockQueue.lockObject) {
            totalCount = mapIndexes.stream().mapToInt(mapIndex -> blockingQueueMap().get(mapIndex).size()).asLongStream().sum();
        }
        return totalCount;
    }



    public static int getSizeByMapIndex(int mapIndex) {
        return blockingQueueConcurrentHashMap.get(mapIndex).size();
    }
}
