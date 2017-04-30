package lv.emes.libraries.tools.xml;

import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.lists.MS_StringList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static lv.emes.libraries.file_system.MS_FileSystemTools.deleteFile;
import static lv.emes.libraries.file_system.MS_FileSystemTools.getTmpDirectory;
import static lv.emes.libraries.tools.MS_StringTools.getTabSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSXMLFileTest {
    private static final String PATH_TO_XML_FILE = getTmpDirectory() + "MSXMLFileTest.xml";
    private static final String PATH_TO_XML_FILE2 = getTmpDirectory() + "MSXMLFileTest2.xml";
    private static final String PATH_TO_XML_FILE3 = getTmpDirectory() + "MSXMLFileTest3.xml";
    private static MS_StringList FOOD_NAME_LIST = null;
    private static MS_XML file1;
    private static MS_XML file2;
    private static MS_XML file3;

    @Test
    public void test01ParseSimpleXMLFile() throws ParserConfigurationException, SAXException, IOException {
        MS_XML file = file1;
        assertEquals("breakfast_menu", file.getRootElementName());
        //everything starting from first <food> tag and ending with very last </food> tag
        //<breakfast_menu><food>...</food><food>...</food></breakfast_menu>
        MS_XMLElementNodeList allFoods = file.getNodesByTagName("food");
        assertEquals(5, allFoods.count());
        assertEquals("food", allFoods.getTag());
        for (int i = 0; i < allFoods.count(); i++) {
            //everything that begins with <food> and ends with </food> in
            //<food>...</food>
            MS_XMLElementNode aCurrentFood = allFoods.get(i);
            assertEquals("food", aCurrentFood.getTagName());
            assertEquals(FOOD_NAME_LIST.get(i), aCurrentFood.getFirstChild("name").getValue());
            assertEquals("name", aCurrentFood.getFirstChild("name").getTagName());
            assertEquals(1, aCurrentFood.getChildList("name").count());
            assertEquals(FOOD_NAME_LIST.get(i), aCurrentFood.getChildList("name").get(0).getValue());
        }

        //just testing iterating
        allFoods.forEachItem((node, ind) -> {
            assertEquals("food", node.getTagName()); //like assertEquals("food", aCurrentFood.getTagName());
            assertEquals("name", node.getFirstChild("name").getTagName()); //like assertEquals("name", aCurrentFood.getFirstChild("name").getTagName());
            assertEquals(1, node.getChildList("name").count());
            assertEquals(FOOD_NAME_LIST.get(ind), node.getFirstChild("name").getValue());
        });

        assertEquals("Light Belgian waffles covered with strawberries and whipped cream", allFoods.get(1).getFirstChild("description").getValue());
        assertEquals("600", allFoods.get(3).getFirstChild("calories").getValue());
        assertEquals("950", allFoods.get(4).getFirstChild("calories").getValue());
        try {
            allFoods.get(5).getFirstChild("calories").getValue();
        } catch (MS_XMLElementNodeList.NodeNotFoundException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void test02DocumentObject() throws ParserConfigurationException, SAXException, IOException {
        MS_XML file = new MS_XML(PATH_TO_XML_FILE);
        Document doc = file.getDocument();
        //TODO test on different machine, cause following commented code fails
//        assertEquals("1.0", doc.getXmlVersion());
//        assertEquals("UTF-8", doc.getInputEncoding());
//        doc.setXmlVersion("1.1");
//        assertEquals("1.1", doc.getXmlVersion());
        assertEquals("file:/" + PATH_TO_XML_FILE, doc.getBaseURI());
    }

    @Test
    public void test03WrongParsing() throws ParserConfigurationException, SAXException, IOException {
        MS_XML file = file1;
        boolean exceptionCaught = false;
        try {
            file.getNodesByTagName("princesses");
        } catch (MS_XML.NodesNotFoundException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        MS_XMLElementNodeList prices = file.getNodesByTagName("price");
        assertEquals(null, prices.get(2).getFirstChild("child"));
    }

    @Test
    public void test04ParseAndEditElements() throws ParserConfigurationException, SAXException, IOException {
        MS_XML file = file1;
        MS_XMLElementNodeList allFoods = file.getNodesByTagName("food");
        allFoods.forEachItem((item, ind) -> {
            MS_XMLElementNode nameOfFood = item.getFirstChild("name");
            nameOfFood.toElement().setTextContent(FOOD_NAME_LIST.get(allFoods.count() + ind));
            assertEquals(FOOD_NAME_LIST.get(allFoods.count() + ind), nameOfFood.getValue());
        });

        //now to check for a new values
        allFoods.forEachItem((node, index) -> {
//            System.out.println(node);
            assertEquals(FOOD_NAME_LIST.get(index + 5), node.getFirstChild("name").getValue());
        });
    }

    @Test
    public void test05ParseComplicatedXMLFile() throws IOException, ParserConfigurationException, SAXException {
        MS_XML file = file2;
//        System.out.println(file.toString());
        assertEquals("Students", file.getRootElementName());
        MS_XMLElementNodeList allTheStudents = file.getNodesByTagName("Student");
        MS_XMLElementNode studentMaris = allTheStudents.get(0);
        MS_XMLElementNode studentJanis = allTheStudents.get(1);
        assertEquals("Māris", studentMaris.getFirstChild("name").getValue());
        assertEquals("Jānis", studentJanis.getFirstChild("name").getValue());

        MS_XMLElementNode university = studentMaris.getFirstChild("university");
        assertEquals("RTU", university.getAttribute("name"));
        assertEquals("university", university.getTagName());
        assertEquals("DITF", university.getFirstChild("faculty").getValue());

        //Janis has no university in records because there is only 1 university registered
        assertEquals(1, file.getNodesByTagName("university").size());
        assertEquals(null, studentJanis.getFirstChild("university"));
    }

    @Test
    public void test06ParseXMLFileWithOnly1Tag() throws IOException, ParserConfigurationException, SAXException {
        assertEquals("", file3.getNodesByTagName(file3.getRootElementName()).get(0).getValue());
    }

    @Test
    public void test07NodeIsNotAnElement() throws IOException, ParserConfigurationException, SAXException {
        MS_XMLElementNode textNode = file1.getRootNode().getAllChildNodes().get(0);
        assertEquals(MS_XMLElementNode._TEXT_NODE_NAME, textNode.getTagName());
        assertEquals(0, textNode.getAllChildNodes().length());
        assertEquals(null, textNode.getFirstChild(""));
        assertEquals(null, textNode.getChildList(""));
    }

    @Test
    public void test08ExportToJSON() {
        JSONObject jsonObject = file1.toJSON();
        assertEquals("breakfast_menu", jsonObject.names().get(0));
        jsonObject = jsonObject.getJSONObject("breakfast_menu");
        assertEquals("food", jsonObject.names().get(0));
        JSONArray jsonArray = jsonObject.getJSONArray("food");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            assertEquals(FOOD_NAME_LIST.get(i), json.get("name"));
        }
    }

    @BeforeClass
    public static void initTestPreConditions() throws IOException, ParserConfigurationException, SAXException {
        //create an XML file:
        //view-source:http://www.w3schools.com/xml/simple.xml
        MS_TextFile xmlFile = new MS_TextFile(PATH_TO_XML_FILE);
        xmlFile.writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<breakfast_menu>\n" +
                "\t<food>\n" +
                "\t\t<name>Belgian Waffles</name>\n" +
                "\t\t<price>$5.95</price>\n" +
                "\t\t<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>\n" +
                "\t\t<calories>650</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>Strawberry Belgian Waffles</name>\n" +
                "\t\t<price>$7.95</price>\n" +
                "\t\t<description>Light Belgian waffles covered with strawberries and whipped cream</description>\n" +
                "\t\t<calories>900</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>Berry-Berry Belgian Waffles</name>\n" +
                "\t\t<price>$8.95</price>\n" +
                "\t\t<description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description>\n" +
                "\t\t<calories>900</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>French Toast</name>\n" +
                "\t\t<price>$4.50</price>\n" +
                "\t\t<description>Thick slices made from our homemade sourdough bread</description>\n" +
                "\t\t<calories>600</calories>\n" +
                "\t</food>\n" +
                "\t<food>\n" +
                "\t\t<name>Homestyle Breakfast</name>\n" +
                "\t\t<price>$6.95</price>\n" +
                "\t\t<description>Two eggs, bacon or sausage, toast, and our ever-popular hash browns</description>\n" +
                "\t\t<calories>950</calories>\n" +
                "\t</food>\n" +
                "</breakfast_menu>\n");
        xmlFile.close();

        FOOD_NAME_LIST = new MS_StringList();
        FOOD_NAME_LIST.add("Belgian Waffles");
        FOOD_NAME_LIST.add("Strawberry Belgian Waffles");
        FOOD_NAME_LIST.add("Berry-Berry Belgian Waffles");
        FOOD_NAME_LIST.add("French Toast");
        FOOD_NAME_LIST.add("Homestyle Breakfast");

        //new names
        FOOD_NAME_LIST.add("Cookies");
        FOOD_NAME_LIST.add("Cakes");
        FOOD_NAME_LIST.add("Peanuts");
        FOOD_NAME_LIST.add("Bananas");
        FOOD_NAME_LIST.add("Salads");

        xmlFile = new MS_TextFile(PATH_TO_XML_FILE2);
        xmlFile.writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlFile.writeln("<Students>");
        xmlFile.writeln(getTabSpace(1) + "<Student>");
        xmlFile.writeln(getTabSpace(2) + "<name>Māris</name>");
        xmlFile.writeln(getTabSpace(2) + "<age>24</age>");
        xmlFile.writeln(getTabSpace(2) + "<university name=\"RTU\">");
        xmlFile.writeln(getTabSpace(3) + "<faculty>DITF</faculty>");
        xmlFile.writeln(getTabSpace(2) + "</university>");
//        xmlFile.writeln(getTabSpace(3) + "<>");
        xmlFile.writeln(getTabSpace(1) + "</Student>");
        xmlFile.writeln(getTabSpace(1) + "<Student>");
        xmlFile.writeln(getTabSpace(2) + "<name>Jānis</name>");
        xmlFile.writeln(getTabSpace(2) + "<age>26</age>");
        xmlFile.writeln(getTabSpace(1) + "</Student>");
        xmlFile.writeln("</Students>");
        xmlFile.close();

        xmlFile = new MS_TextFile(PATH_TO_XML_FILE3);
        xmlFile.writeln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlFile.writeln("<Universities></Universities>");
        xmlFile.close();

        file1 = new MS_XML(MS_BinaryTools.readFile(PATH_TO_XML_FILE));
        file2 = new MS_XML(MS_BinaryTools.readFile(PATH_TO_XML_FILE2));
        file3 = new MS_XML(MS_BinaryTools.readFile(PATH_TO_XML_FILE3));
    }

    @AfterClass
    public static void finalizeTestConditions() {
        deleteFile(PATH_TO_XML_FILE);
        deleteFile(PATH_TO_XML_FILE2);
        deleteFile(PATH_TO_XML_FILE3);
    }
}