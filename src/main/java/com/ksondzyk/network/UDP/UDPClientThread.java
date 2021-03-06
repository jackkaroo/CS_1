package com.ksondzyk.network.UDP;


import com.ksondzyk.entities.Packet;
import com.ksondzyk.utilities.PacketGenerator;
import com.ksondzyk.utilities.Properties;
import lombok.Getter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientThread implements Runnable{

    private DatagramSocket socket;
    private InetAddress addr;
    private final PacketGenerator packetGenerator;
    private static int counter = 0;
    private final int clientID = counter++;

    public UDPClientThread() {
        packetGenerator = new PacketGenerator();
        System.out.println("Запустимо клієнт з номером " + clientID);
        try {
            addr = InetAddress.getByName("localhost");
            socket = new DatagramSocket();
        }
        catch (IOException e) {
            // Сокет має бути закритий при будь якій помилці
            // крім помилки конструктора сокета
            socket.close();
        }
        // Якщо все відбудеться нормально сокет буде закрито
        // в методі run() потоку.
    }



    public void run() {

            try {

                for (int i = 0; i < 4; i++) {

                    Packet packet = packetGenerator.newPacket(i);
                    byte[] sentData = packet.getData();

                    DatagramPacket datagramPacketSent = new DatagramPacket(sentData,sentData.length,addr, Properties.PORT);
                    Packet packetReceived;

                    for(int j = 0; j<5;j++) {

                        socket.send(datagramPacketSent);
                        System.out.println("sent from "+Thread.currentThread());


                        packetReceived = UDPPacketReceiver.receive(socket);


                        System.err.println("Answer from server: "+packetReceived.getDecodedMessage());


                        if(packetReceived.getBMsq().getCType()!=-1){
                            break;
                        }else {
                            System.out.println("The packet was corrupted, sending it once more...");
                        }

                    }

                }

            } catch (IOException e) {
                System.err.println("Поток завершив роботу");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Завжди закриває:
                socket.close();
            }
        }
    }



