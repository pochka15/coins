import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;


/**
 * Strategy which is used to generate jooq objects
 */
public class JooqGenerationStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        String name = super.getJavaClassName(definition, mode);

//        PostgreSql Stuff
        if (name.startsWith("Pg")) return name;

        return depluralize(name);
    }

    private static String depluralize(String name) {
        if (name.endsWith("ies")) {
            return name.substring(0, name.length() - 3) + 'y';
        } else if (name.endsWith("sses")) {
            return name.substring(0, name.length() - 2);
        } else if (name.endsWith("s") && !name.endsWith("ss")) {
            return name.substring(0, name.length() - 1);
        } else if (name.endsWith("List")) {
            return name.substring(0, name.length() - 4);
        } else {
            return name;
        }
    }
}
