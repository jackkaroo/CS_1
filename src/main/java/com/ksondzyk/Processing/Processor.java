package com.ksondzyk.Processing;

import com.ksondzyk.HTTP.dao.Table;
import com.ksondzyk.Server;
import com.ksondzyk.entities.Message;
import com.ksondzyk.entities.Packet;
import com.ksondzyk.exceptions.PacketDamagedException;
import com.ksondzyk.utilities.Properties;
import org.json.JSONObject;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.concurrent.*;


public class Processor implements Callable{
    Packet packet;
    static OutputStream os;

    public Processor(Packet packet){
        this.packet = packet;
        run();
    }

    static ExecutorService executorPool = Executors.newFixedThreadPool(Server.processingThreadCount);

    static JSONObject answerMessage;

    public static boolean idPresent(int id){
        try {
            Table.selectOneById(id).getInt("id");
            return true;
        } catch (SQLException throwables) {
            return false;
        }
    }

    private static JSONObject answer(int cType,JSONObject jsonObject) throws PacketDamagedException {
        answerMessage = new JSONObject();
        try {
            String category;
            String title;
            int id;
            int quantity;
            int price;
        switch (cType) {
            case 1:
                id = (int) jsonObject.get("id");
                title = Table.selectOneById(id).getString("title");
                quantity = Table.selectOneById(id).getInt("quantity");
                price = Table.selectOneByTitle(title, Properties.tableName).getInt("price");

                answerMessage.put("id",id);
                answerMessage.put("title",title);
                answerMessage.put("quantity",quantity);
                answerMessage.put("price",price);

                break;

            case 2:
                category = (String) jsonObject.get("category");
                title = (String) jsonObject.get("title");
                price = (int) jsonObject.get("price");
                quantity = Integer.parseInt(String.valueOf(jsonObject.get("quantity")));

                Table.insert(category,title,quantity,price);

                id = Table.selectOneByTitle(title,Properties.tableName).getInt("id");

                answerMessage.put("id",id);

                break;

            case 3:
                id = Integer.parseInt(String.valueOf(jsonObject.get("id")));

                if(jsonObject.has("category")) {
                    category = (String) jsonObject.get("category");
                }
                else
                    category = Table.selectOneById(id).getString("category");

                if(jsonObject.has("title")) {
                    title = (String) jsonObject.get("title");
                }
                else
                    title = Table.selectOneById(id).getString("title");

                if(jsonObject.has("price")) {
                    price = Integer.parseInt(String.valueOf(jsonObject.get("price")));
                }
                else
                    price = Table.selectOneById(id).getInt("price");

                if(jsonObject.has("quantity")) {
                    quantity = Integer.parseInt(String.valueOf(jsonObject.get("quantity")));
                }
                else
                    quantity = Table.selectOneById(id).getInt("quantity");

                Table.update(id,title,category,price,quantity);

                break;
            case 4:
                id = Integer.parseInt(String.valueOf(jsonObject.get("id")));
                Table.delete(id);
                break;

            default:
                throw new PacketDamagedException("Unknown command");

        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return answerMessage;
    }
    public static Future<Message> process(Packet packet, OutputStream ostream) {
        os = ostream;
        Callable<Message> processingAsync = new Processor(packet);
        return executorPool.submit(processingAsync);
    }
    public static Future<JSONObject> process(JSONObject jsonObject) {
        Packet packet = new Packet(jsonObject);
        Callable<JSONObject> processingAsync = new Processor(packet);
        return executorPool.submit(processingAsync);
    }
    public void run(){
        synchronized (packet) {

            int cType = Integer.parseInt(String.valueOf(packet.getJsonObject().get("cType")));
            try {
            answerMessage = answer(cType,packet.getJsonObject());

            } catch (PacketDamagedException e) {
                e.printStackTrace();
            }
        }
    }
//    private static String process(Packet packet, OutputStream os) throws PacketDamagedException {
//
//        return CipherMy.decode(answerMessage.getMessage());
//    }

    @Override
    public JSONObject call() throws Exception {
        try {
            Thread.sleep(1000 * Server.secondsPerTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return answerMessage;
    }
    public static void shutdown() {
        executorPool.shutdown();
        try {
            if (!executorPool.awaitTermination(60, TimeUnit.SECONDS))
                System.err.println("ProcessingAsync threads didn't finish in 60 seconds!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
