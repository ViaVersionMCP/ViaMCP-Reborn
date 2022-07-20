# ViaMCP Reborn
Repository to keep up with ViaVersion on MCP (Originally from https://github.com/LaVache-FR/ViaMCP)

I have been requested (held at gun point) to also add credits to ViaForge in the README: https://github.com/FlorianMichael/ViaForge

# [(NEW!) Discord Link](https://discord.gg/pBMSy6uVdf)
# [Exporting without needing ViaMCP in JSON](https://github.com/Foreheadchann/ViaMCP-Reborn#exporting-without-jar-files)
# [Optional 1.8.x Block Sound Fixes](https://github.com/Foreheadchann/ViaMCP-Reborn#block-sound-fixes)

# 1.7.x Protocols
Yes, i know they are not working right now, do not make a pull request to remove them, as i am not going to remove them.

# Selecting Version
Choose the version folder that corresponds with your client version

# Installation
Firstly, you will need to add the listed libraries into your dependencies in IntelliJ or Eclipse

Dependencies (Included inside ``libraries`` folder)
```
ViaVersion-[ver].jar > ViaVersion > https://github.com/ViaVersion/ViaVersion
ViaBackwards-[ver].jar > ViaBackwards > https://github.com/ViaVersion/ViaBackwards
ViaRewind-[ver].jar > ViaRewind > https://github.com/ViaVersion/ViaRewind
ViaSnakeYaml-[ver].jar > SnakeYaml > https://bitbucket.org/snakeyaml/snakeyaml
```

Secondly, you need to add code that allows you to actually use ViaMCP

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

# Version Control
You will need to add a button to access the protocol switcher (or alternatively use the version slider under this section)

In ``addSingleplayerMultiplayerButtons()`` function add (if in GuiMainMenu):

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

### Version Slider
You can also use a version slider to control ViaMCP versions

```java
this.buttonList.add(ViaMCP.getInstance().asyncSlider);
```

# Attack Order Fixes

Class: Minecraft.java

Function: clickMouse()

1.8.x

Replace 
```java
public void clickMouse() {
        if (this.leftClickCounter <= 0) {
            switch (this.objectMouseOver.typeOfHit) {
                case ENTITY:
                    this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);
                    break;

                case BLOCK:
                    BlockPos blockpos = this.objectMouseOver.getBlockPos();

                    if (this.world.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                        this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                        break;
                    }

                case MISS:
                default:
                    if (this.playerController.isNotCreative()) {
                        this.leftClickCounter = 10;
                    }
            }
        }
    }
```

```java
public void clickMouse() {
        if (this.leftClickCounter <= 0) {
            boolean oldSwing = ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion();
            if (oldSwing) {
                this.player.swingItem();
            }

            switch (this.objectMouseOver.typeOfHit) {
                case ENTITY:
                    this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);
                    break;

                case BLOCK:
                    BlockPos blockpos = this.objectMouseOver.getBlockPos();

                    if (this.world.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                        this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                        break;
                    }

                case MISS:
                default:
                    if (this.playerController.isNotCreative()) {
                        this.leftClickCounter = 10;
                    }
            }
            if (!oldSwing) {
                this.player.swingItem();
            }
        }
    }
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
