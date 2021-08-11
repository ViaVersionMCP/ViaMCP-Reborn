# ViaMCP-Reborn
Repository to keep up with ViaVersion on MCP (Originally from https://github.com/LaVache-FR/ViaMCP)

I have been requested (held at gun point) to also add credits to ViaForge in the README: https://github.com/FlorianMichael/ViaForge

# 1.7.x Protocols
Yes, i know they are not working right now, do not make a pull request to remove them, as i am not going to remove them.

# Small note about version
If you are using 1.8.x as a client base, use viamcp src folder

If you are using 1.12.2 as a client base, use viamcp1_12 src folder and rename it to viamcp

# Installation
You will need to add some lines of code before you can use ViaMCP

You also need to add libraries into dependencies before using this!

# Main Class
Add this to the main class of your client (aka injection function)

```java
try
{
  ViaMCP.getInstance().start();
}
catch (Exception e)
{
  e.printStackTrace();
}
```

# NetworkManager
You will need to replace 2 functions in NetworkManager.java

1: (Name may vary, but should be func_181124_a or contain (Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)lazyloadbase.getValue()))

After:

(1.8.x)

```java
p_initChannel_1_.pipeline().addLast((String)"timeout", (ChannelHandler)(new ReadTimeoutHandler(30))).addLast((String)"splitter", (ChannelHandler)(new MessageDeserializer2())).addLast((String)"decoder", (ChannelHandler)(new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String)"prepender", (ChannelHandler)(new MessageSerializer2())).addLast((String)"encoder", (ChannelHandler)(new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String)"packet_handler", (ChannelHandler)networkmanager);
```

(1.12.x)

```java
p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(EnumPacketDirection.CLIENTBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(EnumPacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);
```

Add: 

```java
if (p_initChannel_1_ instanceof SocketChannel && ViaMCP.getInstance().getVersion() != ViaMCP.PROTOCOL_VERSION)
{
  UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
  new ProtocolPipelineImpl(user);
  p_initChannel_1_.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new VREncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new VRDecodeHandler(user));
}
```

2: setCompressionTreshold (Yes, minecraft devs cannot spell 'Threshold') 

Comment out: (Old Decoder)

```java
this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));
```

Replace with: (New Decoder)

```java
NettyUtil.decodeEncodePlacement(channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));
```

Comment out: (Old Encoder)

```java
this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold))
```

Replace with: (New Encoder)

```java
NettyUtil.decodeEncodePlacement(channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));
```

# GuiMainMenu
You will need to add a button to access the protocol switcher

In addSingleplayerMultiplayerButtons() function you will need to add the following code:

```java
this.buttonList.add(new GuiButton(69, 5, 5, 90, 20, "Protocol"));
```

In actionPerformed(GuiButton button) function you will need to add the following code:

```java
if (button.id == 69)
{
  this.mc.displayGuiScreen(new GuiProtocolSelector(this));
}
```

# Finishing
You should now be able to use ViaMCP

If you have any problems, DM Hideri#9003 on discord!
