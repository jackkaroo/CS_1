package com.ksondzyk.utilities;

import com.ksondzyk.entities.Message;
import com.ksondzyk.entities.Packet;
import com.ksondzyk.exceptions.PacketDamagedException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class PacketReceiver {


    public Packet receive(InputStream serverInputStream) throws IOException {

        try {

        byte[] maxPacketBuffer = new byte[Packet.packetMaxSize];
        while (serverInputStream.read()!=Packet.bMagic);
        serverInputStream.read(maxPacketBuffer);

        ByteBuffer byteBuffer = ByteBuffer.wrap(maxPacketBuffer);
        Integer wLen = byteBuffer.getInt(Packet.packetPartFirstLengthWithoutwLen-1);

        byte[] fullPacket = new byte[Packet.packetPartFirstLength + Message.BYTES_WITHOUT_MESSAGE + wLen];
        byteBuffer.get(fullPacket);
        System.out.println("Received");
        System.out.println(Arrays.toString(fullPacket) + "\n");

        Packet packet = new Packet(fullPacket,"tcp");

        System.err.println(CipherMy.decode(packet.getBMsq().getMessage()));

//                if (serverInputStream.available() < Integer.BYTES+9)
//                    throw new PacketDamagedException("The packet is not full");
//
//                serverInputStream.read(buffer, 0, 1 + 8);
//
//                packetBytes.write(buffer, 0, 1 + 8); //wrote first part
//
//                int wLen;
//
//                serverInputStream.read(buffer, 0, Integer.BYTES);
//                byteBuffer = ByteBuffer.allocate(Integer.BYTES);
//                byteBuffer.put(buffer, 0, Integer.BYTES);
//                byteBuffer.rewind();
//                wLen = byteBuffer.getInt();
//
//                packetBytes.write(buffer, 0, Integer.BYTES);
//
//                if (serverInputStream.available() < wLen+Short.BYTES+Short.BYTES)
//                    throw new PacketDamagedException("The packet is not full");
//                serverInputStream.read(buffer, 0, Short.BYTES);
//
//                byteBuffer = ByteBuffer.allocate(Short.BYTES);
//                byteBuffer.put(buffer, 0, Short.BYTES);
//
//                byteBuffer.rewind();
//                Short crc16_1_real = byteBuffer.getShort();
//                Short crc16_1_test = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetBytes.toByteArray());
//
//                if (!crc16_1_real.equals(crc16_1_test))
//                    throw new PacketDamagedException(crc16_1_real, crc16_1_test);
//                packetBytes.write(buffer, 0, Short.BYTES);
//
//                serverInputStream.read(buffer, 0, wLen);
//                packetBytes.write(buffer, 0, wLen);
//
//                byteBuffer = ByteBuffer.allocate(wLen);
//                byteBuffer.put(buffer, 0, wLen);
//
//                Short crc16_2_test = (short) CRC.calculateCRC(CRC.Parameters.CRC16, byteBuffer.array());
//
//                serverInputStream.read(buffer, 0, Short.BYTES);
//                byteBuffer = ByteBuffer.allocate(Short.BYTES);
//                byteBuffer.put(buffer, 0, Short.BYTES);
//                byteBuffer.rewind();
//
//                Short crc16_2_real = byteBuffer.getShort();
//
//                if (!crc16_2_real.equals(crc16_2_test))
//                    throw new PacketDamagedException(crc16_2_real, crc16_2_test);
//                packetBytes.write(buffer, 0, Short.BYTES);
//            }
//            catch(PacketDamagedException e){
//                System.err.println("The packet has not been fully sent");
//                return (PacketGenerator.newPacket(1, "END"));
//            }
       // Packet packet = new Packet(buffer);

        return packet;
}catch(PacketDamagedException e){
            PacketGenerator packetGenerator = new PacketGenerator();
            System.err.println("The packet has not been fully sent");
                return (packetGenerator.newPacket(1, "END"));
}
    //}
    }

}



