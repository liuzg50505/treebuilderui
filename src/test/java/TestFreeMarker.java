import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class TestFreeMarker {

    @Test
    public void test1() throws IOException, TemplateException {
        Map<String, Object> context = new HashMap<>();
        context.put("collectionvar","controller.persons");
        context.put("itemvar","person");
        context.put("indexvar","i");
        String templatestr = "Foreach(\n" +
                "    collection: ${collectionvar},\n" +
                "    builder: (${itemvar}, ${indexvar}) {\n" +
                "        ${template.generateCode()??}\n" +
                "    }\n" +
                ")\n";
        Template template = new Template("strTpl", templatestr, new Configuration(new Version("2.3.23")));
        StringWriter result = new StringWriter();
        template.process(context, result);
        System.out.println(result.toString());

    }

}
