package org.example.c01_easymodbus4j;

import com.github.zengfr.easymodbus4j.ModbusConfs;
import com.github.zengfr.easymodbus4j.client.ModbusClient4TcpMaster;
import com.github.zengfr.easymodbus4j.client.ModbusClientTcpFactory;
import com.github.zengfr.easymodbus4j.common.util.ConsoleUtil;
import com.github.zengfr.easymodbus4j.common.util.ScheduledUtil;
import com.github.zengfr.easymodbus4j.handle.impl.ModbusMasterResponseHandler;
import com.github.zengfr.easymodbus4j.handler.ModbusResponseHandler;
import com.github.zengfr.easymodbus4j.processor.ModbusMasterResponseProcessor;
import com.github.zengfr.easymodbus4j.sender.ChannelSender;
import com.github.zengfr.easymodbus4j.sender.ChannelSenderFactory;
import io.netty.channel.Channel;
import org.example.c01_easymodbus4j.processer.ExampleModbusMasterResponseProcessor;

public class TestModbus4j {

  private static ModbusClient4TcpMaster modbusClient;
  public static void main(String[] args) throws Exception {
    initClient();
    sendData();
//		scheduleToSendData();
    //1\ ChannelSender to send data to machine
    //2\ ExampleModbusMasterResponseProcessor  to receive resp data.

  }
  private static void initClient() throws Exception {
    String host="192.168.2.101";
    int port=502;
    long sleep=3000;
    short transactionIdentifierOffset=1;
		ModbusConfs.MASTER_SHOW_DEBUG_LOG = true;
    ModbusMasterResponseProcessor masterProcessor=new ExampleModbusMasterResponseProcessor(transactionIdentifierOffset);
    ModbusResponseHandler responseHandler = new ModbusMasterResponseHandler(masterProcessor);;
    modbusClient = ModbusClientTcpFactory.getInstance().createClient4Master(host, port, responseHandler);

    Thread.sleep(sleep);
  }

  private static void sendData() {
    Channel channel = modbusClient.getChannel();
    if(channel==null||(!channel.isActive())||!channel.isOpen()||!channel.isWritable())
      return;
    ChannelSender sender = ChannelSenderFactory.getInstance().get(channel);
    //short unitIdentifier=1;
    //ChannelSender sender2 =new ChannelSender(channel, unitIdentifier);

    int startAddress=100;
    int quantityOfCoils=0;
    try {
      for(;;) {
        sender.writeSingleCoilAsync(startAddress, false);
        Thread.sleep(5000);
        sender.writeSingleCoilAsync(startAddress, true);
        Thread.sleep(5000);
      }
//      sender.readCoilsAsync(startAddress, quantityOfCoils);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void scheduleToSendData() {

    Runnable runnable = () ->{
      ConsoleUtil.clearConsole(true);
      Channel channel = modbusClient.getChannel();
      if(channel==null||(!channel.isActive())||!channel.isOpen()||!channel.isWritable())
        return;
      ChannelSender sender = ChannelSenderFactory.getInstance().get(channel);
      //short unitIdentifier=1;
      //ChannelSender sender2 =new ChannelSender(channel, unitIdentifier);

      int startAddress=0;
      int quantityOfCoils=4;
      try {
        sender.readCoilsAsync(startAddress, quantityOfCoils);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    };
    int sleep=1000;
    ScheduledUtil.scheduleAtFixedRate(runnable, sleep * 5);
//		ScheduledUtil.getInstance().scheduleAtFixedRate(runnable, sleep * 5);

  }
}
