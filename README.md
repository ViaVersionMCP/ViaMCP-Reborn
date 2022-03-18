# ViaMCP Reborn
Repository to keep up with ViaVersion on MCP (Originally from https://github.com/LaVache-FR/ViaMCP)

I have been requested (held at gun point) to also add credits to ViaForge in the README: https://github.com/FlorianMichael/ViaForge

# [Exporting without needing ViaMCP in JSON](https://github.com/Foreheadchann/ViaMCP-Reborn#exporting-without-jar-files)
# [Optional 1.8.x Block Sound Fixes](https://github.com/Foreheadchann/ViaMCP-Reborn#block-sound-fixes)

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
  
  // Only use one of the following
  ViaMCP.getInstance().initAsyncSlider(); // For top left aligned slider
  ViaMCP.getInstance().initAsyncSlider(x, y, width (min. 110), height (recommended 20)); // For custom position and size slider
}
catch (Exception e)
{
  e.printStackTrace();
}
```

# NetworkManager
You will need to change 2 functions in NetworkManager.java

1: Name may vary, but should be ``func_181124_a``, ``createNetworkManagerAndConnect`` or contain ``(Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)lazyloadbase.getValue())``

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
    p_initChannel_1_.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new MCPEncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new MCPDecodeHandler(user));
}
```

Which should look like this afterwards (1.8.x for example):

```java
p_initChannel_1_.pipeline().addLast((String)"timeout", (ChannelHandler)(new ReadTimeoutHandler(30))).addLast((String)"splitter", (ChannelHandler)(new MessageDeserializer2())).addLast((String)"decoder", (ChannelHandler)(new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String)"prepender", (ChannelHandler)(new MessageSerializer2())).addLast((String)"encoder", (ChannelHandler)(new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String)"packet_handler", (ChannelHandler)networkmanager);

if (p_initChannel_1_ instanceof SocketChannel && ViaMCP.getInstance().getVersion() != ViaMCP.PROTOCOL_VERSION)
{
    UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
    new ProtocolPipelineImpl(user);
    p_initChannel_1_.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new MCPEncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new MCPDecodeHandler(user));
}
```

2: setCompressionTreshold (Yes, minecraft devs cannot spell 'Threshold') 

Decoder Switch

Comment out ``this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));`` and paste in:

```java
NettyUtil.decodeEncodePlacement(channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));
```

Encoder Switch

Comment out ``this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold))`` and paste in:

```java
NettyUtil.decodeEncodePlacement(channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));
```

# GuiMainMenu
You will need to add a button to access the protocol switcher (or alternatively use the version slider under this section)

In ``addSingleplayerMultiplayerButtons()`` function add:

```java
this.buttonList.add(new GuiButton(69, 5, 5, 90, 20, "Version"));
```

In ``actionPerformed()`` function add:

```java
if (button.id == 69)
{
  this.mc.displayGuiScreen(new GuiProtocolSelector(this));
}
```

# Attack Order Fixes

Class: Minecraft.java
Function: clickMouse()

1.8.x

Replace ``this.thePlayer.swingItem();`` on the 1st line in the if-clause with:

```java
AttackOrder.sendConditionalSwing(this.objectMouseOver);
```

Replace ``this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);`` in the switch in case ``ENTITY`` with:

```java
AttackOrder.sendFixedAttack(this.thePlayer, this.objectMouseOver.entityHit);
```

1.12.2

Replace ``this.player.swingArm(EnumHand.MAIN_HAND);`` at the last line in the else if-clause with:

```java
AttackOrder.sendConditionalSwing(this.objectMouseOver, EnumHand.MAIN_HAND);
```

Replace ``this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);`` in the switch in case ``ENTITY`` with:

```java
AttackOrder.sendFixedAttack(this.thePlayer, this.objectMouseOver.entityHit, EnumHand.MAIN_HAND);
```

# Version Slider
You can also use a version slider to control ViaMCP versions

```java
this.buttonList.add(ViaMCP.getInstance().asyncSlider);
```

# Block Sound Fixes
Block Placement

Replace all code in ``onItemUse`` function in the ``ItemBlock`` class with:

```java
return FixedSoundEngine.onItemUse(this, stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
```

Block Breaking

Replace all code in ``destroyBlock`` function in the ``World`` class with:

```java
return FixedSoundEngine.destroyBlock(this, pos, dropBlock);
```

# Exporting Without JAR Files

- This should fix most peoples issues with dependencies (usually NoClassDefFoundError or ClassNotFoundException)

1: First export your client normally

2: Open your client .jar file with an archive program (winrar or 7zip for example)

3: Also open all libraries with the selected archive program (ViaBackwards, ViaRewind, ViaSnakeYml and ViaVersion)

4: From ViaBackwards drag and drop ``assets`` and ``com`` folders to your client .jar

5: From ViaRewind drag and drop ``assets`` and ``de`` folders to your client .jar

6: From ViaSnakeYaml drag and drop ``org`` folder to your client .jar

7: From ViaVersion drag and drop ``assets``, ``com`` and ``us`` folders to your client .jar
 
8: Then save and close, now your client should be working correctly ;)

Credits: mordolpl (Discord)

# Finishing
You should now be able to use ViaMCP

If you have any problems, DM Hideri#9003 on discord!
