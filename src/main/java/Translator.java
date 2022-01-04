import org.json.JSONObject;

public class Translator {
    JSONDB database;

    public Translator(JSONObject umlProject) {
        database = new JSONDB(umlProject);

        Linker linker = new Linker();

        linker.link(database);
    }
}
