package org.example.c02_ModbusMasterTcp;

import com.digitalpetri.modbus.codec.Modbus;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.*;
import com.digitalpetri.modbus.responses.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/***
 * modbus TCP协议Java通讯读取例子
 *
 *
 */
public class ModbusMasterTCPDemo {

  static ModbusTcpMaster master;

  /**
   * 获取TCP协议的Master
   *
   * @return
   */
  public static void initModbusTcpMaster() {
    if (master == null) {
      // 创建配置
      ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder("192.168.2.101").setPort(502).setInstanceId("1").build();
      master = new ModbusTcpMaster(config);
    }
  }

  /***
   * 释放资源
   */
  public static void release() {
    if (master != null) {
      master.disconnect();
    }
    Modbus.releaseSharedResources();
  }

  /**
   * 读取Coils开关量
   *
   * @param address
   *            寄存器开始地址
   * @param quantity
   *            数量
   * @param unitId
   *            ID
   * @return 读取值
   * @throws InterruptedException
   *             异常
   * @throws ExecutionException
   *             异常
   */
  public static Boolean readCoils(int address, int quantity, int unitId)
      throws InterruptedException, ExecutionException {
    Boolean result = null;
    CompletableFuture<ReadCoilsResponse> future = master.sendRequest(new ReadCoilsRequest(address, quantity),
        unitId);
    ReadCoilsResponse readCoilsResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
    if (readCoilsResponse != null) {
      ByteBuf buf = readCoilsResponse.getCoilStatus();
      result = buf.readBoolean();
      ReferenceCountUtil.release(readCoilsResponse);
    }
    return result;
  }

  /**
   * 读取readDiscreteInputs开关量
   *
   * @param address
   *            寄存器开始地址
   * @param quantity
   *            数量
   * @param unitId
   *            ID
   * @return 读取值
   * @throws InterruptedException
   *             异常
   * @throws ExecutionException
   *             异常
   */
  public static Boolean readDiscreteInputs(int address, int quantity, int unitId)
      throws InterruptedException, ExecutionException {
    Boolean result = null;
    CompletableFuture<ReadDiscreteInputsResponse> future = master
        .sendRequest(new ReadDiscreteInputsRequest(address, quantity), unitId);
    ReadDiscreteInputsResponse discreteInputsResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
    if (discreteInputsResponse != null) {
      ByteBuf buf = discreteInputsResponse.getInputStatus();
      result = buf.readBoolean();
      ReferenceCountUtil.release(discreteInputsResponse);
    }
    return result;
  }

  /**
   * 读取HoldingRegister数据
   *
   * @param address
   *            寄存器地址
   * @param quantity
   *            寄存器数量
   * @param unitId
   *            id
   * @return 读取结果
   * @throws InterruptedException
   *             异常
   * @throws ExecutionException
   *             异常
   */
  public static Number readHoldingRegisters(int address, int quantity, int unitId)
      throws InterruptedException, ExecutionException {
    Number result = null;
    CompletableFuture<ReadHoldingRegistersResponse> future = master
        .sendRequest(new ReadHoldingRegistersRequest(address, quantity), unitId);
    ReadHoldingRegistersResponse readHoldingRegistersResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
    if (readHoldingRegistersResponse != null) {
      ByteBuf buf = readHoldingRegistersResponse.getRegisters();
      result = buf.readFloat();
      ReferenceCountUtil.release(readHoldingRegistersResponse);
    }
    return result;
  }

  /**
   * 读取InputRegisters模拟量数据
   *
   * @param address
   *            寄存器开始地址
   * @param quantity
   *            数量
   * @param unitId
   *            ID
   * @return 读取值
   * @throws InterruptedException
   *             异常
   * @throws ExecutionException
   *             异常
   */
  public static Number readInputRegisters(int address, int quantity, int unitId)
      throws InterruptedException, ExecutionException {
    Number result = null;
    CompletableFuture<ReadInputRegistersResponse> future = master
        .sendRequest(new ReadInputRegistersRequest(address, quantity), unitId);
    ReadInputRegistersResponse readInputRegistersResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
    if (readInputRegistersResponse != null) {
      ByteBuf buf = readInputRegistersResponse.getRegisters();
      result = buf.readDouble();
      ReferenceCountUtil.release(readInputRegistersResponse);
    }
    return result;
  }

  public static void main(String[] args) {
    try {
      // 初始化资源
      initModbusTcpMaster();

      // 执行操作

      // 读取开关量
      System.out.println(readCoils(100, 1, 1));

      CompletableFuture<WriteSingleCoilResponse> future = master.sendRequest(new WriteSingleCoilRequest(100, false), 1);
      WriteSingleCoilResponse response = future.get();
      int value = response.getValue();
      System.out.println(value);

      /*System.out.println(readDiscreteInputs(0, 1, 1));
      System.out.println(readDiscreteInputs(1, 1, 1));

      // 读取模拟量
      System.out.println(readHoldingRegisters(0, 2, 1));
      System.out.println(readHoldingRegisters(2, 2, 1));
      System.out.println(readHoldingRegisters(4, 2, 1));
      System.out.println(readInputRegisters(2, 4, 1));
      System.out.println(readInputRegisters(6, 4, 1));*/

      // 释放资源
      release();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}