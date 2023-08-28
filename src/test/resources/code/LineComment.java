import java.nio.file.Path;

public class LineComment{
    public void method() {
        // Load WorldEdit
        try {
            Class<?> clazz = Class.forName("me.wiefferink.areashop.handlers." + weVersion);
            // Check if we have a NMSHandler class at that location.
            if(WorldEditInterface.class.isAssignableFrom(clazz)) { // Make sure it actually implements WorldEditInterface
                worldEditInterface = (WorldEditInterface)clazz.getConstructor(AreaShopInterface.class).newInstance(this); // Set our handler
            }
        } catch(Exception e) {
            error("Could not load the handler for WorldEdit (tried to load " + weVersion + "), report this problem to the author: " + ExceptionUtils.getStackTrace(e));
            error = true;
            weVersion = null;
        }
    }

}
