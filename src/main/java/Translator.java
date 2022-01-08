import org.json.JSONObject;

public class Translator {
    public Translator(JSONObject umlProject) {
        JSONDB.DATABASE = new JSONDB(umlProject);

        Linker linker = new Linker();

        linker.link();
    }
}
