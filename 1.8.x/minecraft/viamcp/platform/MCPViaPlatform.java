package viamcp.platform;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import viamcp.ViaMCP;
import viamcp.utils.FutureTaskId;
import viamcp.utils.JLoggerToLog4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MCPViaPlatform implements ViaPlatform<UUID>
{
    private final Logger logger = new JLoggerToLog4j(LogManager.getLogger("ViaMCP"));

    private final MCPViaConfig config;
    private final File dataFolder;
    private final ViaAPI<UUID> api;

    public MCPViaPlatform(File dataFolder)
    {
        Path configDir = dataFolder.toPath().resolve("ViaVersion");
        config = new MCPViaConfig(configDir.resolve("viaversion.yml").toFile());
        this.dataFolder = configDir.toFile();
        api = new MCPViaAPI();
    }

    @Override
    public Logger getLogger()
    {
        return logger;
    }

    @Override
    public String getPlatformName()
    {
        return "ViaMCP";
    }

    @Override
    public String getPlatformVersion()
    {
        return String.valueOf(ViaMCP.PROTOCOL_VERSION);
    }

    @Override
    public boolean isProxy()
    {
        return ViaPlatform.super.isProxy();
    }

    @Override
    public String getPluginVersion()
    {
        return "4.4.3";
    }

    @Override
    public FutureTaskId runAsync(Runnable runnable)
    {
        return new FutureTaskId(CompletableFuture.runAsync(runnable, ViaMCP.getInstance().getAsyncExecutor()).exceptionally(throwable ->
        {
                if (!(throwable instanceof CancellationException))
                {
                    throwable.printStackTrace();
                }

                return null;
            })
        );
    }

    @Override
    public FutureTaskId runSync(Runnable runnable)
    {
        return new FutureTaskId(ViaMCP.getInstance().getEventLoop().submit(runnable).addListener(errorLogger()));
    }

    @Override
    public PlatformTask<?> runSync(Runnable runnable, long ticks)
    {
        return new FutureTaskId(ViaMCP.getInstance().getEventLoop().schedule(() -> runSync(runnable), ticks * 50, TimeUnit.MILLISECONDS).addListener(errorLogger()));
    }

    @Override
    public PlatformTask<?> runRepeatingSync(Runnable runnable, long ticks)
    {
         return new FutureTaskId(ViaMCP.getInstance().getEventLoop().scheduleAtFixedRate(() -> runSync(runnable), 0, ticks * 50, TimeUnit.MILLISECONDS).addListener(errorLogger()));
    }

    private <T extends Future<?>> GenericFutureListener<T> errorLogger()
    {
        return future ->
        {
            if (!future.isCancelled() && future.cause() != null)
            {
                future.cause().printStackTrace();
            }
        };
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers()
    {
        return new ViaCommandSender[0]; // Empty
    }

    @Override
    public void sendMessage(UUID uuid, String s)
    {
        // Don't even know why this needs to be overridden
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s)
    {
        return false;
    }

    @Override
    public boolean disconnect(UserConnection connection, String message)
    {
        return ViaPlatform.super.disconnect(connection, message);
    }

    @Override
    public boolean isPluginEnabled()
    {
        return true;
    }

    @Override
    public ViaAPI<UUID> getApi()
    {
        return api;
    }

    @Override
    public ViaVersionConfig getConf()
    {
        return config;
    }

    @Override
    public ConfigurationProvider getConfigurationProvider()
    {
        return config;
    }

    @Override
    public File getDataFolder()
    {
        return dataFolder;
    }

    @Override
    public void onReload()
    {
        logger.info("ViaVersion was reloaded? (How did that happen)");
    }

    @Override
    public JsonObject getDump()
    {
        return new JsonObject();
    }

    @Override
    public boolean isOldClientsAllowed()
    {
        return true;
    }

    @Override
    public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses()
    {
        return ViaPlatform.super.getUnsupportedSoftwareClasses();
    }

    @Override
    public boolean hasPlugin(String s)
    {
        return false;
    }
}
