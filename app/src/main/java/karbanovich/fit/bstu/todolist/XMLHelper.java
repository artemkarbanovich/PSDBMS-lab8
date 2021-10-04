package karbanovich.fit.bstu.todolist;

import android.content.Context;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class XMLHelper {

    private static final String FILE_NAME = "tasks.xml";


    public static void writeXML(Context context, ArrayList<Task> tasks) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("Tasks");
            doc.appendChild(root);

            for(Task t : tasks) {
                Element Details = doc.createElement("Task");
                root.appendChild(Details);

                Element description = doc.createElement("Description");
                description.appendChild(doc.createTextNode(t.getDescription()));
                Details.appendChild(description);

                Element category = doc.createElement("Category");
                category.appendChild(doc.createTextNode(t.getCategory()));
                Details.appendChild(category);

                Element date = doc.createElement("Date");
                date.appendChild(doc.createTextNode(t.getDate()));
                Details.appendChild(date);
            }

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer tran = tranFactory.newTransformer();
            DOMSource src = new DOMSource(doc);

            File file = new File(context.getFilesDir(), FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);

            StreamResult result = new StreamResult(fos);
            tran.transform(src, result);

            fos.close();
        } catch(Exception e) { }
    }

    public static ArrayList<Task> readXML(Context context) {
        if(!isExist(context))
            return new ArrayList<>();

        Task currentTask = null;
        boolean isEntry = false;
        String textValue = "";
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser xpp = xppf.newPullParser();

            File file = new File(context.getFilesDir(), FILE_NAME);
            FileInputStream fis = new FileInputStream(file);

            xpp.setInput(fis, null);
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if("Task".equalsIgnoreCase(tagName)) {
                            isEntry = true;
                            currentTask = new Task();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(isEntry) {
                            if("Task".equalsIgnoreCase(tagName)) {
                                tasks.add(currentTask);
                                isEntry = false;
                            } else if("Description".equalsIgnoreCase(tagName))
                                currentTask.setDescription(textValue);
                            else if("Category".equalsIgnoreCase(tagName))
                                currentTask.setCategory(textValue);
                            else if("Date".equalsIgnoreCase(tagName))
                                currentTask.setDate(textValue);
                        }
                        break;
                    default:
                }
                eventType = xpp.next();
            }
        } catch (Exception e ) { }
        return tasks;
    }

    public static void deleteTask(Context context, Task taskToDel) {
        ArrayList<Task> tasks = XMLHelper.readXML(context);
        ArrayList<Task> tasksToFile;

        tasksToFile = (ArrayList<Task>) tasks.stream()
                .filter(t -> !t.equals(taskToDel))
                .collect(Collectors.toList());

        XMLHelper.writeXML(context, tasksToFile);
    }

    public static ArrayList<Task> getTaskByCategory(Context context, String category) {
        if(!isExist(context))
            return new ArrayList<>();

        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(file);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            XPathExpression xpeDescription = xpath.compile("/Tasks/Task[Category='" + category + "']/Description/text()");
            XPathExpression xpeDate = xpath.compile("/Tasks/Task[Category='" + category + "']/Date/text()");
            NodeList nodesDescription = (NodeList) xpeDescription.evaluate(doc, XPathConstants.NODESET);
            NodeList nodesDate = (NodeList) xpeDate.evaluate(doc, XPathConstants.NODESET);

            for(int i = 0; i < nodesDescription.getLength(); i++) {
                Task t = new Task();
                t.setDescription(nodesDescription.item(i).getNodeValue());
                t.setCategory(category);
                t.setDate(nodesDate.item(i).getNodeValue());
                tasks.add(t);
            }

            return tasks;
        } catch (Exception e ) { }
        return tasks;
    }

    public static void xslTransform(Context context) {
        if(!isExist(context))
            return;

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(context.getResources().openRawResource(R.raw.taskstemplate));
            Transformer transformer = factory.newTransformer(xslt);
            Source xml = new StreamSource(new File(context.getFilesDir(), FILE_NAME));
            transformer.transform(xml, new StreamResult(new File(context.getFilesDir(), "xsltresult.xml")));
        } catch (Exception e) { }
    }

    private static boolean isExist(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.exists();
    }
}
