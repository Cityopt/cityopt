package eu.cityopt.opt.io;

/**
 * A non-throwing version of ImportBuilder.
 */
public interface RobustImportBuilder extends ImportBuilder {
    /**
     * Like {@link ImportBuilder#add} but does not throw.
     */
    @Override
    void add(JacksonBinder.Item item);
}
