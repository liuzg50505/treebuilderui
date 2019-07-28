import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;

public class TestEventBus {

    public static class Person {
        public String name;
        public int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class A{
        @Subscribe
        public void onEvent1(Person person) {
            System.out.println(person);
        }
    }

    @Test
    public void test1() {
        EventBus eventBus = new EventBus("E1");
        A a = new A();
        eventBus.register(a);
        eventBus.register(a);

        eventBus.post(new Person("a",2));

    }
}
