package test.domain.actions.object;

public class NewProductObject extends ActionObject {
    public NewProductObject(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "{ name='" + name + '\'' +
                '}';
    }
}
