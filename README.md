# ViaMCP-Reborn
Repository to keep up with ViaVersion on MCP (Originally from https://github.com/LaVache-FR/ViaMCP)

# Installation / Intergation
You will need to add some lines of code before you can use ViaMCP
You also need to add libraries into dependencies before using this!

# Main Class
Add this to the main class of your client (aka injection function)

try
{
  ViaMCP.getInstance().start();
}
catch (Exception e)
{
  e.printStackTrace();
}

# NetworkManager
You will need to replace 2 functions in NetworkManager.java

1: (Name may vary, but should be func_181124_a or contain (Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)lazyloadbase.getValue()))

Add: 
if (p_initChannel_1_ instanceof SocketChannel && ViaMCP.getInstance().getVersion() != ViaMCP.PROTOCOL_VERSION)
{
  UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
  new ProtocolPipelineImpl(user);
  p_initChannel_1_.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new VREncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new VRDecodeHandler(user));
}
                
After:
p_initChannel_1_.pipeline().addLast((String)"timeout", (ChannelHandler)(new ReadTimeoutHandler(30))).addLast((String)"splitter", (ChannelHandler)(new MessageDeserializer2())).addLast((String)"decoder", (ChannelHandler)(new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String)"prepender", (ChannelHandler)(new MessageSerializer2())).addLast((String)"encoder", (ChannelHandler)(new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String)"packet_handler", (ChannelHandler)networkmanager);

2: setCompressionTreshold (Yes, minecraft devs cannot spell 'Threshold') 

Comment out: (Old Decoder)
this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));

Replace with: (New Decoder)
NettyUtil.decodeEncodePlacement(channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));

Comment out: (Old Encoder)
this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold))

Replace with: (New Encoder)
NettyUtil.decodeEncodePlacement(channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));

# GuiMainMenu
You will need to add a button to access the protocol switcher

In addSingleplayerMultiplayerButtons() function you will need to add the following code:
this.buttonList.add(new GuiButton(69, 5, 5, 90, 20, "Protocol"));

In actionPerformed(GuiButton button) function you will need to add the following code:
if (button.id == 69)
{
  this.mc.displayGuiScreen(new GuiProtocolSelector(this));
}

# Finishing
You should now be able to use ViaMCP
If you have any problems, DM Hideri#9003 on discord!
